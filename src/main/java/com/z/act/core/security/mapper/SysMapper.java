package com.z.act.core.security.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SysMapper {

    @Select("show tables like #{pattern}")
    List<String> showTables(@Param("pattern") String pattern);
}
