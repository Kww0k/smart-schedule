package com.test.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InfoVo {
    private Long id;

    //店名
    private String name;
    //地址
    private String address;
    //占地面积
    private Double size;

    private Integer code;
    //早到的职位id
    private String doMorning;
    //可以少做的职位id
    private String doLess;
    //打扫卫生的职位id
    private String doLater;
}
