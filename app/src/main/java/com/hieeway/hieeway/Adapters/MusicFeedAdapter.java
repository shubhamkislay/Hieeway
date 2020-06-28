package com.hieeway.hieeway.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.util.Log;
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
import com.hieeway.hieeway.Model.User;
import com.hieeway.hieeway.MusicFeedActivity;
import com.hieeway.hieeway.R;
import com.hieeway.hieeway.ViewProfileActivity;
import com.spotify.android.appremote.api.ConnectionParams;
import com.spotify.android.appremote.api.Connector;
import com.spotify.android.appremote.api.SpotifyAppRemote;
import com.spotify.protocol.client.CallResult;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MusicFeedAdapter extends RecyclerView.Adapter<MusicFeedAdapter.ViewHolder> {

    private static final int REQUEST_CODE = 1337;
    private static final String REDIRECT_URI = "http://10.0.2.2:8888/callback";
    private static final String CLIENT_ID = "79c53faf8b67451b9adf996d40285521";
    public SpotifyAppRemote spotifyAppRemote;
    List<User> userList;
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

    public MusicFeedAdapter(Context mContext, List<User> userList, Activity activity, SpotifyAppRemote spotifyAppRemote) {
        this.mContext = mContext;
        this.userList = userList;
        this.spotifyAppRemote = spotifyAppRemote;
        this.activity = activity;
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


        return new MusicFeedAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MusicFeedAdapter.ViewHolder holder, int position) {
        User user = userList.get(position);


        Glide.with(mContext).load(user.getPhoto()).into(holder.profile_pic);
        holder.username.setText(user.getUsername() + " is listening to..");
        holder.song_name.setText(user.getSpotifySong());
        holder.artist_name.setText(user.getSpotifyArtist());
        holder.song_name.setSelected(true);
        holder.artist_name.setSelected(true);

        holder.username.setTypeface(Typeface.createFromAsset(mContext.getAssets(), "fonts/samsungsharpsans-bold.otf"));
        holder.song_name.setTypeface(Typeface.createFromAsset(mContext.getAssets(), "fonts/samsungsharpsans-bold.otf"));
        holder.artist_name.setTypeface(Typeface.createFromAsset(mContext.getAssets(), "fonts/samsungsharpsans-bold.otf"));

        spotifyAppRemote.getImagesApi().getImage(user.getSpotifyCover())
                .setResultCallback(new CallResult.ResultCallback<Bitmap>() {
                    @Override
                    public void onResult(Bitmap bitmap) {

                        holder.song_art.setImageBitmap(bitmap);


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
                                            if (vibrantSwatch != null || mutedSwatch != null) {
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

                                            }
                                        } else if (lightMutedSwatch != null && dominantSwatch.getPopulation() == lightMutedSwatch.getPopulation()) {
                                            if (darkMutedSwatch != null && mutedSwatch != null
                                                    && darkMutedSwatch.getPopulation() > mutedSwatch.getPopulation()
                                                    && mutedSwatch.getPopulation() < 50)
                                                holder.layout_background.setBackgroundTintList(ColorStateList.valueOf(darkMutedSwatch.getRgb()));
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

                                        } else if (mutedSwatch != null && mutedSwatch.getPopulation() > 100)
                                            holder.layout_background.setBackgroundTintList(ColorStateList.valueOf(mutedSwatch.getRgb()));

                                        else if (darkMutedSwatch != null)
                                            holder.layout_background.setBackgroundTintList(ColorStateList.valueOf(darkMutedSwatch.getRgb()));
                                        else
                                            holder.layout_background.setBackgroundTintList(ColorStateList.valueOf(dominantSwatch.getRgb()));
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



                    }
                });

        holder.like_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //  holder.like_btn.setBackground(mContext.getResources().getDrawable(R.drawable.heart_full));
                holder.like_btn.setBackgroundTintList(ColorStateList.valueOf(mContext.getResources().getColor(R.color.colorRed)));
            }
        });


        holder.play_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    spotifyAppRemote.getPlayerApi().play(user.getSpotifyId());
                    Toast.makeText(mContext, "Playing \"" + user.getSpotifySong() + "\"", Toast.LENGTH_SHORT).show();
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





    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        CircleImageView profile_pic;
        TextView username;
        ImageView song_art;
        TextView song_name, artist_name;
        RelativeLayout like_box, layout_background, fade_back;
        RelativeLayout play_box;
        ImageButton play_btn;
        Button like_btn;

        //colors
        RelativeLayout darkVibrantLay, lightVibrantLay, vibrantLay, darkMutedLay, lightMutedLay, mutedLay, dominantLay;
        TextView dm_txt, dv_txt, lm_txt, lv_txt, v_txt, m_txt, do_txt;



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


        }
    }
}
