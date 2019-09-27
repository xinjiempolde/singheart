package com.example.singheart.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.example.singheart.R;
import com.example.singheart.Util.RegexUtil;
import com.example.singheart.Util.handleDateUtil;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    private OkHttpClient mClient = null;
    private final String userAgent = "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/55.0.2883.87 Safari/537.36";
    private String stuID = "";
    private String stuPass = "";
    private String JSESSIONID = "";
    private String GSESSIONID = "";
    private String SERVERNAME = "";
    private Button btn_login;
    private CheckBox check_remerber;
    private EditText edit_pwd;
    private EditText edit_user;
    private boolean isRemember = false; //是否记住账号
    private boolean isLogin = false;  //是否已经登陆
    private SharedPreferences mSharedPreferences; //保存登陆状态到本地文件
    private long mExitTime = 0;
    private int mState = 0; //获取网络数据的状态，取值0，1，2，即获取课表和获取用户信息的状态;
    private Context mContext = LoginActivity.this;
    private ProgressDialog mProgressDialog;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 0x001:
                    mProgressDialog.dismiss();
                    Toast.makeText(mContext,"您似乎没有连接到网络",Toast.LENGTH_SHORT).show();
                    break;
                case 0x002:
                    mProgressDialog.dismiss();
                    Toast.makeText(mContext,"首次使用请连接校园网",Toast.LENGTH_SHORT).show();
                    break;
                case 0x003:
                    //获取课表和个人信息同时运行，同时获取到才登陆
                    if (mState == 1){
                        mProgressDialog.dismiss();
                        Toast.makeText(mContext,"登陆成功",Toast.LENGTH_SHORT).show();
                        saveLoginDate();
                        startActivity(new Intent(mContext,MainActivity.class));
                    } else {
                        mState++;
                    }
                    break;
                case 0x004:
                    mProgressDialog.dismiss();
                    Toast.makeText(mContext,"请重试",Toast.LENGTH_SHORT).show();
                    break;
                case 0x005:
                    mProgressDialog.dismiss();
                    Toast.makeText(mContext,"账户或密码错误！",Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_login);
        bindViews();
        initViews();

        //如何已经登陆过了，则直接进入主页面
        if (isLogin) {
            startActivity(new Intent(mContext, MainActivity.class));
            finish();
        }

    }

    public void bindViews() {
        edit_user = (EditText) findViewById(R.id.edit_user);
        edit_pwd = ((EditText) findViewById(R.id.edit_pwd));
        check_remerber = (CheckBox) findViewById(R.id.check_remember);
        btn_login = ((Button) findViewById(R.id.btn_login));
    }

    public void initViews() {
        mSharedPreferences = getSharedPreferences("LoginState", MODE_PRIVATE);
        isLogin = mSharedPreferences.getBoolean("isLogin", false);
        isRemember = mSharedPreferences.getBoolean("isRemember", false);
        mSharedPreferences = getSharedPreferences("LoginState", MODE_PRIVATE);
        String base64_user = mSharedPreferences.getString("user", "");
        String user = new String(Base64.decode(base64_user.getBytes(),Base64.DEFAULT));
        edit_user.setText(user);
        if (isRemember) {
            String base64_pwd = mSharedPreferences.getString("pwd", "");
            String pwd = new String(Base64.decode(base64_pwd.getBytes(),Base64.DEFAULT));
            edit_pwd.setText(pwd);
        }
        check_remerber.setChecked(isRemember);
        btn_login.setOnClickListener(this);
    }

    @Override
    public void onClick(View paramView) {
        stuID = edit_user.getText().toString();
        stuPass = edit_pwd.getText().toString();
        if (stuID.equals("") || stuPass.equals("")){
            Toast.makeText(mContext,"账号或密码不能为空！",Toast.LENGTH_SHORT).show();
            return;
        } else if (stuID.length() != 8){
            Toast.makeText(mContext,"账号长度不足八位！",Toast.LENGTH_SHORT).show();
            return;
        } else {
            mProgressDialog = new ProgressDialog(mContext);
            mProgressDialog.setTitle("登陆中");
            mProgressDialog.setMessage("正在登陆中，请稍后···\n首次加载可能耗时较长，请耐心等待");
            mProgressDialog.setCancelable(false);
            mProgressDialog.show();
            startSpider();
        }

    }

    //将账户信息保存到本地
    public void saveLoginDate() {
        mSharedPreferences = getSharedPreferences("LoginState", MODE_PRIVATE);
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        String user = edit_user.getText().toString();
        String base64_user = Base64.encodeToString(user.getBytes(),Base64.DEFAULT);
        editor.putString("user", base64_user);
        isRemember = check_remerber.isChecked();
        if (isRemember) {
            String pwd = edit_pwd.getText().toString();
            String base64_pwd = Base64.encodeToString(pwd.getBytes(),Base64.DEFAULT);
            editor.putString("pwd",base64_pwd);
            editor.putBoolean("isRemember", true);
        } else {
            editor.putBoolean("isRemember", false);
        }
        editor.putBoolean("isLogin", true);
        editor.commit();
    }

    //开始模拟登陆教务处获取信息
    public void startSpider(){
        final HashMap<String, List<Cookie>> cookieStore = new HashMap<>();
        mClient = new OkHttpClient.Builder()
                .retryOnConnectionFailure(true)
                .cookieJar(new CookieJar() {
                    @Override
                    public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
                        Log.e("url",url.toString());
                        for (Cookie cookie : cookies){
                            if (cookie.name().equals("JSESSIONID")){
                                JSESSIONID = cookie.value();
                            }
                            if (cookie.name().equals("GSESSIONID")){
                                GSESSIONID = cookie.value();
                            }
                            if (cookie.name().equals("SERVERNAME")){
                                SERVERNAME = cookie.value();
                            }
                        }
                        cookieStore.put(url.host(), cookies);
                    }
                    @Override
                    public List<Cookie> loadForRequest(HttpUrl url) {
                        List<Cookie> cookies = cookieStore.get(url.host());
                        return cookies != null ? cookies : new ArrayList<Cookie>();
                    }
                }).build();

        final Request get_request = new Request.Builder()
                .url("https://pass.neu.edu.cn/tpass/login?service=http%3A%2F%2F219.216.96.4%2Feams%2FhomeExt.action")
                .addHeader("User_Agent",userAgent)
                .addHeader("Connection","close")
                .get()
                .build();
        Call get_call = mClient.newCall(get_request);
        get_call.enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                //如果失败了，检查问题原因:未连接校园网或没有连接网络
                final Request baidu_request = new Request.Builder()
                        .url("https://www.baidu.com")
                        .get()
                        .build();
                mClient.newCall(baidu_request).enqueue(new Callback() {
                    @Override
                    public void onFailure(@NotNull Call call, @NotNull IOException e) {
                        mHandler.sendEmptyMessage(0x001);
                        return;
                    }

                    @Override
                    public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                        mHandler.sendEmptyMessage(0x002);
                        return;
                    }
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String firstPage = response.body().string();
                //开始构造表单数据
                final String lt = RegexUtil.getUniqueResulte("<input type=\"hidden\" id=\"lt\" name=\"lt\" value=\"(.*?)\" />",firstPage,1);
                final String execution = RegexUtil.getUniqueResulte("<input type=\"hidden\" name=\"execution\" value=\"(.*?)\" />",firstPage,1);
                final String rsa = stuID+stuPass+lt;
                final String ul = String.valueOf(stuID.length());
                final String pl = String.valueOf(stuPass.length());
                final String _eventId = "submit";
                RequestBody postDate = new FormBody.Builder()
                        .add("rsa",rsa)
                        .add("ul",ul)
                        .add("pl",pl)
                        .add("lt",lt)
                        .add("execution",execution)
                        .add("_eventId",_eventId)
                        .build();
                final Request post_request = new Request.Builder()
                        .url("https://pass.neu.edu.cn/tpass/login?service=http%3A%2F%2F219.216.96.4%2Feams%2FhomeExt.action")
                        .addHeader("User-Agent",userAgent)
                        .addHeader("Connection","close")
                        .post(postDate)
                        .build();
                Call post_call = mClient.newCall(post_request);
                post_call.enqueue(new Callback() {
                    @Override
                    public void onFailure(@NotNull Call call, @NotNull IOException e) {
                        mHandler.sendEmptyMessage(0x002);
                        return;
                    }

                    @Override
                    public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                        if (JSESSIONID.equals("") || GSESSIONID.equals("")){
                            mHandler.sendEmptyMessage(0x005);
                            return;
                        }
                        System.out.println(response.body().string());
                        get_userinfo_from_aao();
                        get_course_from_aao();
                    }
                });
            }
        });
    }

    private void get_userinfo_from_aao(){
        final String myCookie = "JSESSIONID=" + JSESSIONID + "; " + "SERVERNAME=" + SERVERNAME + "; " + "GSESSIONID=" + GSESSIONID;
        Log.e("cookie",myCookie);
        Request user_request = new Request.Builder()
                .url("http://219.216.96.4/eams/stdDetail.action?")
                .addHeader("User-Agent",userAgent)
                .addHeader("Cookie",myCookie)
                .get()
                .build();
        final OkHttpClient new_client = new OkHttpClient();
        final Call user_call = new_client.newCall(user_request);
        //加入线程是为了延时执行，防止点击过快导致爬取数据失败
        new Thread() {
            @Override
            public void run() {
                super.run();
                try {
                    Thread.sleep(3000);//休眠3秒
                    user_call.enqueue(new Callback() {
                        @Override
                        public void onFailure(@NotNull Call call, @NotNull IOException e) {
                            Log.e("失败","获取课表失败");
                        }

                        @Override
                        public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                            String pageDate = response.body().string();
                            System.out.println(pageDate);
                            boolean state = handleDateUtil.handleUserDate(pageDate,mContext);
                            if (state){
                                mHandler.sendEmptyMessage(0x003);
                            }else {
                                mHandler.sendEmptyMessage(0x004);
                            }

                        }
                    });
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    //从教务处获取课表
    private void get_course_from_aao() throws IOException {
        final String myCookie = "JSESSIONID=" + JSESSIONID + "; " + "SERVERNAME=" + SERVERNAME + "; " + "GSESSIONID=" + GSESSIONID;
        Request ids_request = new Request.Builder()
                .url("http://219.216.96.4/eams/courseTableForStd.action")
                .addHeader("User-Agent",userAgent)
                .addHeader("Cookie",myCookie)
                .get()
                .build();
        OkHttpClient ids_client = new OkHttpClient();
        Call ids_call = ids_client.newCall(ids_request);
        Response ids_reponse = ids_call.execute();
        String ids_page = ids_reponse.body().string();
        ArrayList<String> ids_list = RegexUtil.getComplexResulte("(?<=\"ids\",\").*?(?=\")",ids_page);
        String ids = ids_list.get(0);
        RequestBody course_date = new FormBody.Builder()
                .add("ignoreHead","1")
                .add("showPrintAndExport","1")
                .add("setting.kind","std")
                .add("startWeek","")
                .add("semester.id","12")
                .add("ids",ids)
                .build();
        Request course_request = new Request.Builder()
                .url("http://219.216.96.4/eams/courseTableForStd!courseTable.action")
                .addHeader("User-Agent",userAgent)
                .addHeader("Cookie",myCookie)
                .post(course_date)
                .build();
        final OkHttpClient course_client = new OkHttpClient();
        final Call course_call = course_client.newCall(course_request);
        new Thread() {
            @Override
            public void run() {
                super.run();
                try {
                    Thread.sleep(2000);//休眠2秒
                    course_call.enqueue(new Callback() {
                        @Override
                        public void onFailure(@NotNull Call call, @NotNull IOException e) {
                            Log.e("失败","获取课表失败");
                        }

                        @Override
                        public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                            String course_page = response.body().string();
                            System.out.println(course_page);
                            mHandler.sendEmptyMessage(0x003);
                            handleDateUtil.handleCourseDate(course_page,mContext);
                        }
                    });
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }


}