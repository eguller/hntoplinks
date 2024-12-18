package com.eguller.hntoplinks.repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.apache.commons.text.StringSubstitutor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import com.eguller.hntoplinks.entities.Item;
import com.eguller.hntoplinks.entities.SortType;
import com.eguller.hntoplinks.models.Interval;
import com.eguller.hntoplinks.util.DbUtils;

@Repository
public class ItemsRepository {
  private static final int MIN_UPVOTES = 1000;
  private static final int MIN_COMMENTS = 700;
  private final NamedParameterJdbcTemplate template;

  public ItemsRepository(NamedParameterJdbcTemplate jdbcTemplate) {
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
        itemToSqlParameterSource(item));
  }

  private static MapSqlParameterSource itemToSqlParameterSource(Item item) {
    return new MapSqlParameterSource()
        .addValue("id", item.getId())
        .addValue("by", item.getBy())
        .addValue("descendants", item.getDescendants())
        .addValue("score", item.getScore())
        .addValue(
            "time",
            item.getTime() != null ? Timestamp.from(Instant.ofEpochSecond(item.getTime())) : null)
        .addValue("title", item.getTitle())
        .addValue("type", item.getType())
        .addValue("url", item.getUrl())
        .addValue("parent", item.getParent())
        .addValue("dead", item.isDead());
  }

  public void batchSave(Set<Item> items) {
    var batchArgs =
        items.stream()
            .map(item -> itemToSqlParameterSource(item))
            .toArray(MapSqlParameterSource[]::new);
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
        batchArgs);
  }

  public List<Item> findByInterval(Interval interval) {
    return findByInterval(interval, SortType.UPVOTES, 30, 0);
  }

  public List<Item> findByInterval(Interval interval, int page) {
    return findByInterval(interval, SortType.UPVOTES, 30, 1);
  }

  public List<Item> findByInterval(Interval interval, SortType sortBy, int limit, int page) {
    var queryTemplate =
        """
        SELECT
          *
        FROM
          items
        WHERE
          time BETWEEN :from AND :to
          AND type NOT IN ('comment', 'pollopt')
        ORDER BY
          ${firstSortCriteria} DESC,
          ${secondSortCriteria} DESC
        LIMIT :limit
        OFFSET :offset
      """;

    var sortCriterias = getSortyTypeColumnName(sortBy);
    var values = new HashMap<String, String>();
    values.put("firstSortCriteria", sortCriterias[0]);
    values.put("secondSortCriteria", sortCriterias[1]);
    var query = StringSubstitutor.replace(queryTemplate, values);
    return template.query(
        query,
        new MapSqlParameterSource()
            .addValue("from", interval.from())
            .addValue("to", interval.to())
            .addValue("limit", limit)
            .addValue("offset", DbUtils.pageToOffset(page, limit)),
        (rs, rowNum) -> resultSetToItem(rs));
  }

  public List<Item> findByYear(int year, SortType sortBy, int limit, int page) {
    var queryTemplate =
        """
        SELECT
          *
        FROM
          items
        WHERE
          year = :year
          AND type NOT IN ('comment', 'pollopt')
        ORDER BY
          ${firstSortCriteria} DESC,
          ${secondSortCriteria} DESC
        LIMIT :limit
        OFFSET :offset
      """;

    var sortCriterias = getSortyTypeColumnName(sortBy);
    var values = new HashMap<String, String>();
    values.put("firstSortCriteria", sortCriterias[0]);
    values.put("secondSortCriteria", sortCriterias[1]);
    var query = StringSubstitutor.replace(queryTemplate, values);
    return template.query(
        query,
        new MapSqlParameterSource()
            .addValue("year", year)
            .addValue("limit", limit)
            .addValue("offset", DbUtils.pageToOffset(page, limit)),
        (rs, rowNum) -> resultSetToItem(rs));
  }

  public List<Item> findByMonth(int year, int month, SortType sort, int limit, int page) {
    var queryTemplate =
        """
        SELECT
          *
        FROM
          items
        WHERE
          year = :year
          AND month = :month
          AND type NOT IN ('comment', 'pollopt')
        ORDER BY
          ${firstSortCriteria} DESC,
          ${secondSortCriteria} DESC
        LIMIT :limit
        OFFSET :offset
      """;

    var sortCriterias = getSortyTypeColumnName(sort);
    var values = new HashMap<String, String>();
    values.put("firstSortCriteria", sortCriterias[0]);
    values.put("secondSortCriteria", sortCriterias[1]);
    var query = StringSubstitutor.replace(queryTemplate, values);
    return template.query(
        query,
        new MapSqlParameterSource()
            .addValue("year", year)
            .addValue("month", month)
            .addValue("limit", limit)
            .addValue("offset", DbUtils.pageToOffset(page, limit)),
        (rs, rowNum) -> resultSetToItem(rs));
  }

  public List<Item> findAll(SortType sortBy, int limit, int page) {
    var queryTemplate =
        """
        SELECT
          *
        FROM
          items
        WHERE
          ${sortByColumn} > '${minValue}'
          AND type NOT IN ('comment', 'pollopt')
        ORDER BY
          ${firstSortCriteria} DESC,
          ${secondSortCriteria} DESC
        LIMIT :limit
        OFFSET :offset
      """;

    var sortCriterias = getSortyTypeColumnName(sortBy);
    var values = new HashMap<String, String>();
    values.put("firstSortCriteria", sortCriterias[0]);
    values.put("secondSortCriteria", sortCriterias[1]);

    var sortByColumn = getSortByColumn(sortBy);
    var minValue = getMinValueBySortBy(sortBy);

    values.put("sortByColumn", sortByColumn);
    values.put("minValue", String.valueOf(minValue));

    var query = StringSubstitutor.replace(queryTemplate, values);

    return template.query(
        query,
        new MapSqlParameterSource()
            .addValue("limit", limit)
            .addValue("offset", DbUtils.pageToOffset(page, limit)),
        (rs, rowNum) -> resultSetToItem(rs));
  }

  private static Item resultSetToItem(ResultSet rs) throws SQLException {
    return new Item(
        rs.getLong("id"),
        rs.getString("by"),
        rs.getInt("descendants"),
        rs.getInt("score"),
        rs.getTimestamp("time").getTime(),
        rs.getString("title"),
        rs.getString("type"),
        rs.getString("url"),
        rs.getLong("parent"),
        rs.getBoolean("dead"));
  }

  private String[] getSortyTypeColumnName(SortType sortBy) {
    return switch (sortBy) {
      case UPVOTES -> new String[] {"score", "descendants"};
      case COMMENTS -> new String[] {"descendants", "score"};
    };
  }

  private String getSortByColumn(SortType sortBy) {
    return switch (sortBy) {
      case UPVOTES -> "score";
      case COMMENTS -> "descendants";
    };
  }

  private int getMinValueBySortBy(SortType sortBy) {
    return switch (sortBy) {
      case UPVOTES -> MIN_UPVOTES;
      case COMMENTS -> MIN_COMMENTS;
    };
  }

  public void deleteAll() {
    template.update("DELETE FROM items", new MapSqlParameterSource());
  }
}
