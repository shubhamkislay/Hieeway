package com.hieeway.hieeway.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
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
                                if (vibrantSwatch != null)
                                    currentSwatch = vibrantSwatch;
                                else if (darkVibrantSwatch != null)
                                    currentSwatch = darkVibrantSwatch;
                                else if (lightVibrantSwatch != null)
                                    currentSwatch = lightVibrantSwatch;
                                else if (mutedSwatch != null)
                                    currentSwatch = mutedSwatch;
                                else if (darkMutedSwatch != null)
                                    currentSwatch = darkMutedSwatch;
                                else if (lightMutedSwatch != null)
                                    currentSwatch = lightMutedSwatch;
                                else if (dominantSwatch != null)
                                    currentSwatch = dominantSwatch;

                                if (currentSwatch != null) {
                                    int titleColor = currentSwatch.getRgb();

                                    /*notificationTitleColor = titleColor;
                                    notificationBackGroundColor = currentSwatch.getRgb();
                                    setColorized = true;*/

                                    holder.layout_background.setBackgroundTintList(ColorStateList.valueOf(titleColor));
                                    holder.play_box.setBackgroundTintList(ColorStateList.valueOf(titleColor));
                                    holder.like_box.setBackgroundTintList(ColorStateList.valueOf(titleColor));
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
        RelativeLayout like_box, layout_background;
        RelativeLayout play_box;
        ImageButton play_btn;
        Button like_btn;



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


        }
    }
}
