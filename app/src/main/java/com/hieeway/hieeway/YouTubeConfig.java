package com.hieeway.hieeway;

import android.content.Context;
import android.os.Build;
import android.text.Html;
import android.util.Log;

import com.google.api.client.http.HttpHeaders;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.StringUtils;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;
import com.google.api.services.youtube.model.Video;
import com.google.api.services.youtube.model.VideoListResponse;
import com.hieeway.hieeway.Model.VideoItem;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.NumberFormat;
import java.time.Duration;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import io.opencensus.internal.StringUtil;

public class YouTubeConfig {

    private static final String API_KEY = "AIzaSyDl7rYj9tB9Hn1gp_Oe4TUpEyGbTVYGrZc";
    HttpHeaders httpHeaders;
    private YouTube youtube;
    private YouTube.Search.List query;
    private YouTube.Videos.List videoQuery;

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
            query.setMaxResults((long) 5);
            query.setType("video");

            // query.setFields("items(id/videoId,snippet/title,snippet/description,snippet/thumbnails/high/url,snippet/channelTitle)");
            query.setFields("items(id/videoId)");
        } catch (IOException e) {
            Log.d("YC", "Could not initialize: " + e);
        }

        try {

            videoQuery = youtube.videos().list("snippet,contentDetails,statistics");
            videoQuery.setKey(API_KEY);
            //query.setRequestHeaders( )
            videoQuery.setMaxResults((long) 1);


            videoQuery.setFields("items(snippet/title,snippet/description,snippet/thumbnails/high/url,snippet/channelTitle,contentDetails/duration,statistics/viewCount)");
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

                if (Build.VERSION.SDK_INT >= 24) {
                    item.setTitle(Html.fromHtml(result.getSnippet().getTitle(), Html.FROM_HTML_MODE_LEGACY).toString());
                } else {
                    item.setTitle(Html.fromHtml(result.getSnippet().getTitle()).toString());
                }






                /*
                String videoJSON = retrieveVideoJSON(result.getId().getVideoId(), "contentDetails", API_KEY);
                JSONObject jsonObj = new JSONObject(videoJSON).getJSONArray("items").getJSONObject(0).getJSONObject("contentDetails");
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    long seconds = Duration.parse(jsonObj.getString("duration")).getSeconds();
                    item.setDuration(seconds);
                }
                else
                {

                }*/


                item.setDescription(result.getSnippet().getChannelTitle());
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

                item.setDuration("default");

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

    public List<VideoItem> searchVideo(String keywords) {
        //videoQuery.setQ(keywords);
        query.setQ(keywords);
        try {
            SearchListResponse response = query.execute();
            List<SearchResult> results = response.getItems();


            List<VideoItem> items = new ArrayList<VideoItem>();
            for (SearchResult result : results) {
                VideoItem item = new VideoItem();
                videoQuery.setId(result.getId().getVideoId());

                VideoListResponse videoListResponse = videoQuery.execute();
                List<Video> videoResults = videoListResponse.getItems();
                Video video = videoResults.get(0);

                if (Build.VERSION.SDK_INT >= 24) {
                    item.setTitle(Html.fromHtml(video.getSnippet().getTitle(), Html.FROM_HTML_MODE_LEGACY).toString());
                } else {
                    item.setTitle(Html.fromHtml(video.getSnippet().getTitle()).toString());
                }

                BigInteger integer = video.getStatistics().getViewCount();

                String bigInt = NumberFormat.getNumberInstance(Locale.US).format(
                        integer);
                item.setViewCount(bigInt);


                item.setDescription(video.getSnippet().getChannelTitle());
                String thumbnailURL = null;
                try {
                    if (video.getSnippet().getThumbnails().getHigh().getUrl() != null)
                        thumbnailURL = video.getSnippet().getThumbnails().getHigh().getUrl();
                } catch (NullPointerException e) {
                    try {
                        if (video.getSnippet().getThumbnails().getMedium().getUrl() != null)
                            thumbnailURL = video.getSnippet().getThumbnails().getMedium().getUrl();
                    } catch (NullPointerException ne) {

                        try {
                            if (video.getSnippet().getThumbnails().getStandard().getUrl() != null)
                                thumbnailURL = video.getSnippet().getThumbnails().getStandard().getUrl();
                        } catch (NullPointerException nee) {
                            thumbnailURL = video.getSnippet().getThumbnails().getDefault().getUrl();
                        }
                    }

                }
                String duration = "default";

                //PT15M33S
                String hour = "default", mintues = "default", second = "default";
                String durationTime = video.getContentDetails().getDuration();
                int hourIndex = durationTime.indexOf("H");
                int minutesIndex = durationTime.indexOf("M");
                int secondsIndex = durationTime.indexOf("S");
                if (hourIndex != -1) {
                    hour = durationTime.substring(hourIndex - 2, hourIndex);
                    char c = hour.charAt(0);
                    if (!Character.isDigit(c)) {
                        hour = durationTime.substring(hourIndex - 1, hourIndex);
                    }
                }

                if (minutesIndex != -1) {
                    mintues = durationTime.substring(minutesIndex - 2, minutesIndex);
                    char c = mintues.charAt(0);
                    if (!Character.isDigit(c)) {
                        mintues = durationTime.substring(minutesIndex - 1, minutesIndex);
                    }
                }

                if (secondsIndex != -1) {
                    second = durationTime.substring(secondsIndex - 2, secondsIndex);
                    char c = second.charAt(0);
                    if (!Character.isDigit(c)) {
                        second = durationTime.substring(secondsIndex - 1, secondsIndex);
                    }
                }


                if (!hour.equals("default"))
                    durationTime = hour + ":" + mintues + ":" + second;
                else if (!mintues.equals("default"))
                    durationTime = mintues + ":" + second;
                else
                    durationTime = second + "s";

                /*if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    long seconds = Duration.parse(video.getContentDetails().getDuration()).getSeconds();

                    long p1 = (seconds % (24 * 3600)) / 3600 ;
                    long p2 = (seconds % (24 * 3600 * 3600)) / 60;
                    long p3 = (seconds % (24 * 3600 * 3600 * 60)) / 60;

                    if(p1>0) {
                         duration = p1 + ":" + p2 + ":" + p3;

                    }
                    else {
                         duration = p2 + ":" + p3;

                    }
                }*/

                item.setDuration(durationTime);
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

    String retrieveVideoJSON(String videoID, String part, String APIkey) {
        String postURL = "https://www.googleapis.com/youtube/v3/videos?id=" + videoID + "&part=" + part + "&key=" + APIkey;
        String output = "";
        try {
            URL url = new URL(postURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            BufferedReader br1 = new BufferedReader(new InputStreamReader(
                    (conn.getInputStream())));
            String line1;
            while ((line1 = br1.readLine()) != null) {
                output = output + line1;
            }
            conn.disconnect();
            br1.close();

        } catch (IOException e) {
            System.out.println("\ne = " + e.getMessage() + "\n");
        }
        return output;

    }

    public String getVideoDuration(String videoID) {
        try {
            videoQuery = youtube.videos().list("id,contentDetails");
        } catch (IOException e) {
            e.printStackTrace();
        }
        videoQuery.setId(videoID);
        videoQuery.setKey(API_KEY);
        videoQuery.setMaxResults((long) 1);
        videoQuery.setFields("items(contentDetails/duration)");

        VideoListResponse videoListResponse = null;
        try {
            videoListResponse = videoQuery.execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
        List<Video> videoList = videoListResponse.getItems();

        Video video = videoList.get(0);


        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            long seconds = Duration.parse(video.getContentDetails().getDuration()).getSeconds();

            long p1 = (seconds % (24 * 3600)) / 3600;
            long p2 = (seconds % (24 * 3600 * 3600)) / 60;
            double p3 = (seconds % (24 * 3600 * 3600 * 60)) / 60;

            if (p1 > 0) {
                String duration = p1 + ":" + p2 + ":" + p3;
                return duration;
            } else {
                String duration = p2 + ":" + p3;
                return duration;
            }
        } else
            return "default";
    }


}
