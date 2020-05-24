package com.hieeway.hieeway.Fragments;


import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.hieeway.hieeway.Adapters.SendMessageAdapter;
import com.hieeway.hieeway.ArchiveActivityViewModel;
import com.hieeway.hieeway.ArchiveActivityViewModelFactory;
import com.hieeway.hieeway.Model.ChatMessage;
import com.hieeway.hieeway.R;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class PinnedMessagesFragment extends Fragment {

    public RecyclerView recyclerView;
    public ArchiveActivityViewModel archiveActivityViewModel;
    private ArchiveActivityViewModelFactory archiveActivityViewModelFactory;
    private String userIdChattingWith;
    private SendMessageAdapter sendMessageAdapter;
    private List<ChatMessage> messageList, sendMessagelist;


    public PinnedMessagesFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_pinned_messages, container, false);
        recyclerView = view.findViewById(R.id.pinned_messages_recycler_view);

        userIdChattingWith = getArguments().getString("userIdChattingWith");

        sendMessagelist = new ArrayList<>();
        messageList = new ArrayList<>();


        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setStackFromEnd(true);


        recyclerView.setLayoutManager(linearLayoutManager);

        archiveActivityViewModelFactory = new ArchiveActivityViewModelFactory(userIdChattingWith);
        archiveActivityViewModel = ViewModelProviders.of(this, archiveActivityViewModelFactory).get(ArchiveActivityViewModel.class);
        archiveActivityViewModel.getChatList().observe(getViewLifecycleOwner(), new Observer<List<ChatMessage>>() {
            @Override
            public void onChanged(@Nullable List<ChatMessage> chatMessageList) {

                messageList.clear();

                messageList = chatMessageList;


                filterList(messageList);

            }
        });


        //archiveActivityViewModel

        return view;
    }

    private void filterList(List<ChatMessage> messageList) {


        try {

            for (ChatMessage chatMessage : messageList) {

                if (!chatMessage.getSenderId().equals(FirebaseAuth.getInstance().getCurrentUser().getUid()))
                    sendMessagelist.add(chatMessage);

            }


            addListToRecyclerView(sendMessagelist);
        }catch (Exception e)
        {
            Toast.makeText(getActivity(),"Internet is crawling! You might get issues using the App",Toast.LENGTH_LONG).show();
        }


    }

    private void addListToRecyclerView(List<ChatMessage> sendMessagelist) {


        //sendMessageAdapter = new SendMessageAdapter(getContext(), sendMessagelist,userIdChattingWith,sendMessagelist);
        recyclerView.setAdapter(sendMessageAdapter);


    }

    public void setParentFragmentData(String userIdChattingWith)
    {
        this.userIdChattingWith = userIdChattingWith;
    }

}
