package com.hieeway.hieeway.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.BlurMaskFilter;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
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
import com.hieeway.hieeway.ExoPlayerActivity;
import com.hieeway.hieeway.Model.Post;
import com.hieeway.hieeway.Model.User;
import com.hieeway.hieeway.MusicFeedActivity;
import com.hieeway.hieeway.OpenMessageShotActivity;
import com.hieeway.hieeway.PhotoViewActivity;
import com.hieeway.hieeway.R;
import com.hieeway.hieeway.Utils.PostListDiffUtilCallback;
import com.jgabrielfreitas.core.BlurImageView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import org.ocpsoft.prettytime.PrettyTime;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

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
        //this.postList.addAll(postList);
        this.postList = postList;
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
            return new PostMusicViewHolder(view);

        } else if (viewType == TYP_TEXT) {
            View view = LayoutInflater.from(context).inflate(R.layout.message_post_item, parent, false);

            Display display = activity.getWindowManager().getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            float dispSize = size.x;

            BlurImageView backItem = view.findViewById(R.id.photo);

            backItem.getLayoutParams().height = (int) dispSize * 75 / 100;

            TextView type = view.findViewById(R.id.type);
            type.setTypeface(Typeface.createFromAsset(activity.getAssets(), "fonts/samsungsharpsans-bold.otf"));


            return new PostMessageHolder(view);

        } else if (viewType == TYP_PHOTO) {
            View view = LayoutInflater.from(context).inflate(R.layout.shots_post_photo_item, parent, false);


            Display display = activity.getWindowManager().getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            float dispSize = size.x;

            BlurImageView backItem = view.findViewById(R.id.photo);

            backItem.getLayoutParams().height = (int) dispSize * 75 / 100;

            TextView type = view.findViewById(R.id.type);
            type.setTypeface(Typeface.createFromAsset(activity.getAssets(), "fonts/samsungsharpsans-bold.otf"));


            return new PostPhotoViewHolder(view);
        } else if (viewType == TYP_VIDEO) {
            View view = LayoutInflater.from(context).inflate(R.layout.shots_post_video_item, parent, false);

            Display display = activity.getWindowManager().getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            float dispSize = size.x;

            BlurImageView backItem = view.findViewById(R.id.photo);

            backItem.getLayoutParams().height = (int) dispSize * 75 / 100;

            TextView type = view.findViewById(R.id.type);
            type.setTypeface(Typeface.createFromAsset(activity.getAssets(), "fonts/samsungsharpsans-bold.otf"));


            return new PostVideoViewHolder(view);
        } else /*if (viewType == TYP_CREATE)*/ {
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
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        if (postList.get(holder.getAdapterPosition()).getType().equals("message")) {
            PostMessageHolder postMessageHolder = (PostMessageHolder) holder;
            Post post = postList.get(postMessageHolder.getAdapterPosition());

            /**
             * Timestamp
             */
            try {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
                Date parsedDate = dateFormat.parse(post.getTimeStamp());


                PrettyTime prettyTime = new PrettyTime(Locale.getDefault());

                String ago = prettyTime.format(parsedDate).replace("moments ago", "now");

                ago = ago.replace("moments from now", "just now");


                postMessageHolder.timestamp.setText("" + ago);
            } catch (Exception e) {
            }

            /**
             * Setting blurred text
             */
            try {
                postMessageHolder.back_text.setText(post.getMediaUrl());
                float radius = postMessageHolder.back_text.getTextSize() / (5);
                BlurMaskFilter filter = new BlurMaskFilter(radius, BlurMaskFilter.Blur.NORMAL);
                postMessageHolder.back_text.getPaint().setMaskFilter(filter);

            } catch (Exception e) {
            }

            postMessageHolder.photo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(activity, OpenMessageShotActivity.class);
                    intent.putExtra("message", post.getMediaUrl());
                    intent.putExtra("postKey", post.getPostKey());
                    intent.putExtra("otherUserId", post.getUserId());
                    context.startActivity(intent);
                }
            });
            getLatestProfilePic(post.getUserId(), postMessageHolder.user_photo);
        } else if (postList.get(holder.getAdapterPosition()).getType().equals("music")) {
            PostMusicViewHolder postMusicViewHolder = (PostMusicViewHolder) holder;
            Post post = postList.get(postMusicViewHolder.getAdapterPosition());

            postMusicViewHolder.photo.setImageDrawable(context.getDrawable(R.drawable.headphone));
            postMusicViewHolder.username.setText(post.getUsername());

            /**
             * Timestamp
             */
            try {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
                Date parsedDate = dateFormat.parse(post.getTimeStamp());


                PrettyTime prettyTime = new PrettyTime(Locale.getDefault());

                String ago = prettyTime.format(parsedDate).replace("moments ago", "now");

                ago = ago.replace("moments from now", "just now");


                postMusicViewHolder.timestamp.setText("" + ago);
            } catch (Exception e) {
            }

            postMusicViewHolder.photo.setOnClickListener(new View.OnClickListener() {
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
            getLatestProfilePic(post.getUserId(), postMusicViewHolder.user_photo);
        } else if (postList.get(holder.getAdapterPosition()).getType().equals("photo")) {

            PostPhotoViewHolder postPhotoViewHolder = (PostPhotoViewHolder) holder;
            Post post = postList.get(postPhotoViewHolder.getAdapterPosition());
            postPhotoViewHolder.username.setText(post.getUsername());

            /**
             * Timestamp
             */
            try {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
                Date parsedDate = dateFormat.parse(post.getTimeStamp());
                PrettyTime prettyTime = new PrettyTime(Locale.getDefault());
                String ago = prettyTime.format(parsedDate).replace("moments ago", "now");
                ago = ago.replace("moments from now", "just now");
                postPhotoViewHolder.timestamp.setText("" + ago);
            } catch (Exception e) {
            }

            /**
             * User photo
             */
            getLatestProfilePic(post.getUserId(), postPhotoViewHolder.user_photo);

            Glide.with(context)
                    .load(post.getMediaUrl())
                    .addListener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {


                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        postPhotoViewHolder.photo.setBlur(5);
                                        postPhotoViewHolder.photo.animate().alpha(1.0f).setDuration(750);
                                    } catch (Exception e) {
                                        //
                                    }
                                }
                            }, 1000);
                            return false;
                        }
                    })
                    .into(postPhotoViewHolder.photo);

            postPhotoViewHolder.photo.setScaleType(ImageView.ScaleType.CENTER_CROP);
            postPhotoViewHolder.photo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, PhotoViewActivity.class);
                    intent.putExtra("path", post.getMediaUrl());
                    intent.putExtra("requestType", "shot");
                    intent.putExtra("postKey", post.getPostKey());
                    intent.putExtra("otherUserId", post.getUserId());

                    context.startActivity(intent);
                }
            });
        } else if (postList.get(holder.getAdapterPosition()).getType().equals("video")) {
            PostPhotoViewHolder postVideoViewHolder = (PostPhotoViewHolder) holder;
            Post post = postList.get(postVideoViewHolder.getAdapterPosition());
            postVideoViewHolder.username.setText(post.getUsername());

            /**
             * Timestamp
             */
            try {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
                Date parsedDate = dateFormat.parse(post.getTimeStamp());
                PrettyTime prettyTime = new PrettyTime(Locale.getDefault());
                String ago = prettyTime.format(parsedDate).replace("moments ago", "now");
                ago = ago.replace("moments from now", "just now");
                postVideoViewHolder.timestamp.setText("" + ago);
            } catch (Exception e) {
            }

            /**
             * User photo
             */

            getLatestProfilePic(post.getUserId(), postVideoViewHolder.user_photo);

            long thumb = position * 1000;
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


                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        postVideoViewHolder.photo.setBlur(15);
                                        postVideoViewHolder.photo.animate().alpha(1.0f).setDuration(750);
                                    } catch (Exception e) {
                                        //
                                    }


                                }
                            }, 750);

                            return false;
                        }
                    })
                    .into(postVideoViewHolder.photo);
            postVideoViewHolder.photo.setScaleType(ImageView.ScaleType.CENTER_CROP);
            postVideoViewHolder.photo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, ExoPlayerActivity.class);
                    intent.putExtra("path", post.getMediaUrl());
                    intent.putExtra("requestType", "shot");
                    intent.putExtra("postKey", post.getPostKey());
                    intent.putExtra("otherUserId", post.getUserId());
                    context.startActivity(intent);
                }
            });
        } else {
            PostViewCreateHolder postViewCreateHolder = (PostViewCreateHolder) holder;
            postViewCreateHolder.create_shot_txt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    activity.startActivity(new Intent(activity, CreateShotActivity.class));
                }
            });
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


        postList.addAll(newListChatStamp);

        diffResult.dispatchUpdatesTo(this);


    }


    @Override
    public int getItemViewType(int position) {

        /*Post post = postList.get(postion);

        if(post.getType().equals("music"))
            return TYP_MUSIC;
        else if(post.getType().equals("text"))
            return TYP_TEXT;
        else if(post.getType().equals("youtube"))
            return TYP_YOUTUBE;
        else*/

        if (postList.get(position).getType().equals("message"))
            return TYP_TEXT;
        else if (postList.get(position).getType().equals("music"))
            return TYP_MUSIC;
        else if (postList.get(position).getType().equals("photo"))
            return TYP_PHOTO;
        else if (postList.get(position).getType().equals("video"))
            return TYP_VIDEO;
        else
            return TYP_CREATE;
    }

    public class PostMusicViewHolder extends RecyclerView.ViewHolder {

        private CircleImageView user_photo;

        private TextView username, timestamp, type, by_beacon, back_text;
        private RelativeLayout post_ring;
        private BlurImageView photo;
        private ConstraintLayout parent_layout;


       /* EqualizerView equalizer, equalizer_view_two, equalizer_view_three, equalizer_view_four,
                equalizer_view_five, equalizer_view_six, equalizer_view_seven, equalizer_view_eight;*/

        public PostMusicViewHolder(@NonNull View itemView) {
            super(itemView);

            user_photo = itemView.findViewById(R.id.user_photo);
            username = itemView.findViewById(R.id.username);
            timestamp = itemView.findViewById(R.id.timestamp);
            type = itemView.findViewById(R.id.type);
            by_beacon = itemView.findViewById(R.id.by_beacon);
            post_ring = itemView.findViewById(R.id.post_ring);
            photo = itemView.findViewById(R.id.photo);
            back_text = itemView.findViewById(R.id.back_text);
            parent_layout = itemView.findViewById(R.id.parent_layout);

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

    public class PostMessageHolder extends RecyclerView.ViewHolder {

        private CircleImageView user_photo;

        private TextView username, timestamp, type, back_text;
        private RelativeLayout post_ring;
        private BlurImageView photo;
        private ConstraintLayout parent_layout;


        public PostMessageHolder(@NonNull View itemView) {
            super(itemView);

            user_photo = itemView.findViewById(R.id.user_photo);
            username = itemView.findViewById(R.id.username);
            timestamp = itemView.findViewById(R.id.timestamp);
            type = itemView.findViewById(R.id.type);
            post_ring = itemView.findViewById(R.id.post_ring);
            photo = itemView.findViewById(R.id.photo);
            back_text = itemView.findViewById(R.id.back_text);
            parent_layout = itemView.findViewById(R.id.parent_layout);

        }
    }

    public class PostPhotoViewHolder extends RecyclerView.ViewHolder {

        private CircleImageView user_photo;
        private TextView username, timestamp, type, by_beacon;
        private RelativeLayout post_ring;
        private BlurImageView photo;
        private ConstraintLayout parent_layout;


        public PostPhotoViewHolder(@NonNull View itemView) {
            super(itemView);

            user_photo = itemView.findViewById(R.id.user_photo);
            username = itemView.findViewById(R.id.username);
            timestamp = itemView.findViewById(R.id.timestamp);
            type = itemView.findViewById(R.id.type);
            post_ring = itemView.findViewById(R.id.post_ring);
            photo = itemView.findViewById(R.id.photo);
            parent_layout = itemView.findViewById(R.id.parent_layout);


        }
    }

    public class PostVideoViewHolder extends RecyclerView.ViewHolder {

        private CircleImageView user_photo;
        private TextView username, timestamp, type, by_beacon;
        private RelativeLayout post_ring;
        private BlurImageView photo;
        private ConstraintLayout parent_layout;


        public PostVideoViewHolder(@NonNull View itemView) {
            super(itemView);

            user_photo = itemView.findViewById(R.id.user_photo);
            username = itemView.findViewById(R.id.username);
            timestamp = itemView.findViewById(R.id.timestamp);
            type = itemView.findViewById(R.id.type);
            post_ring = itemView.findViewById(R.id.post_ring);
            photo = itemView.findViewById(R.id.photo);
            parent_layout = itemView.findViewById(R.id.parent_layout);


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
