package com.example.singheart.activity;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.ashokvarma.bottomnavigation.BottomNavigationBar;
import com.ashokvarma.bottomnavigation.BottomNavigationItem;
import com.example.singheart.fragment.CourseFragment;
import com.example.singheart.fragment.HomeFragment;
import com.example.singheart.fragment.IpgwInfoFragment;
import com.example.singheart.fragment.MineFragment;
import com.example.singheart.R;

public class MainActivity extends AppCompatActivity implements BottomNavigationBar.OnTabSelectedListener {
    private BottomNavigationBar mBottomNavigationBar = null;
    private CourseFragment mCourseFragment = null;
    private HomeFragment mHomeFragment = null;
    private IpgwInfoFragment mIpgwInfoFragment = null;
    private MineFragment mMineFragment = null;
    long mExitTime = 0;
    private int resIdFlag = 0;
    private int resIdInit = 0;

    //连续双击两次退出
    @Override
    public void onBackPressed() {
        if ((System.currentTimeMillis() - mExitTime) > 2000) {
            Toast.makeText(this, "再按一次退出", Toast.LENGTH_SHORT).show();
            mExitTime = System.currentTimeMillis();
        } else {
            finish();
            System.exit(0);
        }
    }

    @Override
    protected void onCreate(Bundle paramBundle) {
        super.onCreate(paramBundle);
        setContentView(R.layout.activity_main);
        bindViews();
        mBottomNavigationBar.addItem(new BottomNavigationItem(R.drawable.tab_home, "今天"))
                .addItem(new BottomNavigationItem(R.drawable.tab_campus, "课程表"))
                .addItem(new BottomNavigationItem(R.drawable.tab_me, "我"))
                .setActiveColor("#00DFFF")
                .setFirstSelectedPosition(1)
                .initialise();
        createDefaultFragment();
        mBottomNavigationBar.setTabSelectedListener(this);
    }

    public void bindViews() {
        //读取用户之前的课程背景选择
        SharedPreferences preferences = getSharedPreferences("LoginState", MODE_PRIVATE);
        resIdFlag = resIdInit = preferences.getInt("resIdInit", 0);
        mCourseFragment = CourseFragment.newInstance(resIdInit);
        mBottomNavigationBar = (BottomNavigationBar) findViewById(R.id.bottom_navigation_bar);
    }

    public void createDefaultFragment() {
        FragmentManager fm = getFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        transaction.add(R.id.fragment, mCourseFragment);
        transaction.show(mCourseFragment).commit();
    }

    @Override
    public void onTabReselected(int paramInt) {

    }

    //隐藏所有Fragment
    public void hideAllFragment(FragmentTransaction fragmentTransaction) {
        if (mIpgwInfoFragment != null) fragmentTransaction.hide(mIpgwInfoFragment);
        if (mHomeFragment != null) fragmentTransaction.hide(mHomeFragment);
        if (mCourseFragment != null) fragmentTransaction.hide(mCourseFragment);
        if (mMineFragment != null) fragmentTransaction.hide(mMineFragment);
    }

    @Override
    public void onTabSelected(int position) {
        FragmentManager fm = getFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        hideAllFragment(transaction);
        switch (position) {
            case 0:
                if (mHomeFragment == null) {
                    mHomeFragment = new HomeFragment();
                    transaction.add(R.id.fragment, mHomeFragment);
                }
                transaction.show(mHomeFragment).commit();
                break;
            case 1:
                if (mCourseFragment == null || resIdFlag != resIdInit) {
                    resIdInit = resIdFlag;
                    mCourseFragment = CourseFragment.newInstance(resIdFlag);
                    transaction.add(R.id.fragment, mCourseFragment);
                }
                transaction.show(mCourseFragment).commit();
                break;
            case 2:
                if (mMineFragment == null) {
                    mMineFragment = new MineFragment();
                    transaction.add(R.id.fragment, mMineFragment);
                }
                transaction.show(mMineFragment).commit();
                break;
        }
    }

    @Override
    public void onTabUnselected(int paramInt) {

    }

    //后一个Activity传递给前一个Activity
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0x123 && resultCode == 0x123) {
            //从setBgActivity中获取用户选择的背景ID
            Bundle bd = data.getExtras();
            resIdFlag = bd.getInt("resIdFlag");
            Toast.makeText(MainActivity.this, "修改课程表背景成功!", Toast.LENGTH_SHORT).show();
            //保存到本地，记住用户的选择
            SharedPreferences preferences = getSharedPreferences("LoginState", MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putInt("resIdInit", resIdFlag);
            editor.commit();
        }
    }

    public void createIpgwFrag(Bundle bundle){
        FragmentManager fm = getFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        hideAllFragment(transaction);
        if (mIpgwInfoFragment == null){
            mIpgwInfoFragment = IpgwInfoFragment.newInstance(bundle);
            transaction.add(R.id.fragment,mIpgwInfoFragment);
        }
        transaction.show(mIpgwInfoFragment);
        transaction.commit();
    }

    public void createIpgwInit(){
        FragmentManager fm = getFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        hideAllFragment(transaction);
        if (mHomeFragment == null){
            mHomeFragment = new HomeFragment();
            transaction.add(R.id.fragment,mHomeFragment);
        }
        transaction.show(mHomeFragment);
        transaction.commit();
    }
}