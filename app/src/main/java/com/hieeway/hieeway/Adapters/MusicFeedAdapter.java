package com.hieeway.hieeway.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.palette.graphics.Palette;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hieeway.hieeway.CustomCircularView;
import com.hieeway.hieeway.Model.Like;
import com.hieeway.hieeway.Model.Music;
import com.hieeway.hieeway.Model.MusicPost;
import com.hieeway.hieeway.Model.PostSeen;
import com.hieeway.hieeway.R;
import com.spotify.android.appremote.api.ConnectionParams;
import com.spotify.android.appremote.api.SpotifyAppRemote;
import com.spotify.protocol.client.CallResult;
import com.spotify.protocol.types.Empty;

import org.ocpsoft.prettytime.PrettyTime;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class MusicFeedAdapter extends RecyclerView.Adapter<MusicFeedAdapter.ViewHolder> {

    private static final int REQUEST_CODE = 1337;
    private static final String REDIRECT_URI = "http://10.0.2.2:8888/callback";
    private static final String CLIENT_ID = "79c53faf8b67451b9adf996d40285521";
    public SpotifyAppRemote spotifyAppRemote;
    List<MusicPost> musicPostList;
    Activity activity;
    private Context mContext;
    private Palette.Swatch vibrantSwatch;
    private Palette.Swatch mutedSwatch;
    private Palette.Swatch lightVibrantSwatch;
    private Palette.Swatch darkVibrantSwatch;
    private Palette.Swatch dominantSwatch;
    private Palette.Swatch lightMutedSwatch;
    private Palette.Swatch darkMutedSwatch;
    private int swatchNumber;
    private Handler mHandler;
    private String userID;
    private String photo;
    private String otherUid;
    private String username;


    public MusicFeedAdapter(Context mContext
            , String userID
            , List<MusicPost> musicPostList
            , Activity activity
            , SpotifyAppRemote spotifyAppRemote
            , String username
            , String photo
            , String otherUid) {
        this.mContext = mContext;
        this.userID = userID;
        this.musicPostList = musicPostList;
        this.spotifyAppRemote = spotifyAppRemote;
        this.activity = activity;
        this.photo = photo;
        this.username = username;
        this.otherUid = otherUid;

    }

    @NonNull
    @Override
    public MusicFeedAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.spotify_status_item, parent, false);


        ConnectionParams connectionParams =
                new ConnectionParams.Builder(CLIENT_ID)
                        .setRedirectUri(REDIRECT_URI)
                        .setPreferredThumbnailImageSize(1500)
                        .setPreferredImageSize(1500)
                        .showAuthView(true)
                        .build();


        Display display = activity.getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        float displayHeight = size.y;

        RelativeLayout like_btn_back = view.findViewById(R.id.like_btn_back);
        Button like_btn = view.findViewById(R.id.like_btn);
        ImageButton play_btn = view.findViewById(R.id.play_btn);


        play_btn.getLayoutParams().height = (int) (displayHeight * 100) / 975;
        play_btn.getLayoutParams().width = (int) (displayHeight * 100) / 975;

        like_btn_back.getLayoutParams().width = (int) (displayHeight * 100) / 1200;
        like_btn_back.getLayoutParams().height = (int) (displayHeight * 100) / 1200;

        like_btn.getLayoutParams().width = (int) (displayHeight * 100) / 2400;
        like_btn.getLayoutParams().height = (int) (displayHeight * 100) / 2400;

        ImageView songArt = (ImageView) view.findViewById(R.id.song_art);

        songArt.getLayoutParams().height = (int) (displayHeight * 100) / 250;
        songArt.getLayoutParams().width = (int) (displayHeight * 100) / 250;

        TextView songName = view.findViewById(R.id.song_name);
        TextView artistName = view.findViewById(R.id.artist_name);

        songName.getLayoutParams().width = (int) (displayHeight * 100) / 250;
        artistName.getLayoutParams().width = (int) (displayHeight * 100) / 250;
        
        
        

        return new MusicFeedAdapter.ViewHolder(view);
    }

    @Override
    public void onViewAttachedToWindow(@NonNull ViewHolder holder) {
        // super.onViewAttachedToWindow(holder);
        MusicPost musicPost = musicPostList.get(holder.getAdapterPosition());

        /**
         * Below code plays the songs as soon as it is visible on the screen
         */

        /*try {
            spotifyAppRemote.getPlayerApi().play(musicPost.getSpotifyId()).setResultCallback(new CallResult.ResultCallback<Empty>() {
                @Override
                public void onResult(Empty empty) {
                    Toast.makeText(mContext, "Playing \"" + musicPost.getSpotifySong() + "\"", Toast.LENGTH_SHORT).show();
                }
            });

        } catch (Exception e) {
            Toast.makeText(mContext, "Cannot play this song", Toast.LENGTH_SHORT).show();

        }*/

        /*HashMap<String, Object> postSeenHash = new HashMap<>();


       String updateSeen = null;

        //updateSeen.add(userID);
        try {
            updateSeen = musicPost.getSeenBy();
            if (!musicPost.getSeenBy().contains(userID))
                updateSeen = musicPost.getSeenBy()+"/-/"+userID;
            else if(updateSeen==null) {
                updateSeen = userID;
            }
        }catch (Exception e)
        {
            updateSeen = userID;
        }
        postSeenHash.put("seenBy",updateSeen);*/

        FirebaseDatabase.getInstance().getReference("MusicPost")
                .child(userID)
                .child(musicPost.getPostKey())
                .removeValue();
        //.updateChildren(postSeenHash);

                /*.child("seenBy")
                .setValue(updateSeen);*/

        FirebaseDatabase.getInstance().getReference("Post")
                .child(userID)
                .child(musicPost.getPostKey())
                .removeValue();
        //.updateChildren(postSeenHash);
               /* .child("seenBy")
                .setValue(updateSeen);*/

        HashMap<String, Object> postSeenHash = new HashMap<>();

        postSeenHash.put("username", username);
        postSeenHash.put("photo", photo);

        DatabaseReference seenByRef = FirebaseDatabase
                .getInstance()
                .getReference("SeenBy")
                .child(userID);

        seenByRef
                .child(musicPost.getPostKey())
                .child(seenByRef.push().getKey())
                .updateChildren(postSeenHash);


    }

    @Override
    public void onBindViewHolder(@NonNull MusicFeedAdapter.ViewHolder holder, int position) {
        MusicPost musicPost = musicPostList.get(holder.getAdapterPosition());


        mHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message message) {
                // This is where you do your work in the UI thread.
                // Your worker tells you in the message what to do.


                Runnable runnable = message.getCallback();
                new Handler().post(runnable);
            }
        };
        if (!photo.equals("default"))
            Glide.with(mContext).load(photo).addListener(new RequestListener<Drawable>() {
                @Override
                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                    return false;
                }

                @Override
                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {


                    final Matrix matrix = holder.profile_pic.getImageMatrix();
                    final float imageWidth = resource.getIntrinsicWidth();
                    final int screenWidth = mContext.getResources().getDisplayMetrics().widthPixels;
                    final float scaleRatio = screenWidth / imageWidth;
                    //matrix.postScale(scaleRatio, scaleRatio);
                    matrix.postScale(1, 1);
                    holder.profile_pic.setImageMatrix(matrix);

                    return false;
                }
            }).into(holder.profile_pic);

        else {
            holder.profile_pic.setScaleType(ImageView.ScaleType.CENTER_CROP);
            holder.profile_pic.setImageDrawable(activity.getDrawable(R.drawable.no_profile));
        }

        holder.username.setText(username + " played...");
        holder.song_name.setText(musicPost.getSpotifySong());
        holder.artist_name.setText(musicPost.getSpotifyArtist());
        holder.song_name.setSelected(true);
        holder.artist_name.setSelected(true);

        holder.username.setTypeface(Typeface.createFromAsset(mContext.getAssets(), "fonts/samsungsharpsans-bold.otf"));
        holder.song_name.setTypeface(Typeface.createFromAsset(mContext.getAssets(), "fonts/samsungsharpsans-bold.otf"));
        holder.artist_name.setTypeface(Typeface.createFromAsset(mContext.getAssets(), "fonts/samsungsharpsans-bold.otf"));
        holder.open_spotify_text.setTypeface(Typeface.createFromAsset(mContext.getAssets(), "fonts/samsungsharpsans-bold.otf"));
        holder.time.setTypeface(Typeface.createFromAsset(mContext.getAssets(), "fonts/samsungsharpsans-bold.otf"));


        // holder.foreground_lay.animate().alpha(0.0f).setDuration(300);

        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
            Date parsedDate = dateFormat.parse(musicPost.getTimestamp());

            PrettyTime prettyTime = new PrettyTime(Locale.getDefault());
            String ago = prettyTime.format(parsedDate).replace("moments ago", "now");


            holder.time.setText("" + ago);


        } catch (Exception e) { //this generic but you can control another types of exception
            // look the origin of excption


        }

        try {

            FirebaseDatabase.getInstance().getReference("Likes")
                    .child(otherUid)
                    .child(musicPost.getPostKey())
                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            Like like = dataSnapshot.getValue(Like.class);
                            try {
                                if (like.getSong().equals(musicPost.getSpotifySong()))
                                    holder.like_btn.setBackgroundTintList(ColorStateList.valueOf(mContext.getResources().getColor(R.color.colorRed)));
                            } catch (Exception e) {

                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
        } catch (Exception e) {
            //
        }


        spotifyAppRemote.getImagesApi().getImage(musicPost.getSpotifyCover())
                .setResultCallback(new CallResult.ResultCallback<Bitmap>() {
                    @Override
                    public void onResult(Bitmap bitmap) {

                        holder.song_art.setImageBitmap(bitmap);

                        holder.song_art.animate().alpha(1.0f).setDuration(500);


                        try {
                            Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
                                @Override
                                public void onGenerated(@Nullable Palette palette) {

                                    Palette.Swatch currentSwatch = null;

                                    vibrantSwatch = palette.getVibrantSwatch();
                                    darkVibrantSwatch = palette.getDarkVibrantSwatch();
                                    lightVibrantSwatch = palette.getLightVibrantSwatch();
                                    mutedSwatch = palette.getMutedSwatch();
                                    darkMutedSwatch = palette.getDarkMutedSwatch();
                                    lightMutedSwatch = palette.getLightMutedSwatch();
                                    dominantSwatch = palette.getDominantSwatch();


                                    // Toast.makeText(getBaseContext(),"Swatches Size: "+palette.getSwatches().size(),Toast.LENGTH_SHORT).show();


                                    if (vibrantSwatch != null) {
                                        currentSwatch = vibrantSwatch;
                                        holder.vibrantLay.setBackgroundTintList(ColorStateList.valueOf(vibrantSwatch.getRgb()));
                                        holder.v_txt.setText("V " + currentSwatch.getPopulation());
                                    }
                                    if (lightVibrantSwatch != null) {
                                        currentSwatch = lightVibrantSwatch;
                                        holder.lightVibrantLay.setBackgroundTintList(ColorStateList.valueOf(lightVibrantSwatch.getRgb()));
                                        holder.lv_txt.setText("LV " + currentSwatch.getPopulation());
                                    }
                                    if (mutedSwatch != null) {
                                        currentSwatch = mutedSwatch;
                                        holder.mutedLay.setBackgroundTintList(ColorStateList.valueOf(mutedSwatch.getRgb()));
                                        holder.m_txt.setText("M " + currentSwatch.getPopulation());
                                    }
                                    if (lightMutedSwatch != null) {
                                        currentSwatch = lightMutedSwatch;
                                        holder.lightMutedLay.setBackgroundTintList(ColorStateList.valueOf(lightMutedSwatch.getRgb()));
                                        holder.lm_txt.setText("LM " + currentSwatch.getPopulation());
                                    }
                                    if (dominantSwatch != null) {
                                        currentSwatch = dominantSwatch;
                                        holder.dominantLay.setBackgroundTintList(ColorStateList.valueOf(dominantSwatch.getRgb()));
                                        holder.do_txt.setText("DO " + currentSwatch.getPopulation());
                                    }
                                    if (darkVibrantSwatch != null) {
                                        currentSwatch = darkVibrantSwatch;
                                        holder.darkVibrantLay.setBackgroundTintList(ColorStateList.valueOf(darkVibrantSwatch.getRgb()));
                                        holder.dv_txt.setText("DV " + currentSwatch.getPopulation());
                                    }
                                    if (darkMutedSwatch != null) {
                                        currentSwatch = darkMutedSwatch;
                                        holder.darkMutedLay.setBackgroundTintList(ColorStateList.valueOf(darkMutedSwatch.getRgb()));
                                        holder.dm_txt.setText("DM " + currentSwatch.getPopulation());
                                    }


                                    if (currentSwatch != null) {
                                        int titleColor = currentSwatch.getRgb();

                                    /*notificationTitleColor = titleColor;
                                    notificationBackGroundColor = currentSwatch.getRgb();
                                    setColorized = true;*/


                                        //holder.fade_back.setBackgroundTintList(ColorStateList.valueOf(darkMutedSwatch.getRgb()));

                                    /*if(darkVibrantSwatch!=null)
                                        holder.layout_background.setBackgroundTintList(ColorStateList.valueOf(darkVibrantSwatch.getRgb()));
                                    else if(darkMutedSwatch!=null)
                                        holder.layout_background.setBackgroundTintList(ColorStateList.valueOf(darkMutedSwatch.getBodyTextColor()));
                                    else if(mutedSwatch!=null)
                                        holder.layout_background.setBackgroundTintList(ColorStateList.valueOf(mutedSwatch.getRgb()));
                                    else if(lightMutedSwatch!=null)
                                        holder.layout_background.setBackgroundTintList(ColorStateList.valueOf(lightMutedSwatch.getRgb()));
                                    else if(vibrantSwatch!=null)
                                        holder.layout_background.setBackgroundTintList(ColorStateList.valueOf(vibrantSwatch.getRgb()));
                                    else if(lightVibrantSwatch!=null)
                                        holder.layout_background.setBackgroundTintList(ColorStateList.valueOf(lightVibrantSwatch.getRgb()));
                                    else if(dominantSwatch!=null)
                                        holder.layout_background.setBackgroundTintList(ColorStateList.valueOf(dominantSwatch.getRgb()));*/


                                        if (dominantSwatch != null) {
                                            if (vibrantSwatch != null && dominantSwatch.getPopulation() == vibrantSwatch.getPopulation()) {
                                                if (lightMutedSwatch != null && mutedSwatch != null
                                                        && lightMutedSwatch.getPopulation() > mutedSwatch.getPopulation())
                                                    holder.layout_background.setBackgroundTintList(ColorStateList.valueOf(lightMutedSwatch.getRgb()));
                                                else if (lightMutedSwatch != null && mutedSwatch != null
                                                        && lightMutedSwatch.getPopulation() < mutedSwatch.getPopulation())
                                                    holder.layout_background.setBackgroundTintList(ColorStateList.valueOf(mutedSwatch.getRgb()));
                                                else if (lightMutedSwatch != null && mutedSwatch != null && darkMutedSwatch != null)
                                                    holder.layout_background.setBackgroundTintList(ColorStateList.valueOf(darkMutedSwatch.getRgb()));
                                                else
                                                    holder.layout_background.setBackgroundTintList(ColorStateList.valueOf(dominantSwatch.getRgb()));

                                            } else if (mutedSwatch != null && dominantSwatch.getPopulation() == mutedSwatch.getPopulation()) {
                                                if (mutedSwatch != null)
                                                    holder.layout_background.setBackgroundTintList(ColorStateList.valueOf(mutedSwatch.getRgb()));
                                            } else if (darkVibrantSwatch != null && dominantSwatch.getPopulation() == darkVibrantSwatch.getPopulation()) {
                                                if (vibrantSwatch != null && mutedSwatch != null) {
                                                    if (vibrantSwatch.getPopulation() > mutedSwatch.getPopulation() && lightVibrantSwatch != null
                                                            && vibrantSwatch.getPopulation() > 50)
                                                        holder.layout_background.setBackgroundTintList(ColorStateList.valueOf(vibrantSwatch.getRgb()));
                                                    else {
                                                        if (darkMutedSwatch != null && darkMutedSwatch.getPopulation() > mutedSwatch.getPopulation()
                                                                && mutedSwatch.getPopulation() < 50)
                                                            holder.layout_background.setBackgroundTintList(ColorStateList.valueOf(darkMutedSwatch.getRgb()));
                                                        else
                                                            holder.layout_background.setBackgroundTintList(ColorStateList.valueOf(mutedSwatch.getRgb()));
                                                    }

                                                } else {
                                                    if (mutedSwatch != null)
                                                        holder.layout_background.setBackgroundTintList(ColorStateList.valueOf(mutedSwatch.getRgb()));
                                                    else
                                                        holder.layout_background.setBackgroundTintList(ColorStateList.valueOf(dominantSwatch.getRgb()));
                                                }
                                            } else if (lightMutedSwatch != null && dominantSwatch.getPopulation() == lightMutedSwatch.getPopulation()) {
                                                if (darkMutedSwatch != null && mutedSwatch != null
                                                        && darkMutedSwatch.getPopulation() > mutedSwatch.getPopulation()
                                                        && mutedSwatch.getPopulation() < 50)
                                                    holder.layout_background.setBackgroundTintList(ColorStateList.valueOf(darkMutedSwatch.getRgb()));
                                                else if (vibrantSwatch != null)
                                                    holder.layout_background.setBackgroundTintList(ColorStateList.valueOf(vibrantSwatch.getRgb()));
                                                else if (mutedSwatch != null && mutedSwatch.getPopulation() > 50)
                                                    holder.layout_background.setBackgroundTintList(ColorStateList.valueOf(mutedSwatch.getRgb()));

                                                else
                                                    holder.layout_background.setBackgroundTintList(ColorStateList.valueOf(dominantSwatch.getRgb()));

                                            } else if (darkMutedSwatch != null && dominantSwatch.getPopulation() == darkMutedSwatch.getPopulation()) {
                                                if (vibrantSwatch != null && mutedSwatch != null) {
                                                    if (vibrantSwatch.getPopulation() > mutedSwatch.getPopulation()) {
                                                        if (darkVibrantSwatch != null && darkVibrantSwatch.getPopulation() < 50 || darkVibrantSwatch == null)
                                                            holder.layout_background.setBackgroundTintList(ColorStateList.valueOf(vibrantSwatch.getRgb()));

                                                        else
                                                            holder.layout_background.setBackgroundTintList(ColorStateList.valueOf(darkVibrantSwatch.getRgb()));
                                                    } else
                                                        holder.layout_background.setBackgroundTintList(ColorStateList.valueOf(mutedSwatch.getRgb()));

                                                } else if (mutedSwatch != null)
                                                    holder.layout_background.setBackgroundTintList(ColorStateList.valueOf(mutedSwatch.getRgb()));
                                                else if (vibrantSwatch != null)
                                                    holder.layout_background.setBackgroundTintList(ColorStateList.valueOf(vibrantSwatch.getRgb()));
                                                else
                                                    holder.layout_background.setBackgroundTintList(ColorStateList.valueOf(dominantSwatch.getRgb()));
                                            } else if (lightVibrantSwatch != null && lightVibrantSwatch.getPopulation() == dominantSwatch.getPopulation()) {
                                                if (mutedSwatch != null && mutedSwatch.getPopulation() > 50)
                                                    holder.layout_background.setBackgroundTintList(ColorStateList.valueOf(mutedSwatch.getRgb()));
                                                else if (vibrantSwatch != null && vibrantSwatch.getPopulation() > 100
                                                        && mutedSwatch != null && vibrantSwatch.getPopulation() > mutedSwatch.getPopulation()
                                                        && darkMutedSwatch != null && darkMutedSwatch.getPopulation() < 100)
                                                    holder.layout_background.setBackgroundTintList(ColorStateList.valueOf(vibrantSwatch.getRgb()));

                                                else if (darkMutedSwatch != null)
                                                    holder.layout_background.setBackgroundTintList(ColorStateList.valueOf(darkMutedSwatch.getRgb()));
                                                else
                                                    holder.layout_background.setBackgroundTintList(ColorStateList.valueOf(dominantSwatch.getRgb()));

                                            } else if (mutedSwatch != null && mutedSwatch.getPopulation() > 100)
                                                holder.layout_background.setBackgroundTintList(ColorStateList.valueOf(mutedSwatch.getRgb()));

                                            else if (darkMutedSwatch != null)
                                                holder.layout_background.setBackgroundTintList(ColorStateList.valueOf(darkMutedSwatch.getRgb()));
                                            else
                                                holder.layout_background.setBackgroundTintList(ColorStateList.valueOf(dominantSwatch.getRgb()));

                                        /*if(vibrantSwatch!=null)
                                            holder.layout_background.setBackgroundTintList(ColorStateList.valueOf(vibrantSwatch.getRgb()));
                                        else
                                            holder.layout_background.setBackgroundTintList(ColorStateList.valueOf(dominantSwatch.getRgb()));*/
                                            holder.layout_background.animate().alpha(1.0f).setDuration(2000);
                                        }


                                        // Toast.makeText(mContext,"lightVibrantSwatch "+lightVibrantSwatch,Toast.LENGTH_LONG).show();


                                        // holder.fade_back.setBackgroundTintList(ColorStateList.valueOf(titleColor));


                                        //holder.fade_back.setBackgroundTintList(ColorStateList.valueOf(darkMutedSwatch.getRgb()));
                                        /*holder.play_box.setBackgroundTintList(ColorStateList.valueOf(titleColor));
                                        holder.like_box.setBackgroundTintList(ColorStateList.valueOf(titleColor));*/


                                        // holder.like_btn.setBackgroundTintList(ColorStateList.valueOf(titleColor));

                                    }

                                /*collapsedView.setTextViewText(R.id.notification_message_collapsed,remoteMessage.getData().get("label"));
                                collapsedView.setInt(R.id.background_fade, "setBackgroundTint",
                                        notificationTitleColor);*/
                                    //  collapsedView.setT


                                }
                            });

                        } catch (Exception e) {
                            //
                        }



                    }
                });

        holder.like_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //  holder.like_btn.setBackground(mContext.getResources().getDrawable(R.drawable.heart_full));


                HashMap<String, Object> likesHashMap = new HashMap<>();
                likesHashMap.put("song", musicPost.getSpotifySong());
                likesHashMap.put("artist", musicPost.getSpotifyArtist());


                FirebaseDatabase.getInstance().getReference("Likes")
                        .child(otherUid)
                        .child(musicPost.getPostKey())
                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .updateChildren(likesHashMap);

                holder.like_btn.setBackgroundTintList(ColorStateList.valueOf(mContext.getResources().getColor(R.color.colorRed)));


            }
        });

        new Handler().post(new Runnable() {
            @Override
            public void run() {

            }
        });


        holder.play_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    spotifyAppRemote.getPlayerApi().play(musicPost.getSpotifyId()).setResultCallback(new CallResult.ResultCallback<Empty>() {
                        @Override
                        public void onResult(Empty empty) {
                            Toast.makeText(mContext, "Playing \"" + musicPost.getSpotifySong() + "\"", Toast.LENGTH_SHORT).show();
                        }
                    });

                } catch (Exception e) {
                    Toast.makeText(mContext, "Cannot play this song", Toast.LENGTH_SHORT).show();

                }
            }
        });

        holder.play_btn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if (event.getAction() == MotionEvent.ACTION_DOWN) {


                    //holder.play_btn.setAlpha(0.6f);


                    holder.play_box.animate().scaleX(0.95f).scaleY(0.95f).setDuration(0);

                } else if (event.getAction() == MotionEvent.ACTION_UP) {

                    holder.play_box.animate().scaleX(1.0f).scaleY(1.0f).setDuration(50);


                    //holder.play_btn.animate().alpha(1.0f).setDuration(50);
                    //holder.like_box.setBackgroundTintList(null);


                } else {
                    //holder.play_box.setBackgroundTintList(null);

                    holder.play_box.animate().scaleX(1.0f).scaleY(1.0f).setDuration(50);
                }

                return false;
            }

        });

        holder.like_btn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if (event.getAction() == MotionEvent.ACTION_DOWN) {


                    //holder.play_btn.setAlpha(0.6f);


                    holder.like_box.animate().scaleX(0.95f).scaleY(0.95f).setDuration(0);

                } else if (event.getAction() == MotionEvent.ACTION_UP) {

                    holder.like_box.animate().scaleX(1.0f).scaleY(1.0f).setDuration(50);


                    //holder.play_btn.animate().alpha(1.0f).setDuration(50);
                    // holder.like_box.setBackgroundTintList(null);


                } else {


                    holder.like_box.animate().scaleX(1.0f).scaleY(1.0f).setDuration(50);
                }

                return false;
            }

        });

        holder.open_spotify_lay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //spotifyAppRemote.getPlayerApi().play(user.getSpotifyId());
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("" + musicPost.getSpotifyId()));
                intent.putExtra(Intent.EXTRA_REFERRER,
                        Uri.parse("android-app://" + mContext.getPackageName()));
                mContext.startActivity(intent);


            }
        });


        holder.open_spotify_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // spotifyAppRemote.getPlayerApi().play(user.getSpotifyId());
                Intent intent = new Intent(Intent.ACTION_VIEW);
                //intent.setData(Uri.parse("spotify:track:"+user.getSpotifySong()));
                intent.setData(Uri.parse("" + musicPost.getSpotifyId()));
                //spotify:track:2jCnn1QPQ3E8ExtLe6INsx
                intent.putExtra(Intent.EXTRA_REFERRER,
                        Uri.parse("android-app://" + mContext.getPackageName()));
                mContext.startActivity(intent);


            }
        });

        holder.spotify_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // spotifyAppRemote.getPlayerApi().play(user.getSpotifyId());
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("" + musicPost.getSpotifyId()));
                intent.putExtra(Intent.EXTRA_REFERRER,
                        Uri.parse("android-app://" + mContext.getPackageName()));
                mContext.startActivity(intent);


            }
        });



    }

    @Override
    public int getItemCount() {
        return musicPostList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        CustomCircularView profile_pic;
        TextView username;
        ImageView song_art;
        TextView song_name, artist_name;
        RelativeLayout like_box, layout_background, fade_back;
        RelativeLayout play_box, foreground_lay;
        ImageButton play_btn;
        Button like_btn;
        TextView time;

        //colors
        RelativeLayout darkVibrantLay, lightVibrantLay, vibrantLay, darkMutedLay, lightMutedLay, mutedLay, dominantLay, spotify_icon, open_spotify_lay;
        TextView dm_txt, dv_txt, lm_txt, lv_txt, v_txt, m_txt, do_txt, open_spotify_text;



        public ViewHolder(@NonNull View itemView) {
            super(itemView);


            profile_pic = itemView.findViewById(R.id.profile_pic);
            username = itemView.findViewById(R.id.username);
            song_art = itemView.findViewById(R.id.song_art);
            song_name = itemView.findViewById(R.id.song_name);
            artist_name = itemView.findViewById(R.id.artist_name);
            play_box = itemView.findViewById(R.id.play_box);
            play_btn = itemView.findViewById(R.id.play_btn);
            like_box = itemView.findViewById(R.id.like_box);
            layout_background = itemView.findViewById(R.id.layout_background);
            like_btn = itemView.findViewById(R.id.like_btn);
            fade_back = itemView.findViewById(R.id.fade_back);
            darkVibrantLay = itemView.findViewById(R.id.darkVibrant);
            lightMutedLay = itemView.findViewById(R.id.lightMuted);
            lightVibrantLay = itemView.findViewById(R.id.lightVibrant);
            vibrantLay = itemView.findViewById(R.id.vibrant);
            darkMutedLay = itemView.findViewById(R.id.darkMuted);
            mutedLay = itemView.findViewById(R.id.muted);
            dominantLay = itemView.findViewById(R.id.dominant);
            dm_txt = itemView.findViewById(R.id.dm_txt);
            dv_txt = itemView.findViewById(R.id.dv_txt);
            lm_txt = itemView.findViewById(R.id.lm_txt);
            lv_txt = itemView.findViewById(R.id.lv_txt);
            v_txt = itemView.findViewById(R.id.v_txt);
            m_txt = itemView.findViewById(R.id.m_txt);
            do_txt = itemView.findViewById(R.id.do_txt);
            open_spotify_text = itemView.findViewById(R.id.open_spotify_text);
            spotify_icon = itemView.findViewById(R.id.spotify_icon);
            open_spotify_lay = itemView.findViewById(R.id.open_spotify_lay);
            foreground_lay = itemView.findViewById(R.id.foreground_lay);
            time = itemView.findViewById(R.id.time);


        }
    }


}
