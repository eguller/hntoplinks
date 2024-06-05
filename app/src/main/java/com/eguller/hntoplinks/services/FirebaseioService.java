package com.eguller.hntoplinks.services;

import com.eguller.hntoplinks.entities.Item;
import com.eguller.hntoplinks.models.HnStory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestTemplate;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

@Service
public class FirebaseioService {
  private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

  private final RestTemplate restTemplate;
  private final RestClient fireBaseRestClient;

  @Value("${hntoplinks.firebaseio-url}")
  private String firebaseIoBaseUrl;

  public FirebaseioService(RestTemplate restTemplate, RestClient fireBaseRestClient) {
    this.restTemplate = restTemplate;
    this.fireBaseRestClient = fireBaseRestClient;
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

  public Long getMaxItem() {
    var maxItem = fireBaseRestClient.get().uri("/maxitem.json").retrieve().body(Long.class);
    return maxItem;
  }

  public Item readItem(Long itemId) {
    var item = fireBaseRestClient.get().uri("/item/" + itemId + ".json").retrieve().body(Item.class);
    return item;
  }

  public List<Item> readItems(List<Long> itemIds) {
    try {
      var items = itemIds.stream().map(itemId -> readItem(itemId)).collect(Collectors.toList());
      return items;
    } catch (Exception ex) {
      ex.printStackTrace();
      return Collections.emptyList();
    }
  }

  @Async
  public Future<Item> readItemAsync(Long itemId) {
    var item = readItem(itemId);
    return CompletableFuture.completedFuture(item);
  }

  public List<Item> readBestStoriesNew() {
    var bestStoryIds = readBestStoryIds();
    var bestStories = readItems(bestStoryIds);
    return bestStories;
  }

  public List<Item> readTopStoriesNew() {
    var topStoryIds = readTopStoryIds();
    var topStories = readItems(topStoryIds);
    return topStories;
  }

  public List<Item> readNewStoriesNew() {
    var newStoryIds = readNewStoryIds();
    var newStories = readItems(newStoryIds);
    return newStories;
  }

  private List<Long> readBestStoryIds() {
    return readStoryIds("/beststories.json");
  }

  private List<Long> readTopStoryIds() {
    return readStoryIds("/topstories.json");
  }

  private List<Long> readNewStoryIds() {
    return readStoryIds("/newstories.json");
  }

  private List<Long> readStoryIds(String endpoint) {
    var storyIds = fireBaseRestClient.get().uri(endpoint).retrieve().body(new ParameterizedTypeReference<List<Long>>(){});
    return storyIds;
  }
}
