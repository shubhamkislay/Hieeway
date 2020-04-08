package com.shubhamkislay.jetpacklogin;


import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.shubhamkislay.jetpacklogin.Adapters.ThumbnailAdapter;
import com.shubhamkislay.jetpacklogin.Interface.FiltersListFragmentListener;
import com.shubhamkislay.jetpacklogin.Utils.BitmapUtils;
import com.shubhamkislay.jetpacklogin.Utils.SpacesItemDecoration;
import com.zomato.photofilters.FilterPack;
import com.zomato.photofilters.imageprocessors.Filter;
import com.zomato.photofilters.utils.ThumbnailItem;
import com.zomato.photofilters.utils.ThumbnailsManager;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class FiltersListFragment extends BottomSheetDialogFragment implements FiltersListFragmentListener {

    RecyclerView recyclerView;
    ThumbnailAdapter adapter;

    List<ThumbnailItem> thumbnailItems = new ArrayList<>();;



    FiltersListFragmentListener listener;




    static FiltersListFragment instance;

    static Bitmap bitmap;

    public static  FiltersListFragment getInstance(Bitmap bitmapSave)
    {
        bitmap = bitmapSave;

        if(instance == null)
        {

            instance = new FiltersListFragment();


        }

        return instance;
    }




    public void setListener(FiltersListFragmentListener listener) {
        this.listener = listener;
    }

    public FiltersListFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View itemView = inflater.inflate(R.layout.fragment_filters_list, container, false);




        adapter =  new ThumbnailAdapter(thumbnailItems, this, getActivity());


        recyclerView = itemView.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(),LinearLayoutManager.HORIZONTAL,false));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        int space = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,8, getResources().getDisplayMetrics());
        recyclerView.addItemDecoration(new SpacesItemDecoration(space));
        recyclerView.setAdapter(adapter);


        displayThumbnail(bitmap);




        return itemView;
    }

    void displayThumbnail(final Bitmap bitmap) {

        Runnable r = new Runnable() {
            @Override
            public void run() {

                Bitmap thumbImg;
                if(bitmap == null)
                    thumbImg = BitmapUtils.getBitmapFromAssets(getActivity(), PhotoEditToolsActivity.pictureName,  100, 100);
                else
                    thumbImg = Bitmap.createScaledBitmap(bitmap, 100, 100, false);


                if(thumbImg == null)
                    return;
                ThumbnailsManager.clearThumbs();
                thumbnailItems.clear();

                //add normal bitmap first

                ThumbnailItem thumbnailItem = new ThumbnailItem();
                thumbnailItem.image = thumbImg;
                thumbnailItem.filterName = "normal";
                ThumbnailsManager.addThumb(thumbnailItem);

                List<Filter> filters = FilterPack.getFilterPack(getActivity());



                for(Filter filter: filters)
                {
                    ThumbnailItem thumbnailItem1 = new ThumbnailItem();
                    thumbnailItem1.image = thumbImg;
                    thumbnailItem1.filterName=filter.getName();
                    thumbnailItem1.filter = filter;
                    ThumbnailsManager.addThumb(thumbnailItem1);
                }



                thumbnailItems.addAll(ThumbnailsManager.processThumbs(getActivity()));

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        adapter.notifyDataSetChanged();

                    }
                });


            }
        };

        new Thread(r).start();
    }

    @Override
    public void onFilterSelected(Filter filter) {

        if(listener!=null)
            listener.onFilterSelected(filter);


    }
}

