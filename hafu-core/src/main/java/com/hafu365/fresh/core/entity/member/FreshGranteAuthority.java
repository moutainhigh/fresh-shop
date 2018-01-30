package com.hafu365.fresh.core.entity.member;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Setter;
import lombok.ToString;
import org.springframework.security.core.GrantedAuthority;

/**
 * 自定义权限
 * Created by SunHaiyang on 2017/8/4.
 */
@Setter
@Data
@ToString
@AllArgsConstructor
public class FreshGranteAuthority implements GrantedAuthority {

    /**
     * 地址
     */
    private String url;

    /**
     * 链接方式
     */
    private String method;
    /**
     * 权限
     */
    private String authority;

    @Override
    public String getAuthority() {
        return this.authority;
    }
}
