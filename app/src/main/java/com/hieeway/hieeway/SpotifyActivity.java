package com.hieeway.hieeway;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.spotify.android.appremote.api.ConnectionParams;
import com.spotify.android.appremote.api.Connector;
import com.spotify.android.appremote.api.SpotifyAppRemote;
import com.spotify.protocol.client.CallResult;
import com.spotify.protocol.client.Subscription;
import com.spotify.protocol.types.Empty;
import com.spotify.protocol.types.Image;
import com.spotify.protocol.types.PlayerState;
import com.spotify.protocol.types.Track;
import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationRequest;
import com.spotify.sdk.android.authentication.AuthenticationResponse;

public class SpotifyActivity extends AppCompatActivity {

    private static final int REQUEST_CODE = 1337;
    private static final String REDIRECT_URI = "http://10.0.2.2:8888/callback";
    private static final String CLIENT_ID = "79c53faf8b67451b9adf996d40285521";
    Button spotify_login;
    TextView track_name, artist_name;
    ImageView track_cover;
    private SpotifyAppRemote mSpotifyAppRemote;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spotify);
        track_name = findViewById(R.id.track_name);
        track_cover = findViewById(R.id.track_cover);
        artist_name = findViewById(R.id.artist_name);


        ConnectionParams connectionParams =
                new ConnectionParams.Builder(CLIENT_ID)
                        .setRedirectUri(REDIRECT_URI)
                        .setPreferredThumbnailImageSize(500)
                        .setPreferredImageSize(500)
                        .showAuthView(true)
                        .build();


        spotify_login = findViewById(R.id.spotify_login);

        spotify_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AuthenticationRequest.Builder builder =
                        new AuthenticationRequest.Builder(CLIENT_ID, AuthenticationResponse.Type.TOKEN, REDIRECT_URI);

                builder.setScopes(new String[]{"streaming"});
                AuthenticationRequest request = builder.build();

                AuthenticationClient.openLoginActivity(SpotifyActivity.this, REQUEST_CODE, request);

            }
        });


        SpotifyAppRemote.CONNECTOR.connect(this, connectionParams,
                new Connector.ConnectionListener() {

                    @Override
                    public void onConnected(SpotifyAppRemote spotifyAppRemote) {
                        mSpotifyAppRemote = spotifyAppRemote;
                        //Toast.makeText(SpotifyActivity.this,"Connected to spotify automtically :D",Toast.LENGTH_SHORT).show();

                        // Now you can start interacting with App Remote
                        listedToSpotifySong();
                    }

                    @Override
                    public void onFailure(Throwable throwable) {
                        Log.e("MainActivity", throwable.getMessage(), throwable);

                        Toast.makeText(SpotifyActivity.this, "Cannot connect to spotify automtically :(" + throwable.getMessage(), Toast.LENGTH_SHORT).show();
                        // Something went wrong when attempting to connect! Handle errors here
                    }
                });


    }

    private void listedToSpotifySong() {
        //    mSpotifyAppRemote.getPlayerApi().play("spotify:playlist:37i9dQZF1DX2sUQwD7tbmL");

        mSpotifyAppRemote.getPlayerApi()
                .subscribeToPlayerState()
                .setEventCallback(new Subscription.EventCallback<PlayerState>() {
                    @Override
                    public void onEvent(PlayerState playerState) {

                        final Track track = playerState.track;

                        if (track != null) {
                            Log.d("Spotify Activity", track.name + " by " + track.artist.name);
                            // Toast.makeText(SpotifyActivity.this,"You are playing "+track.name,Toast.LENGTH_SHORT).show();
                            track_name.setText(track.name);
                            artist_name.setText(track.artist.name);

                            mSpotifyAppRemote.getImagesApi().getImage(track.imageUri)
                                    .setResultCallback(new CallResult.ResultCallback<Bitmap>() {
                                        @Override
                                        public void onResult(Bitmap bitmap) {

                                            track_cover.setImageBitmap(bitmap);
                                        }
                                    });


                            //Glide.with(SpotifyActivity.this).load(track.imageUri).into(track_cover);
                        }

                    }
                });
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        // Check if result comes from the correct activity
        if (requestCode == REQUEST_CODE) {
            AuthenticationResponse response = AuthenticationClient.getResponse(resultCode, intent);

            switch (response.getType()) {
                // Response was successful and contains auth token
                case TOKEN:
                    // Handle successful response

                    Toast.makeText(SpotifyActivity.this, "Connected to spotify :D", Toast.LENGTH_SHORT).show();
                    break;

                // Auth flow returned an error
                case ERROR:
                    // Handle error response

                    Toast.makeText(SpotifyActivity.this, "Cannot connect" + response.getError(), Toast.LENGTH_SHORT).show();
                    break;

                // Most likely auth flow was cancelled
                default:
                    // Handle other cases
            }
        }
    }
}
