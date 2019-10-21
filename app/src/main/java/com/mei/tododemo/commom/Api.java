package com.mei.tododemo.commom;

/**
 * created by meishenbo
 * 2018/12/13
 */
public class Api {

    public final static int DATA_REFRESH = 0X111111;


    public final static class DB{
        public final static  String DB_NAME="todo.db";
        public final static  int DB_VERSION=1;
        public final static  int DB_NEW_VERSION=1;
        public final static String table_name="todo_list";
        public static String todo_sql = "create table todo_list (id integer primary key autoincrement,title varchar(50),content varchar(500),ctime varchar(50),utime varchar(50))";
    }

}
