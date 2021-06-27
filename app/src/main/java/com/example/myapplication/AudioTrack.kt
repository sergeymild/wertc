package com.example.myapplication



typealias RTCVideoTrack = org.webrtc.VideoTrack
typealias RTCAudioTrack = org.webrtc.AudioTrack
typealias Bool = Boolean

class AudioTrack(val track: RTCAudioTrack) {

    public var isEnabled: Bool
        get() = track.enabled()
        set(value) { track.setEnabled(value) }
}