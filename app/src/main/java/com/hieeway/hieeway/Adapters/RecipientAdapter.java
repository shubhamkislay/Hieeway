package com.hieeway.hieeway.Adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.hieeway.hieeway.CustomImageView;
import com.hieeway.hieeway.Model.Recipient;
import com.hieeway.hieeway.R;

import java.util.List;

public class RecipientAdapter extends RecyclerView.Adapter<RecipientAdapter.ViewHolder> {


    private List<Recipient> recipientList;
    private Context context;
    final static String HAPPY = "happy";
    final static String BORED = "bored";
    final static String EXCITED = "excited";
    final static String SAD = "sad";
    final static String CONFUSED = "confused";
    final static String ANGRY = "angry";
    Activity parentActivity;

    public RecipientAdapter(Activity activity, List<Recipient> recipientList) {
        this.context = (Context) activity;
        this.recipientList = recipientList;
        this.parentActivity = activity;
    }

    @NonNull
    @Override
    public RecipientAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recipient_item, parent, false);

        //profile_view.setTypeface(Typeface.createFromAsset(mContext.getAssets(), "fonts/samsungsharpsans-bold.otf"));

        return new RecipientAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecipientAdapter.ViewHolder holder, int position) {

        Recipient recipient = recipientList.get(holder.getAdapterPosition());

        holder.username.setText(recipient.getUsername());
        if (!recipient.getPhoto().equals("default"))
            Glide.with(context).load(recipient.getPhoto())
                    .into(holder.user_photo);
        else
            Glide.with(context).load(context.getDrawable(R.drawable.no_profile))
                    .into(holder.user_photo);

        //checkUserFeeling(holder.user_photo, holder.get)
        try {

            if (!recipient.getFeelingIcon().equals("default")) {
                holder.feeling_icon.setVisibility(View.GONE);
                holder.emoji_icon.setText(recipient.getFeelingIcon());
                holder.emoji_icon.setVisibility(View.VISIBLE);

            } else {
                holder.feeling_icon.setVisibility(View.GONE);
                holder.emoji_icon.setVisibility(View.GONE);


                switch (recipient.getFeeling()) {
                    case HAPPY:
                        holder.feeling_icon.setVisibility(View.VISIBLE);
                        holder.feeling_icon.setBackground(parentActivity.getResources().getDrawable(R.drawable.ic_emoticon_feeling_happy));
                        break;
                    case SAD:
                        holder.feeling_icon.setVisibility(View.VISIBLE);
                        holder.feeling_icon.setBackground(parentActivity.getResources().getDrawable(R.drawable.ic_emoticon_feeling_sad));
                        break;
                    case BORED:
                        holder.feeling_icon.setVisibility(View.VISIBLE);
                        holder.feeling_icon.setBackground(parentActivity.getResources().getDrawable(R.drawable.ic_emoticon_feeling_bored));
                        break;
                    case ANGRY:
                        holder.feeling_icon.setVisibility(View.VISIBLE);
                        holder.feeling_icon.setBackground(parentActivity.getResources().getDrawable(R.drawable.ic_emoticon_feeling_angry));
                        break;
                    case EXCITED:
                        holder.feeling_icon.setVisibility(View.VISIBLE);
                        holder.feeling_icon.setBackground(parentActivity.getResources().getDrawable(R.drawable.ic_emoticon_feeling_excited));
                        break;
                    case CONFUSED:
                        holder.feeling_icon.setVisibility(View.VISIBLE);
                        holder.feeling_icon.setBackground(parentActivity.getResources().getDrawable(R.drawable.ic_emoticon_feeling_confused));
                        break;
                }
            }

        } catch (Exception e) {
            //
        }


    }

    @Override
    public int getItemCount() {
        return recipientList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView username, feeling_icon, emoji_icon;
        CustomImageView user_photo;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            username = itemView.findViewById(R.id.username);
            feeling_icon = itemView.findViewById(R.id.feeling_icon);
            emoji_icon = itemView.findViewById(R.id.emoji_icon);
            user_photo = itemView.findViewById(R.id.user_photo);


        }
    }
}
