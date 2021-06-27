package com.example.myapplication

import android.content.Context
import android.os.Build
import org.webrtc.*
import org.webrtc.PeerConnectionFactory
import org.webrtc.voiceengine.WebRtcAudioManager
import org.webrtc.voiceengine.WebRtcAudioUtils

private val ICE_SERVERS = listOf(PeerConnection.IceServer.builder("stun:stun.l.google.com:19302").createIceServer())
fun createPeerConnectionConfiguration(): PeerConnection.RTCConfiguration =
        PeerConnection.RTCConfiguration(ICE_SERVERS).apply {
            keyType = PeerConnection.KeyType.ECDSA
            enableDtlsSrtp = true
            sdpSemantics = PeerConnection.SdpSemantics.UNIFIED_PLAN
        }

enum class VideoMode(val description: String) {
    SOFTWARE("SOFTWARE"),
    HARDWARE("HARDWARE");
}
class PeerConnectionFactory {
    var factory: PeerConnectionFactory? = null

    fun createPeerConnection(listener: PeerConnectionListener): PeerConnection {
        return factory!!.createPeerConnection(createPeerConnectionConfiguration(), listener)!!
    }

    fun setContext(context: Context) {
        factory = createPeerConnectionFactory(context, VideoMode.SOFTWARE)
    }

    fun dispose() {
        factory?.dispose()
    }


    fun createPeerConnectionFactory(
            context: Context,
            videoMode: VideoMode,
    ): PeerConnectionFactory {
        WebRtcAudioUtils.setWebRtcBasedAcousticEchoCanceler(MagicWebRTCUtils.HARDWARE_AEC_BLACKLIST.contains(Build.MODEL))
        WebRtcAudioManager.setBlacklistDeviceForOpenSLESUsage(!MagicWebRTCUtils.OPEN_SL_ES_WHITELIST.contains(Build.MODEL))
        WebRtcAudioUtils.setWebRtcBasedNoiseSuppressor(true)
        WebRtcAudioUtils.setWebRtcBasedAutomaticGainControl(true)

        PeerConnectionFactory.initialize(
                PeerConnectionFactory.InitializationOptions
                        .builder(context)
                        .createInitializationOptions()
        )
        val eglBaseContext = eglBase.eglBaseContext
        return PeerConnectionFactory.builder()
                .apply {
                    when (videoMode) {
                        VideoMode.HARDWARE -> {
                            setVideoDecoderFactory(DefaultVideoDecoderFactory(eglBaseContext))
                            setVideoEncoderFactory(DefaultVideoEncoderFactory(eglBaseContext, true, false))
                        }
                        VideoMode.SOFTWARE -> {
                            setVideoDecoderFactory(SoftwareVideoDecoderFactory())
                            setVideoEncoderFactory(SoftwareVideoEncoderFactory())
                        }
                    }
                }
                .setOptions(PeerConnectionFactory.Options().apply {
                    disableEncryption = false
                    disableNetworkMonitor = true
                })
                .createPeerConnectionFactory()
    }
}