package com.shubhamkislay.jetpacklogin.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.shubhamkislay.jetpacklogin.R;

import java.util.ArrayList;
import java.util.List;

public class ColorAdapter extends RecyclerView.Adapter<ColorAdapter.ColorViewHolder> {



    Context context;
    List<Integer> colorList;
    ColorAdapterListener colorAdapterListener;

    public ColorAdapter(Context context, ColorAdapterListener colorAdapterListener) {
        this.context = context;
        this.colorList = getColorList();
        this.colorAdapterListener = colorAdapterListener;
    }

    @NonNull
    @Override
    public ColorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {

        View itemView = LayoutInflater.from(context).inflate(R.layout.color_layout, parent,false);
        return new ColorViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ColorViewHolder colorViewHolder, int position) {

        colorViewHolder.color_section.setCardBackgroundColor(colorList.get(position));

    }

    @Override
    public int getItemCount() {
        return colorList.size();
    }

    public class ColorViewHolder extends RecyclerView.ViewHolder {
        public CardView color_section;

        public ColorViewHolder(View itemView)
        {
            super(itemView);
            color_section = itemView.findViewById(R.id.color_section);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    colorAdapterListener.onColorSelected(colorList.get(getAdapterPosition()));
                }
            });


        }
    }


    private List<Integer> getColorList() {

        List<Integer> colorList = new ArrayList<>();


        colorList.add(Color.parseColor("#8D53E7"));
        colorList.add(Color.parseColor("#73f4ea"));
        colorList.add(Color.parseColor("#ff3e82"));
        colorList.add(Color.parseColor("#ffbff8"));
        colorList.add(Color.parseColor("#fdff00"));
        colorList.add(Color.parseColor("#5e2600"));
        colorList.add(Color.parseColor("#857653"));
        colorList.add(Color.parseColor("#e3d9b9"));
        colorList.add(Color.parseColor("#37dfca"));
        colorList.add(Color.parseColor("#332631"));
        colorList.add(Color.parseColor("#000000"));


        return colorList;
    }

    public interface ColorAdapterListener
    {
        void onColorSelected(int color);
    }




}
