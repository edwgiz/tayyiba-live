package com.github.edwgiz.tayyib.adapter.in.springMvc.infra;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jspecify.annotations.NonNull;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;


@Component
public class RequestIdFilter extends OncePerRequestFilter {


    @Override
    protected void doFilterInternal(
            final @NonNull HttpServletRequest request,
            final @NonNull HttpServletResponse response,
            final @NonNull FilterChain filterChain)
            throws ServletException, IOException {

        final var headerName = "X-Request-ID";
        final var requestId = request.getHeader(headerName);

        if (requestId == null || requestId.isBlank()) {
            filterChain.doFilter(request, response);
        } else {
            response.setHeader(headerName, requestId);
            final var mdcKey = "requestId";
            MDC.put(mdcKey, requestId);
            try {
                filterChain.doFilter(request, response);
            } finally {
                MDC.remove(mdcKey);
            }
        }
    }
}