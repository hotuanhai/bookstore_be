package com.example.demo.service.security;

import com.example.demo.dao.UserRepository;
import com.example.demo.dto.security.LoginUserDto;
import com.example.demo.dto.security.RefreshDto;
import com.example.demo.dto.security.RegisterUserDto;
import com.example.demo.dto.security.VerifyUserDto;
import com.example.demo.entity.user.User;
import com.example.demo.enums.Role;
import com.example.demo.mapper.UserMapper;
import com.example.demo.response.LoginResponse;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final EmailService emailService;
    private final JwtService jwtService;
    private final UserMapper userMapper;

    private static final Logger log = LoggerFactory.getLogger(AuthenticationService.class);

    public User signup(RegisterUserDto input) {
        Optional<User> existingUser = userRepository.findByUsername(input.getEmail());
        if (existingUser.isPresent()) {
            User user = existingUser.get();
            if (!user.isEnabled()) userRepository.delete(user);
             else throw new RuntimeException("Email already registered");
            }

        User newUser = new User(input.getEmail(),
                passwordEncoder.encode(input.getPassword()),
                Role.ROLE_USER);
        newUser.setVerificationCode(generateVerificationCode());
        newUser.setVerificationCodeExpiresAt(LocalDateTime.now().plusMinutes(15));
        newUser.setEnabled(false);

        try {
            sendVerificationEmail(newUser);
        } catch (Exception e) {
            throw new RuntimeException("Failed to send verification email", e);
        }

        return userRepository.save(newUser);
    }

    public LoginResponse login(LoginUserDto dto) {
        User user = authenticate(dto);

        Map<String, Object> claims = new HashMap<>();
        claims.put("isAdmin", !user.getRole().name().equals("ROLE_USER"));

        String accessToken = jwtService.generateToken(claims, user);
        String refreshToken = jwtService.generateRefreshToken(user);

        return new LoginResponse(
                accessToken,
                jwtService.getExpirationTime(),
                refreshToken,
                jwtService.getRefreshExpirationTime(),
                userMapper.toUserResponseDto(user)
        );
    }

    public void logout(String token){

    }

    public LoginResponse refreshAccessToken(RefreshDto dto) {
        User user = userRepository.findByUsername(dto.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));
        // Validate the refresh token (throws if invalid or expired)
        if (!jwtService.isTokenValid(dto.getRefreshToken(), user)) {
            throw new RuntimeException("Refresh token is invalid or expired. Please login again.");
        }

        Map<String, Object> claims = new HashMap<>();
        claims.put("isAdmin", !user.getRole().name().equals("ROLE_USER"));

        String newAccessToken = jwtService.generateToken(claims, user);

        // Reuse refresh token until it expires
        return new LoginResponse(
                newAccessToken,
                jwtService.getExpirationTime(),
                dto.getRefreshToken(),
                jwtService.getRefreshExpirationTime(),
                userMapper.toUserResponseDto(user)
        );
    }

    public User authenticate(LoginUserDto input) {
        User user = userRepository.findByUsername(input.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!user.isEnabled()) {
            throw new RuntimeException("Account not verified. Please verify your account.");
        }
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        input.getEmail(),
                        input.getPassword()
                )
        );

        return user;
    }

    @Transactional
    public void verifyUser(VerifyUserDto input) {
        Optional<User> optionalUser = userRepository.findByUsername(input.getEmail());
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            if (user.getVerificationCodeExpiresAt().isBefore(LocalDateTime.now())) {
                throw new RuntimeException("Verification code has expired");
            }
            if (user.getVerificationCode().equals(input.getVerificationCode())) {
                user.setEnabled(true);
                user.setVerificationCode(null);
                user.setVerificationCodeExpiresAt(null);

//                Profile userProfile = profileService.createProfile(user);
                userRepository.save(user);
            } else {
                throw new RuntimeException("Invalid verification code");
            }
        } else {
            throw new RuntimeException("User not found");
        }
    }

    public void resendVerificationCode(String email) {
        Optional<User> optionalUser = userRepository.findByUsername(email);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            if (user.isEnabled()) {
                throw new RuntimeException("Account is already verified");
            }
            user.setVerificationCode(generateVerificationCode());
            user.setVerificationCodeExpiresAt(LocalDateTime.now().plusHours(1));
            sendVerificationEmail(user);
            userRepository.save(user);
        } else {
            throw new RuntimeException("User not found");
        }
    }

    private void sendVerificationEmail(User user) {
        String subject = "Account Verification";
        String verificationCode = "VERIFICATION CODE " + user.getVerificationCode();
        String htmlMessage = "<html>"
                + "<body style=\"font-family: Arial, sans-serif;\">"
                + "<div style=\"background-color: #f5f5f5; padding: 20px;\">"
                + "<h2 style=\"color: #333;\">Welcome to our app!</h2>"
                + "<p style=\"font-size: 16px;\">Please enter the verification code below to continue:</p>"
                + "<div style=\"background-color: #fff; padding: 20px; border-radius: 5px; box-shadow: 0 0 10px rgba(0,0,0,0.1);\">"
                + "<h3 style=\"color: #333;\">Verification Code:</h3>"
                + "<p style=\"font-size: 18px; font-weight: bold; color: #007bff;\">" + verificationCode + "</p>"
                + "</div>"
                + "</div>"
                + "</body>"
                + "</html>";

        try {
            emailService.sendVerificationEmail(user.getUsername(), subject, htmlMessage);
        } catch (MessagingException e) {
            // Handle email sending exception
            e.printStackTrace();
        }
    }
    private String generateVerificationCode() {
        Random random = new Random();
        int code = random.nextInt(900000) + 100000;
        return String.valueOf(code);
    }
}
