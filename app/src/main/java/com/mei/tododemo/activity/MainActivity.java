package com.mei.tododemo.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.mei.tododemo.R;
import com.mei.tododemo.adapter.MainRAdapter;
import com.mei.tododemo.commom.Api;
import com.mei.tododemo.model.TodoBean;
import com.mei.tododemo.presenter.BasePersenter;
import com.mei.tododemo.presenter.MainPersenter;
import com.mei.tododemo.presenter.MainPersenterImpl;
import com.mei.tododemo.tools.Tools;
import com.mei.tododemo.view.MainView;

import java.util.ArrayList;
import java.util.List;

/**
 * 动态权限没加，
 */
public class MainActivity extends BaseActivity implements MainView{
    private String TAG ="MainActivity";
    private Toolbar mainToolbar;
    private RecyclerView mainRecyclerView;
    private MainPersenterImpl mainPersenter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private MainRAdapter mainRAdapter;
    private List<Object> datas = new ArrayList<>();
    private final static int ret_code = 0x123;
//    private final static int refresh = 1;
//    private final static int load_more = 2;
//    private final static int normal = 0;
//    private int refresh_status = 0;
    private int mPosition = -1;
    private int page = 1;
    private int type = -1;
    private String order ="desc";
    private String sql ="select * from "+Api.DB.table_name;
    private String [] permissions = new String[]{
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
        init();
    }

    private void init() {
        mainPersenter = new MainPersenterImpl(this,this);
        mainPersenter.dataBeans(null, null,null,null);
    }

    private void initView() {
        mainToolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(mainToolbar);
        mainToolbar.setOnMenuItemClickListener(onMenuItemClickListener);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.main_swipeRefresh);
        swipeRefreshLayout.setColorSchemeColors(Color.BLUE);
        swipeRefreshLayout.setOnRefreshListener(refreshListener);
        mainRecyclerView = (RecyclerView) findViewById(R.id.main_recyclerView);
        mainRecyclerView.addOnScrollListener(onScrollListener);
        mainRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mainRAdapter = new MainRAdapter(this);
        mainRAdapter.setListItemClickListener(listItemClickListener);
        mainRecyclerView.setAdapter(mainRAdapter);



    }
    //初始化菜单
    @Override
    public boolean onCreatePanelMenu(int featureId, Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu,menu);
        return true;
    }

    //菜单监听事件
    private  Toolbar.OnMenuItemClickListener onMenuItemClickListener = new Toolbar.OnMenuItemClickListener() {
        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {

            switch (menuItem.getItemId()){
                case R.id.order_todo:
                    if (order.equals("desc")){
                        order="asc";
                    }else {
                        order = "desc";
                    }
                    mainPersenter.dataBeans(null, null,null,order);


                    break;

                case R.id.add_todo:
                    openActivity(AddToDoActivity.class,null,ret_code);

                    break;

                case R.id.copy_data:
                    type = 0;
                    if (Build.VERSION.SDK_INT >=Build.VERSION_CODES.M) {
                        List<String> dPermission = new ArrayList<>();
                        for (int i = 0; i < permissions.length; i++) {

                            if (ActivityCompat.checkSelfPermission(MainActivity.this,permissions[i])== PackageManager.PERMISSION_DENIED) {
                                dPermission.add(permissions[i]);
                            }
                        }
                        if (dPermission.size()>0) {
                            ActivityCompat.requestPermissions(MainActivity.this,dPermission.toArray(new String[dPermission.size()]),0x11);
                        }else {
                            setData("是否备份数据!",type);

                        }



                    }else {
                        setData("是否备份数据!",type);
                    }


                    break;

                case R.id.input_data:

                    type = 1;


                    if (Build.VERSION.SDK_INT >=Build.VERSION_CODES.M) {
                        List<String> dPermission = new ArrayList<>();
                        for (int i = 0; i < permissions.length; i++) {

                            if (ActivityCompat.checkSelfPermission(MainActivity.this,permissions[i])== PackageManager.PERMISSION_DENIED) {
                                dPermission.add(permissions[i]);
                            }
                        }
                        if (dPermission.size()>0) {
                            ActivityCompat.requestPermissions(MainActivity.this,dPermission.toArray(new String[dPermission.size()]),0x11);
                        }else {
                            setData("是否导入数据!",type);

                        }



                    }else {
                        setData("是否导入数据!",type);
                    }

                    break;

            }
            return true;
        }
    };

    //下拉刷新
    SwipeRefreshLayout.OnRefreshListener refreshListener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            page = 1;

            String limit = " limit "+ 10 +" offset "+0;

            mainPersenter.dataBeans(sql,limit,null,null);
        }
    };

    RecyclerView.OnScrollListener onScrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
            int itemCount = recyclerView.getLayoutManager().getItemCount();
            int lastPosition = getLastVisibleItemPosition()+1;

            if (itemCount==lastPosition&&RecyclerView.SCROLL_STATE_IDLE==newState){
                page++;
                String limit = " limit "+ (page*10) +" offset "+0;
                mainRAdapter.setLoad_status(MainRAdapter.LOAD_MORE);
                mainPersenter.dataBeans(sql,limit,null,order);
            }
        }

        @Override
        public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
        }
    };

    private int getLastVisibleItemPosition(){//获取最后一个item的位置
        return ((LinearLayoutManager)mainRecyclerView.getLayoutManager()).findLastVisibleItemPosition();
    }

    //事件点击，记录跳转详情
    MainRAdapter.ListItemClickListener listItemClickListener = new MainRAdapter.ListItemClickListener() {
        @Override
        public void onItemClick(RecyclerView recyclerView, int position, View view) {
            mPosition = -1;
            TodoBean todoBean = (TodoBean) datas.get(position);
            Bundle bundle = new Bundle();
            bundle.putInt("id",todoBean.getId());
            bundle.putString("title",todoBean.getTitle());
            bundle.putString("content",todoBean.getContent());
            bundle.putString("ctime",todoBean.getCtime());
            bundle.putString("utime",todoBean.getUtime());
            mPosition = position;
            openActivity(AddToDoActivity.class,bundle,ret_code);
        }
        //长按
        @Override
        public void onItemLongClick(RecyclerView recyclerView, final int position, View view) {
            mPosition = -1;
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setMessage("是否删除当前TODO?");
            builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });
            builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    mPosition = position;
                    TodoBean todoBean = (TodoBean) datas.get(position);
                    mainPersenter.deleteTodo(todoBean.getId());
                }
            });

            AlertDialog alertDialog = builder.create();
            alertDialog.show();

        }
    };

    //回调刷新
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
       if (requestCode==ret_code&&resultCode==Api.DATA_REFRESH){
           int isAdd = data.getIntExtra("isAdd", -1);
          switch (isAdd){
              case -1:

                  break;


              case 0:

                  String ctime = data.getStringExtra("ctime");
                  mainPersenter.loadTodo("where ctime=?",new String[]{ctime});
                  break;
              case 1:
                  if (mPosition!=-1){
                      String title = data.getStringExtra("title");
                      String content =data.getStringExtra("content");
                      int id = data.getIntExtra("id",-1);
                      TodoBean todoBean  = (TodoBean) datas.get(mPosition);
                      if (todoBean.getId()!=id) {
                          String limit = " limit "+ 10 +" offset "+0;

                          mainPersenter.dataBeans(null,limit,null,order);
                          mPosition = -1;
                          break;
                      }
                      todoBean.setTitle(title);
                      todoBean.setContent(content);
                      mainRAdapter.adddatas(datas);
                      mPosition = -1;
                  }

                  break;

          }


       }

    }

    @Override
    public void showLoading() {

    }



//加载获取到的数据刷新UI
    @Override
    public void refreshUI(List<Object> objects) {

        if (objects!=null&&objects.size()>0) {
           datas.clear();
            datas.addAll(objects);
            mainRAdapter.adddatas(datas);
            if ((page*10)>objects.size()){
                if (page>1) {
                    page--;
                }
                if (mainRAdapter.getLoad_status()==MainRAdapter.LOAD_MORE){//上拉加载提示
                    mainRAdapter.setLoad_status(MainRAdapter.NOT_LOAD_MORE);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mainRAdapter.setLoad_status(MainRAdapter.LOAD_HIDE);
                        }
                    },3000);
                }
            }else {
                if (mainRAdapter.getLoad_status()==MainRAdapter.LOAD_MORE){//上拉加载提示
                    mainRAdapter.setLoad_status(MainRAdapter.LOAD_NORMAL);
                }
            }
            }
        if (swipeRefreshLayout.isRefreshing()) {
            swipeRefreshLayout.setRefreshing(false);
        }


    }
    //替换某一条数据
    @Override
    public void load(Object todoBean) {
        if (todoBean!=null) {
            datas.add(0,todoBean);
            mainRAdapter.adddatas(datas);
        }

    }
    //错误显示i
    @Override
    public void showError(String msg) {
        Log.d(TAG, "showError: "+msg);
    }
    //c成功
    @Override
    public void showSuccess(String msg) {
        if (msg!=null) {
            if (mPosition!=-1) {
                datas.remove(mPosition);
                mainRAdapter.adddatas(datas);
                mPosition = -1;
            }
            Toast.makeText(this,msg,Toast.LENGTH_SHORT).show();
        }
    }


    private void setData(String msg, final int type){

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
                try {
                   switch (type){
                       case 0:
                           mainPersenter.copyDb(0);
                           break;

                       case 1:
                           mainPersenter.copyDb(1);
                           break;

                   }
                } catch (Exception e) {
                    Log.d(TAG, "onDestroy: "+e.getMessage());
                    e.printStackTrace();
                }
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        List<String> dPermission = new ArrayList<>();

        for (int i = 0; i < permissions.length; i++) {
            if (ActivityCompat.checkSelfPermission(MainActivity.this,permissions[i])== PackageManager.PERMISSION_DENIED) {
                dPermission.add(permissions[i]);
            }
        }

        if (dPermission.size()>0) {
            Toast.makeText(MainActivity.this,"没有存储权限",Toast.LENGTH_SHORT).show();
        }else {
            switch (type){
                case 0:

                    setData("是否备份数据!",type);
                    break;

                case 1:
                    setData("是否导入数据!",type);
                    break;
            }

        }




    }

    @Override
    protected void onDestroy() {
        super.onDestroy();


    }
}
