package com.example.rmarkov.mapapp

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.example.rmarkov.mapapp.utils.getAppComponent
import org.jetbrains.annotations.Nullable
import javax.inject.Inject

class LocationService: Service(), ILocationService {

    @Inject
    lateinit var presenter: LocationServicePresenter

    override fun onCreate() {
        getAppComponent().inject(this)
        presenter.attachView(this);
        super.onCreate()
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        presenter.onServiceStarted()
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        presenter.detachView()
        super.onDestroy()
    }
}