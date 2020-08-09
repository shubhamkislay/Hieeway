package com.hieeway.hieeway;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

import java.util.Random;

import io.agora.rtc.IRtcEngineEventHandler;
import io.agora.rtc.RtcEngine;
import io.agora.rtc.video.VideoCanvas;
import io.agora.rtc.video.VideoEncoderConfiguration;

import static com.hieeway.hieeway.VerticalPageActivity.userNameChattingWith;

public class VideoCallTestActivity extends AppCompatActivity {

    Button video_btn;
    FrameLayout remote_video_view_container, local_video_view_container;
    IRtcEngineEventHandler mRtcEventHandler;
    private RtcEngine mRtcEngine;
    private Boolean connect = false;
    private SurfaceView remotesurfaceView, localSurfaceView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_call_test);

        video_btn = findViewById(R.id.video_btn);
        remote_video_view_container = findViewById(R.id.remote_video_view_container);
        local_video_view_container = findViewById(R.id.local_video_view_container);


        video_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!connect) {
                    initializeAgoraEngine();
                    connect = true;
                } else {
                    mRtcEngine.leaveChannel();
                    connect = false;
                }
            }
        });


    }

    private void setupIRtcEngineEventHandler() {
        mRtcEventHandler = new IRtcEngineEventHandler() {


            @Override
            public void onRemoteVideoStateChanged(int uid, int state, int reason, int elapsed) {
                super.onRemoteVideoStateChanged(uid, state, reason, elapsed);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(VideoCallTestActivity.this, "onRemoteVideoStateChanged", Toast.LENGTH_SHORT).show();
                        setupRemoteVideo(uid);
                    }
                });
            }

            @Override
            public void onJoinChannelSuccess(String channel, int uid, int elapsed) {


                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {


                    }
                });
            }

            @Override
            public void onUserJoined(int uid, int elapsed) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // Toast.makeText(VideoCallTestActivity.this, userNameChattingWith + " Joined", Toast.LENGTH_SHORT).show();

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {

                                //enable_audio.setVisibility(View.VISIBLE);
                                Toast.makeText(VideoCallTestActivity.this, userNameChattingWith + " Joined", Toast.LENGTH_SHORT).show();


                            }
                        }, 1000);

                    }
                });
            }

            @Override
            public void onLeaveChannel(RtcStats stats) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                    }
                });
            }

            @Override
            public void onUserOffline(int uid, int reason) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                    }
                });
                // Toast.makeText(VideoCallTestActivity.this,"User left",Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onStreamPublished(String url, int error) {
                super.onStreamPublished(url, error);
                Toast.makeText(VideoCallTestActivity.this, "onFirstRemoteVideoDecoded", Toast.LENGTH_SHORT).show();
            }
        };


        //joinChannel();

    }


    private void initializeAgoraEngine() {

        //liveMessagingViewModel.iConnect();

        setupIRtcEngineEventHandler();
        try {
            mRtcEngine = RtcEngine.create(VideoCallTestActivity.this, getString(R.string.agora_app_id), mRtcEventHandler);
            setupVideoProfile();
        } catch (Exception e) {
            e.printStackTrace();

            Toast.makeText(VideoCallTestActivity.this, "Error initializeAgoraEngine(): " + e.toString(), Toast.LENGTH_LONG).show();
            //resetLiveVideoViews();
        }

    }


    private void setupVideoProfile() {

        mRtcEngine.enableVideo();
        mRtcEngine.disableAudio();
        mRtcEngine.enableLocalAudio(false);
        mRtcEngine.stopAudioRecording();

        // mRtcEngine.setVideoProfile(Constants.VIDEO_PROFILE_360P_3, false);

        mRtcEngine.setVideoEncoderConfiguration(new VideoEncoderConfiguration(
                VideoEncoderConfiguration.VD_640x360,
                VideoEncoderConfiguration.FRAME_RATE.FRAME_RATE_FPS_30,
                VideoEncoderConfiguration.STANDARD_BITRATE,
                VideoEncoderConfiguration.ORIENTATION_MODE.ORIENTATION_MODE_FIXED_PORTRAIT));
        setupLocalVideo();
    }

    private void setupLocalVideo() {

        localSurfaceView = RtcEngine.CreateRendererView(VideoCallTestActivity.this);

        /*int height = displayHeight*28/100;
        int wrapperHeight = displayHeight*25/100;
        surfaceView.getLayoutParams().height = wrapperHeight;
        surfaceView.getLayoutParams().width =  height/2;*/

        localSurfaceView.setZOrderMediaOverlay(true);

        local_video_view_container.addView(localSurfaceView);
        mRtcEngine.setupLocalVideo(new VideoCanvas(localSurfaceView, VideoCanvas.RENDER_MODE_HIDDEN, 0));

        //mRtcEngine.

        joinChannel();


    }

    private void joinChannel() {
        mRtcEngine.joinChannel(null, "hieewayvideotestchannel", "Extra Optional Data", new Random().nextInt(10000000) + 1);
        //wasInCall = true;// if you do not specify the uid, Agora will assign one.


        // mRtcEngine.set
    }


    private void setupRemoteVideo(int uid) {

        remote_video_view_container.removeAllViews();

        //remote_audio_video_view_container.removeAllViews();

        /*if (frameRemoteContainer.getChildCount() >= 1) {
            return;
        }*/


        remotesurfaceView = RtcEngine.CreateRendererView(VideoCallTestActivity.this);

        /*int height = displayHeight*28/100;
        int wrapperHeight = displayHeight*25/100;
        remotesurfaceView.getLayoutParams().height = wrapperHeight;
        remotesurfaceView.getLayoutParams().width =  height/2;*/


        remote_video_view_container.addView(remotesurfaceView);
        mRtcEngine.setupRemoteVideo(new VideoCanvas(remotesurfaceView, VideoCanvas.RENDER_MODE_HIDDEN, uid));
        remotesurfaceView.setTag(uid);


    }
}
