package com.eguller.hntoplinks.repository;

import com.eguller.hntoplinks.entities.Item;
import com.eguller.hntoplinks.models.Interval;
import com.eguller.hntoplinks.util.DbUtils;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Set;

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
            url,
            parent,
            dead
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
            :url,
            :parent,
            :dead
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
                url = :url,
                parent = :parent,
                dead = :dead
        """,
      itemToSqlParameterSource(item)
    );
  }

  private static MapSqlParameterSource itemToSqlParameterSource(Item item) {
    return new MapSqlParameterSource()
      .addValue("id", item.getId())
      .addValue("by", item.getBy())
      .addValue("descendants", item.getDescendants())
      .addValue("score", item.getScore())
      .addValue("time", item.getTime() != null ? Timestamp.from(Instant.ofEpochSecond(item.getTime())) : null)
      .addValue("title", item.getTitle())
      .addValue("type", item.getType())
      .addValue("url", item.getUrl())
      .addValue("parent", item.getParent())
      .addValue("dead", item.isDead());
  }

  public void batchSave(Set<Item> items) {
    var batchArgs = items.stream().map(item -> itemToSqlParameterSource(item)
    ).toArray(MapSqlParameterSource[]::new);
    template.batchUpdate(
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
            url,
            parent,
            dead
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
            :url,
            :parent,
            :dead
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
                url = :url,
                parent = :parent,
                dead = :dead
        """,
      batchArgs
    );
  }

  public List<Item> findByIntervalSortByScore(Interval interval) {
    return findByIntervalSortByScore(interval, 30, 0);
  }

  public List<Item> findByIntervalSortByScore(Interval interval, int page) {
    return findByIntervalSortByScore(interval, 30, 1);
  }

  public List<Item> findByIntervalSortByScore(Interval interval, int limit, int page) {
    return template.query(
      """
          SELECT
            id,
            by,
            descendants,
            score,
            time,
            title,
            type,
            url,
            parent,
            dead
          FROM
            items
          WHERE
            time BETWEEN :from AND :to
            AND type NOT IN ('comment')
          ORDER BY
            score DESC
          LIMIT :limit
          OFFSET :offset
        """,
      new MapSqlParameterSource()
        .addValue("from", interval.from())
        .addValue("to", interval.to())
        .addValue("limit", limit)
        .addValue("offset", DbUtils.pageToOffset(page, limit)),
      (rs, rowNum) -> new Item(
        rs.getLong("id"),
        rs.getString("by"),
        rs.getInt("descendants"),
        rs.getInt("score"),
        rs.getTimestamp("time").getTime(),
        rs.getString("title"),
        rs.getString("type"),
        rs.getString("url"),
        rs.getLong("parent"),
        rs.getBoolean("dead")
      )
    );
  }

  public List<Item> findAllSortByScore(int limit, int page) {
    return template.query(
      """
          SELECT
            id,
            by,
            descendants,
            score,
            time,
            title,
            type,
            url,
            parent,
            dead
          FROM
            items
          WHERE
            type NOT IN ('comment')
          ORDER BY
            score DESC
          LIMIT :limit
          OFFSET :offset
        """,
      new MapSqlParameterSource()
        .addValue("limit", limit)
        .addValue("offset", DbUtils.pageToOffset(page, limit))
      ,
      (rs, rowNum) -> new Item(
        rs.getLong("id"),
        rs.getString("by"),
        rs.getInt("descendants"),
        rs.getInt("score"),
        rs.getTimestamp("time").getTime(),
        rs.getString("title"),
        rs.getString("type"),
        rs.getString("url"),
        rs.getLong("parent"),
        rs.getBoolean("dead")
      )
    );
  }
}
