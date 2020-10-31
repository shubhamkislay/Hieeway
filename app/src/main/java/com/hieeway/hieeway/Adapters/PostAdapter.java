package com.hieeway.hieeway.Adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BlurMaskFilter;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hieeway.hieeway.CreateShotActivity;
import com.hieeway.hieeway.CustomImageView;
import com.hieeway.hieeway.ExoPlayerActivity;
import com.hieeway.hieeway.Model.ChatStamp;
import com.hieeway.hieeway.Model.Post;
import com.hieeway.hieeway.Model.User;
import com.hieeway.hieeway.MusicFeedActivity;
import com.hieeway.hieeway.OpenMessageShotActivity;
import com.hieeway.hieeway.PhotoViewActivity;
import com.hieeway.hieeway.R;
import com.hieeway.hieeway.Utils.ChatStampListDiffUtilCallback;
import com.hieeway.hieeway.Utils.PostListDiffUtilCallback;
import com.jgabrielfreitas.core.BlurImageView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import org.ocpsoft.prettytime.PrettyTime;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;
import es.claucookie.miniequalizerlibrary.EqualizerView;

public class PostAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYP_MUSIC = 0;
    private static final int TYP_TEXT = 1;
    private static final int TYP_PHOTO = 2;
    private static final int TYP_VIDEO = 3;
    private static final int TYP_YOUTUBE = 4;
    private static final int TYP_CREATE = 5;
    private List<Post> postList = new ArrayList<>();
    private Context context;
    private Activity activity;
    private String userId;
    ImageLoader imageLoader = ImageLoader.getInstance();
    //ImageSize targetSize = new ImageSize(100, 200);


    public PostAdapter(List<Post> postList, Activity activity, String userId) {
        this.postList.addAll(postList);
        this.context = (Context) activity;
        this.userId = userId;
        this.activity = activity;
        try {
            imageLoader.init(ImageLoaderConfiguration.createDefault(activity.getApplicationContext()));
        } catch (Exception e) {
            //
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if (viewType == TYP_MUSIC) {
            View view = LayoutInflater.from(context).inflate(R.layout.music_post_item, parent, false);

            Display display = activity.getWindowManager().getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            float dispSize = size.x;

            BlurImageView backItem = view.findViewById(R.id.photo);

            backItem.getLayoutParams().height = (int) dispSize * 75 / 100;

            TextView type = view.findViewById(R.id.type);
            type.setTypeface(Typeface.createFromAsset(activity.getAssets(), "fonts/samsungsharpsans-bold.otf"));

            //backItem.getLayoutParams().height = (int) dispSize * 65 / 100;
            return new PostViewHolder(view);

        } else if (viewType == TYP_TEXT) {
            View view = LayoutInflater.from(context).inflate(R.layout.music_post_item, parent, false);

            Display display = activity.getWindowManager().getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            float dispSize = size.x;

            BlurImageView backItem = view.findViewById(R.id.photo);

            backItem.getLayoutParams().height = (int) dispSize * 75 / 100;

            TextView type = view.findViewById(R.id.type);
            type.setTypeface(Typeface.createFromAsset(activity.getAssets(), "fonts/samsungsharpsans-bold.otf"));

            //backItem.getLayoutParams().height = (int) dispSize * 65 / 100;
            return new PostViewHolder(view);

        } else if (viewType == TYP_PHOTO) {
            View view = LayoutInflater.from(context).inflate(R.layout.shots_post_media_item, parent, false);


            Display display = activity.getWindowManager().getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            float dispSize = size.x;

            BlurImageView backItem = view.findViewById(R.id.photo);

            backItem.getLayoutParams().height = (int) dispSize * 75 / 100;

            TextView type = view.findViewById(R.id.type);
            type.setTypeface(Typeface.createFromAsset(activity.getAssets(), "fonts/samsungsharpsans-bold.otf"));


            return new PostMediaViewHolder(view);
        } else if (viewType == TYP_VIDEO) {
            View view = LayoutInflater.from(context).inflate(R.layout.shots_post_media_item, parent, false);

            Display display = activity.getWindowManager().getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            float dispSize = size.x;

            BlurImageView backItem = view.findViewById(R.id.photo);

            backItem.getLayoutParams().height = (int) dispSize * 75 / 100;

            TextView type = view.findViewById(R.id.type);
            type.setTypeface(Typeface.createFromAsset(activity.getAssets(), "fonts/samsungsharpsans-bold.otf"));


            return new PostMediaViewHolder(view);
        } else if (viewType == TYP_CREATE) {
            View view = LayoutInflater.from(context).inflate(R.layout.post_create_item, parent, false);

            Display display = activity.getWindowManager().getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            float dispSize = size.x;

            RelativeLayout backItem = view.findViewById(R.id.item_back);

            backItem.getLayoutParams().height = (int) dispSize * 75 / 100;

            TextView back_bottom = view.findViewById(R.id.create_shot_txt);
            back_bottom.setTypeface(Typeface.createFromAsset(activity.getAssets(), "fonts/samsungsharpsans-bold.otf"));


            return new PostViewCreateHolder(view);
        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.shots_post_item, parent, false);


            return new PostViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {


        if (position == 0) {

            PostViewCreateHolder postViewCreateHolder = (PostViewCreateHolder) holder;
            postViewCreateHolder.create_shot_txt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    activity.startActivity(new Intent(activity, CreateShotActivity.class));
                }
            });

        } else {

            if (!postList.get(holder.getAdapterPosition()).getType().equals("video")
                    && !postList.get(holder.getAdapterPosition()).getType().equals("photo")) {

                PostViewHolder postViewHolder = (PostViewHolder) holder;
                Post post = postList.get(postViewHolder.getAdapterPosition());

                postViewHolder.username.setText(post.getUsername());


                try {
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
                    Date parsedDate = dateFormat.parse(post.getTimeStamp());


                    PrettyTime prettyTime = new PrettyTime(Locale.getDefault());

                    String ago = prettyTime.format(parsedDate).replace("moments ago", "now");

                    ago = ago.replace("moments from now", "just now");


                    postViewHolder.timestamp.setText("" + ago);
                } catch (Exception e) {
                    //
                }


                //} catch (Exception e) { //this generic but you can control another types of exception
                // look the origin of excption


                //Toast.makeText(context,"Exception: "+e.toString(),Toast.LENGTH_SHORT).show();


                getLatestProfilePic(post.getUserId(), postViewHolder.user_photo);


                postViewHolder.user_photo.setOnTouchListener(new View.OnTouchListener() {
                    @SuppressLint("ClickableViewAccessibility")
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {

                        if (event.getAction() == MotionEvent.ACTION_DOWN) {


                            // viewHolder.user_photo.setAlpha(0.4f);

                            postViewHolder.user_photo.animate().scaleX(0.95f).scaleY(0.95f).setDuration(0);
                            postViewHolder.post_ring.animate().scaleX(0.95f).scaleY(0.95f).setDuration(0);


                        } else if (event.getAction() == MotionEvent.ACTION_UP) {

                            postViewHolder.user_photo.animate().scaleX(1.0f).scaleY(1.0f).setDuration(50);
                            postViewHolder.post_ring.animate().scaleX(1.0f).scaleY(1.0f).setDuration(50);


                            //         viewHolder.user_photo.animate().alpha(1.0f).setDuration(50);


                        } else {
                            // viewHolder.user_photo.animate().setDuration(50).alpha(1.0f);


                            postViewHolder.user_photo.animate().scaleX(1.0f).scaleY(1.0f).setDuration(50);
                            postViewHolder.post_ring.animate().scaleX(1.0f).scaleY(1.0f).setDuration(50);
                        }

                        return false;
                    }

                });


                if (post.getType().equals("music")) {


                    postViewHolder.back_text.setText("");
                    postViewHolder.photo.setImageDrawable(context.getDrawable(R.drawable.headphone));


                /*new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        postViewHolder.equalizer_view_two.animateBars();
                        postViewHolder.equalizer_view_four.animateBars();
                        postViewHolder.equalizer_view_six.animateBars();
                        postViewHolder.equalizer_view_eight.animateBars();
                    }
                }, 300);

                postViewHolder.equalizer.animateBars();
                postViewHolder.equalizer_view_three.animateBars();
                postViewHolder.equalizer_view_five.animateBars();
                postViewHolder.equalizer_view_seven.animateBars();


*/
                    postViewHolder.type.setText("Music");
                    postViewHolder.by_beacon.setText("Music Beacon");
                    postViewHolder.photo.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Toast.makeText(context, "Opening Music Item", Toast.LENGTH_SHORT).show();

                            Intent intent = new Intent(activity, MusicFeedActivity.class);
                            intent.putExtra("otherUserId", post.getUserId());
                            intent.putExtra("otherUserName", post.getUsername());
                            intent.putExtra("otherUserPhoto", "default");

                            activity.startActivity(intent);

                        }
                    });


                } else if (post.getType().equals("message")) {

                    postViewHolder.photo.setImageDrawable(context.getDrawable(R.drawable.loading_default));
                    postViewHolder.photo.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    postViewHolder.photo.setAlpha(0.0f);

                    try {

                        postViewHolder.back_text.setText(post.getMediaUrl());

                        float radius = postViewHolder.back_text.getTextSize() / (5);

                        BlurMaskFilter filter = new BlurMaskFilter(radius, BlurMaskFilter.Blur.NORMAL);
                        postViewHolder.back_text.getPaint().setMaskFilter(filter);

                    } catch (Exception e) {
                        //
                    }

                    postViewHolder.by_beacon.setText("");
                    postViewHolder.type.setText(post.getType());
                    postViewHolder.photo.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(activity, OpenMessageShotActivity.class);
                            intent.putExtra("message", post.getMediaUrl());
                            intent.putExtra("postKey", post.getPostKey());
                            context.startActivity(intent);
                        }
                    });
                } else {
                    postViewHolder.by_beacon.setText("");
                    postViewHolder.type.setText(post.getType());
                }
            } else {
                PostMediaViewHolder postViewHolder = (PostMediaViewHolder) holder;
                Post post = postList.get(postViewHolder.getAdapterPosition());

                postViewHolder.username.setText(post.getUsername());
                postViewHolder.type.setText(post.getType());


                try {
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
                    Date parsedDate = dateFormat.parse(post.getTimeStamp());


                    PrettyTime prettyTime = new PrettyTime(Locale.getDefault());

                    String ago = prettyTime.format(parsedDate).replace("moments ago", "now");

                    ago = ago.replace("moments from now", "just now");


                    postViewHolder.timestamp.setText("" + ago);
                } catch (Exception e) {
                    //
                }

                try {
                    FirebaseDatabase.getInstance().getReference("Users")
                            .child(post.getUserId())
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (snapshot.exists()) {
                                        User user = snapshot.getValue(User.class);

                                        try {
                                            Glide.with(context)
                                                    .load(user.getPhoto())
                                                    .into(postViewHolder.user_photo);
                                        } catch (Exception e) {
                                            Glide.with(context)
                                                    .load(context.getDrawable(R.drawable.no_profile))
                                                    .into(postViewHolder.user_photo);
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });

                } catch (Exception e) {

                }

                if (post.getType().equals("video")) {

                    long thumb = postViewHolder.getAdapterPosition() * 1000;
                    RequestOptions options = new RequestOptions().frame(thumb);
                    Glide.with(context)
                            .load(post.getMediaUrl())
                            .apply(options)
                            .addListener(new RequestListener<Drawable>() {
                                @Override
                                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                    return false;
                                }

                                @Override
                                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {


                                    postViewHolder.photo.animate().alpha(1.0f).setDuration(750);

                                    new Handler().postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            try {
                                                postViewHolder.photo.setBlur(15);
                                            } catch (Exception e) {
                                                //
                                            }
                                        }
                                    }, 750);

                                    return false;
                                }
                            })
                            .into(postViewHolder.photo);
                    postViewHolder.photo.setScaleType(ImageView.ScaleType.CENTER_CROP);


                    postViewHolder.photo.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(context, ExoPlayerActivity.class);
                            intent.putExtra("path", post.getMediaUrl());
                            context.startActivity(intent);
                        }
                    });
                } else {

                    Glide.with(context)
                            .load(post.getMediaUrl())
                            .addListener(new RequestListener<Drawable>() {
                                @Override
                                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                    return false;
                                }

                                @Override
                                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                    postViewHolder.photo.animate().alpha(1.0f).setDuration(750);

                                    new Handler().postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            try {
                                                postViewHolder.photo.setBlur(5);
                                            } catch (Exception e) {
                                                //
                                            }
                                        }
                                    }, 750);
                                    return false;
                                }
                            })
                            .into(postViewHolder.photo);

                    postViewHolder.photo.setScaleType(ImageView.ScaleType.CENTER_CROP);


                    postViewHolder.photo.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(context, PhotoViewActivity.class);
                            intent.putExtra("path", post.getMediaUrl());
                            context.startActivity(intent);
                        }
                    });
                }


            }

        }


    }

    private void getLatestProfilePic(String userId, CircleImageView user_photo) {

        FirebaseDatabase.getInstance().getReference("Users")
                .child(userId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            User user = snapshot.getValue(User.class);

                            try {
                                if (!user.getPhoto().equals("default")) {
                                    Glide.with(context)
                                            .load(user.getPhoto())
                                            .addListener(new RequestListener<Drawable>() {
                                                @Override
                                                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                                    return false;
                                                }

                                                @Override
                                                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {

                                                    if (user.getPhoto().contains("https://firebasestorage")) {
                                                        final Matrix matrix = user_photo.getImageMatrix();
                                                        /*final float imageWidth = resource.getIntrinsicWidth();
                                                        final int screenWidth = context.getResources().getDisplayMetrics().widthPixels / 2;
                                                        final float scaleRatio = screenWidth / imageWidth;*/
                                                        // matrix.postScale(scaleRatio, scaleRatio);
                                                        matrix.postScale(1, 1);
                                                        user_photo.setImageMatrix(matrix);


                                                    } else {
                                                        user_photo.setScaleType(ImageView.ScaleType.CENTER_CROP);

                                                    }


                                                    return false;
                                                }
                                            })
                                            .into(user_photo);


                                }


                            } catch (Exception e) {
                                Glide.with(context)
                                        .load(context.getDrawable(R.drawable.no_profile))
                                        .into(user_photo);
                            }
                        } else {
                            Glide.with(context)
                                    .load(context.getDrawable(R.drawable.no_profile))
                                    .into(user_photo);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    public void updateList(List<Post> newListChatStamp) {


        PostListDiffUtilCallback chatStampListDiffUtilCallback = new PostListDiffUtilCallback(this.postList, newListChatStamp);
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(chatStampListDiffUtilCallback);

        postList.clear();
        /*Post postDummy = new Post();
        postDummy.setMediaKey("xyz");
        postDummy.setMediaUrl("xyz");
        postDummy.setPostKey("xyz");
        postDummy.setTimeStamp("xyz");
        postDummy.setType("xyz");
        postDummy.setUsername("xyz");
        postDummy.setUserId("xyz");

        postList.add(postDummy);*/

        postList.addAll(newListChatStamp);

        diffResult.dispatchUpdatesTo(this);


    }


    @Override
    public int getItemViewType(int position) {

        /*Post post = postList.get(position);

        if(post.getType().equals("music"))
            return TYP_MUSIC;
        else if(post.getType().equals("text"))
            return TYP_TEXT;
        else if(post.getType().equals("youtube"))
            return TYP_YOUTUBE;
        else*/
        if (position == 0)
            return TYP_CREATE;
        else if (postList.get(position).getType().equals("message")) {
            return TYP_TEXT;
        } else if (postList.get(position).getType().equals("music"))
            return TYP_MUSIC;
        else if (postList.get(position).getType().equals("photo"))
            return TYP_PHOTO;
        else if (postList.get(position).getType().equals("video"))
            return TYP_VIDEO;
        else
            return TYP_MUSIC;
    }

    public class PostViewHolder extends RecyclerView.ViewHolder {

        private CircleImageView user_photo;

        private TextView username, timestamp, type, by_beacon, back_text;
        private RelativeLayout post_ring;
        private BlurImageView photo;


       /* EqualizerView equalizer, equalizer_view_two, equalizer_view_three, equalizer_view_four,
                equalizer_view_five, equalizer_view_six, equalizer_view_seven, equalizer_view_eight;*/

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);

            user_photo = itemView.findViewById(R.id.user_photo);
            username = itemView.findViewById(R.id.username);
            timestamp = itemView.findViewById(R.id.timestamp);
            type = itemView.findViewById(R.id.type);
            by_beacon = itemView.findViewById(R.id.by_beacon);
            post_ring = itemView.findViewById(R.id.post_ring);
            photo = itemView.findViewById(R.id.photo);
            back_text = itemView.findViewById(R.id.back_text);

            /*equalizer = (EqualizerView) itemView.findViewById(R.id.equalizer_view);

            equalizer_view_two = (EqualizerView) itemView.findViewById(R.id.equalizer_view_two);
            equalizer_view_three = (EqualizerView) itemView.findViewById(R.id.equalizer_view_three);
            equalizer_view_four = (EqualizerView) itemView.findViewById(R.id.equalizer_view_four);
            equalizer_view_five = (EqualizerView) itemView.findViewById(R.id.equalizer_view_five);
            equalizer_view_six = (EqualizerView) itemView.findViewById(R.id.equalizer_view_six);
            equalizer_view_seven = (EqualizerView) itemView.findViewById(R.id.equalizer_view_seven);
            equalizer_view_eight = (EqualizerView) itemView.findViewById(R.id.equalizer_view_eight);*/

        }
    }

    public class PostMediaViewHolder extends RecyclerView.ViewHolder {

        private CircleImageView user_photo;
        private TextView username, timestamp, type, by_beacon;
        private RelativeLayout post_ring;
        private BlurImageView photo;


        public PostMediaViewHolder(@NonNull View itemView) {
            super(itemView);

            user_photo = itemView.findViewById(R.id.user_photo);
            username = itemView.findViewById(R.id.username);
            timestamp = itemView.findViewById(R.id.timestamp);
            type = itemView.findViewById(R.id.type);
            by_beacon = itemView.findViewById(R.id.by_beacon);
            post_ring = itemView.findViewById(R.id.post_ring);
            photo = itemView.findViewById(R.id.photo);


        }
    }

    public class PostViewCreateHolder extends RecyclerView.ViewHolder {

        private TextView create_shot_txt;

        public PostViewCreateHolder(@NonNull View itemView) {
            super(itemView);
            create_shot_txt = itemView.findViewById(R.id.create_shot_txt);
        }
    }


}
