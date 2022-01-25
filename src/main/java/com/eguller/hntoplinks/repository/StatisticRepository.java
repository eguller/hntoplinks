package com.eguller.hntoplinks.repository;

import com.eguller.hntoplinks.entities.StatisticEntity;
import com.eguller.hntoplinks.models.StatKey;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;

public interface StatisticRepository extends CrudRepository<StatisticEntity, Long> {
    @Modifying
    @Query("update statistic set stat_value=:statValue where stat_key=:statKey")
    void updateStatistic(StatKey statKey, String statValue);
}
