package com.shubhamkislay.jetpacklogin;

import android.content.Context;
import android.util.Log;

import com.google.api.client.http.HttpHeaders;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;
import com.shubhamkislay.jetpacklogin.Model.VideoItem;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class YouTubeConfig {

    private static final String API_KEY = "AIzaSyDl7rYj9tB9Hn1gp_Oe4TUpEyGbTVYGrZc";
    HttpHeaders httpHeaders;
    private YouTube youtube;
    private YouTube.Search.List query;

    public YouTubeConfig() {

    }

    public YouTubeConfig(final Context context) {


        youtube = new YouTube.Builder(new NetHttpTransport(),
                JacksonFactory.getDefaultInstance(), new HttpRequestInitializer() {
            @Override
            public void initialize(HttpRequest hr) throws IOException {
            }
        }).setApplicationName(context.getString(R.string.app_name)).build();

        try {
            query = youtube.search().list("id,snippet");
            query.setKey(API_KEY);
            //query.setRequestHeaders( )
            query.setMaxResults((long) 25);
            query.setType("video");

            query.setFields("items(id/videoId,snippet/title,snippet/description,snippet/thumbnails/high/url)");
        } catch (IOException e) {
            Log.d("YC", "Could not initialize: " + e);
        }
    }

    public static String getApiKey() {
        return API_KEY;
    }

    public List<VideoItem> search(String keywords) {
        query.setQ(keywords);
        try {
            SearchListResponse response = query.execute();
            List<SearchResult> results = response.getItems();


            List<VideoItem> items = new ArrayList<VideoItem>();
            for (SearchResult result : results) {
                VideoItem item = new VideoItem();


                item.setTitle(result.getSnippet().getTitle());
                item.setDescription(result.getSnippet().getDescription());
                String thumbnailURL = null;
                try {
                    if (result.getSnippet().getThumbnails().getHigh().getUrl() != null)
                        thumbnailURL = result.getSnippet().getThumbnails().getHigh().getUrl();
                } catch (NullPointerException e) {
                    try {
                        if (result.getSnippet().getThumbnails().getMedium().getUrl() != null)
                            thumbnailURL = result.getSnippet().getThumbnails().getMedium().getUrl();
                    } catch (NullPointerException ne) {

                        try {
                            if (result.getSnippet().getThumbnails().getStandard().getUrl() != null)
                                thumbnailURL = result.getSnippet().getThumbnails().getStandard().getUrl();
                        } catch (NullPointerException nee) {
                            thumbnailURL = result.getSnippet().getThumbnails().getDefault().getUrl();
                        }
                    }

                }


                item.setThumbnailURL(thumbnailURL);
                item.setId(result.getId().getVideoId());
                items.add(item);
            }
            return items;
        } catch (IOException e) {
            Log.d("YC", "Could not search: " + e);
            return null;
        }
    }


}
