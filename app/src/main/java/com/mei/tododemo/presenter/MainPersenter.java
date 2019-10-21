package com.mei.tododemo.presenter;

import com.mei.tododemo.model.TodoBean;

import java.util.List;

/**
 * created by meishenbo
 * 2018/12/13
 */
public interface MainPersenter extends BasePersenter {

    boolean addTodo(TodoBean todoBean);

    boolean updateTodo(TodoBean todoBean);

    boolean deleteTodo(Integer id);

    void dataBeans(String sql,String limit,String[] selectionArgs,String order);

    void loadTodo(String where,String[] selectionArgs);

    void copyDb(int type) throws Exception;

}
