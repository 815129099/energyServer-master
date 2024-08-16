package com.example.demo.mapper.util;



import com.example.demo.entity.Record;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author lwx
 * @since 2019-03-26
 */
@Mapper
public interface MessageDao {

    List<LinkedHashMap> list(@Param("geNumber") String geNumber);

    Integer updateMessage(@Param("id") Integer id, @Param("dataStatus") Integer dataStatus);

    void saveMessage(@Param("message") String message, @Param("dataStatus") Integer dataStatus, @Param("geNumber") String geNumber);
}
