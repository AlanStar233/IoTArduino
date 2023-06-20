package com.alanstar.iotarduino.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.alanstar.iotarduino.R;
import com.alanstar.iotarduino.utils.TopBarController;
import com.qmuiteam.qmui.widget.QMUITopBar;

public class SettingsFragment extends Fragment {
    // 引入组件
    QMUITopBar mTopBar;

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
    }
}