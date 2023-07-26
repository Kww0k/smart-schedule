package com.test.domain.vo;

import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RestInfoVo {
    @TableId
    private Long id;
    private Date date;
    private String name;

    private String reason;

    private String startTime;

    private String endTime;
}
