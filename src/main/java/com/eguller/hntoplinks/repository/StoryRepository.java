package com.eguller.hntoplinks.repository;

import com.eguller.hntoplinks.entities.StoryEntity;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Repository
public interface StoryRepository extends CrudRepository<StoryEntity, Long> {
    Optional<StoryEntity> findByHnid(Long hnId);
    List<StoryEntity> findTop300ByDateAfterOrderByPointsDesc(LocalDateTime date);
    List<StoryEntity> findTop300ByOrderByPointsDesc();
    List<StoryEntity> findByHnidIn(List<Long> hnIdList);

}
