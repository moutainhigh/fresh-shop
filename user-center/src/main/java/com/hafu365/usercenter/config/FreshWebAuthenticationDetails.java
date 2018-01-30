//package com.hafu365.usercenter.config;
//
//import com.google.code.kaptcha.Constants;
//import org.springframework.security.web.authentication.WebAuthenticationDetails;
//
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpSession;
//
///**
// * Created by SunHaiyang on 2017/9/28.
// */
//public class FreshWebAuthenticationDetails extends WebAuthenticationDetails {
//
//    private String imageCode;
//
//    private String key;
//
//
//    public FreshWebAuthenticationDetails(HttpServletRequest request) {
//        super(request);
//        this.imageCode = request.getParameter("imageCode");
//        HttpSession session = request.getSession();
//        this.key = request.getParameter("key");
//    }
//
//    public String getImageCode(){
//        return imageCode;
//    }
//
//    public String getKey() {
//        return key;
//    }
//
//}
