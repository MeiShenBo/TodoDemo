package com.mei.tododemo.presenter;

import com.mei.tododemo.model.TodoBean;

/**
 * created by meishenbo
 * 2018/12/14
 */
public interface SQLPersenter extends BasePersenter {

    boolean addTodo(TodoBean todoBean);

    boolean updateTodo(TodoBean todoBean);

    void dataBeans(String sql,String[] selectionArgs);
}
