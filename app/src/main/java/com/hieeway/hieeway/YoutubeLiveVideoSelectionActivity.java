package com.hieeway.hieeway;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.Typeface;
import android.net.http.SslError;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.webkit.SslErrorHandler;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.hieeway.hieeway.Adapters.VideoPalAdapter;
import com.hieeway.hieeway.Model.Friend;
import com.hieeway.hieeway.Model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class YoutubeLiveVideoSelectionActivity extends AppCompatActivity {

    public static final String SHARED_PREFS = "sharedPrefs";
    public static final String USER_ID = "userid";
    public String searchingUsername;
    TextView username, result_text;
    RecyclerView friend_list_recyclerview;
    Activity activity;
    VideoPalAdapter videoPalAdapter;
    EditText searchPeople;
    //ImageView //progress_menu_logo;
    ProgressBar progressBar, progressBarTwo; //progress_menu_logo_two;
    TextView logo_title;
    private List<Friend> userList;
    private ValueEventListener valueEventListener;
    private Query query;
    private List<User> userlist;
    private boolean friendAvailable = false;
    private String userID;
    private ImageView video_thumbnail;
    private TextView youtube_url;
    private RelativeLayout youtube_video_view;
    private String youtube_Url;
    WebView youtube_web_view;
    private static String yoututbeHomeURL = "https://m.youtube.com/";
    private boolean loaded = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);

        userID = sharedPreferences.getString(USER_ID, "");
        //try {
            if (userID.length() < 1) {
                Toast.makeText(YoutubeLiveVideoSelectionActivity.this, "You are not logged in: " + userID, Toast.LENGTH_SHORT).show();
                startActivity(new Intent(YoutubeLiveVideoSelectionActivity.this, MainActivity.class));
                finish();
            } else {
                setContentView(R.layout.activity_youtube_live_video_selection);

                userStatusOnDiconnect();
                username = findViewById(R.id.username);
                friend_list_recyclerview = findViewById(R.id.friend_list_recyclerview);
                userlist = new ArrayList<>();

                activity = this;

                userList = new ArrayList<>();

                progressBar = findViewById(R.id.progress);
                progressBarTwo = findViewById(R.id.progressTwo);

                searchPeople = findViewById(R.id.search_people);

                searchPeople.setImeOptions(EditorInfo.IME_ACTION_DONE);

                searchPeople.setRawInputType(InputType.TYPE_CLASS_TEXT);
                logo_title = findViewById(R.id.layout_title);
                video_thumbnail = findViewById(R.id.video_thumbnail);
                youtube_url = findViewById(R.id.youtube_url);

                youtube_web_view = findViewById(R.id.youtube_web_view);

                youtube_web_view.getSettings().setJavaScriptEnabled(true);

                WebSettings webSettings = youtube_web_view.getSettings();
                webSettings.setPluginState(WebSettings.PluginState.ON);
                webSettings.setJavaScriptEnabled(true);
                webSettings.setUseWideViewPort(true);
                webSettings.setLoadWithOverviewMode(true);


                youtube_video_view = findViewById(R.id.youtube_video_view);


                videoPalAdapter = new VideoPalAdapter(YoutubeLiveVideoSelectionActivity.this, userList, activity, youtube_Url);
                friend_list_recyclerview.setAdapter(videoPalAdapter);


                Bundle extras = getIntent().getExtras();
                try {
                    youtube_Url = extras.getString(Intent.EXTRA_TEXT);
                } catch (Exception e) {
                    youtube_Url = null;
                }

                if (youtube_Url != null) {
                    youtube_url.setText(youtube_Url);

                    getVideoIdfromUrl(youtube_Url);
                } else {
                    youtube_web_view.setVisibility(View.VISIBLE);
                }


                final WebViewClient webViewClient = new WebViewClient() {


                    @Override
                    public void doUpdateVisitedHistory(WebView view, String url, boolean isReload) {
                        // Toast.makeText(parentActivity, EMPTY_STRING + url, Toast.LENGTH_SHORT).show();

                        /*if(yoututbeHomeURL.equals(url))
                            youtube_web_view.stopLoading();

                        yoututbeHomeURL  = url;



                        Toast.makeText(YoutubeLiveVideoSelectionActivity.this,"URL: "+url,Toast.LENGTH_SHORT).show();*/

                        youtube_Url = url;
                        videoPalAdapter.setYoutube_Url(youtube_Url);

                        if (!url.equals(yoututbeHomeURL))

                            getVideoIdfromUrl(url);


                    }

                    @Override
                    public void onPageFinished(WebView view, String url) {
                        super.onPageFinished(view, url);


                    }

           /* @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);
            }*/

                    @Override
                    public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                        handler.proceed();
                    }
                };

                //youtube_web_view.setWebChromeClient(new WebChromeClient());
                youtube_web_view.setWebViewClient(webViewClient);
                youtube_web_view.loadUrl(yoututbeHomeURL);


                Window window = this.getWindow();

// clear FLAG_TRANSLUCENT_STATUS flag:
                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

// add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

// finally change the color
                window.setStatusBarColor(ContextCompat.getColor(this, R.color.nav_status_color));

                result_text = findViewById(R.id.search_result_txt);
                result_text.setTypeface(Typeface.createFromAsset(this.getAssets(), "fonts/samsungsharpsans-bold.otf"));

                //progress_menu_logo = findViewById(R.id.//progress_menu_logo);


                logo_title.setTypeface(Typeface.createFromAsset(this.getAssets(), "fonts/samsungsharpsans-bold.otf"));

                Display display = getWindowManager().getDefaultDisplay();
                Point size = new Point();
                display.getSize(size);
                float displayWidth = size.x;


                int spanCount; // 3 columns
                int spacing = 0; // 50px
                boolean includeEdge = true;

                if (displayWidth >= 1920)
                    spanCount = 4;

                else if (displayWidth >= 1080)
                    spanCount = 3;

                else if (displayWidth >= 500)
                    spanCount = 2;
                else
                    spanCount = 1;


                LinearLayoutManager gridLayoutManager = new GridLayoutManager(this, spanCount);


                gridLayoutManager.setOrientation(RecyclerView.VERTICAL);

                //gridLayoutManager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_NONE);
                friend_list_recyclerview.setLayoutManager(gridLayoutManager);
                friend_list_recyclerview.setHasFixedSize(true);
                friend_list_recyclerview.addItemDecoration(new GridSpacingItemDecoration(spanCount, spacing, includeEdge));

                friend_list_recyclerview.setItemViewCacheSize(20);
                friend_list_recyclerview.setDrawingCacheEnabled(true);
                friend_list_recyclerview.setItemAnimator(new DefaultItemAnimator());
                friend_list_recyclerview.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);

                searchPeople.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {


                        //userList.clear();
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                        if (s.length() > 0)
                            searchFriends(s.toString().toLowerCase());

                        else {
                    /*userList.clear();
                    videoPalAdapter.notifyDataSetChanged();
                    result_text.setVisibility(View.VISIBLE);
                    result_text.setText("Add friends from search tab");*/

                            populateWithFriends();
                        }


                    }

                    @Override
                    public void afterTextChanged(Editable s) {


                    }
                });


            }
        //}
        /*catch (Exception e) {
            Toast.makeText(YoutubeLiveVideoSelectionActivity.this, "You are not logged in: "+e.toString(), Toast.LENGTH_SHORT).show();
            startActivity(new Intent(YoutubeLiveVideoSelectionActivity.this, MainActivity.class));
            finish();
        }
*/


        populateWithFriends();
    }

    private void userStatusOnDiconnect() {

        HashMap<String, Object> setOfflineHash = new HashMap<>();

        setOfflineHash.put("online", false);

        FirebaseDatabase.getInstance().getReference("Users")
                .child(userID)
                .onDisconnect()
                .updateChildren(setOfflineHash);
    }

    private void searchFriends(final String username) {


        if (username.length() > 0) {
            query = FirebaseDatabase.getInstance().getReference("FriendList")
                    .child(userID).orderByChild("username")
                    .startAt(username)
                    .endAt(username + "\uf8ff");
        } else {
            query = FirebaseDatabase.getInstance().getReference("FriendList")
                    .child(userID);
        }

        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                // userList.clear();


                if (dataSnapshot.exists()) {
                    userList.clear();
                    result_text.setVisibility(View.GONE);

                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Friend friend = snapshot.getValue(Friend.class);
                        try {
                            if (friend.getStatus().equals("friends")) {


                                userList.add(friend);

                                // videoPalAdapter.notifyDataSetChanged();
                            }
                        } catch (Exception e) {
                            //
                            result_text.setVisibility(View.VISIBLE);
                            result_text.setText("Can't fetch friends");
                            // Toast.makeText(getContext(),"Can't fetch friends",Toast.LENGTH_SHORT).show();

                        }

                    }

                    videoPalAdapter.notifyDataSetChanged();

                    /*peopleAdapter.setList(userlist);*/
                    // if(username.length()>0)
                    //Collections.sort(chatStampsList, Collections.<ChatStamp>reverseOrder());

                    //  videoPalAdapter.notifyDataSetChanged();


                } else {
                    userList.clear();
                    videoPalAdapter.notifyDataSetChanged();
                    result_text.setVisibility(View.VISIBLE);
                    result_text.setText("No results found for '" + username + "'");
                    // Toast.makeText(getContext(),"Couldn't find user: "+username,Toast.LENGTH_SHORT).show();
                }

                // videoPalAdapter.notifyDataSetChanged();


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };


        query.addValueEventListener(valueEventListener);


    }

    private void populateWithFriends() {


        result_text.setVisibility(View.GONE);

        userList.clear();
        DatabaseReference friendsRef = FirebaseDatabase.getInstance().getReference("FriendList")
                .child(userID);

        friendsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                //userList.clear();
                if (dataSnapshot.exists()) {
                    result_text.setVisibility(View.GONE);
                    userList.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Friend friend = snapshot.getValue(Friend.class);
                        try {
                            if (friend.getStatus().equals("friends")) {

                                userList.add(friend);
                                progressBar.setVisibility(View.INVISIBLE);
                                //progress_menu_logo.setVisibility(View.INVISIBLE);
                                //progress_menu_logo_two.setVisibility(View.INVISIBLE);
                                progressBarTwo.setVisibility(View.INVISIBLE);

                                friendAvailable = true;

                            }

                        } catch (Exception e) {
                            //
                            //  Toast.makeText(getContext(),"Can't fetch friends",Toast.LENGTH_SHORT).show();

                            result_text.setVisibility(View.VISIBLE);
                            result_text.setText("Can't fetch friends\nSearch and add friends from the search tab");

                            progressBar.setVisibility(View.INVISIBLE);
                            progressBarTwo.setVisibility(View.INVISIBLE);
                            //progress_menu_logo.setVisibility(View.INVISIBLE);
                            //progress_menu_logo_two.setVisibility(View.INVISIBLE);
                        }

                    }
                    try {
                        videoPalAdapter.notifyDataSetChanged();
                    } catch (Exception e) {

                    }
                    if (!friendAvailable) {

                        result_text.setVisibility(View.VISIBLE);
                        result_text.setText("No friends");
                        progressBar.setVisibility(View.INVISIBLE);
                        //progress_menu_logo.setVisibility(View.INVISIBLE);
                        progressBarTwo.setVisibility(View.INVISIBLE);
                        //progress_menu_logo_two.setVisibility(View.INVISIBLE);

                    }


                } else {
                    //  Toast.makeText(getContext(),"No friends",Toast.LENGTH_SHORT).show();

                    result_text.setVisibility(View.VISIBLE);
                    result_text.setText("No friends");
                    progressBar.setVisibility(View.INVISIBLE);
                    //progress_menu_logo.setVisibility(View.INVISIBLE);
                    progressBarTwo.setVisibility(View.INVISIBLE);
                    //progress_menu_logo_two.setVisibility(View.INVISIBLE);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }


    private void setThumnailFromVideoId(String videoID) {
        TaskCompletionSource<Bitmap> bitmapTaskCompletionSource = new TaskCompletionSource<>();

        new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    Bitmap bitmap = Glide.with(YoutubeLiveVideoSelectionActivity.this)
                            .asBitmap()
                            .load("https://img.youtube.com/vi/" + videoID + "/0.jpg")
                            .submit(512, 512)
                            .get();

                    bitmapTaskCompletionSource.setResult(bitmap);
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            /*bitmap = BitmapFactory.decodeResource(getResources(),
                    R.drawable.profile_pic);*/


            }
        }).start();

        Task<Bitmap> bitmapTask = bitmapTaskCompletionSource.getTask();

        bitmapTask.addOnCompleteListener(new OnCompleteListener<Bitmap>() {
            @Override
            public void onComplete(@NonNull Task<Bitmap> task) {
                if (task.isSuccessful()) {
                    video_thumbnail.setImageBitmap(task.getResult());
                    youtube_video_view.setVisibility(View.VISIBLE);

                    try {
                        youtube_web_view.stopLoading();
                        /*youtube_web_view.loadUrl(yoututbeHomeURL);*/
                        youtube_web_view.setVisibility(View.GONE);
                        youtube_url.setText(youtube_Url);
                    } catch (Exception e) {

                    }
                }

            }
        });


    }


    private void getVideoIdfromUrl(String url) {


        //Toast.makeText(YoutubeLiveVideoSelectionActivity.this, "Called", Toast.LENGTH_SHORT).show();
        TaskCompletionSource<String> stringTaskCompletionSource = new TaskCompletionSource<>();

        new Thread(new Runnable() {
            @Override
            public void run() {

                String videoId = "default";

                String regex = "http(?:s)?:\\/\\/(?:m.)?(?:www\\.)?youtu(?:\\.be\\/|be\\.com\\/(?:watch\\?(?:feature=youtu.be\\&)?v=|v\\/|embed\\/|user\\/(?:[\\w#]+\\/)+))([^&#?\\n]+)";
                Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
                Matcher matcher = pattern.matcher(url);
                if (matcher.find()) {
                    videoId = matcher.group(1);
                }

                stringTaskCompletionSource.setResult(videoId);
            }
        }).start();


        Task<String> stringTask = stringTaskCompletionSource.getTask();

        stringTask.addOnCompleteListener(new OnCompleteListener<String>() {
            @Override
            public void onComplete(@NonNull Task<String> task) {
                if (task.isSuccessful()) {
                    setThumnailFromVideoId(task.getResult());
                    if (!task.getResult().equals("default")) {
                        youtube_web_view.stopLoading();
                        youtube_web_view.setVisibility(View.GONE);
                    }


                }
            }
        });
    }
}
