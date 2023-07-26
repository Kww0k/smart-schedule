package com.test.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NeededListVo {
    private Long id;

    //日期
    private Date date;
    //需要员工的时间
    private String time;
    //需要员工的个数
    private Integer needMan;
}
