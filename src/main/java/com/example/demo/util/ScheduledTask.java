package com.example.demo.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.demo.entity.Params;
import com.example.demo.mapper.power.OrigDLDao;
import com.example.demo.mapper.power.OrigRtvDao;
import com.example.demo.mapper.util.UtilDao;
import com.example.demo.util.JSON.JsonUtil;
import com.example.demo.util.MyException.ResultUtil;
import com.example.demo.util.date.DateUtil;
import com.example.demo.util.jna.tcp.Client;
import com.example.demo.util.webSocket.WebSocket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.*;

@Component
public class ScheduledTask {
    @Autowired
    private UtilDao utilDao;
    @Autowired
    private OrigDLDao origDLDao;
    @Autowired
    private ChargeUtil chargeUtil;
    @Autowired
    private Client client;
    @Autowired
    private WebSocket webSocket;
    @Resource
    private OrigRtvDao origRtvDao;

    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    /*自动采集数据
    * 每点的30分，采集上一个小时的数据
    * */
    //@Scheduled(cron = "0 30 * * * ?")
    @Async("asyncServiceExecutor")
    public void AutoGetAllData(){
        System.out.println("触发定时任务");
        byte[] tm = DateUtil.getTmByHour();
        List<Map> mapList = this.utilDao.getErtuIDList();
        for (Map<String, Object> m : mapList) {
            int port = Integer.parseInt(m.get("port").toString());
            String host = m.get("ip").toString();
            int ErtuID = Integer.parseInt(m.get("ErtuID").toString());
            new Thread() {
                @Override
                public void run() {
                    super.run();
                    logger.warn(port + "," + host + "," + ErtuID);
                    try {
                        client.connect(port, host, ErtuID, tm);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }.start();
                }
    }

    /*自动计算峰平谷数据
    * 每天凌晨4点，自动计算前一天的峰平谷数据
    * */
    //@Scheduled(cron = "0 0 4 1/1 * ? ")
    public void AutoGetPeak(){
        List<Map> maps = this.origDLDao.getEMeterNum();
            for (Map<String,Object> m:maps) {
                Params params = new Params();
                params.setEMeterName(m.get("EMeterName").toString());
                params.setEStationName(m.get("EStationName").toString());
                params.setEMeterID(Integer.parseInt(m.get("EMeterID").toString()));
                params.setPowerType("ZxygZ");
                params.setBeginTime(DateUtil.getBeforeDay());
                try {
                    chargeUtil.insertPowerList(params,DateUtil.getBeforeDay1());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
    }


    //3.添加定时任务
    //@Scheduled(cron = "0/5 * * * * ?")
    //或直接指定时间间隔，例如：5秒
    @Scheduled(fixedRate=200000)
    public void sendMessage() {
        System.out.println("定时发送数据");
        List<Map> list = this.origRtvDao.getInstantI();
        int num = list.size();
        if(WebSocket.webSockets.size()>0){
            Map map = new HashMap();
            List<String> TimeList = DateUtil.getSecondListByNum(num);
            List<Double> IaList = new ArrayList<>();
            List<Double> IbList = new ArrayList<>();
            List<Double> IcList = new ArrayList<>();
            if(null!=webSocket.IaList && webSocket.IaList.size()>0){
                Map m = list.get(list.size()-1);
                double rate = new BigDecimal(m.get("I").toString()).doubleValue();
                webSocket.IaList.remove(0);
                webSocket.IaList.add((double)(Math.round(((double)(m.get("Ia"))*rate+Math.random()*10)*100))/100.0);
                webSocket.IbList.remove(0);
                webSocket.IbList.add((double)(Math.round(((double)(m.get("Ib"))*rate)*100))/100.0);
                webSocket.IcList.remove(0);
                webSocket.IcList.add((double)(Math.round(((double)(m.get("Ic"))*rate+Math.random()*10)*100))/100.0);
            }else{
                for(Map m:list){
                    double rate = new BigDecimal(m.get("I").toString()).doubleValue();
                    IaList.add((double)(Math.round(((double)(m.get("Ia"))*rate+Math.random()*10)*100))/100.0);
                    IbList.add((double)(Math.round(((double)(m.get("Ib"))*rate)*100))/100.0);
                    IcList.add((double)(Math.round(((double)(m.get("Ic"))*rate+Math.random()*10)*100))/100.0);
                }
                webSocket.IaList = IaList;
                webSocket.IbList = IbList;
                webSocket.IcList = IcList;
            }
            map.put("TimeList",TimeList);
            map.put("IaList",webSocket.IaList);
            map.put("IbList",webSocket.IbList);
            map.put("IcList",webSocket.IcList);
            webSocket.sendAllMessage(JSON.toJSONString(map));
        }

    }
}
