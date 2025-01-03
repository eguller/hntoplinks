package com.eguller.hntoplinks.repository;

import java.util.Map;

import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class CheckPointRepository {
  private final NamedParameterJdbcTemplate jdbcTemplate;

  public CheckPointRepository(NamedParameterJdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }

  public Long getLastItem() {
    var lastItem =
        jdbcTemplate.queryForObject(
            """
      SELECT
        checkpoint
      FROM
        checkpoints
      WHERE
        name='stories'
      """,
            Map.ofEntries(),
            Long.class);
    return lastItem;
  }

  public void saveStoriesCheckPoint(Long checkpoint) {
    jdbcTemplate.update(
        """
        UPDATE
          checkpoints
        SET
          checkpoint = :checkpoint,
          last_updated = now()
        WHERE
          name='stories'
        """,
        Map.ofEntries(Map.entry("checkpoint", checkpoint)));
  }
}
