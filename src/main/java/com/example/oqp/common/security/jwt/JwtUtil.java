package com.example.oqp.common.security.jwt;

import com.example.oqp.common.security.custom.CustomUserDetails;
import com.example.oqp.user.model.entity.UserEntity;
import com.example.oqp.user.model.repository.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Component
public class JwtUtil {
    @Value("${jwt.secret}")
    private String secret;

    private final UserRepository userRepository;

    public String generateAccessToken(Authentication authentication) {
        SecretKey key = Keys.hmacShaKeyFor(secret.getBytes());

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        log.info("userDetails:{}", userDetails);

        String userId = userDetails.getUsername();
        log.info("userId:{}", userId);

        UserEntity byUserId = userRepository.findByUserId(userId);

        LocalDateTime expired = LocalDateTime.now().plusMinutes(30);
        Date _expired = Date.from(expired.atZone(ZoneId.systemDefault()).toInstant());

        Claims claims = Jwts.claims();
        claims.put("id", byUserId.getId());
        claims.put("auth", byUserId.getRole().name());
        claims.put("exp", _expired.getTime() / 1000);
        claims.put("subject", byUserId.getNickname());

        return Jwts.builder()
                .setSubject(String.valueOf(byUserId.getId()))
                .setExpiration(_expired)
                .setClaims(claims)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public String generateRefreshToken(Authentication authentication) {
        SecretKey key = Keys.hmacShaKeyFor(secret.getBytes());

        CustomUserDetails principal = (CustomUserDetails) authentication.getPrincipal();
        log.info("principal:{}", principal);

        UserEntity byName = userRepository.findByUserId(principal.getUsername());
        log.info("byName:{}", byName);

        LocalDateTime expired = LocalDateTime.now().plusDays(1);
        Date _expired = Date.from(expired.atZone(ZoneId.systemDefault()).toInstant());

        Claims claims = Jwts.claims();
        claims.put("id", byName.getId());
        claims.put("auth", byName.getRole().name());
        claims.put("exp", _expired.getTime() / 1000);

        return Jwts.builder()
                .signWith(key, SignatureAlgorithm.HS256)
                .setClaims(claims)
                .setExpiration(_expired)
                .compact();
    }

    public Boolean validation(String token) {
        SecretKey key = Keys.hmacShaKeyFor(token.getBytes());
        try{
            Claims body = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
            log.info("body:{}", body);
            return true;
        }catch (Exception e){
            return false;
        }
    }

    public Claims parseToken(String token) {
        SecretKey key = Keys.hmacShaKeyFor(secret.getBytes());
        return Jwts.parser().setSigningKey(key).parseClaimsJws(token).getBody();
    }

    public Authentication getAuthenticate(String token){
        Claims claims = parseToken(token);
        Long id = claims.get("id", Integer.class).longValue();

        UserEntity user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("user not found"));
        List<SimpleGrantedAuthority> auth = Arrays.stream(new String[]{claims.get("auth").toString()})
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());

        CustomUserDetails customUser = new CustomUserDetails(user);

        return new UsernamePasswordAuthenticationToken(customUser, token, auth);
    }
}
