package com.alanstar.iotarduino.utils;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * IoTDataGetter
 * 通过 HTTP 请求获取 IoT 数据, 解析数据
 */
public class IoTDataGetter {
    private static final String TAG = "IoTDataGetter";

    // Light: 锁 Listener
    public interface IoTLockDataListener {
        void onDataReceivedLock(int lockState);

        void onError(String errorMessage);
    }

    // Light: 光照 Listener
    public interface IoTLightDataListener {
        void onDataReceivedLight(List<LightData> light);

        void onError(String errorMessage);
    }

    // Light: 温湿度 Listener
    public interface IoTTempAndHumidDataListener {
        void onDataReceivedTempAndHumid(List<TempAndHumidData> tempAndHumid);

        void onError(String errorMessage);
    }

    // Light: 定义 getLightState 方法, 用于获取光照强度
    public static void getLightState(String url, IoTLightDataListener listener) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<String> future = executor.submit(new GetLightDataTask(url));

        try {
            String response = future.get();
            if (response != null) {
                List<LightData> light = parseLightJsonData(response);
                listener.onDataReceivedLight(light);
            } else {
                listener.onError("Failed to fetch light state.");
            }
        } catch (Exception e) {
            listener.onError("Error occurred: " + e.getMessage());
        }

        executor.shutdown();
    }

    // Light: 定义 getLockState 方法, 用于获取锁的状态
    public static void getLockState(String url, IoTLockDataListener listener) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<String> future = executor.submit(new GetLockDataTask(url));

        try {
            String response = future.get();
            if (response != null) {
                int lockState = parseLockJsonData(response);
                listener.onDataReceivedLock(lockState);
            } else {
                listener.onError("Failed to fetch lock state.");
            }
        } catch (Exception e) {
            listener.onError("Error occurred: " + e.getMessage());
        }

        executor.shutdown();
    }

    // Light: 定义 getTempAndHumidState 方法, 用于获取温湿度
    public static void getTempAndHumidState(String url, IoTTempAndHumidDataListener listener) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<String> future = executor.submit(new GetTempAndHumidDataTask(url));

        try {
            String response = future.get();
            if (response != null) {
                List<TempAndHumidData> tempAndHumid = parseTempAndHumidJsonData(response);
                listener.onDataReceivedTempAndHumid(tempAndHumid);
            } else {
                listener.onError("Failed to fetch temperature and humidity state.");
            }
        } catch (Exception e) {
            listener.onError("Error occurred: " + e.getMessage());
        }

        executor.shutdown();
    }

    // Light: 定义 getLightDataTask Callable 类, 用于获取光照强度
    private static class GetLightDataTask implements Callable<String> {
        private final String url;

        public GetLightDataTask(String url) {
            this.url = url;
        }

        @Override
        public String call() throws Exception {
            return performHttpRequest();
        }

        private String performHttpRequest() throws IOException {
            StringBuilder result = new StringBuilder();
            HttpURLConnection connection = null;
            BufferedReader reader = null;

            try {
                URL apiUrl = new URL(url);
                connection = (HttpURLConnection) apiUrl.openConnection();
                connection.setRequestMethod("GET");

                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    String line;
                    while ((line = reader.readLine()) != null) {
                        result.append(line);
                    }
                } else {
                    throw new IOException("HTTP request failed with response code: " + responseCode);
                }
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        // Ignore
                    }
                }
            }

            return result.toString();
        }
    }

    // Light: 定义 getLockDataTask Callable 类, 用于获取锁的状态
    private static class GetLockDataTask implements Callable<String> {
        private final String url;

        public GetLockDataTask(String url) {
            this.url = url;
        }

        @Override
        public String call() throws Exception {
            return performHttpRequest();
        }

        private String performHttpRequest() throws IOException {
            StringBuilder result = new StringBuilder();
            HttpURLConnection connection = null;
            BufferedReader reader = null;

            try {
                URL apiUrl = new URL(url);
                connection = (HttpURLConnection) apiUrl.openConnection();
                connection.setRequestMethod("GET");

                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    String line;
                    while ((line = reader.readLine()) != null) {
                        result.append(line);
                    }
                } else {
                    throw new IOException("HTTP request failed with response code: " + responseCode);
                }
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        // Ignore
                    }
                }
            }

            return result.toString();
        }
    }

    // Light: 定义 getTempAndHumidDataTask Callable 类, 用于获取温湿度
    private static class GetTempAndHumidDataTask implements Callable<String> {
        private final String url;

        public GetTempAndHumidDataTask(String url) {
            this.url = url;
        }

        @Override
        public String call() throws Exception {
            return performHttpRequest();
        }

        private String performHttpRequest() throws IOException {
            StringBuilder result = new StringBuilder();
            HttpURLConnection connection = null;
            BufferedReader reader = null;

            try {
                URL apiUrl = new URL(url);
                connection = (HttpURLConnection) apiUrl.openConnection();
                connection.setRequestMethod("GET");

                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    String line;
                    while ((line = reader.readLine()) != null) {
                        result.append(line);
                    }
                } else {
                    throw new IOException("HTTP request failed with response code: " + responseCode);
                }
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        // Ignore
                    }
                }
            }

            return result.toString();
        }
    }

    // Light: 解析 response 返回的光照强度, 获取 light 和 timestamp 并重新封装成 LightData
    private static List<LightData> parseLightJsonData(String json) {
        List<LightData> lightDataList = new ArrayList<>();

        try {
            JSONObject jsonObject = new JSONObject(json);
            JSONObject dataObject = jsonObject.getJSONObject("data");
            JSONArray lightArray = dataObject.getJSONArray("light");

            // Light: 从最后一位开始解析, 回到第一位
            for (int i = lightArray.length() - 1; i >= 0; i--) {
                JSONObject lightObject = lightArray.getJSONObject(i);

                int light = lightObject.getInt("light");
                String timestamp = lightObject.getString("timestamp");

                LightData lightData = new LightData(light, timestamp);
                lightDataList.add(lightData);
            }
        } catch (JSONException e) {
            Log.e(TAG, "parseLightJsonData Error: ", e);
        }

        return lightDataList;
    }

    // Light: 解析 response 返回的门锁状态, 获取 lockState
    private static int parseLockJsonData(String json) {
        int lockState = 0;
        try {
            JSONObject jsonObject = new JSONObject(json);
            JSONObject dataObject = jsonObject.getJSONObject("data");
            lockState = dataObject.getInt("lockState");
        } catch (JSONException e) {
            Log.e(TAG, "parseLockJsonData Error: ", e);
            Log.e(TAG, "parseLockJsonData Error Json: " + json);
        }
        return lockState;
    }

    // Light: 解析 response 返回的温湿度, 获取 temperature, humidity 和 timestamp 并重新封装成 TempAndHumidData
    private static List<TempAndHumidData> parseTempAndHumidJsonData(String json) {
        List<TempAndHumidData> tempAndHumidDataList = new ArrayList<>();

        try {
            JSONObject jsonObject = new JSONObject(json);
            JSONObject dataObject = jsonObject.getJSONObject("data");
            JSONArray tempAndHumidArray = dataObject.getJSONArray("mixedData");

            // Light: 从最后一位开始解析, 回到第一位
            for (int i = tempAndHumidArray.length() - 1; i >= 0; i--) {
                JSONObject tempAndHumidObject = tempAndHumidArray.getJSONObject(i);

                int temperature = tempAndHumidObject.getInt("temperature");
                int humidity = tempAndHumidObject.getInt("humidity");
                String timestamp = tempAndHumidObject.getString("timestamp");

                TempAndHumidData tempAndHumidData = new TempAndHumidData(temperature, humidity, timestamp);
                tempAndHumidDataList.add(tempAndHumidData);
            }
        } catch (JSONException e) {
            Log.e(TAG, "parseTempAndHumidJsonData Error: ", e);
        }
        return tempAndHumidDataList;
    }

    // Light: 对 LightData 格式化数据进行定义
    public static class LightData {
        private final int light;
        private final String timestamp;

        public LightData(int light, String timestamp) {
            this.light = light;
            this.timestamp = timestamp;
        }

        public int getLight() {
            return light;
        }

        public String getTimestamp() {
            return timestamp;
        }
    }

    // Light: 对 TempAndHumidData 格式化数据进行定义
    public static class TempAndHumidData {
        private final int temperature;
        private final int humidity;
        private final String timestamp;

        public TempAndHumidData(int temperature, int humidity, String timestamp) {
            this.temperature = temperature;
            this.humidity = humidity;
            this.timestamp = timestamp;
        }

        public int getTemperature() {
            return temperature;
        }

        public int getHumidity() {
            return humidity;
        }

        public String getTimestamp() {
            return timestamp;
        }
    }
}
