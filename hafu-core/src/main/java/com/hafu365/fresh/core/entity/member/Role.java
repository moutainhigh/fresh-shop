package com.hafu365.fresh.core.entity.member;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

/**
 * 角色
 * Created by SunHaiyang on 2017/8/3.
 */
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "fresh_role")
public class Role implements Serializable {

    /**
     * 角色Id
     */
    @Id
    @GeneratedValue
    private long id;

    /**
     * 角色编码 暂时无用方便后期扩展
     */
    private String roleCode;

    /**
     * 角色名称
     */
    private String name;

    /**
     * 默认角色
     */
    private boolean theDefault;

    @ManyToMany(mappedBy = "roleList")
    @JsonBackReference
    private List<Member> memberList;

    @ManyToMany(mappedBy = "roleList",fetch = FetchType.EAGER)
    @JsonBackReference
    private List<Permission> permissionList;

    @Override
    public String toString() {
        return "Role{" +
                "id=" + id +
                ", roleCode='" + roleCode + '\'' +
                ", name='" + name + '\'' +
                ", theDefault=" + theDefault +
                '}';
    }
}
