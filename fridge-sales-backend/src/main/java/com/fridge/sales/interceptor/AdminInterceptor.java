package com.fridge.sales.interceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fridge.sales.common.Result;
import com.fridge.sales.common.ResultCode;
import com.fridge.sales.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Slf4j
@Component
public class AdminInterceptor implements HandlerInterceptor {

    @Autowired
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if ("OPTIONS".equals(request.getMethod())) {
            return true;
        }

        Long userId = (Long) request.getAttribute("userId");
        if (userId == null) {
            return forbidden(response, "请先登录");
        }

        String role = userService.getUserRole(userId);
        if (role == null) {
            return forbidden(response, "用户不存在");
        }

        if (!"admin".equals(role)) {
            log.warn("非管理员用户尝试访问管理员接口, userId={}, role={}", userId, role);
            return forbidden(response, "无权限访问");
        }

        request.setAttribute("userRole", role);
        return true;
    }

    /**
     * 清除用户角色缓存
     */
    public void clearUserRoleCache(Long userId) {
        // 注意：这里需要UserService提供清除缓存的方法
        // 实际项目中应该在用户角色变更时调用
        // TODO: 实现清除缓存的方法
    }

    private boolean forbidden(HttpServletResponse response, String message) throws Exception {
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        Result<Object> result = Result.error(ResultCode.FORBIDDEN.getCode(), message);
        response.getWriter().write(objectMapper.writeValueAsString(result));
        return false;
    }
}
