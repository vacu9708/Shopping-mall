package com.product_management.product_management.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;

@Aspect
@Component
public class AuthAspect {
    // private final List<String> allowedIPs = Arrays.asList("127.0.0.1");

    @Pointcut("execution(* com.product_management.product_management.product.ProductController.manager*(..)) && args(request,..)")
    public void managerApi(HttpServletRequest request) {}

    @Around("managerApi(request)")
    public Object checkPassword(ProceedingJoinPoint joinPoint, HttpServletRequest request) throws Throwable {
        String password = request.getHeader("password");
        // String requestURL = request.getRequestURL().toString();

        if (/*requestURL.contains("manager") &&*/ password == null || !password.equals("123")) {
            return ResponseEntity.status(403).body("MANAGER_ONLY");
        }
        // Continue the intercepted method and finish the aspect method
        return joinPoint.proceed();
    }
}