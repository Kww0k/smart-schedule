package com.test.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FileInfoVo {

    private Long id;

    //文件名称
    private String name;
    //文件类型
    private String type;
    //文件大小
    private Long size;
    //下载链接
    private String url;
    //是否禁用
    private int status;
}
