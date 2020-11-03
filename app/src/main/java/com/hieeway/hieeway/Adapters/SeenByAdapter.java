package com.hieeway.hieeway.Adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.hieeway.hieeway.CustomImageView;
import com.hieeway.hieeway.Interface.AddRecipientListener;
import com.hieeway.hieeway.Model.Recipient;
import com.hieeway.hieeway.Model.SeenByUser;
import com.hieeway.hieeway.R;
import com.hieeway.hieeway.Utils.RecipientListDiffUtilCallback;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class SeenByAdapter extends RecyclerView.Adapter<SeenByAdapter.ViewHolder> {


    private List<SeenByUser> stringList;
    private Context context;
    Activity parentActivity;

    public SeenByAdapter(Activity activity, List<SeenByUser> stringList) {
        this.context = (Context) activity;
        this.stringList = stringList;
        this.parentActivity = activity;


    }

    @NonNull
    @Override
    public SeenByAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.seen_by_item, parent, false);

        TextView username = view.findViewById(R.id.username);
        username.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/samsungsharpsans-bold.otf"));

        return new SeenByAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SeenByAdapter.ViewHolder holder, int position) {

        SeenByUser user = stringList.get(position);

        holder.username.setText(user.getUsername());
        try {

            Glide.with(context)
                    .load(user.getPhoto())
                    .into(holder.user_photo);


        } catch (Exception e) {
            Glide.with(context)
                    .load(context.getDrawable(R.drawable.no_profile))
                    .into(holder.user_photo);
        }
    }

    @Override
    public int getItemCount() {
        return stringList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView username;
        CircleImageView user_photo;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            username = itemView.findViewById(R.id.username);
            user_photo = itemView.findViewById(R.id.user_photo);

        }
    }
}
