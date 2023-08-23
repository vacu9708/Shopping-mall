package com.user_management.user_management.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;

@Aspect
@Component
public class AuthAspect {
    static final Logger logger = LoggerFactory.getLogger(AuthAspect.class);
    // private final List<String> allowedIPs = Arrays.asList("127.0.0.1");

    @Pointcut("execution(* com.user_management.user_management.user.UserController.manager*(..)) && args(request,..)")
    public void managerApi(HttpServletRequest request) {}

    @Around("managerApi(request)")
    public Object checkPassword(ProceedingJoinPoint joinPoint, HttpServletRequest request) throws Throwable {
        // logger.info("Checking password\n\n");
        String password = request.getHeader("password");
        // String requestURL = request.getRequestURL().toString();

        if (/*requestURL.contains("manager") &&*/ password == null || !password.equals("123")) {
            return ResponseEntity.status(403).body("MANAGER_ONLY");
        }
        return joinPoint.proceed();
    }
}