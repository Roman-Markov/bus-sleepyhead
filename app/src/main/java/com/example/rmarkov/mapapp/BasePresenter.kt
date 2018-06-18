package com.example.rmarkov.mapapp

import io.reactivex.disposables.CompositeDisposable

abstract class BasePresenter<V> {

    protected var view: V? = null

    protected var compositeDisposable = CompositeDisposable()

    public open fun attachView(view: V) {
        this.view = view
    }

    public open fun detachView() {
        compositeDisposable.clear()
        view = null
    }
}