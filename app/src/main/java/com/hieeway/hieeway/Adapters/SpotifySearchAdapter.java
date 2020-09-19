package com.hieeway.hieeway.Adapters;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.bumptech.glide.Glide;
import com.hieeway.hieeway.Interface.FiltersListFragmentListener;
import com.hieeway.hieeway.Interface.SpotifySongSelectedListener;
import com.hieeway.hieeway.Model.SpotiySearchItem;
import com.hieeway.hieeway.MusicPalService;
import com.hieeway.hieeway.R;
import com.spotify.android.appremote.api.SpotifyAppRemote;
import com.spotify.protocol.client.CallResult;
import com.spotify.protocol.types.Empty;
import com.zomato.photofilters.utils.ThumbnailItem;

import java.util.List;

import static com.hieeway.hieeway.MusicPalService.MUSIC_PAL_ID;
import static com.hieeway.hieeway.MusicPalService.MUSIC_PAL_SERVICE_RUNNING;
import static com.hieeway.hieeway.MusicPalService.USER_NAME_MUSIC_SYNC;

public class SpotifySearchAdapter extends RecyclerView.Adapter<SpotifySearchAdapter.MyViewHolder> {


    private List<SpotiySearchItem> thumbnailItems;
    private Context context;
    private int selectedIndex = 0;
    private SpotifyAppRemote spotifyAppRemote;
    private String userPhoto;
    private String useridchattingwith;
    private String username;
    private SpotifySongSelectedListener spotifySongSelectedListener;


    public SpotifySearchAdapter(List<SpotiySearchItem> thumbnailItems, Context context, SpotifyAppRemote spotifyAppRemote, String userPhoto, String useridchattingwith, String username, SpotifySongSelectedListener spotifySongSelectedListener) {
        this.thumbnailItems = thumbnailItems;
        this.context = context;
        this.spotifyAppRemote = spotifyAppRemote;
        this.userPhoto = userPhoto;
        this.useridchattingwith = useridchattingwith;
        this.username = username;
        this.spotifySongSelectedListener = spotifySongSelectedListener;

    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int position) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.spotify_search_video_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, final int position) {
        final SpotiySearchItem thumbnailItem = thumbnailItems.get(position);

        myViewHolder.song_name.setText(thumbnailItem.getSongName());
        myViewHolder.artist_name.setText(thumbnailItem.getArtistName());

        Glide.with(context).load(thumbnailItem.getImageUrl()).into(myViewHolder.song_art);

        myViewHolder.open_spotify_lay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse("" + thumbnailItem.getTrackId()));
                    intent.putExtra(Intent.EXTRA_REFERRER,
                            Uri.parse("android-app://" + context.getPackageName()));
                    context.startActivity(intent);
                } catch (Exception e) {
                    Toast.makeText(context, "Cannot play this song! Since spotify app is not installed", Toast.LENGTH_SHORT).show();
                }
            }
        });

        myViewHolder.song_art.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playSong(thumbnailItem);
            }
        });
        myViewHolder.song_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playSong(thumbnailItem);
            }
        });
        myViewHolder.artist_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playSong(thumbnailItem);
            }
        });

       /*
        myViewHolder.parent_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (MUSIC_PAL_SERVICE_RUNNING)
                    try {
                        spotifyAppRemote.getPlayerApi().play(thumbnailItem.getTrackId()).setResultCallback(new CallResult.ResultCallback<Empty>() {
                            @Override
                            public void onResult(Empty empty) {
                                Toast.makeText(context, "Playing \"" + thumbnailItem.getSongName() + "\"", Toast.LENGTH_SHORT).show();
                            }
                        });

                    } catch (Exception e) {
                        Toast.makeText(context, "Cannot play this song", Toast.LENGTH_SHORT).show();

                    }

                else {

                    Intent musicPalService = new Intent(context, MusicPalService.class);

                    musicPalService.putExtra("otherUserId", useridchattingwith);
                    musicPalService.putExtra("username", username);
                    musicPalService.putExtra("userPhoto", userPhoto);

                    context.startService(musicPalService);

                    Toast.makeText(context, "Activating music sharing...", Toast.LENGTH_LONG).show();

                    try {
                        spotifyAppRemote.getPlayerApi().play(thumbnailItem.getTrackId()).setResultCallback(new CallResult.ResultCallback<Empty>() {
                            @Override
                            public void onResult(Empty empty) {
                                Toast.makeText(context, "Playing \"" + thumbnailItem.getSongName() + "\"", Toast.LENGTH_SHORT).show();
                            }
                        });

                    } catch (Exception e) {
                        Toast.makeText(context, "Cannot play this song", Toast.LENGTH_SHORT).show();

                    }
                }
            }
        });
*/

    }

    @Override
    public int getItemCount() {
        return thumbnailItems.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView song_art;
        TextView song_name, artist_name;
        RelativeLayout open_spotify_lay, parent_layout;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            song_art = (ImageView) itemView.findViewById(R.id.song_art);
            song_name = itemView.findViewById(R.id.song_name);
            artist_name = itemView.findViewById(R.id.artist_name);
            open_spotify_lay = itemView.findViewById(R.id.open_spotify_lay);
            parent_layout = itemView.findViewById(R.id.parent_layout);

        }
    }


    private void playSong(SpotiySearchItem thumbnailItem) {
        if (MUSIC_PAL_SERVICE_RUNNING) {


            try {
                if (USER_NAME_MUSIC_SYNC.equals(username))
                    try {
                        spotifyAppRemote.getPlayerApi().play(thumbnailItem.getTrackId()).setResultCallback(new CallResult.ResultCallback<Empty>() {
                            @Override
                            public void onResult(Empty empty) {
                                spotifySongSelectedListener.onSongSelected();
                                Toast.makeText(context, "Playing \"" + thumbnailItem.getSongName() + "\"", Toast.LENGTH_SHORT).show();
                            }
                        });

                    } catch (Exception e) {
                        Toast.makeText(context, "Cannot play this song", Toast.LENGTH_SHORT).show();

                    }
                else {
                    Toast.makeText(context, "Cannot play, since you are connected to " + USER_NAME_MUSIC_SYNC, Toast.LENGTH_LONG).show();
                }
            } catch (Exception e) {
                Toast.makeText(context, "Cannot play, since you are connected to " + USER_NAME_MUSIC_SYNC, Toast.LENGTH_LONG).show();
            }
        } else {

            Intent musicPalService = new Intent(context, MusicPalService.class);

            musicPalService.putExtra("otherUserId", useridchattingwith);
            musicPalService.putExtra("username", username);
            musicPalService.putExtra("userPhoto", userPhoto);

            context.startService(musicPalService);

            Toast.makeText(context, "Activating music sharing...", Toast.LENGTH_LONG).show();

            try {
                spotifyAppRemote.getPlayerApi().play(thumbnailItem.getTrackId()).setResultCallback(new CallResult.ResultCallback<Empty>() {
                    @Override
                    public void onResult(Empty empty) {
                        spotifySongSelectedListener.onSongSelected();
                        Toast.makeText(context, "Playing \"" + thumbnailItem.getSongName() + "\"", Toast.LENGTH_SHORT).show();
                    }
                });

            } catch (Exception e) {
                Toast.makeText(context, "Cannot play this song. Check if you have spotify app installed and logged in, into your phone", Toast.LENGTH_SHORT).show();

            }
        }
    }
}
