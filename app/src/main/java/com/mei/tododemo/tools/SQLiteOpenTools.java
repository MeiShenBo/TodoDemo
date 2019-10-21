package com.mei.tododemo.tools;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.util.Log;


import com.mei.tododemo.commom.Api;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

/**
 * create by meishenbo
 */
public class SQLiteOpenTools extends SQLiteOpenHelper {

    private final static String TAG="MSQLiteOpenHelper";

    private String sql = null;

    private String oldTable;
    private String newTable;
    private boolean isCopy = false;

    public SQLiteOpenTools(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public SQLiteOpenTools(Context context, String dbname, int version){
        super(context,dbname,null,version);
    }

    //(id integer primary key autoincrement,name varchar(20),age varchar(10))
    @Override
    public void onCreate(SQLiteDatabase db) {

        if (sql!=null&&!sql.equals("")) {
            db.execSQL(sql);
        }
    }

    /**
     * 创建表
     * @param db
     * @param sql
     */
    public boolean createDb(SQLiteDatabase db,String sql,String table) throws Exception {
        Cursor cursor = db.rawQuery("select name from sqlite_master where type='table' ",null);
       while (cursor.moveToNext()){
           String tabName = cursor.getString(0);
           if (tabName.equals(table)){
               throw new Exception("新表已存在!");
           }
       }

        this.sql = null;
        this.sql = sql;
        onCreate(db);
        return  true;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

                if (isCopy&&oldTable!=null&&!oldTable.equals("")&&newTable!=null&&!newTable.equals("")) {
                    db.execSQL("alter table "+oldTable+" rename to _temp_table"); //修改旧表名
                    db.execSQL(sql);//创建新表
                    db.execSQL("insert into "+newTable+" select * from _temp_table");//将旧表中数据存入新表
                    db.execSQL("drop table _temp_table");//删除旧表
                }else if (oldTable!=null&&!oldTable.equals("")){
                    db.execSQL("drop table "+oldTable);//删除旧表
                    db.execSQL(sql);//创建新表
                }

                if (db.getVersion()<newVersion){
                    db.setVersion(newVersion);
                }

    }




    /**
     *
     * @param db
     * @param oldVersion 旧版本
     * @param newsVersion 新版本
     * @param sql 创建的表
     * @param oldTable 旧表
     * @param newTable 新表
     * @param isCopy 是否拷贝旧表数据到新表中
     */
    public boolean upgradeDb(SQLiteDatabase db,int oldVersion,int newsVersion,String sql,String oldTable,String newTable,boolean isCopy) throws Exception {
        boolean isTable = true;
        Cursor cursor = db.rawQuery("select name from sqlite_master where type='table'",null);
        while (cursor.moveToNext()){
            String tabName = cursor.getString(0);
            if (tabName.equals(oldTable)){//如果在表中找到旧表
              isTable = false;
            }

            if (tabName.equals(newTable)){
                throw new Exception("新表已存在!");
            }
        }

        if (isTable){
            throw new Exception("旧表不存在!");
        }

        this.sql = null;
        this.sql = sql;
        this.oldTable = oldTable;
        this.newTable = newTable;
        this.isCopy = isCopy;
        if (sql!=null&&!sql.equals("")) {
            onUpgrade(db,oldVersion,newsVersion);
            return  true;
        }

        return  false;

    }


    public boolean onDeleteDb(SQLiteDatabase db,String table) throws Exception {
        boolean isTable = true;
        Cursor cursor = db.rawQuery("select name from sqlite_master where type='table'",null);
        while (cursor.moveToNext()){
            String tabName = cursor.getString(0);
            if (tabName.equals(table)){//如果在表中找到旧表
                isTable = false;
            }

        }

        if (isTable){
            throw new Exception("表不存在!");
        }

        db.execSQL("drop table "+table);

        return true;
    }
    /**
     * 查询是否有这个表
     * @param db
     * @param table
     * @return
     */
    public boolean isTable(SQLiteDatabase db,String table){
        Cursor cursor = db.rawQuery("select name from sqlite_master where type='table'",null);
        while (cursor.moveToNext()){
            String tabName = cursor.getString(0);
            if (tabName.equals(table)){//如果在表中找到旧表
                return true;
            }

        }
        return false;
    }


    public boolean insert(SQLiteDatabase db,Object obj,String table) throws Exception {
        boolean isTable = true;
        Cursor cursor = db.rawQuery("select name from sqlite_master where type='table' ",null);
        while (cursor.moveToNext()){
            String tabName = cursor.getString(0);
            if (tabName.equals(table)){
                isTable = false;
            }
        }
        if (isTable){
            throw new Exception("表不存在!");
        }
        Class<?> aClass = obj.getClass();
        ContentValues contentValues = new ContentValues();

            for (Field field : aClass.getDeclaredFields()) {
                if (!Modifier.isPrivate(field.getModifiers())) {
                    continue;
                }
                field.setAccessible(true);
                String name = field.getName();
                Object value =  field.get(obj);
                if (value==null)
                    continue;

                if (field.getType().getName().equals("java.lang.String")){
                    contentValues.put(name,(String)value);
                }else if(field.getType().getName().equals("int")){
                    contentValues.put(name,(int) value);
                }else if(field.getType().getName().equals("long")){
                    contentValues.put(name,(long)value);
                }else if(field.getType().getName().equals("float")){
                    contentValues.put(name,(float)value);
                }else if(field.getType().getName().equals("double")){
                    contentValues.put(name,(double)value);
                }
                field.setAccessible(false);

            }

            long insert = db.insert(table, null, contentValues);

            if (insert>0) {
                return true;
            }
        return false;
    }


    /**
     *
     * @param db
     * @param obj 传入的数据
     * @param table 需要的表
     * @param where 修改条件
     * @param values 修改条值
     * @return
     */
    public boolean update(SQLiteDatabase db,Object obj,String table,String where,String[] values) throws Exception {
        boolean isTable = true;
        Cursor cursor = db.rawQuery("select name from sqlite_master where type='table' ",null);
        while (cursor.moveToNext()){
            String tabName = cursor.getString(0);
            if (tabName.equals(table)){
                isTable = false;
            }
        }
        if (isTable){
            throw new Exception("表不存在!");
        }

        Class<?> aClass = obj.getClass();
        ContentValues contentValues = new ContentValues();

        for (Field field : aClass.getDeclaredFields()) {
            if (!Modifier.isPrivate(field.getModifiers())) {
                continue;
            }
            field.setAccessible(true);
            String name = field.getName();
            Object value =  field.get(obj);
            if (value==null)
                continue;

            if (field.getType().getName().equals("java.lang.String")){
                contentValues.put(name,(String)value);
            }else if(field.getType().getName().equals("int")){
                contentValues.put(name,(int) value);
            }else if(field.getType().getName().equals("long")){
                contentValues.put(name,(long)value);
            }else if(field.getType().getName().equals("float")){
                contentValues.put(name,(float)value);
            }else if(field.getType().getName().equals("double")){
                contentValues.put(name,(double)value);
            }
            field.setAccessible(false);

        }

        if (contentValues.size()>0){
            long update = db.update(table, contentValues,where,values);
            if (update>0) {
                return true;
            }
        }
        return false;
    }

    /**
     * 传入对象，属性必须是私有属性
     * @param db
     * @param table
     * @param object 显示全部字段
     * @param selectionArgs 占位符中的值
     * @return
     */
    public List<Object> select(SQLiteDatabase db,String ssql,String table,Object object,String[] selectionArgs) throws Exception {
        boolean isTable = true;
        Cursor cursort = db.rawQuery("select name from sqlite_master where type='table' ",null);
        while (cursort.moveToNext()){
            String tabName = cursort.getString(0);
            if (tabName.equals(table)){
                isTable = false;
            }
        }
        if (isTable){
            throw new Exception("表不存在!");
        }


        List<Object> objects = new ArrayList<>();
        Cursor cursor = db.rawQuery(ssql,selectionArgs);
        Class<?> aClass = object.getClass();
        Field[] fields = aClass.getDeclaredFields();

        while (cursor.moveToNext()) {
            Object obj = null;
            try {
                obj = aClass.getConstructor(new Class[]{}).newInstance(new Object[]{});
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }

            for (int i = 0; i < fields.length; i++) {

                Field field = fields[i];

                if (!Modifier.isPrivate(field.getModifiers())){//判断是否是私有属性，不是跳过
                    continue;
                }
                try {
                    if (obj==null){
                        Log.d(TAG, "Object:  NullPointerException");
                        throw  new Exception("Object: Exception,复制类出错");
                    }

                    field.setAccessible(true);
                    String name = field.getName();//获取私有属性名
                    int columnIndex = cursor.getColumnIndex(name);//获取数据库查出来的下标
                    if (field.getType().getName().equals("java.lang.String")){
                        String values = cursor.getString(columnIndex);
                        if (values!=null){
                            field.set(obj,cursor.getString(columnIndex));//获取数据，存入类中
                        }

                    } else if(field.getType().getName().equals("java.lang.Integer")){//0
                        field.set(obj,cursor.getInt(columnIndex));//获取数据，存入类中(仅限id使用)
                    }else if(field.getType().getName().equals("long")){ //0
                        field.set(obj,cursor.getLong(columnIndex));//获取数据，存入类中
                    }else if(field.getType().getName().equals("float")){ //0.0
                        field.set(obj,cursor.getFloat(columnIndex));//获取数据，存入类中
                    }else if(field.getType().getName().equals("double")){ //0.0
                        field.set(obj,cursor.getDouble(columnIndex));//获取数据，存入类中
                    }
                    else {
                        throw  new Exception("类型异常:类型为String,Integer,float,double!");
                    }
                    field.setAccessible(false);

                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }

            }
            objects.add(obj);
        }
        return  objects;
    }

    public boolean copyDb(Context context,byte[] bytes){
        final File copyFile = new File(Environment.getExternalStorageDirectory() + File.separator + context.getPackageName() + "/"+"todo.txt");
        final File folderFile = new File(Environment.getExternalStorageDirectory() + File.separator + context.getPackageName() + "/");
        if (!copyFile.exists()) {
            if (!folderFile.exists()) {
                folderFile.mkdirs();
            }
        }else {
            copyFile.delete();
        }

        ByteArrayInputStream bais = null;
        FileOutputStream fos = null;
                try {
                    bais = new ByteArrayInputStream(bytes);
                    fos = new FileOutputStream(copyFile);
                    int len = 0;
                    byte[] buf = new byte[1024];
                    while ((len=bais.read(buf)) != -1) {
                        fos.write(buf,0,len);
                        fos.flush();
                    }
                   return true;
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    return false;
                } catch (IOException e) {
                    e.printStackTrace();
                    return false;
                }finally {
                    if (bais!=null) {
                        try {
                            bais.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    if (fos!=null) {
                        try {
                            fos.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }



    }




    public String inputDb(Context context) throws Exception {
        final File dbFile = new File(Environment.getExternalStorageDirectory() + File.separator + context.getPackageName()  + "/"+"todo.txt");
        if (!dbFile.exists()) {
            throw  new Exception("数据导入失败:未找到数据!");
        }
                FileInputStream fis = null;
                ByteArrayOutputStream baos = null;
                try {
                    fis= new FileInputStream(dbFile);
                    baos = new ByteArrayOutputStream();
                    int len = 0;
                    byte[] buf = new byte[1024];
                    while ((len=fis.read(buf)) != -1) {
                        baos.write(buf,0,len);
                        baos.flush();
                    }


                    return baos.toString();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }finally {
                    if (fis!=null) {
                        try {
                            fis.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    if (baos!=null) {
                        try {
                            if (baos.toString()!=null) {
                                dbFile.delete();
                            }
                            baos.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }


            return  null;


    }

}
