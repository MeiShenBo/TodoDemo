package com.mei.tododemo.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.mei.tododemo.R;
import com.mei.tododemo.commom.Api;
import com.mei.tododemo.model.TodoBean;
import com.mei.tododemo.presenter.SQLPersenterImpl;
import com.mei.tododemo.tools.Tools;
import com.mei.tododemo.view.SQLView;

import java.util.List;

public class AddToDoActivity extends BaseActivity implements SQLView{
    private String TAG ="AddToDoActivity";
    private Toolbar addToolbar;
    private EditText addTitle;
    private EditText addContent;
    private SQLPersenterImpl sqlPersenter;
    private Bundle bundle = null;
    private final static int ADD=1;
    private final static int EDITOR=0;
    private TodoBean todoBean = null;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_to_do);
        initView();
        init();

    }

    private void init() {
        bundle = getIntent().getExtras();
        if (bundle !=null){
            addToolbar.setTitle("EDITOR TODO");
            String title = bundle.getString("title");
            String content = bundle.getString("content");
            String utime = bundle.getString("utime");
            String ctime = bundle.getString("ctime");
            int id = bundle.getInt("id");
            todoBean = new TodoBean(id,title,content,ctime,utime);
            addTitle.setText(title);
            addContent.setText(content);
            addTitle.setFocusable(false);
            addTitle.setFocusableInTouchMode(false);
            addContent.setFocusable(false);
            addContent.setFocusableInTouchMode(false);
        }
        sqlPersenter = new SQLPersenterImpl(this, this);
    }

    private void initView() {
        addToolbar = (Toolbar) findViewById(R.id.add_toolbar);
        addTitle = (EditText) findViewById(R.id.add_title);
        addContent = (EditText) findViewById(R.id.add_content);
        setSupportActionBar(addToolbar);
        addToolbar.setOnMenuItemClickListener(onMenuItemClickListener);
        addToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (bundle!=null){
            getMenuInflater().inflate(R.menu.edit_menu,menu);
        }else {
            getMenuInflater().inflate(R.menu.add_menu,menu);
        }

        return super.onCreateOptionsMenu(menu);
    }

    Toolbar.OnMenuItemClickListener onMenuItemClickListener = new Toolbar.OnMenuItemClickListener() {
        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.add_submit:
                        saveTodo("确定保存么?",ADD);
                        break;
                    case R.id.editor_manage:
                        Log.d(TAG, "onMenuItemClick: 编辑");
                        addTitle.setFocusable(true);
                        addTitle.setFocusableInTouchMode(true);
                        addContent.setFocusable(true);
                        addContent.setFocusableInTouchMode(true);
                        break;
                    case R.id.editor_save:
                        if (addTitle.isFocusable()) {
                            saveTodo("确定保存么?",EDITOR);
                        }
                        break;
                }

            return true;
        }
    };

    @Override
    public void refreshUI(List<Object> todoBeans) {

    }

    @Override
    public void showError(String msg) {
        Log.d(TAG, "showError: "+msg);
    }

    private boolean addTodo(){
        String title = addTitle.getText().toString().trim();
        String content = addContent.getText().toString().trim();
        String date = Tools.currentDate();
        if (title==null){
            Toast.makeText(this,"请填写标题!",Toast.LENGTH_SHORT).show();
            return false;
        }

        boolean b = sqlPersenter.addTodo(new TodoBean(title, content, date, date));
        if (!b) {
            Toast.makeText(this,"保存失败!",Toast.LENGTH_SHORT).show();
        }else {
            Intent intent = new Intent();
            intent.putExtra("isAdd",0);
            intent.putExtra("ctime",date);
            setResult(Api.DATA_REFRESH,intent);
            finish();
        }
        return b;
    }


    private boolean editTodo(){
        boolean b = false;
        String title = addTitle.getText().toString().trim();
        String content = addContent.getText().toString().trim();
        String udate = Tools.currentDate();
        if (todoBean!=null) {
            todoBean.setContent(content);
            todoBean.setTitle(title);
            todoBean.setUtime(udate);
             b = sqlPersenter.updateTodo(todoBean);

        }
        if (!b) {
            Toast.makeText(this,"保存失败!",Toast.LENGTH_SHORT).show();
        }else {
            Intent intent = new Intent();
            intent.putExtra("isAdd",1);
            intent.putExtra("id",todoBean.getId());
            intent.putExtra("title",todoBean.getTitle());
            intent.putExtra("content",todoBean.getContent());
            setResult(Api.DATA_REFRESH,intent);
            finish();
        }


        return false;
    }


    private void saveTodo(String msg, final int type){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(msg);
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                switch (type){
                    case ADD:
                        addTodo();
                        break;
                    case EDITOR:
                        editTodo();
                        break;
                }

                dialogInterface.dismiss();
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();


    }




}
