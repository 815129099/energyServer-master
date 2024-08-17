package com.example.demo.service.power;

import com.example.demo.entity.ExportPower;
import com.example.demo.entity.OrigDL;
import com.example.demo.entity.Params;
import com.example.demo.mapper.power.OrigDLDao;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.example.demo.mapper.util.MessageDao;
import com.example.demo.util.date.DateUtil;
import com.example.demo.util.python.CsvUtil;
import com.example.demo.util.python.SocketUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.*;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author lwx
 * @since 2019-05-08
 */
@Service
public class OrigDLServiceImpl extends ServiceImpl<OrigDLDao, OrigDL> implements OrigDLService {

    @Autowired
    private OrigDLDao origDLDao;
    @Autowired
    private ThreadPoolTaskExecutor threadPoolTaskExecutor;
    @Autowired
    private MessageDao messageDao;

    //获取电量数据
    @Override
    public PageInfo<Map> getPowerData(Params param) {
        System.out.println(param.toString());
        Map<String,Object> map = new HashMap();
        //有数据的电表的电量数据
        List<Map> maps = null;
        //所有电表的信息
        List<Map> maps1 = null;
        PageInfo<Map> page = null;
        /*
        if(StringUtils.isEmpty(param.getEMeterName()) && !StringUtils.isEmpty(param.getEStationName())){
            //按厂站查
            maps = origDLDao.getAllDataByEStationName(param);
            //获取厂站下所有电表
            maps1 = origDLDao.getAllDataByEStationName1(param);
        }else if(StringUtils.isEmpty(param.getEMeterName()) && StringUtils.isEmpty(param.getEStationName())){
            //按厂站查
            maps = origDLDao.getAllData(param);
            //获取厂站下所有电表
            maps1 = origDLDao.getAllData1(param);
        }else {
            //按电表查
            maps = origDLDao.getPowerData(param);
            maps1 = origDLDao.getPowerData1(param);
        }
        List<Map> list = new ArrayList<>();
        int j = 0;
        List<Integer> idList = new ArrayList<>();
        for(int i=0;i<maps.size();i++){
            if(idList.contains(Integer.parseInt(maps.get(i).get("EMeterID").toString()))){
                if(i==maps.size()-1 || !idList.get(j-1).equals(Integer.parseInt(maps.get(i+1).get("EMeterID").toString()))){
                    double endNumber1 = 0.0;
                    if(param.getPowerType().equals("ZxygZ")){
                        endNumber1 = Double.valueOf(maps.get(i).get("ZxygZ").toString());
                        list.get(j-1).put("endNumber",endNumber1);
                    }else if(param.getPowerType().equals("FxygZ")){
                        endNumber1 = Double.valueOf(maps.get(i).get("FxygZ").toString());
                        list.get(j-1).put("endNumber",endNumber1);
                    }else if(param.getPowerType().equals("ZxwgZ")){
                        endNumber1 = Double.valueOf(maps.get(i).get("ZxwgZ").toString());
                        list.get(j-1).put("endNumber",endNumber1);
                    }else if(param.getPowerType().equals("FxwgZ")){
                        endNumber1 = Double.valueOf(maps.get(i).get("FxwgZ").toString());
                        list.get(j-1).put("endNumber",endNumber1);
                    }
                    double beginNumber1 =   Double.valueOf(list.get(j-1).get("beginNumber").toString());
                    list.get(j-1).put("difValue",String.format("%.6f",endNumber1-beginNumber1));
                    list.get(j-1).put("powerTotal",String.format("%.6f",(endNumber1-beginNumber1)*Double.valueOf(list.get(j-1).get("num").toString())));
                }
            }else {
                //如果该电表ID不存在就添加
                idList.add(j,Integer.parseInt(maps.get(i).get("EMeterID").toString()));
                j++;
                Map<String,Object> map1 = new LinkedHashMap<>();
                //添加功
                if(param.getPowerType().equals("ZxygZ")){
                    map1.put("beginNumber",Double.valueOf(maps.get(i).get("ZxygZ").toString()));
                }else if(param.getPowerType().equals("FxygZ")){
                    map1.put("beginNumber",Double.valueOf(maps.get(i).get("FxygZ").toString()));
                }else if(param.getPowerType().equals("ZxwgZ")){
                    map1.put("beginNumber",Double.valueOf(maps.get(i).get("ZxwgZ").toString()));
                }else if(param.getPowerType().equals("FxwgZ")){
                    map1.put("beginNumber",Double.valueOf(maps.get(i).get("FxwgZ").toString()));
                }
                int MultiplyRatio = Integer.parseInt(maps.get(i).get("MultiplyRatio").toString());
                Double num;
                if(MultiplyRatio==1){
                    num = Double.valueOf(maps.get(i).get("num").toString());
                }else {
                    num = 1.0;
                }
                map1.put("num",num);
                map1.put("EMeterName",maps.get(i).get("EMeterName").toString());
                map1.put("EMeterID",maps.get(i).get("EMeterID").toString());
                map1.put("beginTime", DateUtil.DateToString(param.getBeginTime()));
                map1.put("endTime",DateUtil.DateToString(param.getEndTime()));
                list.add(j-1,map1);
            }
        }
        //遍历所有电表，筛选出没有数据的电表
        List<Map> collect = maps1.stream().filter(map1->list.stream().noneMatch(map2->map2.get("EMeterID").toString().equals(map1.get("EMeterID").toString()))).collect(Collectors.toList());
        for (Map n:collect) {
            Map<String,Object> map1 = new LinkedHashMap<>();
            int MultiplyRatio = Integer.parseInt(n.get("MultiplyRatio").toString());
            Double num;
            if(MultiplyRatio==1){
                num = Double.valueOf(n.get("num").toString());
            }else {
                num = 1.0;
            }
            map1.put("num",num);
            map1.put("EMeterName",n.get("EMeterName").toString());
            map1.put("EMeterID",n.get("EMeterID").toString());
            map1.put("beginTime", DateUtil.DateToString(param.getBeginTime()));
            map1.put("endTime",DateUtil.DateToString(param.getEndTime()));
            map1.put("difValue","--");
            map1.put("beginNumber","--");
            map1.put("endNumber","--");
            map1.put("powerTotal","--");
            list.add(map1);
        }*/
        List<Map> list = new ArrayList<>();
        //获取电量数据
        maps = origDLDao.getPowerData(param);
        if (!CollectionUtils.isEmpty(maps)) {
            //获取电表信息
            maps1 = origDLDao.getPowerData1(param);
            for (Map m :maps1) {
                //过滤出该电表下的数据
                List<Map> mapList = maps.stream().filter(ms1 ->m.get("EMeterID").equals(ms1.get("EMeterID"))).collect(Collectors.toList());
                //mapList.stream().forEach(map1 -> System.out.println(map1.get("EMeterID").toString()+","+DateUtil.DateToString((Date)map1.get("TimeTag"))+","+map1.get("ZxygZ")
                //       +","+map1.get("FxygZ")));
                //如果该电表在该时间段没数据，则--
                if(mapList.size()==0){
                    Map<String,Object> map1 = new HashMap<>();
                    map1.put("EMeterID",m.get("EMeterID"));
                    map1.put("EMeterName",m.get("EMeterName"));
                    int MultiplyRatio = Integer.parseInt(m.get("MultiplyRatio").toString());
                    Double num;
                    if(MultiplyRatio==1){
                        num = Double.valueOf(m.get("num").toString());
                    }else {
                        num = 1.0;
                    }
                    map1.put("num",num);
                    map1.put("beginTime", DateUtil.DateToString(param.getBeginTime()));
                    map1.put("endTime",DateUtil.DateToString(param.getEndTime()));
                    map1.put("difValue","--");
                    map1.put("beginNumber","--");
                    map1.put("endNumber","--");
                    map1.put("powerTotal","--");
                    list.add(map1);
                }else {
                    //如果有数据
                    //num是倍率
                    Map<String,Object> map1 = new LinkedHashMap<>();
                    //MultiplyRatio变电比
                    int MultiplyRatio = Integer.parseInt(mapList.get(0).get("MultiplyRatio").toString());
                    Double num;
                    if(MultiplyRatio==1){
                        num = Double.valueOf(m.get("num").toString());
                    }else {
                        num = 1.0;
                    }
                    //beginNumber开始读数、endNumber结束读数、difValue读数差、powerTotal总电量
                    double beginNumber=0.0 ,endNumber=0.0 ,difValue=0.0 ,powerTotal=0.0 ;
                    beginNumber = Double.valueOf(mapList.get(0).get(param.getPowerType()).toString());
                    endNumber = Double.valueOf(mapList.get(mapList.size()-1).get(param.getPowerType()).toString());
                    difValue = endNumber-beginNumber;
                    powerTotal = difValue*num;
                    map1.put("num",num);
                    map1.put("EMeterName",mapList.get(0).get("EMeterName"));
                    map1.put("EMeterID",mapList.get(0).get("EMeterID"));
                    map1.put("difValue",String.format("%.4f",difValue));
                    map1.put("beginNumber",String.format("%.4f",beginNumber));
                    map1.put("endNumber",String.format("%.4f",endNumber));
                    map1.put("powerTotal",String.format("%.4f",powerTotal));
                    map1.put("beginTime", DateUtil.DateToString((Date) mapList.get(0).get("TimeTag")));
                    map1.put("endTime",DateUtil.DateToString((Date) mapList.get(mapList.size()-1).get("TimeTag")));
                    list.add(map1);
                }

            }
        }

        /*
        list.stream().forEach(map1 -> System.out.println(map1.get("EMeterName").toString()+","+map1.get("powerTotal").toString()+","+
                map1.get("num").toString()+","+map1.get("difValue").toString()+","+
                map1.get("beginNumber").toString()+","+map1.get("endNumber").toString()+","+
                map1.get("beginTime").toString()+","+map1.get("endTime").toString()+","+
                map1.get("EMeterID").toString()));*/
        PageHelper.startPage(Integer.parseInt(param.getPageNum()), Integer.parseInt(param.getPageSize()));
        page = new PageInfo(list);
        return page;
    }

    //获取电量分析
    @Override
    public Map getPowerAnalyze(Params params) {
        PageInfo<Map> page = null;
        PageHelper.startPage(Integer.parseInt(params.getPageNum()), Integer.parseInt(params.getPageSize()));
        Map<String,Object> map = new HashMap<>();
        //获取数据库的map
        List<Map> maps = null;
        //图表数据
        List<Map> chartList = new ArrayList<Map>();
        List<LinkedHashMap> newMaps = new ArrayList<LinkedHashMap>();
        if(params.getDateType().equals("hour")){
            maps = origDLDao.getPowerAnalyzeByHour(params);
            if (!CollectionUtils.isEmpty(maps)) {
                int MultiplyRatio = Integer.parseInt(maps.get(0).get("MultiplyRatio").toString());
                Double num;
                if(MultiplyRatio==1){
                    num = Double.valueOf(maps.get(0).get("num").toString());
                }else {
                    num = 1.0;
                }
                for(int i=0;i<maps.size()-1;i++){
                    //用于数据显示
                    LinkedHashMap map1 = new LinkedHashMap();
                    String Time = maps.get(i).get("Time").toString();
                    map1.put("Time", Time);

                    map1.put("num",num);
                    map1.put("beginTime",DateUtil.DateToString((Date) maps.get(i).get("TimeTag")));
                    map1.put("endTime",DateUtil.DateToString((Date)maps.get(i+1).get("TimeTag")));
                    Double beginNumber = 0.0,endNumber = 0.0;
                    beginNumber = Double.valueOf(maps.get(i).get(params.getPowerType()).toString());
                    endNumber = Double.valueOf(maps.get(i+1).get(params.getPowerType()).toString());

                    map1.put("beginNumber",beginNumber);
                    map1.put("endNumber",endNumber);
                    int totalNumber = (int)((endNumber-beginNumber)*num);
                    map1.put("totalNumber",totalNumber);
                    newMaps.add(i,map1);
                    //用于图表显示
                    Map<String ,Object> map2 = new LinkedHashMap<>();
                    map2.put("name",Time);
                    map2.put("value",totalNumber);
                    chartList.add(i,map2);
                }
            }
        }else if(params.getDateType().equals("day")){
            maps = origDLDao.getPowerAnalyzeByDay(params);
            if (!CollectionUtils.isEmpty(maps)) {
                for(int i=0;i<maps.size()-1;i++){
                    //用于数据显示
                    LinkedHashMap map1 = new LinkedHashMap();
                    String Time = maps.get(i).get("Time").toString();
                    map1.put("Time", Time);
                    int MultiplyRatio = Integer.parseInt(maps.get(i).get("MultiplyRatio").toString());
                    Double num;
                    if(MultiplyRatio==1){
                        num = Double.valueOf(maps.get(i).get("num").toString());
                    }else {
                        num = 1.0;
                    }
                    map1.put("num",num);
                    map1.put("beginTime",DateUtil.DateToString((Date) maps.get(i).get("TimeTag")));
                    map1.put("endTime",DateUtil.DateToString((Date)maps.get(i+1).get("TimeTag")));
                    Double beginNumber = 0.0,endNumber = 0.0;
                    beginNumber = Double.valueOf(maps.get(i).get(params.getPowerType()).toString());
                    endNumber = Double.valueOf(maps.get(i+1).get(params.getPowerType()).toString());
                    map1.put("beginNumber",beginNumber);
                    map1.put("endNumber",endNumber);
                    int totalNumber = (int)((endNumber-beginNumber)*num);
                    map1.put("totalNumber",totalNumber);
                    newMaps.add(i,map1);
                    //用于图表显示
                    Map<String ,Object> map2 = new LinkedHashMap<>();
                    map2.put("name",Time);
                    map2.put("value",totalNumber);
                    chartList.add(i,map2);
                }
            }
        }

        page = new PageInfo(newMaps);
        map.put("page",page);
        map.put("chartList",chartList);
        return map;
    }

    @Override
    public List<List<Object>> getData() {
        List<String> days = DateUtil.getYesDay();
        List<Map> list = this.origDLDao.getYesData(days.get(1),days.get(0));
        List<String> strings = new ArrayList<>();
        if(list==null || list.size()<=0){
            return null;
        }
        List<List<Object>> dataList = new ArrayList<>();
        for(Map s:list){
            String name = s.get("EMeterName").toString();
            if(strings.contains(name)){
                List<Object> objects = dataList.get(dataList.size()-1);
                dataList.remove(objects);
                Double beforeData = (Double) objects.get(1);
                Double yesData = (Double) s.get("PowerTotal");
                objects.add(s.get("PowerTotal"));
                if(beforeData==0 || yesData==0){
                    //objects.add("<span  class='colorGrass'>↑100%</span>");
                    continue;
                }else{
                    Double rate = yesData/beforeData-1;
                    NumberFormat num = NumberFormat.getPercentInstance();
                    num.setMaximumIntegerDigits(3);
                    num.setMaximumFractionDigits(4);
                    if(rate>0){
                        objects.add("<span  class='colorGrass'>↑"+ num.format(rate)+"</span>");
                    }else if(rate<0){
                        objects.add("<span  class='colorRed'>↓"+ num.format(rate)+"</span>");
                    }
                }
                dataList.add(objects);
            }else{
                strings.add(name);
                List<Object> l = new ArrayList<>();
                l.add(name);
                l.add(s.get("PowerTotal"));
                dataList.add(l);
            }
        }
        Collections.sort(dataList, new Comparator<List<Object>>() {
            @Override
            public int compare(List<Object> o1, List<Object> o2) {
                double yesNum = (double)o2.get(2);
                double num = (double)o1.get(2);
                return new Double(yesNum-num).intValue();
            }
        });
        return dataList;
    }

    @Override
    public List<Double> getCenterData() {
        List<Double> list = new ArrayList<>();
        int normalNum = this.origDLDao.getNormalNum(DateUtil.getNowByDay());
        list.add(Double.valueOf(normalNum));
        int badNum = this.origDLDao.getBadNum(DateUtil.getNowByDay());
        list.add(Double.valueOf(badNum));
        double rate = Double.valueOf(normalNum)/Double.valueOf(normalNum+badNum);
        list.add(rate);
        //获取昨天总电量
        double yesTotalPower = this.origDLDao.getYesTotalPower(DateUtil.getYesDay().get(0));
        list.add(yesTotalPower);
        //获取今天总电量
        double totalPower = this.origDLDao.getTotalPower(DateUtil.getNowByDay());
        list.add(totalPower);
        list.add(totalPower/yesTotalPower);
        return list;
    }

    @Override
    public Map getPeakData() {
        List<Map> list = this.origDLDao.getPeakData(DateUtil.getYesDay().get(0));
        if(list.size()>0){
            List<String> names = new ArrayList<>();
            List<Double> peakList = new ArrayList<>();
            List<Double> flatList = new ArrayList<>();
            List<Double> ravineList = new ArrayList<>();
            List<Double> peakPercentList = new ArrayList<>();
            for(Map map:list){
                names.add(map.get("EMeterName").toString());
                peakList.add(Double.valueOf(map.get("peak").toString()));
                flatList.add(Double.valueOf(map.get("flat").toString()));
                ravineList.add(Double.valueOf(map.get("ravine").toString()));
                peakPercentList.add(Double.valueOf(map.get("peakPercent").toString()));
            }
            Map map = new HashMap();
            map.put("peak",peakList);
            map.put("name",names);
            map.put("flat",flatList);
            map.put("ravine",ravineList);
            map.put("peakPercent",peakPercentList);
            return map;
        }
        return null;
    }

    @Override
    public List<Map> getPowerTotal() {
        return this.origDLDao.getPowerTotal(DateUtil.getYesDay().get(0));
    }


    @Override
    public List<List<Object>> getExceptionData() {
        List<Map> list = this.origDLDao.getExceptionData(DateUtil.getYesDay().get(0));
        if(list.size()>0){
            List<List<Object>> dataList = new ArrayList<>();
            for(Map s:list){
                List<Object> objects = new ArrayList<>();
                objects.add(s.get("EMeterName"));
                double num = (Double) s.get("PowerTotal");
                if(num>0){
                    objects.add("<span  class='colorGrass'>正常</span>");
                }else {
                    objects.add("<span  class='colorRed'>异常</span>");
                }
                dataList.add(objects);
            }
            return dataList;
        }
        return null;
    }

    //获取电量预测
    @Override
    public Map getPowerPredict(Params params) {
        PageInfo<Map> page = null;
        PageHelper.startPage(Integer.parseInt(params.getPageNum()), Integer.parseInt(params.getPageSize()));
        Map<String,Object> map = new HashMap<>();
        //获取数据库的map
        List<Map> maps = null;
        //图表数据
        List<Map> chartList = new ArrayList<Map>();
        List<LinkedHashMap> newMaps = new ArrayList<LinkedHashMap>();
        if(params.getDateType().equals("hour")){
            maps = origDLDao.getPowerPredictByHour(params);
            if (!CollectionUtils.isEmpty(maps)) {
                int MultiplyRatio = Integer.parseInt(maps.get(0).get("MultiplyRatio").toString());
                Double num;
                if(MultiplyRatio==1){
                    num = Double.valueOf(maps.get(0).get("num").toString());
                }else {
                    num = 1.0;
                }
                for(int i=0;i<maps.size()-1;i++){
                    //用于数据显示
                    LinkedHashMap map1 = new LinkedHashMap();
                    String Time = maps.get(i).get("Time").toString();
                    map1.put("Time", Time);

                    map1.put("num",num);
                    map1.put("beginTime",DateUtil.DateToString((Date) maps.get(i).get("TimeTag")));
                    map1.put("endTime",DateUtil.DateToString((Date)maps.get(i+1).get("TimeTag")));
                    Double beginNumber = 0.0,endNumber = 0.0;
                    beginNumber = Double.valueOf(maps.get(i).get(params.getPowerType()).toString());
                    endNumber = Double.valueOf(maps.get(i+1).get(params.getPowerType()).toString());

                    map1.put("beginNumber",beginNumber);
                    map1.put("endNumber",endNumber);
                    int totalNumber = (int)((endNumber-beginNumber)*num);
                    map1.put("totalNumber",totalNumber);
                    Double predictTotalNumber = Double.valueOf(maps.get(i).get("PredictZxygZ").toString());
                    map1.put("predictTotalNumber",predictTotalNumber);
                    Double price = Double.valueOf(maps.get(i).get("price").toString());
                    map1.put("price",price);
                    newMaps.add(i,map1);
                    //用于图表显示
                    Map<String ,Object> map2 = new LinkedHashMap<>();
                    map2.put("name",Time);
                    map2.put("value",totalNumber);
                    map2.put("predictTotalNumber",predictTotalNumber);
                    map2.put("price",price);
                    chartList.add(i,map2);
                }
            }
        }else if(params.getDateType().equals("day")){
            maps = origDLDao.getPowerPredictByDay(params);
            if (!CollectionUtils.isEmpty(maps)) {
                for(int i=0;i<maps.size()-1;i++){
                    //用于数据显示
                    LinkedHashMap map1 = new LinkedHashMap();
                    String Time = maps.get(i).get("Time").toString();
                    map1.put("Time", Time);
                    int MultiplyRatio = Integer.parseInt(maps.get(i).get("MultiplyRatio").toString());
                    Double num;
                    if(MultiplyRatio==1){
                        num = Double.valueOf(maps.get(i).get("num").toString());
                    }else {
                        num = 1.0;
                    }
                    map1.put("num",num);
                    map1.put("beginTime",DateUtil.DateToString((Date) maps.get(i).get("TimeTag")));
                    map1.put("endTime",DateUtil.DateToString((Date)maps.get(i+1).get("TimeTag")));
                    Double beginNumber = 0.0,endNumber = 0.0;
                    beginNumber = Double.valueOf(maps.get(i).get(params.getPowerType()).toString());
                    endNumber = Double.valueOf(maps.get(i+1).get(params.getPowerType()).toString());
                    map1.put("beginNumber",beginNumber);
                    map1.put("endNumber",endNumber);
                    int totalNumber = (int)((endNumber-beginNumber)*num);
                    map1.put("totalNumber",totalNumber);
                    Double predictTotalNumber = Double.valueOf(maps.get(i).get("PredictZxygZ").toString());
                    map1.put("predictTotalNumber",predictTotalNumber);
                    Double price = Double.valueOf(maps.get(i).get("price").toString());
                    map1.put("price",price);
                    newMaps.add(i,map1);
                    //用于图表显示
                    Map<String ,Object> map2 = new LinkedHashMap<>();
                    map2.put("name",Time);
                    map2.put("value",totalNumber);
                    map2.put("value1",totalNumber);
                    map2.put("predictTotalNumber",predictTotalNumber);
                    map2.put("price",price);
                    chartList.add(i,map2);
                }
            }
        }

        page = new PageInfo(newMaps);
        map.put("page",page);
        map.put("chartList",chartList);
        return map;
    }

    @Override
    public String generaPowerPredict(Params params) {
        threadPoolTaskExecutor.execute(() -> {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(params.getEndTime());
            calendar.add(Calendar.DATE,1);
            Date predictDate = calendar.getTime();
            //多查一天数据
            calendar.add(Calendar.DATE,1);
            params.setEndTime(calendar.getTime());
            List<Map> maps = origDLDao.getPowerForPowerPredict(params);
            List<String> dataList = new ArrayList<>();
            //1、查询数据
            if (!CollectionUtils.isEmpty(maps)) {
                List<Integer> saveIdSet = new ArrayList<>();
                int MultiplyRatio = Integer.parseInt(maps.get(0).get("MultiplyRatio").toString());
                Double num;
                if(MultiplyRatio==1){
                    num = Double.valueOf(maps.get(0).get("num").toString());
                }else {
                    num = 1.0;
                }
                for(int i=0;i<maps.size()-1;i++){
                    String data = "";
                    Date timeTag = (Date) maps.get(i).get("TimeTag");
                    calendar.setTime(timeTag);
                    //month
                    int month = calendar.get(Calendar.MONTH)+1;
                    //date
                    int date = calendar.get(Calendar.DAY_OF_MONTH);
                    //hour
                    int hour = calendar.get(Calendar.HOUR_OF_DAY);
                    //peak_flat_valley
                    Integer peakFlatValley = DateUtil.getPeakFlatValley(hour);
                    //power_num
                    Double beginNumber = 0.0,endNumber = 0.0;
                    beginNumber = Double.valueOf(maps.get(i).get(params.getPowerType()).toString());
                    endNumber = Double.valueOf(maps.get(i+1).get(params.getPowerType()).toString());
                    int totalNumber = (int)((endNumber-beginNumber)*num);
                    //price
                    int id = (int) maps.get(i).get("id");

                    if (DateUtil.isSameDay(timeTag, predictDate)) {
                        saveIdSet.add(id);
                    }
                    data = month+","+date+","+hour+","+peakFlatValley+","+totalNumber+","+id;
                    dataList.add(data);
                }

                //2、构建数据
                String headDataStr = "month,date,hour,peak_flat_valley,power_num,id";
                String filePath = "D:\\lunwengit\\LTSF-Linear-main\\lwx\\data\\power_"+System.currentTimeMillis()+".csv";
                String saveFilePath = "D:\\lunwengit\\LTSF-Linear-main\\lwx\\data\\power_predict_"+System.currentTimeMillis()+".csv";
                CsvUtil.writeToCsv(headDataStr, dataList, filePath, false);

                //3、请求socket
                try {
                    SocketUtil.testSocket(1,filePath,saveFilePath);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                //4、解析csv文件
                List<Map<String,Object>> list = CsvUtil.readFromCsv(saveFilePath);
                if (!CollectionUtils.isEmpty(list)) {
                    //保存预测值
                    for (Map<String,Object> map:list) {
                        Integer id = null;
                        BigDecimal predictZxygZ = null;
                        try {
                            id = Integer.valueOf(map.get("id").toString());
                            predictZxygZ = new BigDecimal(map.get("PredictZxygZ").toString());
                        } catch (Exception e) {
                            //
                        }
                        if (null != id && null != predictZxygZ && saveIdSet.contains(id)) {
                            //仅保存需要的id
                            origDLDao.savePredictPower(id, predictZxygZ);
                        }
                    }
                }

                //5、保存消息
                String message = "厂站："+params.getEStationName()+"，电表："+params.getEMeterName()+"，日期："+DateUtil.DateToString(params.getEndTime(),"yyyy-MM-dd")+"，电量已预测完毕！";
                messageDao.saveMessage(message, 0, params.getGeNumber());
            }
        });
        return "电量预测中，完成后将下发消息通知";
    }

    @Override
    public String generaPowerPrice(Params params) {
        threadPoolTaskExecutor.execute(() -> {
            params.setEndTime(params.getBeginTime());
            List<Map> maps = origDLDao.getPowerForPowerPredict(params);
            List<String> dataList = new ArrayList<>();
            //1、查询数据
            if (!CollectionUtils.isEmpty(maps)) {
                for(int i=0;i<maps.size();i++){
                    //预测电量
                    BigDecimal predictZxygZ = new BigDecimal(maps.get(i).get(params.getPowerType()).toString());
                    //id
                    int id = (int) maps.get(i).get("id");
                    String data = id+","+predictZxygZ;
                    dataList.add(data);
                }

                //2、构建数据
                String headDataStr = "id,pred";
                String filePath = "D:\\lunwengit\\LTSF-Linear-main\\lwx\\data\\price_"+System.currentTimeMillis()+".csv";
                String saveFilePath = "D:\\lunwengit\\LTSF-Linear-main\\lwx\\data\\price_predict_"+System.currentTimeMillis()+".csv";
                CsvUtil.writeToCsv(headDataStr, dataList, filePath, false);

                //3、请求socket
                try {
                    SocketUtil.testSocket(2,filePath,saveFilePath);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                //4、解析csv文件
                List<Map<String,Object>> list = CsvUtil.readPriceFromCsv(saveFilePath);
                if (!CollectionUtils.isEmpty(list)) {
                    //保存预测值
                    for (Map<String,Object> map:list) {
                        Integer id = null;
                        BigDecimal price = null;
                        try {
                            id = Integer.valueOf(map.get("id").toString());
                            price = new BigDecimal(map.get("price").toString());
                        } catch (Exception e) {
                            //
                        }
                        if (null != id && null != price) {
                            origDLDao.savePredictPrice(id, price);
                        }
                    }
                }

                //5、保存消息
                String message = "厂站："+params.getEStationName()+"，电表："+params.getEMeterName()+"，日期："+DateUtil.DateToString(params.getEndTime(),"yyyy-MM-dd")+"，电力定价完毕！";
                messageDao.saveMessage(message, 0, params.getGeNumber());
            }
        });
        return "电力定价中，完成后将下发消息通知";
    }
}
