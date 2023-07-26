package com.test.domain.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel("更新文章信息的实体类")
public class UpdateArticleDto {
    @ApiModelProperty("id")
    private Long id;
    //标题
    @ApiModelProperty("标题")
    private String name;
    //内容
    @ApiModelProperty("内容")
    private String content;
}
