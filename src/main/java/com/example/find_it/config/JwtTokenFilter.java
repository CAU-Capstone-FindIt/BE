package com.example.find_it.config;

import com.example.find_it.utils.JwtProviderUtil;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtTokenFilter extends OncePerRequestFilter {
    private final JwtProviderUtil jwtProviderUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String accessToken = getTokenFromRequest(request, "Authorization");

        if (accessToken != null) {
            try {
                Authentication auth = jwtProviderUtil.getAuthentication(accessToken);
                SecurityContextHolder.getContext().setAuthentication(auth);
            } catch (UsernameNotFoundException e) {
                sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "토큰에 해당하는 유저가 존재하지 않습니다." + e.getMessage());
                return;
            } catch (JwtException e) {
                sendErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "Access Token이 유효하지 않습니다.");
                return;
            }
        }

        filterChain.doFilter(request, response);
    }

    private String getTokenFromRequest(HttpServletRequest request, String headerName) {
        String header = request.getHeader(headerName);
        if (header != null && header.startsWith("Bearer ")) {
            return header.substring(7);
        }
        return null;
    }

    private void sendErrorResponse(HttpServletResponse response, int errorCode, String message) throws IOException {
        response.setStatus(errorCode);
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json");
        response.getWriter().write("{\"error\":\"" + message + "\"}");
    }
}

