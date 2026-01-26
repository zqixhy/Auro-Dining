package com.qiao.filter;

import com.alibaba.fastjson.JSON;
import com.qiao.common.BaseContext;
import com.qiao.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Filter to check login status and manage ThreadLocal context
 */
@WebFilter(urlPatterns = "/*")
@Slf4j
public class LoginCheckFilter implements Filter {

    public static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        String requestURI = request.getRequestURI();
        log.info("Intercepted request: {}", requestURI);

        // Define white list
        String[] urls = new String[]{
                "/employee/login",
                "/employee/logout",
                "/backend/page/login/login.html",
                "/backend/js/**",
                "/backend/api/**",
                "/backend/styles/**",
                "/backend/images/**",
                "/backend/plugins/**",
                "/backend/favicon.ico",
                "/front/page/login.html",
                "/front/index.html",
                "/front/js/**",
                "/front/styles/**",
                "/front/images/**",
                "/front/api/**",
                "/front/plugins/**",
                "/user/sendMsg",
                "/user/login",
                "/common/download"
        };

        // Check if the path needs to be handled
        boolean check = check(urls, requestURI);
        if (check) {
            log.info("Path {} is in white list, passing...", requestURI);
            filterChain.doFilter(request, response);
            return;
        }

        // ThreadLocal logic with proper cleanup
        try {
            // Check Administration (Employee) login
            if (request.getSession().getAttribute("employee") != null) {
                Long empId = (Long) request.getSession().getAttribute("employee");
                BaseContext.setCurrentId(empId);
                log.info("Employee logged in, ID: {}", empId);
                filterChain.doFilter(request, response);
                return;
            }

            // Check Mobile User login
            if (request.getSession().getAttribute("user") != null) {
                Long userId = (Long) request.getSession().getAttribute("user");
                BaseContext.setCurrentId(userId);
                log.info("User logged in, ID: {}", userId);
                filterChain.doFilter(request, response);
                return;
            }

            // Handle unauthorized access
            log.info("Unauthorized access to: {}", requestURI);
            handleUnauthorized(request, response, requestURI);

        } finally {
            // Cleanup threadLocal to prevent data contamination and memory leaks
            BaseContext.removeCurrentId();
            log.debug("ThreadLocal context cleared for URI: {}", requestURI);
        }
    }

    private void handleUnauthorized(HttpServletRequest request, HttpServletResponse response, String uri) throws IOException {
        String xRequestedWith = request.getHeader("X-Requested-With");
        boolean isAjax = "XMLHttpRequest".equals(xRequestedWith);

        if (uri.endsWith(".html") && !isAjax) {
            // Determine redirect URL based on request path
            if (uri.startsWith("/backend")) {
                // Backend management pages redirect to backend login page
                response.sendRedirect("/backend/page/login/login.html");
            } else {
                // Frontend pages redirect to frontend login page
                response.sendRedirect("/front/page/login.html");
            }
        } else {
            response.setContentType("application/json;charset=utf-8");
            response.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));
        }
    }

    public boolean check(String[] urls, String requestURI) {
        for (String url : urls) {
            if (PATH_MATCHER.match(url, requestURI)) return true;
        }
        return false;
    }
}