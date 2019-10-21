package com.mei.tododemo.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

public class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /**
     * 跳转，带消息，回调
     * @param aClass 当前activity
     * @param bundle 传入的消息
     * @param requestCode 返回标识
     */
    public void openActivity(Class<?> aClass,Bundle bundle,int requestCode){

        Intent intent = new Intent(this,aClass);
        if (bundle!=null){
            intent.putExtras(bundle);
        }
        if (requestCode!=0) {
            startActivityForResult(intent,requestCode);
        }else {
            startActivity(intent);
        }

    }

    /**
     *跳转，带消息
     * @param aClass 当前activity
     * @param bundle 传入的消息
     */
    public void openActivity(Class<?> aClass,Bundle bundle){

        openActivity(aClass,bundle,0);
    }

    /**
     * 跳转
     * @param aClass
     */
    public void openActivity(Class<?> aClass){
        openActivity(aClass,null,0);

    }

}
