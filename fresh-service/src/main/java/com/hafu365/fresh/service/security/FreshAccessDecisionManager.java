package com.hafu365.fresh.service.security;

import com.hafu365.fresh.core.entity.member.FreshGranteAuthority;
import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.FilterInvocation;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.Collection;

/**
 * 权限判断
 * Created by SunHaiyang on 2017/8/4.
 */
@Service
public class FreshAccessDecisionManager implements AccessDecisionManager {
    @Override
    public void decide(Authentication authentication, Object o, Collection<ConfigAttribute> collection) throws AccessDeniedException, InsufficientAuthenticationException {
        HttpServletRequest request = ((FilterInvocation)o).getHttpRequest();
        if(permitAll(request)){ //验证是否是公开接口
            return;
        }
        String url,method,roleCode;
        for (GrantedAuthority grantedAuthority : authentication.getAuthorities()){
            if (grantedAuthority instanceof FreshGranteAuthority){
                FreshGranteAuthority freshGranteAuthority = (FreshGranteAuthority)grantedAuthority;
                url = freshGranteAuthority.getUrl();
                method = freshGranteAuthority.getMethod();
                roleCode = freshGranteAuthority.getAuthority();
                if(roleCode.equals("ROLE:USER:GHOST")){
                    if(matchers("/memberInfo/saveMe",request)||matchers("/member/updateMe",request)){
                        return;
                    }
                }
                if(roleCode.equals("ROLE:ADMIN:SUPER")){
                    return;
                }
                if(matchers(url,request) || matchers("/error",request)){
                    if(method.toUpperCase().equals(request.getMethod()) || method.toUpperCase().equals("ALL")){
                        return;
                    }
                }
            }else if(grantedAuthority.getAuthority().equals("ROLE_ANONYMOUS")){
//                if(permitAll(request)){
//                    return;
//                }
                if(matchers("/member/register",request)){
                    return;
                }
            }
        }
//        /**
//         * 取消注释并删除return则开启拦截器
//         */
        throw new AccessDeniedException("no right");

    }

    /**
     * 判断是公开接口
     * @param request
     * @return
     */
    private boolean permitAll(HttpServletRequest request){
        String[] urls = {
                "/error",
                "/login",
                "/logout",
                "/adv/findAll",
                "/floor/findAll",
                "/goods/findById",
                "/goods/findGoods",
                "/gc/findShowGcList",
                "/store/selectStore",
                "/memberInfo/find",
                "/member/find",
                "/memberInfo/findMe",
                "/upload",
                "/bills/payMent",
                "/bills/submit",
                "/order/findOrdersByOrderId",
                "/order/finshOrders",
                "/order/returnOrders",
                "/code",
                "/memberInfo/findMyRole"
        };
        for (String url : urls){
            if (matchers(url,request)){
                return true;
            }
        }
        return false;
    }

    /**
     * 校验url是否有效
     * @param url 拦截地址
     * @param request 访问消息
     * @return
     */
    private boolean matchers(String url, HttpServletRequest request){
        if(url == null || url.isEmpty()){
            return false;
        }
        AntPathRequestMatcher matcher = new AntPathRequestMatcher(url);
        if(matcher.matches(request)){
            return true;
        }
        return false;
    }

    @Override
    public boolean supports(ConfigAttribute configAttribute) {
        return false;
    }

    @Override
    public boolean supports(Class<?> aClass) {
        return true;
    }
}
