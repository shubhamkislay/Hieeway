package com.hieeway.hieeway.Fragments;


import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.app.NotificationManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.Typeface;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaRecorder;
import android.media.SoundPool;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.VibrationEffect;
import android.os.Vibrator;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.devlomi.record_view.OnBasketAnimationEnd;
import com.devlomi.record_view.OnRecordClickListener;
import com.devlomi.record_view.OnRecordListener;
import com.devlomi.record_view.RecordButton;
import com.devlomi.record_view.RecordView;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.android.material.snackbar.Snackbar;

import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.ebanx.swipebtn.SwipeButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.hieeway.hieeway.MusicPalService;
import com.jgabrielfreitas.core.BlurImageView;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.hieeway.hieeway.AudioRecorderActivity;
import com.hieeway.hieeway.CameraActivity;
import com.hieeway.hieeway.EphemeralMessageActivityViewModelFactory;
import com.hieeway.hieeway.EphemeralMessageViewModel;
import com.hieeway.hieeway.EphemeralPhotoActivity;
import com.hieeway.hieeway.Interface.MessageRunningListener;
import com.hieeway.hieeway.Model.ChatListItemCreationModel;
import com.hieeway.hieeway.Model.ChatMessage;
import com.hieeway.hieeway.Model.ChatMessageCompound;
import com.hieeway.hieeway.Model.ChatStamp;
import com.hieeway.hieeway.Model.SendMessageAsyncModel;
import com.hieeway.hieeway.Model.User;
import com.hieeway.hieeway.R;
import com.hieeway.hieeway.SendMediaService;
import com.hieeway.hieeway.TypeWriter;
import com.hieeway.hieeway.UserPicViewModel;
import com.hieeway.hieeway.UserPicViewModelFactory;
import com.hieeway.hieeway.VerticalPageActivity;
import com.hieeway.hieeway.VideoPlayActivity;
import com.hieeway.hieeway.ViewProfileActivity;

import java.io.File;
import java.io.IOException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.spec.KeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutionException;

import javax.crypto.Cipher;

import static com.hieeway.hieeway.MyApplication.notificationIDHashMap;
import static com.hieeway.hieeway.VerticalPageActivity.CURRENT_USERNAME;
import static com.hieeway.hieeway.VerticalPageActivity.CURRENT_USERPHOTO;
import static com.hieeway.hieeway.VerticalPageActivity.userIDCHATTINGWITH;

/**
 * A simple {@link Fragment} subclass.
 */
public class EphemeralMessagingFragment extends Fragment implements MessageRunningListener {


    private static final int MSG_TYPING_BOX_ID = 1;
    private static final int MSG_TYPING_BOX_TWO_ID = 2;
    private static final int MSG_TYPING_BOX_THREE_ID = 3;
    private TextView username, button_exterior, message_counter_background, message_no_message_counter_background;
    private Button message_counter;
    private String userFeeling;
    private TextView message_text_dummy, message_text, message_text_two, message_text_three;
    private TextView message_text_sender_dummy, message_text_sender, message_text_two_sender, message_text_three_sender;
    private BlurImageView profile_pic;
    private TypeWriter message_text_typewriter;
    private int msg_send_size =0, msg_send_two_size=0, msg_send_three_size=0;
    private ProgressBar message_pulse, chain_pulse, sending_progress_bar, message_log_pulse, message_running, message_running_two;
    private float opacity = 0.0f;
    private Boolean blinking = false, message_box_blinking = false;
    private String highlightMessageText = "";
    private static String fileName = null;
    private EditText message_box;
    private RelativeLayout message_box_behind;
    private boolean continue_message_box_blinking = true;
    private Boolean messageHighlight = true, sendButtonEnabled = false;
    private Button sendButton, message_bar, reply_btn, cancel_reply_btn;
    private RelativeLayout bottom_bar, message_counter_layout, message_space, reply_tag, app_context_layout, sender_reply_body, sender_reply_tag;
    private RelativeLayout read_message_reply_layout;
    private View rootView;
    private Float alphaValueProfilePic = 1.0f;
    private Boolean enableXtransformation = true;
    private int textSize;
    private Boolean replyTag = false;
    private Boolean ifHintExists = false;
    private String liveMessage, liveMessageTwo, liveMessageThree;
    private Boolean messageTwoPresent = false, messageThreePresent = false;
    private Button emoji, camera;
    private Button read_message_reply;
    private TextView send_arrow_two, archive_btn;
    private ImageView send_arrow, receiver_arrow;
    private TextView your_sent_msg, back_button;
    private String messageStr = "", messageStrTwo = "", messageStrThree = "";
    private Handler deletionHandler, blinkerHandler;
    private String userIdChattingWith;
    private Runnable runnable, blinkerThreadRunnable, blinkerHandlerRunnable;
    private Thread blinkerThread;
    private List<ChatMessage> messageList, sendMessageList, photoMessageList;
    private int blinkInterval;
    private Boolean isSenderReplying = false;
    private ImageView view;
    private int before_count, after_count;
    private StorageTask uploadTask;
    private StorageReference storageReference;
    private ToggleButton toggleButton;
    private Boolean imageReady = true;
    private Boolean imageLoaded = false;
    private String lastMessageID = "none";
    private String senderReplyID = "none";
    private String otherUserPublicKey = null;
    private String otherUserPublicKeyID = null;
    private String currentUserPublicKey = null;
    private String currentUserPublicKeyID = null;
    private String currentUserPrivateKey = null;
    private Boolean senderReplyTag = false;
    private String senderReplyMessage = "none";
    private Boolean isSeeingReply = false;
    private String messageTextOne, messageTextTwo, MessageTextThree;
    private Boolean setChainBtn = false;
    private SeekBar textSizeseekBar;
    private RelativeLayout equlizer;
    private RelativeLayout equi_one;
    /*    username.setText(intent.getStringExtra("username"));
            name.setText(intent.getStringExtra("name"));
            feeling_txt.setText(intent.getStringExtra("feeling_txt"));
            bio_txt.setText(intent.getStringExtra("bio_txt"));
        friendStatus = intent.getStringExtra("friendStatus");
        feelingEmoji = intent.getStringExtra("feelingEmoji");
        photourl = intent.getStringExtra("photourl");
        userId = intent.getStringExtra("userId");
        currentUsername = intent.getStringExtra("currentUsername");
        feeling_text_label = intent.getStringExtra("feeling_txt");
        usernameText = intent.getStringExtra("username");

        String otherusername;
        String feeling_txt;*/
    private Button photosNotification;
    private ObjectAnimator UsernameObjectAnimator;

    private Handler replyHandler, vibratorHandler;
    private Runnable replyRunnable, vibratorRunnable;

    private Switch switchBtn;
    private Boolean softKeyVisible = true;
    private String messageKeySender, messageKeyReceiver;

    private int typingID = 1;

    private Button chainBtn;
    private SwipeButton swipeButton;
    private Boolean swipeState = false;
    private Point size;
    private float displayHeight;

    private Boolean emojiActive = false;
    private RelativeLayout equi_two;
    private RelativeLayout equi_three;
    private RelativeLayout equi_four;
    private RelativeLayout equi_five;
    private RecordView recordView;
    private RecordButton recordButton;

    private int setAnimationSendingDuration;

    private SoundPool soundPool;
    private Boolean readyToCheckSize = false;
    private int delSound1, cantDelSound2, sendSound3, sendingSound4, sendingSound5;

    private float messageTextOpacity = 1.0f;
    private float messageTextTwoOpacity = 1.0f;
    private float messageTextThreeOpacity = 1.0f;


    private EphemeralMessageViewModel ephemeralMessageViewModel;

    private SendMessageFragment sendMessageFragment;
    private Boolean isMessageRunning = false;
    private Window window;
    private Boolean threadPending;
    private int sendTextSize = 34;
    private String currentUsername;
    private TextView top_swipe_text;
    private String currentUserPhoto;
    private TextView online_status;
    private ImageView online_ring;
    private String usernameChattingWith;
    private String photo;
    private Boolean textListner_msg = false;
    private Boolean textListner_msg_two = false;
    private Boolean textListner_msg_three = false;
    private Button record_audio_btn;

    private TextView message_log_text, swipe_down, go_live_txt;
    private boolean notSending = true;
    private boolean textFieldAnimating = false;
    private boolean firstAnimComplete = false;
    private boolean checkConnectedSatus = false;
    private ObjectAnimator animatorY, alphaArrow, animateMsg, animateMsgTwo, animatorMsgThree, alphaMsg, alphaMsgTwo, alphaMsgThree, animatePic;
    private ObjectAnimator animateReceiveArrowY, receiveArrowAlpha, animateNextMsgHintBody, animateNextMsgHintBodyALpha;
    private AnimatorSet animatorSet;
    private boolean sendButtonPressed = false;
    private int arrowAnimDuration = 600;
    private DatabaseReference sendderUsersRef;
    private DatabaseReference receiverUsersRef;
    private DatabaseReference senderChatCreateRef;
    private DatabaseReference receiverChatCreateRef;
    private ChatMessage firstReceivedMessage = new ChatMessage();
    private NotificationManager notificationManager;
    private Button camera_background;
    private MediaRecorder recorder = null;
    private boolean audioRecording = false;
    private RelativeLayout bin;
    private ImageView disablerecord_button;
    private boolean isDisablerecord_button = false;
    private boolean confirmConnected = true;
    private TextView sender_reply_body_title;
    private Button photo_btn, photo_btn_bg;

    public EphemeralMessagingFragment() {
        // Required empty public constructor
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_ephemeral_message, container, false);

        /**
         * The below code is used to block screenshots
         */
        getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,
                WindowManager.LayoutParams.FLAG_SECURE);


        Display display = getActivity().getWindowManager().getDefaultDisplay();
        size = new Point();
        display.getSize(size);
        displayHeight = size.y;

        record_audio_btn = view.findViewById(R.id.record_audio_btn);

        bin = view.findViewById(R.id.bin);
        disablerecord_button = (ImageView) view.findViewById(R.id.disablerecord_button);



        NotificationManager notificationManager = (NotificationManager) getContext().getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);



        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

       final VerticalPageActivity verticalPageActivity = (VerticalPageActivity) getActivity();

       switchBtn = view.findViewById(R.id.switch_btn);
//

        toggleButton = view.findViewById(R.id.toggle_msg_highlight);

        camera_background = view.findViewById(R.id.camera_background);


        app_context_layout = view.findViewById(R.id.app_context_layout);

        textSizeseekBar = view.findViewById(R.id.text_size_seekbar);

        message_log_text = view.findViewById(R.id.message_log_text);

        button_exterior = view.findViewById(R.id.button_exterior);

        sendMessageFragment = new SendMessageFragment();

        online_ring = view.findViewById(R.id.online_ring);

        online_status = view.findViewById(R.id.online_status);

        top_swipe_text = view.findViewById(R.id.swipe_up);

        swipe_down = view.findViewById(R.id.swipe_down);

        go_live_txt = view.findViewById(R.id.go_live_txt);

        recordView = (RecordView) view.findViewById(R.id.record_view);
        recordButton = (RecordButton) view.findViewById(R.id.record_button);

        equlizer = view.findViewById(R.id.equlizer);
        equi_one = view.findViewById(R.id.equi_one);
        equi_two = view.findViewById(R.id.equi_two);
        equi_three = view.findViewById(R.id.equi_three);
        equi_four = view.findViewById(R.id.equi_four);
        equi_five = view.findViewById(R.id.equi_five);

        photo_btn_bg = view.findViewById(R.id.photo_btn_bg);

        sender_reply_body_title = view.findViewById(R.id.sender_reply_body_title);

        sender_reply_body_title.setTypeface(Typeface.createFromAsset(getContext().getAssets(), "fonts/samsungsharpsans-bold.otf"));


        recordView.setSmallMicColor(Color.parseColor("#ffffff"));

        // recordView.setSlideToCancelText("TEXT");

        //disable Sounds
        recordView.setSoundEnabled(false);

        //prevent recording under one Second (it's false by default)
        recordView.setLessThanSecondAllowed(false);

        //set Custom sounds onRecord
        //you can pass 0 if you don't want to play sound in certain state
        recordView.setCustomSounds(R.raw.quiet_knock, R.raw.shootem, R.raw.record_error);

        //change slide To Cancel Text Color
        recordView.setSlideToCancelTextColor(Color.parseColor("#ffffff"));
        //change slide To Cancel Arrow Color
        recordView.setSlideToCancelArrowColor(Color.parseColor("#ffffff"));
        //change Counter Time (Chronometer) color
        recordView.setCounterTimeColor(getActivity().getResources().getColor(R.color.colorPrimaryDark));


        //recordButton.setListenForRecord(false);
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
            disablerecord_button.setVisibility(View.VISIBLE);
            isDisablerecord_button = true;
            recordButton.setVisibility(View.GONE);
            //recordButton.setEnabled(false);
        } else {
            disablerecord_button.setVisibility(View.GONE);
            isDisablerecord_button = true;
            recordButton.setVisibility(View.VISIBLE);
        }

        disablerecord_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.RECORD_AUDIO)
                        != PackageManager.PERMISSION_GRANTED) {
                    requestAudioPermisson();
                    Toast.makeText(getContext(), "Requesting Audio Permission", Toast.LENGTH_SHORT).show();
                }
            }
        });

        //ListenForRecord must be false ,otherwise onClick will not be called
        recordButton.setOnRecordClickListener(new OnRecordClickListener() {
            @Override
            public void onClick(View v) {
                //requestAudioPermisson();
                // Toast.makeText(AudioRecorderActivity.this, "RECORD BUTTON CLICKED", Toast.LENGTH_SHORT).show();
                //  Log.d("RecordButton","RECORD BUTTON CLICKED");
            }
        });

        recordView.setOnBasketAnimationEndListener(new OnBasketAnimationEnd() {
            @Override
            public void onAnimationEnd() {
                // Log.d("RecordView", "Basket Animation Finished");
                camera.setVisibility(View.VISIBLE);
                camera_background.setVisibility(View.VISIBLE);
                message_box.setVisibility(View.VISIBLE);

                //message_box_behind.setVisibility(View.VISIBLE);
            }
        });


        recordView.setSlideToCancelText("Slide to cancel");

        //IMPORTANT
        recordButton.setRecordView(recordView);



        recordView.setOnRecordListener(new OnRecordListener() {
            @Override
            public void onStart() {
                //Start Recording..

                /*if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.RECORD_AUDIO)
                        != PackageManager.PERMISSION_GRANTED )
                    requestAudioPermisson();

                else {*/

                startRecording();
                // bottom_bar.setVisibility(View.INVISIBLE);
                camera.setVisibility(View.INVISIBLE);
                camera_background.setVisibility(View.INVISIBLE);
                message_box.setVisibility(View.INVISIBLE);
                bin.setAlpha(0.0f);
                message_box_behind.setAlpha(0.0f);

                Log.d("RecordView", "onStart");
                // }

            }

            @Override
            public void onCancel() {
                //On Swipe To Cancel
                recorder.reset();
                recorder.release();
                audioRecording = false;
                bin.animate().setDuration(200).alpha(1.0f);

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        bin.setAlpha(0.0f);
                    }
                }, 1000);
                    /*}
                },500);*/
                //recorder.
                // bottom_bar.setVisibility(View.VISIBLE);
                Log.d("RecordView", "onCancel");

            }

            @Override
            public void onFinish(long recordTime) {
                //Stop Recording..
                /*String time = getHumanTimeText(recordTime);
                Log.d("RecordView", "onFinish");





                Log.d("RecordTime", time);*/
                audioRecording = false;
                camera.setVisibility(View.VISIBLE);
                camera_background.setVisibility(View.VISIBLE);
                message_box.setVisibility(View.VISIBLE);
                bin.setAlpha(0.0f);
                //message_box_behind.setVisibility(View.VISIBLE);

                stopRecording();
            }

            @Override
            public void onLessThanSecond() {
                //When the record time is less than One Second
                Log.d("RecordView", "onLessThanSecond");
                audioRecording = false;
                camera.setVisibility(View.VISIBLE);
                camera_background.setVisibility(View.VISIBLE);
                message_box.setVisibility(View.VISIBLE);
                bin.setAlpha(0.0f);
                //message_box_behind.setVisibility(View.VISIBLE);

                recorder.reset();
                recorder.release();
            }
        });





        //rootView = view.findViewById(R.id.app_context_layout);

        replyHandler = new Handler();
        vibratorHandler = new Handler();


        deletionHandler = new Handler();

        message_box = view.findViewById(R.id.message_box);
        message_box_behind = view.findViewById(R.id.message_box_behind);

        AnimationDrawable animationDrawable = (AnimationDrawable) message_box_behind.getBackground();

        animationDrawable.setEnterFadeDuration(150);
        animationDrawable.setExitFadeDuration(300);
        animationDrawable.start();

        message_box.clearFocus();


        emoji = view.findViewById(R.id.emoji);

        toggleButton.setChecked(true);

        toggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                messageHighlight = isChecked;
                setMessageHighlight(messageHighlight);
            }
        });

        message_running = view.findViewById(R.id.message_running);

        message_running_two = view.findViewById(R.id.message_running_two);



        // emojiKeyboardLayout = view.findViewById(R.id.emoji_keyboard_layout);

       // message_box.setUseSystemDefault(true);


        usernameChattingWith = getArguments().getString("usernameChattingWith");
        userIdChattingWith = getArguments().getString("userIdChattingWith");
         photo = getArguments().getString("photo");
        otherUserPublicKey = getArguments().getString("otherUserPublicKey");
        otherUserPublicKeyID = getArguments().getString("otherUserPublicKeyID");
        currentUserPublicKey = getArguments().getString("currentUserPublicKey");
        currentUserPrivateKey = getArguments().getString("currentUserPrivateKey");
        currentUserPublicKeyID = getArguments().getString("currentUserPublicKeyID");


        record_audio_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AudioRecorderActivity.class);
                intent.putExtra("userIdChattingWith", userIdChattingWith);
                intent.putExtra("audiourl", "default");
                //intent.putExtra("sender", chatMessage.getSenderId());

                startActivity(intent);
            }
        });



        DatabaseReference otherUserkeyReference = FirebaseDatabase.getInstance().getReference("Users")
                .child(userIdChattingWith);

        otherUserkeyReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                TaskCompletionSource<User> userTaskCompletionSource = new TaskCompletionSource<>();

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        if (dataSnapshot.exists()) {
                            User user = dataSnapshot.getValue(User.class);
                            try {
                                otherUserPublicKey = user.getPublicKey();
                                otherUserPublicKeyID = user.getPublicKeyId();

                            } catch (Exception e) {
                                //
                            }
                        }
                    }
                }).start();


                Task<User> userTask = userTaskCompletionSource.getTask();

                userTask.addOnCompleteListener(new OnCompleteListener<User>() {
                    @Override
                    public void onComplete(@NonNull Task<User> task) {
                        if (task.isSuccessful()) {
                            //
                        }
                    }
                });




            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        message_text_sender = view.findViewById(R.id.message_text_sender);
        message_text_sender_dummy = view.findViewById(R.id.message_text_sender_dummy);
        message_text_two_sender = view.findViewById(R.id.message_text_two_sender);
        message_text_three_sender = view.findViewById(R.id.message_text_three_sender);

        photosNotification = view.findViewById(R.id.photo_notification);

        sendderUsersRef = FirebaseDatabase.getInstance().getReference("Messages")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child(userIdChattingWith);



        receiverUsersRef = FirebaseDatabase.getInstance().getReference("Messages")
                .child(userIdChattingWith)
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());


        senderChatCreateRef = FirebaseDatabase.getInstance().getReference("ChatList")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child(userIdChattingWith);

        receiverChatCreateRef = FirebaseDatabase.getInstance().getReference("ChatList")
                .child(userIdChattingWith)
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());






        textSizeseekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {


                    message_text_sender.setAutoSizeTextTypeUniformWithConfiguration(1,progress,1,1);

                    message_text_two_sender.setAutoSizeTextTypeUniformWithConfiguration(1,progress,1,1);

                    message_text_three_sender.setAutoSizeTextTypeUniformWithConfiguration(1,progress,1,1);

                    sendTextSize = progress;

                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


        camera = view.findViewById(R.id.camera);

        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED ||
                        ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)
                                != PackageManager.PERMISSION_GRANTED ||
                        ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                != PackageManager.PERMISSION_GRANTED)
                    requestAllPermissions();


                else
                {
                Intent intent = new Intent(getContext(), CameraActivity.class);

                // ephemeralMessageViewModel.createChatListItem(usernameChattingWith, photo, currentUsername, currentUserPhoto);
                if (currentUsername != null && currentUserPhoto != null) {

                    intent.putExtra("userChattingWithId", userIdChattingWith);
                    intent.putExtra("userphoto", photo);
                    intent.putExtra("username", usernameChattingWith);
                    intent.putExtra("currentUsername", currentUsername);
                    intent.putExtra("currentUserPhoto", currentUserPhoto);
                    intent.putExtra("otherUserPublicKeyID", otherUserPublicKeyID);
                    intent.putExtra("currentUserPublicKeyID", currentUserPublicKeyID);
                    intent.putExtra("otherUserPublicKey", otherUserPublicKey);
                    intent.putExtra("currentUserPublicKey", currentUserPublicKey);



                    // intent.putExtra("userChattingWithId", currentUserPhoto);


                    startActivity(intent);
                } else
                    Toast.makeText(getActivity(), "Getting user details", Toast.LENGTH_SHORT).show();
            }
            }
        });


        /**
         * Add text change listener
         */

        message_text_sender.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                msg_send_size=s.length();
                if(s.length()>0 && !textListner_msg)
                {
                    textListner_msg=true;
                    enableXtransformation = false;
                    sender_reply_body.setTranslationX(0);
                    sender_reply_body.animate().translationXBy(-3000).setDuration(500);

                }
                else if(s.length()==0)
                {
                    enableXtransformation = false;
                    textListner_msg=false;
                }



            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                msg_send_size=s.length();
                if(s.length()>0 && !textListner_msg)
                {
                    textListner_msg=true;
                    sender_reply_body.setTranslationX(0);
                    sender_reply_body.animate().translationXBy(-3000).setDuration(500);

                }
                else if(s.length()==0)
                {
                    textListner_msg=false;
                    enableXtransformation = false;
                    if(!textListner_msg_two && !textListner_msg_three) {
                        sender_reply_body.setTranslationX(-2000);
                        sender_reply_body.animate().translationXBy(2000).setDuration(500);
                    }
                }

            }

            @Override
            public void afterTextChanged(Editable s) {
                msg_send_size=s.length();
                if(s.length()>0 && !textListner_msg)
                {
                    textListner_msg=true;
                    sender_reply_body.setTranslationX(0);
                    sender_reply_body.animate().translationXBy(-3000).setDuration(500);

                }
                else if(s.length()==0)
                {
                    enableXtransformation = false;
                    textListner_msg=false;
                }

            }
        });
        message_text_two_sender.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                msg_send_two_size=s.length();
                if(s.length()>0 && !textListner_msg_two && !textListner_msg)
                {
                    textListner_msg_two=true;
                    sender_reply_body.setTranslationX(0);
                    sender_reply_body.animate().translationXBy(-3000).setDuration(500);

                }
                else if(s.length()==0)
                {
                    enableXtransformation = false;
                    textListner_msg_two=false;
                }

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                msg_send_two_size=s.length();
                if(s.length()>0 && !textListner_msg_two && !textListner_msg)
                {
                    textListner_msg_two=true;
                    sender_reply_body.setTranslationX(0);
                    sender_reply_body.animate().translationXBy(-3000).setDuration(500);

                }
                else if(s.length()==0)
                {
                    enableXtransformation = false;
                    textListner_msg_two=false;
                }

            }

            @Override
            public void afterTextChanged(Editable s) {
                msg_send_two_size=s.length();
                if(s.length()>0 && !textListner_msg_two && !textListner_msg)
                {
                    textListner_msg_two=true;
                    sender_reply_body.setTranslationX(0);
                    sender_reply_body.animate().translationXBy(-3000).setDuration(500);

                }
                else if(s.length()==0)
                {
                    enableXtransformation = false;
                    textListner_msg_two=false;
                }

            }
        });
        message_text_three_sender.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                msg_send_three_size=s.length();

                if(s.length()>0 && !textListner_msg_three && !textListner_msg_two && !textListner_msg)
                {
                    textListner_msg_three=true;
                    sender_reply_body.setTranslationX(0);
                    sender_reply_body.animate().translationXBy(-3000).setDuration(500);

                }
                else if(s.length()==0)
                {
                    enableXtransformation = false;
                    textListner_msg_three=false;


                }

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                msg_send_three_size=s.length();
                if(s.length()>0 && !textListner_msg_three && !textListner_msg_two && !textListner_msg)
                {
                    textListner_msg_three=true;
                    sender_reply_body.setTranslationX(0);
                    sender_reply_body.animate().translationXBy(-3000).setDuration(500);

                }
                else if(s.length()==0)
                {
                    enableXtransformation = false;
                    textListner_msg_three=false;
                }

            }

            @Override
            public void afterTextChanged(Editable s) {
                msg_send_three_size=s.length();
                if(s.length()>0 && !textListner_msg_three && !textListner_msg_two && !textListner_msg)
                {
                    textListner_msg_three=true;
                    sender_reply_body.setTranslationX(0);
                    sender_reply_body.animate().translationXBy(-3000).setDuration(500);

                }
                else if(s.length()==0)
                {
                    enableXtransformation = false;
                    textListner_msg_three=false;
                }

            }
        });



        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build();

            soundPool = new SoundPool.Builder()
                    .setMaxStreams(1)
                    .setAudioAttributes(audioAttributes)
                    .build();

        } else {
            soundPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
        }

        username = view.findViewById(R.id.username);

        message_text = view.findViewById(R.id.message_text);


        message_text_dummy = view.findViewById(R.id.message_text_dummy);

        message_text_two = view.findViewById(R.id.message_text_two);
        message_text_three = view.findViewById(R.id.message_text_three);

        your_sent_msg = view.findViewById(R.id.your_sent_msg);

        read_message_reply = view.findViewById(R.id.read_message_reply);


        chainBtn = view.findViewById(R.id.chain_message_btn);


        username.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), ViewProfileActivity.class);


                intent.putExtra("username", usernameChattingWith);
                intent.putExtra("name", usernameChattingWith);
                intent.putExtra("userId", userIdChattingWith);
                intent.putExtra("currentUsername", currentUsername);
                intent.putExtra("photourl", photo);
                intent.putExtra("friendStatus", "friends");


                getContext().startActivity(intent);
            }
        });

        chainBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (message_box.getText().toString().length() > 0) {
                    if (typingID == MSG_TYPING_BOX_ID) {
                        soundPool.play(sendingSound5, 1, 1, 0, 0, 1);


                        messageStr = message_box.getText().toString();
                        message_text_sender.setText(message_box.getText());
                        typingID = MSG_TYPING_BOX_TWO_ID;
                        message_box.setText("");
                        message_text_two_sender.setVisibility(View.VISIBLE);

                        message_text_sender_dummy.setVisibility(View.GONE);
                        message_text_sender.setAlpha(0.5f);

                        // message_text_sender.setTextColor(Color.parseColor("#e6e200"));

                        // message_text_two_sender.setLayoutParams;

                        chainBtn.setVisibility(View.GONE);
                        chain_pulse.setVisibility(View.GONE);
                        Toast.makeText(getContext(), "Chained", Toast.LENGTH_SHORT).show();

                    } else if (typingID == MSG_TYPING_BOX_TWO_ID) {
                        soundPool.play(sendingSound5, 1, 1, 0, 0, 1);

                        messageStrTwo = message_box.getText().toString();
                        message_text_two_sender.setText(message_box.getText());
                        typingID = MSG_TYPING_BOX_THREE_ID;
                        message_box.setText("");
                        message_text_three_sender.setVisibility(View.VISIBLE);


                        message_text_two_sender.setAlpha(0.5f);
                        // message_text_two_sender.setTextColor(Color.parseColor("#e6e200"));


                        chainBtn.setVisibility(View.GONE);
                        chain_pulse.setVisibility(View.GONE);
                        Toast.makeText(getContext(), "Chained", Toast.LENGTH_SHORT).show();

                    }
                }
            }
        });



        message_text_sender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                switch (typingID) {
                    case 1:


                        message_text_sender.setAlpha(0.5f);

                        messageStr = message_box.getText().toString();
                        break;

                    case 2:

                        if (message_text_two_sender.getText().length() > 0) {

                            chainBtn.setVisibility(View.GONE);
                            chain_pulse.setVisibility(View.GONE);

                        }


                        message_text_two_sender.setAlpha(0.5f);
                        messageStrTwo = message_box.getText().toString();

                        break;

                    case 3:


                        if (message_text_two_sender.getText().length() > 0) {

                            chainBtn.setVisibility(View.GONE);
                            chain_pulse.setVisibility(View.GONE);

                        }

                        if (message_text_three_sender.getText().length() <= 0)
                            message_text_three_sender.setVisibility(View.GONE);

                        message_text_three_sender.setAlpha(0.5f);
                        messageStrThree = message_box.getText().toString();
                        break;

                }
                typingID = MSG_TYPING_BOX_ID;
                message_text_sender.setFocusable(true);
                message_text_sender.setAlpha(1.0f);

                message_box.setText(message_text_sender.getText());


            }
        });



        message_text_two_sender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                switch (typingID) {
                    case 1:


                        message_text_sender.setAlpha(0.5f);

                        messageStr = message_box.getText().toString();
                        break;

                    case 2:


                        message_text_two_sender.setAlpha(0.5f);
                        messageStrTwo = message_box.getText().toString();

                        break;

                    case 3:
                        if (message_text_three_sender.getText().length() <= 0)
                            message_text_three_sender.setVisibility(View.GONE);

                        message_text_three_sender.setAlpha(0.5f);
                        messageStrThree = message_box.getText().toString();
                        break;

                }


                typingID = MSG_TYPING_BOX_TWO_ID;
                message_text_two_sender.setFocusable(true);
                message_text_two_sender.setAlpha(1.0f);

                message_box.setText(message_text_two_sender.getText());
            }
        });


        message_text_three_sender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                switch (typingID) {
                    case 1:
                        message_text_sender.setAlpha(0.5f);

                        messageStr = message_box.getText().toString();
                        break;

                    case 2:
                        message_text_two_sender.setAlpha(0.5f);
                        messageStrTwo = message_box.getText().toString();

                        break;

                    case 3:
                        message_text_three_sender.setAlpha(0.5f);
                        messageStrThree = message_box.getText().toString();
                        break;

                }


                typingID = MSG_TYPING_BOX_THREE_ID;
                message_text_three_sender.setFocusable(true);
                message_text_three_sender.setAlpha(1.0f);
                message_box.setText(message_text_three_sender.getText());
            }
        });

        message_text_typewriter = view.findViewById(R.id.message_text_type_writer);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getActivity().getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.BLACK);
        }

        sendButton = view.findViewById(R.id.send_button);

        sendButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {


                if(event.getAction() == MotionEvent.ACTION_DOWN && sendButtonEnabled)
                {
                    sendButton.animate().scaleX(1.25f).scaleY(1.25f).setDuration(100);
                    soundPool.play(sendSound3, 1, 1, 0, 0, 1);
                }

                else
                {
                    sendButton.animate().scaleX(1.0f).scaleY(1.0f).setDuration(300);
                }

                return false;
            }
        });

        sender_reply_tag = view.findViewById(R.id.sender_reply_tag);


        bottom_bar = view.findViewById(R.id.bottom_bar);

        message_space = view.findViewById(R.id.message_space);

        reply_tag = view.findViewById(R.id.reply_tag);

        message_counter_layout = view.findViewById(R.id.message_counter_layout);

        message_bar = view.findViewById(R.id.message_bar);

        message_counter = view.findViewById(R.id.message_counter);

        window = getActivity().getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

        message_counter_background = view.findViewById(R.id.message_counter_background);

        message_no_message_counter_background = view.findViewById(R.id.message_no_message_counter_background);


        read_message_reply_layout = view.findViewById(R.id.read_message_reply_layout);

        profile_pic = view.findViewById(R.id.profile_pic);




        sender_reply_body = view.findViewById(R.id.sender_reply_body);


        profile_pic.setBlur(0);

        message_pulse = view.findViewById(R.id.message_pulse);
        message_log_pulse = view.findViewById(R.id.message_log_pulse);

        sending_progress_bar = view.findViewById(R.id.send_pending);

        chain_pulse = view.findViewById(R.id.chain_pulse);

        archive_btn = view.findViewById(R.id.archive);

        back_button = view.findViewById(R.id.back_button);

        reply_btn = view.findViewById(R.id.reply_message_btn);

        cancel_reply_btn = view.findViewById(R.id.cancel_message_reply_btn);

        send_arrow = view.findViewById(R.id.send_arrow);

        receiver_arrow = view.findViewById(R.id.receive_arrow);

        send_arrow_two = view.findViewById(R.id.send_arrow_two);

        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                //startActivity(new Intent(getActivity(), NavButtonTest.class));
                getActivity().finish();

            }
        });

        swipeButton = view.findViewById(R.id.swipeBtn);


        try {
            UsernameObjectAnimator.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {

                }

                @Override
                public void onAnimationEnd(Animator animation) {

                }

                @Override
                public void onAnimationCancel(Animator animation) {


                    username.setAlpha(0.0f);
                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });
        }catch (Exception e)
        {

        }


/*
        swipeButton.setOnStateChangeListener(new OnStateChangeListener() {
            @Override
            public void onStateChange(boolean active) {
                if (active) {
                    //   swipeState = active;
                    setChainBtn = true;
                    if (!isMessageRunning)
                        readNextMessage();
                    try {
                        swipeButton.setBackground(getActivity().getDrawable(R.drawable.message_swipe_bg));
                    }catch (Exception e)
                    {
                        Toast.makeText(getContext(),"Swipe Button Null pointer",Toast.LENGTH_SHORT).show();
                    }
                    Toast.makeText(getContext(), "Message chained", Toast.LENGTH_SHORT).show();
                } else {
                    setChainBtn = false;
                    Toast.makeText(getContext(), "Message de-chained!", Toast.LENGTH_SHORT).show();
                }

            }
        });
*/

        switchBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    //   swipeState = active;
                    setChainBtn = true;
                    if (!isMessageRunning)
                        readNextMessage();
/*                    try {
                        swipeButton.setBackground(getActivity().getDrawable(R.drawable.message_swipe_bg));
                    }catch (Exception e)
                    {
                        Toast.makeText(getContext(),"Swipe Button Null pointer",Toast.LENGTH_SHORT).show();
                    }*/
                    Toast.makeText(getContext(), "Message chained", Toast.LENGTH_SHORT).show();
                } else {
                    setChainBtn = false;
                    Toast.makeText(getContext(), "Message de-chained!", Toast.LENGTH_SHORT).show();
                }

            }
        });


        photo_btn = view.findViewById(R.id.photo_btn);

        photo_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(photoMessageList.size()>0) {

                    ChatMessage chatMessage = photoMessageList.get(0);

                    if (!chatMessage.getPhotourl().equals("none")) {

                        Intent intent = new Intent(getActivity(), EphemeralPhotoActivity.class);
                        intent.putExtra("userIdChattingWith", userIdChattingWith);
                        intent.putExtra("photoUrl", chatMessage.getPhotourl());
                        intent.putExtra("mKey", chatMessage.getMessageId());
                        intent.putExtra("sender", chatMessage.getSenderId());
                        intent.putExtra("currentUserPublicKeyID", currentUserPublicKeyID);
                        intent.putExtra("publicKeyID", chatMessage.getPublicKeyID());
                        intent.putExtra("currentUserPrivateKey", currentUserPrivateKey);
                        intent.putExtra("mediaKey", chatMessage.getMediaKey());


                        startActivity(intent);
                    } else if (!chatMessage.getAudiourl().equals("none")) {
                        Intent intent = new Intent(getActivity(), AudioRecorderActivity.class);
                        intent.putExtra("userIdChattingWith", userIdChattingWith);
                        intent.putExtra("audiourl", chatMessage.getAudiourl());
                        intent.putExtra("mKey", chatMessage.getMessageId());
                        intent.putExtra("sender", chatMessage.getSenderId());
                        intent.putExtra("currentUserPublicKeyID", currentUserPublicKeyID);
                        intent.putExtra("publicKeyID", chatMessage.getPublicKeyID());
                        intent.putExtra("currentUserPrivateKey", currentUserPrivateKey);
                        intent.putExtra("mediaKey", chatMessage.getMediaKey());


                        startActivity(intent);
                    } else if (!chatMessage.getVideourl().equals("none")) {
                        Intent intent = new Intent(getActivity(), VideoPlayActivity.class);

                        intent.putExtra("userIdChattingWith", userIdChattingWith);
                        intent.putExtra("videoUrl", chatMessage.getVideourl());
                        intent.putExtra("mKey", chatMessage.getMessageId());
                        intent.putExtra("sender", chatMessage.getSenderId());
                        intent.putExtra("currentUserPublicKeyID", currentUserPublicKeyID);
                        intent.putExtra("publicKeyID", chatMessage.getPublicKeyID());
                        intent.putExtra("currentUserPrivateKey", currentUserPrivateKey);
                        intent.putExtra("mediaKey", chatMessage.getMediaKey());


                        startActivity(intent);
                    }
                }
                else
                {
                    Toast.makeText(getActivity(),"You have no photo messages",Toast.LENGTH_SHORT).show();
                }
            }
        });


        messageKeySender = FirebaseAuth.getInstance().getUid() + userIdChattingWith;
        messageKeyReceiver = userIdChattingWith + FirebaseAuth.getInstance().getUid();

        setAnimationSendingDuration = 600;

/*        reply_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (!lastMessageID.equals("none")) {
                    setChainBtn = false;
                    switchBtn.setChecked(false);
                    isSenderReplying = true;
                    reply_tag.setVisibility(View.VISIBLE);
                    reply_btn.setVisibility(View.GONE);
                    message_box.requestFocus();
                    InputMethodManager inputMethodManager =
                            (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputMethodManager.toggleSoftInputFromWindow(
                            app_context_layout.getApplicationWindowToken(),
                            InputMethodManager.SHOW_FORCED, 0);
                }


            }
        });*/

/*        cancel_reply_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isSenderReplying = false;
                reply_tag.setVisibility(View.GONE);
                reply_btn.setVisibility(View.VISIBLE);
            }
        });*/

        messageList = new ArrayList<>();
        sendMessageList = new ArrayList<>();
        photoMessageList = new ArrayList<>();


        before_count = messageList.size();


        //send_arrow.setTranslationY(0);
        send_arrow_two.setTranslationY(0);


        DisplayMetrics displayMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        final int height = displayMetrics.heightPixels;
        final int width = displayMetrics.widthPixels;


        //setting the opacity of profile pic when message is being read..
        setProfilePicOpacity(0.05f);
        /*if(photo.equals("default"))
            profile_pic.setImageResource(R.drawable.profile_pic);*/


        //  delSound1 = soundPool.load(this,R.raw.communication_channel,1 );
        delSound1 = soundPool.load(getContext(), R.raw.forsure, 1);
        cantDelSound2 = soundPool.load(getContext(), R.raw.knuckle, 1);
        sendSound3 = soundPool.load(getContext(),R.raw.show_stopper,1);
        //sendSound3 = soundPool.load(getContext(), R.raw.justmaybe, 1);
        sendingSound4 = soundPool.load(getContext(), R.raw.youwouldntbelieve, 1);

        sendingSound5 = soundPool.load(getContext(), R.raw.knob, 1);


        try {
            if (!photo.equals("default")) {
               /*

                Glide.with(getActivity()).load(photo*//*.replace("s96-c", "s384-c")*//*)*//*.transition(withCrossFade())*//*.apply(new RequestOptions().override(width, height)).listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {


                        if (imageReady) {
                            {
                                profile_pic.animate().alpha(1.0f).setDuration(750);
                                *//*final Matrix matrix = profile_pic.getImageMatrix();
                                final float imageWidth = resource.getIntrinsicWidth();
                                final int screenWidth = getContext().getResources().getDisplayMetrics().widthPixels/2;
                                final float scaleRatio = screenWidth / imageWidth;
                                matrix.postScale(scaleRatio, scaleRatio);*//*
                            }

                            imageLoaded = true;


                            //   read_message_back.animate().alpha(0.0f);



                        }


                        return false;
                    }
                }).into(profile_pic);

                */
                TaskCompletionSource<Bitmap> bitmapTaskCompletionSource = new TaskCompletionSource<>();

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Bitmap bitmap = Glide.with(getContext())
                                    .asBitmap()
                                    .load(photo.replace("s96-c", "s384-c"))
                                    .submit(width, height)
                                    .get();

                            bitmapTaskCompletionSource.setResult(bitmap);
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }


                    }
                }).start();

                Task<Bitmap> bitmapTask = bitmapTaskCompletionSource.getTask();

                bitmapTask.addOnCompleteListener(new OnCompleteListener<Bitmap>() {
                    @Override
                    public void onComplete(@NonNull Task<Bitmap> task) {
                        if (task.isSuccessful()) {
                            try {


                                Glide.with(getActivity()).load(task.getResult()/*.replace("s96-c", "s384-c")*/)/*.transition(withCrossFade())*//*.apply(new RequestOptions().override(width, height))*/.listener(new RequestListener<Drawable>() {
                                    @Override
                                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                        return false;
                                    }

                                    @Override
                                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {


                                        if (imageReady) {
                                            {
                                                profile_pic.animate().alpha(1.0f).setDuration(750);
                                /*final Matrix matrix = profile_pic.getImageMatrix();
                                final float imageWidth = resource.getIntrinsicWidth();
                                final int screenWidth = getContext().getResources().getDisplayMetrics().widthPixels/2;
                                final float scaleRatio = screenWidth / imageWidth;
                                matrix.postScale(scaleRatio, scaleRatio);*/
                                            }

                                            imageLoaded = true;


                                            //   read_message_back.animate().alpha(0.0f);


                                        }


                                        return false;
                                    }
                                }).into(profile_pic);
                            } catch (NullPointerException ne) {
                                //
                            }

                        }

                    }
                });




            }
            else
            {
                profile_pic.setImageResource(R.drawable.no_profile);
                profile_pic.animate().alpha(alphaValueProfilePic).setDuration(750);
            }
        }catch (Exception e)
        {
            profile_pic.setImageDrawable(getActivity().getDrawable(R.drawable.no_profile));
        }

        archive_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Toast.makeText(getActivity(),"Sending Message...",Toast.LENGTH_SHORT).show();

            }
        });



        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        if (dataSnapshot.exists()) {

                            User user = dataSnapshot.getValue(User.class);

                            currentUsername = user.getUsername();
                            currentUserPhoto = user.getPhoto();

                            CURRENT_USERNAME = currentUsername;
                            CURRENT_USERPHOTO = currentUserPhoto;



                    /* = user.getPublicKey();
                    otherUserPublicKeyID = user.getPublicKeyId();*/

                        }
                    }
                }).start();



            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        EphemeralMessageActivityViewModelFactory ephemeralMessageActivityViewModelFactory = new EphemeralMessageActivityViewModelFactory(userIdChattingWith);
        ephemeralMessageViewModel = ViewModelProviders.of(this, ephemeralMessageActivityViewModelFactory).get(EphemeralMessageViewModel.class);
        ephemeralMessageViewModel.getAllMessages().observe(getViewLifecycleOwner(), new Observer<List<ChatMessage>>() {
            @Override
            public void onChanged(@Nullable List<ChatMessage> chatMessageList) {

                notificationIDHashMap.put(userIdChattingWith + "numbersent", 0);
                notificationIDHashMap.put(userIdChattingWith + "numberreply", 0);

                /*try {

                    updateChatList(messageList.get(messageList.size() - 1));
                }catch (Exception e)
                {
                    //
                }*/


                //Testing purpose only
                // messageList = chatMessageList;



                /**
                 * Uncomment the following line to only read received messages and not sent messages.
                 */
                filterList(chatMessageList);


                /*if(after_count>before_count) {
                    soundPool.play(sendingSound4, 1, 1, 0, 0, 1);


                    before_count = after_count;
                }

                else
                    after_count = messageList.size();*/



                //    filterList(chatMessageList);
                // threadPending = ephemeralMessageViewModel.isThreadRunning();
                // setIsMessageRunning(threadPending);


            }
        });


        UserPicViewModelFactory userPicViewModelFactory = new UserPicViewModelFactory(userIdChattingWith);
       UserPicViewModel userPicViewModel = ViewModelProviders.of(this, userPicViewModelFactory).get(UserPicViewModel.class);
        userPicViewModel.getUserPic().observe(getViewLifecycleOwner(), new Observer<User>() {
            @Override
            public void onChanged(@Nullable final User user) {


                        try {
                            if (user.getOnline())
                            {


                                // try{

                                online_ring.animate().alpha(1.0f).setDuration(500);
                                //online_status.animate().alpha(1.0f).setDuration(500);
                            }
                            else
                            {
                                online_ring.animate().alpha(0.0f).setDuration(500);
                                //online_status.animate().alpha(0.0f).setDuration(500);
                            }

                        }catch (Exception e)
                        {

                        }





            }
        });

             /*message_box.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                message_box.setEnabled(true);
            }
        });*/


        //setting the font style of message text
        /*Typeface custom_font = Typeface.createFromAsset(getAssets(),  "fonts/samsungsharpsans-bold.otf");
        username.setTypeface(custom_font);*/


        message_box.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                // message_text_sender.setText("");
                /*

                if (s.toString().trim().length() > 0) {

                    if(!getIsMessageRunning()) {
                        sendButton.setEnabled(true);
                        message_text_sender.setText(s);
                        message_text_sender.setAlpha(0.5f);
                        message_bar.setBackground(getDrawable(R.drawable.message_identifier_sender_drawable));
                        profile_pic.setBlur(7);
                        profile_pic.setAlpha(0.085f);
                    }
                } else {
                    if(!getIsMessageRunning()) {
                        profile_pic.setAlpha(0.5f);
                        profile_pic.setBlur(0);
                      //  message_text_sender.setAlpha(0.5f);
                        if(messageList.size()<1)
                            message_bar.setBackground(getDrawable(R.drawable.message_identifier_no_message));
                        else
                            message_bar.setBackground(getDrawable(R.drawable.message_identifier_reply_drawable));

                    }
                    sendButton.setEnabled(false);
                }*/


                if (s.toString().trim().length() > 0) {

                    textSizeseekBar.setTranslationX(0);
                    textSizeseekBar.setVisibility(View.VISIBLE);

                   // textSizeseekBar.animate().translationXBy(2000).setDuration(500);

                    alphaValueProfilePic = 0.0f;

                    profile_pic.setAlpha(0.0f);
                    imageReady = false;
                    sendButton.setVisibility(View.VISIBLE);
                    sendButton.setAlpha(1.0f);

                    recordView.setVisibility(View.GONE);
                    recordButton.setVisibility(View.GONE);
                    if (isDisablerecord_button)
                        disablerecord_button.setVisibility(View.GONE);

                    if (!getIsMessageRunning()) {


                        sendButton.setEnabled(true);
                        sendButtonEnabled = true;
                        switch (typingID) {
                            case 1:
                                message_text_sender.setAlpha(1.0f);
                                message_text_sender.setText(s);
                                if (message_text_three_sender.getText().length() <= 0 && message_text_two_sender.getText().length() <= 0) {
                                    chainBtn.setVisibility(View.VISIBLE);
                                    chain_pulse.setVisibility(View.VISIBLE);
                                }
                                break;

                            case 2:
                                message_text_two_sender.setAlpha(1.0f);
                                message_text_two_sender.setText(s);
                                if (message_text_three_sender.getText().length() <= 0) {
                                    chainBtn.setVisibility(View.VISIBLE);
                                    chain_pulse.setVisibility(View.VISIBLE);
                                }
                                break;

                            case 3:
                                message_text_three_sender.setAlpha(1.0f);
                                message_text_three_sender.setText(s);
                                break;

                        }
                        message_bar.setBackground(getActivity().getDrawable(R.drawable.message_identifier_sender_drawable));

                        profile_pic.setAlpha(0.0f);
                        // read_message_back.animate().alpha(1.0f).setDuration(750);
                        //read_message_back

                        if (imageLoaded)
                            profile_pic.setBlur(4);


                    }


                }
                else
                {
                    alphaValueProfilePic = 1.0f;
                    textSizeseekBar.setVisibility(View.INVISIBLE);

                }

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (s.toString().trim().length() > 0) {

                    textSizeseekBar.setTranslationX(0);
                    textSizeseekBar.setVisibility(View.VISIBLE);

                    //textSizeseekBar.animate().translationXBy(2000).setDuration(500);
                }
                else {
                    textSizeseekBar.setVisibility(View.INVISIBLE);
                }


                // message_text_sender.setText("");
                if (!sendButtonPressed)
                {


                message_text_sender.setTranslationY(0);
                message_text_two_sender.setTranslationY(0);
                message_text_three_sender.setTranslationY(0);


                //  message_text_sender.animateText(s);


                if (s.toString().trim().length() > 0) {

                    alphaValueProfilePic = 0.0f;
                    imageReady = false;
                    sendButton.setAlpha(1.0f);
                    sendButton.setVisibility(View.VISIBLE);

                    recordView.setVisibility(View.GONE);
                    recordButton.setVisibility(View.GONE);
                    if (isDisablerecord_button)
                        disablerecord_button.setVisibility(View.GONE);

                    if (!getIsMessageRunning()) {


                        sendButton.setEnabled(true);
                        sendButtonEnabled = true;
                        switch (typingID) {
                            case 1:
                                message_text_sender.setAlpha(1.0f);
                                message_text_sender.setText(s);
                                if (message_text_three_sender.getText().length() <= 0 && message_text_two_sender.getText().length() <= 0) {
                                    chainBtn.setVisibility(View.VISIBLE);
                                    chain_pulse.setVisibility(View.VISIBLE);
                                }
                                break;

                            case 2:
                                message_text_two_sender.setAlpha(1.0f);
                                message_text_two_sender.setText(s);
                                if (message_text_three_sender.getText().length() <= 0) {
                                    chainBtn.setVisibility(View.VISIBLE);
                                    chain_pulse.setVisibility(View.VISIBLE);
                                }
                                break;

                            case 3:
                                message_text_three_sender.setAlpha(1.0f);
                                message_text_three_sender.setText(s);
                                break;

                        }
                        message_bar.setBackground(getActivity().getDrawable(R.drawable.message_identifier_sender_drawable));

                        profile_pic.animate().alpha(opacity).setDuration(250);
                       // read_message_back.animate().alpha(1.0f).setDuration(750);
                        //read_message_back

                        if (imageLoaded)
                            profile_pic.setBlur(4);


                    }


                } else if (s.toString().trim().length() == 0 && typingID == MSG_TYPING_BOX_ID) {
                    if (!getIsMessageRunning()) {
                        // message_text_sender.setAlpha(0.0f);

                        alphaValueProfilePic = 1.0f;
                        //  imageReady = false;
                        chainBtn.setVisibility(View.GONE);
                        chain_pulse.setVisibility(View.GONE);
                        sendButton.setAlpha(0.15f);
                        sendButton.setVisibility(View.INVISIBLE);

                        if (isDisablerecord_button)
                            disablerecord_button.setVisibility(View.VISIBLE);
                        else {

                            recordView.setVisibility(View.VISIBLE);
                            recordButton.setVisibility(View.VISIBLE);
                        }


                        profile_pic.animate().alpha(alphaValueProfilePic).setDuration(setAnimationSendingDuration /** 3 + 650*/);


                        if (imageLoaded)
                            profile_pic.setBlur(0);


                        // profile_pic.setBlur(0);
                        if (messageList.size() < 1)
                            message_bar.setBackground(getActivity().getDrawable(R.drawable.message_identifier_no_message));
                        else
                            message_bar.setBackground(getActivity().getDrawable(R.drawable.message_identifier_reply_drawable));

                        //message_text_sender.setText("");

                    }

                    switch (typingID) {


                        case 1:
                            sendButton.setEnabled(false);
                            sendButtonEnabled = false;
                            //  message_text_sender.setText(s);
                            message_text_sender.animate().alpha(0.0f).setDuration(250);
                            break;

                        case 2:
                            typingID = 1;
                            break;

                        case 3:
                            typingID = 2;
                            break;

                    }


                } else {
                    switch (typingID) {


                        case MSG_TYPING_BOX_ID:
                            message_text_sender.setText("");
                            break;

                        case MSG_TYPING_BOX_TWO_ID:
                            message_text_two_sender.setText("");
                            break;

                        case MSG_TYPING_BOX_THREE_ID:
                            message_text_three_sender.setText("");
                            break;

                    }
                }


            }
                else
                {
                    if (s.toString().trim().length() > 0) {

                        alphaValueProfilePic = 0.0f;
                    }
                    else
                    {
                        alphaValueProfilePic = 1.0f;
                    }
                }


            }

            @Override
            public void afterTextChanged(Editable s) {


                if (s.toString().trim().length() > 0) {

                    textSizeseekBar.setTranslationX(0);
                    textSizeseekBar.setVisibility(View.VISIBLE);
                    //textSizeseekBar.animate().translationX(2000).setDuration(500);
                }
                else {
                    textSizeseekBar.setVisibility(View.INVISIBLE);
                }

                if (!sendButtonPressed)
                {
                    // message_text_sender.setText("");

                    if (s.toString().trim().length() > 0) {

                        alphaValueProfilePic = 0.0f;
                    }
                    else {
                        alphaValueProfilePic = 1.0f;
                    }
/*

                if (s.toString().trim().length() > 0) {


                    if(!getIsMessageRunning()) {
                        sendButton.setEnabled(true);
                        message_text_sender.setText(s);
                        message_text_sender.setAlpha(0.5f);
                        message_bar.setBackground(getDrawable(R.drawable.message_identifier_sender_drawable));
                        profile_pic.setBlur(7);
                        profile_pic.setAlpha(0.085f);
                    }

                } else {
                    if(!getIsMessageRunning()) {
                        profile_pic.setBlur(0);
                        profile_pic.setAlpha(0.5f);
                      //  message_text_sender.setAlpha(0.0f);
                        if(messageList.size()<1)
                            message_bar.setBackground(getDrawable(R.drawable.message_identifier_no_message));
                        else
                            message_bar.setBackground(getDrawable(R.drawable.message_identifier_reply_drawable));

                    }
                    sendButton.setEnabled(false);

                }

*/
                    if (typingID == 1) {
                        if (s.length() == 0) {
                            if (notSending)
                                message_text_sender.setText("");
                        }
                    }
            }
                else
                {
                    if (s.toString().trim().length() > 0) {

                        alphaValueProfilePic = 0.0f;
                    }
                    else
                    {
                        alphaValueProfilePic = 1.0f;
                    }
                }

            }
        });

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                animateArrow();
               // sendMessage();
            }
        });


        username.setText(usernameChattingWith);


        // message_space.setOnTouchListener(this);

        // message_counter.setOnTouchListener(this);

        // message_counter.setOnTouchListener(this);


/*        message_counter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!getIsMessageRunning()) {

                    readNextMessage();

                }
            }
        });*/


        message_counter.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    if (!getIsMessageRunning()) {

                        readNextMessage();

                        setChainBtn = true;

                        message_running.setVisibility(View.VISIBLE);
                        message_running_two.setVisibility(View.VISIBLE);

                    }
                    // start your timer

                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    // stop your timer.

                    setChainBtn = false;

                    message_running.setVisibility(View.INVISIBLE);
                    message_running_two.setVisibility(View.INVISIBLE);
                    //Toast.makeText(getContext(),"Button Released!",Toast.LENGTH_SHORT).show();

                }






                return false;
            }
        });


        //   blink();

        // readNextMessage();

        /*new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(messageList.size()<1)
                    ephemeralMessageViewModel.setChatPending(false);
            }
        },1000);*/

        setUpBlink();
        checkPresence();


        String samsungBold = "fonts/samsungsharpsans-bold.otf";
        String samsungMedium = "fonts/samsungsharpsans-medium.otf";
        String samsung = "fonts/samsungsharpsans.otf";

        String primeNovaBlack = "fonts/ProximaNova-Black.ttf";
        String primeNovaBold = "fonts/ProximaNova-Bold.ttf";
        String primeNovaExtrabld = "fonts/ProximaNova-Extrabld.ttf";
        String primeNovaLight = "fonts/ProximaNova-Light.ttf";
        String primeNovaRegular = "fonts/ProximaNova-Regular.ttf";
        String primeNovaSemiBold = "fonts/ProximaNova-Semibold.ttf";
        String primeNovaThin = "fonts/ProximaNovaT-Thin.ttf";

        String helvetica = "fonts/Helvetica.ttf";
        String helvecticaBold = "fonts/Helvetica-Bold.ttf";
        String helvecticaBoldOblique = "fonts/Helvetica-BoldOblique.ttf";
        String helvecticaOblique = "fonts/Helvetica-Oblique.ttf";


        setTypeFace(samsungBold,"null");

        checkForHint();



       // updateUserPresence(false);

        return view;
    }

    private void requestAudioPermisson() {



        /*Dexter.withActivity(getActivity())
                .withPermission(Manifest.permission.RECORD_AUDIO)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        disablerecord_button.setVisibility(View.GONE);
                        recordButton.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {

                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {

                    }
                }).check();*/

        Dexter.withActivity(getActivity())
                .withPermissions(Manifest.permission.RECORD_AUDIO)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {

                        if (report.areAllPermissionsGranted()) {
                            disablerecord_button.setVisibility(View.GONE);
                            recordButton.setVisibility(View.VISIBLE);
                        } else {
                            Toast.makeText(getActivity(), "Please give audio record permission", Toast.LENGTH_SHORT).show();
                        }

                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {

                        token.continuePermissionRequest();

                        // Toast.makeText(getActivity(), "Permission Denied!", Toast.LENGTH_SHORT).show();
                    }
                }).check();
    }

    private ChatMessageCompound setUpSendingChat()
    {
        if (typingID == MSG_TYPING_BOX_ID)
            messageStr = message_box.getText().toString();
        else if (typingID == MSG_TYPING_BOX_TWO_ID)
            messageStrTwo = message_box.getText().toString();
        else
            messageStrThree = message_box.getText().toString();
        typingID = MSG_TYPING_BOX_ID;

        reply_tag.setVisibility(View.GONE);
        reply_btn.setVisibility(View.GONE);

        String receiverCopyMessage = encryptRSAToString(messageStr,otherUserPublicKey);



        ChatMessageCompound chatMessageCompound = new ChatMessageCompound();


        ChatMessage chatMessageReceiverCopy = new ChatMessage();
        ChatMessage chatMessageSenderCopy = new ChatMessage();
        Boolean setIfMessageTwo = false, setIfMessageThree = false;
        String setMessageTextTwo;
        String setMessageTextThree;
        String senderCopyMessage = encryptRSAToString(messageStr,currentUserPublicKey);
        String senderCopyMessageTwo = null, senderCopyMessageThree = null;

        if (messageStrTwo.length() > 0) {
            setIfMessageTwo = true;
            setMessageTextTwo = encryptRSAToString(messageStrTwo,otherUserPublicKey);
            senderCopyMessageTwo = encryptRSAToString(messageStrTwo,currentUserPublicKey);

            /*if(!otherUserPublicKey.equals(null))
              Toast.makeText(getContext(),"Encrypted", Toast.LENGTH_SHORT).show();*/
        } else {
            setMessageTextTwo = "";
            senderCopyMessageTwo = "";
        }

        if (messageStrThree.length() > 0) {
            setIfMessageThree = true;
            setMessageTextThree = encryptRSAToString(messageStrThree,otherUserPublicKey);
            senderCopyMessageThree = encryptRSAToString(messageStrThree,currentUserPublicKey);
        } else {
            setMessageTextThree = "";
            senderCopyMessageThree = "";
        }

        Timestamp timestamp = new Timestamp(System.currentTimeMillis());

        chatMessageReceiverCopy.setMessageId("notAssigned");
        chatMessageReceiverCopy.setPublicKeyID(otherUserPublicKeyID);
        chatMessageReceiverCopy.setMessageText(receiverCopyMessage);
        chatMessageReceiverCopy.setSenderId("notAssigned");
        chatMessageReceiverCopy.setReceiverId("notAssigned");
        chatMessageReceiverCopy.setSentStatus("sending");
        chatMessageReceiverCopy.setSeen("notseen");
        chatMessageReceiverCopy.setPhotourl("none");
        chatMessageReceiverCopy.setVideourl("none");
        chatMessageReceiverCopy.setAudiourl("none");
        chatMessageReceiverCopy.setGotReplyID("none");
        chatMessageReceiverCopy.setReplyID(lastMessageID);
        chatMessageReceiverCopy.setTextSize(sendTextSize);
        chatMessageReceiverCopy.setReplyTag(false);
        chatMessageReceiverCopy.setSenderReplyMessage("none");
        chatMessageReceiverCopy.setIfMessageTwo(setIfMessageTwo);
        chatMessageReceiverCopy.setMessageTextTwo(setMessageTextTwo);
        chatMessageReceiverCopy.setIfMessageThree(setIfMessageThree);
        chatMessageReceiverCopy.setMessageTextThree(setMessageTextThree);
        chatMessageReceiverCopy.setShowReplyMsg(false);
        chatMessageReceiverCopy.setShowGotReplyMsg(false);
        chatMessageReceiverCopy.setGotReplyMsg(" ");
        chatMessageReceiverCopy.setReplyMsg(" ");
        chatMessageReceiverCopy.setTimeStamp(timestamp.toString());


        chatMessageSenderCopy.setMessageId("notAssigned");
        chatMessageSenderCopy.setPublicKeyID(currentUserPublicKeyID);
        chatMessageSenderCopy.setMessageText(senderCopyMessage);
        chatMessageSenderCopy.setSenderId("notAssigned");
        chatMessageSenderCopy.setReceiverId("notAssigned");
        chatMessageSenderCopy.setSentStatus("sending");
        chatMessageSenderCopy.setSeen("notseen");
        chatMessageSenderCopy.setPhotourl("none");
        chatMessageSenderCopy.setVideourl("none");
        chatMessageSenderCopy.setAudiourl("none");
        chatMessageSenderCopy.setGotReplyID("none");
        chatMessageSenderCopy.setReplyID(lastMessageID);
        chatMessageSenderCopy.setTextSize(sendTextSize);
        chatMessageSenderCopy.setReplyTag(false);
        chatMessageSenderCopy.setSenderReplyMessage("none");
        chatMessageSenderCopy.setIfMessageTwo(setIfMessageTwo);
        chatMessageSenderCopy.setMessageTextTwo(senderCopyMessageTwo);
        chatMessageSenderCopy.setIfMessageThree(setIfMessageThree);
        chatMessageSenderCopy.setMessageTextThree(senderCopyMessageThree);
        chatMessageSenderCopy.setShowReplyMsg(false);
        chatMessageSenderCopy.setShowGotReplyMsg(false);
        chatMessageSenderCopy.setGotReplyMsg(" ");
        chatMessageSenderCopy.setReplyMsg(" ");
        chatMessageSenderCopy.setTimeStamp(timestamp.toString());




        message_box.setText("");

        lastMessageID = "none";

        chatMessageCompound.setChatMessageReceiverCopy(chatMessageReceiverCopy);
        chatMessageCompound.setChatMessageSenderCopy(chatMessageSenderCopy);


        return chatMessageCompound;
    }

    private String encryptRSAToString(String clearText, String publicKey) {
        String encryptedBase64 = "";
        try {
            KeyFactory keyFac = KeyFactory.getInstance("RSA");
            KeySpec keySpec = new X509EncodedKeySpec(Base64.decode(publicKey.trim().getBytes(), Base64.DEFAULT));
            Key key = keyFac.generatePublic(keySpec);

            // get an RSA cipher object and print the provider
            final Cipher cipher = Cipher.getInstance("RSA/ECB/OAEPWITHSHA-256ANDMGF1PADDING");
            // encrypt the plain text using the public key
            cipher.init(Cipher.ENCRYPT_MODE, key);

            byte[] encryptedBytes = cipher.doFinal(clearText.getBytes("UTF-8"));
            encryptedBase64 = new String(Base64.encode(encryptedBytes, Base64.DEFAULT));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return encryptedBase64.replaceAll("(\\r|\\n)", "");
    }

    private String decryptRSAToString(String encryptedBase64, String privateKey) {

        String decryptedString = "";
        try {
            KeyFactory keyFac = KeyFactory.getInstance("RSA");
            KeySpec keySpec = new PKCS8EncodedKeySpec(Base64.decode(privateKey.trim().getBytes(), Base64.DEFAULT));
            Key key = keyFac.generatePrivate(keySpec);

            // get an RSA cipher object and print the provider
            final Cipher cipher = Cipher.getInstance("RSA/ECB/OAEPWITHSHA-256ANDMGF1PADDING");
            // encrypt the plain text using the public key
            cipher.init(Cipher.DECRYPT_MODE, key);

            byte[] encryptedBytes = Base64.decode(encryptedBase64, Base64.DEFAULT);
            byte[] decryptedBytes = cipher.doFinal(encryptedBytes);
            decryptedString = new String(decryptedBytes);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return decryptedString;
    }

    private void sendMessage(ChatMessageCompound chatMessageCompound) {

                notSending = false;


                textFieldAnimating = false;


                //Toast.makeText(getContext(),"Why is the send asyncTask being called?",Toast.LENGTH_SHORT).show();


                ChatListItemCreationModel chatListItemCreationModel = new ChatListItemCreationModel();
                chatListItemCreationModel.setUsernameUserChattingWith(usernameChattingWith);
                chatListItemCreationModel.setCurrentUserPhoto(currentUserPhoto);
                chatListItemCreationModel.setCurrentUserName(currentUsername);
                chatListItemCreationModel.setUserChattingWith_photo(photo);
                chatListItemCreationModel.setSenderChatCreateRef(senderChatCreateRef);
                chatListItemCreationModel.setReceiverChatCreateRef(receiverChatCreateRef);


                SendMessageAsyncModel sendMessageAsyncModel = new SendMessageAsyncModel();
                sendMessageAsyncModel.setChatMessageSenderCopy(chatMessageCompound.getChatMessageSenderCopy());
                sendMessageAsyncModel.setChatMessageReceiverCopy(chatMessageCompound.getChatMessageReceiverCopy());
                sendMessageAsyncModel.setSenderReference(sendderUsersRef);
                sendMessageAsyncModel.setReceiverReference(receiverUsersRef);
                sendMessageAsyncModel.setUserChattingWithId(userIdChattingWith);
                sendMessageAsyncModel.setCurrentID(FirebaseAuth.getInstance().getCurrentUser().getUid());
                sendMessageAsyncModel.setChatListItemCreationModel(chatListItemCreationModel);

                SendMessageAsyncTask sendMessageAsyncTask = new SendMessageAsyncTask();

                sendMessageAsyncTask.execute(sendMessageAsyncModel);




              //  lastMessageID = "none";


                /*ephemeralMessageViewModel.sendMessage(chatMessageNew);
                ephemeralMessageViewModel.createChatListItem(usernameChattingWith, photo, currentUsername, currentUserPhoto);*/












                //message_text_sender.setAlpha(0.0f);
        if (messageList.size() < 1) {
            try {
                message_bar.setBackground(getActivity().getDrawable(R.drawable.message_identifier_no_message));
            } catch (Exception e) {
                //
            }
        }
                else
            try {
                message_bar.setBackground(getActivity().getDrawable(R.drawable.message_identifier_reply_drawable));
            } catch (Exception e) {
                //
            }

                        notSending = true;
                        message_text_sender.setText("");
                        message_text_two_sender.setText("");
                        message_text_three_sender.setText("");

                        message_text_sender.setAlpha(1.0f);

                        message_text_two_sender.setAlpha(1.0f);

                        message_text_three_sender.setAlpha(1.0f);

                        message_text_two_sender.setVisibility(View.GONE);
                        message_text_three_sender.setVisibility(View.GONE);

                        textFieldAnimating = true;

                      //  animateSenderTextFields(true);

                        messageStr = "";
                        messageStrTwo = "";
                        messageStrThree = "";


                        imageReady = true;



    }

    private void animateArrow() {
        //send_arrow.setAlpha(1.0f);
        /**
         *
         *Chain button and progress view get hidden when send button is pressed
         */

        sendButtonPressed = true;
        continue_message_box_blinking = false;

        if(!getIsMessageRunning())
            if (messageList.size() < 1)
                message_bar.setBackground(getActivity().getDrawable(R.drawable.message_identifier_no_message));

       // final ChatMessage chatMessage = setUpSendingChat();
        final ChatMessageCompound  chatMessageCompound = setUpSendingChat();
        chainBtn.setVisibility(View.GONE);
        chain_pulse.setVisibility(View.GONE);

      //  profile_pic.animate().alpha(1.0f).setDuration(setAnimationSendingDuration * 2 + 650);



        //soundPool.play(sendSound3, 1, 1, 0, 0, 1);
        //sending_progress_bar.setVisibility(View.VISIBLE);
        archive_btn.setVisibility(View.VISIBLE);
        archive_btn.setBackgroundTintList
                (ContextCompat.getColorStateList(getContext(), R.color.colorPending));


        try {
            if (animatorSet.isRunning()) {
                animatorSet.cancel();
            }
        }catch (Exception e)
        {

        }

        Display display = getActivity().getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        displayHeight = size.y;

        message_text_sender.animate().translationY(-displayHeight - (displayHeight) / 4).setDuration(setAnimationSendingDuration - 50);
        message_text_two_sender.animate().translationY(-displayHeight - (displayHeight) / 4).setDuration(setAnimationSendingDuration - 50);
        message_text_three_sender.animate().translationY(-displayHeight - (displayHeight) / 4).setDuration(setAnimationSendingDuration - 50);



        animatorY = ObjectAnimator.ofFloat(send_arrow,"translationY",-displayHeight -(displayHeight)/3);
      //  animatorY.setDuration(arrowAnimDuration);

        animateMsg = ObjectAnimator.ofFloat(message_text_sender,"translationY",-displayHeight - (displayHeight) / 4);
       // animateMsg.setDuration(arrowAnimDuration);


        animateMsgTwo = ObjectAnimator.ofFloat(message_text_two_sender,"translationY",-displayHeight - (displayHeight) / 4);
       // animateMsgTwo.setDuration(arrowAnimDuration);

        animatorMsgThree = ObjectAnimator.ofFloat(message_text_three_sender,"translationY",-displayHeight - (displayHeight) / 4);
       // animatorMsgThree.setDuration(arrowAnimDuration);
        //animatorY.setDuration(650);
        // animatorY.start();

        alphaArrow = ObjectAnimator.ofFloat(send_arrow,"alpha",1.0f,0.02f);
      //  alphaArrow.setDuration(arrowAnimDuration);

        alphaMsg = ObjectAnimator.ofFloat(message_text_sender,"alpha",0.35f,0.15f);
       // alphaArrow.setDuration(arrowAnimDuration);

        alphaMsgTwo = ObjectAnimator.ofFloat(message_text_two_sender,"alpha",0.35f,0.15f);
       // alphaArrow.setDuration(arrowAnimDuration);

        alphaMsgThree = ObjectAnimator.ofFloat(message_text_three_sender,"alpha",0.35f,0.15f);
        //alphaArrow.setDuration(arrowAnimDuration);

        animatePic = ObjectAnimator.ofFloat(profile_pic,"alpha",0.0f,1.0f);
       // animatePic.setDuration(arrowAnimDuration+300);


        //alphaArrow.setDuration(650);

        sendButton.setAlpha(0.15f);
        sendButton.setEnabled(false);
        sendButton.setVisibility(View.INVISIBLE);

        if (isDisablerecord_button)
            disablerecord_button.setVisibility(View.VISIBLE);
        else {

            recordView.setVisibility(View.VISIBLE);
            recordButton.setVisibility(View.VISIBLE);
        }
        sendButtonEnabled = false;

        send_arrow.setAlpha(1.0f);





        profile_pic.setBlur(0);

        animatorSet = new AnimatorSet();

        animatorSet.setDuration(arrowAnimDuration);

        animatorSet.playTogether(animatorY,alphaArrow,animateMsg,animateMsgTwo,animatorMsgThree,alphaMsg,alphaMsgTwo,alphaMsgThree/*,animatePic*/);

        animatorSet.start();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                profile_pic.animate().alpha(1.0f).setDuration(arrowAnimDuration/2+250);
            }
        }, arrowAnimDuration/2);


        /**
         * calling the below two commented function results in a UI lag
         */
        /*resetMessageBox();
        sendMessage(chatMessage);*/


        animatorSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

                send_arrow.setAlpha(1.0f);
                profile_pic.setAlpha(0.0f);
                message_text_sender.setAlpha(0.35f);
                message_text_two_sender.setAlpha(0.35f);
                message_text_three_sender.setAlpha(0.35f);

                message_text_sender.setTextColor(Color.parseColor("#ffffff"));
                message_text_two_sender.setTextColor(Color.parseColor("#ffffff"));



               // message_box.setText("");



            }

            @Override
            public void onAnimationEnd(Animator animation) {

                /*if(imageLoaded)
                    profile_pic.setBlur(0);*/

                send_arrow.setTranslationY(0f);
                send_arrow.setAlpha(0.0f);


                resetMessageBox();

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        sendMessage(chatMessageCompound);
                    }
                }, 250);


                //profile_pic.animate().alpha(1.0f).setDuration(250);

                message_text_sender.setAlpha(1.0f);

                message_text_dummy.setVisibility(View.VISIBLE);
                message_text_sender_dummy.setVisibility(View.VISIBLE);

                sendButtonPressed = false;




            }

            @Override
            public void onAnimationCancel(Animator animation) {

                send_arrow.setTranslationY(0f);
                send_arrow.setAlpha(0.0f);

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });

    }

    private void resetMessageBox() {
        message_box.setHint("Chat here...");
        continue_message_box_blinking = false;
        message_box_behind.setAlpha(0.0f);
        try {
            message_box.setHintTextColor(getActivity().getResources().getColor(R.color.textThemeColorDark));
        }catch (Exception e)
        {

        }
    }

    private void resetReceiveMessageView()
    {
        receiver_arrow.setTranslationY(0f);
        receiver_arrow.setAlpha(0.0f);



        message_text.setAlpha(0.0f);
        message_text_two.setAlpha(0.0f);
        message_text_three.setAlpha(0.0f);



        username.setAlpha(1.0f);
        try {
            if(message_text_sender.length()<1
             &&message_text_two_sender.length()<1
            &&message_text_three_sender.length()<1)
            profile_pic.setAlpha(1.0f);

        }catch (Exception e)
        {
            //
        }
        try
        {
            if(imageLoaded)
                profile_pic.setBlur(0);
        }catch (Exception e)
        {

        }

        message_text_dummy.setVisibility(View.VISIBLE);
        //message_text_sender_dummy.setVisibility(View.VISIBLE);

        sendButtonPressed = false;
    }

    private void startRecording() {

        audioRecording = true;
        equlizer.setVisibility(View.VISIBLE);
        equlizer.animate().alpha(1.0f).setDuration(300);
        equilizerAnimation(equi_one);
        equilizerAnimation(equi_two);
        equilizerAnimation(equi_three);
        equilizerAnimation(equi_four);
        equilizerAnimation(equi_five);

        recorder = new MediaRecorder();
        fileName = getActivity().getExternalCacheDir().getAbsolutePath();
        fileName += "/audiorecordtest.3gp";
        //play_btn.setVisibility(View.GONE);
        Toast.makeText(getActivity(), "Recording started", Toast.LENGTH_SHORT).show();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        recorder.setOutputFile(fileName);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            recorder.prepare();
        } catch (IOException e) {
            //Log.e(LOG_TAG, "prepare() failed");
        }

        try {
            recorder.start();
        } catch (Exception e) {

        }
    }

    private void stopRecording() {

        //audioRecording = true;


        Toast.makeText(getActivity(), "Recording stopped", Toast.LENGTH_SHORT).show();
        recorder.stop();
        recorder.release();
        recorder = null;
        //animateArrow();

        // uploadAudio();
        sendAudio();

        // play_btn.setVisibility(View.VISIBLE);
    }

    private void sendAudio() {

        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Messages")
                .child(FirebaseAuth.getInstance()
                        .getCurrentUser()
                        .getUid())
                .child(userIdChattingWith);

        final String mKey = databaseReference.push().getKey();

        Uri uri = Uri.fromFile(new File(fileName));
        Intent intent1 = new Intent(getActivity(), SendMediaService.class);
        intent1.putExtra("userChattingWithId", userIdChattingWith);
        intent1.putExtra("imageUri", uri.toString());
        //intent1.putExtra("encrptedVide", videoUri.toString());

        intent1.putExtra("usernameChattingWith", usernameChattingWith);
        intent1.putExtra("userphotoUrl", photo);
        intent1.putExtra("currentUsername", currentUsername);
        intent1.putExtra("currentUserPhoto", currentUserPhoto);
        intent1.putExtra("currentUserPublicKeyID", currentUserPublicKeyID);
        intent1.putExtra("otherUserPublicKeyID", otherUserPublicKeyID);
        intent1.putExtra("currentUserPublicKey", currentUserPublicKey);
        intent1.putExtra("otherUserPublicKey", otherUserPublicKey);
        intent1.putExtra("mKey", mKey);
        intent1.putExtra("type", "audio");


        getActivity().startService(intent1);

        // finish();

    }

    private void uploadAudio() {

        /*progressDialog = new ProgressDialog(AudioRecorderActivity.this);
        progressDialog.setTitle("Uploading audio...");
        progressDialog.show();*/

        Uri uri = Uri.fromFile(new File(fileName));


        storageReference = FirebaseStorage.getInstance().getReference().child(System.currentTimeMillis() + "." + getExtension(uri));


        uploadTask = storageReference.putFile(uri);


        final Timestamp timestamp = new Timestamp(System.currentTimeMillis());


        archive_btn.setVisibility(View.VISIBLE);


        uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task task) throws Exception {

                if (!task.isSuccessful()) {
                    throw task.getException();
                }


                return storageReference.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {

                    archive_btn.setVisibility(View.GONE);
                    // progressDialog.dismiss();
                    Uri downloadUri = task.getResult();

                    String mUri = downloadUri.toString();

                    final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Messages")
                            .child(FirebaseAuth.getInstance()
                                    .getCurrentUser()
                                    .getUid())
                            .child(userIdChattingWith);

                    final String mKey = databaseReference.push().getKey();


                    final HashMap<String, Object> sendMessageHash = new HashMap<>();
                    sendMessageHash.put("senderId", FirebaseAuth.getInstance()
                            .getCurrentUser()
                            .getUid());
                    sendMessageHash.put("receiverId", userIdChattingWith);
                    sendMessageHash.put("messageId", mKey);
                    sendMessageHash.put("publicKeyID", currentUserPublicKeyID);
                    sendMessageHash.put("timeStamp", timestamp.toString());
                    sendMessageHash.put("messageText", "");
                    sendMessageHash.put("sentStatus", "sending");
                    sendMessageHash.put("seen", "notseen");
                    sendMessageHash.put("photourl", "none");
                    sendMessageHash.put("audiourl", mUri);
                    sendMessageHash.put("videourl", "none");
                    sendMessageHash.put("gotReplyID", "none");
                    sendMessageHash.put("replyTag", false);
                    sendMessageHash.put("replyID", "none");
                    sendMessageHash.put("senderReplyMessage", "none");
                    sendMessageHash.put("ifMessageTwo", false);
                    sendMessageHash.put("messageTextTwo", "");
                    sendMessageHash.put("ifMessageThree", false);
                    sendMessageHash.put("messageTextThree", "");
                    sendMessageHash.put("showReplyMsg", false);
                    sendMessageHash.put("replyMsg", " ");
                    sendMessageHash.put("showGotReplyMsg", false);
                    sendMessageHash.put("gotReplyMsg", " ");


                    DatabaseReference receiverReference = FirebaseDatabase.getInstance().getReference("Messages")
                            .child(userIdChattingWith)
                            .child(FirebaseAuth.getInstance()
                                    .getCurrentUser()
                                    .getUid());


                    final HashMap<String, Object> sendReceiverMessageHash = new HashMap<>();
                    sendReceiverMessageHash.put("senderId", FirebaseAuth.getInstance()
                            .getCurrentUser()
                            .getUid());
                    sendReceiverMessageHash.put("receiverId", userIdChattingWith);
                    sendReceiverMessageHash.put("messageId", mKey);
                    sendReceiverMessageHash.put("publicKeyID", otherUserPublicKeyID);
                    sendReceiverMessageHash.put("timeStamp", timestamp.toString());
                    sendReceiverMessageHash.put("messageText", "");
                    sendReceiverMessageHash.put("sentStatus", "sending");
                    sendReceiverMessageHash.put("seen", "notseen");
                    sendReceiverMessageHash.put("photourl", "none");
                    sendReceiverMessageHash.put("audiourl", mUri);
                    sendReceiverMessageHash.put("videourl", "none");
                    sendReceiverMessageHash.put("gotReplyID", "none");
                    sendReceiverMessageHash.put("replyTag", false);
                    sendReceiverMessageHash.put("replyID", "none");
                    sendReceiverMessageHash.put("senderReplyMessage", "none");
                    sendReceiverMessageHash.put("ifMessageTwo", false);
                    sendReceiverMessageHash.put("messageTextTwo", "");
                    sendReceiverMessageHash.put("ifMessageThree", false);
                    sendReceiverMessageHash.put("messageTextThree", "");
                    sendReceiverMessageHash.put("showReplyMsg", false);
                    sendReceiverMessageHash.put("replyMsg", " ");
                    sendReceiverMessageHash.put("showGotReplyMsg", false);
                    sendReceiverMessageHash.put("gotReplyMsg", " ");

                    databaseReference.child(mKey).updateChildren(sendMessageHash).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                HashMap<String, Object> hashMap = new HashMap<>();
                                hashMap.put("sentStatus", "sent");

                                databaseReference.child(mKey).updateChildren(hashMap);
                            }
                        }
                    });
                    receiverReference.child(mKey).updateChildren(sendReceiverMessageHash);


                    createChatListItem(usernameChattingWith, photo, currentUsername, currentUserPhoto);
                }
            }
        });


    }

    private String getExtension(Uri uri) {


        ContentResolver contentResolver = getActivity().getContentResolver();

        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();

        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));

    }

    private void createChatListItem(final String usernameUserChattingWith, final String userChattingWith_photo, final String currentUserName, final String currentUserPhoto) {

        Long tsLong = System.currentTimeMillis() / 1000;
        final String ts = tsLong.toString();


        final HashMap<String, Object> timeStampHash = new HashMap<>();
        timeStampHash.put("timeStamp", ts);
        timeStampHash.put("id", userIdChattingWith);
        timeStampHash.put("username", usernameUserChattingWith);
        timeStampHash.put("photo", userChattingWith_photo);
        timeStampHash.put("seen", "notseen");
        timeStampHash.put("chatPending", false);
        senderChatCreateRef.updateChildren(timeStampHash);

        HashMap<String, Object> timeStampHashReceiver = new HashMap<>();

        timeStampHashReceiver.put("timeStamp", ts);
        timeStampHashReceiver.put("id", FirebaseAuth.getInstance().getCurrentUser().getUid());
        timeStampHashReceiver.put("username", currentUserName);
        timeStampHashReceiver.put("photo", currentUserPhoto);
        timeStampHashReceiver.put("seen", "notseen");
        timeStampHashReceiver.put("chatPending", true);
        receiverChatCreateRef.updateChildren(timeStampHashReceiver);

    }


    private void filterList(List<ChatMessage> chatMessageList) {

        try {


                List<ChatMessage> messageListReading = chatMessageList;
                //if(!getIsMessageRunning())
                messageList.clear();
                photoMessageList.clear();

                try {

                    for (ChatMessage chatMessage : messageListReading) {
                        if (chatMessage.getSenderId().equals(userIdChattingWith)) {
                            if (chatMessage.getPhotourl().equals("none")
                                    && chatMessage.getAudiourl().equals("none")
                                    && chatMessage.getVideourl().equals("none")) {

                                if (currentUserPublicKeyID.equals(chatMessage.getPublicKeyID()))
                                    messageList.add(chatMessage);
                            } else
                                photoMessageList.add(chatMessage);
                        } else if (chatMessage.getSenderId().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {

                            try {

                                if (chatMessage.getMessageId() != null)
                                    sendMessageList.add(chatMessage);

                                else {
                                    messageListReading.remove(chatMessage);
                                }
                            } catch (Exception e) {
                                messageListReading.remove(chatMessage);
                            }

                        }


                    }


                    if (photoMessageList.size() > 0) {
                        photo_btn.setVisibility(View.VISIBLE);
                        photo_btn_bg.setVisibility(View.VISIBLE);
                        ChatMessage chatMessage = photoMessageList.get(0);

                        if (!chatMessage.getPhotourl().equals("none")) {


                            photo_btn.setBackground(getActivity().getResources().getDrawable(R.drawable.ic_image_white_24dp));

                        } else if (!chatMessage.getAudiourl().equals("none")) {

                            photo_btn.setBackground(getActivity().getResources().getDrawable(R.drawable.ic_mic_black_24dp));

                        } else if (!chatMessage.getVideourl().equals("none")) {
                            photo_btn.setBackground(getActivity().getResources().getDrawable(R.drawable.ic_videocam_black_24dp));
                        }
                    } else {
                        photo_btn.setVisibility(View.INVISIBLE);
                        photo_btn_bg.setVisibility(View.INVISIBLE);
                    }

                    checkForHint();
                    setMessageHighlight(messageHighlight);

                    if (photoMessageList.size() >= 1) {
                        photosNotification.setVisibility(View.VISIBLE);
                    } else {
                        photosNotification.setVisibility(View.INVISIBLE);
                    }


                    if (messageList.size() < 1) {


                        ephemeralMessageViewModel.setChatPending(false);

                   /* swipeButton.setEnabled(false);

                    swipeButton.setVisibility(View.GONE);*/

                        switchBtn.setChecked(false);

                        switchBtn.setVisibility(View.GONE);

                        setChainBtn = false;

                        message_running.setVisibility(View.INVISIBLE);
                        message_running_two.setVisibility(View.INVISIBLE);

                        // username.setVisibility(View.VISIBLE);


                        //  swipeButton = null;


                        /**
                         *  Automatically queues the messages after button is explicitely triggered,
                         *  after a message in received when this activity is opened,
                         *  unless we add the following
                         *  setChainBtn = false;
                         **/


                        chainBtn.setVisibility(View.GONE);
                        chain_pulse.setVisibility(View.GONE);
                        //
                        //  ephemeralMessageViewModel.setChatSeen();
                        message_counter_background.setVisibility(View.INVISIBLE);
                        message_pulse.setVisibility(View.INVISIBLE);
                        message_no_message_counter_background.setVisibility(View.INVISIBLE);
                        message_counter.setText("");


                        message_counter.setBackground(getActivity().getDrawable(R.drawable.message_counter_drawable));
                        message_counter.setVisibility(View.INVISIBLE);
                        button_exterior.setVisibility(View.INVISIBLE);
                        if (!isMessageRunning)
                            message_bar.setBackground(getActivity().getDrawable(R.drawable.message_identifier_no_message));


                        // message_counter.setVisibility(View.GONE);
                    } else {

                        //  ephemeralMessageViewModel.setChatPending(true);

                    /*swipeButton = findViewById(R.id.swipeBtn);
                    swipeButton.setEnabled(true);
                    swipeButton.setVisibility(View.VISIBLE);*/
                        message_counter_background.setVisibility(View.VISIBLE);
                        message_no_message_counter_background.setVisibility(View.INVISIBLE);
                        message_counter.setVisibility(View.VISIBLE);
                        button_exterior.setVisibility(View.VISIBLE);
                        message_counter.setBackground(getActivity().getDrawable(R.drawable.message_counter_new_message_drawable));


                        if (!isMessageRunning) {
                            message_bar.setBackground(getActivity().getDrawable(R.drawable.message_identifier_reply_drawable));
                            message_pulse.setVisibility(View.VISIBLE);
                        }

                        if (messageList.size() > 99) {
                            message_counter.setText("99+");
                            message_counter.setTextSize(26);
                        } else {
                            message_counter.setText(messageList.size() + "");
                            message_counter.setTextSize(32);
                        }
                    }


                    if (sendMessageList.size() > 0) {

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {

                                int size = sendMessageList.size();
/*//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                            DatabaseReference databaseReference = FirebaseDatabase.getInstance()
                                    .getReference("SentStatus")
                                    .child(userIdChattingWith)
                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .child(sendMessageList.get(size - 1).getMessageId());

                            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                    DeliveryStatus deliveryStatus = dataSnapshot.getValue(DeliveryStatus.class);

                                    try {
                                        if (deliveryStatus.getSentStatus().equals("sending")) {
                                            sending_progress_bar.setVisibility(View.VISIBLE);
                                            archive_btn.setBackgroundTintList(ContextCompat.getColorStateList(getActivity(), R.color.colorPending));
                                        } else {
                                            archive_btn.setBackgroundTintList(ContextCompat.getColorStateList(getActivity(), R.color.colorWhite));
                                            sending_progress_bar.setVisibility(View.GONE);
                                        }
                                    }catch (Exception e)
                                    {
                                        archive_btn.setBackgroundTintList(ContextCompat.getColorStateList(getActivity(), R.color.colorWhite));
                                        sending_progress_bar.setVisibility(View.GONE);
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });*/
                                if (sendMessageList.get(size - 1).getSentStatus().equals("sending")) {

                                    try {
                                        archive_btn.setBackgroundTintList(ContextCompat.getColorStateList(getContext(), R.color.colorPending));

                                    } catch (Exception e) {
                                        //
                                    }
                                    //sending_progress_bar.setVisibility(View.VISIBLE);
                                    archive_btn.setVisibility(View.VISIBLE);
                                } else {
                                    try {
                                        archive_btn.setBackgroundTintList(ContextCompat.getColorStateList(getContext(), R.color.colorWhite));

                                    } catch (Exception e) {
                                        //
                                    }
                                    sending_progress_bar.setVisibility(View.GONE);
                                    archive_btn.setVisibility(View.INVISIBLE);
                                }

                            }
                        }, 0);


                    }


                } catch (Exception e) {
                    //
                    // listTaskCompletionSource.setResult(photoMessageList);
                }






        } catch (NullPointerException e) {

        }


    }

    private void checkForHint() {
        ifHintExists = false;
        try {


            for (ChatMessage sentMessage : sendMessageList) {
                if (messageList.get(0).getReplyID().equals(sentMessage.getMessageId())) {
                    highlightMessageText = decryptRSAToString(sentMessage.getMessageText(),currentUserPrivateKey);


                    if(sentMessage.getIfMessageTwo())
                        highlightMessageText = highlightMessageText+" "+decryptRSAToString(sentMessage.getMessageTextTwo(),currentUserPrivateKey);

                    if(sentMessage.getIfMessageThree())
                        highlightMessageText = highlightMessageText+" "+decryptRSAToString(sentMessage.getMessageTextThree(),currentUserPrivateKey);

                    ifHintExists = true;
                    if(enableXtransformation)
                        setMessageHighlight(messageHighlight);
               }

            }


        }catch (Exception e)
        {
            //ifHintExists=false;
            //Toast.makeText(getContext(),"Exception : "+e.toString(),Toast.LENGTH_SHORT).show();
        }

        if(ifHintExists)
        {
            message_log_pulse.setVisibility(View.VISIBLE);
        }
        else
            message_log_pulse.setVisibility(View.INVISIBLE);
    }
    private void requestAllPermissions() {

        Dexter.withActivity(getActivity())
                .withPermissions(Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.CAMERA)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {

                        if(report.areAllPermissionsGranted())
                        {
                            Intent intent = new Intent(getContext(), CameraActivity.class);

                            // ephemeralMessageViewModel.createChatListItem(usernameChattingWith, photo, currentUsername, currentUserPhoto);
                            if (currentUsername != null && currentUserPhoto != null) {

                                intent.putExtra("userChattingWithId", userIdChattingWith);
                                intent.putExtra("userphoto", photo);
                                intent.putExtra("username", usernameChattingWith);
                                intent.putExtra("currentUsername", currentUsername);
                                intent.putExtra("currentUserPhoto", currentUserPhoto);
                                intent.putExtra("otherUserPublicKeyID", otherUserPublicKeyID);
                                intent.putExtra("currentUserPublicKeyID", currentUserPublicKeyID);
                                intent.putExtra("otherUserPublicKey", otherUserPublicKey);
                                intent.putExtra("currentUserPublicKey", currentUserPublicKey);



                                // intent.putExtra("userChattingWithId", currentUserPhoto);


                                startActivity(intent);
                            } else
                                Toast.makeText(getActivity(), "Getting user details", Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            Toast.makeText(getActivity(), "Permission not given!", Toast.LENGTH_SHORT).show();
                        }

                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {

                        token.continuePermissionRequest();

                        // Toast.makeText(getActivity(), "Permission Denied!", Toast.LENGTH_SHORT).show();
                    }
                }).check();
    }

    private void readNextMessage() {

        resetMessageBox();
        //resetReceiveMessageView();
        if (messageList.size() == 0) {
            username.setVisibility(View.VISIBLE);
            message_running.setVisibility(View.INVISIBLE);
            message_running_two.setVisibility(View.INVISIBLE);


            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {

                    if(!isMessageRunning) {

                        UsernameObjectAnimator = ObjectAnimator.ofFloat(username,"alpha",0.0f,1.0f);
                        UsernameObjectAnimator.setDuration(500);
                        UsernameObjectAnimator.start();

                    }
                    else
                        username.setAlpha(0.0f);
                    //username.setVisibility(View.VISIBLE);
                   // username.setAlpha(1.0f);

                }
            },300);

            Toast.makeText(getContext(), "You have no messages to read!", Toast.LENGTH_SHORT).show();
        }

        else {

            animateReceiveArrow();

            Vibrator v = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
// Vibrate for 500 milliseconds
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                v.vibrate(VibrationEffect.createOneShot(150, VibrationEffect.DEFAULT_AMPLITUDE));
            } else {
                //deprecated in API 26
                v.vibrate(100);
            }


            soundPool.play(delSound1, 1, 1, 0, 0, 1);

                           /*// This is for type writer
                            message_text_typewriter.setCharacterDelay(75);
                            message_text_typewriter.animate().alpha(1.0f).setDuration(750);
                            message_text_typewriter.animateText(liveMessage);*/


        }


    }

    private void animateReceiveArrow() {

        //animateArrow();


        imageReady = false;


    /*
        message_text_sender.setAlpha(0.0f);
        message_text_two_sender.setAlpha(0.0f);
        message_text_three_sender.setAlpha(0.0f);*/
        message_text_two.setVisibility(View.GONE);
        message_text_three.setVisibility(View.GONE);

        /**
         * Hide user typed message.
         */



            AnimatorSet animatorSet = new AnimatorSet();

            ObjectAnimator objectAnimatorMsg1 = ObjectAnimator.ofFloat(message_text_sender, "alpha", 1.0f, 0.0f);
            ObjectAnimator objectAnimatorMsg2 = ObjectAnimator.ofFloat(message_text_two_sender, "alpha", 1.0f, 0.0f);
            ObjectAnimator objectAnimatorMsg3 = ObjectAnimator.ofFloat(message_text_three_sender, "alpha", 1.0f, 0.0f);

            animatorSet.playTogether(objectAnimatorMsg1, objectAnimatorMsg2, objectAnimatorMsg3);
            animatorSet.setDuration(250);
            animatorSet.start();






                                /*message_text_sender.animate().alpha(0.0f).setDuration(250);
        message_text_two_sender.animate().alpha(0.0f).setDuration(250);
        message_text_three_sender.animate().alpha(0.0f).setDuration(250);*/

        /**
         *hide message pulse animation
         *
         */
        message_pulse.setVisibility(View.GONE);


        /**
         *set message bar indicator for reading message background
         */

        message_bar.setBackground(getActivity().getDrawable(R.drawable.message_identifier_drawable));

        /**
         * place the receiving arrow to its original positon and set the opacity to 100%
         */

        receiver_arrow.setTranslationY(0);
        receiver_arrow.setAlpha(1.0f);

        /**
         * set the highlight message idicator box to its original position
         */
        sender_reply_body.setTranslationX(0);


        /**
         * set username opacity to 0% and make it invisible
         */
        /*username.setAlpha(0.0f);
        username.setVisibility(View.INVISIBLE);*/


        try {
            if (UsernameObjectAnimator.isRunning())
                UsernameObjectAnimator.cancel();
        }catch (Exception e)
        {
            //
        }

        UsernameObjectAnimator = ObjectAnimator.ofFloat(username,"alpha",1.0f,0.0f);
        UsernameObjectAnimator.setDuration(0);
        UsernameObjectAnimator.start();





        if(imageLoaded)
            profile_pic.setBlur(4);

        setIsMessageRunning(true);

        final ChatMessage chatMessage = setUpReceiveMessage();



        animateReceiveArrowY = ObjectAnimator.ofFloat(receiver_arrow,"translationY",displayHeight + (displayHeight) / 4);
        //animatorY.setDuration(650);
        // animatorY.start();

        if(message_text_sender.getText().length()>0
                ||message_text_two_sender.getText().length()>0
                ||message_text_three_sender.getText().length()>0||setChainBtn
        )
        {
            animatePic = ObjectAnimator.ofFloat(profile_pic,"alpha",0.0f,0.0f);
        }
        else
            animatePic = ObjectAnimator.ofFloat(profile_pic,"alpha",1.0f,0.0f);



        receiveArrowAlpha = ObjectAnimator.ofFloat(receiver_arrow,"alpha",1.0f,0.02f);

        //  ObjectAnimator messageTextAnimator = ObjectAnimator.ofFloat(message_text,"alpha",0.0f,1.0f);

        animateNextMsgHintBody = ObjectAnimator.ofFloat(sender_reply_body,"translationY",displayHeight + (displayHeight) / 4);

        animateNextMsgHintBodyALpha = ObjectAnimator.ofFloat(sender_reply_body,"alpha",1.0f,0.0f);

        // sender_reply_body.animate().translationXBy(-3000).setDuration(500);



        AnimatorSet animatorSetReceivingArrow = new AnimatorSet();
        animatorSetReceivingArrow.setDuration(arrowAnimDuration);

        animatorSetReceivingArrow.playTogether(animateReceiveArrowY,receiveArrowAlpha,animatePic,animateNextMsgHintBody,animateNextMsgHintBodyALpha/*,messageTextAnimator*/);

        animatorSetReceivingArrow.start();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                message_text.animate().alpha(1.0f).setDuration(250);
            }
        }, 300);


        animatorSetReceivingArrow.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {


            }

            @Override
            public void onAnimationEnd(Animator animation) {





                // message_text.animate().alpha(1.0f).setDuration(250);
                Runnable runnable = initializeDeletionRunnable();
                deletionHandler.postDelayed(runnable, liveMessage.length() * 75 + 750);
                ephemeralMessageViewModel.deleteMessage(chatMessage, false);

                receiver_arrow.setTranslationY(0);
                receiver_arrow.setAlpha(0.0f);
                // sender_reply_body.setAlpha(1);
                sender_reply_body.setTranslationY(0);

            }

            @Override
            public void onAnimationCancel(Animator animation) {

                animatorSetReceivingArrowCancel();

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

                animatorSetReceivingArrowCancel();

            }
        });

        /**
         * animate receiving message text
         */








    }

    private void animatorSetReceivingArrowCancel()
    {
        receiver_arrow.setTranslationY(0);
        sender_reply_body.setTranslationX(0);
        receiver_arrow.setAlpha(1.0f);
        username.setAlpha(0.0f);
        username.setVisibility(View.INVISIBLE);
    }

    private Runnable initializeDeletionRunnable() {

        messageTextOpacity = 1.0f;
        messageTextTwoOpacity = 1.0f;
        messageTextThreeOpacity = 1.0f;



        runnable = new Runnable() {
            @Override
            public void run() {


                if (!messageTwoPresent && !messageThreePresent) {



                    /*if(setChainBtn)
                    {
                        messageTextOpacity = 0.0f;
                        messageTextTwoOpacity = 0.0f;
                        messageTextThreeOpacity = 0.0f;


                    }*/



                    final ObjectAnimator animateAlphaMsg = ObjectAnimator.ofFloat(message_text,"alpha",messageTextOpacity,0.0f);
                    ObjectAnimator animateAlphaMsgTwo = ObjectAnimator.ofFloat(message_text_two,"alpha",messageTextTwoOpacity,0.0f);
                    ObjectAnimator animateAlphaMsgThree = ObjectAnimator.ofFloat(message_text_three,"alpha",messageTextThreeOpacity,0.0f);


                    AnimatorSet animatorSet = new AnimatorSet();
                    animatorSet.setDuration(setAnimationSendingDuration);

                    animatorSet.playTogether(animateAlphaMsg,animateAlphaMsgTwo,animateAlphaMsgThree);
                    animatorSet.start();


                    animatorSet.addListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {


                            try {

                                if (messageList.size() < 1)
                                    message_bar.setBackground(getActivity().getDrawable(R.drawable.message_identifier_no_message));
                                else
                                    message_bar.setBackground(getActivity().getDrawable(R.drawable.message_identifier_reply_drawable));

                            } catch (Exception e) {
                                //
                            }


                            message_bar.setVisibility(View.VISIBLE);



                            if(!isMessageRunning) {
                                username.setVisibility(View.VISIBLE);

                                //  Toast.makeText(getContext(),"entered block isMessageRunning line 2356",Toast.LENGTH_SHORT).show();


                                List<ObjectAnimator> objectAnimators = new ArrayList<>();



                                ObjectAnimator animateUsername = ObjectAnimator.ofFloat(username, "alpha", 0.0f, 1.0f);
                                objectAnimators.add(animateUsername);
                                if(typingID == MSG_TYPING_BOX_ID){
                                    ObjectAnimator animateMsg = ObjectAnimator.ofFloat(message_text_sender, "alpha", 0.0f, 1.0f);
                                    objectAnimators.add(animateMsg);
                                }
                                else
                                {
                                    ObjectAnimator animateMsg = ObjectAnimator.ofFloat(message_text_sender, "alpha", 0.0f, 0.5f);
                                    objectAnimators.add(animateMsg);
                                }

                                if (typingID == MSG_TYPING_BOX_TWO_ID) {
                                    ObjectAnimator animateMsgTwo = ObjectAnimator.ofFloat(message_text_two_sender, "alpha", 0.0f, 1.0f);
                                    objectAnimators.add(animateMsgTwo);
                                }
                                else
                                {
                                    ObjectAnimator animateMsgTwo = ObjectAnimator.ofFloat(message_text_two_sender, "alpha", 0.0f, 0.5f);
                                    objectAnimators.add(animateMsgTwo);
                                }

                                if (typingID == MSG_TYPING_BOX_THREE_ID){
                                    ObjectAnimator animateMsgThree = ObjectAnimator.ofFloat(message_text_three_sender, "alpha", 0.0f, 1.0f);
                                    objectAnimators.add(animateMsgThree);
                                }
                                else
                                {
                                    ObjectAnimator animateMsgThree = ObjectAnimator.ofFloat(message_text_three_sender, "alpha", 0.0f, 0.5f);
                                    objectAnimators.add(animateMsgThree);
                                }

                                try {

                                    AnimatorSet animatorSet1 = new AnimatorSet();

                                    animatorSet1.setDuration(250);
                                    animatorSet1.playTogether((Animator) objectAnimators);
                                    animatorSet1.start();
                                }catch (Exception e)
                                {
                                    // Toast.makeText(getContext(),"Problem in playing animation together using a list",Toast.LENGTH_SHORT).show();
                                }

                            }

                            message_text.setText("");
                            message_text.setAlpha(0.0f);

                            message_text_two.setText("");
                            message_text_two.setAlpha(0.0f);

                            message_text_three.setText("");
                            message_text_three.setAlpha(0.0f);

                            if (message_text_sender.getText().length() > 0 ||
                                    message_text_two_sender.getText().length() > 0 ||
                                    message_text_three_sender.getText().length() > 0) {

                                message_bar.setBackground(getActivity().getDrawable(R.drawable.message_identifier_sender_drawable));

                            }

                            //setIsMessageRunning(false);

                            deletionHandler.removeCallbacks(runnable);
                            blinkerHandler.removeCallbacks(blinkerHandlerRunnable);
                            liveMessage = "";

                            if (messageList.size() > 0)
                                message_pulse.setVisibility(View.VISIBLE);

                            imageReady = true;


                            if (setChainBtn) {
                                username.setVisibility(View.INVISIBLE);
                                username.setAlpha(0.0f);

                                if(messageList.size()>=1)
                                    readNextMessage();
                                else
                                {
                                    setChainBtn = false;
                                    message_counter.setVisibility(View.INVISIBLE);
                                    setIsMessageRunning(false);
                                }
                            } else {


                                try{
                                    replyRunnable = new Runnable() {
                                        @Override
                                        public void run() {

                                            username.setVisibility(View.VISIBLE);
                                            username.animate().alpha(1.0f).setDuration(500);

                                            if (imageLoaded)
                                                profile_pic.setBlur(0);

                                            profile_pic.animate().alpha(alphaValueProfilePic).setDuration(500);

                                            enableXtransformation = true;
                                            setMessageHighlight(messageHighlight);


                                            if (!lastMessageID.equals("none")) {

                                                message_box.setHint("Reply to last message...");
                                                try {

                                                    message_box.setHintTextColor(getActivity().getResources().getColor(R.color.textThemeColorSemiDark));
                                                } catch (NullPointerException e) {
                                                    //
                                                }


                                                try {
                                                    final Animation hyperspaceJump = AnimationUtils.loadAnimation(getContext(), R.anim.text_bounce_anim);

                                                    hyperspaceJump.setRepeatMode(Animation.INFINITE);

                                                    message_box.setAnimation(hyperspaceJump);

                                                    continue_message_box_blinking = true;

                                                    vibratorRunnable = new Runnable() {
                                                        @Override
                                                        public void run() {

                                                            try {

                                                                Vibrator v = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);

// Vibrate for 500 milliseconds
                                                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                                                    v.vibrate(VibrationEffect.createOneShot(75, VibrationEffect.DEFAULT_AMPLITUDE));
                                                                } else {
                                                                    //deprecated in API 26
                                                                    v.vibrate(75);
                                                                }
                                                            } catch (Exception e) {
                                                                //

                                                            }
                                                            blinkMessageBox();
                                                            setIsMessageRunning(false);

                                                        }
                                                    };

                                                    vibratorHandler.postDelayed(vibratorRunnable, 500);
                                                } catch (Exception e) {

                                                }


                                            } else {


                                                final Animation hyperspaceJump = AnimationUtils.loadAnimation(getContext(), R.anim.text_bounce_anim);

                                                hyperspaceJump.setRepeatMode(Animation.INFINITE);
                                                message_box.setAnimation(hyperspaceJump);
                                                setIsMessageRunning(false);
                                            }




                                        }
                                    };


                                    replyHandler.postDelayed(replyRunnable,0);



                                    switch (typingID) {
                                        case MSG_TYPING_BOX_ID:


                                            ObjectAnimator animateMsg = ObjectAnimator.ofFloat(message_text_sender, "alpha", 0.0f, 1.0f);
                                            ObjectAnimator animateMsgTwo = ObjectAnimator.ofFloat(message_text_two_sender, "alpha", 0.0f, 0.1f);
                                            ObjectAnimator animateMsgThree = ObjectAnimator.ofFloat(message_text_three_sender, "alpha", 0.0f, 0.2f);

                                            AnimatorSet animatorSet1 = new AnimatorSet();
                                            animatorSet1.setDuration(250);
                                            animatorSet1.playTogether(animateMsg,animateMsgTwo,animateMsgThree);
                                            animatorSet1.start();

                                            break;

                                        case MSG_TYPING_BOX_TWO_ID:

                                            ObjectAnimator animateMsg2 = ObjectAnimator.ofFloat(message_text_sender, "alpha", 0.0f, 0.1f);
                                            ObjectAnimator animateMsgTwo2 = ObjectAnimator.ofFloat(message_text_two_sender, "alpha", 0.0f, 1.0f);
                                            ObjectAnimator animateMsgThree2 = ObjectAnimator.ofFloat(message_text_three_sender, "alpha", 0.0f, 0.2f);

                                            AnimatorSet animatorSet2 = new AnimatorSet();
                                            animatorSet2.setDuration(250);
                                            animatorSet2.playTogether(animateMsg2,animateMsgTwo2,animateMsgThree2);
                                            animatorSet2.start();

                                            break;

                                        case MSG_TYPING_BOX_THREE_ID:

                                            ObjectAnimator animateMsg3 = ObjectAnimator.ofFloat(message_text_sender, "alpha", 0.0f, 0.1f);
                                            ObjectAnimator animateMsgTwo3 = ObjectAnimator.ofFloat(message_text_two_sender, "alpha", 0.0f, 0.2f);
                                            ObjectAnimator animateMsgThree3 = ObjectAnimator.ofFloat(message_text_three_sender, "alpha", 0.0f, 1.0f);

                                            AnimatorSet animatorSet3 = new AnimatorSet();
                                            animatorSet3.setDuration(250);
                                            animatorSet3.playTogether(animateMsg3,animateMsgTwo3,animateMsgThree3);
                                            animatorSet3.start();

                                            break;

                                        default:
                                            break;

                                    }


                                }catch (Exception e)
                                {
                                    //
                                }


                            }


                        }

                        @Override
                        public void onAnimationCancel(Animator animation) {

                        }

                        @Override
                        public void onAnimationRepeat(Animator animation) {

                        }
                    });


                } else if (messageTwoPresent) {

                    messageTextOpacity = 0.2f;
                    messageTextTwoOpacity = 1.0f;
                    //messageTextThreeOpacity = 1.0f;
                    message_text.setAlpha(messageTextOpacity);
                    message_text_dummy.setVisibility(View.GONE);
                    message_text_two.setVisibility(View.VISIBLE);
                    message_text_two.animate().setDuration(500).alpha(messageTextTwoOpacity);
                    messageTwoPresent = false;
                    deletionHandler.postDelayed(runnable,liveMessageTwo.length() * 75 + 1250);

                } else if (messageThreePresent) {

                    messageTextOpacity = 0.1f;
                    messageTextTwoOpacity = 0.2f;


                    message_text.setAlpha(messageTextOpacity);
                    message_text_two.setAlpha(messageTextTwoOpacity);
                    message_text_dummy.setVisibility(View.GONE);
                    message_text_three.setVisibility(View.VISIBLE);
                    message_text_three.animate().setDuration(500).alpha(messageTextThreeOpacity);
                    messageThreePresent = false;
                    deletionHandler.postDelayed(runnable,liveMessageThree.length() * 75 + 1250);
                }

            }


        };



        return runnable;
    }

    private ChatMessage setUpReceiveMessage() {


        /**
         * make the dummy message box visible, this will be used for animation purposes.
         */
        message_text_dummy.setVisibility(View.VISIBLE);
        enableXtransformation = false;

        message_text.setTranslationY(0);
        message_text_two.setTranslationY(0);
        message_text_three.setTranslationY(0);

        message_text_two.setVisibility(View.GONE);
        message_text_three.setVisibility(View.GONE);
        messageTwoPresent = false;
        messageThreePresent = false;

        final ChatMessage chatMessage = messageList.get(0);
        lastMessageID = chatMessage.getMessageId();

        senderReplyID = chatMessage.getReplyID();

        senderReplyMessage = decryptRSAToString(chatMessage.getMessageText(),currentUserPrivateKey);

        liveMessage = senderReplyMessage;

        //Toast.makeText(getContext(),"Setup Receive Message is called",Toast.LENGTH_SHORT).show();

        if(chatMessage.getIfMessageTwo()) {
            senderReplyMessage = senderReplyMessage + " " + decryptRSAToString(chatMessage.getMessageTextTwo(),currentUserPrivateKey);
            liveMessageTwo = decryptRSAToString(chatMessage.getMessageTextTwo(),currentUserPrivateKey);
            messageTwoPresent = true;
        }

        if(chatMessage.getIfMessageThree()) {
            senderReplyMessage = senderReplyMessage + " " + decryptRSAToString(chatMessage.getMessageTextThree(),currentUserPrivateKey);
            liveMessageThree = decryptRSAToString(chatMessage.getMessageTextThree(),currentUserPrivateKey);
            messageThreePresent = true;
        }


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {


            message_text.setAutoSizeTextTypeUniformWithConfiguration(1,chatMessage.getTextSize(),1,1);

            message_text_two.setAutoSizeTextTypeUniformWithConfiguration(1,chatMessage.getTextSize(),1,1);

            message_text_three.setAutoSizeTextTypeUniformWithConfiguration(1,chatMessage.getTextSize(),1,1);
        }

        try{
            if (blinking) {
                interruptBlink();
            }
            // blink();
            else blink();
        }catch (Exception e)
        {
            // setUpBlink();
            blink();
        }
        message_text.setText(liveMessage);
        if (messageTwoPresent)
            message_text_two.setText(liveMessageTwo);

        if (messageThreePresent)
            message_text_three.setText(liveMessageThree);

        /**
         * setting the interval at which the message bar would blink
         */
        if (messageThreePresent && messageTwoPresent)
            blinkInterval = (liveMessage.length() * 50 + 750 +
                    liveMessageTwo.length() * 50 + 750 +
                    liveMessageThree.length() * 50 + 750) / 10;

        else if (messageTwoPresent)
            blinkInterval = (liveMessage.length() * 50 + 750 +
                    liveMessageTwo.length() * 50 + 750) / 10;

        else if (messageThreePresent)
            blinkInterval = (liveMessage.length() * 50 + 750 +
                    liveMessageThree.length() * 50 + 750) / 10;

        else
            blinkInterval = (liveMessage.length() * 50 + 750) / 10;

        return chatMessage;

    }

    private void blinkMessageBox() {



            message_box_behind.animate().alpha(1.0f).setDuration(250);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {

                    message_box_behind.animate().alpha(0.0f).setDuration(250);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {


                            if(continue_message_box_blinking)
                                blinkMessageBox();


                        }
                    }, 300);
                }

            },300);




    }

    private void blink() {


        blinkerThread = new Thread(blinkerThreadRunnable);
        blinkerThread.start();
        blinking = true;

    }

    public void interruptBlink() {
        blinkerThread.interrupt();
        blinkerHandler.removeCallbacks(blinkerHandlerRunnable);
        message_bar.setVisibility(View.VISIBLE);
        blinking = false;
    }

    private void equilizerAnimation(final RelativeLayout equi) {

        equlizer.setAlpha(1.0f);
        equlizer.setVisibility(View.VISIBLE);


        Random r = new Random();
        float min = 0.65f;
        float max = 1.3f;

        float equir = min + r.nextFloat() * (max - min);


        Random ri = new Random();

        final int minTime = 150;
        final int maxTime = 300;

        final int equitime = ri.nextInt((maxTime - minTime) + 1) + minTime;

        equi.animate().scaleY(equir).setDuration(equitime);

        new Handler().postDelayed(new Runnable() {


            @Override
            public void run() {
                equi.animate().scaleY(1.0f).setDuration(equitime);

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (audioRecording)
                            equilizerAnimation(equi);
                        else {

                            equlizer.animate().alpha(0.0f).setDuration(300);
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    equlizer.setVisibility(View.GONE);
                                }
                            }, 300);
                        }

                    }
                }, equitime);
            }
        }, equitime);
    }

    private void setUpBlink() {
        blinkerHandler = new Handler();


        blinkerHandlerRunnable = new Runnable() {
            @Override
            public void run() {

                if (message_bar.getVisibility() == View.VISIBLE) {
                    message_bar.setVisibility(View.INVISIBLE);
                } else {

                    message_bar.setVisibility(View.VISIBLE);
                }


                if (isMessageRunning) { blink(); }

                else {
                    interruptBlink();
                }

            }
        };


        blinkerThreadRunnable = new Runnable() {
            @Override
            public void run() {
                int timeToBlink = blinkInterval;    //in milliseconds
                try {
                    Thread.sleep(timeToBlink);
                } catch (Exception e) {
                    //
                }
                blinkerHandler.post(blinkerHandlerRunnable);
            }
        };
    }

    public void setProfilePicOpacity(float opacity) {
        this.opacity = opacity;
    }

    public float getProfilePicOpacity() {
        return opacity;
    }


    public void setIsMessageRunning(Boolean isMessageRunning) {

        this.isMessageRunning = isMessageRunning;

    }

    public Boolean getIsMessageRunning() {
        return isMessageRunning;
    }

    private void checkPresence() {
        final DatabaseReference connectedRef = FirebaseDatabase.getInstance().getReference(".info/connected");
        connectedRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean connected = snapshot.getValue(Boolean.class);
                if (connected) {

                    online_ring.setVisibility(View.VISIBLE);
                    //online_status.setVisibility(View.VISIBLE);
                   // Log.d(TAG, "connected");
                   // Toast.makeText(getActivity(),"You are connected!",Toast.LENGTH_SHORT).show();

                   // updateUserPresence(false);
/*                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users")
                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .child("online");

                    databaseReference.setValue(true);
                    databaseReference.onDisconnect().setValue(false);*/


                  //  updateUserPresence(true);

                    confirmConnected = true;

                    if(checkConnectedSatus) {

                        try {


                            Snackbar snackbar = Snackbar
                                    .make(app_context_layout, "You are connected", Snackbar.LENGTH_SHORT);
                            View snackBarView = snackbar.getView();
                            snackBarView.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.colorGreen));
                            snackbar.show();
                        } catch (Exception e) {
                            //
                        }
                    }




                } else {
                   // Log.d(TAG, "not connected");
                   // Toast.makeText(getActivity(),"You are disconnected!",Toast.LENGTH_SHORT).show();

                    online_ring.setVisibility(View.INVISIBLE);
                    online_status.setVisibility(View.INVISIBLE);
                    confirmConnected = false;
                   // updateUserPresence(false);

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {

                            if (!confirmConnected) {
                                try {
                                    Snackbar snackbar = Snackbar
                                            .make(app_context_layout, "You are disconnected", Snackbar.LENGTH_SHORT);
                                    View snackBarView = snackbar.getView();
                                    snackBarView.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.darkGrey));
                                    snackbar.show();
                                    checkConnectedSatus = true;
                                } catch (Exception e) {
                                    //
                                }
                            } else {

                            }
                        }
                    }, 3000);








                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
               // Log.w(TAG, "Listener was cancelled");
            }
        });
    }



    public void setMessageHighlight(Boolean messageHighlight) {


        this.messageHighlight = messageHighlight;

           // message_log_text.setText("HIGHLIGHTS");
            if(ifHintExists)
            {
                your_sent_msg.setText(highlightMessageText);
                sender_reply_body.setVisibility(View.VISIBLE);

                if(enableXtransformation) {


                    //sender_reply_body.setTranslationY(0);

                    sender_reply_body.setAlpha(0.75f);


                    sender_reply_body.setTranslationX(-2000);
                    sender_reply_body.animate().translationXBy(2000).setDuration(500);


                }

            }
            else
            {
                your_sent_msg.setText("");
                sender_reply_body.setVisibility(View.GONE);
                sender_reply_body.setAlpha(1.0f);
            }


    }

    private void setTypeFace(String path,String pathLight)
    {
        if(!path.equals("null") && !pathLight.equals("null")) {
            message_text.setTypeface(Typeface.createFromAsset(getContext().getAssets(), path));
            message_text_two.setTypeface(Typeface.createFromAsset(getContext().getAssets(), path));
            message_text_three.setTypeface(Typeface.createFromAsset(getContext().getAssets(), path));

            message_text_sender.setTypeface(Typeface.createFromAsset(getContext().getAssets(), path));
            message_text_two_sender.setTypeface(Typeface.createFromAsset(getContext().getAssets(), path));
            message_text_three_sender.setTypeface(Typeface.createFromAsset(getContext().getAssets(), path));

            username.setTypeface(Typeface.createFromAsset(getContext().getAssets(), pathLight));

            message_box.setTypeface(Typeface.createFromAsset(getContext().getAssets(), path));

            top_swipe_text.setTypeface(Typeface.createFromAsset(getContext().getAssets(), path));

            message_log_text.setTypeface(Typeface.createFromAsset(getContext().getAssets(), path));


            swipe_down.setTypeface(Typeface.createFromAsset(getContext().getAssets(), path));
            go_live_txt.setTypeface(Typeface.createFromAsset(getContext().getAssets(), path));
        }

    }

    @Override
    public void onPause() {

        replyHandler.removeCallbacks(replyRunnable);
        vibratorHandler.removeCallbacks(vibratorRunnable);

        deletionHandler.removeCallbacks(runnable);
        setIsMessageRunning(false);

        resetViews();

        super.onPause();
    }

    private void resetViews() {

        resetMessageBox();
        resetReceiveMessageView();

    }

    @Override
    public void updateViews(Boolean isMessageRunning) {

    }


    private class SendMessageAsyncTask extends AsyncTask<SendMessageAsyncModel,Void,Void>
    {

        @Override
        protected Void doInBackground(SendMessageAsyncModel... sendMessageAsyncModels) {


            final SendMessageAsyncModel sendMessageAsyncModel = sendMessageAsyncModels[0];



            ChatMessage chatMessageReceiverCopy = sendMessageAsyncModel.getChatMessageReceiverCopy();
            ChatMessage chatMessageSenderCopy = sendMessageAsyncModel.getChatMessageSenderCopy();

            /*ephemeralMessageViewModel.sendMessage(chatMessage);
            ephemeralMessageViewModel.createChatListItem(usernameChattingWith, photo, currentUsername, currentUserPhoto);*/


            final String messageKey = sendMessageAsyncModel.getSenderReference().push().getKey();

            chatMessageReceiverCopy.setSenderId(sendMessageAsyncModel.getCurrentID());
            chatMessageReceiverCopy.setReceiverId(sendMessageAsyncModel.getUserChattingWithId());
            chatMessageReceiverCopy.setMessageId(messageKey);

            final HashMap<String,  Object> sendMessageHash = new HashMap<>();
            sendMessageHash.put("publicKeyID",chatMessageReceiverCopy.getPublicKeyID());
            sendMessageHash.put("senderId",chatMessageReceiverCopy.getSenderId());
            sendMessageHash.put("receiverId",chatMessageReceiverCopy.getReceiverId());
            sendMessageHash.put("messageId",chatMessageReceiverCopy.getMessageId());
            sendMessageHash.put("messageText",chatMessageReceiverCopy.getMessageText());
            sendMessageHash.put("sentStatus",chatMessageReceiverCopy.getSentStatus());
            sendMessageHash.put("seen", chatMessageReceiverCopy.getSeen());
            sendMessageHash.put("photourl",chatMessageReceiverCopy.getPhotourl());
            sendMessageHash.put("audiourl", chatMessageReceiverCopy.getAudiourl());
            sendMessageHash.put("videourl", chatMessageReceiverCopy.getVideourl());
            sendMessageHash.put("textSize", chatMessageReceiverCopy.getTextSize());
            sendMessageHash.put("replyID",chatMessageReceiverCopy.getReplyID());
            sendMessageHash.put("gotReplyID",chatMessageReceiverCopy.getGotReplyID());
            sendMessageHash.put("senderReplyMessage",chatMessageReceiverCopy.getSenderReplyMessage());
            sendMessageHash.put("replyTag",chatMessageReceiverCopy.getReplyTag());
            sendMessageHash.put("ifMessageTwo",chatMessageReceiverCopy.getIfMessageTwo());
            sendMessageHash.put("messageTextTwo",chatMessageReceiverCopy.getMessageTextTwo());
            sendMessageHash.put("ifMessageThree",chatMessageReceiverCopy.getIfMessageThree());
            sendMessageHash.put("messageTextThree",chatMessageReceiverCopy.getMessageTextThree());
            sendMessageHash.put("showReplyMsg",chatMessageReceiverCopy.getShowReplyMsg());
            sendMessageHash.put("replyMsg",chatMessageReceiverCopy.getReplyMsg());
            sendMessageHash.put("showGotReplyMsg",chatMessageReceiverCopy.getShowGotReplyMsg());
            sendMessageHash.put("gotReplyMsg",chatMessageReceiverCopy.getGotReplyMsg());
            sendMessageHash.put("timeStamp", chatMessageReceiverCopy.getTimeStamp());




            chatMessageSenderCopy.setSenderId(sendMessageAsyncModel.getCurrentID());
            chatMessageSenderCopy.setReceiverId(sendMessageAsyncModel.getUserChattingWithId());
            chatMessageSenderCopy.setMessageId(messageKey);

            final HashMap<String,  Object> sendMessageHashSenderCopy = new HashMap<>();
            sendMessageHashSenderCopy.put("publicKeyID",chatMessageSenderCopy.getPublicKeyID());
            sendMessageHashSenderCopy.put("senderId",chatMessageSenderCopy.getSenderId());
            sendMessageHashSenderCopy.put("receiverId",chatMessageSenderCopy.getReceiverId());
            sendMessageHashSenderCopy.put("messageId",chatMessageSenderCopy.getMessageId());
            sendMessageHashSenderCopy.put("messageText",chatMessageSenderCopy.getMessageText());
            sendMessageHashSenderCopy.put("sentStatus",chatMessageSenderCopy.getSentStatus());
            sendMessageHashSenderCopy.put("seen", chatMessageSenderCopy.getSeen());
            sendMessageHashSenderCopy.put("photourl",chatMessageSenderCopy.getPhotourl());
            sendMessageHashSenderCopy.put("audiourl", chatMessageSenderCopy.getAudiourl());
            sendMessageHashSenderCopy.put("videourl", chatMessageSenderCopy.getVideourl());
            sendMessageHashSenderCopy.put("textSize", chatMessageSenderCopy.getTextSize());
            sendMessageHashSenderCopy.put("replyID",chatMessageSenderCopy.getReplyID());
            sendMessageHashSenderCopy.put("gotReplyID",chatMessageSenderCopy.getGotReplyID());
            sendMessageHashSenderCopy.put("senderReplyMessage",chatMessageSenderCopy.getSenderReplyMessage());
            sendMessageHashSenderCopy.put("replyTag",chatMessageSenderCopy.getReplyTag());
            sendMessageHashSenderCopy.put("ifMessageTwo",chatMessageSenderCopy.getIfMessageTwo());
            sendMessageHashSenderCopy.put("messageTextTwo",chatMessageSenderCopy.getMessageTextTwo());
            sendMessageHashSenderCopy.put("ifMessageThree",chatMessageSenderCopy.getIfMessageThree());
            sendMessageHashSenderCopy.put("messageTextThree",chatMessageSenderCopy.getMessageTextThree());
            sendMessageHashSenderCopy.put("showReplyMsg",chatMessageSenderCopy.getShowReplyMsg());
            sendMessageHashSenderCopy.put("replyMsg",chatMessageSenderCopy.getReplyMsg());
            sendMessageHashSenderCopy.put("showGotReplyMsg",chatMessageSenderCopy.getShowGotReplyMsg());
            sendMessageHashSenderCopy.put("gotReplyMsg",chatMessageSenderCopy.getGotReplyMsg());
            sendMessageHashSenderCopy.put("timeStamp", chatMessageSenderCopy.getTimeStamp());


/*            final DatabaseReference messageReportSender = FirebaseDatabase.getInstance().getReference("ReportMessages")
                    .child(sendMessageAsyncModel.getCurrentID())
                    .child(sendMessageAsyncModel.getUserChattingWithId());

            DatabaseReference messageReportReceiver = FirebaseDatabase.getInstance().getReference("ReportMessages")
                    .child(sendMessageAsyncModel.getCurrentID())
                    .child(sendMessageAsyncModel.getUserChattingWithId());

*/

            if(!chatMessageReceiverCopy.getReplyID().equals("none"))
            {
                DatabaseReference messageReportUpdateReply = FirebaseDatabase.getInstance().getReference("Messages")
                        .child(sendMessageAsyncModel.getUserChattingWithId())
                        .child(sendMessageAsyncModel.getCurrentID());

                DatabaseReference messageReportReceiver = FirebaseDatabase.getInstance().getReference("Reports")
                        .child(sendMessageAsyncModel.getUserChattingWithId())
                        .child(sendMessageAsyncModel.getCurrentID());

                HashMap<String,Object> hashMap = new HashMap<>();

                hashMap.put("gotReplyID",messageKey);
                hashMap.put("seen","seen");

                messageReportUpdateReply.child(chatMessageReceiverCopy.getReplyID()).updateChildren(hashMap);
                messageReportReceiver.child(chatMessageReceiverCopy.getReplyID()).updateChildren(hashMap);

              //  Toast.makeText(getContext(),"Why is the send asyncTask being called?",Toast.LENGTH_SHORT).show();
            }

/*            if(!chatMessageSenderCopy.getReplyID().equals("none"))
            {
                DatabaseReference messageReportUpdateReply = FirebaseDatabase.getInstance().getReference("Messages")
                        .child(sendMessageAsyncModel.getUserChattingWithId())
                        .child(sendMessageAsyncModel.getCurrentID());

                HashMap<String,Object> hashMap = new HashMap<>();

                hashMap.put("gotReplyID",messageKey);
                hashMap.put("seen","seen");

                messageReportUpdateReply.child(chatMessageSenderCopy.getReplyID()).updateChildren(hashMap);
            }*/





            sendMessageAsyncModel.getSenderReference().child(messageKey).updateChildren(sendMessageHashSenderCopy).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    HashMap<String,Object> sentHash = new HashMap<>();
                    sentHash.put("sentStatus","sent");
                    // sentHash.put("messageId",messageKey);


                    sendMessageAsyncModel.getSenderReference().child(messageKey).updateChildren(sentHash);

                }
            });
            sendMessageAsyncModel.getReceiverReference().child(messageKey).updateChildren(sendMessageHash);

            DatabaseReference reportReceiverReference = FirebaseDatabase.getInstance().getReference("Reports")
                    .child(userIdChattingWith)
                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid());

            DatabaseReference reportSenderReference = FirebaseDatabase.getInstance().getReference("Reports")
                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .child(userIdChattingWith);

            reportReceiverReference.child(messageKey).updateChildren(sendMessageHash);
            reportSenderReference.child(messageKey).updateChildren(sendMessageHashSenderCopy);


            Long tsLong = System.currentTimeMillis()/1000;
            final String ts = tsLong.toString();

            final HashMap<String, Object> timeStampHash = new HashMap<>();

            final HashMap<String,Object> timeStampHashReceiver = new HashMap<>();


            /**
             * Creating chat list
             */

            final ChatListItemCreationModel chatListItemCreationModel = sendMessageAsyncModel.getChatListItemCreationModel();

            chatListItemCreationModel.getSenderChatCreateRef().runTransaction(new Transaction.Handler() {
                @NonNull
                @Override
                public Transaction.Result doTransaction(@NonNull MutableData mutableData) {

                    ChatStamp chatStamp = mutableData.getValue(ChatStamp.class);

                    if(chatStamp==null)
                    {

                        timeStampHash.put("timeStamp", ts);
                        timeStampHash.put("id", sendMessageAsyncModel.getUserChattingWithId());
                        timeStampHash.put("username", chatListItemCreationModel.getUsernameUserChattingWith());
                        timeStampHash.put("photo", chatListItemCreationModel.getUserChattingWith_photo());
                        timeStampHash.put("seen", "notseen");
                        timeStampHash.put("chatPending", false);
                        timeStampHash.put("gemCount",2);

                        timeStampHashReceiver.put("timeStamp", ts);
                        timeStampHashReceiver.put("id", sendMessageAsyncModel.getCurrentID());
                        timeStampHashReceiver.put("username", chatListItemCreationModel.getCurrentUserName());
                        timeStampHashReceiver.put("photo", currentUserPhoto);
                        timeStampHashReceiver.put("seen", "notseen");
                        timeStampHashReceiver.put("chatPending",true);
                        timeStampHashReceiver.put("gemCount",2);
                        chatListItemCreationModel.getSenderChatCreateRef().updateChildren(timeStampHash);
                        chatListItemCreationModel.getReceiverChatCreateRef().updateChildren(timeStampHashReceiver);

                    }
                    else
                    {
                        timeStampHash.put("timeStamp", ts);
                        timeStampHash.put("id", sendMessageAsyncModel.getUserChattingWithId());
                        timeStampHash.put("username", chatListItemCreationModel.getUsernameUserChattingWith());
                        timeStampHash.put("photo", chatListItemCreationModel.getUserChattingWith_photo());
                        timeStampHash.put("seen", "notseen");
                        timeStampHash.put("chatPending", false);

                        timeStampHashReceiver.put("timeStamp", ts);
                        timeStampHashReceiver.put("id", sendMessageAsyncModel.getCurrentID());
                        timeStampHashReceiver.put("username", chatListItemCreationModel.getCurrentUserName());
                        timeStampHashReceiver.put("photo", currentUserPhoto);
                        timeStampHashReceiver.put("seen", "notseen");
                        timeStampHashReceiver.put("chatPending",true);

                        chatListItemCreationModel.getSenderChatCreateRef().updateChildren(timeStampHash);
                        chatListItemCreationModel.getReceiverChatCreateRef().updateChildren(timeStampHashReceiver);

                    }

                    return null;
                }

                @Override
                public void onComplete(@Nullable DatabaseError databaseError, boolean b, @Nullable DataSnapshot dataSnapshot) {

                }
            });

            return null;
        }
    }




}
