package com.shubhamkislay.jetpacklogin.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.shubhamkislay.jetpacklogin.Model.ChatMessage;
import com.shubhamkislay.jetpacklogin.R;
import com.shubhamkislay.jetpacklogin.VerticalPageActivity;

import java.security.Key;
import java.security.KeyFactory;
import java.security.spec.KeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.crypto.Cipher;

public class RevealRequestsAdapter extends RecyclerView.Adapter<RevealRequestsAdapter.ViewHolder> {


    private List<ChatMessage> chatMessageList;
    private Context context;
    private String currentUserPrivateKey;
    private String currentUserPublicKeyID;
    private String userIdChattingWith;
    RelativeLayout messageBody;
    TextView replyTag;
    TextView gotReplyTag;
    TextView messageView;
    TextView timestampView;
    Button acceptBtn, rejectBtn;
    private String username;
    private String photo;


    public RevealRequestsAdapter(List<ChatMessage> chatMessageList, Context context, String currentUserPrivateKey, String currentUserPublicKeyID, String userIdChattingWith, String username, String photo)
    {
        this.chatMessageList = chatMessageList;
        this.context = context;
        this.currentUserPrivateKey = currentUserPrivateKey;
        this.currentUserPublicKeyID = currentUserPublicKeyID;
        this.userIdChattingWith = userIdChattingWith;
        this.username = username;
        this.photo = photo;
    }

    @NonNull
    @Override
    public RevealRequestsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View view = LayoutInflater.from(context).inflate(R.layout.reveal_request_layout_view, viewGroup, false);

        return new ViewHolder(view);
    }

    public String decryptRSAToString(String encryptedBase64, String privateKey) {

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

    private void getMessageRevealed(final TextView gotReplyTag, final TextView replyTag, final String replyId, final String gotReplyId, final ChatMessage chatMessage, Boolean showGotReply, Boolean showReply) {


        if (!replyId.equals("none") && showReply) {
            final DatabaseReference reportReplyIdRef = FirebaseDatabase.getInstance().getReference("Reports")
                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .child(userIdChattingWith)
                    .child(replyId);


            reportReplyIdRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        ChatMessage chatMessage1 = dataSnapshot.getValue(ChatMessage.class);
                        replyTag.setVisibility(View.VISIBLE);

                        String messsage = decryptRSAToString(chatMessage1.getMessageText(), currentUserPrivateKey);
                        if (chatMessage1.getIfMessageTwo())
                            messsage = messsage + "\n" + decryptRSAToString(chatMessage1.getMessageTextTwo(), currentUserPrivateKey);
                        if (chatMessage1.getIfMessageThree())
                            messsage = messsage + "\n" + decryptRSAToString(chatMessage1.getMessageTextThree(), currentUserPrivateKey);

                        replyTag.setText(messsage);
                    } else {
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

        if (!gotReplyId.equals("none") && showGotReply) {
            final DatabaseReference reportReplyIdRef = FirebaseDatabase.getInstance().getReference("Reports")
                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .child(userIdChattingWith)
                    .child(gotReplyId);


            reportReplyIdRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        /*if(!chatMessage.getSeen().equals("notseen")) {
                            gotReplyTag.setVisibility(View.VISIBLE);*/

                        ChatMessage chatMessage1 = dataSnapshot.getValue(ChatMessage.class);
                        gotReplyTag.setVisibility(View.VISIBLE);
                        String messsage = decryptRSAToString(chatMessage1.getMessageText(), currentUserPrivateKey);
                        if (chatMessage1.getIfMessageTwo())
                            messsage = messsage + "\n" + decryptRSAToString(chatMessage1.getMessageTextTwo(), currentUserPrivateKey);
                        if (chatMessage1.getIfMessageThree())
                            messsage = messsage + "\n" + decryptRSAToString(chatMessage1.getMessageTextThree(), currentUserPrivateKey);

                        gotReplyTag.setText(messsage);
                        //  }
                    } else {

                        //  if(!chatMessage.getSeen().equals("notseen") )
                        gotReplyTag.setVisibility(View.INVISIBLE);
                        // gotReplyTag.setAlpha(0.60f);

                        gotReplyTag.setBackground(context.getDrawable(R.drawable.layer_message_unavailable_drawable));
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }


    }

    @Override
    public int getItemCount() {
        return chatMessageList.size();
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onBindViewHolder(@NonNull final RevealRequestsAdapter.ViewHolder viewHolder, int i) {

        final ChatMessage chatMessage = chatMessageList.get(viewHolder.getAdapterPosition());


        //Ths is try catch block is to add the timestamp
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS");
            Date parsedDate = dateFormat.parse(chatMessage.getTimeStamp());


            Timestamp timestamp = new java.sql.Timestamp(parsedDate.getTime());

            // S is the millisecond
            // SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy' 'HH:mm:ss:S");
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm  dd MMM YYYY");


            timestampView.setText("" + simpleDateFormat.format(timestamp));


        } catch (Exception e) { //this generic but you can control another types of exception
            // look the origin of excption
            timestampView.setVisibility(View.INVISIBLE);

        }


        if (!chatMessage.getReplyID().equals("none"))
            replyTag.setVisibility(View.INVISIBLE);

        if(!chatMessage.getGotReplyID().equals("none"))
            gotReplyTag.setVisibility(View.INVISIBLE);


        String message = decryptRSAToString(chatMessage.getMessageText(),currentUserPrivateKey);
        if(chatMessage.getIfMessageTwo())
            message = message +"\n"+decryptRSAToString(chatMessage.getMessageTextTwo(),currentUserPrivateKey);

        if(chatMessage.getIfMessageThree())
            message = message +"\n"+decryptRSAToString(chatMessage.getMessageTextThree(),currentUserPrivateKey);

        messageView.setText(message);

        messageBody.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, VerticalPageActivity.class);
                intent.putExtra("revealmessage", "yes");
                intent.putExtra("messageID", chatMessage.getMessageId());
                intent.putExtra("userid", userIdChattingWith);
                intent.putExtra("username", username);
                intent.putExtra("photo", photo);
                intent.putExtra("live", "no");

                context.startActivity(intent);
            }
        });

        getMessageRevealed(gotReplyTag,replyTag,chatMessage.getReplyID(),chatMessage.getGotReplyID(),chatMessage, chatMessage.getShowGotReplyMsg(),chatMessage.getShowReplyMsg());


        acceptBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!chatMessage.getReplyID().equals("none") && chatMessage.getShowReplyMsg()) {
                    final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Messages")
                            .child(userIdChattingWith)
                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .child(chatMessage.getReplyID());

                    databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                HashMap<String,Object> messageHash = new HashMap<>();
                                messageHash.put("showGotReplyMsg",true);

                                databaseReference.updateChildren(messageHash);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                }
                if (!chatMessage.getGotReplyID().equals("none") && chatMessage.getShowGotReplyMsg()) {
                    final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Messages")
                            .child(userIdChattingWith)
                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .child(chatMessage.getGotReplyID());

                    databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                HashMap<String,Object> messageHash = new HashMap<>();
                                messageHash.put("showReplyMsg",true);

                                databaseReference.updateChildren(messageHash);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                }

                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("MessageRequests")
                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .child(userIdChattingWith)
                        .child(chatMessage.getMessageId());


                chatMessageList.remove(chatMessage);
                notifyItemRemoved(viewHolder.getAdapterPosition());
                databaseReference.removeValue();


            }
        });

        messageBody.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if (event.getAction() == MotionEvent.ACTION_DOWN) {


                    messageBody.setAlpha(0.75f);

                    messageBody.animate().scaleX(0.95f).scaleY(0.95f).setDuration(0);

                } else if (event.getAction() == MotionEvent.ACTION_UP) {

                    messageBody.animate().scaleX(1.0f).scaleY(1.0f).setDuration(100);


                    messageBody.animate().alpha(1.0f).setDuration(100);


                } else {
                    messageBody.animate().setDuration(100).alpha(1.0f);

                    messageBody.animate().scaleX(1.0f).scaleY(1.0f).setDuration(100);
                }

                return false;
            }

        });


        rejectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("MessageRequests")
                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .child(userIdChattingWith)
                        .child(chatMessage.getMessageId());


                chatMessageList.remove(chatMessage);
                notifyItemRemoved(viewHolder.getAdapterPosition());

                databaseReference.removeValue();
            }
        });


    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            replyTag = itemView.findViewById(R.id.reply_tag);
            gotReplyTag = itemView.findViewById(R.id.got_reply_tag);
            messageView = itemView.findViewById(R.id.message_view);
            timestampView = itemView.findViewById(R.id.timestamp);
            acceptBtn = itemView.findViewById(R.id.acceptBtn);
            rejectBtn = itemView.findViewById(R.id.rejectBtn);
            messageBody = itemView.findViewById(R.id.relative_layout);


        }
    }
}
