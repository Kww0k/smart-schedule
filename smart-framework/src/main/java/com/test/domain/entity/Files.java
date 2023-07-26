package com.test.domain.entity;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
/**
 * (File)表实体类
 *
 * @author makejava
 * @since 2023-01-12 14:06:19
 */
@SuppressWarnings("serial")
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("sys_file")
public class Files {
    //Id
    @TableId
    private Long id;

    //文件名称
    private String name;
    private String md5;
    //文件类型
    private String type;
    //文件大小
    private Long size;
    //下载链接
    private String url;
    //是否禁用
    private int status;
    //逻辑删除
    private Integer delFlag;
    
    private Long createBy;
    
    private Date createTime;
    
    private Long updateBy;
    
    private Date updateTime;



}
