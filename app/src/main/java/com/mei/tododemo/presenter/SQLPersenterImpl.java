package com.mei.tododemo.presenter;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;

import com.mei.tododemo.commom.Api;
import com.mei.tododemo.model.TodoBean;
import com.mei.tododemo.tools.SQLiteOpenTools;
import com.mei.tododemo.view.SQLView;

import java.util.List;

/**
 * created by meishenbo
 * 2018/12/14
 */
public class SQLPersenterImpl implements SQLPersenter {

    private final SQLiteOpenTools sqLiteOpenTools;
    private final SQLiteDatabase db;
    private SQLView sqlView;
    private Context context;

    public SQLPersenterImpl(SQLView sqlView, Context context) {
        this.sqlView = sqlView;
        this.context = context;
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
            sqlView.showError(e.getMessage());
            e.printStackTrace();
        }

        return false;
    }

    @Override
    public boolean updateTodo(TodoBean todoBean) {
        try {
            boolean update = sqLiteOpenTools.update(db, todoBean, Api.DB.table_name, "id=?", new String[]{String.valueOf(todoBean.getId())});

            return update;
        } catch (Exception e) {
            sqlView.showError(e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public void dataBeans(String sql, String[] selectionArgs) {
        try {
            List<Object> select = sqLiteOpenTools.select(db,sql, Api.DB.table_name, new TodoBean(), selectionArgs);
            sqlView.refreshUI(select);
        } catch (Exception e) {
            sqlView.showError(e.getMessage());
            e.printStackTrace();
        }

    }
}
