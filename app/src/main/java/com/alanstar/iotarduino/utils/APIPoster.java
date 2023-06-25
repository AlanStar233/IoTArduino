package com.alanstar.iotarduino.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Scanner;

import javax.net.ssl.HttpsURLConnection;

public class APIPoster {
    // Light: 定义 TAG
    public static final String TAG = "APIPoster";

    /**
     * 设置锁状态
     * @param lockState 锁状态
     * @return API 返回值
     */
    public String setLockState(int lockState) {
        int responseCode;
        try {
            // 创建 URL 对象
            URL url = new URL(GlobalValue.API_ADDRESS + GlobalValue.SET_LOCK_STATE + "?lockState=" + lockState);

            // 创建 Connection 并设置请求方法
            HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
            connection.setRequestMethod("POST");

            // 发送请求并 get 响应码
            responseCode = connection.getResponseCode();

            // 如果响应码为 200 则返回响应内容
            if (responseCode == 200) {
                // 发送请求并获取输入流
                InputStream inputStream = connection.getInputStream();

                // 将输入流转换为字符串
                return this.beautifyJson(this.convertInputStreamToString(inputStream));
            } else {
                return "Error: " + responseCode;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 设置光照强度
     * @param light 光照强度
     * @return API 返回值
     */
    public String setLightState(int light) {
        int response;
        try {
            // 创建 URL 对象
            URL url = new URL(GlobalValue.API_ADDRESS + GlobalValue.SET_LIGHT_STATE + "?light=" + light);

            // 创建 Connection 并设置请求方法
            HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
            connection.setRequestMethod("POST");

            // 发送请求并 get 响应码
            response = connection.getResponseCode();

            // 如果响应码为 200 则返回响应内容
            if (response == 200) {
                // 发送请求并获取输入流
                InputStream inputStream = connection.getInputStream();
                // 将输入流转换为字符串
                return this.beautifyJson(this.convertInputStreamToString(inputStream));
            } else {
                return "Error: " + response;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 设置温湿度
     * @param temp 温度
     * @param humid 湿度
     * @return API 返回值
     */
    public String setTempAndHumidState(int temp, int humid) {
        int response;
        try {
            // 创建 URL 对象
            URL url = new URL(GlobalValue.API_ADDRESS + GlobalValue.SET_TEMP_AND_HUMID_STATE + "?temp=" + temp + "&humid=" + humid);

            // 创建 Connection 并设置请求方法
            HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
            connection.setRequestMethod("POST");

            // 发送请求并 get 响应码
            response = connection.getResponseCode();

            // 如果响应码为 200 则返回响应内容
            if (response == 200) {
                // 发送请求并获取输入流
                InputStream inputStream = connection.getInputStream();
                // 将输入流转换为字符串
                return this.beautifyJson(this.convertInputStreamToString(inputStream));
            } else {
                return "Error: " + response;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 将输入流转换为字符串
     * @param inputStream 输入流
     * @return 字符串
     */
    private String convertInputStreamToString(InputStream inputStream) {
        Scanner scanner = new Scanner(inputStream, "UTF-8").useDelimiter("\\A");
        String result = scanner.hasNext() ? scanner.next() : "";
        scanner.close();
        return result;
    }

    /**
     * 美化输出的 JSON 字符串
     * @param json JSON 字符串
     * @return 美化后的 JSON 字符串
     */
    private String beautifyJson(String json) {
        try {
            // 创建 Gson 对象
            Gson gson = new GsonBuilder().setPrettyPrinting().create();

            // 创建 JsonParser 对象
            JsonParser jsonParser = new JsonParser();

            // 将 JSON 字符串解析为 JsonElement
            JsonElement jsonElement = jsonParser.parse(json);

            // 使用 toJson 方法将 JsonElement 转换为美化输出的 JSON 字符串
            return gson.toJson(jsonElement);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
