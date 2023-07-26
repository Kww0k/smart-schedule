package com.test.controller;

import com.test.annotation.SystemLog;
import com.test.domain.ResponseResult;
import com.test.domain.dto.AddInfoDto;
import com.test.domain.dto.UpdateInfoDto;
import com.test.service.InfoService;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags = "门店信息接口", description = "管理员对于门店信息的增删改查")
@RestController
@RequestMapping("/store")
public class StoreController {

    @Autowired
    private InfoService infoService;

    @ApiResponses({
            @ApiResponse(code = 200, message = "查询成功")
    })
    @ApiOperation("查看所有门店的信息，也可以根据名称和地址进行模糊查询")
    @GetMapping("/list")
    @SystemLog(businessName = "查看所有门店的信息")
    public ResponseResult getStoreList(@ApiParam("名称信息") @RequestParam(value = "name", required = false) String name,
                                       @ApiParam("地址信息") @RequestParam(value = "address", required = false) String address) {
        return infoService.getStoreList(name, address);
    }

    @ApiResponses({
            @ApiResponse(code = 200, message = "查询成功"),
            @ApiResponse(code = 404, message = "没有这个门店")
    })
    @ApiOperation("根据id查找门店信息")
    @GetMapping("/findById")
    @SystemLog(businessName = "根据id查找门店信息")
    public ResponseResult getStoreInfo(@ApiParam("要找的门店id") @RequestParam("id") Long id) {
        return infoService.getStoreInfo(id);
    }

    @ApiResponses({
            @ApiResponse(code = 200, message = "增加成功"),
            @ApiResponse(code = 400, message = "传参有误")
    })
    @ApiOperation("增加一个门店的信息")
    @PutMapping("/addStore")
    @SystemLog(businessName = "增加一个门店的信息")
    public ResponseResult addStore(@ApiParam("传进来的新门店的信息") @RequestBody AddInfoDto addInfoDto) {
        return infoService.addStore(addInfoDto);
    }

    @ApiResponses({
            @ApiResponse(code = 200, message = "删除成功"),
            @ApiResponse(code = 404, message = "没有这个门店")
    })
    @ApiOperation("删除一个门店的信息")
    @DeleteMapping("/deleteStore")
    @SystemLog(businessName = "删除一个门店的信息")
    public ResponseResult deleteStore(@ApiParam("要删除的门店的id,id为Long类型") @RequestParam("id") Long id) {
        return infoService.deleteStore(id);
    }

    @ApiResponses({
            @ApiResponse(code = 200, message = "更新成功")
    })
    @ApiOperation("更新门店信息")
    @PostMapping("/updateInfo")
    @SystemLog(businessName = "更新门店信息")
    public ResponseResult updateStoreInfo(@ApiParam("要更新的门店信息") @RequestBody UpdateInfoDto updateInfoDto) {
        return infoService.updateStoreInfo(updateInfoDto);
    }

//    @ApiResponses({
//            @ApiResponse(code = 200, message = "删除成功")
//    })
//    @ApiOperation("根据id批量删除门店信息")
//    @PostMapping("/deleteBatch")
//    @SystemLog(businessName = "根据id批量删除门店信息")
//    public ResponseResult deleteBatch(@ApiParam("ids") @RequestBody List<Long> ids) {
//        return infoService.deleteBatch(ids);
//    }

    @ApiResponses({
            @ApiResponse(code = 200, message = "查询成功")
    })
    @ApiOperation("查询门店数量")
    @GetMapping("/number")
    @SystemLog(businessName = "查询门店数量")
    public ResponseResult number() {
        return infoService.number();
    }

}
