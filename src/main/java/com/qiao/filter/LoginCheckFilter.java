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
 * Filter to check if the user is logged in
 */
@WebFilter(urlPatterns = "/*")
@Slf4j
public class LoginCheckFilter implements Filter {

    // Path matcher to support wildcard characters
    public static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        // 1. Get the current request URI
        String requestURI = request.getRequestURI();
        log.info("Intercepted request: {}", requestURI);

        // 2. Define the white list paths that do not require login
        String[] urls = new String[]{
                "/employee/login",
                "/employee/logout",
                "/backend/page/login/login.html",
                "/backend/js/**",
                "/backend/styles/**",
                "/backend/images/**",
                "/backend/plugins/**",
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

        // 3. Check if the current request needs to be handled
        boolean check = check(urls, requestURI);

        // 4. If in white list, release the request
        if (check) {
            log.info("Path {} is in white list, passing...", requestURI);
            filterChain.doFilter(request, response);
            return;
        }

        // 5-1. Check login status for administration (Employee)
        if (request.getSession().getAttribute("employee") != null) {
            Long empId = (Long) request.getSession().getAttribute("employee");
            BaseContext.setCurrentId(empId);
            log.info("Employee is logged in, id: {}", empId);
            filterChain.doFilter(request, response);
            return;
        }

        // 5-2. Check login status for mobile users (User)
        if (request.getSession().getAttribute("user") != null) {
            Long userId = (Long) request.getSession().getAttribute("user");
            BaseContext.setCurrentId(userId);
            log.info("User is logged in, id: {}", userId);
            filterChain.doFilter(request, response);
            return;
        }

        // 6. If not logged in, handle the response based on request type
        log.info("User/Employee not logged in. Handling unauthorized request: {}", requestURI);

        // Check if it's an AJAX/Async request
        String xRequestedWith = request.getHeader("X-Requested-With");
        boolean isAjax = "XMLHttpRequest".equals(xRequestedWith);

        if (requestURI.endsWith(".html") && !isAjax) {
            // For direct page access, redirect to login page
            log.info("Redirecting to login page...");
            response.sendRedirect("/front/page/login.html");
        } else {
            // For API data requests, return JSON to trigger frontend interceptor
            log.info("Returning NOTLOGIN JSON response");
            response.setContentType("application/json;charset=utf-8");
            response.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));
        }
    }

    /**
     * Path matching check
     * @param urls
     * @param requestURI
     * @return
     */
    public boolean check(String[] urls, String requestURI) {
        for (String url : urls) {
            boolean match = PATH_MATCHER.match(url, requestURI);
            if (match) {
                return true;
            }
        }
        return false;
    }
}