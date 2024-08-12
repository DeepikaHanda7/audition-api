package com.audition.configuration;


import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import io.micrometer.tracing.Tracer;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Objects;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@SuppressFBWarnings
@Component
public class ResponseHeaderInjector implements Filter {

    // TODO Inject openTelemetry trace and span Ids in the response headers
    @Autowired
    private Tracer tracer;

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
        throws IOException, ServletException {

        HttpServletResponse response = (HttpServletResponse) servletResponse;
        response.setHeader("traceId", Objects.requireNonNull(tracer.currentSpan()).context().traceId());
        response.setHeader("spanId", Objects.requireNonNull(tracer.currentSpan()).context().spanId());

        filterChain.doFilter(servletRequest, response);

    }
}


