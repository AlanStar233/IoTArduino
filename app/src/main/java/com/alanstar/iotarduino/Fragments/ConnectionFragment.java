package com.alanstar.iotarduino.Fragments;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.alanstar.iotarduino.R;
import com.alanstar.iotarduino.utils.IoTDataGetter;
import com.alanstar.iotarduino.utils.TopBarController;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.qmuiteam.qmui.widget.QMUITopBar;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class ConnectionFragment extends Fragment {

    // 引入组件
    QMUITopBar mTopBar;
    TextView tv_lockState;
    TextView APIDelayTimer_Connection;
    LineChart lightLineChart;
    LineChart tempAndHumidLineChart;

    // 创建变量
    boolean isFirstLoad = true; // 是否为第一次加载 Fragment
    int nowTimerCount = 0;  // 当前计时时长
    int targetTimerCount = 5;   // 目标计时时长 (与 settings 同步)
    Timer mTimer;
    TimerTask mTimerTask;

    // Light: 定义 static 接口
    public static final String TAG = "ConnectionFragment";
    public static final String API_ADDRESS = "https://api.biliforum.cn/";
    public static final String GET_LOCK_STATE = "v1/getLockState/";
    public static final String GET_LIGHT_STATE = "v1/getLightState/";
    public static final String GET_TEMP_AND_HUMID_STATE = "v1/getTempAndHumidState/";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_connection, container, false);

        // 注册组件
        initComponents(view);

        // 监听组件

        // Light: 设置 TopBar (在此之前需要清除所有已创建元素)
        TopBarController mTopBarController = new TopBarController();
        mTopBarController.clearTopBar(mTopBar);

        // Light: 设置 lightLineChart
        lightLineChart.setBackgroundColor(Color.WHITE);
        lightLineChart.setDragEnabled(true);
        lightLineChart.setScaleEnabled(true);
        lightLineChart.setScaleXEnabled(true);
        lightLineChart.setScaleYEnabled(true);
        lightLineChart.setPinchZoom(true);  // 捏合缩放

        // Light: 设置 tempAndHumidLineChart
        tempAndHumidLineChart.setBackgroundColor(Color.WHITE);
        tempAndHumidLineChart.setDragEnabled(true);
        tempAndHumidLineChart.setScaleEnabled(true);
        tempAndHumidLineChart.setScaleXEnabled(true);
        tempAndHumidLineChart.setScaleYEnabled(true);
        tempAndHumidLineChart.setPinchZoom(true);  // 捏合缩放

        mTopBar.setTitle("数据大盘");

        return view;
    }

    // Light: 注册组件
    private void initComponents(View view) {
        mTopBar = view.findViewById(R.id.mTopBar);
        tv_lockState = view.findViewById(R.id.tv_lockState);
        APIDelayTimer_Connection = view.findViewById(R.id.APIDelayTimer_Connection);
        lightLineChart = view.findViewById(R.id.lightLineChart);
        tempAndHumidLineChart = view.findViewById(R.id.tempAndHumidLineChart);
    }

    // Light: Fragment 获得焦点
    @Override
    public void onResume() {
        super.onResume();
        // Light: 图表统一刷新 (执行于主线程), 若第一次加载则将标志位设为 false, 否则直接刷新
        if (isFirstLoad) {
            isFirstLoad = false;
            startTimer();
        } else {
            startTimer();
        }
    }

    // Light: Fragment 失去焦点
    @Override
    public void onPause() {
        super.onPause();
        stopTimer();
    }

    // Light: API 调用延时循环逻辑
    private void startTimer() {
        mTimer = new Timer();
        mTimerTask = new TimerTask() {
            @SuppressLint("SetTextI18n")
            @Override
            public void run() {
                // 检测 nowTimerCount 是否与设置相同, 相同则重置计时
                if (nowTimerCount != targetTimerCount) {
                    nowTimerCount += 1;
                }
                else
                {
                    nowTimerCount = 0;
                    // Light: 在主线程 call chartFresherRunnable 刷新图表
                    getActivity().runOnUiThread(chartFresherRunnable);
                }
                // 跳回主线程更新计时 UI
                getActivity().runOnUiThread(() -> APIDelayTimer_Connection.setText(nowTimerCount + "/" + targetTimerCount));
            }
        };
        mTimer.schedule(mTimerTask, 0, 1000);
    }

    // Light: Fragment 失去焦点后的 Timer 处理逻辑
    private void stopTimer() {
        nowTimerCount = 0;
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }
        if (mTimerTask != null) {
            mTimerTask.cancel();
            mTimerTask = null;
        }
    }

    // Light: 图表统一刷新
    Runnable chartFresherRunnable = new Runnable() {
        @Override
        public void run() {
            // Light: 门锁数据
            IoTDataGetter.getLockState(API_ADDRESS + GET_LOCK_STATE, new IoTDataGetter.IoTLockDataListener() {
                @Override
                public void onDataReceivedLock(int lockState) {
                    getActivity().runOnUiThread(() -> tv_lockState.setText(lockState == 1 ? "已锁" : "未锁"));
                }

                @Override
                public void onError(String errorMessage) {
                    getActivity().runOnUiThread(() -> Toast.makeText(getContext(), errorMessage, Toast.LENGTH_SHORT).show());
                }
            });
            // Light: 光照数据
            IoTDataGetter.getLightState(API_ADDRESS + GET_LIGHT_STATE, new IoTDataGetter.IoTLightDataListener() {
                @Override
                public void onDataReceivedLight(List<IoTDataGetter.LightData> light) {
                    // 预定义 entries, 供填入数据 -> DataSet
                    ArrayList<Entry> entries = new ArrayList<>();

                    // 在 LightData 列表中 提取出 light 和 timestamp
                    Log.i(TAG, "onDataReceivedLight: " + light);
                    for (IoTDataGetter.LightData data : light) {
                        int lightValue = data.getLight();
                        String timestamp = data.getTimestamp();
                        // 时间转换 (但其实并没有用上 = =)
                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                            DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
                            DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
//                            timestamp = outputFormatter.format(inputFormatter.parse(timestamp));
                        }
                        // 将数据填入 entries
                        entries.add(new Entry(entries.size(), lightValue));
                    }
                    // 创建 DataSet, 传入 entries, 并赋标题
                    LineDataSet lightDataSet = new LineDataSet(entries, "光照强度");
                    lightDataSet.setColor(Color.YELLOW);
                    LineData dataSet = new LineData(lightDataSet);
                    // Light: 在主线程更新图表
                    getActivity().runOnUiThread(() -> {
                        lightLineChart.setData(dataSet);
                        lightLineChart.invalidate();    // 刷新图表显示
                    });
                }
                @Override
                public void onError(String errorMessage) {

                }
            });
            // Light: 温湿度数据
            IoTDataGetter.getTempAndHumidState(API_ADDRESS + GET_TEMP_AND_HUMID_STATE, new IoTDataGetter.IoTTempAndHumidDataListener() {
                @Override
                public void onDataReceivedTempAndHumid(List<IoTDataGetter.TempAndHumidData> tempAndHumid) {
                    // 预定义 entriesTemp & entriesHumid, 供填入数据 -> DataSet
                    ArrayList<Entry> entriesTemp = new ArrayList<>();
                    ArrayList<Entry> entriesHumid = new ArrayList<>();

                    // 在 TempAndHumidData 列表中 提取出 temp, humid 和 timestamp
                    Log.i(TAG, "onDataReceivedTempAndHumid: " + tempAndHumid);
                    for (IoTDataGetter.TempAndHumidData data : tempAndHumid) {
                        int tempValue = data.getTemperature();
                        int humidValue = data.getHumidity();
                        String timestamp = data.getTimestamp();
                        // 时间转换 (但其实并没有用上 = =)
                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                            DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
                            DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
                        }
                        // 将数据填入 entriesTemp & entriesHumid
                        entriesTemp.add(new Entry(entriesTemp.size(), tempValue));
                        entriesHumid.add(new Entry(entriesHumid.size(), humidValue));
                        // 创建 DataSet, 传入 entriesTemp & entriesHumid, 并赋标题
                        LineDataSet tempLineDataSet = new LineDataSet(entriesTemp, "温度");
                        LineDataSet humidLineDataSet = new LineDataSet(entriesHumid, "湿度");
                        // 设置线条颜色
                        tempLineDataSet.setColor(Color.RED);
                        humidLineDataSet.setColor(Color.BLUE);
                        LineData dataSet = new LineData(tempLineDataSet, humidLineDataSet); // tip: LineData 可以传入多个 LineDataSet
                        // Light: 在主线程更新图表
                        getActivity().runOnUiThread(() -> {
                            tempAndHumidLineChart.setData(dataSet);
                            tempAndHumidLineChart.invalidate();    // 刷新图表显示
                        });
                    }
                }
                @Override
                public void onError(String errorMessage) {
                }
            });
        }
    };
}