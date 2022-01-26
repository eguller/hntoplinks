package com.eguller.hntoplinks.services;

import com.eguller.hntoplinks.models.HnStory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;

@Service
public class FirebaseioService {
  private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

  private final RestTemplate restTemplate;

  @Value("${hntoplinks.firebaseio-url}")
  private String firebaseIoBaseUrl;

  public FirebaseioService(RestTemplate restTemplate) {
    this.restTemplate = restTemplate;
  }

  public List<HnStory> readBestStories() {
    return readStories("beststories.json");
  }

  public List<HnStory> readTopStories() {
    return readStories("topstories.json");
  }

  private List<HnStory> readStories(String endpoint) {
    String _endpoint = endpoint.startsWith("/") ? endpoint : "/" + endpoint;
    var storyList = new ArrayList<HnStory>();
    var topStories = restTemplate.getForEntity(firebaseIoBaseUrl + _endpoint, List.class);

    if (topStories.getStatusCode() == HttpStatus.OK) {
      topStories.getBody().forEach(storyId -> {
        try {
          var story = restTemplate.getForEntity(firebaseIoBaseUrl + "/item/" + storyId.toString() + ".json", HnStory.class);
          storyList.add(story.getBody());
        } catch (HttpClientErrorException ex) {
          logger.error("Story cannot be read. story={}, status={}", storyId, ex.getStatusCode());
        } catch (Exception ex) {
          logger.error("Story cannot be read. story={}", storyId, ex);
        }
      });
    } else {
      logger.error("Top stories cannot be retrieved. status=" + topStories.getStatusCodeValue());
    }
    return storyList;
  }
}
