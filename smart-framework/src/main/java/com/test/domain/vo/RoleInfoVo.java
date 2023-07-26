package com.test.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RoleInfoVo {
    private Long id;
    //角色名称
    private String roleName;
    //标注
    private String roleKey;
    private String remark;
    private Long count;
}
