package com.example.rmarkov.mapapp

import android.app.Activity
import android.app.AlarmManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import com.example.rmarkov.mapapp.utils.getAppComponent
import org.jetbrains.annotations.Nullable
import java.util.*
import javax.inject.Inject

class LocationService: Service(), ILocationService {

    @Inject
    lateinit var presenter: LocationServicePresenter

    override fun onCreate() {
        getAppComponent().inject(this)
        presenter.attachView(this);
        super.onCreate()
    }
    companion object {
        public fun createIntent(context: Context): Intent {
            return Intent(context, this::class.java)
        }
    }


    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        presenter.onServiceStarted(intent != null)
        return START_REDELIVER_INTENT
    }

    override fun startAlert() {
        val cal = Calendar.getInstance()
        cal.add(Calendar.SECOND, 1)

        //Create a new PendingIntent and add it to the AlarmManager
        val intent = Intent(this, AlarmReceiverActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this,
                12345, intent, PendingIntent.FLAG_CANCEL_CURRENT)
        val am = getSystemService(Activity.ALARM_SERVICE) as AlarmManager
        am.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(),
                pendingIntent)
    }

    override fun onDestroy() {
        presenter.detachView()
        super.onDestroy()
    }
}