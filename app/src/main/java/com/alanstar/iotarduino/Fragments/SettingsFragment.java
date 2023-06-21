package com.alanstar.iotarduino.Fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.alanstar.iotarduino.R;
import com.alanstar.iotarduino.utils.GlobalValue;
import com.alanstar.iotarduino.utils.TopBarController;
import com.qmuiteam.qmui.widget.QMUITopBar;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class SettingsFragment extends Fragment implements View.OnClickListener {
    // 引入组件
    QMUITopBar mTopBar;
    EditText et_APIRefreshTime;
    EditText et_ChartsRefreshTime;
    Button btn_SettingsUpdate;

    // 创建变量
    boolean isFirstLoad = true; // 是否为第一次加载 Fragment
    // 创建常量
    public static final String TAG = "SettingsFragment";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        // 注册组件
        initComponents(view);

        // 监听组件

        // Light: 设置 TopBar (在此之前需要清除所有已创建元素)
        TopBarController mTopBarController = new TopBarController();
        mTopBarController.clearTopBar(mTopBar);

        mTopBar.setTitle("设置");

        return view;
    }

    // Light: 注册组件
    private void initComponents(View view) {
        mTopBar = view.findViewById(R.id.mTopBar);
        btn_SettingsUpdate = view.findViewById(R.id.btn_SettingsUpdate);
        et_APIRefreshTime = view.findViewById(R.id.et_APIRefreshTime);
        et_ChartsRefreshTime = view.findViewById(R.id.et_ChartsRefreshTime);
    }

    // Light: Fragment 获得焦点
    @Override
    public void onResume() {
        super.onResume();
        if (isFirstLoad) {
            isFirstLoad = false;
            Thread configRefresherThread = new Thread(configRefresherRunnable);
            configRefresherThread.start();
        } else {
            Thread configRefresherThread = new Thread(configRefresherRunnable);
            configRefresherThread.start();
        }
        btn_SettingsUpdate.setOnClickListener(this);
    }

    // Light: Fragment 失去焦点
    @Override
    public void onPause() {
        super.onPause();
    }

    // Light: config 更新器
    @SuppressLint("SetTextI18n")
    Runnable configRefresherRunnable = () -> {
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
            int apiFreshTime = configJson.getInt("APIFreshTime");
            int chartsFreshTime = configJson.getInt("ChartsFreshTime");

            // 数据更新
            getActivity().runOnUiThread(() -> {
                et_APIRefreshTime.setText(String.valueOf(apiFreshTime));
                et_ChartsRefreshTime.setText(String.valueOf(chartsFreshTime));
            });

        } catch (IOException | JSONException e) {
            Log.e(TAG, "configRefresher error: ", e);
        }
    };

    // Light: config 数值提交器
    Runnable configValueSubmitterRunnable = () -> {
        try {
            File configFile = new File(getActivity().getFilesDir(), GlobalValue.CONFIG_FILE_NAME);

            // 读取配置文件
            FileReader configReader = new FileReader(configFile);
            BufferedReader configBufferedReader = new BufferedReader(configReader);
            StringBuilder configStringBuilder = new StringBuilder();

            String configLine;
            while ((configLine = configBufferedReader.readLine()) != null) {
                configStringBuilder.append(configLine);
            }
            configBufferedReader.close();

            // 将读取到的内容转换为 JSONObject
            JSONObject configJson = new JSONObject(configStringBuilder.toString());

            // 更新数据
            configJson.put("APIFreshTime", Integer.parseInt(et_APIRefreshTime.getText().toString()));
            configJson.put("ChartsFreshTime", Integer.parseInt(et_ChartsRefreshTime.getText().toString()));

            // 以默认的覆盖模式写回文件
            FileWriter configWriter = new FileWriter(configFile, false);
            configWriter.write(configJson.toString());
            configWriter.close();
        } catch (IOException | JSONException e) {
            Log.e(TAG, "configValueSubmitter error: ", e);
        }
    };

    // Light: 按钮点击事件
    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btn_SettingsUpdate) {
            Thread configValueSubmitterThread = new Thread(configValueSubmitterRunnable);
            configValueSubmitterThread.start();
            Toast.makeText(getActivity(), "设置已更新", Toast.LENGTH_SHORT).show();
        }
    }
}