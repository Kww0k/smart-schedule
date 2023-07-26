package com.test.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserInfoVo {

    private Long id;

    //姓名
    private String name;
    //邮件地址
    private String email;
    private String phoneNumber;
    //0正常，1停用
    private Integer status;
    //工作日偏好
    private String dayPreference;
    //工作时长偏好1 上午 2下午 3晚上 空为全天
    private String timePreference;
    //日班次时长偏好
    private Double dayTimePreference;
    //周班次时长偏好
    private Double weekTimePreference;
    private String store;
    private String role;
}
