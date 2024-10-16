package com.example.oqp.user.service;

import com.example.oqp.common.enums.Role;
import com.example.oqp.common.error.CustomException;
import com.example.oqp.common.error.ErrorCode;
import com.example.oqp.common.masking.Masking;
import com.example.oqp.common.security.custom.CustomUserDetails;
import com.example.oqp.common.security.custom.CustomUserDetailsService;
import com.example.oqp.common.security.jwt.JwtAccessResponse;
import com.example.oqp.common.security.jwt.JwtLoginResponse;
import com.example.oqp.common.security.jwt.JwtUtil;
import com.example.oqp.content.model.dto.ContentDto;
import com.example.oqp.user.controller.reqeust.*;
import com.example.oqp.user.controller.response.EmailSendResponse;
import com.example.oqp.user.model.dto.UserDto;
import com.example.oqp.user.model.entity.UserEntity;
import com.example.oqp.user.model.repository.UserRepository;
import io.jsonwebtoken.Claims;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;
    private final CustomUserDetailsService customUserDetailsService;
    private final JavaMailSender mailSender;
    private final Masking masking;
    @Value("${spring.mail.username}")
    private String from;

    public UserDto register(RegisterRequest registerRequest) {

        int userIdLength = registerRequest.getEmail().length();

        if(userRepository.existsByUserId(registerRequest.getUserId())){
            throw new CustomException(ErrorCode.ALREADY_SAVE_ID);
        }
        if(userRepository.existsByNickname(registerRequest.getNickname())){
            throw new CustomException(ErrorCode.ALREADY_SAVE_NICKNAME);
        }

        UserEntity user = toUserEntity(registerRequest);
        UserEntity save = userRepository.save(user);
        UserDto dto = UserDto.builder()
                .id(save.getId())
                .userId(save.getUserId())
                .password(save.getPassword())
                .nickname(save.getNickname())
                .name(save.getName())
                .registerAt(save.getRegisterAt())
                .email(save.getEmail())
                .star(save.getStar())
                .postingCount(save.getPostingCount())
                .role(save.getRole())
                .content(null)
                .build();


        return dto;
    }

    public JwtLoginResponse login(LoginRequest request) {
        try{
            log.info("request user Id : {}", request.getUserId());
            log.info("request password : {}", request.getPassword());
            UserEntity byUserId = userRepository.findByUserId(request.getUserId());

            Authentication authenticate = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getUserId(), request.getPassword()));
            log.info("authenticate : {}", authenticate);
            String accessToken = jwtUtil.generateAccessToken(authenticate);
            String refreshToken = jwtUtil.generateRefreshToken(authenticate);

            return JwtLoginResponse.builder()
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .nickname(byUserId.getNickname())
                    .build();
        }catch (Exception e){
            log.error(e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }


    public JwtAccessResponse refresh(HttpServletRequest request) {
        String token = request.getHeader("Refresh-Token");
        log.info("token : {}", token);

        if (token == null) {
            log.error("Refresh Token이 제공되지 않았습니다.");
            throw new CustomException(ErrorCode.NOT_FOUND_TOKEN);
        }

        try {
            Claims claims = jwtUtil.parseToken(token);
            log.info("claim : {}", claims.toString());

            Date exp = new Date(claims.get("exp", Long.class) * 1000);
            if (exp.after(new Date())) {
                Long id = claims.get("id", Long.class);
                UserEntity user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User Not Found"));

                CustomUserDetails userDetails = (CustomUserDetails) customUserDetailsService.loadUserByUsername(user.getUserId());
                Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

                String accessToken = jwtUtil.generateAccessToken(authentication);
                String refreshToken = jwtUtil.generateRefreshToken(authentication);

                return JwtAccessResponse.builder()
                        .accessToken(accessToken)
                        .refreshToken(refreshToken)
                        .build();
            } else {
                log.error("Refresh Token 만료");
                throw new CustomException(ErrorCode.EXPIRED_REFRESH_TOKEN);
            }
        } catch (Exception e) {
            log.error("UserService refresh Fail : {}", e.getMessage());
            throw new RuntimeException("UserService refresh Fail", e);
        }
    }


    public UserEntity toUserEntity(RegisterRequest request){
        return UserEntity.builder()
                .name(request.getName())
                .nickname(request.getNickname())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .userId(request.getUserId())
                .star(0)
                .postingCount(0)
                .role(Role.ROLE_USER)
                .registerAt(LocalDateTime.now())
                .build();
    }

    public UserDto found(String nickname) {
        try{
            UserEntity byNickname = userRepository.findByNickname(nickname);

            if(byNickname == null){
                throw new CustomException(ErrorCode.USER_NOT_FOUND);
            }

            List<ContentDto> content = byNickname.getContent().stream()
                    .map(contentEntity -> {
                        return ContentDto.builder()
                                .id(contentEntity.getId())
                                .title(contentEntity.getTitle())
                                .frontImage(contentEntity.getFrontImage())
                                .category(contentEntity.getCategory())
                                .rating(contentEntity.getRating())
                                .writer(contentEntity.getWriter())
                                .createAt(contentEntity.getCreateAt())
                                .userId(contentEntity.getUserId().getId())
                                .build();
                    }).collect(Collectors.toList());

            return UserDto.builder()
                    .id(byNickname.getId())
                    .userId(masking.maskingUserId(byNickname.getUserId()))
                    .password(byNickname.getPassword())
                    .nickname(byNickname.getNickname())
                    .email(masking.maskingEmail(byNickname.getEmail()))
                    .star(byNickname.getStar())
                    .name(byNickname.getNickname())
                    .registerAt(byNickname.getRegisterAt())
                    .postingCount(byNickname.getPostingCount())
                    .role(byNickname.getRole())
                    .content(content)
                    .build();
        }catch (Exception e){
            log.error("UserService found Fail : {}", e.getMessage());
            throw new RuntimeException("UserService found Fail", e);
        }
    }

    public boolean delete(CustomUserDetails userDetails) {
        UserEntity user = userRepository.findByUserId(userDetails.getUsername());
        if(user == null){
            throw new CustomException(ErrorCode.USER_NOT_FOUND);
        }

        userRepository.delete(user);
        return true;
    }

    public UserDto modify(CustomUserDetails customUserDetails, UserModifyRequest modifyRequest) {
        UserEntity user = userRepository.findByUserId(customUserDetails.getUsername());
        if(user == null){
            throw new CustomException(ErrorCode.USER_NOT_FOUND);
        }
        UserEntity modifyUser = UserModifyRequest.patch(user, modifyRequest);

        UserEntity save = userRepository.save(modifyUser);
        List<ContentDto> dtos = save.getContent().stream()
                .map(contentEntity -> {
                    return ContentDto.builder()
                            .id(contentEntity.getId())
                            .title(contentEntity.getTitle())
                            .frontImage(contentEntity.getFrontImage())
                            .category(contentEntity.getCategory())
                            .rating(contentEntity.getRating())
                            .writer(contentEntity.getWriter())
                            .createAt(contentEntity.getCreateAt())
                            .userId(contentEntity.getUserId().getId())
                            .build();
                }).collect(Collectors.toList());

        return UserDto.builder()
                .id(save.getId())
                .userId(save.getUserId())
                .nickname(save.getNickname())
                .password(save.getPassword())
                .registerAt(save.getRegisterAt())
                .star(save.getStar())
                .postingCount(save.getPostingCount())
                .role(save.getRole())
                .name(save.getName())
                .email(save.getEmail())
                .content(dtos)
                .build();
    }

    public EmailSendResponse sendEmail(FindByPasswordRequest request) {
        UserEntity user = userRepository.findByUserId(request.getUserId());
        log.info("user : {}", user);

        if (user != null && user.getEmail().equals(request.getEmail())) {
            String[] element = {
                    "0", "1", "2", "3", "4", "5", "6", "7", "8", "9",
                    "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L",
                    "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X",
                    "Y", "Z"
            };

            String newPassword = "";
            int idx = 0;

            for (int i = 0; i < 10; i++) {
                idx = (int) (element.length * Math.random());
                newPassword += element[idx];
            }
            log.info("newPassword : {}", newPassword);

            String encode = passwordEncoder.encode(newPassword);

            user.setPassword(encode);
            userRepository.save(user);

            MimeMessage mimeMessage = mailSender.createMimeMessage();
            try {
                MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, false, "UTF-8");
                mimeMessageHelper.setTo(request.getEmail());

                mimeMessageHelper.setSubject("임시 비밀번호 발급 이메일");

                mimeMessageHelper.setText(user.getName() + "님 새로 발급된 비밀번호는 " + newPassword
                        + " 발급 받은 비밀번호로 로그인 후 사용자 정보 수정으로 비밀번호 재설정");

                mimeMessageHelper.setFrom(new InternetAddress(from));

                mailSender.send(mimeMessage);

                return EmailSendResponse.builder()
                        .message("이메일 전송 완료")
                        .build();

            } catch (MessagingException e) {
                throw new RuntimeException(e);
            }
        } else {
            throw new CustomException(ErrorCode.USER_NOT_FOUND);
        }
    }
}
