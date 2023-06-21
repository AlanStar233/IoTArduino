package com.alanstar.iotarduino.Fragments;

import static com.alanstar.iotarduino.utils.GlobalValue.API_ADDRESS;
import static com.alanstar.iotarduino.utils.GlobalValue.GET_LIGHT_STATE;
import static com.alanstar.iotarduino.utils.GlobalValue.GET_LOCK_STATE;
import static com.alanstar.iotarduino.utils.GlobalValue.GET_TEMP_AND_HUMID_STATE;
import static com.alanstar.iotarduino.utils.GlobalValue.SET_LIGHT_STATE;
import static com.alanstar.iotarduino.utils.GlobalValue.SET_LOCK_STATE;
import static com.alanstar.iotarduino.utils.GlobalValue.SET_TEMP_AND_HUMID_STATE;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.alanstar.iotarduino.R;
import com.alanstar.iotarduino.utils.APILiveTester;
import com.alanstar.iotarduino.utils.GlobalValue;
import com.alanstar.iotarduino.utils.TopBarController;
import com.qmuiteam.qmui.widget.QMUITopBar;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

public class HomeFragment extends Fragment {

    // 引入组件
    QMUITopBar mTopBar;
    TextView APIDelayTimer;
    TextView setLockStateCode;
    TextView setLockStateLatency;
    TextView setLightStateCode;
    TextView setLightStateLatency;
    TextView setTempAndHumidStateCode;
    TextView setTempAndHumidStateLatency;
    TextView getLockStateCode;
    TextView getLockStateLatency;
    TextView getLightStateCode;
    TextView getLightStateLatency;
    TextView getTempAndHumidStateCode;
    TextView getTempAndHumidStateLatency;

    // 创建变量
    boolean isFirstLoad = true; // 是否为第一次加载 Fragment
    int nowTimerCount = 0;  // 当前计时时长
    int targetTimerCount = 0;   // 目标计时时长 (与 settings 同步)
    Timer mTimer;
    TimerTask mTimerTask;
    APILiveTester mAPILiveTester = new APILiveTester();

    // 创建常量
    public static final String TAG = "HomeFragment";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        // get View
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // 注册组件
        initComponents(view);

        // Light: 初始化设置 TopBar
        TopBarController mTopBarController = new TopBarController();
        mTopBarController.clearTopBar(mTopBar);

        // 设置标题水平垂直居中
        mTopBar.setTitle("主页");

        return view;
    }

    // Light: 视图创建完毕
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // 调用 APIRefreshTime 数值更新器
        Thread apiRefreshTimeThread = new Thread(apiRefreshTimeRunnable);
        apiRefreshTimeThread.start();
    }

    // Light: 注册组件
    private void initComponents(View view) {
        mTopBar = view.findViewById(R.id.mTopBar);
        APIDelayTimer = view.findViewById(R.id.APIDelayTimer);

        setLockStateCode = view.findViewById(R.id.setLockStateCode);
        setLockStateLatency = view.findViewById(R.id.setLockStateLatency);
        setLightStateCode = view.findViewById(R.id.setLightStateCode);
        setLightStateLatency = view.findViewById(R.id.setLightStateLatency);
        setTempAndHumidStateCode = view.findViewById(R.id.setTempAndHumidStateCode);
        setTempAndHumidStateLatency = view.findViewById(R.id.setTempAndHumidStateLatency);
        getLockStateCode = view.findViewById(R.id.getLockStateCode);
        getLockStateLatency = view.findViewById(R.id.getLockStateLatency);
        getLightStateCode = view.findViewById(R.id.getLightStateCode);
        getLightStateLatency = view.findViewById(R.id.getLightStateLatency);
        getTempAndHumidStateCode = view.findViewById(R.id.getTempAndHumidStateCode);
        getTempAndHumidStateLatency = view.findViewById(R.id.getTempAndHumidStateLatency);
    }

    // Light: Fragment 获得焦点
    @Override
    public void onResume() {
        super.onResume();
        // 调用一遍 APIRefreshTime 数值更新器
        Thread apiRefreshTimeThread = new Thread(apiRefreshTimeRunnable);
        apiRefreshTimeThread.start();
        if (isFirstLoad) {
            Thread apiThread = new Thread(apiFresherRunnable);
            apiThread.start();
        }
        startTimer();
    }

    // Light: Fragment 失去焦点
    @Override
    public void onPause() {
        super.onPause();
        stopTimer();
    }

    // Light: API 监控延时循环逻辑
    public void startTimer() {
        mTimer = new Timer();
        mTimerTask = new TimerTask() {
            @SuppressLint("SetTextI18n")
            @Override
            public void run() {
                // 检测 nowTimerCount 是否与设置相同, 相同则重置计时
                if (nowTimerCount != targetTimerCount) {
                    nowTimerCount += 1;
                } else {
                    nowTimerCount = 0;

                    // 在新线程中执行网络请求
                    Thread apiThread = new Thread(apiFresherRunnable);
                    apiThread.start();
                }
                // 跳回主线程更新 UI
                getActivity().runOnUiThread(() -> APIDelayTimer.setText(nowTimerCount + "/" + targetTimerCount));
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

    // Light: 线程方式执行 API Fresher 统一刷新数值
    Runnable apiFresherRunnable = new Runnable() {
        @SuppressLint("SetTextI18n")
        @Override
        public void run() {
            setLockStateCode.setText(mAPILiveTester.APIHttpsCode(API_ADDRESS + SET_LOCK_STATE, "POST"));
            setLockStateLatency.setText(mAPILiveTester.APILatency(API_ADDRESS + SET_LOCK_STATE, "POST") + "ms");

            getLockStateCode.setText(mAPILiveTester.APIHttpsCode(API_ADDRESS + GET_LOCK_STATE, "GET"));
            getLockStateLatency.setText(mAPILiveTester.APILatency(API_ADDRESS + GET_LOCK_STATE, "GET") + "ms");

            setLightStateCode.setText(mAPILiveTester.APIHttpsCode(API_ADDRESS + SET_LIGHT_STATE, "POST"));
            setLightStateLatency.setText(mAPILiveTester.APILatency(API_ADDRESS + SET_LIGHT_STATE, "POST") + "ms");

            getLightStateCode.setText(mAPILiveTester.APIHttpsCode(API_ADDRESS + GET_LIGHT_STATE, "GET"));
            getLightStateLatency.setText(mAPILiveTester.APILatency(API_ADDRESS + GET_LIGHT_STATE, "GET") + "ms");

            setTempAndHumidStateCode.setText(mAPILiveTester.APIHttpsCode(API_ADDRESS + SET_TEMP_AND_HUMID_STATE, "POST"));
            setTempAndHumidStateLatency.setText(mAPILiveTester.APILatency(API_ADDRESS + SET_TEMP_AND_HUMID_STATE, "POST") + "ms");

            getTempAndHumidStateCode.setText(mAPILiveTester.APIHttpsCode(API_ADDRESS + GET_TEMP_AND_HUMID_STATE, "GET"));
            getTempAndHumidStateLatency.setText(mAPILiveTester.APILatency(API_ADDRESS + GET_TEMP_AND_HUMID_STATE, "GET") + "ms");
        }
    };

    // Light: APIRefreshTime 数值更新器
    Runnable apiRefreshTimeRunnable = () -> {
        try {
            File configFile = new File(getActivity().getFilesDir(), GlobalValue.CONFIG_FILE_NAME);

            FileReader configReader = new FileReader(configFile);
            BufferedReader configBufferedReader = new BufferedReader(configReader);

            StringBuilder configStringBuilder = new StringBuilder();
            String configLine;
            while ((configLine = configBufferedReader.readLine()) != null) {
                configStringBuilder.append(configLine);
            }
            configBufferedReader.close();

            // 数据提取
            JSONObject configJson = new JSONObject(configStringBuilder.toString());
            targetTimerCount = configJson.getInt("APIFreshTime");
        } catch (IOException | JSONException e) {
            Log.e(TAG, "APIRefreshTime: ", e);
        }
    };

}