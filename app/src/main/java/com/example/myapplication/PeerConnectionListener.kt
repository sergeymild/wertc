package com.example.myapplication

import org.webrtc.*

class PeerConnectionListener: PeerConnection.Observer {
    private val id: String


    constructor(id: String) {
        this.id = id
    }

    override fun onDataChannel(p0: DataChannel?) {
        val dataChannel = p0 ?: return
    }

    override fun onConnectionChange(newState: PeerConnection.PeerConnectionState?) {
    }

    override fun onSignalingChange(p0: PeerConnection.SignalingState?) {}
    override fun onIceConnectionChange(p0: PeerConnection.IceConnectionState?) {}
    override fun onIceConnectionReceivingChange(p0: Boolean) {}
    override fun onIceGatheringChange(p0: PeerConnection.IceGatheringState?) {}
    override fun onIceCandidate(p0: IceCandidate?) {}
    override fun onIceCandidatesRemoved(p0: Array<out IceCandidate>?) {}
    override fun onAddStream(stream: MediaStream?) {}
    override fun onRemoveStream(stream: MediaStream?) {}
    override fun onRenegotiationNeeded() {}
    override fun onAddTrack(p0: RtpReceiver?, p1: Array<out MediaStream>?) {}
}