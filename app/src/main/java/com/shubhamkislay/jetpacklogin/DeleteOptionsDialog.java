package com.shubhamkislay.jetpacklogin;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.shubhamkislay.jetpacklogin.Interface.BrushFragmentListener;
import com.shubhamkislay.jetpacklogin.Interface.ChatStampSizeListener;
import com.shubhamkislay.jetpacklogin.Interface.DeleteOptionsListener;
import com.shubhamkislay.jetpacklogin.Model.ChatStamp;

import java.util.List;

public class DeleteOptionsDialog extends Dialog {




    public Context context;
    public Dialog d;
    private int position;
    private ChatStamp chatStamp;
    private Activity activity;
    private List<ChatStamp> mChatStamps;
    private TextView deleteForMe, deleteForAll, optionsTag, deleteForMeDesc, deleteForAllDesc;
    private DeleteOptionsListener deleteOptionsListener;
    private ChatStampSizeListener chatStampSizeListener;
    RecyclerView.ViewHolder viewHolder;
    private RelativeLayout delete_for_all_option_layout,delete_for_me_option_layout;


    public DeleteOptionsDialog(Context context, ChatStamp chatStamp, int position,
                               List<ChatStamp> mChatStamps, Activity activity, RecyclerView.ViewHolder viewHolder) {



        super(context);
        this.context=context;
        this.chatStamp = chatStamp;
        this.position = position;
        this.mChatStamps = mChatStamps;
        this.activity = activity;
        this.viewHolder = viewHolder;
        chatStampSizeListener = (NavButtonTest) activity;
    }



    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);



        setContentView(R.layout.delete_options_dialog);



        deleteForMe = findViewById(R.id.delete_for_me_option);

        deleteForAll = findViewById(R.id.delete_for_all_option);

        optionsTag = findViewById(R.id.options_txt);

        deleteForMeDesc = findViewById(R.id.delete_for_me_des);
        deleteForAllDesc = findViewById(R.id.delete_for_all_des);

        delete_for_all_option_layout = findViewById(R.id.delete_for_all_option_layout);

        delete_for_me_option_layout = findViewById(R.id.delete_for_me_option_layout);



/*        deleteForAll.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/samsungsharpsans-bold.otf"));
        deleteForMe.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/samsungsharpsans-bold.otf"));
        optionsTag.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/samsungsharpsans-bold.otf"));
        deleteForAllDesc.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/samsungsharpsans.otf"));
        deleteForMeDesc.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/samsungsharpsans.otf"));*/


        delete_for_all_option_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                deleteForAll();

                chatStampSizeListener.setChatStampSizeActivity(mChatStamps.size());
                deleteOptionsListener.onDeleteForAll(position,mChatStamps.size(),viewHolder);



            }
        });

        delete_for_all_option_layout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN)
                {
                    delete_for_all_option_layout.setAlpha(0.5f);
                    delete_for_all_option_layout.animate().scaleY(0.9f).scaleX(0.9f).setDuration(0);
                }
                if(event.getAction() == MotionEvent.ACTION_UP)
                {
                    delete_for_all_option_layout.setAlpha(1.0f);
                    delete_for_all_option_layout.animate().scaleY(1.0f).scaleX(1.0f).setDuration(50);
                }

                return false;
            }
        });

        deleteForAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteForAll();
                chatStampSizeListener.setChatStampSizeActivity(mChatStamps.size());
                deleteOptionsListener.onDeleteForAll(position,mChatStamps.size(),viewHolder);
            }
        });

        deleteForAll.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN)
                {
                    delete_for_all_option_layout.setAlpha(0.5f);
                    delete_for_all_option_layout.animate().scaleY(0.9f).scaleX(0.9f).setDuration(0);
                }
                if(event.getAction() == MotionEvent.ACTION_UP)
                {
                    delete_for_all_option_layout.setAlpha(1.0f);
                    delete_for_all_option_layout.animate().scaleY(1.0f).scaleX(1.0f).setDuration(50);
                }

                return false;
            }
        });


        delete_for_me_option_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                deleteForMe();
                chatStampSizeListener.setChatStampSizeActivity(mChatStamps.size());
                deleteOptionsListener.onDeleteForMe(position,mChatStamps.size(),viewHolder);
            }
        });

        delete_for_me_option_layout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN)
                {
                    delete_for_me_option_layout.setAlpha(0.5f);
                    delete_for_me_option_layout.animate().scaleY(0.9f).scaleX(0.9f).setDuration(0);
                }
                if(event.getAction() == MotionEvent.ACTION_UP)
                {
                    delete_for_me_option_layout.setAlpha(1.0f);
                    delete_for_me_option_layout.animate().scaleY(1.0f).scaleX(1.0f).setDuration(50);
                }

                return false;
            }
        });


        deleteForMe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteForMe();
                chatStampSizeListener.setChatStampSizeActivity(mChatStamps.size());
                deleteOptionsListener.onDeleteForMe(position,mChatStamps.size(),viewHolder);
            }
        });

        deleteForMe.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN)
                {
                    delete_for_me_option_layout.setAlpha(0.5f);
                    delete_for_me_option_layout.animate().scaleY(0.9f).scaleX(0.9f).setDuration(0);
                }
                if(event.getAction() == MotionEvent.ACTION_UP)
                {
                    delete_for_me_option_layout.setAlpha(1.0f);
                    delete_for_me_option_layout.animate().scaleY(1.0f).scaleX(1.0f).setDuration(50);
                }

                return false;
            }
        });





    }

    public void setListener(DeleteOptionsListener deleteOptionsListener) {
        this.deleteOptionsListener = deleteOptionsListener;
    }

    private void deleteForAll()
    {

//                deleteOptionsListener.onDeleteForAll();

        //      Toast.makeText(context,"Delete for all",Toast.LENGTH_SHORT).show();

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("ChatList")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child(chatStamp.getId());

        // mChatStamps.remove(i);

        databaseReference.removeValue();


        /**
         * The following code deletes the chat heads from both the users, chat fragment.
         * Use this when the user wants to remove its digital footprint from the other users phone
         * To be only used if the other user has no pending messages left from the current user
         * Use it to add after the code followed by the following code is live in action.
         */

        DatabaseReference databaseReferenceOtherPerson = FirebaseDatabase.getInstance().getReference("ChatList")
                .child(chatStamp.getId())
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());


        databaseReferenceOtherPerson.removeValue();


        /**
         * Add the following to delete the messages also, when the delete button is clicked!
         * The user must be able to delete the pinned and sent messages also after the following code.
         */


        DatabaseReference deleteAllMessagesForCurrentUserRef = FirebaseDatabase.getInstance().getReference("Messages")
                .child(chatStamp.getId())
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());


        deleteAllMessagesForCurrentUserRef.removeValue();

        DatabaseReference deleteAllMessagesForOtherUserRef = FirebaseDatabase.getInstance().getReference("Messages")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child(chatStamp.getId());

        deleteAllMessagesForOtherUserRef.removeValue();


        hide();

    }

    private void deleteForMe()
    {


                //       deleteOptionsListener.onDeleteForMe();
                // Toast.makeText(context,"Delete for me",Toast.LENGTH_SHORT).show();

                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("ChatList")
                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .child(chatStamp.getId());

                // mChatStamps.remove(i);

                databaseReference.removeValue();

                DatabaseReference deleteAllMessagesForOtherUserRef = FirebaseDatabase.getInstance().getReference("Messages")
                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .child(chatStamp.getId());

                deleteAllMessagesForOtherUserRef.removeValue();


                hide();

    }

}
