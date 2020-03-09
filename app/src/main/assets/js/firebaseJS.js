  var firebaseConfig = {
    apiKey: "AIzaSyCRbGfvLy476spwqpdWYBMCkpyT7fyiuzQ",
    authDomain: "pekulr-20eec.firebaseapp.com",
    databaseURL: "https://pekulr-20eec.firebaseio.com",
    projectId: "pekulr-20eec",
    storageBucket: "pekulr-20eec.appspot.com",
    messagingSenderId: "590289745600",
    appId: "1:590289745600:web:2d9784da86e77dcf23df25"
  };
  // Initialize Firebase
  firebase.initializeApp(firebaseConfig);

  var database = firebase.database().ref("Signal");

  var yourVideo = document.getElementById("localVideo");
  var friendsVideo = document.getElementById("remoteVideo");
  var yourId = Math.floor(Math.random()*1000000000);
  var servers = {'iceServers': [{'urls': 'stun:stun.services.mozilla.com'}, {'urls': 'stun:stun.l.google.com:19302'}]};
  var pc = new RTCPeerConnection(servers);
  pc.onicecandidate = (event => event.candidate?sendMessage(yourId, JSON.stringify({'ice': event.candidate})):console.log("Sent All Ice") );
  pc.onaddstream = (event => friendsVideo.srcObject = event.stream);

  function sendMessage(senderId, data) {
   var msg = database.push({ sender: senderId, message: data });
   msg.remove();
  }

  function readMessage(data) {
   var msg = JSON.parse(data.val().message);
   var sender = data.val().sender;
   if (sender != yourId) {
   if (msg.ice != undefined)
   pc.addIceCandidate(new RTCIceCandidate(msg.ice));
   else if (msg.sdp.type == "offer")
   pc.setRemoteDescription(new RTCSessionDescription(msg.sdp))
   .then(() => pc.createAnswer())
   .then(answer => pc.setLocalDescription(answer))
   .then(() => sendMessage(yourId, JSON.stringify({'sdp': pc.localDescription})));
   else if (msg.sdp.type == "answer")
   pc.setRemoteDescription(new RTCSessionDescription(msg.sdp));
   }
  };

  database.on('child_added', readMessage);

  function showMyFace() {
   navigator.mediaDevices.getUserMedia({audio:false, video:true})
   .then(stream => yourVideo.srcObject = stream)
   .then(stream => pc.addStream(stream));
  }

  function showFriendsFace() {
   pc.createOffer()
   .then(offer => pc.setLocalDescription(offer) )
   .then(() => sendMessage(yourId, JSON.stringify({'sdp': pc.localDescription})) );
  }

  const startButton = document.getElementById('startButton');
  const callButton = document.getElementById('callButton');
  const hangupButton = document.getElementById('hangupButton');

  startButton.addEventListener('click', showMyFace);
  callButton.addEventListener('click', showFriendsFace);
  hangupButton.addEventListener('click', hangupAction);