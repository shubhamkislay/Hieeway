package com.hieeway.hieeway;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.IBinder;
import android.support.v4.media.session.MediaSessionCompat;
import android.util.Log;
import android.widget.RemoteViews;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.hieeway.hieeway.Helper.SpotifyMusicPalRemoteHelper;
import com.hieeway.hieeway.Model.Music;
import com.hieeway.hieeway.Model.Pal;
import com.spotify.android.appremote.api.ConnectionParams;
import com.spotify.android.appremote.api.Connector;
import com.spotify.android.appremote.api.SpotifyAppRemote;
import com.spotify.protocol.client.CallResult;
import com.spotify.protocol.client.Subscription;
import com.spotify.protocol.types.Artist;
import com.spotify.protocol.types.Empty;
import com.spotify.protocol.types.MotionState;
import com.spotify.protocol.types.PlayerState;
import com.spotify.protocol.types.Track;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static br.com.instachat.emojilibrary.model.Emoji.TAG;
import static com.hieeway.hieeway.MyApplication.CHANNEL_3_ID;
import static com.hieeway.hieeway.NavButtonTest.USER_ID;
import static com.hieeway.hieeway.NavButtonTest.USER_NAME;
import static com.hieeway.hieeway.NavButtonTest.USER_PHOTO;

public class MusicPalService extends Service {

    public static boolean MUSIC_PAL_SERVICE_RUNNING = false;
    public static String USER_NAME_MUSIC_SYNC = "";
    private static final String REDIRECT_URI = "http://10.0.2.2:8888/callback";
    private static final String CLIENT_ID = "79c53faf8b67451b9adf996d40285521";
    private static String ACTION_STOP_SERVICE = "stop";
    private static String OPEN_SPOTIFY = "spotify";
    private static String OPEN_PROFILE = "profile";
    final String appPackageName = "com.spotify.music";
    SpotifyAppRemote mSpotifyAppRemote = SpotifyMusicPalRemoteHelper.getInstance().getSpotifyAppRemote();
    Intent stopSelfIntent, openSpotifyIntent, openProfileIntent;
    String username, otherUserId, userPhoto;
    String songID = "default";
    String connectionMsg = "Connecting with ";
    String connectionStatus = "default";
    Boolean musicConnected = false;
    public static ValueEventListener musicChangeListener = null;
    public static DatabaseReference databaseReference = null;
    private PendingIntent pIntentlogin, openSpotify, openProfile;


    private int notificationId;
    private Bitmap imageBitmap;
    private boolean previousConnected = false;
    private boolean initialised = false;
    private boolean syncStart = false;

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

            MUSIC_PAL_SERVICE_RUNNING = false;
            USER_NAME_MUSIC_SYNC = "";

            try {
                SpotifyAppRemote.CONNECTOR.disconnect(SpotifyMusicPalRemoteHelper.getInstance().getSpotifyAppRemote());
            } catch (Exception e) {
                //
            }

            HashMap<String, Object> palHash = new HashMap<>();


            palHash.put("connection", "disconnect");

            FirebaseDatabase.getInstance().getReference("Pal")
                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .child(otherUserId)
                    .updateChildren(palHash);

            stopSelf();


        } else if (OPEN_SPOTIFY.equals(intent.getAction())) {


            Intent openIntent = new Intent(Intent.ACTION_VIEW);
            openIntent.setData(Uri.parse("" + intent.getStringExtra("songID")));
            openIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            openIntent.putExtra(Intent.EXTRA_REFERRER,
                    Uri.parse("android-app://" + getPackageName()));
            startActivity(openIntent);
            sendBroadcast(new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS));
        } else if (OPEN_PROFILE.equals(intent.getAction())) {
            Intent openProfileIntent = new Intent(this, VerticalPageActivityPerf.class);
            openProfileIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            openProfileIntent.putExtra("username", username);
            openProfileIntent.putExtra("userid", otherUserId);
            openProfileIntent.putExtra("photo", userPhoto);
            openProfileIntent.putExtra("live", "no");

            startActivity(openProfileIntent);
            sendBroadcast(new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS));
        } else {


            if (musicChangeListener != null)
                databaseReference.removeEventListener(musicChangeListener);


            MUSIC_PAL_SERVICE_RUNNING = true;

            notificationId = MyApplication.NotificationID.getID();

            username = intent.getStringExtra("username");
            // USER_NAME = username;
            USER_NAME_MUSIC_SYNC = username;

            otherUserId = intent.getStringExtra("otherUserId");
            //  USER_ID = otherUserId;
            userPhoto = intent.getStringExtra("userPhoto");
            //USER_PHOTO = userPhoto;

            stopSelfIntent = new Intent(MusicPalService.this, MusicPalService.class);

            stopSelfIntent.setAction(ACTION_STOP_SERVICE);

            //This is optional if you have more than one buttons and want to differentiate between two


            pIntentlogin = PendingIntent.getService(MusicPalService.this, 0, stopSelfIntent, PendingIntent.FLAG_CANCEL_CURRENT);


            Notification notification = new NotificationCompat.Builder(MusicPalService.this, CHANNEL_3_ID)
                    .setContentTitle("Connecting with spotify")
                    .setSmallIcon(R.mipmap.ic_hieeway_logo)
                    .addAction(R.drawable.ic_cancel_white_24dp, "Stop", pIntentlogin)
                    // .setContentIntent(pendingIntent)
                    .setAutoCancel(true)
                    .build();


            startForeground(notificationId, notification);



            ConnectionParams connectionParams =
                    new ConnectionParams.Builder(CLIENT_ID)
                            .setRedirectUri(REDIRECT_URI)
                            .setPreferredThumbnailImageSize(1500)
                            .setPreferredImageSize(1500)
                            .showAuthView(true)
                            .build();


            if (mSpotifyAppRemote != null) {
                try {
                    SpotifyAppRemote.CONNECTOR.disconnect(mSpotifyAppRemote);
                } catch (Exception e) {

                }
            }


            SpotifyAppRemote.CONNECTOR.connect(this, connectionParams,
                    new Connector.ConnectionListener() {

                        @Override
                        public void onConnected(SpotifyAppRemote spotifyAppRemote) {
                            mSpotifyAppRemote = spotifyAppRemote;


                            SpotifyMusicPalRemoteHelper.getInstance().setSpotifyAppRemote(mSpotifyAppRemote);




                            RemoteViews expandedView = new RemoteViews(getPackageName(), R.layout.music_pal_expanded);


                            new Thread(new Runnable() {
                                @Override
                                public void run() {

                                    try {
                                        imageBitmap = Glide.with(MusicPalService.this)
                                                .asBitmap()
                                                .load(userPhoto)
                                                .submit(512, 512)
                                                .get();


                                        expandedView.setImageViewBitmap(R.id.logo, imageBitmap);

                                        expandedView.setTextViewText(R.id.notification_message_collapsed, "Syncing your music with spotify");
                                        expandedView.setTextViewText(R.id.artist_name, "Try changing the song");
                                        expandedView.setOnClickPendingIntent(R.id.logo, openSpotify);

                                        expandedView.setOnClickPendingIntent(R.id.open_spotify, openSpotify);
                                        expandedView.setOnClickPendingIntent(R.id.stop_beacon, pIntentlogin);

                                        Notification notification = new NotificationCompat.Builder(MusicPalService.this, CHANNEL_3_ID)
                                                .setSmallIcon(R.drawable.ic_stat_hieeway_arrow_title_bar)
                                                .setCustomContentView(expandedView)
                                                .setCustomBigContentView(expandedView)
                                                .setAutoCancel(true)
                                                /*.setContentTitle("Music Sync with " + username)
                                                .setSmallIcon(R.drawable.ic_stat_hieeway_arrow_title_bar)*/
                                                /*.addAction(R.drawable.ic_cancel_white_24dp, "Stop Music Beacon", pIntentlogin)
                                                .addAction(R.drawable.spotify_white_icon, "Open in Spotify", openSpotify)*/
                                                .build();

                                        startForeground(notificationId, notification);

                                    } catch (ExecutionException e) {
                                        e.printStackTrace();
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }

                                }
                            }).start();



                            /*
                            collapsedView.setTextViewText(R.id.notification_message_collapsed, "Music Sync");
                            collapsedView.setTextViewText(R.id.artist_name, connectionMsg + username);
                            collapsedView.setOnClickPendingIntent(R.id.logo, openProfile);
                            */


                            listenToChanges();
                            listedToSpotifySong();




                        }

                        @Override
                        public void onFailure(Throwable throwable) {
                            Log.e("Music Beam Service", throwable.getMessage(), throwable);
                            Notification notification = new NotificationCompat.Builder(MusicPalService.this, CHANNEL_3_ID)
                                    .setContentTitle("You are not connected to spotify app")
                                    .setContentText("Install and login to spotify app")
                                    .setSmallIcon(R.mipmap.ic_hieeway_logo)
                                    .addAction(R.drawable.ic_cancel_white_24dp, "Close", pIntentlogin)
                                    // .setContentIntent(pendingIntent)
                                    .setAutoCancel(true)
                                    .build();

                            startForeground(notificationId, notification);

                            // Something went wrong when attempting to connect! Handle errors here
                        }
                    });


        }

        return START_NOT_STICKY;
    }

    private void listedToSpotifySong() {
        //    mSpotifyAppRemote.getPlayerApi().play("spotify:playlist:37i9dQZF1DX2sUQwD7tbmL");

        Intent intent = new Intent(MusicPalService.this, MusicBeamService.class);
        intent.putExtra("forcestart", true);
        startService(intent);

        mSpotifyAppRemote.getPlayerApi()
                .subscribeToPlayerState()
                .setEventCallback(new Subscription.EventCallback<PlayerState>() {
                    @Override
                    public void onEvent(PlayerState playerState) {


                        if (playerState != null) {
                            final Track track = playerState.track;
                            String songId = track.uri;

                            if (!songID.equals(songId)) {

                                HashMap<String, Object> palHash = new HashMap<>();
                                palHash.put("songID", songId);

                                FirebaseDatabase.getInstance().getReference("Pal")
                                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                        .child(otherUserId)
                                        .updateChildren(palHash);
                            }
                            songID = songId;


                            //song_name.setAnimation(outToLeftAnimation());


                            //if (track.artist.name.length() > 1) {

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


                                                openSpotifyIntent = new Intent(MusicPalService.this, MusicPalService.class);

                                                openSpotifyIntent.setAction(OPEN_SPOTIFY);
                                                openSpotifyIntent.putExtra("songID", songId);


                                                openProfileIntent = new Intent(MusicPalService.this, MusicPalService.class);
                                                openProfileIntent.setAction(OPEN_PROFILE);



                                                //This is optional if you have more than one buttons and want to differentiate between two

                                                openProfile = PendingIntent.getService(MusicPalService.this, 0, openProfileIntent, PendingIntent.FLAG_CANCEL_CURRENT);


                                                openSpotify = PendingIntent.getService(MusicPalService.this, 0, openSpotifyIntent, PendingIntent.FLAG_CANCEL_CURRENT);



                                                RemoteViews collapsedView = new RemoteViews(getPackageName(), R.layout.music_pal_collapsed);
                                                RemoteViews expandedView = new RemoteViews(getPackageName(), R.layout.music_pal_expanded);


                                                TaskCompletionSource<Bitmap> bitmapTaskCompletionSource = new TaskCompletionSource<>();
                                                new Thread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        Bitmap imageBitmap = null;
                                                        try {
                                                            imageBitmap = Glide.with(MusicPalService.this)
                                                                    .asBitmap()
                                                                    .load(userPhoto)
                                                                    .submit(512, 512)
                                                                    .get();

                                                            bitmapTaskCompletionSource.setResult(imageBitmap);


                                                        } catch (ExecutionException e) {
                                                            e.printStackTrace();
                                                        } catch (InterruptedException e) {
                                                            e.printStackTrace();
                                                        }

                                                    }
                                                }).start();


                                                Task<Bitmap> bitmapTask = bitmapTaskCompletionSource.getTask();

                                                bitmapTask.addOnCompleteListener(new OnCompleteListener<Bitmap>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Bitmap> task) {

                                                        if (task.isSuccessful()) {


                                                            collapsedView.setImageViewBitmap(R.id.logo, task.getResult());
                                                            expandedView.setImageViewBitmap(R.id.logo, task.getResult());

                                                            collapsedView.setTextViewText(R.id.notification_message_collapsed, "Music Sync");
                                                            collapsedView.setTextViewText(R.id.artist_name, connectionMsg + username);


                                                            collapsedView.setOnClickPendingIntent(R.id.logo, openProfile);


                                                            expandedView.setTextViewText(R.id.notification_message_collapsed, "Music Sync");
                                                            expandedView.setTextViewText(R.id.artist_name, connectionMsg + username);
                                                            // expandedView.setImageViewBitmap(R.id.logo, bitmap);
                                                            expandedView.setOnClickPendingIntent(R.id.logo, openSpotify);

                                                            expandedView.setOnClickPendingIntent(R.id.open_spotify, openSpotify);
                                                            expandedView.setOnClickPendingIntent(R.id.stop_beacon, pIntentlogin);

                                                            Notification notification = new NotificationCompat.Builder(MusicPalService.this, CHANNEL_3_ID)
                                                                    .setSmallIcon(R.drawable.ic_stat_hieeway_arrow_title_bar)
                                                                    .setCustomContentView(collapsedView)
                                                                    .setCustomBigContentView(expandedView)
                                                                    .setAutoCancel(true)
                                                                    .setContentTitle("Music Sync with " + username)
                                                                    .setContentText(track.name + " by " + postArtist)
                                                                    .setSmallIcon(R.drawable.ic_stat_hieeway_arrow_title_bar)
                                                                    .addAction(R.drawable.ic_cancel_white_24dp, "Stop Music Beacon", pIntentlogin)
                                                                    .addAction(R.drawable.spotify_white_icon, "Open in Spotify", openSpotify)
                                                                    .build();

                                                            startForeground(notificationId, notification);
                                                        }

                                                    }
                                                });



                                                // palHash.put("connection", connectionStatus);


                                            }
                                        });

                        } else {
                            // Toast.makeText(getActivity(),"PlayerState is null",Toast.LENGTH_SHORT).show();
                        }

                    }


                });


    }

    private void listenToChanges() {

        initialised = true;

        databaseReference = FirebaseDatabase.getInstance().getReference("Pal")
                .child(otherUserId)
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        musicChangeListener = new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            Pal pal = dataSnapshot.getValue(Pal.class);
                            if (pal.getConnection().equals("join")) {



                                connectionMsg = "You are connected with ";

                                /*if (!songID.equals(pal.getSongID())) {
                                    if (!pal.getSongID().equals("default"))

                                }*/
                                musicConnected = true;

                                if (connectionStatus.equals("join")) {
                                    if (!songID.equals(pal.getSongID()) && !syncStart) {
                                        if (!pal.getSongID().equals("default")) {
                                            try {
                                                mSpotifyAppRemote.getPlayerApi().play(pal.getSongID());
                                            } catch (Exception e) {
                                                //
                                            }
                                            // songID = pal.getSongID();
                                            // listedToSpotifySong();
                                        }
                                    }
                                } else {
                                    mSpotifyAppRemote.getPlayerApi().getPlayerState().setResultCallback(new CallResult.ResultCallback<PlayerState>() {
                                        @Override
                                        public void onResult(PlayerState playerState) {
                                            if (playerState.isPaused)
                                                try {
                                                    songID = playerState.track.uri;
                                                    mSpotifyAppRemote.getPlayerApi().play(playerState.track.uri);
                                                } catch (Exception e) {
                                                    //
                                                }
                                            //
                                        }
                                    });
                                }

                                connectionStatus = "join";

                                syncStart = false;


                            } else if (pal.getConnection().equals("disconnect")) {
                                MUSIC_PAL_SERVICE_RUNNING = false;
                                USER_NAME_MUSIC_SYNC = "";

                                try {
                                    SpotifyAppRemote.CONNECTOR.disconnect(mSpotifyAppRemote);
                                } catch (Exception e) {
                                    //
                                }


                                FirebaseDatabase.getInstance().getReference("Pal")
                                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                        .child(otherUserId).removeValue();

                                FirebaseDatabase.getInstance().getReference("Pal")
                                        .child(otherUserId)
                                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                        .removeValue();

                                stopSelf();
                            }

                        } else {

                            syncStart = true;

                            HashMap<String, Object> palHash = new HashMap<>();
                            palHash.put("songID", songID);

                            FirebaseDatabase.getInstance().getReference("Pal")
                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .child(otherUserId)
                                    .updateChildren(palHash);

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
        };

        databaseReference.addValueEventListener(musicChangeListener);

        DatabaseReference otherRef = FirebaseDatabase.getInstance().getReference("Pal")
                .child(otherUserId);

        otherRef.onDisconnect().removeValue();

        DatabaseReference thisRef = FirebaseDatabase.getInstance().getReference("Pal")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child(otherUserId);

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("connection", "disconnect");

        thisRef.onDisconnect().updateChildren(hashMap);

        DatabaseReference connectedRef = FirebaseDatabase.getInstance().getReference(".info/connected");
        connectedRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                boolean connected = dataSnapshot.getValue(Boolean.class);


                if (previousConnected) {
                    if (!connected) {

                        MUSIC_PAL_SERVICE_RUNNING = false;
                        USER_NAME_MUSIC_SYNC = "";

                        if (musicChangeListener != null)
                            databaseReference.removeEventListener(musicChangeListener);

                        try {
                            SpotifyAppRemote.CONNECTOR.disconnect(mSpotifyAppRemote);
                        } catch (Exception e) {
                            //
                        }
                        stopSelf();
                    }
                }

                previousConnected = connected;


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }
}
