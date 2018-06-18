package com.example.rmarkov.mapapp

import android.app.Activity
import android.content.Context
import android.media.RingtoneManager
import android.media.AudioManager
import android.media.MediaPlayer
import android.net.Uri
import android.view.WindowManager
import android.os.Bundle
import android.view.View
import android.view.Window
import android.widget.Button
import com.example.rmarkov.mapapp.location.LocationService
import java.io.IOException


class AlarmReceiverActivity: Activity() {
    private var mMediaPlayer: MediaPlayer? = null

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.requestWindowFeature(Window.FEATURE_NO_TITLE)
        this.window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN)
        setContentView(R.layout.alarm)

        val stopAlarm = findViewById(R.id.stopAlarm) as Button
        stopAlarm.setOnClickListener(object : View.OnClickListener {
            override  fun onClick(arg0: View) {
                mMediaPlayer!!.stop()
//                stopService(LocationService.createIntent(this@AlarmReceiverActivity))
                finish()
            }
        })
    }

    override fun onStart() {
        val alarmUri = getAlarmUri()
        alarmUri?.let {playSound(this, alarmUri)}
        stopService(LocationService.createIntent(this@AlarmReceiverActivity))
        super.onStart()
    }

    private fun playSound(context: Context, alert: Uri) {
        mMediaPlayer = MediaPlayer()
        try {
            mMediaPlayer!!.setDataSource(context, alert)
            val audioManager = context
                    .getSystemService(Context.AUDIO_SERVICE) as AudioManager
            if (audioManager.getStreamVolume(AudioManager.STREAM_ALARM) != 0) {
                mMediaPlayer!!.setAudioStreamType(AudioManager.STREAM_ALARM)
                mMediaPlayer!!.prepare()
                mMediaPlayer!!.start()
            }
        } catch (e: IOException) {
            println("OOPS")
        }

    }

    //Get an alarm sound. Try for an alarm. If none set, try notification,
    //Otherwise, ringtone.
    private fun getAlarmUri(): Uri? {
        var alert: Uri? = RingtoneManager
                .getActualDefaultRingtoneUri(this, RingtoneManager.TYPE_ALARM)
        if (alert == null) {
            alert = RingtoneManager
                    .getActualDefaultRingtoneUri(this, RingtoneManager.TYPE_NOTIFICATION)
            if (alert == null) {
                alert = RingtoneManager
                        .getActualDefaultRingtoneUri(this, RingtoneManager.TYPE_RINGTONE)
            }
        }
        return alert
    }

    override fun onStop() {
        super.onStop()
        mMediaPlayer!!.stop()
    }
}