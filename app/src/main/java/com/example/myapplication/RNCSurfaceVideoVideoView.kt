package com.example.myapplication

import android.content.Context
import android.view.View
import org.webrtc.VideoTrack

class RNCSurfaceVideoVideoView(context: Context) : TextureViewRenderer(context) {

    private var currentTrack: VideoTrack? = null

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        currentTrack?.addSink(this)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        unsubscribe()
        release()
    }

    override fun release() {
        unsubscribe()
        super.release()
    }

    fun enableVideo(participant: RemoteParticipant) {
        synchronized(this) {
            val newTrackId = participant.videoTrack?.track?.id()
            if (this.currentTrack?.id() == newTrackId) return
            if (participant.videoTrack?.track?.enabled() != true) return
            val videoTrack = participant.videoTrack!!
            loadTrack(videoTrack.track)
            setFirstFrameDisposableListener {
                println("+++++++++")
            }
        }
    }

    private fun unsubscribe() {
        currentTrack?.removeSink(this)
        currentTrack = null
    }

    private fun loadTrack(track: RTCVideoTrack) {
        if (!track.enabled()) {
            return
        }

        currentTrack?.removeSink(this)
        currentTrack = null
        try {
            currentTrack = track
            track.addSink(this@RNCSurfaceVideoVideoView)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
