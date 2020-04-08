package com.shubhamkislay.jetpacklogin.Adapters;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import com.shubhamkislay.jetpacklogin.Interface.FiltersListFragmentListener;
import com.shubhamkislay.jetpacklogin.R;
import com.zomato.photofilters.utils.ThumbnailItem;

import java.util.List;

public class ThumbnailAdapter extends RecyclerView.Adapter<ThumbnailAdapter.MyViewHolder> {



    private List<ThumbnailItem> thumbnailItems;
    private FiltersListFragmentListener listener;
    private Context context;
    private int selectedIndex = 0;


    public ThumbnailAdapter(List<ThumbnailItem> thumbnailItems, FiltersListFragmentListener listener, Context context) {
        this.thumbnailItems = thumbnailItems;
        this.listener = listener;
        this.context = context;

    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int position) {
        View itemView  = LayoutInflater.from(context).inflate(R.layout.thumbnail_item,parent,false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder,final int position) {
        final ThumbnailItem thumbnailItem = thumbnailItems.get(position);


        myViewHolder.thumbnail.setImageBitmap(thumbnailItem.image);
        myViewHolder.thumbnail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                listener.onFilterSelected(thumbnailItem.filter);
                selectedIndex = position;
                notifyDataSetChanged();

            }
        });


        myViewHolder.filter_name.setText(thumbnailItem.filterName);



        if(selectedIndex == position)

            myViewHolder.filter_name.setTextColor(ContextCompat.getColor(context, R.color.selected_filter));

        else

            myViewHolder.filter_name.setTextColor(ContextCompat.getColor(context, R.color.normal_filter));
    }

    @Override
    public int getItemCount() {
        return thumbnailItems.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        ImageView thumbnail;
        TextView filter_name;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            thumbnail = (ImageView) itemView.findViewById(R.id.thumbnail);
            filter_name = itemView.findViewById(R.id.filter_name);
        }
    }
}
