package com.shubhamkislay.jetpacklogin.Adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Handler;
import android.os.VibrationEffect;
import android.os.Vibrator;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Base64;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.shubhamkislay.jetpacklogin.EphemeralPhotoActivity;
import com.shubhamkislay.jetpacklogin.Interface.RevealOptionListener;
import com.shubhamkislay.jetpacklogin.Model.ChatMessage;
import com.shubhamkislay.jetpacklogin.Model.ChatMessageCompound;
import com.shubhamkislay.jetpacklogin.Model.ChatStamp;
import com.shubhamkislay.jetpacklogin.R;
import com.shubhamkislay.jetpacklogin.RevealMessageDialog;
import com.shubhamkislay.jetpacklogin.RevealedMessageActivity;

import java.security.Key;
import java.security.KeyFactory;

import java.security.spec.KeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.crypto.Cipher;

public class SendMessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements RevealOptionListener{


    private List<ChatMessage> chatMessageList, receiverMessageList;
    private String userIdChattingWith;
    private Context context;
    private String highlightMessage;
    private int gemCount = 0;
    private static int MSG_SENDING = 0;
    private static int MSG_SENT = 1;
    private static int MSG_PHOTO_SENT =2;
    private static int MSG_PHOTO_SEEN = 5;
    private static int MSG_SEEN = 6;
    private static int MSG_PHOTO_SENDING = 3;
    private static int MSG_SENT_HIGH = 4;
    private static int MSG_REPLY_LAYOUT = 7;
    private static int MSG_REPLY_GOT_REPLY_LAYOUT = 8;
    private static int MSG_GOT_REPLY_LAYOUT = 9;
    private static int MSG_SENT_REPLY = 10;
    private static int MSG_SENDING_REPLY = 11;
    private static int MSG_REPLY_LAYOUT_CHAIN = 12;
    private static int MSG_SENDING_REPLY_CHAIN = 13;
    private static int MSG_REPLY_GOT_REPLY_LAYOUT_CHAIN = 14;
    private static int MSG_SENT_REPLY_CHAIN = 15;
    private static int TIMESTAMP_VIEW = 16;
    private String currentUserPrivateKey;
    private String currentUserPublicKeyID;
    private Activity activity;
    private ChatMessage replyMessage = null;
    private ChatMessage gotReplyMessage = null;
    private ChatMessageCompound chatMessageCompound = null;
    private static final int BLINK_SPEED = 350;
    private static final float SCALE_SIZE = 0.85f;
    private String searchedMessageID;
    private Boolean blinked = false;


    public SendMessageAdapter(Activity activity, Context context, List<ChatMessage> chatMessageList, String userIdChattingWith, List<ChatMessage> receiverMessageList, int gemCount, String currentUserPrivateKey, String currentUserPublicKeyID, String searchMessageID)
    {
        this.context = context;
        this.activity = activity;

        this.chatMessageList = chatMessageList;
        this.userIdChattingWith = userIdChattingWith;
        this.receiverMessageList = receiverMessageList;
        this.searchedMessageID = searchMessageID;
        try {

            this.highlightMessage = receiverMessageList.get(0).getReplyID();
        }catch (Exception e)
        {
            this.highlightMessage = "none";
        }
        this.currentUserPrivateKey = currentUserPrivateKey;
        this.currentUserPublicKeyID = currentUserPublicKeyID;




    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {

        if(i==MSG_SENDING) {

            View view = LayoutInflater.from(context).inflate(R.layout.send_message_sending_layout, parent, false);

            return new SendMessageViewHolder(view);
        }
        else if(i==MSG_SENDING_REPLY) {

            View view = LayoutInflater.from(context).inflate(R.layout.sending_message_reply_layout, parent, false);

            return new SendMessageViewHolder(view);
        }
        else if(i==MSG_SENDING_REPLY_CHAIN) {

            View view = LayoutInflater.from(context).inflate(R.layout.sending_message_reply_layout_chain, parent, false);

            return new SendMessageViewHolder(view);
        }
        else if(i==MSG_PHOTO_SENT)
        {
            View view = LayoutInflater.from(context).inflate(R.layout.send_message_photo_layout, parent, false);

            return new SendMessageViewHolder(view);
        }
        else if(i==MSG_PHOTO_SEEN)
        {
            View view = LayoutInflater.from(context).inflate(R.layout.send_message_seen_photo_layout, parent, false);

            return new SendMessageViewHolder(view);
        }
        else if(i==MSG_SEEN)
        {
            View view = LayoutInflater.from(context).inflate(R.layout.send_message_seen_layout, parent, false);

            return new SendMessageViewHolder(view);
        }
        else if(i==MSG_PHOTO_SENDING)
        {
            View view = LayoutInflater.from(context).inflate(R.layout.send_message_photo_sending_layout, parent, false);

            return new SendMessageViewHolder(view);
        }

        else if(i==MSG_SENT_HIGH)
        {
            View view = LayoutInflater.from(context).inflate(R.layout.send_message_highlight_layout, parent, false);

            return new SendMessageViewHolder(view);
        }
        else if(i==MSG_REPLY_LAYOUT)
        {
            View view = LayoutInflater.from(context).inflate(R.layout.send_message_reply_layout, parent, false);

            return new SendMessageViewHolder(view);
        }
        else if(i==MSG_REPLY_LAYOUT_CHAIN)
        {
            View view = LayoutInflater.from(context).inflate(R.layout.sent_message_reply_layout_chain_seen, parent, false);

            return new SendMessageViewHolder(view);
        }
        else if(i==MSG_GOT_REPLY_LAYOUT)
        {
            View view = LayoutInflater.from(context).inflate(R.layout.send_message_got_reply_layout, parent, false);

            return new SendMessageViewHolder(view);
        }
        else if(i==MSG_REPLY_GOT_REPLY_LAYOUT)
        {
            View view = LayoutInflater.from(context).inflate(R.layout.send_message_reply_got_reply_layout, parent, false);

            return new SendMessageViewHolder(view);
        }
        else if(i==MSG_REPLY_GOT_REPLY_LAYOUT_CHAIN)
        {
            View view = LayoutInflater.from(context).inflate(R.layout.send_message_reply_got_reply_layout_chain, parent, false);

            return new SendMessageViewHolder(view);
        }
        else if(i==MSG_SENT_REPLY)
        {
            View view = LayoutInflater.from(context).inflate(R.layout.send_message_sent_reply_layout, parent, false);

            return new SendMessageViewHolder(view);
        }
        else if(i==MSG_SENT_REPLY_CHAIN)
        {
            View view = LayoutInflater.from(context).inflate(R.layout.send_message_sent_reply_layout_chain, parent, false);

            return new SendMessageViewHolder(view);
        }
        else if(i==TIMESTAMP_VIEW)
        {
            View view = LayoutInflater.from(context).inflate(R.layout.time_stamp_view, parent, false);

            return new TimeStampViewHolder(view);
        }
        else
        {
            View view = LayoutInflater.from(context).inflate(R.layout.send_message_layout, parent, false);

            return new SendMessageViewHolder(view);
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder viewHolder, final int position) {





            final SendMessageViewHolder sendMessageViewHolder = (SendMessageViewHolder) viewHolder;
            final ChatMessage chatMessage = chatMessageList.get(sendMessageViewHolder.getAdapterPosition());


            countGems(userIdChattingWith);

        if (!searchedMessageID.equals("default") && searchedMessageID.equals(chatMessage.getMessageId()) && !blinked) {
            sendMessageViewHolder.messageView.setTextSize(24);
            //sendMessageViewHolder.messageView.setS
            sendMessageViewHolder.messageView.setTypeface(null, Typeface.BOLD);
            sendMessageViewHolder.relativeLayout.setBackground(context.getResources().getDrawable(R.drawable.layer_message_box_highlight_drawable));

            sendMessageViewHolder.relativeLayout.animate().scaleX(SCALE_SIZE).scaleY(SCALE_SIZE).setDuration(BLINK_SPEED);

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    sendMessageViewHolder.relativeLayout.animate().scaleX(1.0f).scaleY(1.0f).setDuration(BLINK_SPEED);
                    //  sendMessageViewHolder.relativeLayout.setBackground(context.getResources().getDrawable(R.drawable.layer_message_box_drawable));

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            sendMessageViewHolder.relativeLayout.animate().scaleX(SCALE_SIZE).scaleY(SCALE_SIZE).setDuration(BLINK_SPEED);
                            // sendMessageViewHolder.relativeLayout.setBackground(context.getResources().getDrawable(R.drawable.layer_message_box_highlight_drawable));
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    sendMessageViewHolder.relativeLayout.animate().scaleX(1.0f).scaleY(1.0f).setDuration(BLINK_SPEED);
                                    //sendMessageViewHolder.relativeLayout.setBackground(context.getResources().getDrawable(R.drawable.layer_message_box_drawable));

                                    new Handler().postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            sendMessageViewHolder.relativeLayout.setBackground(context.getResources().getDrawable(R.drawable.layer_message_box_drawable));
                                            sendMessageViewHolder.messageView.setTextSize(14);
                                            sendMessageViewHolder.messageView.setTypeface(null, Typeface.NORMAL);
                                            blinked = true;

                                        }
                                    }, BLINK_SPEED);

                                }
                            }, BLINK_SPEED);

                        }
                    }, BLINK_SPEED);

                }
            }, BLINK_SPEED);

        }

            if (currentUserPublicKeyID.equals(chatMessage.getPublicKeyID())) {

                if(!chatMessage.getGotReplyID().equals("none"))
                    sendMessageViewHolder.gotReplyTag.setVisibility(View.INVISIBLE);

                if(!chatMessage.getReplyID().equals("none"))
                    sendMessageViewHolder.replyTag.setVisibility(View.INVISIBLE);

                final int pos = sendMessageViewHolder.getAdapterPosition();


                try {
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS");
                    Date parsedDate = dateFormat.parse(chatMessage.getTimestamp());


                    Timestamp timestamp = new java.sql.Timestamp(parsedDate.getTime());

                    // S is the millisecond
                    // SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy' 'HH:mm:ss:S");
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm  dd MMM YYYY");


                    sendMessageViewHolder.timestamp.setText("" + simpleDateFormat.format(timestamp));


                } catch (Exception e) { //this generic but you can control another types of exception
                    // look the origin of excption
                    sendMessageViewHolder.timestamp.setVisibility(View.INVISIBLE);

                }


                // S is the millisecond
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy' 'HH:mm:ss:S");

             /*System.out.println(simpleDateFormat.format(timestamp));
             System.out.println(simpleDateFormat.format(date));*/



                getMessageRevealed(sendMessageViewHolder.gotReplyTag,sendMessageViewHolder.replyTag,chatMessage.getReplyID(),chatMessage.getGotReplyID(),chatMessage,sendMessageViewHolder.getItemViewType());


                String message = decryptRSAToString(chatMessage.getMessageText(), currentUserPrivateKey);


                if (chatMessage.getIfMessageTwo())
                    message = message + "\n" + decryptRSAToString(chatMessage.getMessageTextTwo(), currentUserPrivateKey);

                if (chatMessage.getIfMessageThree())
                    message = message + "\n" + decryptRSAToString(chatMessage.getMessageTextThree(), currentUserPrivateKey);

                sendMessageViewHolder.messageView.setText(message);

                //  sendMessageViewHolder.messageView.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/samsungsharpsans-bold.otf"));

                /*if (chatMessage.getShowGotReplyMsg())
                    sendMessageViewHolder.gotReplyTag.setText(chatMessage.getGotReplyMsg());*/

                /*else if (position + 1 != chatMessageList.size() && chatMessageList.get(position + 1).getShowReplyMsg()
                        && chatMessage.getGotReplyID().equals(chatMessageList.get(position + 1).getReplyID())
                        && !chatMessage.getGotReplyID().equals("none")) {
                    sendMessageViewHolder.gotReplyTag.setText(chatMessageList.get(position + 1).getReplyMsg());
                }*/


                /*if (chatMessage.getShowReplyMsg())
                    sendMessageViewHolder.replyTag.setText(chatMessage.getReplyMsg());

                else if (position - 1 != -1 && chatMessageList.get(position - 1).getShowGotReplyMsg()
                        && chatMessage.getReplyID().equals(chatMessageList.get(position - 1).getGotReplyID())
                        && !chatMessage.getReplyID().equals("none")) {
                    sendMessageViewHolder.replyTag.setText(chatMessageList.get(position - 1).getGotReplyMsg());
                }*/



                sendMessageViewHolder.gotReplyTag.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Vibrator vb = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
// Vibrate for 500 milliseconds
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            vb.vibrate(VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE));
                        } else {
                            //deprecated in API 26
                            vb.vibrate(50);
                        }

                        String searchMessageId = chatMessage.getGotReplyID();
                        Boolean replyMessagePass = chatMessage.getShowGotReplyMsg();

                        RevealMessageDialog revealMessageDialog = new RevealMessageDialog(activity,context, chatMessage, userIdChattingWith, gemCount, pos, searchMessageId,replyMessagePass,"gotReply");

                        revealMessageDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                        revealMessageDialog.setListener(SendMessageAdapter.this);
                        revealMessageDialog.show();


                    }
                });


                sendMessageViewHolder.gotReplyTag.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {

                        if (event.getAction() == MotionEvent.ACTION_DOWN) {


                            sendMessageViewHolder.gotReplyTag.setAlpha(0.75f);

                            sendMessageViewHolder.gotReplyTag.animate().scaleX(0.95f).scaleY(0.95f).setDuration(0);

                        } else if (event.getAction() == MotionEvent.ACTION_UP) {

                            sendMessageViewHolder.gotReplyTag.animate().scaleX(1.0f).scaleY(1.0f).setDuration(100);



                            sendMessageViewHolder.gotReplyTag.animate().alpha(1.0f).setDuration(100);



                        }
                        else
                        {
                            sendMessageViewHolder.gotReplyTag.animate().setDuration(100).alpha(1.0f);

                            sendMessageViewHolder.gotReplyTag.animate().scaleX(1.0f).scaleY(1.0f).setDuration(100);
                        }

                        return false;
                    }

                });


                sendMessageViewHolder.replyTag.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Vibrator vb = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
// Vibrate for 500 milliseconds
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            vb.vibrate(VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE));
                        } else {
                            //deprecated in API 26
                            vb.vibrate(50);
                        }

                        String searchMessageId = chatMessage.getReplyID();

                        Boolean gotReplyMessagePass = chatMessage.getShowReplyMsg();

                        RevealMessageDialog revealMessageDialog = new RevealMessageDialog(activity,context, chatMessage, userIdChattingWith, gemCount, pos, searchMessageId,gotReplyMessagePass,"reply");
                        revealMessageDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        revealMessageDialog.setListener(SendMessageAdapter.this);

                            revealMessageDialog.show();


                    }
                });



                sendMessageViewHolder.replyTag.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {

                        if (event.getAction() == MotionEvent.ACTION_DOWN) {


                            sendMessageViewHolder.replyTag.setAlpha(0.75f);

                            sendMessageViewHolder.replyTag.animate().scaleX(0.95f).scaleY(0.95f).setDuration(0);

                        } else if (event.getAction() == MotionEvent.ACTION_UP) {

                            sendMessageViewHolder.replyTag.animate().scaleX(1.0f).scaleY(1.0f).setDuration(100);



                            sendMessageViewHolder.replyTag.animate().alpha(1.0f).setDuration(100);



                        }
                        else
                        {
                            sendMessageViewHolder.gotReplyTag.animate().setDuration(100).alpha(1.0f);

                            sendMessageViewHolder.gotReplyTag.animate().scaleX(1.0f).scaleY(1.0f).setDuration(100);
                        }

                        return false;
                    }

                });


                if (!chatMessage.getPhotourl().equals("none")) {

                    sendMessageViewHolder.relativeLayout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {


                            Intent intent = new Intent(context, EphemeralPhotoActivity.class);

                            // intent.putExtra("photoList", (Serializable) photoMessageList);
                            intent.putExtra("userIdChattingWith", userIdChattingWith);
                            intent.putExtra("photoUrl", chatMessage.getPhotourl());
                            intent.putExtra("mKey", chatMessage.getMessageId());
                            intent.putExtra("sender", chatMessage.getSenderId());


                            context.startActivity(intent);

                        }
                    });

                    sendMessageViewHolder.message_identifier.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {


                            Intent intent = new Intent(context, EphemeralPhotoActivity.class);

                            // intent.putExtra("photoList", (Serializable) photoMessageList);
                            intent.putExtra("userIdChattingWith", userIdChattingWith);
                            intent.putExtra("photoUrl", chatMessage.getPhotourl());
                            intent.putExtra("mKey", chatMessage.getMessageId());
                            intent.putExtra("sender", chatMessage.getSenderId());


                            context.startActivity(intent);

                        }
                    });
                } else {

                    sendMessageViewHolder.relativeLayout.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View v) {



                            Toast.makeText(context,""+sendMessageViewHolder.getItemViewType(),Toast.LENGTH_SHORT).show();
/*                            Vibrator vb = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
// Vibrate for 500 milliseconds
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                vb.vibrate(VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE));
                            } else {
                                //deprecated in API 26
                                vb.vibrate(50);
                            }

                            RevealMessageDialog revealMessageDialog = new RevealMessageDialog(context, chatMessage, userIdChattingWith, gemCount, pos);

                            revealMessageDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                            revealMessageDialog.setListener(SendMessageAdapter.this);
                            revealMessageDialog.show();*/

                            return false;
                        }
                    });


                }


            } else {
             /*chatMessageList.remove(position);
             notifyDataSetChanged();*/
            }




    }

    private void getMessagesRevealed(final TextView gotReplyTag, final TextView replyTag, final String replyId, final String gotReplyId) {



       // if(!replyId.equals("none")|| !gotReplyId.equals("none")) {
            final DatabaseReference reportReplyIdRef= FirebaseDatabase.getInstance().getReference("Reports")
                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .child(userIdChattingWith);
                    //.child(replyId);


            reportReplyIdRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists())
                    {
                        for(DataSnapshot snapshot: dataSnapshot.getChildren()) {


                            ChatMessage chatMessage = snapshot.getValue(ChatMessage.class);
                            if(chatMessage.getMessageId().equals(replyId))
                                replyTag.setVisibility(View.VISIBLE);

                            if(chatMessage.getMessageId().equals(gotReplyId))
                                gotReplyTag.setVisibility(View.VISIBLE);
                        }

                    }
                    else {
                        //replyTag.setVisibility(View.INVISIBLE);

                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
       // }

       /* if(!gotReplyId.equals("none")) {
            final DatabaseReference reportReplyIdRef= FirebaseDatabase.getInstance().getReference("Reports")
                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .child(userIdChattingWith)
                    .child(gotReplyId);


            reportReplyIdRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists())
                    {
                        gotReplyTag.setVisibility(View.VISIBLE);

                    }
                    else {
                        gotReplyTag.setVisibility(View.INVISIBLE);
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }*/






    }
    private void getMessageRevealed(final TextView gotReplyTag, final TextView replyTag, final String replyId, final String gotReplyId, final ChatMessage chatMessage,final int viewType) {


        if (!replyId.equals("none")/*|| !gotReplyId.equals("none")*/) {
        final DatabaseReference reportReplyIdRef= FirebaseDatabase.getInstance().getReference("Reports")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child(userIdChattingWith)
                .child(replyId);



        reportReplyIdRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                {

                    replyTag.setVisibility(View.VISIBLE);
                    replyTag.setBackground(context.getDrawable(R.drawable.layer_replied_message_box));

                    if(chatMessage.getShowReplyMsg()) {
                        replyTag.setBackground(context.getDrawable(R.drawable.layer_message_pass_drawable));
                        replyTag.setText("You received a view pass for this message");
                    }

                }
                else {
                    replyTag.setVisibility(View.VISIBLE);
                   // replyTag.setAlpha(0.60f);
                    replyTag.setBackground(context.getDrawable(R.drawable.layer_message_unavailable_drawable));
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
         }

        if(!gotReplyId.equals("none")) {
            final DatabaseReference reportReplyIdRef= FirebaseDatabase.getInstance().getReference("Reports")
                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .child(userIdChattingWith)
                    .child(gotReplyId);


            reportReplyIdRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists())
                    {
                        if(chatMessage.getSeen().equals("seen") && viewType!= MSG_SENT_HIGH ) {
                            gotReplyTag.setVisibility(View.VISIBLE);
                            gotReplyTag.setBackground(context.getDrawable(R.drawable.layer_replied_message_box));

                            if(chatMessage.getShowGotReplyMsg()) {
                                gotReplyTag.setBackground(context.getDrawable(R.drawable.layer_message_pass_drawable));
                                gotReplyTag.setText("You received a view pass for this message");
                            }
                        }


                    }
                    else {

                        if(!chatMessage.getSeen().equals("notseen") && viewType!= MSG_SENT_HIGH ) {
                            gotReplyTag.setVisibility(View.VISIBLE);
                            gotReplyTag.setBackground(context.getDrawable(R.drawable.layer_message_unavailable_drawable));
                        }
                       // gotReplyTag.setAlpha(0.60f);


                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }






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

    @Override
    public int getItemCount() {
        return chatMessageList.size();
    }

    @Override
    public int getItemViewType(int position){



            ChatMessage chatMessage = chatMessageList.get(position);


            if (!chatMessage.getPhotourl().equals("none")) {
                if (chatMessage.getSeen().equals("seen"))
                    return MSG_PHOTO_SEEN;
                else {
                    if (chatMessage.getSentStatus().equals("sending")) {

                        return MSG_PHOTO_SENDING;
                    } else
                        return MSG_PHOTO_SENT;
                }
            } else {

                if (chatMessage.getSeen().equals("seen")) {
                    if (chatMessage.getMessageId().equals(highlightMessage))
                        return MSG_SENT_HIGH;

                    else {
                        if (!chatMessage.getReplyID().equals("none")) {

                            if (!chatMessage.getGotReplyID().equals("none")) {
                                if (position != 0 && chatMessageList.get(position - 1).getGotReplyID().equals(chatMessage.getReplyID()))
                                    return MSG_REPLY_GOT_REPLY_LAYOUT_CHAIN;

                                else
                                    return MSG_REPLY_GOT_REPLY_LAYOUT;
                            } else {

                                if (position != 0 && chatMessageList.get(position - 1).getGotReplyID().equals(chatMessage.getReplyID()))
                                    return MSG_REPLY_LAYOUT_CHAIN;

                                else
                                    return MSG_REPLY_LAYOUT;
                            }
                        } else {
                            if (!chatMessage.getGotReplyID().equals("none"))
                                return MSG_GOT_REPLY_LAYOUT;
                            else
                                return MSG_SEEN;
                        }
                    }
                } else {

                    if (chatMessage.getSentStatus().equals("sending")) {

                        if (!chatMessage.getReplyID().equals("none")) {
                            try {
                                if (position != 0 && chatMessageList.get(position - 1).getGotReplyID().equals(chatMessage.getReplyID()))
                                    return MSG_SENDING_REPLY_CHAIN;
                                else
                                    return MSG_SENDING_REPLY;
                            } catch (Exception e) {
                                return MSG_SENDING_REPLY;
                            }
                        } else
                            return MSG_SENDING;
                    } else {
                        if (!chatMessage.getReplyID().equals("none")) {

                            if (position != 0 && chatMessageList.get(position - 1).getGotReplyID().equals(chatMessage.getReplyID()))
                                return MSG_SENT_REPLY_CHAIN;

                            else
                                return MSG_SENT_REPLY;
                        } else
                            return MSG_SENT;
                    }
                }
            }


    }

    @Override
    public void updateGem(int gemCount,int position) {

        this.gemCount = gemCount;

        searchedMessageID = "default";
        notifyDataSetChanged();


    }

    @Override
    public void openRevealActivity(final String userIdChattingWith,final String searchMessageId,final Boolean messagePass,final String messageId,final String tagType) {

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {



                final Intent intent = new Intent(context, RevealedMessageActivity.class);

                intent.putExtra("userIdChattingWith", userIdChattingWith);
                intent.putExtra("searchMessageId", searchMessageId);
                intent.putExtra("messagePass",messagePass);
                intent.putExtra("chatMessageId",messageId);
                intent.putExtra("tagType",tagType);

                context.startActivity(intent);

                // dismiss();

            }
        }, 200);


    }


    private void countGems(String userIdChattingWith) {

        DatabaseReference deleteMessageSenderRef = FirebaseDatabase.getInstance().getReference("ChatList")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child(userIdChattingWith);


        deleteMessageSenderRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.exists()) {
                    ChatStamp chatStamp = dataSnapshot.getValue(ChatStamp.class);
                    try {

                        gemCount = chatStamp.getGemCount();
                    } catch (NullPointerException e) {

                        gemCount = 0;
                    }
                }
                else{

                    gemCount = 0;
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



    }


    public class SendMessageViewHolder extends RecyclerView.ViewHolder {

        TextView messageView;
        Button message_bar;
        RelativeLayout relativeLayout;
        Button message_identifier;
        TextView replyTag;
        TextView gotReplyTag;
        TextView timestamp;

        public SendMessageViewHolder(@NonNull View itemView) {
            super(itemView);

            messageView = itemView.findViewById(R.id.message_view);
            message_bar = itemView.findViewById(R.id.message_bar);
            relativeLayout = itemView.findViewById(R.id.relative_layout);
            message_identifier = itemView.findViewById(R.id.message_identifier);
            replyTag = itemView.findViewById(R.id.reply_tag);
            gotReplyTag = itemView.findViewById(R.id.got_reply_tag);
            timestamp = itemView.findViewById(R.id.timestamp);

        }
    }

        public class TimeStampViewHolder extends RecyclerView.ViewHolder {

            TextView timeStampView;


            public TimeStampViewHolder(@NonNull View itemView) {
                super(itemView);

                timeStampView = itemView.findViewById(R.id.timestamp_textview);

            }


    }


    public int getItemPosition(String chatMessageID) {
        for (ChatMessage chatMessage : chatMessageList) {
            if (chatMessage.getMessageId().equals(chatMessageID))
                return chatMessageList.indexOf(chatMessage);
        }
        return -1;
    }



}
