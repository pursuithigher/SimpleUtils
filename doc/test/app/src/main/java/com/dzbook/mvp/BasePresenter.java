package com.dzbook.mvp;

import com.dzbook.lib.rx.CompositeDisposable;

/**
 * Base Presenter
 *
 * @author dongdianzhou on 2017/3/29.
 */
public abstract class BasePresenter {
    protected final CompositeDisposable composite = new CompositeDisposable();
}
