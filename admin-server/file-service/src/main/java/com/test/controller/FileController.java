package com.test.controller;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import com.alibaba.fastjson.annotation.JSONField;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.test.annotation.SystemLog;
import com.test.domain.ResponseResult;
import com.test.domain.entity.Files;
import com.test.mapper.FileMapper;
import com.test.service.FileService;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@RestController
@RequestMapping("/file")
@Api(tags = "文件接口", description = "对文件进行一系列操作")
public class FileController {

    @Value("${files.upload.path}")
    private String fileUploadPath;

    @Autowired
    private FileMapper fileMapper;
    @Autowired
    private FileService fileService;

    @ApiResponses({
            @ApiResponse(code = 200, message = "查询成功")
    })
    @ApiOperation("分页查询所有文件信息")
    @GetMapping("/page")
    @SystemLog(businessName = "分页查询所有文章信息")
    public ResponseResult roleList(@ApiParam("页数") Integer pageNum,
                                   @ApiParam("一页有几个") Integer pageSize,
                                   @ApiParam("文件名称") String name) {
        return fileService.fileList(pageNum, pageSize, name);
    }

    @ApiResponses({
            @ApiResponse(code = 200, message = "删除成功"),
            @ApiResponse(code = 404, message = "没有这个文章")
    })
    @ApiOperation("根据id删除文件信息")
    @DeleteMapping("/deleteById")
    @SystemLog(businessName = "根据id删除文件信息")
    public ResponseResult deleteArticleById(@ApiParam("id")@RequestParam Long id) {
        return fileService.deleteFileById(id);
    }

    @ApiResponses({
            @ApiResponse(code = 200, message = "删除成功")
    })
    @ApiOperation("根据id批量删除文件信息")
    @PostMapping("/deleteBatch")
    @SystemLog(businessName = "根据id批量删除文件信息")
    public ResponseResult deleteBatch(@ApiParam("ids") @RequestBody List<Long> ids) {
        return fileService.deleteBatch(ids);
    }

    @ApiResponses({
            @ApiResponse(code = 200, message = "更新成功")
    })
    @ApiOperation("更新文件信息")
    @PostMapping ("/updateFile")
    @SystemLog(businessName = "更新文件信息")
    public ResponseResult updateArticleInfo(@ApiParam("更新后的信息") @RequestBody Files files) {
        return fileService.updateFileInfo(files);
    }

    @ApiResponses({
            @ApiResponse(code = 200, message = "上传成功")
    })
    @ApiOperation("上传文件")
    @PostMapping("/upload")
    @SystemLog(businessName = "上传文件")
    public String upload(@RequestParam MultipartFile file) throws IOException {
        String originalFilename = file.getOriginalFilename();
        String type = FileUtil.extName(originalFilename);
        long size = file.getSize();

        // 定义一个文件唯一的标识码
        String fileUUID = IdUtil.fastSimpleUUID() + StrUtil.DOT + type;

        File uploadFile = new File(fileUploadPath + fileUUID);
        // 判断配置的文件目录是否存在，若不存在则创建一个新的文件目录
        File parentFile = uploadFile.getParentFile();
        if(!parentFile.exists()) {
            parentFile.mkdirs();
        }

        String url;
        // 获取文件的md5
        String md5 = SecureUtil.md5(file.getInputStream());
        // 从数据库查询是否存在相同的记录
        Files dbFiles = getFileByMd5(md5);
        if (dbFiles != null) {
            url = dbFiles.getUrl();
        } else {
            // 上传文件到磁盘
            file.transferTo(uploadFile);
            // 数据库若不存在重复文件，则不删除刚才上传的文件
            url = "http://localhost:9301/file/" + fileUUID;
        }


        // 存储数据库
        Files saveFile = new Files();
        saveFile.setName(originalFilename);
        saveFile.setType(type);
        saveFile.setSize(size/1024); // 单位 kb
        saveFile.setUrl(url);
        saveFile.setMd5(md5);
        fileMapper.insert(saveFile);

        return url;
    }

    @ApiResponses({
            @ApiResponse(code = 200, message = "更新成功")
    })
    @ApiOperation("下载文件")
    @GetMapping("/{fileUUID}")
    @SystemLog(businessName = "下载文件")
    public void download(@PathVariable String fileUUID, HttpServletResponse response) throws IOException {
        // 根据文件的唯一标识码获取文件
        File uploadFile = new File(fileUploadPath + fileUUID);
        // 设置输出流的格式
        ServletOutputStream os = response.getOutputStream();
        response.addHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileUUID, "UTF-8"));
        response.setContentType("application/octet-stream");

        // 读取文件的字节流
        os.write(FileUtil.readBytes(uploadFile));
        os.flush();
        os.close();
    }

    /**
     * 通过文件的md5查询文件
     * @param md5
     * @return
     */
    private Files getFileByMd5(String md5) {
        // 查询文件的md5是否存在
        QueryWrapper<Files> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("md5", md5);
        List<Files> filesList = fileMapper.selectList(queryWrapper);
        return filesList.size() == 0 ? null : filesList.get(0);
    }
}
