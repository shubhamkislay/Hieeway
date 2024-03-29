package com.hieeway.hieeway.Adapters;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.hieeway.hieeway.R;

import java.util.List;

public class EmojiAdapter extends RecyclerView.Adapter<EmojiAdapter.EmojiViewHolder> {


    private Context context;
    private List<String> emojiList;
    EmojiAdapterListener listener;


    public EmojiAdapter(Context context, List<String> emojiList, EmojiAdapterListener listener) {
        this.context = context;
        this.emojiList = emojiList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public EmojiViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {

        View itemView = LayoutInflater.from(context).inflate(R.layout.emoji_item, parent, false);

        return new EmojiViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull EmojiViewHolder emojiViewHolder, int position) {


        emojiViewHolder.emojiconTextView.setText(emojiList.get(position));

    }

    @Override
    public int getItemCount() {
        return emojiList.size();
    }

    public class EmojiViewHolder extends RecyclerView.ViewHolder {
        public TextView emojiconTextView;

        public EmojiViewHolder(@NonNull View itemView) {
            super(itemView);

            emojiconTextView = itemView.findViewById(R.id.emoji_text_view);
            emojiconTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onEmojiItemSelected(emojiList.get(getAdapterPosition()));
                }
            });
        }
    }


    public interface EmojiAdapterListener
    {

        void onEmojiItemSelected(String emoji);

    }

}

