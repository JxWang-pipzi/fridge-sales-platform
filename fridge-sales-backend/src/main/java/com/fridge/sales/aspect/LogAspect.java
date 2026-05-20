package com.fridge.sales.aspect;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fridge.sales.annotation.Log;
import com.fridge.sales.entity.SysOperLog;
import com.fridge.sales.service.SysOperLogService;
import com.fridge.sales.utils.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Map;

/**
 * 操作日志AOP切面
 */
@Aspect
@Component
public class LogAspect {

    private static final Logger log = LoggerFactory.getLogger(LogAspect.class);

    @Autowired
    private SysOperLogService operLogService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JwtUtil jwtUtil;

    @AfterReturning(pointcut = "@annotation(controllerLog)", returning = "jsonResult")
    public void doAfterReturning(JoinPoint joinPoint, Log controllerLog, Object jsonResult) {
        handleLog(joinPoint, controllerLog, null, jsonResult);
    }

    @AfterThrowing(value = "@annotation(controllerLog)", throwing = "e")
    public void doAfterThrowing(JoinPoint joinPoint, Log controllerLog, Exception e) {
        handleLog(joinPoint, controllerLog, e, null);
    }

    protected void handleLog(JoinPoint joinPoint, Log controllerLog, Exception e, Object jsonResult) {
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes == null) {
                return;
            }
            HttpServletRequest request = attributes.getRequest();

            SysOperLog operLog = new SysOperLog();
            operLog.setStatus(0);
            operLog.setOperIp(getClientIp(request));
            operLog.setOperUrl(substring(request.getRequestURI(), 0, 255));
            operLog.setOperName(getUsername(request));
            operLog.setOperTime(LocalDateTime.now());

            String className = joinPoint.getTarget().getClass().getName();
            String methodName = joinPoint.getSignature().getName();
            operLog.setMethod(className + "." + methodName + "()");
            operLog.setRequestMethod(request.getMethod());

            if (e != null) {
                operLog.setStatus(1);
                operLog.setErrorMsg(substring(e.getMessage(), 0, 2000));
            }

            String title = controllerLog.title();
            operLog.setTitle(title);
            operLog.setBusinessType(controllerLog.businessType().getCode());
            operLog.setOperatorType(controllerLog.operatorType());

            if (controllerLog.isSaveRequestData()) {
                setRequestValue(joinPoint, operLog, request);
            }

            if (controllerLog.isSaveResponseData() && jsonResult != null) {
                operLog.setJsonResult(objectMapper.writeValueAsString(jsonResult));
            }

            operLogService.insertOperLog(operLog);
            log.info("[成功][操作日志] 模块:{} | 操作:{} | 操作人:{} | IP:{}",
                    title, controllerLog.businessType().getDesc(), operLog.getOperName(), operLog.getOperIp());

        } catch (Exception exp) {
            log.error("[失败][操作日志] 异常信息:{}", exp.getMessage());
        }
    }

    private void setRequestValue(JoinPoint joinPoint, SysOperLog operLog, HttpServletRequest request) {
        String requestMethod = request.getMethod();
        if (HttpMethod.PUT.name().equals(requestMethod) || HttpMethod.POST.name().equals(requestMethod)) {
            Object[] args = joinPoint.getArgs();
            if (args != null && args.length > 0) {
                Object param = args[0];
                if (param instanceof MultipartFile) {
                    return;
                }
                try {
                    String params = objectMapper.writeValueAsString(param);
                    operLog.setOperParam(substring(params, 0, 2000));
                } catch (Exception e) {
                    log.error("参数序列化失败: {}", e.getMessage());
                }
            }
        } else {
            Map<String, String[]> params = request.getParameterMap();
            if (!params.isEmpty()) {
                try {
                    operLog.setOperParam(objectMapper.writeValueAsString(params));
                } catch (Exception e) {
                    log.error("参数序列化失败: {}", e.getMessage());
                }
            }
        }
    }

    private String getUsername(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
            try {
                return jwtUtil.getUsernameFromToken(token);
            } catch (Exception e) {
                return "未知用户";
            }
        }
        return "匿名用户";
    }

    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }

    private String substring(String str, int start, int end) {
        if (str == null) {
            return null;
        }
        if (end > str.length()) {
            return str.substring(start);
        }
        return str.substring(start, end);
    }
}
