package ru.bvn13.adastor.config;

import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * @author boykovn at 12.03.2019
 */
@Component
public class NotAllowedContentFilter implements Filter {
    private static final List<String> NOT_ALLOWED_CONTENT_TYPE = Arrays.asList(
            "application/x-dosexec",
            "application/x-executable"
    );

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        if (!"GET".equalsIgnoreCase(httpRequest.getMethod())) {
            if (NOT_ALLOWED_CONTENT_TYPE.contains(httpRequest.getContentType().toLowerCase())) {
                httpResponse.sendError(406, "This content is not allowed here!");
                return;
            }
        }

        chain.doFilter(request, response);
    }
}
