package com.example.registerandlogin;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    private  Button loginButton;
    private Handler handler; // 声明一个Handler对象
    private String result = "";	//声明一个代表显示内容的字符串
    private EditText  usernameText;
    String username;
    String password;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        loginButton=findViewById(R.id.loginButton);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                usernameText=findViewById(R.id.username);
                EditText passwordText=findViewById(R.id.password);
                username=usernameText.getText().toString();
                password=passwordText.getText().toString();
                Toast.makeText(getApplicationContext(), "输入"+username+" "+password, Toast.LENGTH_SHORT).show();
                // 创建一个新线程，用于发送并读取微博信息
                new Thread(new Runnable() {
                    public void run() {
                        send();	//发送文本内容到Web服务器
                        Message m = handler.obtainMessage(); // 获取一个Message
               //         Toast.makeText(getApplicationContext(), "message"+m, Toast.LENGTH_SHORT).show();
                        handler.sendMessage(m); // 发送消息
                    }
                }).start(); // 开启线程



//                if(username.equals("jackhe") && password.equals("123")){
//                    Toast.makeText(getApplicationContext(), "登录成功", Toast.LENGTH_SHORT).show();//提示用户登陆成功
//                    Intent intent=new Intent();
//                    intent.putExtra("USERNAME",username);//就像存储键值对一样
//                    intent.putExtra("PASSWORD",password);
//                    intent.setClass(MainActivity.this,LoginSuccessActivity.class);//指定传递对象
//                    startActivity(intent);//将intent传递给activity
//
//                }else {
//                    Toast.makeText(getApplicationContext(), "用户名或密码错误", Toast.LENGTH_SHORT).show();
//                }
            }
        });

        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (result != null) {

                    Gson gson=new Gson();
                    Msg message=gson.fromJson(result,Msg.class);
                    if(message.code==100){
                        Toast.makeText(getApplicationContext(), "登录成功，进行跳转", Toast.LENGTH_SHORT).show();
                        Intent intent=new Intent();
                        intent.setClass(MainActivity.this,LoginSuccessActivity.class);//指定传递对象
                        startActivity(intent);//将intent传递给activity
                    }else{
                        usernameText.setText("error number");
                    }

                }
                super.handleMessage(msg);
            }
        };
    }

    public void send() {
        String target="";
        target = "http://175.10.104.21/login?username="+username.trim()+"&password="+password.trim();	//要访问的URL地址
        URL url;
        try {
            url = new URL(target);
            HttpURLConnection urlConn = (HttpURLConnection) url
                    .openConnection();	//创建一个HTTP连接
            InputStreamReader in = new InputStreamReader(
                    urlConn.getInputStream()); // 获得读取的内容
            BufferedReader buffer = new BufferedReader(in); // 获取输入流对象
            String inputLine = null;
            //通过循环逐行读取输入流中的内容
            while ((inputLine = buffer.readLine()) != null) {
                result += inputLine + "\n";
            }
            in.close();	//关闭字符输入流对象
            urlConn.disconnect();	//断开连接
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
