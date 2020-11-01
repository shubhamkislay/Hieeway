package com.hieeway.hieeway.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.Group;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Handler;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.hieeway.hieeway.AddGroupDetailsActivity;
import com.hieeway.hieeway.CustomCircularView;
import com.hieeway.hieeway.GroupChatActivity;
import com.hieeway.hieeway.Interface.SeeAllGroupItemsListener;
import com.hieeway.hieeway.LeaveGroupDialog;
import com.hieeway.hieeway.MusicPalActivity;
import com.hieeway.hieeway.RemoveFriendDialog;
import com.hieeway.hieeway.Utils.MyGroupListDiffUtilCallBack;
import com.hieeway.hieeway.Model.MyGroup;
import com.hieeway.hieeway.R;
import com.hieeway.hieeway.YoutubeLiveVideoSelectionActivity;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.sql.Timestamp;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class GroupsAdapter extends RecyclerView.Adapter<GroupsAdapter.MyViewHolder> {

    private static final int GROUP_ITEM = 3;
    private static final int CREATE_GROUP_ITEM = 0;
    private static final int START_MUSIC_ITEM = 2;
    private static final int START_VIDEO_ITEM = 1;
    private List<MyGroup> myGroupList;
    private List<MyGroup> refreshedList;
    private Context context;
    private int selectedIndex = 0;
    private Boolean seeAllBtn = false;
    private SeeAllGroupItemsListener seeAllGroupItemsListener;
    private SharedPreferences sharedPreferences;
    public static final String SHARED_PREFS = "sharedPrefs";
    private int currentViewCount = 0;
    private String userID;
    ImageLoader imageLoader = ImageLoader.getInstance();
    ImageSize targetSize = new ImageSize(64, 64);
    DisplayImageOptions options = new DisplayImageOptions.Builder()
            .showImageOnLoading(R.drawable.no_profile) // resource or drawable// resource or drawable
            .showImageOnFail(R.drawable.no_profile) // resource or drawable
            .resetViewBeforeLoading(false)  // default
            .delayBeforeLoading(500)
            //.cacheInMemory(false) // default
            .cacheOnDisk(true) // default
            //.considerExifParams(false) // default
            .imageScaleType(ImageScaleType.IN_SAMPLE_POWER_OF_2) // default
            .bitmapConfig(Bitmap.Config.ARGB_8888) // default
            .displayer(new SimpleBitmapDisplayer()) // default
            .handler(new Handler()) // default
            .build();


    public GroupsAdapter(List<MyGroup> myGroupList, Context context, SeeAllGroupItemsListener seeAllGroupItemsListener, String userID) {
        this.myGroupList = myGroupList;

        this.context = context;
        this.seeAllGroupItemsListener = seeAllGroupItemsListener;

        imageLoader.init(ImageLoaderConfiguration.createDefault(context));
        sharedPreferences = context.getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        this.userID = userID;

        //refreshedList = new ArrayList<>();
        /*
        MyGroup friend1 = new MyGroup();
        MyGroup friend2 = new MyGroup();
        MyGroup friend3 = new MyGroup();
        refreshedList.add(friend1);
        refreshedList.add(friend2);
        refreshedList.add(friend3);
        refreshedList.addAll(myGroupList);*/

        /*
        MyGroup seeAllItem = new MyGroup();

        seeAllItem.setGroupID("123");
        seeAllItem.setGroupName("234");
        seeAllItem.setIcon("345");

        if(myGroupList.size()>3&&myGroupList.size()<7)
            myGroupList.add(seeAllItem);
        else if(myGroupList.size()>=7)
            myGroupList.add(6,seeAllItem);
        */

    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int position) {

        if (position == CREATE_GROUP_ITEM) {
            View itemView = LayoutInflater.from(context).inflate(R.layout.create_group_item, parent, false);


            return new MyViewHolder(itemView);
        } else if (position == START_VIDEO_ITEM) {
            View itemView = LayoutInflater.from(context).inflate(R.layout.new_watch_party_item, parent, false);


            return new MyViewHolder(itemView);
        } else if (position == START_MUSIC_ITEM) {
            View itemView = LayoutInflater.from(context).inflate(R.layout.pair_music_item, parent, false);


            return new MyViewHolder(itemView);
        } else {
            View itemView = LayoutInflater.from(context).inflate(R.layout.group_item, parent, false);


            return new MyViewHolder(itemView);
        }

    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, final int position) {

        myViewHolder.prof_pic.setOnTouchListener(new View.OnTouchListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    myViewHolder.prof_pic.animate().scaleX(0.9f).scaleY(0.9f).setDuration(0);
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    myViewHolder.prof_pic.animate().scaleX(1.0f).scaleY(1.0f).setDuration(100);
                } else {
                    myViewHolder.prof_pic.animate().scaleX(1.0f).scaleY(1.0f).setDuration(100);
                }
                return false;
            }
        });

        if (myViewHolder.getAdapterPosition() > 2) {
            MyGroup myGroup = myGroupList.get(myViewHolder.getAdapterPosition());


            /**
             * Uncomment below code to implement group image loading through Glide
             */

            /*if (!myGroup.getIcon().equals("default")) {

                RequestOptions requestOptions = new RequestOptions();
                requestOptions.override(100, 100);
                requestOptions.placeholder(context.getDrawable(R.drawable.no_profile));
                requestOptions.circleCrop();
                requestOptions.diskCacheStrategy(DiskCacheStrategy.ALL);   //If your images are always same
                requestOptions.format(DecodeFormat.PREFER_RGB_565);

                Glide.with(context).load(myGroup.getIcon()).apply(requestOptions).addListener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {

                        myViewHolder.one.setVisibility(View.GONE);
                        myViewHolder.two.setVisibility(View.GONE);

                        return false;
                    }
                }).into(myViewHolder.prof_pic);
            } else {
                Glide.with(context).load(context.getDrawable(R.drawable.no_profile)).into(myViewHolder.prof_pic);
                myViewHolder.one.setVisibility(View.GONE);
                myViewHolder.two.setVisibility(View.GONE);
            }*/
            if (!myGroup.getIcon().equals("default")) {
                /*imageLoader.loadImage(myGroup.getIcon(), targetSize, new SimpleImageLoadingListener() {
                    @Override
                    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                        //super.onLoadingComplete(imageUri, view, loadedImage);
                        myViewHolder.prof_pic.setImageBitmap(loadedImage);

                        //imageLoader.


                    }
                });*/

                ImageLoader.getInstance().displayImage(myGroup.getIcon(), myViewHolder.prof_pic, options);
            } else
                imageLoader.displayImage("drawable://" + R.drawable.groups_image, myViewHolder.prof_pic);
            myViewHolder.username.setText(myGroup.getGroupName());

            myViewHolder.prof_pic.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Toast.makeText(context, "Opening Group chat...", Toast.LENGTH_SHORT).show();
                    myViewHolder.back_item.setVisibility(View.INVISIBLE);

                    Intent intent = new Intent(context, GroupChatActivity.class);
                    intent.putExtra("groupID", myGroup.getGroupID());
                    intent.putExtra("groupName", myGroup.getGroupName());
                    intent.putExtra("icon", myGroup.getIcon());

                    context.startActivity(intent);
                }
            });

            myViewHolder.prof_pic.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {

                    Vibrator vb = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
// Vibrate for 500 milliseconds
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        vb.vibrate(VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE));
                    } else {
                        //deprecated in API 26
                        vb.vibrate(50);
                    }

                    LeaveGroupDialog leaveGroupDialog = new LeaveGroupDialog(context, myGroup.getGroupID(), userID);
                    leaveGroupDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    leaveGroupDialog.show();

                    return true;
                }
            });

            MyGroup group = new MyGroup();
            group.setIcon("asdf");
            group.setGroupName("fasdf");
            group.setGroupID("fasdf");
            group.setTimeStamp(sharedPreferences.getString(myGroup.getGroupID(), ""));


            if (myGroup.compareTo(group) >= 0) {
                //Toast.makeText(context,"Visible",Toast.LENGTH_SHORT).show();
                if (!myGroup.getSender().equals(userID))
                    myViewHolder.back_item.setVisibility(View.VISIBLE);
            } else {
                //Toast.makeText(context,"Invisible",Toast.LENGTH_SHORT).show();
                myViewHolder.back_item.setVisibility(View.INVISIBLE);
            }


        } else {
            if (myViewHolder.getAdapterPosition() == 0) {
                myViewHolder.prof_pic.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        context.startActivity(new Intent(context, AddGroupDetailsActivity.class));
                    }
                });

            } else if (myViewHolder.getAdapterPosition() == 1) {
                myViewHolder.prof_pic.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        context.startActivity(new Intent(context, YoutubeLiveVideoSelectionActivity.class));
                    }
                });

            } else if (myViewHolder.getAdapterPosition() == 2) {
                myViewHolder.prof_pic.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        context.startActivity(new Intent(context, MusicPalActivity.class));
                    }
                });

            }
        }


    }

    @Override
    public int getItemCount() {

        /*if(myGroupList.size()>3&&myGroupList.size()<7)
            return myGroupList.size();
        else if(myGroupList.size()>=7)
            return 7;
        else*/
        return myGroupList.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (position > 2)
            return GROUP_ITEM;
        else {
            if (position == 0)
                return CREATE_GROUP_ITEM;
            else if (position == 1)
                return START_VIDEO_ITEM;
            else
                return START_MUSIC_ITEM;
        }

    }

    public void updateList(List<MyGroup> newListChatStamp) {


        MyGroupListDiffUtilCallBack myGroupListDiffUtilCallBack = new MyGroupListDiffUtilCallBack(this.myGroupList, newListChatStamp);
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(myGroupListDiffUtilCallBack);

        myGroupList.clear();
        myGroupList.addAll(newListChatStamp);


        /*        refreshedList.clear();
        MyGroup friend1 = new MyGroup();
        MyGroup friend2 = new MyGroup();
        MyGroup friend3 = new MyGroup();
        refreshedList.add(friend1);
        refreshedList.add(friend2);
        refreshedList.add(friend3);
        refreshedList.addAll(myGroupList);*/


        diffResult.dispatchUpdatesTo(this);


    }

    @Override
    public void onViewAttachedToWindow(@NonNull MyViewHolder holder) {
        super.onViewAttachedToWindow(holder);

        if (holder.getAdapterPosition() == 2)
            if (seeAllBtn) {
                /*seeAllGroupItemsListener.seeAllBtnVisibility(View.GONE);
                seeAllBtn = false;*/
            }

    }

    @Override
    public void onViewDetachedFromWindow(@NonNull MyViewHolder holder) {
        super.onViewDetachedFromWindow(holder);


        if (holder.getAdapterPosition() == 2)
            if (!seeAllBtn) {
                /*seeAllGroupItemsListener.seeAllBtnVisibility(View.VISIBLE);
                seeAllBtn = true;*/
            }


    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        CustomCircularView prof_pic;
        TextView username;
        RelativeLayout back_item;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            prof_pic = itemView.findViewById(R.id.user_photo);
            username = itemView.findViewById(R.id.username);
            back_item = itemView.findViewById(R.id.back_item);


        }
    }
}
