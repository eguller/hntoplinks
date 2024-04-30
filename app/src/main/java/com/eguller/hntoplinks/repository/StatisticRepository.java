package com.eguller.hntoplinks.repository;

import com.eguller.hntoplinks.entities.StatisticEntity;
import com.eguller.hntoplinks.models.StatKey;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class StatisticRepository {
  private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

  public StatisticRepository(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
    this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
  }

  public void updateStatistic(StatKey statKey, String statValue) {
    namedParameterJdbcTemplate.update("""
        UPDATE
         statistic
        SET
          stat_value = :statValue
        WHERE
          stat_key = :statKey
          """,
      new MapSqlParameterSource()
        .addValue("statKey", statKey)
        .addValue("statValue", statValue)
    );
  }

  public void saveAll(List<StatisticEntity> statisticEntityList) {
    namedParameterJdbcTemplate.batchUpdate(
      """
        INSERT INTO statistic (stat_key, stat_value)
        VALUES (:statKey, :statValue)
        ON CONFLICT (stat_key)
        DO
          UPDATE
            SET
              stat_value = :statValue
      """,
      statisticEntityList.stream()
        .map(statisticEntity -> new MapSqlParameterSource()
          .addValue("statKey", statisticEntity.getStatKey())
          .addValue("statValue", statisticEntity.getStatValue())
        )
        .toArray(MapSqlParameterSource[]::new)
    );
  }

  public List<StatisticEntity> findAll() {
    var result = namedParameterJdbcTemplate.query(
      """
        SELECT
         *
        FROM
          statistic
      """,
      (rs, rowNum) -> new StatisticEntity(
        rs.getLong("id"),
        rs.getString("stat_key"),
        rs.getString("stat_value")
      )
    );
    return result;
  }
}
