package com.eguller.hntoplinks.repository;

import com.eguller.hntoplinks.entities.Item;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.time.Instant;

@Repository
public class ItemRepository {
  private final NamedParameterJdbcTemplate template;

  public ItemRepository(NamedParameterJdbcTemplate jdbcTemplate) {
    this.template = jdbcTemplate;
  }

  public void save(Item item) {

    template.update(
      """
          INSERT INTO items
          (
            id,
            by,
            descendants,
            score,
            time,
            title,
            type,
            url
          )
          VALUES
          (
            :id,
            :by,
            :descendants,
            :score,
            :time,
            :title,
            :type,
            :url
          )
          ON CONFLICT (id)
          DO
            UPDATE
              SET
                by = :by,
                descendants = :descendants,
                score = :score,
                time = :time,
                title = :title,
                type = :type,
                url = :url
        """,
      new MapSqlParameterSource()
        .addValue("id", item.getId())
        .addValue("by", item.getBy())
        .addValue("descendants", item.getDescendants() == null ? 0 : item.getDescendants())
        .addValue("score", item.getScore() == null ? 0 : item.getScore())
        .addValue("time", Timestamp.from(Instant.ofEpochSecond(item.getTime())))
        .addValue("title", item.getTitle())
        .addValue("type", item.getType())
        .addValue("url", item.getUrl())
    );
  }
}
