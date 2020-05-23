package com.shubhamkislay.jetpacklogin.Adapters;


import android.annotation.SuppressLint;
import android.app.Activity;

import android.content.Context;

import static android.content.Context.MODE_PRIVATE;
import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Handler;
import android.os.VibrationEffect;
import android.os.Vibrator;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.shubhamkislay.jetpacklogin.ArchiveActivity;
import com.shubhamkislay.jetpacklogin.DeleteOptionsDialog;
import com.shubhamkislay.jetpacklogin.EphemeralMessageViewModel;
import com.shubhamkislay.jetpacklogin.Interface.DeleteOptionsListener;
import com.shubhamkislay.jetpacklogin.Model.ChatMessage;
import com.shubhamkislay.jetpacklogin.Model.ChatStamp;
import com.shubhamkislay.jetpacklogin.Model.CheckPendingMessageAsyncModel;
import com.shubhamkislay.jetpacklogin.Model.User;
import com.shubhamkislay.jetpacklogin.PaletteActivity;
import com.shubhamkislay.jetpacklogin.R;
import com.shubhamkislay.jetpacklogin.VerticalPageActivity;
import com.shubhamkislay.jetpacklogin.VerticalRegisterationActivity;
import com.shubhamkislay.jetpacklogin.VideoEncryptionActivity;
import com.shubhamkislay.jetpacklogin.YoutubePlayerActivity;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ChatMessageAdapter extends RecyclerView.Adapter<ChatMessageAdapter.ViewHolder> implements DeleteOptionsListener {

    private Context mContext;
    private List<ChatMessage> countMessages = new ArrayList<>();
    public static final int ODD_MESSAGE = 1;
    public static final int EVEN_MESSAGE = 2;

    public static final String SHARED_PREFS = "sharedPrefs";
    public static final String PRIVATE_KEY = "privateKey";
    public static final String PUBLIC_KEY = "publicKey";
    public String privateKeyText, publicKeyText;

    private Boolean backgroundHighlight = false;
    private Activity activity;
    private List<ChatStamp> mChatStamps;
    private List<ChatStamp> newListChatStamp = new ArrayList<>();
    RelativeLayout relativeLayout;
    private EphemeralMessageViewModel ephemeralMessageViewModel;
    FirebaseAuth firebaseAuth;
    String userPic = null;
    Boolean chatPending = false;
    DeleteOptionsDialog deleteOptionsDialog;
    DeleteOptionsListener deleteOptionsListener;
    public static final String PUBLIC_KEY_ID = "publicKeyID";
    private  String publicKeyId = null;
    int spanCount;
    public static final int MSG_FIRST_ROW= 1,MSG_NOT_PEND=0,MSG_FIRST_ITEM = 2;


    public ChatMessageAdapter(Context mContext, List<ChatStamp> mUsers, Activity activity/*, DeleteOptionsListener deleteOptionsListener*/) {


        this.activity = activity;
        this.mChatStamps = mUsers;
        this.mContext = mContext;
        this.deleteOptionsListener = deleteOptionsListener;

        SharedPreferences sharedPreferences = mContext.getSharedPreferences(SHARED_PREFS,MODE_PRIVATE);
        publicKeyId = sharedPreferences.getString(PUBLIC_KEY_ID,null);


        privateKeyText = sharedPreferences.getString(PRIVATE_KEY,null);
        publicKeyText = sharedPreferences.getString(PUBLIC_KEY,null);
        publicKeyId = sharedPreferences.getString(PUBLIC_KEY_ID,null);


      /*  int newpos = 0;
        int oldListpos = 0;

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Messages")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        databaseReference.keepSynced(true);

        Display display = activity.getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        float displayWidth = size.x;


         // 3 columns
         // 50px
        boolean includeEdge = true;

        if(displayWidth>=1920)
            spanCount=4;

        else if(displayWidth>=1080)
            spanCount=3;

        else if(displayWidth>=500)
            spanCount=2;
        else
            spanCount=1;

        for( newpos = 0;newpos<spanCount;newpos++)
        {

            ChatStamp chatStamp = new ChatStamp("#","#","#","#","#",false);
            newListChatStamp.add(chatStamp);
        }


        for(int i = newpos-1;i<mChatStamps.size()-1+spanCount;i++)
        {
            newListChatStamp.add(mChatStamps.get(oldListpos));
            oldListpos++;
        }

        *//*for(int i= 0;i<spanCount;i++)
        {
            ChatStamp chatStamp = new ChatStamp("#","#","#","#","#",false);
            newListChatStamp.add(chatStamp);
            oldListpos++;
        }*/




    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {

      //  if(viewType == ODD_MESSAGE) {



            View view = LayoutInflater.from(mContext).inflate(R.layout.edge_to_edge, viewGroup, false);


            return new ChatMessageAdapter.ViewHolder(view);
/*        }


        else
        {
            View view = LayoutInflater.from(mContext).inflate(R.layout.edge_to_edge_second, viewGroup, false);


            return new ChatMessageAdapter.ViewHolder(view);
        }*/




    }

    @SuppressLint({"CheckResult", "ClickableViewAccessibility"})
    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, final int i) {


            final ChatStamp chatStamp = mChatStamps.get(viewHolder.getAdapterPosition());
            final int position = viewHolder.getAdapterPosition();
        CheckPendingMessageAsyncModel checkPendingMessageAsyncModel = new CheckPendingMessageAsyncModel();

        viewHolder.username.setText(chatStamp.getUsername());


       // viewHolder.username.setTypeface(Typeface.createFromAsset(mContext.getAssets(), "fonts/samsungsharpsans-medium.otf"));
        firebaseAuth = FirebaseAuth.getInstance();

       // viewHolder.progressBar.setVisibility(View.VISIBLE);
        viewHolder.progressBarOne.setVisibility(View.VISIBLE);
        viewHolder.progressBarTwo.setVisibility(View.VISIBLE);



        checkPendingMessageAsyncModel.setUserChattingWith(chatStamp.getId());
        checkPendingMessageAsyncModel.setRelativeLayout(viewHolder.relativeLayout);
        checkPendingMessageAsyncModel.setCountMsgLayout(viewHolder.count_message_layout);
        checkPendingMessageAsyncModel.setCountMessageText(viewHolder.count_message_text);



        checkPendingMessages(chatStamp.getId(), viewHolder.relativeLayout,viewHolder.count_message_layout,viewHolder.count_message_text);
        checkUserChangeAccountChange(chatStamp.getId(),chatStamp.getPhoto(),viewHolder.user_photo);

      //  new CheckPendingMessagesAsyncTask().execute(checkPendingMessageAsyncModel);





        viewHolder.archiveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(mContext, ArchiveActivity.class);
                        intent.putExtra("userIdChattingWith", chatStamp.getId());

                        mContext.startActivity(intent);

                    }
                }, 200);


            }
        });

        viewHolder.longMsgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            //    Intent intent = new Intent(mContext, AutoTextSizeChangeActivity.class);
          //Intent intent = new Intent(mContext, AlphaActivity.class);

                /**
                 * Uncomment the below snippet to get the final version
                  */

/*                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        Intent intent = new Intent(mContext, RevealReplyActivity.class);


                intent.putExtra("userIdChattingWith", chatStamp.getId());
                intent.putExtra("currentUserPrivateKey", privateKeyText);
                intent.putExtra("currentUserPublicKeyID", publicKeyId);


                        mContext.startActivity(intent);

                    }
                }, 200);*/


                /**
                 * WebRTC testing activity
                 */
                /*Intent intent = new Intent(mContext, WebRTCActivity.class);
                intent.putExtra("userIdChattingWith", chatStamp.getId());
                mContext.startActivity(intent);*/
                //mContext.startActivity(new Intent(mContext, VerticalRegisterationActivity.class));

                /*FirebaseMessaging.getInstance().unsubscribeFromTopic(chatStamp.getUsername()).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(mContext, "unsubscribed to " + chatStamp.getUsername(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });*/


                Intent intent = new Intent(mContext, YoutubePlayerActivity.class);
                intent.putExtra("userIdChattingWith", chatStamp.getId());
                mContext.startActivity(intent);

                //mContext.startActivity(new Intent(mContext, PaletteActivity.class));


            }
        });



        viewHolder.user_photo.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if (event.getAction() == MotionEvent.ACTION_DOWN) {


                    viewHolder.user_photo.setAlpha(0.6f);

                    viewHolder.relativeLayout.animate().scaleX(0.95f).scaleY(0.95f).setDuration(0);

                } else if (event.getAction() == MotionEvent.ACTION_UP) {

                    viewHolder.relativeLayout.animate().scaleX(1.0f).scaleY(1.0f).setDuration(50);



                            viewHolder.user_photo.animate().alpha(1.0f).setDuration(50);



                }
                else
                {
                    viewHolder.user_photo.animate().setDuration(100).alpha(1.0f);

                    viewHolder.relativeLayout.animate().scaleX(1.0f).scaleY(1.0f).setDuration(50);
                }

                return false;
            }

        });


        viewHolder.user_photo.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

/*                int[] itemIds = {R.id.delete_for_me_option, R.id.delete_for_all_option};


                String[] colors = {"Delete for me", "Delete for All"};

                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setTitle("Delete Options");
                builder.setItems(colors, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // the user clicked on colors[which]
                    }
                });
                builder.show();*/
                viewHolder.user_photo.animate().setDuration(100).alpha(1.0f);

                viewHolder.relativeLayout.animate().scaleX(1.0f).scaleY(1.0f).setDuration(100);


                Vibrator vb = (Vibrator) mContext.getSystemService(Context.VIBRATOR_SERVICE);
// Vibrate for 500 milliseconds
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    vb.vibrate(VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE));
                } else {
                    //deprecated in API 26
                    vb.vibrate(50);
                }


                deleteOptionsDialog = new DeleteOptionsDialog(mContext,chatStamp,position,mChatStamps,activity,viewHolder);

                deleteOptionsDialog.setListener(ChatMessageAdapter.this);

                deleteOptionsDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                deleteOptionsDialog.show();

              //  DeleteOptionsListener deleteOptionsListener;

                // deleteOptionsListener.setDeleteOptionsDialog(mContext,chatStamp,position,mChatStamps,activity,viewHolder);


                return true;
            }
        });


        viewHolder.delete_chat_head_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Delete functions

                /**
                 * Deletes only the chat head and not the messages
                 * To be used when the user wants to just delete the chat head but, no the the messages.
                 */
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

               // notifyItemRemoved(i);

                notifyItemRangeChanged(i,0);


            }
        });


        try {
            if (chatStamp.getPhoto().equals("default")) {

                viewHolder.user_photo.setImageResource(R.drawable.no_profile);
                //viewHolder.progressBar.setVisibility(View.INVISIBLE);

                viewHolder.progressBarOne.setVisibility(View.INVISIBLE);
                viewHolder.progressBarTwo.setVisibility(View.INVISIBLE);

            } else {

                //  Bitmap bitmap = getBitmapFromURL(chatStamp.getPhoto());

                RequestOptions requestOptions = new RequestOptions();
                requestOptions.placeholder(R.color.darkGrey);
                requestOptions.centerCrop();
               // requestOptions.override(200, 400);




               // Glide.with(mContext).setDefaultRequestOptions(requestOptions).load(chatStamp.getPhoto()).transition(withCrossFade()).into(viewHolder.user_photo);
                try {
                    Glide.with(mContext).setDefaultRequestOptions(requestOptions).load(chatStamp.getPhoto().replace("s96-c", "s384-c")).listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {

                           // viewHolder.progressBar.setVisibility(View.INVISIBLE);

                            viewHolder.progressBarOne.setVisibility(View.INVISIBLE);
                            viewHolder.progressBarTwo.setVisibility(View.INVISIBLE);
                            return false;
                        }
                    }).transition(withCrossFade()).into(viewHolder.user_photo);
                }catch (Exception e)
                {

                    viewHolder.user_photo.setImageDrawable(mContext.getDrawable(R.drawable.hieeway_background_blurred));
                }
                /*Glide.with(mContext).asBitmap().load(chatStamp.getPhoto()).into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {

                        Bitmap roundedCorner = getRoundedCornerBitmap(resource);
                        viewHolder.user_photo.setImageBitmap(roundedCorner);

                    }
                });*/

            }
        } catch (Exception e) {
            Log.e("Null pointer exp", "Internet issue");
        }





        viewHolder.user_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        try{
                            deleteOptionsDialog.hide();

                        }catch (Exception e)
                        {

                        }
                        Intent intent = new Intent(mContext, VerticalPageActivity.class);
                        intent.putExtra("username", chatStamp.getUsername());
                        intent.putExtra("userid", chatStamp.getId());
                        intent.putExtra("photo", chatStamp.getPhoto());
                        intent.putExtra("live", "no");
                        //   intent.putExtra("otherUserPublicKey",chatStamp.getPublicKey());

                        mContext.startActivity(intent);

                    }
                }, 50);



            }
        });






    }

    private void checkUserChangeAccountChange(final String userChattingWith, final String photo, final ImageView user_photo) {

        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users")
                .child(userChattingWith);

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                {
                    User user = dataSnapshot.getValue(User.class);
                    if(!user.getPhoto().equals(photo)&&!user.getPhoto().equals("default"))
                    {

                            HashMap<String, Object> userPhotoChangehash = new HashMap<>();
                            userPhotoChangehash.put("photo", user.getPhoto());
                            DatabaseReference databaseReferenceChatStamp = FirebaseDatabase.getInstance().getReference("ChatList")
                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .child(userChattingWith);

                            databaseReferenceChatStamp.updateChildren(userPhotoChangehash);

                            try {
                                Glide.with(mContext).load(user.getPhoto().replace("s96-c", "s384-c")).into(user_photo);
                            }
                            catch (Exception e)
                            {

                                user_photo.setImageResource(R.drawable.no_profile);
                            }

                    }
                    if(user.getPhoto().equals("default"))
                    {
                        user_photo.setImageResource(R.drawable.no_profile);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    @Override
    public int getItemCount() {
        return mChatStamps.size();
    }


    public void setList(List<ChatStamp> mChatStamps)
    {

       // this.chatRead = chatRead;
        this.mChatStamps = mChatStamps;
        notifyDataSetChanged();



    }

    @Override
    public void onDeleteForAll(int position, int mChatStampsListSize,RecyclerView.ViewHolder viewHolder) {


        int pos = viewHolder.getAdapterPosition();

        ChatStamp chatStamp = mChatStamps.get(pos);
        mChatStamps.remove(chatStamp);

        notifyItemRemoved(pos);


    }

    @Override
    public void onDeleteForMe(int position, int mChatStampsListSize,RecyclerView.ViewHolder viewHolder) {


       int pos = viewHolder.getAdapterPosition();

        ChatStamp chatStamp = mChatStamps.get(pos);
        mChatStamps.remove(chatStamp);

        notifyItemRemoved(pos);
    }


    public class ViewHolder extends RecyclerView.ViewHolder{


        public TextView username, count_message_text;
       // public CircleImageView user_photo;
        public RelativeLayout counterBackgroundLayout;
        public ProgressBar progressBar, progressBarOne, progressBarTwo;
        public ImageView user_photo;
        public RelativeLayout relativeLayout, chatbox_tem_Layout, count_message_layout;
        public Button archiveBtn, longMsgBtn, delete_chat_head_btn;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            progressBar = itemView.findViewById(R.id.loading_image_progress_bar);
            progressBarOne = itemView.findViewById(R.id.progress_one);
            progressBarTwo = itemView.findViewById(R.id.progress_two);
            username = itemView.findViewById(R.id.username);
            user_photo = itemView.findViewById(R.id.user_photo);
            relativeLayout = itemView.findViewById(R.id.chat_back_parent_layout);

          //  counterBackgroundLayout = itemView.findViewById(R.id.message_count_back_chatlist);

            count_message_layout = itemView.findViewById(R.id.count_message_layout);
            count_message_text = itemView.findViewById(R.id.count_message_text);
            chatbox_tem_Layout = itemView.findViewById(R.id.chatbox_tem);
            archiveBtn = itemView.findViewById(R.id.archive_btn);
            longMsgBtn = itemView.findViewById(R.id.long_msg_btn);
            delete_chat_head_btn = itemView.findViewById(R.id.delete_chat_head_btn);


        }

    }

    public static Bitmap getRoundedCornerBitmap(Bitmap bitmap) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);
        final float roundPx = 12;

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        return output;
    }

    public static Bitmap getBitmapFromURL(String src) {
        try {
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            // Log exception
            return null;
        }
    }








    public void checkPendingMessages(final String userChattingWith, final RelativeLayout relativeLayout, final RelativeLayout countMsgLayout, final TextView countMessageText)
    {

        countMessages.clear();
        DatabaseReference checkPendingMessagesRef = FirebaseDatabase.getInstance().getReference("Messages")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child(userChattingWith);

        checkPendingMessagesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                countMessages.clear();

                for(DataSnapshot snapshot: dataSnapshot.getChildren())
                {


                    ChatMessage chatMessage = snapshot.getValue(ChatMessage.class);

                    try {
                        if(publicKeyId.equals(null))
                            Toast.makeText(mContext,"PublicKeyId is empty",Toast.LENGTH_SHORT).show();

                        if (chatMessage.getSenderId().equals(userChattingWith)&&chatMessage.getPublicKeyID().equals(publicKeyId))
                            countMessages.add(chatMessage);
                    }catch (NullPointerException ne)
                    {

                    }




                }

                if(countMessages.size()>0) {
                    relativeLayout.setBackground(mContext.getDrawable(R.drawable.chatlist_item_back_new_message));
                    countMessageText.setText(""+countMessages.size());
                    countMsgLayout.setVisibility(View.VISIBLE);
                  //  counterBackgroundLayout.setVisibility(View.VISIBLE);

                }

                else {
                    relativeLayout.setBackground(mContext.getDrawable(R.drawable.chatlist_item_back));
                    countMsgLayout.setVisibility(View.INVISIBLE);
                   // counterBackgroundLayout.setVisibility(View.INVISIBLE);
                }




            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        countMessages.clear();

    }


}
