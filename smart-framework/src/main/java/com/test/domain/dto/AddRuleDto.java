package com.test.domain.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel("增加规则的信息的实体类")
public class AddRuleDto {

    //客流规则
    @ApiModelProperty("客流规则")
    private String flowRule;
    //开店规则
    @ApiModelProperty("开店规则")
    private String startRule;
    //关店规则
    @ApiModelProperty("关店规则")
    private String endRule;

    @ApiModelProperty("门店id")
    private Long store;
}
