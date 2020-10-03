package com.hieeway.hieeway.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.hieeway.hieeway.CustomImageView;
import com.hieeway.hieeway.FriendListFragmentViewModel;
import com.hieeway.hieeway.Interface.ScrollRecyclerViewListener;
import com.hieeway.hieeway.Model.ChatStamp;
import com.hieeway.hieeway.Model.Friend;
import com.hieeway.hieeway.Model.GroupMessage;
import com.hieeway.hieeway.R;
import com.hieeway.hieeway.Utils.ChatStampListDiffUtilCallback;
import com.hieeway.hieeway.Utils.FriendListDiffUtilCallback;
import com.hieeway.hieeway.Utils.GroupMessageListDiffUtilCallback;

import org.ocpsoft.prettytime.PrettyTime;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static android.content.Context.MODE_PRIVATE;

public class GroupMessageAdapter extends RecyclerView.Adapter<GroupMessageAdapter.GroupMessageViewHolder> {


    private static final int MESSAGE_SENDING = 0;
    private static final int MESSAGE_SENT = 1;
    private static final int MESSAGE_RECEIVED = 2;
    public String userID;
    private Context context;
    private List<GroupMessage> groupMessageList = new ArrayList<>();
    private ScrollRecyclerViewListener scrollRecyclerViewListener;
    private Activity activity;

    public GroupMessageAdapter(Context context, List<GroupMessage> groupMessageList, String currentUserId, ScrollRecyclerViewListener scrollRecyclerViewListener) {
        this.context = context;
        this.groupMessageList.addAll(groupMessageList);
        this.userID = currentUserId;
        this.scrollRecyclerViewListener = scrollRecyclerViewListener;
        this.activity = (Activity) context;
    }


    @NonNull
    @Override
    public GroupMessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if (viewType == MESSAGE_SENDING) {

            View view = LayoutInflater.from(context).inflate(R.layout.group_message_sending_layout, parent, false);

            /*TextView relative_layout = view.findViewById(R.id.message_view);
            int height = relative_layout.getHeight();
            CustomImageView user_photo = view.findViewById(R.id.user_photo);
            user_photo.getLayoutParams().height = (int) height;
            user_photo.getLayoutParams().width = (int) height/2;*/

            return new GroupMessageAdapter.GroupMessageViewHolder(view);
        } else if (viewType == MESSAGE_SENT) {
            View view = LayoutInflater.from(context).inflate(R.layout.group_message_sent_layout, parent, false);

            /*TextView relative_layout = view.findViewById(R.id.message_view);
            int height = relative_layout.getHeight();
            CustomImageView user_photo = view.findViewById(R.id.user_photo);
            user_photo.getLayoutParams().height = (int) height;
            user_photo.getLayoutParams().width = (int) height/2;*/

            return new GroupMessageAdapter.GroupMessageViewHolder(view);
        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.group_message_receive_layout, parent, false);

            /*TextView relative_layout = view.findViewById(R.id.message_view);
            int height = relative_layout.getHeight();
            CustomImageView user_photo = view.findViewById(R.id.user_photo);
            user_photo.getLayoutParams().width = (int) height;
            user_photo.getLayoutParams().height = (int) height/2;*/

            return new GroupMessageAdapter.GroupMessageViewHolder(view);
        }
    }

    public static float pxFromDp(final Context context, final float dp) {
        return dp * context.getResources().getDisplayMetrics().density;
    }

    @Override
    public int getItemCount() {
        return groupMessageList.size();
    }

    @Override
    public int getItemViewType(int position) {
        GroupMessage groupMessage = groupMessageList.get(position);
        if (groupMessage.getSenderId().equals(userID)) {
            if (groupMessage.getSentStatus().equals("sending"))
                return MESSAGE_SENDING;
            else
                return MESSAGE_SENT;
        } else
            return MESSAGE_RECEIVED;
    }

    public void updateList(List<GroupMessage> newListChatStamp) {


        GroupMessageListDiffUtilCallback chatStampListDiffUtilCallback = new GroupMessageListDiffUtilCallback(this.groupMessageList, newListChatStamp);
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(chatStampListDiffUtilCallback);

        groupMessageList.clear();
        groupMessageList.addAll(newListChatStamp);

        diffResult.dispatchUpdatesTo(this);


    }

    @Override
    public void onViewDetachedFromWindow(@NonNull GroupMessageViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
        if (holder.getAdapterPosition() == groupMessageList.size() - 1)
            scrollRecyclerViewListener.scrollViewToLastItem(false);

    }

    @Override
    public void onViewAttachedToWindow(@NonNull GroupMessageViewHolder holder) {
        super.onViewAttachedToWindow(holder);
        if (holder.getAdapterPosition() == groupMessageList.size() - 1)
            scrollRecyclerViewListener.scrollViewToLastItem(true);

        /*GroupMessage groupMessage = groupMessageList.get(holder.getAdapterPosition());
        int height = holder.message_view.getHeight();

        holder.user_photo.getLayoutParams().height = (int) height;
        holder.user_photo.getLayoutParams().width = (int) height/2;

        RequestOptions requestOptions = new RequestOptions();
        requestOptions.placeholder(context.getDrawable(R.drawable.no_profile));
        requestOptions.centerCrop();
        requestOptions.diskCacheStrategy(DiskCacheStrategy.ALL);


        Glide.with(context).load(groupMessage.getPhoto()).apply(requestOptions).into(holder.user_photo);*/


    }

    public class GroupMessageViewHolder extends RecyclerView.ViewHolder {

        TextView message_view;
        ImageView user_photo;
        TextView timestamp, username;


        public GroupMessageViewHolder(@NonNull View itemView) {
            super(itemView);
            message_view = itemView.findViewById(R.id.message_view);
            user_photo = itemView.findViewById(R.id.user_photo);
            timestamp = itemView.findViewById(R.id.timestamp);
            username = itemView.findViewById(R.id.username);

        }
    }

    public static float dpFromPx(final Context context, final float px) {
        return px / context.getResources().getDisplayMetrics().density;
    }

    @Override
    public void onBindViewHolder(@NonNull GroupMessageViewHolder holder, int position) {


        GroupMessage groupMessage = groupMessageList.get(holder.getAdapterPosition());

        holder.message_view.setText(groupMessage.getMessageText());

        if (groupMessage.getSenderId().equals(userID))
            holder.username.setVisibility(View.GONE);
        else
            holder.username.setText(groupMessage.getUsername());

        RequestOptions requestOptions = new RequestOptions();
        //requestOptions.override(100, 100);
        requestOptions.placeholder(context.getDrawable(R.drawable.no_profile));
        //requestOptions.circleCrop();
        //requestOptions.centerCrop();
        //requestOptions.diskCacheStrategy(DiskCacheStrategy.ALL);


        if (!groupMessage.getPhoto().equals("default"))
            Glide.with(context).load(groupMessage.getPhoto()).apply(requestOptions).addListener(new RequestListener<Drawable>() {
                @Override
                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                    return false;
                }

                @Override
                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {

                    int height = holder.message_view.getHeight();
                    holder.user_photo.getLayoutParams().width = (int) height / 2;

                    holder.user_photo.setScaleType(ImageView.ScaleType.CENTER_CROP);

                    /*
                    if (height > pxFromDp(context,100) ) {
                        if((float)height / 2 < pxFromDp(context,340) )
                            holder.user_photo.getLayoutParams().width = (int) height / 2;
                        else
                        {
                            holder.user_photo.getLayoutParams().width = (int) pxFromDp(context,340)  / 2;
                        }


                    }

*/

                    return false;
                }
            }).into(holder.user_photo);
        else
            Glide.with(context).load(activity.getDrawable(R.drawable.no_profile)).apply(requestOptions).into(holder.user_photo);


        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS");
            Date parsedDate = dateFormat.parse(groupMessage.getTimeStamp());


            Timestamp timestamp = new java.sql.Timestamp(parsedDate.getTime());

            PrettyTime prettyTime = new PrettyTime(Locale.getDefault());
            String ago = prettyTime.format(parsedDate);
            // S is the millisecond
            // SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy' 'HH:mm:ss:S");
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");/*  dd MMM YYYY*/


            holder.timestamp.setText("" + simpleDateFormat.format(timestamp));

            //holder.timestamp.setText(""+height);


            //sendMessageViewHolder.timestamp.setText("" + ago);

        } catch (Exception e) { //this generic but you can control another types of exception
            // look the origin of excption
            holder.timestamp.setVisibility(View.INVISIBLE);

        }

        /*if (holder.getAdapterPosition() == groupMessageList.size() - 1)
            holder.user_photo.setVisibility(View.VISIBLE);
        else {
            if (!groupMessageList.get(holder.getAdapterPosition()).getSenderId().equals(groupMessageList.get(holder.getAdapterPosition() + 1).getSenderId()))
                holder.user_photo.setVisibility(View.VISIBLE);
            else
                holder.user_photo.setVisibility(View.GONE);
        }*/

    }
}
