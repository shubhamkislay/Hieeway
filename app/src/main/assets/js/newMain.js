'use strict';

// Set up media stream constant and parameters.

// In this codelab, you will be streaming video only: "video: true".
// Audio will not be streamed because it is set to "audio: false" by default.
const mediaStreamConstraints = {
  video: true,
};

// Set up to exchange only video.
const offerOptions = {
  offerToReceiveVideo: 1,
};

// Define initial start time of the call (defined as connection between peers).
let startTime = null;

// Define peer connections, streams and video elements.
const localVideo = document.getElementById('localVideo');
const remoteVideo = document.getElementById('remoteVideo');

let localStream;
let remoteStream;

//let localPeerConnection;
  var servers = {'iceServers': [{'urls': 'stun:stun.l.google.com:19302'}]};

var localPeerConnection = new RTCPeerConnection(servers);


var peerDescp = "Calling from JavaScript";
var type = "";
var otherPeerDesrp = null;
var mess = "";

localPeerConnection.onaddstream = (event =>
remoteVideo.srcObject = event.stream);

//localPeerConnection.addEventListener('addstream', gotRemoteMediaStream);

//remoteVideo.srcObject = event.stream;

function gotRemoteMediaStream(Event)
{

    const mediaStream = event.stream;
  remoteVideo.srcObject = mediaStream;
  remoteStream = mediaStream;
  trace('Remote peer connection received remote stream.');

  Android.showToast();


}



function showMyFace() {
 navigator.mediaDevices.getUserMedia({audio:false, video:true})
 .then(stream => localVideo.srcObject = stream)
 .then(stream => localPeerConnection.addStream(stream));
 //callButton.disabled = false;

// Android.signalOtherUser()
showFriendsFace()

}

function showFriendsFace() {
 hangupButton.disabled = false;
 localPeerConnection.createOffer()
 .then(offer => localPeerConnection.setLocalDescription(offer) )
 .then(() => Android.signalOtherUser(JSON.stringify({'sdp': localPeerConnection.localDescription}),"offer") );

   /*localPeerConnection.createOffer()
     .then(createdOffer).catch(setSessionDescriptionError);*/

}

/*function createdOffer(description) {
  trace(`Offer from localPeerConnection:\n${description.sdp}`);


  toast = description.sdp.toString();
  mess = "offer"

  //showAndroidToast(toast);

  Android.signalOtherUser(toast,mess);


  trace('localPeerConnection setLocalDescription start.');
  localPeerConnection.setLocalDescription(description)
    .then(() => {
      setLocalDescriptionSuccess(localPeerConnection);
    }).catch(setSessionDescriptionError);


}*/

function hangupAction() {

  Android.closeConnection();
  localPeerConnection.close();
  localPeerConnection = null;
  hangupButton.disabled = true;
  callButton.disabled = true;
  localVideo.srcObject = null;
  localVideo.disabled = true;
  trace('Ending call.');
}

function recieveSignalOtherUser(data){

    //otherPeerDescrp = Android.getRemoteDescription();
    readMessage(data);

}

function readMessage(data,type) {

 navigator.mediaDevices.getUserMedia({audio:false, video:true})
 .then(stream => localVideo.srcObject = stream)
 .then(stream => localPeerConnection.addStream(stream));

 //var msg = JSON.parse(data.val().message);

 Android.showToast(msg);

/* if (msg.ice != undefined)
 localPeerConnection.addIceCandidate(new RTCIceCandidate(msg.ice));*/

 if (type == "offer")
 {
 localPeerConnection.setRemoteDescription(new RTCSessionDescription(data))
 .then(() => localPeerConnection.createAnswer())
 .then(answer => localPeerConnection.setLocalDescription(answer))
 .then(() => Android.signalOtherUser(JSON.stringify({'sdp': localPeerConnection.localDescription}),"answer"));

 localPeerConnection.onaddstream = (event =>
 remoteVideo.srcObject = event.stream);
/*    localPeerConnection.setRemoteDescription(data)
      .then(() => {
        setRemoteDescriptionSuccess(localPeerConnection);
      }).catch(setSessionDescriptionError);

    trace('remotePeerConnection createAnswer start.');
    localPeerConnection.createAnswer()
      .then(createdAnswer)
      .catch(setSessionDescriptionError);*/
 }
 else if (type == "answer")
 localPeerConnection.setRemoteDescription(new RTCSessionDescription(data));

 localPeerConnection.onaddstream = (event =>
 remoteVideo.srcObject = event.stream);


};

function createdAnswer(description) {
  trace(`Answer from remotePeerConnection:\n${description.sdp}.`);

  Android.signalOtherUser(toast,"answer");


  trace('localPeerConnection setRemoteDescription start.');
  localPeerConnection.setRemoteDescription(description)
    .then(() => {
      setRemoteDescriptionSuccess(localPeerConnection);
    }).catch(setSessionDescriptionError);


}

function setSessionDescriptionError(error) {
  //trace(`Failed to create session description: ${error.toString()}.`);
}

// Logs success when setting session description.
function setDescriptionSuccess(peerConnection, functionName) {
  const peerName = getPeerName(peerConnection);
  trace(`${peerName} ${functionName} complete.`);
}

// Logs success when localDescription is set.
function setLocalDescriptionSuccess(peerConnection) {
  setDescriptionSuccess(peerConnection, 'setLocalDescription');
}

// Logs success when remoteDescription is set.
function setRemoteDescriptionSuccess(peerConnection) {
  setDescriptionSuccess(peerConnection, 'setRemoteDescription');
}




startButton.addEventListener('click', showMyFace);
//callButton.addEventListener('click', callAction);
hangupButton.addEventListener('click', hangupAction);
