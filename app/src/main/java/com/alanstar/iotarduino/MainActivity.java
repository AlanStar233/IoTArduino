package com.alanstar.iotarduino;

import static com.alanstar.iotarduino.utils.GlobalValue.CONFIG_FILE_NAME;
import static com.alanstar.iotarduino.utils.GlobalValue.DEFAULT_API_FRESH_TIME;
import static com.alanstar.iotarduino.utils.GlobalValue.DEFAULT_CHARTS_FRESH_TIME;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.Toast;

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

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

public class MainActivity extends AppCompatActivity implements RadioGroup.OnCheckedChangeListener, ViewPager.OnPageChangeListener {

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
    // Light: 公有 init
    public static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 组件注册
        initComponents();

        // 注册 ViewPager
        initViewPager();

        // config 初始化
        initConfigFile();

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

    // Light: Settings 配置文件检测
    public void initConfigFile() {
        File config = new File(getFilesDir(), CONFIG_FILE_NAME);
        if (!config.exists()) {
            try {
                Toast.makeText(MainActivity.this, "配置文件不存在, 正在创建...", Toast.LENGTH_SHORT).show();
                File configFile = new File(getFilesDir(), CONFIG_FILE_NAME);
                // 对配置文件写入配置
                JSONObject configJson = new JSONObject();
                configJson.put("APIFreshTime", DEFAULT_API_FRESH_TIME);
                configJson.put("ChartsFreshTime", DEFAULT_CHARTS_FRESH_TIME);
                // 配置写入文件
                FileWriter configWriter = new FileWriter(configFile);
                configWriter.write(configJson.toString());
                configWriter.close();
            } catch (IOException | JSONException e) {
                Log.e(TAG, "initConfigFile error: ", e);
            }
        }
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

}