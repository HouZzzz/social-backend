package org.houzz.social.auth.service;

import org.houzz.social.auth.dto.AuthResponse;
import org.houzz.social.auth.dto.LoginRequest;
import org.houzz.social.auth.dto.RegisterRequest;
import org.houzz.social.auth.dto.UserDto;
import org.houzz.social.auth.entity.User;
import org.houzz.social.auth.repository.UserRepository;
import org.houzz.social.common.exception.BadRequestException;
import org.houzz.social.common.exception.NotFoundException;
import org.houzz.social.common.exception.UnauthorizedException;
import org.houzz.social.config.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthService.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        String username = request.username().trim();

        if (userRepository.existsByUsernameIgnoreCase(username)) {
            throw new BadRequestException("Пользователь с таким именем уже существует");
        }

        User user = new User(username, passwordEncoder.encode(request.password()));

        try {
            user = userRepository.saveAndFlush(user);
        } catch (DataIntegrityViolationException e) {
            throw new BadRequestException("Пользователь с таким именем уже существует");
        }

        log.info("Зарегистрирован пользователь id={} username={}", user.getId(), user.getUsername());

        return AuthResponse.of(issueToken(user), user);
    }

    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByUsernameIgnoreCase(request.username().trim())
                .orElseThrow(() -> new UnauthorizedException("Неверное имя пользователя или пароль"));

        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new UnauthorizedException("Неверное имя пользователя или пароль");
        }

        log.info("Вход пользователя id={}", user.getId());

        return AuthResponse.of(issueToken(user), user);
    }

    @Transactional(readOnly = true)
    public UserDto currentUser(Long userId) {
        return userRepository.findById(userId)
                .map(UserDto::from)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
    }

    private String issueToken(User user) {
        return jwtService.issue(user.getId(), user.getUsername());
    }
}
