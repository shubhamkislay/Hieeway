package com.hieeway.hieeway.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
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
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hieeway.hieeway.CustomImageView;
import com.hieeway.hieeway.Model.ChatStamp;
import com.hieeway.hieeway.Model.Post;
import com.hieeway.hieeway.Model.User;
import com.hieeway.hieeway.MusicFeedActivity;
import com.hieeway.hieeway.R;
import com.hieeway.hieeway.Utils.ChatStampListDiffUtilCallback;
import com.hieeway.hieeway.Utils.PostListDiffUtilCallback;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.util.ArrayList;
import java.util.List;

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
        imageLoader.init(ImageLoaderConfiguration.createDefault(context));
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

            CustomImageView backItem = view.findViewById(R.id.user_photo);

            backItem.getLayoutParams().height = (int) dispSize * 75 / 100;

            TextView back_bottom = view.findViewById(R.id.back_bottom);
            back_bottom.setTypeface(Typeface.createFromAsset(activity.getAssets(), "fonts/samsungsharpsans-bold.otf"));

            //backItem.getLayoutParams().height = (int) dispSize * 65 / 100;
            return new PostViewHolder(view);

        } else if (viewType == TYP_TEXT) {
            View view = LayoutInflater.from(context).inflate(R.layout.group_msg_sending_perf, parent, false);


            return new PostViewHolder(view);
        } else if (viewType == TYP_PHOTO) {
            View view = LayoutInflater.from(context).inflate(R.layout.group_msg_sending_perf, parent, false);


            return new PostViewHolder(view);
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


            return new PostViewHolder(view);
        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.post_create_item, parent, false);


            return new PostViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {


        if (position == 0) {

        } else {

            PostViewHolder postViewHolder = (PostViewHolder) holder;
            Post post = postList.get(postViewHolder.getAdapterPosition());

            postViewHolder.username.setText(post.getUsername());

            getLatestProfilePic(post.getUserId(), postViewHolder.user_photo);


            if (post.getType().equals("music")) {


                new Handler().postDelayed(new Runnable() {
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


                postViewHolder.user_photo.setOnClickListener(new View.OnClickListener() {
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
            }
        }


    }

    private void getLatestProfilePic(String userId, CustomImageView user_photo) {

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

                                                    final Matrix matrix = user_photo.getImageMatrix();
                                                    final float imageWidth = resource.getIntrinsicWidth();
                                                    final int screenWidth = context.getResources().getDisplayMetrics().widthPixels / 2;
                                                    final float scaleRatio = screenWidth / imageWidth;
                                                    // matrix.postScale(scaleRatio, scaleRatio);
                                                    matrix.postScale(1, 1);
                                                    user_photo.setImageMatrix(matrix);


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
        else
            return TYP_MUSIC;
    }

    public class PostViewHolder extends RecyclerView.ViewHolder {

        private CustomImageView user_photo;
        private TextView username;
        EqualizerView equalizer, equalizer_view_two, equalizer_view_three, equalizer_view_four,
                equalizer_view_five, equalizer_view_six, equalizer_view_seven, equalizer_view_eight;

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);

            user_photo = itemView.findViewById(R.id.user_photo);
            username = itemView.findViewById(R.id.username);

            equalizer = (EqualizerView) itemView.findViewById(R.id.equalizer_view);

            equalizer_view_two = (EqualizerView) itemView.findViewById(R.id.equalizer_view_two);
            equalizer_view_three = (EqualizerView) itemView.findViewById(R.id.equalizer_view_three);
            equalizer_view_four = (EqualizerView) itemView.findViewById(R.id.equalizer_view_four);
            equalizer_view_five = (EqualizerView) itemView.findViewById(R.id.equalizer_view_five);
            equalizer_view_six = (EqualizerView) itemView.findViewById(R.id.equalizer_view_six);
            equalizer_view_seven = (EqualizerView) itemView.findViewById(R.id.equalizer_view_seven);
            equalizer_view_eight = (EqualizerView) itemView.findViewById(R.id.equalizer_view_eight);

        }
    }

}
