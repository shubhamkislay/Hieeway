package com.shubhamkislay.jetpacklogin;


import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.ToggleButton;


import com.shubhamkislay.jetpacklogin.Adapters.ColorAdapter;
import com.shubhamkislay.jetpacklogin.Interface.BrushFragmentListener;

import java.util.ArrayList;
import java.util.List;


public class BrushFragment extends BottomSheetDialogFragment implements ColorAdapter.ColorAdapterListener {


    SeekBar seekBar_brush_size,seekbar_Opacity_size;
    RecyclerView recycler_color;
    ToggleButton btn_brush_state;
    ColorAdapter colorAdapter;


    BrushFragmentListener listener;

    static BrushFragment instance;

    public static BrushFragment getInstance()
    {
        if(instance == null)
            instance = new BrushFragment();
        return instance;
    }


    public void setListener(BrushFragmentListener listener) {
        this.listener = listener;
    }


    public BrushFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View itemView=inflater.inflate(R.layout.fragment_brush, container, false);

        seekBar_brush_size = itemView.findViewById(R.id.seekbar_brush_size);
        seekbar_Opacity_size = itemView.findViewById(R.id.seekbar_brush_opacity);
        btn_brush_state = itemView.findViewById(R.id.btn_brush_state);
        recycler_color = itemView.findViewById(R.id.recycler_color);
        recycler_color.setHasFixedSize(true);
        recycler_color.setLayoutManager(new LinearLayoutManager(getActivity(),LinearLayoutManager.HORIZONTAL,false));


        colorAdapter = new ColorAdapter(getContext(), this);

        recycler_color.setAdapter(colorAdapter);

        seekbar_Opacity_size.setProgress(100);



        //Event

        seekbar_Opacity_size.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                listener.onBrushOpacityChangedListener(progress);

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        seekBar_brush_size.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                listener.onBrushSizeChangedListener(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        btn_brush_state.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                listener.onBrushStateChangedListener(isChecked);
            }
        });

        return itemView;
    }

    /*private List<Integer> getColorList() {

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
    }*/

    @Override
    public void onColorSelected(int color) {

        listener.onBrushColorChangedListener(color);

    }
}
