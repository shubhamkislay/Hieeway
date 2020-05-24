package com.hieeway.hieeway;

import androidx.annotation.NonNull;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hieeway.hieeway.Model.ChatStamp;
import com.hieeway.hieeway.Model.VideoItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class YoutubePlayerActivity extends YouTubeBaseActivity {

    YouTubePlayerView youTubePlayerView;
    Button btnPlay;
    YouTubePlayer.OnInitializedListener onInitializedListener;
    ListView video_listView;
    Boolean resultReady = false;
    EditText search_video_edittext;
    Button search_video_btn;
    String videoID = "default";
    Boolean playerInitialised = false;
    YouTubePlayer youTubePlayer;
    String userIdChattingWith;
    ValueEventListener valueEventListener;
    DatabaseReference urlRef;
    private List<VideoItem> searchResults;
    private boolean initialiseActivity = false;
    private YouTubePlayer.PlayerStateChangeListener playerStateChangeListener;

    public static String extractYTId(String ytUrl) {
        String vId = null;
        Pattern pattern = Pattern.compile(
                "^https?://.*(?:youtu.be/|v/|u/\\w/|embed/|watch?v=)([^#&?]*).*$",
                Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(ytUrl);
        if (matcher.matches()) {
            vId = matcher.group(1);
        }
        return vId;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_youtube_player);

        searchResults = new ArrayList<>();

        Intent intent = getIntent();

        userIdChattingWith = intent.getStringExtra("userIdChattingWith");

        youTubePlayerView = findViewById(R.id.youtube_player_view);
        btnPlay = findViewById(R.id.youtube_play_btn);


        search_video_edittext = findViewById(R.id.search_video_edittext);
        search_video_btn = findViewById(R.id.search_video_btn);
        video_listView = findViewById(R.id.video_listView);

        search_video_edittext.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    searchOnYoutube(v.getText().toString());
                    return false;
                }
                return true;
            }
        });


        video_listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                VideoItem videoItem = searchResults.get(position);
                videoID = videoItem.getId();

                /*if(!playerInitialised)
                youTubePlayerView.initialize(YouTubeConfig.getApiKey(),onInitializedListener);
                else {
                    youTubePlayer.cueVideo(videoID);
                    youTubePlayer.play();
                }
*/
                HashMap<String, Object> youtubeVideoHash = new HashMap<>();
                youtubeVideoHash.put("youtubeUrl", videoID);

                FirebaseDatabase.getInstance().getReference("ChatList")
                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .child(userIdChattingWith)
                        .updateChildren(youtubeVideoHash);
                //  Toast.makeText(YoutubePlayerActivity.this,"Set Video cued",Toast.LENGTH_SHORT).show();

                FirebaseDatabase.getInstance().getReference("ChatList")
                        .child(userIdChattingWith)
                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .updateChildren(youtubeVideoHash);

            }
        });


        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    ChatStamp chatStamp = dataSnapshot.getValue(ChatStamp.class);

                    if (initialiseActivity) {
                        try {
                            //  if (!videoID.equals(chatStamp.getYoutubeUrl())) {

                            videoID = chatStamp.getYoutubeUrl();
                            if (!playerInitialised)
                                youTubePlayerView.initialize(YouTubeConfig.getApiKey(), onInitializedListener);
                            else {
                                youTubePlayer.cueVideo(videoID);
                                youTubePlayer.play();
                            }

                            //  }
                        } catch (NullPointerException e) {
                            //
                            videoID = "default";
                        }
                    }

                }
                initialiseActivity = true;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };


        onInitializedListener = new YouTubePlayer.OnInitializedListener() {
            @Override
            public void onInitializationSuccess(YouTubePlayer.Provider provider, final YouTubePlayer mYouTubePlayer, boolean b) {


                youTubePlayer = mYouTubePlayer;
                youTubePlayer.setPlayerStyle(YouTubePlayer.PlayerStyle.CHROMELESS);

                if (!videoID.equals("default")) {
                    youTubePlayer.loadVideo(videoID);
                }
                playerInitialised = true;


                youTubePlayer.setFullscreen(false);

                playerStateChangeListener = new YouTubePlayer.PlayerStateChangeListener() {
                    @Override
                    public void onLoading() {

                    }

                    @Override
                    public void onLoaded(String s) {

                    }

                    @Override
                    public void onAdStarted() {

                    }

                    @Override
                    public void onVideoStarted() {

                    }

                    @Override
                    public void onVideoEnded() {
                        youTubePlayerView.setVisibility(View.GONE);
                    }

                    @Override
                    public void onError(YouTubePlayer.ErrorReason errorReason) {

                    }
                };

                youTubePlayer.setPlayerStateChangeListener(playerStateChangeListener);

            }

            @Override
            public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {

            }
        };
        urlRef = FirebaseDatabase.getInstance().getReference("ChatList")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child(userIdChattingWith);

        urlRef.addValueEventListener(valueEventListener);

        //youTubePlayerView.initialize(YouTubeConfig.getApiKey(),onInitializedListener);


        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                youTubePlayerView.initialize(YouTubeConfig.getApiKey(), onInitializedListener);
            }
        });

    }

    private void searchOnYoutube(final String keywords) {
        CheckSearchResult();
        new Thread() {
            public void run() {
                YouTubeConfig yc = new YouTubeConfig(YoutubePlayerActivity.this);
                searchResults = yc.search(keywords);
                resultReady = true;

            }
        }.start();
    }

    public void CheckSearchResult() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (resultReady)
                    updateVideosFound();
                else
                    CheckSearchResult();
            }
        }, 1000);
    }

    private void updateVideosFound() {
        ArrayAdapter<VideoItem> adapter = new ArrayAdapter<VideoItem>(getApplicationContext(), R.layout.youtube_video_item, searchResults) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                if (convertView == null) {
                    convertView = getLayoutInflater().inflate(R.layout.youtube_video_item, parent, false);
                }
                ImageView thumbnail = (ImageView) convertView.findViewById(R.id.video_thumbnail);
                TextView title = (TextView) convertView.findViewById(R.id.video_title);
                TextView description = (TextView) convertView.findViewById(R.id.video_description);

                VideoItem searchResult = searchResults.get(position);


                Glide.with(getApplicationContext()).load(searchResult.getThumbnailURL()).into(thumbnail);
                title.setText(searchResult.getTitle());
                description.setText(searchResult.getDescription());
                return convertView;
            }
        };

        try {
            video_listView.setAdapter(adapter);
        } catch (NullPointerException e) {
            //
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        youTubePlayer.release();
        youTubePlayer = null;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (youTubePlayer != null)
            youTubePlayer.setPlayerStateChangeListener(playerStateChangeListener);

        //urlRef.addValueEventListener(valueEventListener);
    }

    @Override
    protected void onPause() {
        super.onPause();
        urlRef.removeEventListener(valueEventListener);

        //youTubePlayer.release();


    }
}
