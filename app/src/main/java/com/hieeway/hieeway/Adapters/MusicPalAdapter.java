package com.hieeway.hieeway.Adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.hieeway.hieeway.Model.Pal;
import com.hieeway.hieeway.Model.User;
import com.hieeway.hieeway.MusicPalService;
import com.hieeway.hieeway.R;
import com.hieeway.hieeway.VerticalPageActivity;

import java.util.HashMap;
import java.util.List;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;
import static com.hieeway.hieeway.MusicPalService.MUSIC_PAL_SERVICE_RUNNING;

public class MusicPalAdapter extends RecyclerView.Adapter<MusicPalAdapter.ViewHolder> {

    FirebaseAuth firebaseAuth;
    private Context mContext;
    private Activity activity;
    private List<User> mUsers;

    public MusicPalAdapter(Context mContext, List<User> mUsers, Activity activity) {


        this.activity = activity;
        this.mUsers = mUsers;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.edge_to_edge, viewGroup, false);
        return new MusicPalAdapter.ViewHolder(view);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, int i) {


        viewHolder.longMsgBtn.setVisibility(View.GONE);
        final User user = mUsers.get(viewHolder.getAdapterPosition());
        final int position = viewHolder.getAdapterPosition();

        viewHolder.username.setText(user.getUsername());


        // viewHolder.username.setTypeface(Typeface.createFromAsset(mContext.getAssets(), "fonts/samsungsharpsans-medium.otf"));
        firebaseAuth = FirebaseAuth.getInstance();


        viewHolder.progressBarOne.setVisibility(View.VISIBLE);
        viewHolder.progressBarTwo.setVisibility(View.VISIBLE);


        viewHolder.user_photo.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {


                //Unfriend and block options to be built here

                return false;
            }
        });

        viewHolder.user_photo.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if (event.getAction() == MotionEvent.ACTION_DOWN) {


                    viewHolder.user_photo.setAlpha(0.95f);
                    viewHolder.relativeLayout.animate().scaleX(0.95f).scaleY(0.95f).setDuration(0);

                } else if (event.getAction() == MotionEvent.ACTION_UP) {


                    viewHolder.user_photo.animate().alpha(1.0f).setDuration(50);

                    viewHolder.relativeLayout.animate().scaleX(1.0f).scaleY(1.0f).setDuration(50);

                } else {

                    viewHolder.user_photo.animate().setDuration(50).alpha(1.0f);
                    viewHolder.relativeLayout.animate().scaleX(1.0f).scaleY(1.0f).setDuration(50);


                }

                return false;
            }

        });


        try {
            if (user.getPhoto().equals("default")) {

                viewHolder.user_photo.setImageResource(R.drawable.no_profile);
                //viewHolder.progressBar.setVisibility(View.INVISIBLE);

                viewHolder.progressBarOne.setVisibility(View.INVISIBLE);
                viewHolder.progressBarTwo.setVisibility(View.INVISIBLE);

            } else {

                //  Bitmap bitmap = getBitmapFromURL(user.getPhoto());

                RequestOptions requestOptions = new RequestOptions();
                requestOptions.placeholder(R.color.darkGrey);
                requestOptions.centerCrop();
                // requestOptions.override(200, 400);


                // Glide.with(mContext).setDefaultRequestOptions(requestOptions).load(user.getPhoto()).transition(withCrossFade()).into(viewHolder.user_photo);
                try {
                    Glide.with(mContext).setDefaultRequestOptions(requestOptions).load(user.getPhoto().replace("s96-c", "s384-c")).listener(new RequestListener<Drawable>() {
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
                } catch (Exception e) {

                    viewHolder.user_photo.setImageDrawable(mContext.getDrawable(R.drawable.no_profile));
                }
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
                        /*FirebaseDatabase.getInstance().getReference("Pal")
                                .child(user.getUserid())
                                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        if (dataSnapshot.exists()) {
                                            Pal pal = dataSnapshot.getValue(Pal.class);

                                            if (pal.getConnection().equals(user.getUserid())) {
                                                HashMap<String, Object> palHash = new HashMap<>();

                                                palHash.put("connection", "join");
                                                palHash.put("songID", "default");


                                                FirebaseDatabase.getInstance().getReference("Pal")
                                                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                                        .child(user.getUserid())
                                                        .updateChildren(palHash);

                                                FirebaseDatabase.getInstance().getReference("Pal")
                                                        .child(user.getUserid())
                                                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                                        .updateChildren(palHash);

                                                Intent musicPalService = new Intent(activity, MusicPalService.class);

                                                musicPalService.putExtra("otherUserId", user.getUserid());
                                                musicPalService.putExtra("username", user.getUsername());
                                                musicPalService.putExtra("userPhoto",user.getPhoto());

                                                activity.startService(musicPalService);

                                                Toast.makeText(mContext,"Connecting your music with "+user.getUsername(),Toast.LENGTH_SHORT).show();
                                                activity.finish();
                                            } else {
                                                HashMap<String, Object> palHash = new HashMap<>();

                                                palHash.put("connection", FirebaseAuth.getInstance().getCurrentUser().getUid());
                                                palHash.put("songID", "default");


                                                FirebaseDatabase.getInstance().getReference("Pal")
                                                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                                        .child(user.getUserid())
                                                        .updateChildren(palHash);

                                                FirebaseDatabase.getInstance().getReference("Pal")
                                                        .child(user.getUserid())
                                                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                                        .updateChildren(palHash);

                                                Intent musicPalService = new Intent(activity, MusicPalService.class);

                                                musicPalService.putExtra("otherUserId", user.getUserid());
                                                musicPalService.putExtra("username", user.getUsername());
                                                musicPalService.putExtra("userPhoto",user.getPhoto());

                                                activity.startService(musicPalService);
                                                Toast.makeText(mContext,"Connecting your music with "+user.getUsername(),Toast.LENGTH_SHORT).show();

                                                activity.finish();
                                            }


                                        } else {
                                            HashMap<String, Object> palHash = new HashMap<>();

                                            palHash.put("connection", FirebaseAuth.getInstance().getCurrentUser().getUid());
                                            palHash.put("songID", "default");


                                            FirebaseDatabase.getInstance().getReference("Pal")
                                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                                    .child(user.getUserid())
                                                    .updateChildren(palHash);

                                            FirebaseDatabase.getInstance().getReference("Pal")
                                                    .child(user.getUserid())
                                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                                    .updateChildren(palHash);

                                            Intent musicPalService = new Intent(mContext, MusicPalService.class);

                                            musicPalService.putExtra("otherUserId", user.getUserid());
                                            musicPalService.putExtra("username", user.getUsername());
                                            musicPalService.putExtra("userPhoto",user.getPhoto());

                                            mContext.startService(musicPalService);
                                            Toast.makeText(mContext,"Connecting your music with "+user.getUsername(),Toast.LENGTH_SHORT).show();
                                            activity.finish();
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });*/

                        if (MUSIC_PAL_SERVICE_RUNNING)
                            Toast.makeText(mContext, "You are already connected to someone", Toast.LENGTH_SHORT).show();

                        else {

                            FirebaseDatabase.getInstance().getReference("Pal")
                                    .child(user.getUserid())
                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .runTransaction(new Transaction.Handler() {
                                        @NonNull
                                        @Override
                                        public Transaction.Result doTransaction(@NonNull MutableData mutableData) {

                                            try {
                                                Pal pal = mutableData.getValue(Pal.class);
                                                if (pal.getConnection().equals("join")) {


                                                    FirebaseDatabase.getInstance().getReference("Pal")
                                                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                                            .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {

                                                            if (task.isSuccessful()) {
                                                                HashMap<String, Object> palHash = new HashMap<>();

                                                                palHash.put("connection", "join");
                                                                palHash.put("songID", "default");
                                                                palHash.put("senderId", FirebaseAuth.getInstance().getCurrentUser().getUid());

                                                                FirebaseDatabase.getInstance().getReference("Pal")
                                                                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                                                        .child(user.getUserid())
                                                                        .updateChildren(palHash);

                                                                Intent musicPalService = new Intent(activity, MusicPalService.class);

                                                                musicPalService.putExtra("otherUserId", user.getUserid());
                                                                musicPalService.putExtra("username", user.getUsername());
                                                                musicPalService.putExtra("userPhoto", user.getPhoto());

                                                                activity.startService(musicPalService);

                                                                Toast.makeText(mContext, "Connecting your music with " + user.getUsername(), Toast.LENGTH_SHORT).show();
                                                                activity.finish();
                                                            }
                                                        }
                                                    });


                                                } else {


                                                    FirebaseDatabase.getInstance().getReference("Pal")
                                                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                                            .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if (task.isSuccessful()) {
                                                                HashMap<String, Object> palHash = new HashMap<>();

                                                                palHash.put("connection", "join");
                                                                palHash.put("songID", "default");
                                                                palHash.put("senderId", FirebaseAuth.getInstance().getCurrentUser().getUid());

                                                                FirebaseDatabase.getInstance().getReference("Pal")
                                                                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                                                        .child(user.getUserid())
                                                                        .updateChildren(palHash);

                                                            /*HashMap<String, Object> palRequestHash = new HashMap<>();

                                                            palRequestHash.put("connection", "request");
                                                            palRequestHash.put("songID", "default");


                                                            FirebaseDatabase.getInstance().getReference("Pal")
                                                                    .child(user.getUserid())
                                                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                                                    .updateChildren(palRequestHash);*/

                                                                Intent musicPalService = new Intent(activity, MusicPalService.class);

                                                                musicPalService.putExtra("otherUserId", user.getUserid());
                                                                musicPalService.putExtra("username", user.getUsername());
                                                                musicPalService.putExtra("userPhoto", user.getPhoto());

                                                                activity.startService(musicPalService);

                                                                Toast.makeText(mContext, "Connecting your music with " + user.getUsername(), Toast.LENGTH_SHORT).show();
                                                                activity.finish();
                                                            }
                                                        }
                                                    });


                                                }
                                            } catch (Exception e) {


                                                FirebaseDatabase.getInstance().getReference("Pal")
                                                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                                        .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {

                                                        HashMap<String, Object> palHash = new HashMap<>();

                                                        palHash.put("connection", "join");
                                                        palHash.put("songID", "default");
                                                        palHash.put("senderId", FirebaseAuth.getInstance().getCurrentUser().getUid());

                                                        FirebaseDatabase.getInstance().getReference("Pal")
                                                                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                                                .child(user.getUserid())
                                                                .updateChildren(palHash);

                                                        //HashMap<String, Object> palRequestHash = new HashMap<>();

                                                    /*palRequestHash.put("connection", "request");
                                                    palRequestHash.put("songID", "default");


                                                    FirebaseDatabase.getInstance().getReference("Pal")
                                                            .child(user.getUserid())
                                                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                                            .updateChildren(palRequestHash);*/

                                                        Intent musicPalService = new Intent(activity, MusicPalService.class);

                                                        musicPalService.putExtra("otherUserId", user.getUserid());
                                                        musicPalService.putExtra("username", user.getUsername());
                                                        musicPalService.putExtra("userPhoto", user.getPhoto());

                                                        activity.startService(musicPalService);

                                                        Toast.makeText(mContext, "Connecting your music with " + user.getUsername(), Toast.LENGTH_SHORT).show();
                                                        activity.finish();

                                                    }
                                                });


                                            }

                                            return null;
                                        }

                                        @Override
                                        public void onComplete(@Nullable DatabaseError databaseError, boolean b, @Nullable DataSnapshot dataSnapshot) {

                                        }
                                    });


                        }
                    }
                }, 50);


            }

        });

    }


    @Override
    public int getItemCount() {
        return mUsers.size();
    }


    public void setList(List<User> mUsers) {


        this.mUsers = mUsers;
        notifyDataSetChanged();


    }


    public class ViewHolder extends RecyclerView.ViewHolder {


        public TextView username, count_message_text;
        // public CircleImageView user_photo;
        public RelativeLayout counterBackgroundLayout;
        public ProgressBar progressBar, progressBarOne, progressBarTwo;
        public ImageView user_photo;
        public RelativeLayout relativeLayout, chatbox_tem_Layout, count_message_layout;
        public Button archiveBtn, delete_chat_head_btn;
        public ImageButton longMsgBtn;


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


}

