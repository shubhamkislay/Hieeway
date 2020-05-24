package com.hieeway.hieeway.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hieeway.hieeway.R;

import java.util.List;

public class EmojiDisplayAdapter extends RecyclerView.Adapter<EmojiDisplayAdapter.EmojiDisplayViewholder> {


    List<String> emojiList;
    Context context;
    EmojiAdapterListener listener;

    public EmojiDisplayAdapter(Context context, List<String> emojiList, EmojiAdapterListener listener) {
        this.context = context;
        this.emojiList = emojiList;
        this.listener = listener;
    }


    @NonNull
    @Override
    public EmojiDisplayViewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.emoji_item, parent, false);

        return new EmojiDisplayViewholder(itemView);
    }

    @Override
    public void onBindViewHolder(final EmojiDisplayViewholder holder, final int position) {

        holder.emojiconTextView.setText(emojiList.get(position));
        holder.emojiconTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                listener.onEmojiItemSelected(emojiList.get(position));
            }
        });

    }

    @Override
    public int getItemCount() {
        return emojiList.size();
    }

    public interface EmojiAdapterListener {

        void onEmojiItemSelected(String emoji);

    }

    public class EmojiDisplayViewholder extends RecyclerView.ViewHolder {
        public TextView emojiconTextView;

        public EmojiDisplayViewholder(@NonNull View itemView) {
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
}
