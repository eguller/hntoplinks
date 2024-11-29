package com.eguller.hntoplinks.services;

import java.lang.invoke.MethodHandles;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestTemplate;

import com.eguller.hntoplinks.entities.Item;

@Service
public class FirebaseioService {
  private static final Logger logger =
      LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

  private final RestTemplate restTemplate;
  private final RestClient fireBaseRestClient;

  @Value("${hntoplinks.firebaseio-url}")
  private String firebaseIoBaseUrl;

  public FirebaseioService(RestTemplate restTemplate, RestClient fireBaseRestClient) {
    this.restTemplate = restTemplate;
    this.fireBaseRestClient = fireBaseRestClient;
  }

  public Long getMaxItem() {
    var maxItem = fireBaseRestClient.get().uri("/maxitem.json").retrieve().body(Long.class);
    return maxItem;
  }

  public Item readItem(Long itemId) {
    var item =
        fireBaseRestClient.get().uri("/item/" + itemId + ".json").retrieve().body(Item.class);
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

  public List<Item> readBestStories() {
    var bestStoryIds = readBestStoryIds();
    var bestStories = readItems(bestStoryIds);
    return bestStories;
  }

  public List<Item> readTopStories() {
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
    var storyIds =
        fireBaseRestClient
            .get()
            .uri(endpoint)
            .retrieve()
            .body(new ParameterizedTypeReference<List<Long>>() {});
    return storyIds;
  }
}
