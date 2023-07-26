package com.test.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RestDto {
    private Long id;
    private String date;

    private String reason;

    private String startTime;

    private String endTime;
}
