package com.example.oqp.common.security.jwt;

import com.example.oqp.common.security.custom.CustomUserDetails;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;

@RequiredArgsConstructor
@Slf4j
@Component
public class JwtFilter extends GenericFilterBean {

    private final JwtUtil jwtUtil;

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;

        String header = request.getHeader("Authorization");

        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7);
            log.info("token: {}", token);

            if(jwtUtil.validation(token)) {
                Authentication authenticate = jwtUtil.getAuthenticate(token);
                log.info("authenticate: {}", authenticate.getPrincipal().toString());

                SecurityContextHolder.getContext().setAuthentication(authenticate);

                CustomUserDetails principal = (CustomUserDetails) authenticate.getPrincipal();
                log.info("user Id : {}", principal.getUsername());

            }else{
                log.info("token validation failed");
            }
        }

        filterChain.doFilter(servletRequest, servletResponse);
    }
}
