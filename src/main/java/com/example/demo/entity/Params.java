package com.example.demo.entity;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public class Params {
    private String parameter;
    private String EMeterName;
    private String EStationName;
    private Date beginTime;
    private Date endTime;
    //数据来源
    private String Source;
    //功率类型
    private String powerType;
    private String pageNum;
    private String pageSize;
    //时间间隔
    private String dateType;
    private int EMeterID;
    //登录用户
    private String geNumber;

    private List<Message> messageList;

    private List<Integer> flatValue;
    private List<Integer> peakValue;
    private List<Integer> valleyValue;
    private double peakElasticCoefficient;
    private double valleyElasticCoefficient;
    private double flatElasticCoefficient;
    private BigDecimal flatPrice;
    private BigDecimal peakPrice;
    private BigDecimal valleyPrice;

    public List<Integer> getFlatValue() {
        return flatValue;
    }

    public void setFlatValue(List<Integer> flatValue) {
        this.flatValue = flatValue;
    }

    public List<Integer> getPeakValue() {
        return peakValue;
    }

    public void setPeakValue(List<Integer> peakValue) {
        this.peakValue = peakValue;
    }

    public List<Integer> getValleyValue() {
        return valleyValue;
    }

    public void setValleyValue(List<Integer> valleyValue) {
        this.valleyValue = valleyValue;
    }

    public double getPeakElasticCoefficient() {
        return peakElasticCoefficient;
    }

    public void setPeakElasticCoefficient(double peakElasticCoefficient) {
        this.peakElasticCoefficient = peakElasticCoefficient;
    }

    public double getValleyElasticCoefficient() {
        return valleyElasticCoefficient;
    }

    public void setValleyElasticCoefficient(double valleyElasticCoefficient) {
        this.valleyElasticCoefficient = valleyElasticCoefficient;
    }

    public double getFlatElasticCoefficient() {
        return flatElasticCoefficient;
    }

    public void setFlatElasticCoefficient(double flatElasticCoefficient) {
        this.flatElasticCoefficient = flatElasticCoefficient;
    }

    public BigDecimal getFlatPrice() {
        return flatPrice;
    }

    public void setFlatPrice(BigDecimal flatPrice) {
        this.flatPrice = flatPrice;
    }

    public BigDecimal getPeakPrice() {
        return peakPrice;
    }

    public void setPeakPrice(BigDecimal peakPrice) {
        this.peakPrice = peakPrice;
    }

    public BigDecimal getValleyPrice() {
        return valleyPrice;
    }

    public void setValleyPrice(BigDecimal valleyPrice) {
        this.valleyPrice = valleyPrice;
    }

    public List<Message> getMessageList() {
        return messageList;
    }

    public void setMessageList(List<Message> messageList) {
        this.messageList = messageList;
    }

    public String getGeNumber() {
        return geNumber;
    }

    public void setGeNumber(String geNumber) {
        this.geNumber = geNumber;
    }

    public int getEMeterID() {
        return EMeterID;
    }

    public void setEMeterID(int EMeterID) {
        this.EMeterID = EMeterID;
    }

    public String getDateType() {
        return dateType;
    }

    public void setDateType(String dateType) {
        this.dateType = dateType;
    }

    public String getPageNum() {
        return pageNum;
    }

    public void setPageNum(String pageNum) {
        this.pageNum = pageNum;
    }

    public String getPageSize() {
        return pageSize;
    }

    public void setPageSize(String pageSize) {
        this.pageSize = pageSize;
    }

    public String getEMeterName() {
        return EMeterName;
    }

    public String getParameter() {
        return parameter;
    }

    public void setParameter(String parameter) {
        this.parameter = parameter;
    }


    public void setEMeterName(String EMeterName) {
        this.EMeterName = EMeterName;
    }

    public Date getBeginTime() {
        return beginTime;
    }

    public void setBeginTime(Date beginTime) {
        this.beginTime = beginTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public String getSource() {
        return Source;
    }

    public void setSource(String source) {
        Source = source;
    }

    public String getPowerType() {
        return powerType;
    }

    public String getEStationName() {
        return EStationName;
    }

    public void setEStationName(String EStationName) {
        this.EStationName = EStationName;
    }

    public void setPowerType(String powerType) {
        this.powerType = powerType;
    }

    @Override
    public String toString() {
        return "Params{" +
                "parameter='" + parameter + '\'' +
                ", EMeterName='" + EMeterName + '\'' +
                ", EStationName='" + EStationName + '\'' +
                ", beginTime=" + beginTime +
                ", endTime=" + endTime +
                ", Source='" + Source + '\'' +
                ", powerType='" + powerType + '\'' +
                ", pageNum='" + pageNum + '\'' +
                ", pageSize='" + pageSize + '\'' +
                ", dateType='" + dateType + '\'' +
                ", EMeterID=" + EMeterID +
                '}';
    }
}
