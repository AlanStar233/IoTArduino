package com.alanstar.iotarduino.utils;

/**
 * 全局常量
 */
public class GlobalValue {
    public static final String CONFIG_FILE_NAME = "config.json";
    public static final int DEFAULT_API_FRESH_TIME = 5;
    public static final int DEFAULT_CHARTS_FRESH_TIME = 5;
    public static final String API_ADDRESS = "https://api.biliforum.cn/";
    public static final String SET_LOCK_STATE = "v1/setLockState/";
    public static final String GET_LOCK_STATE = "v1/getLockState/";
    public static final String SET_LIGHT_STATE = "v1/setLightState/";
    public static final String GET_LIGHT_STATE = "v1/getLightState/";
    public static final String SET_TEMP_AND_HUMID_STATE = "v1/setTempAndHumidState/";
    public static final String GET_TEMP_AND_HUMID_STATE = "v1/getTempAndHumidState/";
}
