package com.fridge.sales.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fridge.sales.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;

/**
 * 用户Mapper接口
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {

    @Select("SELECT COUNT(*) FROM sys_user WHERE create_time >= #{startTime} AND create_time < #{endTime}")
    Long selectTodayUsers(@Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);
}
