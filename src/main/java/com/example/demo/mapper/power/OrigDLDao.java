package com.example.demo.mapper.power;

import com.example.demo.entity.Charge;
import com.example.demo.entity.OrigDL;
import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.example.demo.entity.Params;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.scheduling.annotation.Async;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author lwx
 * @since 2019-05-08
 */
@Mapper
public interface OrigDLDao extends BaseMapper<OrigDL> {

    void insertOrigDL(OrigDL origDL);

    List<Map> getPowerData(Params param);
    List<Map> getPowerData1(Params param);

    List<Map> getPowerAnalyzeByHour(Params params);

    List<Map> getPowerAnalyzeByDay(Params params);

    List<Map> getPowerByMonthAndID(@Param("id") int id,@Param("month") String month,@Param("hours") Set<Integer> set);

    List<Map> getEMeterNum();

    List<Map> getEMeterNumByErtuID(@Param("ErtuID") int ErtuID);

    List<Integer> getEMeterIDByEStationName(@Param("EStationName") String EStationName);

    List<Map> getAllDataByEStationName(Params params);
    List<Map> getAllDataByEStationName1(Params params);
    List<Map> getAllData(Params params);
    List<Map> getAllData1(Params params);

    List<Map> getYesData(@Param("yesterday") String yesterday,@Param("beforeDay") String beforeDay);
    //获取正常的电表数
    int getNormalNum(@Param("day") String day);
    //获取故障的电表数
    int getBadNum(@Param("day") String day);
    //获取昨天总电量
    double getYesTotalPower(@Param("day") String day);

    Double getTotalPower(@Param("day") String day);

    List<Map> getPeakData(@Param("day") String day);

    List<Map> getPowerTotal(@Param("day") String day);

    List<Map> getExceptionData(@Param("day") String day);

    List<Map> getPowerPredictByHour(Params params);

    List<Map> getPowerPredictByDay(Params params);

    List<Map> getPowerForPowerPredict(Params params);
    //保存预测值
    Integer savePredictPower(@Param("id") Integer id, @Param("PredictZxygZ") BigDecimal PredictZxygZ);
    //保存预测值
    Integer savePredictPrice(@Param("id") Integer id, @Param("price") BigDecimal price, @Param("predictPrice") BigDecimal predictPrice, @Param("predictPricePower") BigDecimal predictPricePower);
}
