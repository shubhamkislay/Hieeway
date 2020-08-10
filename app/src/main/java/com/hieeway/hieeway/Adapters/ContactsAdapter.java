package com.hieeway.hieeway.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.hieeway.hieeway.CustomCircularView;
import com.hieeway.hieeway.Model.Friend;
import com.hieeway.hieeway.Model.User;
import com.hieeway.hieeway.R;
import com.hieeway.hieeway.VerticalPageActivity;
import com.hieeway.hieeway.ViewProfileActivity;

import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ContactsAdapter extends RecyclerView.Adapter<ContactsAdapter.ViewHolder> {

    FirebaseAuth firebaseAuth;
    String currentUsername;
    HashMap<String, Object> phoneBookNameHashMap;
    private Context mContext;
    private Activity activity;
    private List<User> mUsers;

    public ContactsAdapter(Context mContext, List<User> mUsers, Activity activity, HashMap<String, Object> phoneBookNameHashMap) {


        this.activity = activity;
        this.mUsers = mUsers;
        this.mContext = mContext;
        this.currentUsername = returnCurrentUsername();
        this.phoneBookNameHashMap = phoneBookNameHashMap;
    }

    @NonNull
    @Override
    public ContactsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.user_contacts, viewGroup, false);

        //profile_view.setTypeface(Typeface.createFromAsset(mContext.getAssets(), "fonts/samsungsharpsans-bold.otf"));

        return new ContactsAdapter.ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull final ContactsAdapter.ViewHolder viewHolder, int i) {

        final User user = mUsers.get(i);

        viewHolder.phonebook_name.setText((String) phoneBookNameHashMap.get(user.getUserid()));
        viewHolder.username.setText(user.getUsername());
        firebaseAuth = FirebaseAuth.getInstance();


        viewHolder.progressBar.setVisibility(View.VISIBLE);
        viewHolder.progressBarTwo.setVisibility(View.VISIBLE);


        try {
            if (user.getPhoto().equals("default")) {

                viewHolder.user_photo.setImageResource(R.drawable.no_profile);

            } else {

                RequestOptions requestOptions = new RequestOptions();
                /*requestOptions.placeholder(R.color.darkBack);
                requestOptions.centerCrop();*/
                requestOptions.placeholder(R.drawable.no_profile);
                try {
                    // Glide.with(mContext).setDefaultRequestOptions(requestOptions).load(user.getPhoto()).transition(withCrossFade()).into(viewHolder.user_photo);

                    Glide.with(mContext).setDefaultRequestOptions(requestOptions).load(user.getPhoto()).addListener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            viewHolder.progressBar.setVisibility(View.INVISIBLE);
                            viewHolder.progressBarTwo.setVisibility(View.INVISIBLE);

                            final Matrix matrix = viewHolder.user_photo.getImageMatrix();
                            matrix.postScale(1, 1);
                            viewHolder.user_photo.setImageMatrix(matrix);
                            return false;
                        }
                    }).into(viewHolder.user_photo);
                } catch (Exception e) {
                    viewHolder.user_photo.setImageDrawable(mContext.getDrawable(R.drawable.no_profile));
                }


/*                try {
                    Glide.with(mContext).setDefaultRequestOptions(requestOptions).load(user.getPhoto()).listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {

                            // viewHolder.progressBar.setVisibility(View.INVISIBLE);

                            viewHolder.progressBar.setVisibility(View.INVISIBLE);
                            viewHolder.progressBarTwo.setVisibility(View.INVISIBLE);
                            return false;
                        }
                    }).transition(withCrossFade()).into(viewHolder.user_photo);
                }catch (Exception e)
                {

                    *//*viewHolder.progressBar.setVisibility(View.INVISIBLE);
                    viewHolder.progressBarTwo.setVisibility(View.INVISIBLE);*//*
                    viewHolder.user_photo.setImageDrawable(mContext.getDrawable(R.drawable.no_profile));

                }*/


            }
        } catch (Exception e) {
            Log.e("Null pointer exp", "Internet issue");
        }


        checkFriendShip(viewHolder.friendsBtn, viewHolder.addFriendBtn, viewHolder.requestBtn, viewHolder.acceptBtn, viewHolder.denyBtn, user.getUserid());

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (viewHolder.friendsBtn.getVisibility() == View.VISIBLE) {
                    Intent intent = new Intent(mContext, VerticalPageActivity.class);
                    intent.putExtra("username", user.getUsername());
                    intent.putExtra("userid", user.getUserid());
                    intent.putExtra("photo", user.getPhoto());
                    intent.putExtra("live", "no");


                        /*Pair[] pairs = new Pair[1];
                        pairs[0] = new Pair<View, String>(viewHolder.user_photo,"imageTransition");
                        pairs[0] = new Pair<View, String>(viewHolder.username,"nameTransition");

                        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(activity,pairs);



                        mContext.startActivity(intent,options.toBundle());*/

                    mContext.startActivity(intent);
                } else {
                    //Toast.makeText(mContext,"Can't send messages as the user is not your friend",Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(mContext, ViewProfileActivity.class);


                    intent.putExtra("username", user.getUsername());
                    intent.putExtra("name", user.getUsername());
                    intent.putExtra("feeling_txt", user.getFeeling());
                    intent.putExtra("bio_txt", user.getBio());
                    intent.putExtra("userId", user.getUserid());
                    intent.putExtra("currentUsername", currentUsername);
                    if (viewHolder.acceptBtn.getVisibility() == View.VISIBLE)
                        intent.putExtra("friendStatus", "got");
                    else if (viewHolder.requestBtn.getVisibility() == View.VISIBLE)
                        intent.putExtra("friendStatus", "requested");
                    else
                        intent.putExtra("friendStatus", "notFriend");

                    intent.putExtra("feelingEmoji", user.getFeelingIcon());
                    intent.putExtra("photourl", user.getPhoto());

                    mContext.startActivity(intent);

                }

            }
        });


        viewHolder.addFriendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                viewHolder.addFriendBtn.setVisibility(View.GONE);
                DatabaseReference requestSentReference = FirebaseDatabase.getInstance().getReference("FriendList")
                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .child(user.getUserid());


                HashMap<String, Object> hashMap = new HashMap<>();

                hashMap.put("friendId", user.getUserid());
                hashMap.put("status", "requested");
                hashMap.put("username", user.getUsername());

                requestSentReference.updateChildren(hashMap);


                DatabaseReference requestReceiveReference = FirebaseDatabase.getInstance().getReference("FriendList")
                        .child(user.getUserid())
                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid());

                HashMap<String, Object> receiveHashMap = new HashMap<>();


                receiveHashMap.put("friendId", FirebaseAuth.getInstance().getCurrentUser().getUid());
                receiveHashMap.put("status", "got");
                receiveHashMap.put("username", currentUsername);

                requestReceiveReference.updateChildren(receiveHashMap);

                notifyDataSetChanged();


            }
        });

        viewHolder.acceptBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                viewHolder.acceptBtn.setVisibility(View.GONE);
                DatabaseReference requestSentReference = FirebaseDatabase.getInstance().getReference("FriendList")
                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .child(user.getUserid());


                HashMap<String, Object> hashMap = new HashMap<>();

                hashMap.put("friendId", user.getUserid());
                hashMap.put("status", "friends");
                hashMap.put("username", user.getUsername());

                requestSentReference.updateChildren(hashMap);

                FirebaseMessaging.getInstance().subscribeToTopic(user.getUsername()).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(mContext, "Friend request accepted", Toast.LENGTH_SHORT).show();
                        }
                    }
                });


                DatabaseReference requestReceiveReference = FirebaseDatabase.getInstance().getReference("FriendList")
                        .child(user.getUserid())
                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid());

                HashMap<String, Object> receiveHashMap = new HashMap<>();


                receiveHashMap.put("friendId", FirebaseAuth.getInstance().getCurrentUser().getUid());
                receiveHashMap.put("status", "friends");
                receiveHashMap.put("username", currentUsername);

                requestReceiveReference.updateChildren(receiveHashMap);

                notifyDataSetChanged();


            }
        });

        viewHolder.denyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                viewHolder.denyBtn.setVisibility(View.GONE);

                //notifyItemRemoved(viewHolder.getAdapterPosition());

                DatabaseReference requestSentReference = FirebaseDatabase.getInstance().getReference("FriendList")
                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .child(user.getUserid());


                requestSentReference.removeValue();


                DatabaseReference requestReceiveReference = FirebaseDatabase.getInstance().getReference("FriendList")
                        .child(user.getUserid())
                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid());


                requestReceiveReference.removeValue();

                notifyDataSetChanged();


            }
        });

        viewHolder.requestBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                viewHolder.requestBtn.setVisibility(View.GONE);

                // notifyItemRemoved(viewHolder.getAdapterPosition());

                DatabaseReference requestSentReference = FirebaseDatabase.getInstance().getReference("FriendList")
                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .child(user.getUserid());


                requestSentReference.removeValue();


                DatabaseReference requestReceiveReference = FirebaseDatabase.getInstance().getReference("FriendList")
                        .child(user.getUserid())
                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid());


                requestReceiveReference.removeValue();

                notifyDataSetChanged();

            }
        });


    }

    private void checkFriendShip(final Button friendBtn, final Button addFriendBtn, final Button requestBtn, final Button acceptBtn, final Button denyBtn, final String userId) {

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("FriendList")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child(userId);


        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()) {

                    Friend friend = dataSnapshot.getValue(Friend.class);
                    try {
                        if (friend.getFriendId().equals(userId)) {

                            if (friend.getStatus().equals("requested")) {
                                requestBtn.setVisibility(View.VISIBLE);
                                acceptBtn.setVisibility(View.GONE);
                                denyBtn.setVisibility(View.GONE);
                                friendBtn.setVisibility(View.GONE);
                                addFriendBtn.setVisibility(View.GONE);
                                // notifyDataSetChanged();
                            } else if (friend.getStatus().equals("got")) {
                                acceptBtn.setVisibility(View.VISIBLE);
                                denyBtn.setVisibility(View.VISIBLE);
                                requestBtn.setVisibility(View.GONE);
                                friendBtn.setVisibility(View.GONE);
                                addFriendBtn.setVisibility(View.GONE);
                                // notifyDataSetChanged();
                            } else if (friend.getStatus().equals("friends")) {
                                acceptBtn.setVisibility(View.GONE);
                                requestBtn.setVisibility(View.GONE);
                                denyBtn.setVisibility(View.GONE);
                                friendBtn.setVisibility(View.VISIBLE);
                                addFriendBtn.setVisibility(View.GONE);
                                //notifyDataSetChanged();
                            }


                        } else {
                            acceptBtn.setVisibility(View.GONE);
                            requestBtn.setVisibility(View.GONE);
                            denyBtn.setVisibility(View.GONE);
                            friendBtn.setVisibility(View.GONE);
                            addFriendBtn.setVisibility(View.VISIBLE);
                            // notifyDataSetChanged();
                        }
                    } catch (Exception e) {
                        //Node yet to be created.
                    }


                } else {
                    acceptBtn.setVisibility(View.GONE);
                    requestBtn.setVisibility(View.GONE);
                    friendBtn.setVisibility(View.GONE);
                    denyBtn.setVisibility(View.GONE);
                    addFriendBtn.setVisibility(View.VISIBLE);
                    // notifyDataSetChanged();

                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


    @Override
    public int getItemCount() {
        return mUsers.size();
    }

    public String returnCurrentUsername() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    User user = dataSnapshot.getValue(User.class);

                    currentUsername = user.getUsername();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        return currentUsername;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {


        public TextView username, phonebook_name;
        public CustomCircularView user_photo;
        public ProgressBar progressBar, progressBarTwo;
        public Button friendsBtn, addFriendBtn, requestBtn, acceptBtn, denyBtn;
        public Boolean friend = false;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            username = itemView.findViewById(R.id.username);
            user_photo = itemView.findViewById(R.id.user_photo);
            progressBar = itemView.findViewById(R.id.progress);
            progressBarTwo = itemView.findViewById(R.id.progressTwo);
            addFriendBtn = itemView.findViewById(R.id.followbtn);
            friendsBtn = itemView.findViewById(R.id.followingbtn);
            requestBtn = itemView.findViewById(R.id.requestedBtn);
            acceptBtn = itemView.findViewById(R.id.acceptBtn);
            denyBtn = itemView.findViewById(R.id.denyBtn);
            phonebook_name = itemView.findViewById(R.id.phonebook_name);


        }

    }

}
