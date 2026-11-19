package org.houzz.social.post.service;

import org.houzz.social.auth.entity.User;
import org.houzz.social.auth.repository.UserRepository;
import org.houzz.social.common.exception.BadRequestException;
import org.houzz.social.common.exception.ForbiddenException;
import org.houzz.social.common.exception.NotFoundException;
import org.houzz.social.media.service.S3Service;
import org.houzz.social.post.dto.CreatePostRequest;
import org.houzz.social.post.dto.LikeResponse;
import org.houzz.social.post.dto.PostDto;
import org.houzz.social.post.entity.Post;
import org.houzz.social.post.repository.FeedRow;
import org.houzz.social.post.repository.PostLikeRepository;
import org.houzz.social.post.repository.PostRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Service
public class PostService {

    private static final Logger log = LoggerFactory.getLogger(PostService.class);

    private static final Set<String> FILTERS = Set.of("all", "my", "liked");

    private final PostRepository postRepository;
    private final PostLikeRepository postLikeRepository;
    private final UserRepository userRepository;
    private final S3Service s3Service;

    public PostService(PostRepository postRepository,
                       PostLikeRepository postLikeRepository,
                       UserRepository userRepository,
                       S3Service s3Service) {
        this.postRepository = postRepository;
        this.postLikeRepository = postLikeRepository;
        this.userRepository = userRepository;
        this.s3Service = s3Service;
    }

    @Transactional(readOnly = true)
    public List<PostDto> feed(Long viewerId, String search, String filter, int limit, int offset) {
        String safeSearch = search == null ? "" : search.trim();
        String safeFilter = filter == null || !FILTERS.contains(filter) ? "all" : filter;
        int safeLimit = Math.clamp(limit, 1, 100);
        int safeOffset = Math.max(offset, 0);

        return postRepository.findFeed(viewerId, safeSearch, safeFilter, safeLimit, safeOffset)
                .stream()
                .map(this::toDto)
                .toList();
    }

    @Transactional
    public PostDto create(Long authorId, CreatePostRequest request) {
        String content = request.content().trim();
        if (content.isEmpty()) {
            throw new BadRequestException("Текст поста не может быть пустым");
        }

        User author = userRepository.findById(authorId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));

        String imageKey = validateImageKey(request.imageKey(), authorId);

        Post post = postRepository.saveAndFlush(new Post(author, content, imageKey));
        log.info("Создан пост id={} автором id={}", post.getId(), authorId);

        FeedRow row = postRepository.findFeedRowById(post.getId(), authorId);
        return toDto(row);
    }

    @Transactional
    public void delete(Long postId, Long requesterId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new NotFoundException("Пост не найден"));

        if (!post.getAuthor().getId().equals(requesterId)) {
            throw new ForbiddenException("Удалить пост может только его автор");
        }

        postRepository.delete(post);
        log.info("Удалён пост id={} автором id={}", postId, requesterId);
    }

    @Transactional
    public LikeResponse toggleLike(Long postId, Long userId) {
        PostLikeRepository.ToggleResult result = postLikeRepository.toggle(postId, userId);
        return new LikeResponse(postId, result.likesCount(), result.likedByMe());
    }

    private String validateImageKey(String imageKey, Long authorId) {
        if (imageKey == null || imageKey.isBlank()) {
            return null;
        }

        String key = imageKey.trim();
        String expectedPrefix = "posts/" + authorId + "/";

        if (!key.startsWith(expectedPrefix)) {
            throw new BadRequestException("Ключ изображения не принадлежит текущему пользователю");
        }

        if (!s3Service.exists(key)) {
            throw new BadRequestException("Изображение не загружено");
        }

        return key;
    }

    private PostDto toDto(FeedRow row) {
        return new PostDto(
                row.getId(),
                row.getContent(),
                row.getAuthorUsername(),
                row.getCreatedAt(),
                row.getLikesCount(),
                row.getCommentsCount(),
                row.getIsLikedByMe(),
                s3Service.publicUrl(row.getImageKey())
        );
    }
}
