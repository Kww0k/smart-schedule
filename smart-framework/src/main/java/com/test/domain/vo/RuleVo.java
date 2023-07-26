package com.test.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RuleVo {
    private Long id;

    //客流规则
    private String flowRule;
    //开店规则
    private String startRule;
    //关店规则
    private String endRule;

    private String store;
}
