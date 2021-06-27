package com.example.myapplication

import android.Manifest
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.Gravity
import android.widget.FrameLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.material.floatingactionbutton.FloatingActionButton
import org.webrtc.EglBase


class MainActivity : AppCompatActivity() {

    private val PERMISSION_REQUEST_CODE = 200
    var peerFactory = PeerConnectionFactory()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.toolbar))

        findViewById<FloatingActionButton>(R.id.fab).setOnClickListener { view ->

            if (checkPermission()) {
                showPreview()
            } else {
                requestPermission();
            }


        }
    }

    private fun checkPermission(): Boolean {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermission() {
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA),
                PERMISSION_REQUEST_CODE)
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            PERMISSION_REQUEST_CODE -> if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                showPreview()
                // main logic
            } else {

            }
        }
    }


    fun showPreview() {
        peerFactory.setContext(applicationContext)
        val participant = RemoteParticipant("23")
        setLocalMediaTracks(
                applicationContext,
                peerFactory.factory!!,
                participant,
                640,
                640,
                30,
                true,
                true
        )
        if (participant.videoTrack == null || participant.audioTrack == null) {
            throw RuntimeException()
        }

        val videoTrack = participant.videoTrack!!.track
        val audioTrack = participant.audioTrack!!.track
        videoTrack.setEnabled(true)
        audioTrack.setEnabled(true)
        participant.capturer?.startCapture()

        findViewById<FrameLayout>(R.id.nav_host_fragment).also {
            val videiView = RNCSurfaceVideoVideoView(it.context)
            videiView.init(EglBase.create().eglBaseContext,null)
            it.addView(videiView, FrameLayout.LayoutParams(640, 640, Gravity.CENTER))
            videiView.enableVideo(participant)
        }
    }
}