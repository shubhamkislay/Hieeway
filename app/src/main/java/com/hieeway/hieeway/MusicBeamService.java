package com.hieeway.hieeway;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.media.session.MediaSessionCompat;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.hieeway.hieeway.Helper.SpotifyRemoteHelper;
import com.hieeway.hieeway.Model.ChatStamp;
import com.hieeway.hieeway.Model.Music;
import com.spotify.android.appremote.api.ConnectionParams;
import com.spotify.android.appremote.api.Connector;
import com.spotify.android.appremote.api.SpotifyAppRemote;
import com.spotify.protocol.client.CallResult;
import com.spotify.protocol.client.Subscription;
import com.spotify.protocol.error.SpotifyAppRemoteException;
import com.spotify.protocol.types.Artist;
import com.spotify.protocol.types.PlayerState;
import com.spotify.protocol.types.Track;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;

import static br.com.instachat.emojilibrary.model.Emoji.TAG;
import static com.hieeway.hieeway.MyApplication.CHANNEL_3_ID;
import static com.hieeway.hieeway.NavButtonTest.USER_ID;
import static com.hieeway.hieeway.NavButtonTest.USER_NAME;
import static com.hieeway.hieeway.NavButtonTest.USER_PHOTO;

public class MusicBeamService extends Service {

    private static final int REQUEST_CODE = 1337;
    private static final String REDIRECT_URI = "http://10.0.2.2:8888/callback";
    private static final String CLIENT_ID = "79c53faf8b67451b9adf996d40285521";
    private static String ACTION_STOP_SERVICE = "stop";
    private static String OPEN_SPOTIFY = "spotify";
    final String appPackageName = "com.spotify.music";
    public static final String MUSIC_BEACON = "musicbeacon";
    public static final String SPOTIFY_CONNECT = "spotifyconnect";
    public static final String SHARED_PREFS = "sharedPrefs";
    public static final String USER_ID = "userid";
    public static final String USERNAME = "username";
    Intent stopSelfIntent, openSpotifyIntent;
    private SpotifyAppRemote mSpotifyAppRemote = SpotifyRemoteHelper.getInstance().getSpotifyAppRemote();
    private PendingIntent pIntentlogin, openSpotify;
    private MediaSessionCompat mediaSessionCompat;
    private int notificationId;
    SharedPreferences sharedPreferences;
    String userId;
    String username;


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (ACTION_STOP_SERVICE.equals(intent.getAction())) {
            Log.d(TAG, "called to cancel service");
            //manager.cancel(NOTIFCATION_ID);

            try {
                SpotifyAppRemote.CONNECTOR.disconnect(mSpotifyAppRemote);

            } catch (Exception e) {

                //
            }

            SpotifyRemoteHelper.getInstance().setSpotifyAppRemote(null);
            stopSelf();


        } else if (OPEN_SPOTIFY.equals(intent.getAction())) {


            Intent openIntent = new Intent(Intent.ACTION_VIEW);
            openIntent.setData(Uri.parse("" + intent.getStringExtra("songID")));
            openIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            openIntent.putExtra(Intent.EXTRA_REFERRER,
                    Uri.parse("android-app://" + getPackageName()));
            startActivity(openIntent);
            sendBroadcast(new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS));
        } else {


            boolean forceStart = intent.getBooleanExtra("forcestart", false);

            notificationId = MyApplication.NotificationID.getID();

            mediaSessionCompat = new MediaSessionCompat(this, "tag");


            ConnectionParams connectionParams =
                    new ConnectionParams.Builder(CLIENT_ID)
                            .setRedirectUri(REDIRECT_URI)
                            .setPreferredThumbnailImageSize(1500)
                            .setPreferredImageSize(1500)
                            .showAuthView(true)
                            .build();


            if (mSpotifyAppRemote == null || forceStart) {

                try {
                    SpotifyAppRemote.CONNECTOR.disconnect(mSpotifyAppRemote);
                } catch (Exception e) {

                }

                SpotifyAppRemote.CONNECTOR.connect(this, connectionParams,
                        new Connector.ConnectionListener() {

                            @Override
                            public void onConnected(SpotifyAppRemote spotifyAppRemote) {
                                mSpotifyAppRemote = spotifyAppRemote;

                                SpotifyRemoteHelper.getInstance().setSpotifyAppRemote(mSpotifyAppRemote);

                                stopSelfIntent = new Intent(MusicBeamService.this, MusicBeamService.class);

                                stopSelfIntent.setAction(ACTION_STOP_SERVICE);

                                //This is optional if you have more than one buttons and want to differentiate between two


                                pIntentlogin = PendingIntent.getService(MusicBeamService.this, 0, stopSelfIntent, PendingIntent.FLAG_CANCEL_CURRENT);


                                Notification notification = new NotificationCompat.Builder(MusicBeamService.this, CHANNEL_3_ID)
                                        .setContentTitle("Listening to music from spotify app")
                                        .setSmallIcon(R.mipmap.ic_hieeway_logo)
                                        .addAction(R.drawable.ic_cancel_white_24dp, "Stop Music Beacon", pIntentlogin)
                                        // .setContentIntent(pendingIntent)
                                        .setAutoCancel(true)
                                        .build();


                                sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);

                                userId = sharedPreferences.getString(USER_ID, "");
                                username = sharedPreferences.getString(USERNAME, "");

                                checkSettings();

                                startForeground(notificationId, notification);


                                listedToSpotifySong();


                            }

                            @Override
                            public void onFailure(Throwable throwable) {
                                Log.e("Music Beam Service", throwable.getMessage(), throwable);
                                stopSelf();
                                // Something went wrong when attempting to connect! Handle errors here
                            }
                        });

            } else {
                stopSelfIntent = new Intent(MusicBeamService.this, MusicBeamService.class);

                stopSelfIntent.setAction(ACTION_STOP_SERVICE);

                //This is optional if you have more than one buttons and want to differentiate between two


                pIntentlogin = PendingIntent.getService(MusicBeamService.this, 0, stopSelfIntent, PendingIntent.FLAG_CANCEL_CURRENT);


                Notification notification = new NotificationCompat.Builder(MusicBeamService.this, CHANNEL_3_ID)
                        .setContentTitle("Listening to music from spotify app")
                        .setSmallIcon(R.mipmap.ic_hieeway_logo)
                        .addAction(R.drawable.ic_cancel_white_24dp, "Stop Music Beacon", pIntentlogin)
                        // .setContentIntent(pendingIntent)
                        .setAutoCancel(true)
                        .build();


                startForeground(notificationId, notification);


                listedToSpotifySong();

                sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);

                checkSettings();
            }


        }

        return START_NOT_STICKY;
    }

    private void checkSettings() {


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!(sharedPreferences.getBoolean(MUSIC_BEACON, false) &&
                        sharedPreferences.getBoolean(SPOTIFY_CONNECT, false))) {

                    try {
                        SpotifyAppRemote.CONNECTOR.disconnect(mSpotifyAppRemote);
                    } catch (Exception e) {

                    }
                    SpotifyRemoteHelper.getInstance().setSpotifyAppRemote(null);
                    stopSelf();
                }

                else
                    checkSettings();

            }
        }, 1000);

    }

    private void listedToSpotifySong() {
        //    mSpotifyAppRemote.getPlayerApi().play("spotify:playlist:37i9dQZF1DX2sUQwD7tbmL");


        mSpotifyAppRemote.getPlayerApi()
                .subscribeToPlayerState()
                .setEventCallback(new Subscription.EventCallback<PlayerState>() {
                    @Override
                    public void onEvent(PlayerState playerState) {


                        if (playerState != null) {
                            final Track track = playerState.track;
                            String songId = track.uri;

                            //song_name.setAnimation(outToLeftAnimation());


                            if (track.artist.name.length() > 1) {

                                String artistNames = null;

                                List<Artist> artists = track.artists;
                                for (Artist artist : artists) {
                                    if (artistNames != null)
                                        artistNames = artistNames + artist.name + ", ";
                                    else
                                        artistNames = artist.name + ", ";
                                }
                                artistNames = artistNames.substring(0, artistNames.length() - 2);
                                final String postArtist = artistNames;


                                mSpotifyAppRemote.getImagesApi().getImage(track.imageUri)
                                        .setResultCallback(new CallResult.ResultCallback<Bitmap>() {
                                            @Override
                                            public void onResult(Bitmap bitmap) {


                                                openSpotifyIntent = new Intent(MusicBeamService.this, MusicBeamService.class);

                                                openSpotifyIntent.setAction(OPEN_SPOTIFY);
                                                openSpotifyIntent.putExtra("songID", songId);


                                                //This is optional if you have more than one buttons and want to differentiate between two


                                                openSpotify = PendingIntent.getService(MusicBeamService.this, 0, openSpotifyIntent, PendingIntent.FLAG_CANCEL_CURRENT);


                                                RemoteViews collapsedView = new RemoteViews(getPackageName(), R.layout.collapsed_message_notification);
                                                RemoteViews expandedView = new RemoteViews(getPackageName(), R.layout.expanded_message_notification);

                                                collapsedView.setTextViewText(R.id.notification_message_collapsed, "" + track.name);
                                                collapsedView.setTextViewText(R.id.artist_name, "" + postArtist);
                                                collapsedView.setImageViewBitmap(R.id.logo, bitmap);
                                                collapsedView.setOnClickPendingIntent(R.id.logo, openSpotify);


                                                expandedView.setTextViewText(R.id.notification_message_collapsed, "" + track.name);
                                                expandedView.setTextViewText(R.id.artist_name, "" + postArtist);
                                                expandedView.setImageViewBitmap(R.id.logo, bitmap);
                                                expandedView.setOnClickPendingIntent(R.id.logo, openSpotify);

                                                expandedView.setOnClickPendingIntent(R.id.open_spotify, openSpotify);
                                                expandedView.setOnClickPendingIntent(R.id.stop_beacon, pIntentlogin);

                                                Notification notification = new NotificationCompat.Builder(MusicBeamService.this, CHANNEL_3_ID)
                                                        .setSmallIcon(R.drawable.ic_stat_hieeway_arrow_title_bar)
                                                        .setCustomContentView(collapsedView)
                                                        .setCustomBigContentView(expandedView)
                                                        .setAutoCancel(true)
                                                        .build();
                                                startForeground(notificationId, notification);



                                                /*.setContentTitle(""+track.name)
                                                        .setContentText(""+postArtist)
                                                        .setSmallIcon(R.drawable.ic_stat_hieeway_arrow_title_bar)
                                                        .addAction(R.drawable.ic_cancel_white_24dp, "Stop Music Beacon", pIntentlogin)
                                                        .addAction(R.drawable.spotify_white_icon,"Open in Spotify",openSpotify)*/
                                                //.setColor(getResources().getColor(R.color.colorPrimaryDark))
                                                //.setColorized(true)
                                                        /*.setLargeIcon(bitmap)
                                                        //.addAction(R.drawable.ic_action_chat_bubble,"Open",null)
                                                        .setAutoCancel(true)
                                                        .setStyle(new androidx.media.app.NotificationCompat.MediaStyle()
                                                        .setMediaSession(mediaSessionCompat.getSessionToken())
                                                        .setShowActionsInCompactView(0,1))*/
                                                // .setContentIntent(pendingIntent)
                                                //.setProgress(0, 0, true)
                                            }
                                        });


                                final Timestamp timestamp = new Timestamp(System.currentTimeMillis());

                                FirebaseDatabase.getInstance().getReference("Music")
                                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                        .runTransaction(new Transaction.Handler() {
                                            @NonNull
                                            @Override
                                            public Transaction.Result doTransaction(@NonNull MutableData mutableData) {

                                                String musicKey = FirebaseDatabase.getInstance().getReference("Music")
                                                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).push().getKey();

                                                Music music = mutableData.getValue(Music.class);

                                                try {
                                                    if (music.getSpotifyId() == null || !music.getSpotifyId().equals(songId)) {

                                                        HashMap<String, Object> songHash = new HashMap<>();
                                                        songHash.put("spotifyId", songId);
                                                        songHash.put("spotifySong", track.name);
                                                        songHash.put("spotifyArtist", postArtist);
                                                        songHash.put("spotifyCover", track.imageUri);
                                                        songHash.put("username", userId);
                                                        songHash.put("userId", username);
                                                        songHash.put("userPhoto", USER_PHOTO);
                                                        songHash.put("musicKey", musicKey);
                                                        songHash.put("timestamp", timestamp.toString());

                                                        //song_name.setTextColor(getActivity().getResources().getColor(R.color.colorPrimaryDark));

                                                        FirebaseDatabase.getInstance().getReference("Music")
                                                                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                                                .removeValue()
                                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                        FirebaseDatabase.getInstance().getReference("Likes")
                                                                                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                                                                .removeValue();

                                                                        FirebaseDatabase.getInstance()
                                                                                .getReference("Music")
                                                                                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                                                                .updateChildren(songHash);

                                                                    }
                                                                });

                                                        HashMap<String, Object> musicPostHash = new HashMap<>();
                                                        musicPostHash.put("spotifyId", songId);
                                                        musicPostHash.put("spotifySong", track.name);
                                                        musicPostHash.put("spotifyArtist", postArtist);
                                                        musicPostHash.put("spotifyCover", track.imageUri);
                                                        musicPostHash.put("timestamp", timestamp.toString());

                                                        FirebaseDatabase.getInstance().getReference("MusicPost")
                                                                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                                                .child(musicKey)
                                                                .updateChildren(musicPostHash);


                                                        DatabaseReference postRef = FirebaseDatabase.getInstance().getReference("Post")
                                                                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());

                                                        String postKey = FirebaseDatabase.getInstance().getReference("Post")
                                                                .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).push().getKey();


                                                        HashMap<String, Object> postHash = new HashMap<>();
                                                        postHash.put("userId", userId);
                                                        postHash.put("postKey", postKey);
                                                        postHash.put("type", "music");
                                                        postHash.put("mediaUrl", "default");
                                                        postHash.put("mediaKey", musicKey);
                                                        postHash.put("username", username);

                                                        postRef.child(postKey).updateChildren(postHash);


                                                    }


                                                } catch (NullPointerException e) {

                                                    HashMap<String, Object> songHash = new HashMap<>();
                                                    songHash.put("spotifyId", songId);
                                                    songHash.put("spotifySong", track.name);
                                                    songHash.put("spotifyArtist", postArtist);
                                                    songHash.put("spotifyCover", track.imageUri);
                                                    songHash.put("username", username);
                                                    songHash.put("userId", userId);
                                                    songHash.put("userPhoto", USER_PHOTO);
                                                    songHash.put("musicKey", musicKey);
                                                    songHash.put("timestamp", timestamp.toString());

                                                    //song_name.setTextColor(getActivity().getResources().getColor(R.color.colorPrimaryDark));

                                                    FirebaseDatabase.getInstance().getReference("Music")
                                                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                                            .removeValue()
                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                    FirebaseDatabase.getInstance()
                                                                            .getReference("Music")
                                                                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                                                            .updateChildren(songHash);

                                                                }
                                                            });


                                                    HashMap<String, Object> musicPostHash = new HashMap<>();
                                                    musicPostHash.put("spotifyId", songId);
                                                    musicPostHash.put("spotifySong", track.name);
                                                    musicPostHash.put("spotifyArtist", postArtist);
                                                    musicPostHash.put("spotifyCover", track.imageUri);
                                                    musicPostHash.put("timestamp", timestamp.toString());

                                                    FirebaseDatabase.getInstance().getReference("MusicPost")
                                                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                                            .child(musicKey)
                                                            .updateChildren(musicPostHash);

                                                    DatabaseReference postRef = FirebaseDatabase.getInstance().getReference("Post")
                                                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid());

                                                    String postKey = FirebaseDatabase.getInstance().getReference("Post")
                                                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).push().getKey();

                                                    HashMap<String, Object> postHash = new HashMap<>();
                                                    postHash.put("userId", userId);
                                                    postHash.put("postKey", postKey);
                                                    postHash.put("type", "music");
                                                    postHash.put("mediaUrl", "default");
                                                    postHash.put("mediaKey", musicKey);
                                                    postHash.put("username", username);


                                                    postRef.child(postKey).updateChildren(postHash);

                                                }

                                                return null;
                                            }

                                            @Override
                                            public void onComplete(@Nullable DatabaseError databaseError, boolean b, @Nullable DataSnapshot dataSnapshot) {

                                            }
                                        });


                                /*HashMap<String, Object> songHash = new HashMap<>();
                                songHash.put("spotifyId", songId);
                                songHash.put("spotifySong", track.name);
                                songHash.put("spotifyArtist", artistNames);
                                songHash.put("spotifyCover", track.imageUri);
                                songHash.put("username", USER_NAME);
                                songHash.put("userId", USER_ID);
                                songHash.put("userPhoto", USER_PHOTO);
                                songHash.put("timestamp", timestamp.toString());

                                //song_name.setTextColor(getActivity().getResources().getColor(R.color.colorPrimaryDark));

                                FirebaseDatabase.getInstance()
                                        .getReference("Music")
                                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                        .updateChildren(songHash);*/

                                Log.d("Spotify Activity", track.name + " by " + track.artist.name);
                                // Toast.makeText(SpotifyActivity.this,"You are playing "+track.name,Toast.LENGTH_SHORT).show();
                                //  Toast.makeText(getActivity(),"Track is "+track.name,Toast.LENGTH_SHORT).show();


                                //Glide.with(SpotifyActivity.this).load(track.imageUri).into(track_cover);
                            } else {
                                // Toast.makeText(getActivity(),"Track is null",Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            // Toast.makeText(getActivity(),"PlayerState is null",Toast.LENGTH_SHORT).show();
                        }

                    }


                });


    }

}
