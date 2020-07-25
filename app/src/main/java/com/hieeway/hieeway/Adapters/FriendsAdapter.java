package com.hieeway.hieeway.Adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.google.firebase.auth.FirebaseAuth;
import com.hieeway.hieeway.Model.User;
import com.hieeway.hieeway.R;
import com.hieeway.hieeway.VerticalPageActivity;

import java.util.List;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

public class FriendsAdapter  extends RecyclerView.Adapter<FriendsAdapter.ViewHolder> {

    private Context mContext;
    private Activity activity;
    private List<User> mUsers;
    public FriendsAdapter(Context mContext, List<User> mUsers,Activity activity) {


        this.activity = activity;
        this.mUsers = mUsers;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.edge_to_edge, viewGroup, false );


        Display display = activity.getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        float dispSize = size.x;

        RelativeLayout chatItem = view.findViewById(R.id.chatbox_tem);

        chatItem.getLayoutParams().height = (int) dispSize * 6 / 10;

        return new FriendsAdapter.ViewHolder(view);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, int i) {


        viewHolder.longMsgBtn.setVisibility(View.GONE);
        final User user = mUsers.get(viewHolder.getAdapterPosition());
        final int position = viewHolder.getAdapterPosition();

        viewHolder.username.setText(user.getUsername());


        // viewHolder.username.setTypeface(Typeface.createFromAsset(mContext.getAssets(), "fonts/samsungsharpsans-medium.otf"));



        viewHolder.progressBarOne.setVisibility(View.VISIBLE);
        viewHolder.progressBarTwo.setVisibility(View.VISIBLE);



        viewHolder.user_photo.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {


                //Unfriend and block options to be built here

                return false;
            }
        });

        /*viewHolder.user_photo.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if (event.getAction() == MotionEvent.ACTION_DOWN) {


                    viewHolder.user_photo.setAlpha(0.95f);
                    viewHolder.relativeLayout.animate().scaleX(0.95f).scaleY(0.95f).setDuration(0);

                } else if (event.getAction() == MotionEvent.ACTION_UP) {




                            viewHolder.user_photo.animate().alpha(1.0f).setDuration(100);

                    viewHolder.relativeLayout.animate().scaleX(1.0f).scaleY(1.0f).setDuration(100);

                }
                else
                {

                            viewHolder.user_photo.animate().setDuration(100).alpha(1.0f);
                    viewHolder.relativeLayout.animate().scaleX(1.0f).scaleY(1.0f).setDuration(100);


                }

                return false;
            }

        });*/





        try {
            if (user.getPhoto().equals("default")) {

                viewHolder.user_photo.setImageResource(R.drawable.no_profile);
                //viewHolder.progressBar.setVisibility(View.INVISIBLE);

                viewHolder.progressBarOne.setVisibility(View.INVISIBLE);
                viewHolder.progressBarTwo.setVisibility(View.INVISIBLE);

            } else {

                //  Bitmap bitmap = getBitmapFromURL(chatStamp.getPhoto());

                RequestOptions requestOptions = new RequestOptions();
                requestOptions.placeholder(R.color.darkButtonBackground);
                //requestOptions.centerCrop();
                // requestOptions.override(200, 400);




                // Glide.with(mContext).setDefaultRequestOptions(requestOptions).load(chatStamp.getPhoto()).transition(withCrossFade()).into(viewHolder.user_photo);
                try {
                    Glide.with(mContext).setDefaultRequestOptions(requestOptions).load(user.getPhoto().replace("s96-c", "s384-c")).listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {

                            // viewHolder.progressBar.setVisibility(View.INVISIBLE);

                            final Matrix matrix = viewHolder.user_photo.getImageMatrix();
                            final float imageWidth = resource.getIntrinsicWidth();
                            final int screenWidth = mContext.getResources().getDisplayMetrics().widthPixels / 2;
                            final float scaleRatio = screenWidth / imageWidth;
                            matrix.postScale(scaleRatio, scaleRatio);

                            viewHolder.progressBarOne.setVisibility(View.INVISIBLE);
                            viewHolder.progressBarTwo.setVisibility(View.INVISIBLE);
                            return false;
                        }
                    }).transition(withCrossFade()).into(viewHolder.user_photo);
                }catch (Exception e)
                {

                    viewHolder.user_photo.setImageDrawable(mContext.getDrawable(R.drawable.no_profile));
                }
            }
        } catch (Exception e) {
            Log.e("Null pointer exp", "Internet issue");
        }

        viewHolder.user_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


              /*  viewHolder.progressBarOne.setVisibility(View.VISIBLE);
                viewHolder.progressBarTwo.setVisibility(View.VISIBLE);*/
                viewHolder.user_photo.setAlpha(0.5f);
                viewHolder.relativeLayout.animate().scaleX(0.95f).scaleY(0.95f).setDuration(0);

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(mContext, VerticalPageActivity.class);
                        intent.putExtra("username", user.getUsername());
                        intent.putExtra("userid", user.getUserid());
                        intent.putExtra("photo", user.getPhoto());
                        intent.putExtra("live", "no");
                        mContext.startActivity(intent);
                        /*viewHolder.progressBarOne.setVisibility(View.INVISIBLE);
                        viewHolder.progressBarTwo.setVisibility(View.INVISIBLE);*/
                        viewHolder.user_photo.animate().setDuration(100).alpha(1.0f);
                        viewHolder.relativeLayout.animate().scaleX(1.0f).scaleY(1.0f).setDuration(100);


                    }
                }, 50);

            }
        });

    }




    @Override
    public int getItemCount() {
        return mUsers.size();
    }


    public void setList(List<User> mUsers)
    {


        this.mUsers = mUsers;
        notifyDataSetChanged();



    }


    public class ViewHolder extends RecyclerView.ViewHolder{


        private TextView username, count_message_text;
        // public CircleImageView user_photo;
        private RelativeLayout counterBackgroundLayout;
        private ProgressBar progressBar, progressBarOne, progressBarTwo;
        private ImageView user_photo;
        private RelativeLayout relativeLayout, chatbox_tem_Layout, count_message_layout;
        private Button archiveBtn, delete_chat_head_btn;
        private ImageButton longMsgBtn;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            progressBar = itemView.findViewById(R.id.loading_image_progress_bar);
            progressBarOne = itemView.findViewById(R.id.progress_one);
            progressBarTwo = itemView.findViewById(R.id.progress_two);
            username = itemView.findViewById(R.id.username);
            user_photo = itemView.findViewById(R.id.user_photo);
            relativeLayout = itemView.findViewById(R.id.chat_back_parent_layout);

            //  counterBackgroundLayout = itemView.findViewById(R.id.message_count_back_chatlist);

            count_message_layout = itemView.findViewById(R.id.count_message_layout);
            count_message_text = itemView.findViewById(R.id.count_message_text);
            chatbox_tem_Layout = itemView.findViewById(R.id.chatbox_tem);
            archiveBtn = itemView.findViewById(R.id.archive_btn);
            longMsgBtn = itemView.findViewById(R.id.long_msg_btn);
            delete_chat_head_btn = itemView.findViewById(R.id.delete_chat_head_btn);


        }

    }


}

