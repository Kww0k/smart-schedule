package com.test.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ArticleInfoVo {

    private Long id;

    //标题
    private String name;
    //内容

    private String content;
    //发布时间
    private Date createTime;

    private Long createBy;

    private String user;
}
