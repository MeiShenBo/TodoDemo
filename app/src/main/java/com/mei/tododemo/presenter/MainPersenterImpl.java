package com.mei.tododemo.presenter;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.mei.tododemo.commom.Api;
import com.mei.tododemo.model.TodoBean;
import com.mei.tododemo.tools.SQLiteOpenTools;
import com.mei.tododemo.view.MainView;

import java.util.List;

/**
 * created by meishenbo
 * 2018/12/13
 */
public class MainPersenterImpl implements MainPersenter {
    private String TAG ="MainPersenterImpl";

    private final SQLiteOpenTools sqLiteOpenTools;
    private final SQLiteDatabase db;
    private Context context;
    private  MainView mainView;



    public MainPersenterImpl(Context context,MainView mainView) {
        this.context =context;
        this.mainView = mainView;
        sqLiteOpenTools = new SQLiteOpenTools(context, Api.DB.DB_NAME, Api.DB.DB_VERSION);
        db = sqLiteOpenTools.getReadableDatabase();
        try {
            if (!sqLiteOpenTools.isTable(db,Api.DB.table_name)) {
                sqLiteOpenTools.createDb(db,Api.DB.todo_sql,Api.DB.table_name);
            }
        } catch (Exception e) {
            Toast.makeText(context,e.getMessage(),Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    @Override
    public boolean addTodo(TodoBean todoBean) {
        try {
            boolean insert = sqLiteOpenTools.insert(db, todoBean, Api.DB.table_name);
            return insert;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean updateTodo(TodoBean todoBean) {

        ContentValues contentValues = new ContentValues();
        try {

            return true;
        } catch (Exception e) {
            Toast.makeText(context,e.getMessage(),Toast.LENGTH_SHORT).show();
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean deleteTodo(Integer id) {
        int delete = db.delete(Api.DB.table_name, "id=?", new String[]{String.valueOf(id)});
        if (delete>0){
            mainView.showSuccess("删除成功!");
        }else {
            mainView.showError("删除失败:id="+id);
        }
        return false;
    }



    @Override
    public void dataBeans(String sql,String limit, String[] selectionArgs,String order) {
        List<Object> select = null;
        if (sql==null){
            sql = "select * from "+Api.DB.table_name;
            selectionArgs = null;
        }
        if (limit==null) {
            Log.d(TAG, "dataBeans: xxx");
            limit = " ";
        }
        
        try {

            if (order==null) {
                order = " desc ";
            }
            select = sqLiteOpenTools.select(db,sql+" order by ctime "+order+" "+limit, Api.DB.table_name, new TodoBean(), selectionArgs);
            mainView.refreshUI(select);
        } catch (Exception e) {
            mainView.showError(e.getMessage()+"sss");
            e.printStackTrace();
        }
    }

    @Override
    public void loadTodo(String where,String[] selectionArgs) {
        try {
            List<Object> select = sqLiteOpenTools.select(db, "select * from " + Api.DB.table_name+" "+where, Api.DB.table_name, new TodoBean(),selectionArgs);
            if (select.size()>0) {
                mainView.load(select.get(0));
            }else {
                mainView.showError("查询未奏效！");
            }

        } catch (Exception e) {
            mainView.showError(e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void copyDb(int type) throws Exception {
        switch (type){
            case 0:
                List<Object> select = sqLiteOpenTools.select(db, "select * from "+Api.DB.table_name, Api.DB.table_name, new TodoBean(), null);
                JSONArray jsonArray = new JSONArray();
                for (Object o : select) {
                    TodoBean todoBean = (TodoBean) o;
                    String json = todoBean.toString();
                    JSONObject jsonObject = JSON.parseObject(json);
                    jsonArray.add(jsonObject);

                }
                if (sqLiteOpenTools.copyDb(context,jsonArray.toString().getBytes())) {
                    mainView.showSuccess("备份数据成功!");
                }else {
                    mainView.showSuccess("备份数据失败!");
                }
                break;

            case 1:
                String json = sqLiteOpenTools.inputDb(context);
                if (json!=null) {
                  List<TodoBean> todoBeans = JSON.parseObject(json,new TypeReference<List<TodoBean>>(){});
                    for (TodoBean todoBean : todoBeans) {
                        addTodo(todoBean);
                    }

                    mainView.showSuccess("导入数据成功!");

                }else {
                    mainView.showSuccess("导入数据失败!");
                }
                break;
        }

    }

}
