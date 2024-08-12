package com.audition.configuration;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class MethodFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
        throws ServletException, IOException {
        if (request.getMethod().equals("OPTIONS") || request.getMethod().equals("PATCH") || request.getMethod()
            .equals("LOCK") || request.getMethod().equals("COPY") || request.getMethod().equals("HEAD")
            || request.getMethod().equals("DELETE")) {
            response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
        } else {
            filterChain.doFilter(request, response);
        }

    }
}
