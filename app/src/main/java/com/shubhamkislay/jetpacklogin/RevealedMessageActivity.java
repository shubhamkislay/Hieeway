package com.shubhamkislay.jetpacklogin;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.shubhamkislay.jetpacklogin.Model.ChatMessage;
import com.shubhamkislay.jetpacklogin.Model.ChatStamp;

import java.security.Key;
import java.security.KeyFactory;
import java.security.spec.KeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.HashMap;

import javax.crypto.Cipher;

public class RevealedMessageActivity extends AppCompatActivity {


    private TextView message_text_dummy;
    private TextView message_text;
    private TextView message_text_two;
    private TextView message_text_three;
    private String messageText;
    private String messageID;
    private String messageTextTwo;
    private String messageTextThree;
    private int gemCount;
    private int messageTextSize;
    private String replyingToId;
    private String gotReplyId;
    private String searchMessageId;
    private String chatMessageId;
    private int found = 0;
    public static final String SHARED_PREFS = "sharedPrefs";
    public static final String PRIVATE_KEY = "privateKey";
    public static final String PUBLIC_KEY = "publicKey";
    public static final String PUBLIC_KEY_ID = "publicKeyID";
    private Boolean getIfMessageTwo = false;
    private Boolean getIfMessageThree = false;
    private Boolean dataExists = false;
    public ValueEventListener reportValueEventListener;
    public ValueEventListener eventListener;
    public DatabaseReference reportReferenceOne;
    public Boolean changeViews = false;
    private String  userIdChattingWith;
    private Boolean messagePass = false;
    private String messageId, tagType = null;
    private Boolean gotReply = false,reply =false;
    private Button change_activity;

    String publicKeyText, privateKeyText, otherUserPublicKey, otherUserPublicKeyID, publicKeyId;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_revealed_message);

        message_text_dummy = findViewById(R.id.message_text_dummy);
        message_text = findViewById(R.id.message_text);
        message_text_two = findViewById(R.id.message_text_two);
        message_text_three = findViewById(R.id.message_text_three);

        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS,MODE_PRIVATE);

        privateKeyText = sharedPreferences.getString(PRIVATE_KEY,null);
        publicKeyText = sharedPreferences.getString(PUBLIC_KEY,null);
        publicKeyId = sharedPreferences.getString(PUBLIC_KEY_ID,null);

        Intent intent = getIntent();
        userIdChattingWith = intent.getStringExtra("userIdChattingWith");
        searchMessageId = intent.getStringExtra("searchMessageId");
        messagePass = intent.getBooleanExtra("messagePass",false);
        chatMessageId = intent.getStringExtra("chatMessageId");
        tagType = intent.getStringExtra("tagType");



        messageID = searchMessageId;

        if(tagType.equals("reply"))
            reply = false;

        else if(tagType.equals("gotReply"))
            gotReply = false;


       // populateViews();
        revealMessageActivity();



    }

    private void findViews()
    {

    }

    private void populateViews() {

        message_text.setText(messageText);
        message_text.setTextSize((float) messageTextSize);

        if(!messageTextTwo.equals(messageID)) {
            message_text_two.setText(messageTextTwo);
            message_text.setTextSize((float) messageTextSize);

        }

        if(!messageTextThree.equals(messageID)) {
            message_text_three.setText(messageTextThree);
            message_text.setTextSize((float) messageTextSize);
        }

        playMessages();
    }

    private void playMessages() {

        final DatabaseReference userChatListRef = FirebaseDatabase.getInstance().getReference("ChatList")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child(userIdChattingWith);
        if(!messagePass) {



            userChatListRef.runTransaction(new Transaction.Handler() {
                @NonNull
                @Override
                public Transaction.Result doTransaction(@NonNull MutableData mutableData) {

                    if (mutableData == null)
                        return null;

                    ChatStamp chatStamp = mutableData.getValue(ChatStamp.class);

                    if (chatStamp.getGemCount() >= 0) {
                        int gemCt = chatStamp.getGemCount();
                        gemCt = gemCt - 1;
                        gemCount = gemCt;
                        chatStamp.setGemCount(gemCt);
                        mutableData.setValue(chatStamp);
                        return Transaction.success(mutableData);
                    }


                    return null;
                }

                @Override
                public void onComplete(@Nullable DatabaseError databaseError, boolean b, @Nullable DataSnapshot dataSnapshot) {

                    if (b) {


                        found += 1;
                    } else {
                        Toast.makeText(RevealedMessageActivity.this, "Cannot retrieve the message", Toast.LENGTH_SHORT).show();
                        //findViews();
                    }
                }
            });
        }
        else
        {

            if(tagType.equals("reply")) {

                HashMap<String, Object> hashMap = new HashMap<>();

                hashMap.put("showReplyMsg", false);

                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Messages")
                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .child(userIdChattingWith)
                        .child(chatMessageId);

                databaseReference.updateChildren(hashMap);
            }
            else if(tagType.equals("gotReply"))
            {
                HashMap<String, Object> hashMap = new HashMap<>();

                hashMap.put("showGotReplyMsg", false);

                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Messages")
                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .child(userIdChattingWith)
                        .child(chatMessageId);

                databaseReference.updateChildren(hashMap);
            }



        }

        message_text.setAlpha(1.0f);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {


                if(messageTextTwo.equals(messageID)&&messageTextThree.equals(messageID)) {
                    finish();
                }
                else
                {
                    if(!messageTextTwo.equals(messageID))
                    {
                        message_text.setAlpha(0.5f);
                        message_text_two.setVisibility(View.VISIBLE);
                        message_text_two.setAlpha(1.0f);
                        new Handler().postDelayed(new Runnable() {
                                                      @Override
                                                      public void run() {

                                                          if(!messageTextThree.equals(messageID))
                                                          {

                                                              message_text.setAlpha(0.25f);
                                                              message_text_two.setAlpha(0.5f);
                                                              message_text_three.setVisibility(View.VISIBLE);
                                                              new Handler().postDelayed(new Runnable() {
                                                                  @Override
                                                                  public void run() {
                                                                      finish();
                                                                  }
                                                              }, messageTextThree.length()* 75 + 750);
                                                          }
                                                          else
                                                          {
                                                             finish();
                                                          }

                                                      }
                                                  },messageTextTwo.length()* 75 + 750);
                    }
                    else{
                        if(!messageTextThree.equals(messageID))
                        {

                            message_text.setAlpha(0.25f);
                            message_text_two.setAlpha(0.5f);
                            message_text_three.setVisibility(View.VISIBLE);
                            message_text_three.setAlpha(1.0f);
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    finish();
                                }
                            }, messageTextThree.length()* 75 + 750);
                        }
                        else
                        {
                            finish();
                        }
                    }

                }


            }
        },messageText.length()* 75 + 750);


    }

    public void revealMessageActivity()
    {

        reportReferenceOne = FirebaseDatabase.getInstance().getReference("Reports")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child(userIdChattingWith)
                .child(searchMessageId);






        reportValueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.exists()) {

                   // for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                        ChatMessage chatMessageRep = dataSnapshot.getValue(ChatMessage.class);


                        if (chatMessageRep.getMessageId().equals(searchMessageId)) {

                            getIfMessageTwo = chatMessageRep.getIfMessageTwo();
                            getIfMessageThree = chatMessageRep.getIfMessageThree();


                            messageText = decryptRSAToString(chatMessageRep.getMessageText(), privateKeyText);
                            if (getIfMessageTwo)
                                messageTextTwo = decryptRSAToString(chatMessageRep.getMessageTextTwo(), privateKeyText);
                            else
                                messageTextTwo = messageID;

                            if (getIfMessageThree)
                                messageTextThree = decryptRSAToString(chatMessageRep.getMessageTextThree(), privateKeyText);
                            else
                                messageTextThree = messageID;

                            messageId = chatMessageRep.getMessageId();

                            messageTextSize = chatMessageRep.getTextSize();



                            populateViews();

                            //reportReferenceOne.removeEventListener(reportValueEventListener);


                        } /*else {

                            Toast.makeText(RevealedMessageActivity.this, "Message doesn't exist", Toast.LENGTH_SHORT).show();
                            reportReferenceOne.removeEventListener(reportValueEventListener);
                            finish();
                        }*/
                  //  }
/*                    if(found==0)
                    {
                        Toast.makeText(RevealedMessageActivity.this, "Message doesn't exist", Toast.LENGTH_SHORT).show();
                        //reportReferenceOne.removeEventListener(reportValueEventListener);
                        finish();
                    }*/
                }
                else
                {
                    Toast.makeText(RevealedMessageActivity.this, "Message unavailable", Toast.LENGTH_SHORT).show();
                    //reportReferenceOne.removeEventListener(reportValueEventListener);
                    finish();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {


            }
        };

        reportReferenceOne.addValueEventListener(reportValueEventListener);


/*        revealOptionListener.updateGem(gemCount,position);



        hide();*/

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
}
