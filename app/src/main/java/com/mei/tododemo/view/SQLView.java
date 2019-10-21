package com.mei.tododemo.view;

import java.util.List;

/**
 * created by meishenbo
 * 2018/12/14
 */
public interface SQLView {
    void refreshUI(List<Object> todoBeans);

    void showError(String msg);
}
