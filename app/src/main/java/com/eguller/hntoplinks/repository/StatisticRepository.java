package com.eguller.hntoplinks.repository;

import java.util.List;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import com.eguller.hntoplinks.entities.StatisticEntity;
import com.eguller.hntoplinks.models.StatKey;

@Repository
public class StatisticRepository {
  private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

  public StatisticRepository(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
    this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
  }

  public void updateStatistic(StatKey statKey, String statValue) {
    namedParameterJdbcTemplate.update(
        """
                        UPDATE
                         statistic
                        SET
                          stat_value = :statValue
                        WHERE
                          stat_key = :statKey
                          """,
        new MapSqlParameterSource().addValue("statKey", statKey).addValue("statValue", statValue));
  }

  public void saveAll(List<StatisticEntity> statisticEntityList) {
    var entitiesWithId =
        statisticEntityList.stream()
            .filter(statisticEntity -> statisticEntity.getId() != null)
            .toList();
    var entitiesWithoutId =
        statisticEntityList.stream()
            .filter(statisticEntity -> statisticEntity.getId() == null)
            .toList();

    namedParameterJdbcTemplate.batchUpdate(
        """
                          INSERT INTO statistic (id, stat_key, stat_value)
                          VALUES (nextval('hibernate_sequence'), :statKey, :statValue)
                          ON CONFLICT (stat_key)
                          DO
                            UPDATE
                              SET
                                stat_value = :statValue
                        """,
        entitiesWithoutId.stream()
            .map(
                statisticEntity ->
                    new MapSqlParameterSource()
                        .addValue("statKey", statisticEntity.getStatKey())
                        .addValue("statValue", statisticEntity.getStatValue()))
            .toArray(MapSqlParameterSource[]::new));

    namedParameterJdbcTemplate.batchUpdate(
        """
                          UPDATE
                            statistic
                          SET
                            stat_value = :value
                          WHERE
                            stat_key = :key
                        """,
        entitiesWithId.stream()
            .map(
                statisticEntity ->
                    new MapSqlParameterSource()
                        .addValue("key", statisticEntity.getStatKey())
                        .addValue("value", statisticEntity.getStatValue()))
            .toArray(MapSqlParameterSource[]::new));
  }

  public List<StatisticEntity> findAll() {
    var result =
        namedParameterJdbcTemplate.query(
            """
                          SELECT
                           *
                          FROM
                            statistic
                        """,
            (rs, rowNum) ->
                new StatisticEntity(
                    rs.getLong("id"), rs.getString("stat_key"), rs.getString("stat_value")));
    return result;
  }
}
