package com.product_management.product_management.aspect;
// package com.product_management.product_management.product.aspect;


// import org.aspectj.lang.annotation.Aspect;
// import org.aspectj.lang.annotation.Before;
// import org.aspectj.lang.annotation.Pointcut;
// import org.springframework.stereotype.Component;

// @Aspect
// @Component
// public class LogAspect {

//     @Pointcut("execution(* com.product_management.product_management.product.ProductController.*(..))")
//     public void api() {}

//     @Before("api()")
//     public void logBefore() {
//         System.out.println("hi");
//     }

//     // @Pointcut("@annotation(log)")  
//     // public void logPointcut(){  
//     // }  
//     // @Before("logPointcut()")  
//     // public void log(){  
//     //     System.out.println("In Aspect");  
//     // }
// }