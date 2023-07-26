package com.test.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddApplyDto {

    private Long id;
    private Date date;
    String applyTime;
    private String reason;
}
