package com.shubhamkislay.jetpacklogin.Fragments;


import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
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
import com.jgabrielfreitas.core.BlurImageView;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.shubhamkislay.jetpacklogin.CameraActivity;
import com.shubhamkislay.jetpacklogin.EphemeralMessageActivityViewModelFactory;
import com.shubhamkislay.jetpacklogin.EphemeralMessageViewModel;
import com.shubhamkislay.jetpacklogin.EphemeralPhotoActivity;
import com.shubhamkislay.jetpacklogin.Interface.MessageHighlightListener;
import com.shubhamkislay.jetpacklogin.Interface.MessageRunningListener;
import com.shubhamkislay.jetpacklogin.Model.ChatListItemCreationModel;
import com.shubhamkislay.jetpacklogin.Model.ChatMessage;
import com.shubhamkislay.jetpacklogin.Model.ChatMessageCompound;
import com.shubhamkislay.jetpacklogin.Model.ChatStamp;
import com.shubhamkislay.jetpacklogin.Model.SendMessageAsyncModel;
import com.shubhamkislay.jetpacklogin.Model.User;
import com.shubhamkislay.jetpacklogin.R;
import com.shubhamkislay.jetpacklogin.SwipeButtonActivity;
import com.shubhamkislay.jetpacklogin.TypeWriter;
import com.shubhamkislay.jetpacklogin.UserPicViewModel;
import com.shubhamkislay.jetpacklogin.UserPicViewModelFactory;
import com.shubhamkislay.jetpacklogin.VerticalPageActivity;

import java.security.Key;
import java.security.KeyFactory;
import java.security.spec.KeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.crypto.Cipher;

/**
 * A simple {@link Fragment} subclass.
 */
public class EphemeralMessagingFragment extends Fragment implements MessageRunningListener {


    public TextView username, button_exterior, message_counter_background, message_no_message_counter_background;
    public Button  message_counter;

    public TextView message_text_dummy, message_text, message_text_two, message_text_three;
    public TextView message_text_sender_dummy, message_text_sender, message_text_two_sender, message_text_three_sender;
    public BlurImageView profile_pic;
    public TypeWriter message_text_typewriter;
    public ProgressBar message_pulse, chain_pulse, sending_progress_bar, message_log_pulse, message_running, message_running_two;
    public float opacity = 0.0f;
    public Boolean blinking = false, message_box_blinking = false;
    private int msg_send_size =0, msg_send_two_size=0, msg_send_three_size=0;
    public String highlightMessageText = "";
    public EditText message_box, message_box_behind;
    boolean continue_message_box_blinking = true;
    public Boolean messageHighlight = true, sendButtonEnabled =false;
    public Button sendButton, message_bar, reply_btn, cancel_reply_btn;
    public RelativeLayout bottom_bar, message_counter_layout, message_space, reply_tag, app_context_layout, sender_reply_body, sender_reply_tag;
    public RelativeLayout read_message_reply_layout;
    public View rootView;
    public Float alphaValueProfilePic = 1.0f;
    public Boolean enableXtransformation = true;
    public int textSize;
    public Boolean replyTag = false;
    public Boolean ifHintExists =false;
    public TextView send_arrow_two, archive_btn;
    public ImageView send_arrow, receiver_arrow;
    public TextView your_sent_msg, back_button;
    private String liveMessage, liveMessageTwo, liveMessageThree;
    private Boolean messageTwoPresent = false, messageThreePresent = false;
    private Button emoji, camera;
    private ImageView read_message_back;
    private Button read_message_reply;
    public String messageStr = "", messageStrTwo = "", messageStrThree = "";
    public Handler deletionHandler, blinkerHandler;
    public Runnable runnable, blinkerThreadRunnable, blinkerHandlerRunnable;
    public Thread blinkerThread;
    public List<ChatMessage> messageList, sendMessageList, photoMessageList;
    private String userIdChattingWith;
    public int blinkInterval;
    public Boolean isSenderReplying = false;
    public ImageView view;
    public int before_count, after_count;
    public ToggleButton toggleButton;
    public Boolean imageReady = true;
    public Boolean imageLoaded = false;
    public String lastMessageID = "none";
    public String senderReplyID = "none";
    public String otherUserPublicKey = null;
    public String otherUserPublicKeyID = null;
    public String currentUserPublicKey = null;
    public String currentUserPublicKeyID = null;
    public String currentUserPrivateKey = null;
    public Boolean senderReplyTag = false;
    public String senderReplyMessage = "none";
    public Boolean isSeeingReply = false;
    public String messageTextOne, messageTextTwo, MessageTextThree;
    public Boolean setChainBtn = false;
    public SeekBar textSizeseekBar;
    public static final int MSG_TYPING_BOX_ID = 1;
    public static final int MSG_TYPING_BOX_TWO_ID = 2;
    public static final int MSG_TYPING_BOX_THREE_ID = 3;

    public Button photosNotification;
    private ObjectAnimator UsernameObjectAnimator;

    private Handler replyHandler, vibratorHandler;
    private Runnable replyRunnable, vibratorRunnable;

    Switch switchBtn;
    Boolean softKeyVisible = true;
    String messageKeySender, messageKeyReceiver;

    public int typingID = 1;

    public Button chainBtn;
    public SwipeButton swipeButton;
    Boolean swipeState = false;
    Point size;
    float displayHeight;

    Boolean emojiActive = false;

    public int setAnimationSendingDuration;

    public SoundPool soundPool;
    Boolean readyToCheckSize = false;
    private int delSound1, cantDelSound2, sendSound3, sendingSound4, sendingSound5;

    float messageTextOpacity = 1.0f;
    float messageTextTwoOpacity = 1.0f;
    float messageTextThreeOpacity = 1.0f;


    private EphemeralMessageViewModel ephemeralMessageViewModel;

    private SendMessageFragment sendMessageFragment;
    public Boolean isMessageRunning = false;
    Window window;
    private Boolean threadPending;
    public int sendTextSize = 34;
    public String currentUsername;
    private TextView top_swipe_text;
    public String currentUserPhoto;
    public TextView online_status;
    public ImageView online_ring;
    public String usernameChattingWith;
    public String photo;
    Boolean textListner_msg = false;
    Boolean textListner_msg_two = false;
    Boolean textListner_msg_three = false;

    public TextView message_log_text, swipe_down,go_live_txt;
    private boolean notSending = true;
    private boolean textFieldAnimating = false;
    private boolean firstAnimComplete = false;
    private boolean checkConnectedSatus = false;
    ObjectAnimator animatorY, alphaArrow, animateMsg, animateMsgTwo, animatorMsgThree, alphaMsg, alphaMsgTwo, alphaMsgThree, animatePic;
    ObjectAnimator animateReceiveArrowY, receiveArrowAlpha, animateNextMsgHintBody, animateNextMsgHintBodyALpha;
    AnimatorSet animatorSet;
    private boolean sendButtonPressed = false;
    private int arrowAnimDuration = 600;
    private DatabaseReference sendderUsersRef;
    private DatabaseReference receiverUsersRef;
    private DatabaseReference senderChatCreateRef;
    private DatabaseReference receiverChatCreateRef;
    private ChatMessage firstReceivedMessage = new ChatMessage();

    public EphemeralMessagingFragment() {
        // Required empty public constructor
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_ephemeral_message, container, false);
        getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,
                WindowManager.LayoutParams.FLAG_SECURE);


        Display display = getActivity().getWindowManager().getDefaultDisplay();
        size = new Point();
        display.getSize(size);
        displayHeight = size.y;




        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

       final VerticalPageActivity verticalPageActivity = (VerticalPageActivity) getActivity();

       switchBtn = view.findViewById(R.id.switch_btn);
//

        toggleButton = view.findViewById(R.id.toggle_msg_highlight);


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





        //rootView = view.findViewById(R.id.app_context_layout);

        replyHandler = new Handler();
        vibratorHandler = new Handler();


        deletionHandler = new Handler();

        message_box = view.findViewById(R.id.message_box);
        message_box_behind = view.findViewById(R.id.message_box_behind);
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

        DatabaseReference otherUserkeyReference = FirebaseDatabase.getInstance().getReference("Users")
                .child(userIdChattingWith);

        otherUserkeyReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                {
                    User user = dataSnapshot.getValue(User.class);
                    try {
                        otherUserPublicKey = user.getPublicKey();
                        otherUserPublicKeyID = user.getPublicKeyId();
                    }
                    catch (Exception e)
                    {
                        //
                    }
                }
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

        read_message_back = view.findViewById(R.id.read_message_back);


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


        Button photo_btn = view.findViewById(R.id.photo_btn);

        photo_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(photoMessageList.size()>0) {

                    ChatMessage chatMessage = photoMessageList.get(0);

                    Intent intent = new Intent(getActivity(), EphemeralPhotoActivity.class);
                    intent.putExtra("userIdChattingWith", userIdChattingWith);
                    intent.putExtra("photoUrl",chatMessage.getPhotourl());
                    intent.putExtra("mKey",chatMessage.getMessageId());
                    intent.putExtra("sender",chatMessage.getSenderId());


                    startActivity(intent);
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
                Glide.with(getActivity()).load(photo.replace("s96-c", "s384-c"))/*.transition(withCrossFade())*/.apply(new RequestOptions().override(width, height)).listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {


                        if (imageReady) {
                            profile_pic.animate().alpha(1.0f).setDuration(750);

                            imageLoaded = true;

                            //   read_message_back.animate().alpha(0.0f);

                            read_message_back.setAlpha(0.0f);

                        }


                        return false;
                    }
                }).into(profile_pic);
            }
            else
            {
                profile_pic.setImageResource(R.drawable.hieeway_background_blurred);
                profile_pic.animate().alpha(alphaValueProfilePic).setDuration(750);
            }
        }catch (Exception e)
        {
            profile_pic.setImageDrawable(getActivity().getDrawable(R.drawable.hieeway_background_blurred));
        }

        archive_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /*Intent intent = new Intent(getContext(), SwipeButtonActivity.class);
                intent.putExtra("userIdChattingWith", userIdChattingWith);

                startActivity(intent);*/

                Toast.makeText(getActivity(),"Sending Message...",Toast.LENGTH_SHORT).show();

            }
        });



        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()) {

                    User user = dataSnapshot.getValue(User.class);

                    currentUsername = user.getUsername();
                    currentUserPhoto = user.getPhoto();
                    /* = user.getPublicKey();
                    otherUserPublicKeyID = user.getPublicKeyId();*/

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        EphemeralMessageActivityViewModelFactory ephemeralMessageActivityViewModelFactory = new EphemeralMessageActivityViewModelFactory(userIdChattingWith);
        ephemeralMessageViewModel = ViewModelProviders.of(this, ephemeralMessageActivityViewModelFactory).get(EphemeralMessageViewModel.class);
        ephemeralMessageViewModel.getAllMessages().observe(this, new Observer<List<ChatMessage>>() {
            @Override
            public void onChanged(@Nullable List<ChatMessage> chatMessageList) {

                /*try {

                    updateChatList(messageList.get(messageList.size() - 1));
                }catch (Exception e)
                {
                    //
                }*/


                //Testing purpose only
                // messageList = chatMessageList;


                before_count = messageList.size();

                /**
                 * Uncomment the following line to only read received messages and not sent messages.
                 */
                after_count = filterList(chatMessageList);


                /*if(after_count>before_count) {
                    soundPool.play(sendingSound4, 1, 1, 0, 0, 1);


                    before_count = after_count;
                }

                else
                    after_count = messageList.size();*/

                if(photoMessageList.size()>=1)
                {
                    photosNotification.setVisibility(View.VISIBLE);
                }
                else
                {
                    photosNotification.setVisibility(View.INVISIBLE);
                }


                if (messageList.size() > 3) {
                    /*swipeButton = view.findViewById(R.id.swipeBtn);
                    swipeButton.setEnabled(true);
                    swipeButton.setVisibility(View.VISIBLE);*/

                   // switchBtn.setVisibility(View.VISIBLE);
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
                                            archive_btn.setBackgroundTintList(ContextCompat.getColorStateList(EphemeralChainMessageActivity.this, R.color.colorPending));
                                        } else {
                                            archive_btn.setBackgroundTintList(ContextCompat.getColorStateList(EphemeralChainMessageActivity.this, R.color.colorWhite));
                                            sending_progress_bar.setVisibility(View.GONE);
                                        }
                                    }catch (Exception e)
                                    {
                                        archive_btn.setBackgroundTintList(ContextCompat.getColorStateList(EphemeralChainMessageActivity.this, R.color.colorWhite));
                                        sending_progress_bar.setVisibility(View.GONE);
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });*/
                            if (sendMessageList.get(size - 1).getSentStatus().equals("sending")) {

                                try{
                                    archive_btn.setBackgroundTintList(ContextCompat.getColorStateList(getContext(), R.color.colorPending));

                                }catch (Exception e)
                                {
                                    //
                                }
                                //sending_progress_bar.setVisibility(View.VISIBLE);
                                archive_btn.setVisibility(View.VISIBLE);
                               } else {
                                try{
                                    archive_btn.setBackgroundTintList(ContextCompat.getColorStateList(getContext(), R.color.colorWhite));

                                }catch (Exception e)
                                {
                                    //
                                }
                                sending_progress_bar.setVisibility(View.GONE);
                                archive_btn.setVisibility(View.INVISIBLE);
                            }

                        }
                    }, 0);


                }

                //    filterList(chatMessageList);
                // threadPending = ephemeralMessageViewModel.isThreadRunning();
                // setIsMessageRunning(threadPending);


            }
        });


        UserPicViewModelFactory userPicViewModelFactory = new UserPicViewModelFactory(userIdChattingWith);
       UserPicViewModel userPicViewModel = ViewModelProviders.of(this, userPicViewModelFactory).get(UserPicViewModel.class);
        userPicViewModel.getUserPic().observe(this, new Observer<User>() {
            @Override
            public void onChanged(@Nullable final User user) {


                        try {
                            if (user.getOnline())
                            {



                                online_ring.animate().alpha(1.0f).setDuration(500);
                                online_status.animate().alpha(1.0f).setDuration(500);
                            }
                            else
                            {
                                online_ring.animate().alpha(0.0f).setDuration(500);
                                online_status.animate().alpha(0.0f).setDuration(500);
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
                    sendButton.setAlpha(1.0f);

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


                        profile_pic.animate().alpha(alphaValueProfilePic).setDuration(setAnimationSendingDuration /** 3 + 650*/);
                        read_message_back.setAlpha(0.0f);

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
        chatMessageReceiverCopy.setTimestamp(timestamp.toString());


        chatMessageSenderCopy.setMessageId("notAssigned");
        chatMessageSenderCopy.setPublicKeyID(currentUserPublicKeyID);
        chatMessageSenderCopy.setMessageText(senderCopyMessage);
        chatMessageSenderCopy.setSenderId("notAssigned");
        chatMessageSenderCopy.setReceiverId("notAssigned");
        chatMessageSenderCopy.setSentStatus("sending");
        chatMessageSenderCopy.setSeen("notseen");
        chatMessageSenderCopy.setPhotourl("none");
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
        chatMessageSenderCopy.setTimestamp(timestamp.toString());




        message_box.setText("");

        lastMessageID = "none";

        chatMessageCompound.setChatMessageReceiverCopy(chatMessageReceiverCopy);
        chatMessageCompound.setChatMessageSenderCopy(chatMessageSenderCopy);


        return chatMessageCompound;
    }

    public  String encryptRSAToString(String clearText, String publicKey) {
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
    public  String decryptRSAToString(String encryptedBase64, String privateKey) {

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
                if (messageList.size() < 1)
                    message_bar.setBackground(getActivity().getDrawable(R.drawable.message_identifier_no_message));
                else
                    message_bar.setBackground(getActivity().getDrawable(R.drawable.message_identifier_reply_drawable));

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


    private int filterList(List<ChatMessage> chatMessageList) {

/*        new Handler().post(new Runnable() {
            @Override
            public void run() {

            }
        });*/

        List<ChatMessage> messageListReading = chatMessageList;
        //if(!getIsMessageRunning())
        messageList.clear();
        photoMessageList.clear();

        try {

            for (ChatMessage chatMessage : messageListReading) {
                if (chatMessage.getSenderId().equals(userIdChattingWith)) {
                    if(chatMessage.getPhotourl().equals("none")) {

                        if(currentUserPublicKeyID.equals(chatMessage.getPublicKeyID()))
                        messageList.add(chatMessage);
                    }
                    else
                        photoMessageList.add(chatMessage);
                }

                else if (chatMessage.getSenderId().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){

                    try {

                        if(chatMessage.getMessageId()!=null)
                            sendMessageList.add(chatMessage);

                        else
                        {
                            messageListReading.remove(chatMessage);
                        }
                    }catch (Exception e)
                    {
                        messageListReading.remove(chatMessage);
                    }

                }


            }



        } catch (Exception e) {
            //  Toast.makeText(EphemeralChainMessageActivity.this, "Internet is crawling! You might get issues using the App", Toast.LENGTH_LONG).show();
        }



        checkForHint();
        setMessageHighlight(messageHighlight);
        return messageList.size();


    }

    public void checkForHint() {
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

                                Toast.makeText(getContext(),"entered block isMessageRunning line 2356",Toast.LENGTH_SHORT).show();


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
                                    Toast.makeText(getContext(),"Problem in playing animation together using a list",Toast.LENGTH_SHORT).show();
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

                                                message_box.setHintTextColor(getActivity().getResources().getColor(R.color.textThemeColorSemiDark));


                                                final Animation hyperspaceJump = AnimationUtils.loadAnimation(getContext(), R.anim.text_bounce_anim);

                                                hyperspaceJump.setRepeatMode(Animation.INFINITE);

                                                message_box.setAnimation(hyperspaceJump);

                                                continue_message_box_blinking = true;

                                                vibratorRunnable = new Runnable() {
                                                    @Override
                                                    public void run() {

                                                        Vibrator v = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
// Vibrate for 500 milliseconds
                                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                                            v.vibrate(VibrationEffect.createOneShot(75, VibrationEffect.DEFAULT_AMPLITUDE));
                                                        } else {
                                                            //deprecated in API 26
                                                            v.vibrate(75);
                                                        }
                                                        blinkMessageBox();
                                                        setIsMessageRunning(false);

                                                    }
                                                };

                                                vibratorHandler.postDelayed(vibratorRunnable,500);


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

    public void setUpBlink() {
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

    public void checkPresence() {
        final DatabaseReference connectedRef = FirebaseDatabase.getInstance().getReference(".info/connected");
        connectedRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean connected = snapshot.getValue(Boolean.class);
                if (connected) {

                    online_ring.setVisibility(View.VISIBLE);
                    online_status.setVisibility(View.VISIBLE);
                   // Log.d(TAG, "connected");
                   // Toast.makeText(getActivity(),"You are connected!",Toast.LENGTH_SHORT).show();

                   // updateUserPresence(false);
/*                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users")
                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .child("online");

                    databaseReference.setValue(true);
                    databaseReference.onDisconnect().setValue(false);*/


                  //  updateUserPresence(true);

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
                   // updateUserPresence(false);
                    try {
                        Snackbar snackbar = Snackbar
                                .make(app_context_layout, "You are disconnected", Snackbar.LENGTH_SHORT);
                        View snackBarView = snackbar.getView();
                        snackBarView.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.darkGrey));
                        snackbar.show();
                    }catch (Exception e)
                    {
                        //
                    }



                    checkConnectedSatus = true;



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
            sendMessageHash.put("timeStamp",chatMessageReceiverCopy.getTimestamp());




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
            sendMessageHashSenderCopy.put("timeStamp",chatMessageSenderCopy.getTimestamp());


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
