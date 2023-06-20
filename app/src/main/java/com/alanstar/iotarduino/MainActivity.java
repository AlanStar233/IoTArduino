package com.alanstar.iotarduino;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioGroup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.alanstar.iotarduino.Fragments.ConnectionFragment;
import com.alanstar.iotarduino.Fragments.HomeFragment;
import com.alanstar.iotarduino.Fragments.RequestFragment;
import com.alanstar.iotarduino.Fragments.SettingsFragment;
import com.alanstar.iotarduino.utils.MyFragmentPagerAdapter;
import com.alanstar.iotarduino.utils.TopBarController;
import com.qmuiteam.qmui.widget.QMUITopBar;

import java.util.ArrayList;
import java.util.Objects;

public class MainActivity extends AppCompatActivity implements RadioGroup.OnCheckedChangeListener, ViewPager.OnPageChangeListener, HomeFragment.mTopBarStateListener {

    // 组件引入 & 变量定义
    private ViewPager mViewPager;
    public ArrayList<Fragment> fragmentList;
    private RadioGroup bottomBarGroup;

    // Light: Fragments 引入
    public HomeFragment homeFragment;   // 主页
    public ConnectionFragment connectionFragment;   // 数据
    public RequestFragment requestFragment;     // 模拟请求
    public SettingsFragment settingsFragment;   // 设置

    // Light: 初始化一个 TopBarController (原用于TopBar 右上方 state 控制)
    public TopBarController topBarController = new TopBarController();
    // Light: 初始化一个公共变量 mTopBarStatement
    public int mTopBarStatement;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 组件注册
        initComponents();

        // 注册 ViewPager
        initViewPager();

        // RadioGroup 监听
        bottomBarGroup.setOnCheckedChangeListener(this);

    }

    // Light: 沉浸式工具栏
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        getWindow().setStatusBarColor(Color.parseColor("#00AEEC"));
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
    }

    private void initComponents() {
        mViewPager = findViewById(R.id.mViewPager);
        bottomBarGroup = findViewById(R.id.bottomBarRadioGroup);
    }

    private void initViewPager() {
        // Light: 注册 Fragments
        homeFragment = new HomeFragment();
        connectionFragment = new ConnectionFragment();
        requestFragment = new RequestFragment();
        settingsFragment = new SettingsFragment();

        // Light: 在 ArrayList 中加入新增 Fragment
        fragmentList = new ArrayList<>();
        fragmentList.add(0, homeFragment);
        fragmentList.add(1, connectionFragment);
        fragmentList.add(2, requestFragment);
        fragmentList.add(3, settingsFragment);

        // setAdapter
        mViewPager.setAdapter(new MyFragmentPagerAdapter(getSupportFragmentManager(), fragmentList));
        // ViewPager 初始化指向
        mViewPager.setCurrentItem(0);
        // 页面切换监听
        mViewPager.addOnPageChangeListener(this);

    }

    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
        // Light: 获取当前 fragment
        int index = mViewPager.getCurrentItem();
        Fragment fragment = (Fragment) Objects.requireNonNull(mViewPager.getAdapter()).instantiateItem(mViewPager, index);
        QMUITopBar fragmentTopBar = fragment.requireView().findViewById(R.id.mTopBar);

        if (checkedId == R.id.radioHome) {
            mViewPager.setCurrentItem(0, true);
            // TODO: 触发底部导航切换事件时, TopBar 右上角按钮颜色动态更新
        } else if (checkedId == R.id.radioConnection) {
            mViewPager.setCurrentItem(1, true);
        } else if (checkedId == R.id.radioRequest) {
            mViewPager.setCurrentItem(2, true);
        } else if (checkedId == R.id.radioSettings) {
            mViewPager.setCurrentItem(3, true);
        }

    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        switch (position) {
            case 0:
                bottomBarGroup.check(R.id.radioHome);
                break;
            case 1:
                bottomBarGroup.check(R.id.radioConnection);
                break;
            case 2:
                bottomBarGroup.check(R.id.radioRequest);
                break;
            case 3:
                bottomBarGroup.check(R.id.radioSettings);
                break;
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void sendTopBarState(int state) {
        this.mTopBarStatement = state;
    }
}