package com.example.demo.util.python;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CsvUtil {
    /**
     * CSV文件生成方法  字符流追加：FileWriter writer = new FileWriter(file，true)
     *
     * @param headLabel 头部标签
     * @param dataList  数据列表
     * @param filePath  文件路径
     * @param addFlag   是否追加
     */
    public static void writeToCsv(String headLabel, List<String> dataList, String filePath, boolean addFlag) {
        BufferedWriter buffWriter = null;
        try {
            //根据指定路径构建文件对象
            File csvFile = new File(filePath);
            //文件输出流对象，第二个参数时boolean类型,为true表示文件追加（在已有的文件中追加内容）
            FileWriter writer = new FileWriter(csvFile, addFlag);
            //构建缓存字符输出流（不推荐使用OutputStreamWriter）
            buffWriter = new BufferedWriter(writer, 1024);
            //头部不为空则写入头部，并且换行
            if (StringUtils.isNotBlank(headLabel)) {
                buffWriter.write(headLabel);
                buffWriter.newLine();
            }
            //遍历list
            for (String rowStr : dataList) {
                //如果数据不为空，则写入文件内容,并且换行
                if (StringUtils.isNotBlank(rowStr)) {
                    buffWriter.write(rowStr);
                    buffWriter.newLine();//文件写完最后一个换行不用处理
                }
            }
            //刷新流，也就是把缓存中剩余的内容输出到文件
            buffWriter.flush();
        } catch (Exception e) {
            System.out.println("写入csv出现异常");
            e.printStackTrace();
        } finally {
            try {
                //关闭流
                if (buffWriter != null) {
                    buffWriter.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 根据文件路径读取csv文件的内容
     *
     * @param filePath
     * @return
     */
    public static List<Map<String,Object>> readFromCsv(String filePath) {
        List<Map<String,Object>> dataList = new ArrayList();
        BufferedReader buffReader = null;
        try {
            //构建文件对象
            File csvFile = new File(filePath);
            //判断文件是否存在
            if (!csvFile.exists()) {
                System.out.println("文件不存在");
                return dataList;
            }
            //构建字符输入流
            FileReader fileReader = new FileReader(csvFile);
            //构建缓存字符输入流
            buffReader = new BufferedReader(fileReader);
            String line = "";
            //根据合适的换行符来读取一行数据,赋值给line
            while ((line = buffReader.readLine()) != null) {
                if (StringUtils.isNotBlank(line)) {
                    Map<String,Object> map = new HashMap<>();
                    String[] split = line.split(",");
                    map.put("id",split[5]);
                    map.put("PredictZxygZ",split[6]);
                    //数据不为空则加入列表
                    dataList.add(map);
                }
            }
        } catch (Exception e) {
            System.out.println("读取csv文件发生异常");
            e.printStackTrace();
        } finally {
            try {
                //关闭流
                if (buffReader != null) {
                    buffReader.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return dataList;
    }

    /**
     * 根据文件路径读取csv文件的内容
     *
     * @param filePath
     * @return
     */
    public static List<Map<String,Object>> readPriceFromCsv(String filePath) {
        List<Map<String,Object>> dataList = new ArrayList();
        BufferedReader buffReader = null;
        try {
            //构建文件对象
            File csvFile = new File(filePath);
            //判断文件是否存在
            if (!csvFile.exists()) {
                System.out.println("文件不存在");
                return dataList;
            }
            //构建字符输入流
            FileReader fileReader = new FileReader(csvFile);
            //构建缓存字符输入流
            buffReader = new BufferedReader(fileReader);
            String line = "";
            //根据合适的换行符来读取一行数据,赋值给line
            while ((line = buffReader.readLine()) != null) {
                if (StringUtils.isNotBlank(line)) {
                    Map<String,Object> map = new HashMap<>();
                    String[] split = line.split(",");
                    map.put("id",split[0]);
                    map.put("price",split[2]);
                    map.put("predictPrice",split[3]);
                    map.put("predictPricePower",split[4]);
                    //数据不为空则加入列表
                    dataList.add(map);
                }
            }
        } catch (Exception e) {
            System.out.println("读取csv文件发生异常");
            e.printStackTrace();
        } finally {
            try {
                //关闭流
                if (buffReader != null) {
                    buffReader.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return dataList;
    }

    public static void testWriteToCsv() {
        String headDataStr = "month,date,hour,peak_flat_valley,power_num,price";
        String csvfile = "D:\\lunwengit\\LTSF-Linear-main\\lwx\\data\\power_predict_2024-08-15.csv";
        List<String> dataList = new ArrayList<>();
        dataList.add("5,1,1,1,9568.365,2358.602");
        dataList.add("5,1,2,1,9742.259,2401.467");
        dataList.add("5,1,3,1,9617.787,2370.785");
        writeToCsv(headDataStr, dataList, csvfile, false);
    }

    public static void testReadFromCsv() {
        readFromCsv("D:\\lunwengit\\LTSF-Linear-main\\lwx\\data\\power_predict_1723796348647.csv");
    }

    public static void main(String[] args) {
        testReadFromCsv();
    }
}
