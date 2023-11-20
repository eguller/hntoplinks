package com.eguller.hntoplinks.repository;

import com.eguller.hntoplinks.entities.Item;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;

@Repository
public class ItemsRepository {
  private final NamedParameterJdbcTemplate template;
  public ItemsRepository(NamedParameterJdbcTemplate jdbcTemplate){
    this.template = jdbcTemplate;
  }

  public void save(Item item) {
    template.update(
      """
        insert into items(id, hnid, story_type, created_at, url, score, title, comment_count, version)
        values(:id, :hnid, :storyType, :createdAt, :url, :score, :title, :commentCount, :version)
        on conflict (hnid) do update set
          story_type = :storyType,
          created_at = :createdAt,
          url = :url,
          score = :score,
          title = :title,
          comment_count = :commentCount,
          version = :version + 1
      """, Map.ofEntries(
        Map.entry("id", item.id()),
        Map.entry("hnid", item.hnid()),
        Map.entry("storyType", item.storyType()),
        Map.entry("createdAt", item.createdAt()),
        Map.entry("url", item.url()),
        Map.entry("score", item.score()),
        Map.entry("title", item.title()),
        Map.entry("commentCount", item.commentCount())
      ));
  }
}
