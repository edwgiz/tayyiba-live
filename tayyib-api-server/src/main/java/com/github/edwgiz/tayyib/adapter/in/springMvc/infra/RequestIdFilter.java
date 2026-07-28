package com.github.edwgiz.tayyib.adapter.in.springMvc.infra;

import jakarta.annotation.Nonnull;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;


@Component
public class RequestIdFilter extends OncePerRequestFilter {

    public static final String HEADER = "X-Request-ID";

    @Override
    protected void doFilterInternal(
            final @Nonnull HttpServletRequest request,
            final @Nonnull HttpServletResponse response,
            final @Nonnull FilterChain filterChain)
            throws ServletException, IOException {

        final var requestId = request.getHeader(HEADER);

        if (requestId == null || requestId.isBlank()) {
            filterChain.doFilter(request, response);
        } else {
            response.setHeader(HEADER, requestId);

            MDC.put("requestId", requestId);
            request.setAttribute("requestId", requestId);

            try {
                filterChain.doFilter(request, response);
            } finally {
                MDC.remove("requestId");
            }
        }
    }
}