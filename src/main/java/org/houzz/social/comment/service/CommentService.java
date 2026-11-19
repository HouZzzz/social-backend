package org.houzz.social.comment.service;

import org.houzz.social.auth.entity.User;
import org.houzz.social.auth.repository.UserRepository;
import org.houzz.social.comment.dto.CommentDto;
import org.houzz.social.comment.dto.CreateCommentRequest;
import org.houzz.social.comment.entity.Comment;
import org.houzz.social.comment.repository.CommentRepository;
import org.houzz.social.common.exception.BadRequestException;
import org.houzz.social.common.exception.NotFoundException;
import org.houzz.social.post.entity.Post;
import org.houzz.social.post.repository.PostRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CommentService {

    private static final Logger log = LoggerFactory.getLogger(CommentService.class);

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    public CommentService(CommentRepository commentRepository,
                          PostRepository postRepository,
                          UserRepository userRepository) {
        this.commentRepository = commentRepository;
        this.postRepository = postRepository;
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public List<CommentDto> list(Long postId) {
        if (!postRepository.existsById(postId)) {
            throw new NotFoundException("Пост не найден");
        }
        return commentRepository.findAllByPostId(postId).stream()
                .map(this::toDto)
                .toList();
    }

    @Transactional
    public CommentDto add(Long postId, Long authorId, CreateCommentRequest request) {
        String text = request.text().trim();
        if (text.isEmpty()) {
            throw new BadRequestException("Комментарий не может быть пустым");
        }

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new NotFoundException("Пост не найден"));

        User author = userRepository.findById(authorId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));

        Comment comment = commentRepository.saveAndFlush(new Comment(post, author, text));

        commentRepository.incrementCommentsCount(postId);

        log.info("Добавлен комментарий id={} к посту id={}", comment.getId(), postId);

        return toDto(comment);
    }

    private CommentDto toDto(Comment comment) {
        return new CommentDto(
                comment.getId(),
                comment.getText(),
                comment.getAuthor().getUsername(),
                comment.getCreatedAt()
        );
    }
}
