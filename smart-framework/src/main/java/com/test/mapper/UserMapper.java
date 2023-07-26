package com.test.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.test.domain.entity.User;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;


/**
 * 用户表(User)表数据库访问层
 *
 * @author makejava
 * @since 2022-12-02 18:47:12
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {

    String selectStore(Long id);

    String selectRole(Long id);

    List<User> selectInfo(Long storeId);

    @Delete("delete from sys_rest where id = #{restId}")
    void realDelete(Long restId);
}
