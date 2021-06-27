package com.example.myapplication

class VideoTrack(val track: RTCVideoTrack) {

    public var isEnabled: Bool
        get() = track.enabled()
        set(value) { track.setEnabled(value) }
}