package com.hafu365.fresh.core.entity.member;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

/**
 * 权限
 * Created by SunHaiyang on 2017/8/3.
 */
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties({"roleList"})
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@Table(name = "fresh_permission")
public class Permission implements Serializable {

    /**
     * 权限id
     */
    @Id
    @GeneratedValue
    private long id;
    /**
     * 地址
     */
    private String url;
    /**
     * 访问方式
     */
    private String method;
    /**
     * 父类
     */
    @ManyToOne
    @JsonBackReference
    private Permission old;
    /**
     * 描述
     */
    private String description;
    /**
     * 路由-名称 - 暂不使用
     */
    private String name;
    /**
     * 路由-地址
     */
    private String path;
    /**
     * 路由-组件 - 暂时不使用
     */
    private String component;

    /**
     * 子类
     */
    @OneToMany(mappedBy = "old",fetch = FetchType.EAGER,cascade = {CascadeType.REMOVE})
    @JsonManagedReference
    private List<Permission> children;

    /**
     * 角色
     */
    @ManyToMany(fetch = FetchType.EAGER,cascade = {CascadeType.REMOVE,CascadeType.PERSIST})
    @JoinTable(name = "fresh_role_permission",
            joinColumns = {@JoinColumn(name = "permission_id")},
            inverseJoinColumns = {@JoinColumn(name = "role_id")}
    )
    @JsonBackReference
    private List<Role> roleList;

    @Override
    public String toString() {
        return "Permission{" +
                "id=" + id +
                ", url='" + url + '\'' +
                ", method='" + method + '\'' +
                ", old=" + old +
                ", description='" + description + '\'' +
                ", name='" + name + '\'' +
                ", path='" + path + '\'' +
                ", component='" + component + '\'' +
                ", children=" + children +
                '}';
    }
}
