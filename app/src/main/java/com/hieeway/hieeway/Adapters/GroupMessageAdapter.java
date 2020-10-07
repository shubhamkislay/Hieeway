package com.hieeway.hieeway.Adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hieeway.hieeway.CustomImageView;
import com.hieeway.hieeway.FriendListFragmentViewModel;
import com.hieeway.hieeway.Interface.ScrollRecyclerViewListener;
import com.hieeway.hieeway.Interface.SpotifyRemoteConnectListener;
import com.hieeway.hieeway.Model.ChatStamp;
import com.hieeway.hieeway.Model.Friend;
import com.hieeway.hieeway.Model.GroupMessage;
import com.hieeway.hieeway.Model.Music;
import com.hieeway.hieeway.Model.MusicMessage;
import com.hieeway.hieeway.R;
import com.hieeway.hieeway.Utils.ChatStampListDiffUtilCallback;
import com.hieeway.hieeway.Utils.FriendListDiffUtilCallback;
import com.hieeway.hieeway.Utils.GroupMessageListDiffUtilCallback;
import com.spotify.android.appremote.api.SpotifyAppRemote;
import com.spotify.protocol.client.CallResult;
import com.spotify.protocol.types.Empty;

import org.ocpsoft.prettytime.PrettyTime;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.content.Context.MODE_PRIVATE;

public class GroupMessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    private static final int MESSAGE_SENDING = 0;
    private static final int MESSAGE_SENT = 1;
    private static final int MESSAGE_RECEIVED = 2;
    private static final int SONG_SENT = 3;
    private static final int SONG_RECEIVED = 4;
    public String userID;
    private Context context;
    private List<GroupMessage> groupMessageList = new ArrayList<>();
    private ScrollRecyclerViewListener scrollRecyclerViewListener;
    private Activity activity;
    private String groupID;
    private SpotifyAppRemote appRemote;
    private SpotifyRemoteConnectListener spotifyRemoteConnectListener;

    public GroupMessageAdapter(Context context, List<GroupMessage> groupMessageList, String currentUserId, ScrollRecyclerViewListener scrollRecyclerViewListener, String groupID, SpotifyAppRemote appRemote, Activity activity) {
        this.context = context;
        this.groupMessageList.addAll(groupMessageList);
        this.userID = currentUserId;
        this.scrollRecyclerViewListener = scrollRecyclerViewListener;
        this.activity = (Activity) context;
        this.groupID = groupID;
        this.appRemote = appRemote;
        this.spotifyRemoteConnectListener = (SpotifyRemoteConnectListener) activity;
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

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
        } else if (viewType == MESSAGE_RECEIVED) {
            View view = LayoutInflater.from(context).inflate(R.layout.group_message_receive_layout, parent, false);

            /*TextView relative_layout = view.findViewById(R.id.message_view);
            int height = relative_layout.getHeight();
            CustomImageView user_photo = view.findViewById(R.id.user_photo);
            user_photo.getLayoutParams().width = (int) height;
            user_photo.getLayoutParams().height = (int) height/2;*/

            return new GroupMessageAdapter.GroupMessageViewHolder(view);
        } else if (viewType == SONG_SENT) {
            View view = LayoutInflater.from(context).inflate(R.layout.group_message_spotify_item_sent, parent, false);

            /*TextView relative_layout = view.findViewById(R.id.message_view);
            int height = relative_layout.getHeight();
            CustomImageView user_photo = view.findViewById(R.id.user_photo);
            user_photo.getLayoutParams().width = (int) height;
            user_photo.getLayoutParams().height = (int) height/2;*/

            return new GroupMessageAdapter.GroupSongViewHolder(view);
        } else if (viewType == SONG_RECEIVED) {
            View view = LayoutInflater.from(context).inflate(R.layout.group_message_spotify_item_receive, parent, false);

            /*TextView relative_layout = view.findViewById(R.id.message_view);
            int height = relative_layout.getHeight();
            CustomImageView user_photo = view.findViewById(R.id.user_photo);
            user_photo.getLayoutParams().width = (int) height;
            user_photo.getLayoutParams().height = (int) height/2;*/

            return new GroupMessageAdapter.GroupSongViewHolder(view);
        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.group_message_sending_layout, parent, false);

            /*TextView relative_layout = view.findViewById(R.id.message_view);
            int height = relative_layout.getHeight();
            CustomImageView user_photo = view.findViewById(R.id.user_photo);
            user_photo.getLayoutParams().height = (int) height;
            user_photo.getLayoutParams().width = (int) height/2;*/

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

        if (groupMessage.getType().equals("song")) {
            if (groupMessage.getSenderId().equals(userID))
                return SONG_SENT;
            else
                return SONG_RECEIVED;

        } else {
            if (groupMessage.getSenderId().equals(userID)) {
                if (groupMessage.getSentStatus().equals("sending"))
                    return MESSAGE_SENDING;
                else
                    return MESSAGE_SENT;
            } else
                return MESSAGE_RECEIVED;
        }
    }

    public void updateList(List<GroupMessage> newListChatStamp) {


        GroupMessageListDiffUtilCallback chatStampListDiffUtilCallback = new GroupMessageListDiffUtilCallback(this.groupMessageList, newListChatStamp);
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(chatStampListDiffUtilCallback);

        groupMessageList.clear();
        groupMessageList.addAll(newListChatStamp);

        diffResult.dispatchUpdatesTo(this);


    }

    @Override
    public void onViewDetachedFromWindow(@NonNull RecyclerView.ViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
        if (holder.getAdapterPosition() == groupMessageList.size() - 1)
            scrollRecyclerViewListener.scrollViewToLastItem(false);

    }

    @Override
    public void onViewAttachedToWindow(@NonNull RecyclerView.ViewHolder holder) {
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

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        if (groupMessageList.get(holder.getAdapterPosition()).getType().equals("text")) {

            GroupMessageViewHolder viewHolder = (GroupMessageViewHolder) holder;
            GroupMessage groupMessage = groupMessageList.get(viewHolder.getAdapterPosition());

            if (groupMessage.getSenderId().equals(userID))
                viewHolder.username.setVisibility(View.GONE);
            else
                viewHolder.username.setText(groupMessage.getUsername());


            try {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS");
                Date parsedDate = dateFormat.parse(groupMessage.getTimeStamp());


                Timestamp timestamp = new java.sql.Timestamp(parsedDate.getTime());

                PrettyTime prettyTime = new PrettyTime(Locale.getDefault());
                String ago = prettyTime.format(parsedDate);
                // S is the millisecond
                // SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy' 'HH:mm:ss:S");
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");/*  dd MMM YYYY*/


                viewHolder.timestamp.setText("" + simpleDateFormat.format(timestamp));

                //holder.timestamp.setText(""+height);


                //sendMessageViewHolder.timestamp.setText("" + ago);

            } catch (Exception e) { //this generic but you can control another types of exception
                // look the origin of excption
                viewHolder.timestamp.setVisibility(View.INVISIBLE);

            }


            RequestOptions requestOptions = new RequestOptions();
            //requestOptions.override(100, 100);
            requestOptions.placeholder(context.getDrawable(R.drawable.no_profile));
            //requestOptions.circleCrop();
            //requestOptions.centerCrop();
            //requestOptions.diskCacheStrategy(DiskCacheStrategy.ALL);

            viewHolder.message_view.setText(groupMessage.getMessageText());

            if (!groupMessage.getPhoto().equals("default"))
                Glide.with(context).load(groupMessage.getPhoto()).apply(requestOptions).addListener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {

                        int height = viewHolder.message_view.getHeight();
                        viewHolder.user_photo.getLayoutParams().width = (int) height / 2;

                        viewHolder.user_photo.setScaleType(ImageView.ScaleType.CENTER_CROP);

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
                }).into(viewHolder.user_photo);
            else
                Glide.with(context).load(activity.getDrawable(R.drawable.no_profile)).apply(requestOptions).into(viewHolder.user_photo);


        } else if (groupMessageList.get(holder.getAdapterPosition()).getType().equals("song")) {


            GroupSongViewHolder groupSongViewHolder = (GroupSongViewHolder) holder;
            GroupMessage groupMessage = groupMessageList.get(groupSongViewHolder.getAdapterPosition());

            groupSongViewHolder.song_name.setVisibility(View.GONE);
            groupSongViewHolder.spinkit_wave.setVisibility(View.VISIBLE);
            groupSongViewHolder.fetch_music_txt.setVisibility(View.VISIBLE);
            groupSongViewHolder.artist_name.setVisibility(View.GONE);
            groupSongViewHolder.spotify_back.setVisibility(View.GONE);
            groupSongViewHolder.spotify_icon.setVisibility(View.GONE);
            groupSongViewHolder.open_spotify_text.setVisibility(View.GONE);
            groupSongViewHolder.song_art.setVisibility(View.INVISIBLE);

            if (groupMessage.getSenderId().equals(userID))
                groupSongViewHolder.username.setText("You played this song");
            else
                groupSongViewHolder.username.setText(groupMessage.getUsername() + " played the song");


            try {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS");
                Date parsedDate = dateFormat.parse(groupMessage.getTimeStamp());


                Timestamp timestamp = new java.sql.Timestamp(parsedDate.getTime());

                PrettyTime prettyTime = new PrettyTime(Locale.getDefault());
                String ago = prettyTime.format(parsedDate);
                // S is the millisecond
                // SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy' 'HH:mm:ss:S");
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");/*  dd MMM YYYY*/


                groupSongViewHolder.timestamp.setText("" + simpleDateFormat.format(timestamp));

                //holder.timestamp.setText(""+height);


                //sendMessageViewHolder.timestamp.setText("" + ago);

                fetchSong(groupMessage,
                        groupSongViewHolder.song_name,
                        groupSongViewHolder.artist_name,
                        groupSongViewHolder.song_art,
                        groupSongViewHolder.spinkit_wave,
                        groupSongViewHolder.fetch_music_txt,
                        groupSongViewHolder.spotify_back,
                        groupSongViewHolder.spotify_icon,
                        groupSongViewHolder.open_spotify_text);

            } catch (Exception e) {
                groupSongViewHolder.timestamp.setVisibility(View.INVISIBLE);

            }
        }


    }

    private void fetchSong(GroupMessage groupMessage,
                           TextView song_name,
                           TextView artist_name,
                           ImageView song_art,
                           ProgressBar spinkit_wave,
                           TextView fetch_music_txt,
                           ImageView spotify_back,
                           Button spotify_icon,
                           TextView open_spotify_text) {
        FirebaseDatabase.getInstance().getReference("MusicMessage")
                .child(groupID)
                .child(groupMessage.getMediaID())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {

                            MusicMessage music = snapshot.getValue(MusicMessage.class);
                            song_name.setText(music.getSpotifySong());
                            artist_name.setText(music.getSpotifyArtist());

/*
                           appRemote.getImagesApi().getImage(music.getSpotifyCover()).setResultCallback(new CallResult.ResultCallback<Bitmap>() {
                               @Override
                               public void onResult(Bitmap bitmap) {

                                   song_art.setImageBitmap(bitmap);

                               }
                           });
*/
                            Glide.with(context).load(music.getSpotifyCover()).into(song_art);

                            song_name.setVisibility(View.VISIBLE);
                            spinkit_wave.setVisibility(View.GONE);
                            fetch_music_txt.setVisibility(View.GONE);
                            artist_name.setVisibility(View.VISIBLE);
                            spotify_back.setVisibility(View.VISIBLE);
                            spotify_icon.setVisibility(View.VISIBLE);
                            open_spotify_text.setVisibility(View.VISIBLE);
                            song_art.setVisibility(View.VISIBLE);

                            song_art.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    try {
                                        appRemote.getPlayerApi().play(music.getSpotifyId())
                                                .setResultCallback(new CallResult.ResultCallback<Empty>() {
                                                    @Override
                                                    public void onResult(Empty empty) {
                                                        Toast.makeText(context, "Playing " + music.getSpotifySong(), Toast.LENGTH_SHORT).show();
                                                    }

                                                });
                                    } catch (Exception e) {
                                        Toast.makeText(context, "Connecting to spotify ", Toast.LENGTH_SHORT).show();
                                        spotifyRemoteConnectListener.getSpotifyAppRemote(music.getSpotifyId(), music.getSpotifySong());
                                    }

                                }
                            });
                            song_art.setOnTouchListener(new View.OnTouchListener() {
                                @SuppressLint("ClickableViewAccessibility")
                                @Override
                                public boolean onTouch(View v, MotionEvent event) {

                                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                                        song_art.animate().scaleX(0.95f).scaleY(0.95f).setDuration(0);
                                    } else if (event.getAction() == MotionEvent.ACTION_UP) {
                                        song_art.animate().scaleX(1.0f).scaleY(1.0f).setDuration(100);
                                    } else {
                                        song_art.animate().scaleX(1.0f).scaleY(1.0f).setDuration(100);
                                    }
                                    return false;
                                }
                            });


                        } else {
                            song_name.setVisibility(View.GONE);
                            spinkit_wave.setVisibility(View.VISIBLE);
                            fetch_music_txt.setVisibility(View.VISIBLE);
                            artist_name.setVisibility(View.GONE);
                            spotify_back.setVisibility(View.GONE);
                            spotify_icon.setVisibility(View.GONE);
                            open_spotify_text.setVisibility(View.GONE);
                            song_art.setVisibility(View.INVISIBLE);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        song_name.setVisibility(View.GONE);
                        spinkit_wave.setVisibility(View.VISIBLE);
                        fetch_music_txt.setVisibility(View.VISIBLE);
                        artist_name.setVisibility(View.GONE);
                        spotify_back.setVisibility(View.GONE);
                        spotify_icon.setVisibility(View.GONE);
                        open_spotify_text.setVisibility(View.GONE);
                        song_art.setVisibility(View.INVISIBLE);
                    }
                });
    }

    public static float dpFromPx(final Context context, final float px) {
        return px / context.getResources().getDisplayMetrics().density;
    }

    public void setAppRemote(SpotifyAppRemote appRemote) {
        this.appRemote = appRemote;
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

    public class GroupSongViewHolder extends RecyclerView.ViewHolder {

        CircleImageView user_photo;
        TextView timestamp, username;
        TextView song_name, artist_name;
        ImageView song_art, spotify_back;
        ProgressBar spinkit_wave;
        Button spotify_icon;
        TextView fetch_music_txt, open_spotify_text;


        public GroupSongViewHolder(@NonNull View itemView) {
            super(itemView);

            user_photo = itemView.findViewById(R.id.user_photo);
            timestamp = itemView.findViewById(R.id.timestamp);
            username = itemView.findViewById(R.id.username);
            fetch_music_txt = itemView.findViewById(R.id.fetch_music_txt);
            song_art = itemView.findViewById(R.id.song_art);
            song_name = itemView.findViewById(R.id.song_name);
            artist_name = itemView.findViewById(R.id.artist_name);
            spinkit_wave = itemView.findViewById(R.id.spinkit_wave);
            open_spotify_text = itemView.findViewById(R.id.open_spotify_text);
            spotify_back = itemView.findViewById(R.id.spotify_back);
            spotify_icon = itemView.findViewById(R.id.spotify_icon);


        }
    }
}
