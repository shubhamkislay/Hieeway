package com.shubhamkislay.jetpacklogin.Utils;


import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.shubhamkislay.jetpacklogin.Adapters.ColorAdapter;
import com.shubhamkislay.jetpacklogin.Interface.AddTextFragmentListener;
import com.shubhamkislay.jetpacklogin.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class AddTextFragment extends BottomSheetDialogFragment implements ColorAdapter.ColorAdapterListener {



    int colorSelected = Color.parseColor("#000000");//default color of add_text_view


    AddTextFragmentListener listener;


    EditText edt_add_text;
    RecyclerView recycler_color;
    Button btn_done;
    ColorAdapter colorAdapter;

    static AddTextFragment instance;


    public static AddTextFragment getInstance(){
        if(instance == null)
            instance = new AddTextFragment();
        return instance;
    }






    public void setListener(AddTextFragmentListener listener) {
        this.listener = listener;
    }

    public AddTextFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View itemView = inflater.inflate(R.layout.fragment_add_text, container, false);


        edt_add_text = itemView.findViewById(R.id.edt_add_text);
        btn_done = itemView.findViewById(R.id.btn_done);

        recycler_color = itemView.findViewById(R.id.recycler_color);
        recycler_color = itemView.findViewById(R.id.recycler_color);
        recycler_color.setHasFixedSize(true);
        recycler_color.setLayoutManager(new LinearLayoutManager(getActivity(),LinearLayoutManager.HORIZONTAL,false));


        colorAdapter = new ColorAdapter(getContext(), this);

        recycler_color.setAdapter(colorAdapter);


        btn_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onAddTextButtonClick(edt_add_text.getText().toString(), colorSelected);
            }
        });




        return itemView;
    }

    @Override
    public void onColorSelected(int color) {

        colorSelected = color;

    }
}
