package com.example.myapplication

import android.content.Context
import org.webrtc.*
import org.webrtc.PeerConnectionFactory
import kotlin.math.min

val eglBase: EglBase = EglBase.create()

fun setLocalMediaTracks(
        context: Context,
        peerFactory: PeerConnectionFactory,
        user: RemoteParticipant,
        videoWidth: Int,
        videoHeight: Int,
        fps: Int,
        videoEnabled: Bool,
        audioEnabled: Bool
) {
    val frameSide = min(videoWidth, videoHeight)
    val (video, capturer, videoSource) = localVideoTrackAndCapturer(context, peerFactory, user.endpoint, frameSide, fps)
    user.videoTrack = VideoTrack(video)
    val surface = SurfaceTextureHelper.create("WebRTC-SurfaceTextureHelper", eglBase.eglBaseContext)
    user.capturer = LocalVideoCapturer(capturer, frameSide, fps, surface)
    user.capturer!!.rtcCapturer.initialize(surface, context, videoSource.capturerObserver)

    user.audioTrack = AudioTrack(peerFactory.createAudioTrack(user.endpoint, peerFactory.createAudioSource(
        MediaConstraints()
    )))
    if (user.videoTrack == null || user.audioTrack == null) {
        throw RuntimeException()
    }

    user.videoTrack?.isEnabled = videoEnabled
    user.audioTrack?.isEnabled = audioEnabled
}

enum class Direction {
    FRONT
}

fun localVideoTrackAndCapturer(context: Context, peerFactory: PeerConnectionFactory, id: String, frameSide: Int, fps: Int): Triple<RTCVideoTrack, CameraVideoCapturer, VideoSource> {
    val videoSource = peerFactory.createVideoSource(false)
    videoSource.adaptOutputFormat(frameSide, frameSide, frameSide, frameSide, fps)

    val enumerator = getCameraEnumerator(context)

    println("localVideoTrackAndCapturer enumerator $enumerator")
    val videoCapturer = createVideoCapturer(enumerator, Direction.FRONT)!!
    println("localVideoTrackAndCapturer videoCapturer $videoCapturer")
    val videoTrack = peerFactory.createVideoTrack(id, videoSource)

    return Triple(videoTrack, videoCapturer, videoSource)
}

fun createVideoCapturer(enumerator: CameraEnumerator, direction: Direction): CameraVideoCapturer? {
    val deviceNames = enumerator.deviceNames
    for (deviceName in deviceNames) {
        if (direction === Direction.FRONT && enumerator.isFrontFacing(deviceName)) {
            return enumerator.createCapturer(deviceName, null)
        }
    }
    return null
}

private fun getCameraEnumerator(context: Context): CameraEnumerator {
    var camera2EnumeratorIsSupported = false
    camera2EnumeratorIsSupported = Camera2Enumerator.isSupported(context)
    return if (camera2EnumeratorIsSupported) Camera2Enumerator(context) else Camera1Enumerator(true)
}


class LocalVideoCapturer(
        internal val rtcCapturer: CameraVideoCapturer,
        val frameSide: Int,
        val fps: Int,
        private var surface: SurfaceTextureHelper?
) {

    fun stopCapture() {
        println("LocalVideoCapturer stopCapture")
        rtcCapturer.stopCapture()
    }

    fun startCapture() {
        println("LocalVideoCapturer startCapture")
        rtcCapturer.startCapture(frameSide, frameSide, fps)
    }

    fun destroy() {
        println("LocalVideoCapturer destroy")
        rtcCapturer.stopCapture()
        rtcCapturer.dispose()
        surface?.stopListening()
        surface?.dispose()
        surface = null
    }
}