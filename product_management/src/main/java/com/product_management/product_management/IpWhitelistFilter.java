// package com.product_management.product_management;

// import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

// import javax.servlet.http.HttpServletRequest;
// import javax.servlet.http.HttpServletResponse;
// import java.util.Arrays;
// import java.util.List;

// public class IpWhitelistFilter extends HandlerInterceptorAdapter {

//     private static final List<String> WHITELISTED_IPS = Arrays.asList("192.168.1.1", "192.168.1.2"); // Add your allowed IPs here

//     @Override
//     public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
//         String ipAddress = request.getRemoteAddr();

//         if (!WHITELISTED_IPS.contains(ipAddress)) {
//             response.setStatus(HttpServletResponse.SC_FORBIDDEN);
//             return false;
//         }

//         return true;
//     }
// }
