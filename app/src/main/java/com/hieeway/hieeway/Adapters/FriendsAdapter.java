package com.hieeway.hieeway.Adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
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
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hieeway.hieeway.DeleteOptionsDialog;
import com.hieeway.hieeway.Model.ChatStamp;
import com.hieeway.hieeway.Model.Friend;
import com.hieeway.hieeway.Model.User;
import com.hieeway.hieeway.R;
import com.hieeway.hieeway.RemoveFriendDialog;
import com.hieeway.hieeway.Utils.ChatStampListDiffUtilCallback;
import com.hieeway.hieeway.Utils.FriendListDiffUtilCallback;
import com.hieeway.hieeway.VerticalPageActivityPerf;

import java.util.HashMap;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;
import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

public class FriendsAdapter  extends RecyclerView.Adapter<FriendsAdapter.ViewHolder> {

    private Context mContext;
    private Activity activity;
    private List<Friend> mUsers;
    private RemoveFriendDialog removeFriendDialog;

    public FriendsAdapter(Context mContext, List<Friend> mUsers, Activity activity) {


        this.activity = activity;
        this.mUsers = mUsers;
        this.mContext = mContext;


    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.edge_to_edge, viewGroup, false );


        Display display = activity.getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        float dispSize = size.x;

        RelativeLayout chatItem = view.findViewById(R.id.chatbox_tem);

        chatItem.getLayoutParams().height = (int) dispSize * 65 / 100;

        return new FriendsAdapter.ViewHolder(view);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, int i) {

        try {


            viewHolder.longMsgBtn.setVisibility(View.GONE);
            final Friend friend = mUsers.get(viewHolder.getAdapterPosition());
            final int position = viewHolder.getAdapterPosition();

            viewHolder.username.setText(friend.getUsername());


            // viewHolder.username.setTypeface(Typeface.createFromAsset(mContext.getAssets(), "fonts/samsungsharpsans-medium.otf"));


            viewHolder.progressBarOne.setVisibility(View.VISIBLE);
            viewHolder.progressBarTwo.setVisibility(View.VISIBLE);


            checkUserChangeAccountChange(friend.getFriendId(), friend.getPhoto(), friend.getActivePhoto(), viewHolder.user_photo, viewHolder.username);


            viewHolder.user_photo.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {


                    //Unfriend and block options to be built here
                    viewHolder.relativeLayout.animate().scaleX(1.0f).scaleY(1.0f).setDuration(100);


                    Vibrator vb = (Vibrator) mContext.getSystemService(Context.VIBRATOR_SERVICE);
// Vibrate for 500 milliseconds
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        vb.vibrate(VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE));
                    } else {
                        //deprecated in API 26
                        vb.vibrate(50);
                    }


                    removeFriendDialog = new RemoveFriendDialog(mContext, friend.getFriendId(), friend.getUsername());

                    removeFriendDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                    removeFriendDialog.show();

                    //  DeleteOptionsListener deleteOptionsListener;

                    // deleteOptionsListener.setDeleteOptionsDialog(mContext,chatStamp,position,mChatStamps,activity,viewHolder);


                    return true;

                }
            });

            viewHolder.user_photo.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {

                    if (event.getAction() == MotionEvent.ACTION_DOWN) {


                        // viewHolder.user_photo.setAlpha(0.4f);

                        viewHolder.relativeLayout.animate().scaleX(0.95f).scaleY(0.95f).setDuration(0);


                    } else if (event.getAction() == MotionEvent.ACTION_UP) {

                        viewHolder.relativeLayout.animate().scaleX(1.0f).scaleY(1.0f).setDuration(50);


                        //         viewHolder.user_photo.animate().alpha(1.0f).setDuration(50);


                    } else {
                        // viewHolder.user_photo.animate().setDuration(50).alpha(1.0f);


                        viewHolder.relativeLayout.animate().scaleX(1.0f).scaleY(1.0f).setDuration(50);
                    }

                    return false;
                }

            });


        /*viewHolder.user_photo.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if (event.getAction() == MotionEvent.ACTION_DOWN) {


                    viewHolder.user_photo.setAlpha(0.95f);
                    viewHolder.relativeLayout.animate().scaleX(0.95f).scaleY(0.95f).setDuration(0);

                } else if (event.getAction() == MotionEvent.ACTION_UP) {




                            viewHolder.user_photo.animate().alpha(1.0f).setDuration(100);

                    viewHolder.relativeLayout.animate().scaleX(1.0f).scaleY(1.0f).setDuration(100);

                }
                else
                {

                            viewHolder.user_photo.animate().setDuration(100).alpha(1.0f);
                    viewHolder.relativeLayout.animate().scaleX(1.0f).scaleY(1.0f).setDuration(100);


                }

                return false;
            }

        });*/


            try {
                if (friend.getPhoto().equals("default")) {


                    //viewHolder.progressBar.setVisibility(View.INVISIBLE);
                    final Matrix matrix = viewHolder.user_photo.getImageMatrix();
                    matrix.postScale(1, 1);
                    viewHolder.user_photo.setImageMatrix(matrix);
                    viewHolder.user_photo.setImageResource(R.drawable.no_profile);
                    viewHolder.progressBarOne.setVisibility(View.INVISIBLE);
                    viewHolder.progressBarTwo.setVisibility(View.INVISIBLE);

                } else {

                    //  Bitmap bitmap = getBitmapFromURL(chatStamp.getPhoto());

                    RequestOptions requestOptions = new RequestOptions();
                    requestOptions.placeholder(R.color.darkButtonBackground);
                    //requestOptions.centerCrop();
                    // requestOptions.override(200, 400);


                    // Glide.with(mContext).setDefaultRequestOptions(requestOptions).load(chatStamp.getPhoto()).transition(withCrossFade()).into(viewHolder.user_photo);
                    try {
                        Glide.with(mContext).setDefaultRequestOptions(requestOptions).load(friend.getPhoto().replace("s96-c", "s384-c")).listener(new RequestListener<Drawable>() {
                            @Override
                            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {

                                // viewHolder.progressBar.setVisibility(View.INVISIBLE);

                                final Matrix matrix = viewHolder.user_photo.getImageMatrix();
                                final float imageWidth = resource.getIntrinsicWidth();
                                final int screenWidth = mContext.getResources().getDisplayMetrics().widthPixels / 2;
                                //final float scaleRatio = screenWidth / imageWidth;
                                //matrix.postScale(scaleRatio, scaleRatio);
                                matrix.postScale(1, 1);
                                viewHolder.user_photo.setImageMatrix(matrix);

                                viewHolder.progressBarOne.setVisibility(View.INVISIBLE);
                                viewHolder.progressBarTwo.setVisibility(View.INVISIBLE);
                                return false;
                            }
                        }).transition(withCrossFade()).into(viewHolder.user_photo);
                    }catch (Exception e) {

                        final Matrix matrix = viewHolder.user_photo.getImageMatrix();
                        matrix.postScale(1, 1);
                        viewHolder.user_photo.setImageMatrix(matrix);
                        viewHolder.user_photo.setImageDrawable(mContext.getDrawable(R.drawable.no_profile));
                    }
                }
            } catch (Exception e) {
                Log.e("Null pointer exp", "Internet issue");
            }

            viewHolder.user_photo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    if (!viewHolder.username.getText().equals("User Unavailable")) {

              /*  viewHolder.progressBarOne.setVisibility(View.VISIBLE);
                viewHolder.progressBarTwo.setVisibility(View.VISIBLE);*/
                        viewHolder.user_photo.setAlpha(0.5f);
                        viewHolder.relativeLayout.animate().scaleX(0.95f).scaleY(0.95f).setDuration(0);

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Intent intent = new Intent(mContext, VerticalPageActivityPerf.class);
                                intent.putExtra("username", friend.getUsername());
                                intent.putExtra("userid", friend.getFriendId());
                                intent.putExtra("photo", friend.getPhoto());
                                intent.putExtra("activePhoto", friend.getActivePhoto());
                                intent.putExtra("live", "no");
                                mContext.startActivity(intent);
                        /*viewHolder.progressBarOne.setVisibility(View.INVISIBLE);
                        viewHolder.progressBarTwo.setVisibility(View.INVISIBLE);*/
                                viewHolder.user_photo.animate().setDuration(100).alpha(1.0f);
                                viewHolder.relativeLayout.animate().scaleX(1.0f).scaleY(1.0f).setDuration(100);


                            }
                        }, 50);
                    } else {
                        Toast.makeText(mContext, "This account is deleted", Toast.LENGTH_SHORT).show();
                    }

                }
            });
        } catch (NullPointerException e) {
            //
        }

    }




    @Override
    public int getItemCount() {
        return mUsers.size();
    }


    public void setList(List<Friend> mUsers)
    {


        this.mUsers = mUsers;
        notifyDataSetChanged();



    }

    public void updateList(List<Friend> newListChatStamp) {


        FriendListDiffUtilCallback chatStampListDiffUtilCallback = new FriendListDiffUtilCallback(this.mUsers, newListChatStamp);
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(chatStampListDiffUtilCallback);

        mUsers.clear();
        mUsers.addAll(newListChatStamp);

        diffResult.dispatchUpdatesTo(this);


    }


    public class ViewHolder extends RecyclerView.ViewHolder{


        private TextView username, count_message_text;
        // public CircleImageView user_photo;
        private RelativeLayout counterBackgroundLayout;
        private ProgressBar progressBar, progressBarOne, progressBarTwo;
        private ImageView user_photo;
        private RelativeLayout relativeLayout, chatbox_tem_Layout, count_message_layout;
        private Button archiveBtn, delete_chat_head_btn;
        private ImageButton longMsgBtn;


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

    private void checkUserChangeAccountChange(final String userIDChattingWith, final String photo, final String activePhoto, final ImageView user_photo, final TextView username) {


        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users")
                .child(userIDChattingWith);

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                TaskCompletionSource<Bitmap> bitmapTaskCompletionSource = new TaskCompletionSource<>();


                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        if (dataSnapshot.exists()) {
                            try {
                                User user = dataSnapshot.getValue(User.class);


                                try {

                                    if (!user.getActivePhoto().equals("default") && !user.getActivePhoto().equals(activePhoto)) {

                                        HashMap<String, Object> hashMap = new HashMap<>();

                                        hashMap.put("activePhoto", user.getActivePhoto());

                                        FirebaseDatabase.getInstance().getReference("FriendList")
                                                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                                .child(userIDChattingWith)
                                                .updateChildren(hashMap);
                                    }

                                } catch (Exception e) {
                                    //
                                }





                                if (!user.getPhoto().equals(photo) && !user.getPhoto().equals("default")) {

                                    HashMap<String, Object> userPhotoChangehash = new HashMap<>();
                                    userPhotoChangehash.put("photo", user.getPhoto());
                                    DatabaseReference databaseReferenceChatStamp = FirebaseDatabase.getInstance().getReference("FriendList")
                                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                            .child(userIDChattingWith);

                                    databaseReferenceChatStamp.updateChildren(userPhotoChangehash);

                                    try {
                                        //Glide.with(mContext).load(user.getPhoto().replace("s96-c", "s384-c")).into(user_photo);


                                        Bitmap bitmap = Glide.with(mContext)
                                                .asBitmap()
                                                .load(user.getPhoto().replace("s96-c", "s384-c"))
                                                .submit(300, 300)
                                                .get();

                                        bitmapTaskCompletionSource.setResult(bitmap);


                                    } catch (Exception e) {

                                        bitmapTaskCompletionSource.setResult(null);
                                    }

                                }
                                if (user.getPhoto().equals("default")) {
                                    bitmapTaskCompletionSource.setResult(null);
                                }

                            } catch (Exception e) {
                                bitmapTaskCompletionSource.setException(new NullPointerException());
                            }
                        } else {
                            bitmapTaskCompletionSource.setException(new NullPointerException());
                        }
                    }
                }).start();

                Task<Bitmap> bitmapTask = bitmapTaskCompletionSource.getTask();
                bitmapTask.addOnCompleteListener(new OnCompleteListener<Bitmap>() {
                    @Override
                    public void onComplete(@NonNull Task<Bitmap> task) {

                        if (task.isSuccessful()) {

                            if (task.getResult() != null) {
                                Glide.with(mContext).load(task.getResult()).addListener(new RequestListener<Drawable>() {
                                    @Override
                                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                        return false;
                                    }

                                    @Override
                                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {

                                        final Matrix matrix = user_photo.getImageMatrix();
                                        matrix.postScale(1, 1);
                                        //final float scaleRatio = screenWidth / imageWidth;
                                        //matrix.postScale(scaleRatio, scaleRatio);

                                        user_photo.setImageMatrix(matrix);


                                        return false;
                                    }
                                }).into(user_photo);
                            } else {
                                user_photo.setScaleType(ImageView.ScaleType.CENTER_CROP);
                                user_photo.setImageResource(R.drawable.bottom_sheet_background_drawable);
                            }

                        } else {
                            // user_photo.setScaleType(ImageView.ScaleType.CENTER_CROP);
                            user_photo.setImageResource(R.drawable.bottom_sheet_background_drawable);
                            username.setText("User Unavailable");

                            if (task.getException() instanceof NullPointerException) {
                                FirebaseDatabase.getInstance().getReference("FriendList")
                                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                        .child(userIDChattingWith)
                                        .removeValue();

                                FirebaseDatabase.getInstance().getReference("LiveMessages")
                                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                        .child(userIDChattingWith)
                                        .removeValue();

                                FirebaseDatabase.getInstance().getReference("Reports")
                                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                        .child(userIDChattingWith)
                                        .removeValue();

                                FirebaseDatabase.getInstance().getReference("Video")
                                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                        .child(userIDChattingWith)
                                        .removeValue();

                                FirebaseDatabase.getInstance().getReference("ChatList")
                                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                        .child(userIDChattingWith)
                                        .removeValue();
                            }
                        }

                    }
                });


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


}

