package snowbot.Music;

import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.ResourceId;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;
import com.google.api.services.youtube.model.Video;
import com.google.api.services.youtube.model.VideoListResponse;
import java.io.BufferedReader;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

public class YoutubeSearch {
    private static final String PROPERTIES_FILENAME = "youtube.properties";
    private static final long NUMBER_OF_VIDEOS_RETURNED = 5;
    private static YouTube youtube;
    private List<SearchResult> searchResultList;
    private String song;
    
    public YoutubeSearch(String query) {
        Properties properties = new Properties();
        song = query;

        try {
            youtube = new YouTube.Builder(new NetHttpTransport(), new JacksonFactory(), new HttpRequestInitializer() {
                public void initialize(HttpRequest request) throws IOException {
                }
            }).setApplicationName("youtube-cmdline-search-sample").build();

            String queryTerm = query;

            YouTube.Search.List search = youtube.search().list("id,snippet");

            String apiKey = Read(PROPERTIES_FILENAME);
            search.setKey(apiKey);
            search.setQ(queryTerm);

            search.setType("video");

            search.setFields("items(id/kind,id/videoId,snippet/title,snippet/thumbnails/default/url)");
            search.setMaxResults(NUMBER_OF_VIDEOS_RETURNED);

            SearchListResponse searchResponse = search.execute();
            searchResultList = searchResponse.getItems();
        } catch (GoogleJsonResponseException e) {
            System.err.println("There was a service error: " + e.getDetails().getCode() + " : "
                    + e.getDetails().getMessage());
        } catch (IOException e) {
            System.err.println("There was an IO error: " + e.getCause() + " : " + e.getMessage());
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    public String returnID() {
        Iterator<SearchResult> iteratorSearchResults = searchResultList.iterator();
        
        while (iteratorSearchResults.hasNext()) {

            SearchResult singleVideo = iteratorSearchResults.next();
            ResourceId rId = singleVideo.getId();

            if (rId.getKind().equals("youtube#video")) {
                return rId.getVideoId();
            }
        }
        
        return "No ID";
    }
    
    public String returnTitle() throws IOException {
        YouTube.Videos.List videoRequest = youtube.videos().list("snippet,statistics,contentDetails");
        videoRequest.setId(returnID());
        videoRequest.setKey(Read(PROPERTIES_FILENAME));
        VideoListResponse listResponse = videoRequest.execute();
        List<Video> videoList = listResponse.getItems();

        Video targetVideo = videoList.iterator().next();
        
        return targetVideo.getSnippet().getTitle();
    }
    
    private static String Read(String filePath) {
        Path file = Paths.get(filePath);
        String text = null;
        
        InputStream input = null;
        
        try {
            input = Files.newInputStream(file);
            BufferedReader reader = new BufferedReader(new InputStreamReader(input));
            text = reader.readLine();
            input.close();
        }
        catch (Exception e) {
            System.out.println(e);
        }
        
        return text;
    }
}
