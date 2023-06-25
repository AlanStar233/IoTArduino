package com.alanstar.iotarduino.Fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.alanstar.iotarduino.R;
import com.alanstar.iotarduino.utils.APIPoster;
import com.alanstar.iotarduino.utils.TopBarController;
import com.qmuiteam.qmui.widget.QMUITopBar;

public class RequestFragment extends Fragment implements AdapterView.OnItemSelectedListener, View.OnClickListener {
    // 引入组件
    QMUITopBar mTopBar;
    Spinner spinner_portSelection;
    TextView tv_param_1;
    TextView tv_param_2;
    EditText et_param_1;
    EditText et_param_2;
    Button btn_sendRequest;
    TextView tv_ReqResults;

    // 创建变量
    boolean isFirstLoad = true; // 是否为第一次加载 Fragment
    private int itemPosition = 0; // 选中的 Spinner 位置

    // Light: 定义 TAG
    public static final String TAG = "RequestFragment";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_request, container, false);

        // 注册组件
        initComponents(view);

        // 监听组件
        // 初始化一个 ArrayAdapter 供 spinner_portSelection 使用, 与 res/values/arrays.xml 中的 spinner_array 绑定
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(), R.array.spinner_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_portSelection.setAdapter(adapter);
        spinner_portSelection.setOnItemSelectedListener(this);
        btn_sendRequest.setOnClickListener(this);

        // Light: 设置 TopBar (在此之前需要清除所有已创建元素)
        TopBarController mTopBarController = new TopBarController();
        mTopBarController.clearTopBar(mTopBar);

        mTopBar.setTitle("模拟请求");

        return view;
    }

    // Light: 注册组件
    private void initComponents(View view) {
        mTopBar = view.findViewById(R.id.mTopBar);
        spinner_portSelection = view.findViewById(R.id.spinner_portSelection);
        tv_param_1 = view.findViewById(R.id.tv_param_1);
        tv_param_2 = view.findViewById(R.id.tv_param_2);
        et_param_1 = view.findViewById(R.id.et_param_1);
        et_param_2 = view.findViewById(R.id.et_param_2);
        btn_sendRequest = view.findViewById(R.id.btn_sendRequest);
        tv_ReqResults = view.findViewById(R.id.tv_ReqResults);
    }

    // Light: Spinner 监听器
    @SuppressLint("SetTextI18n")
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//        String selectedValue = parent.getItemAtPosition(position).toString();
        itemPosition = position;    // 在 class 中保存 position 的位置
        // Toast.makeText(getActivity(), "Selected: " + position + " " + selectedItem + " " + selectedValue, Toast.LENGTH_SHORT).show();
        tv_param_1.setText("请求参数1");
        tv_param_2.setText("请求参数2");
        tv_param_1.setVisibility(View.VISIBLE);
        tv_param_2.setVisibility(View.VISIBLE);
        et_param_1.setVisibility(View.VISIBLE);
        et_param_2.setVisibility(View.VISIBLE);
        switch (position) {
            case 0:
                // v1/setLockState?lockState={}
                tv_param_1.setText("门锁状态 (lockState)");
                tv_param_2.setVisibility(View.GONE);
                et_param_2.setVisibility(View.GONE);
                break;
            case 1:
                // v1/setLightState?light={}
                tv_param_1.setText("灯光状态 (light)");
                tv_param_2.setVisibility(View.GONE);
                et_param_2.setVisibility(View.GONE);
                break;
            case 2:
                // v1/setTempAndHumidState/?temp={}&humid={}
                tv_param_1.setText("温度 (temp)");
                tv_param_2.setText("湿度 (humid)");
                et_param_1.setVisibility(View.VISIBLE);
                et_param_2.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    // Light: Button 监听器
    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btn_sendRequest) {
            new Thread(() -> {
                // 根据当前的 position 发送请求
                // Light: v1/setLockState?lockState={}
                if (itemPosition == 0) {
                    String lockState = et_param_1.getText().toString();
                    final String response = new APIPoster().setLockState(Integer.parseInt(lockState));
                    getActivity().runOnUiThread(() -> tv_ReqResults.setText(response));
                } else if (itemPosition == 1) {
                    String lightState = et_param_1.getText().toString();
                    final String response = new APIPoster().setLightState(Integer.parseInt(lightState));
                    getActivity().runOnUiThread(() -> tv_ReqResults.setText(response));
                } else if (itemPosition == 2) {
                    String temp = et_param_1.getText().toString();
                    String humid = et_param_2.getText().toString();
                    final String response = new APIPoster().setTempAndHumidState(Integer.parseInt(temp), Integer.parseInt(humid));
                    getActivity().runOnUiThread(() -> tv_ReqResults.setText(response));
                }
            }).start();
        }
    }

}