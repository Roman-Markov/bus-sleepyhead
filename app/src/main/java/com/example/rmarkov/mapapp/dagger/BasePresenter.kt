package com.example.rmarkov.mapapp.dagger

abstract class BasePresenter<V> {

    protected var view: V? = null

    protected open fun attachView(view: V) {
        this.view = view
    }

    protected open fun detachView() {
        view = null
    }
}