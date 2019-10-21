package com.mei.tododemo.view;

import com.mei.tododemo.model.TodoBean;
import com.mei.tododemo.presenter.BasePersenter;

import java.util.List;

/**
 * created by meishenbo
 * 2018/12/13
 */
public interface MainView extends BaseView{
    void showLoading();

    void refreshUI(List<Object> todoBeans);

    void load(Object todoBean);

    void showError(String msg);

    void showSuccess(String msg);





}
