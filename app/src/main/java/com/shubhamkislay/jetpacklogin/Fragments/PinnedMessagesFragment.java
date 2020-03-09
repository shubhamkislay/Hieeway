package com.shubhamkislay.jetpacklogin.Fragments;


import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.shubhamkislay.jetpacklogin.Adapters.SendMessageAdapter;
import com.shubhamkislay.jetpacklogin.ArchiveActivity;
import com.shubhamkislay.jetpacklogin.ArchiveActivityViewModel;
import com.shubhamkislay.jetpacklogin.ArchiveActivityViewModelFactory;
import com.shubhamkislay.jetpacklogin.Model.ChatMessage;
import com.shubhamkislay.jetpacklogin.R;

import java.security.spec.ECField;
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
        archiveActivityViewModel.getChatList().observe(this, new Observer<List<ChatMessage>>() {
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
