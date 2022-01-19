package com.eguller.hntoplinks.repository;

import com.eguller.hntoplinks.entities.StoryEntity;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface StoryRepository extends CrudRepository<StoryEntity, Long> {
    Optional<StoryEntity> findByHnid(long l);

    List<StoryEntity> findTop300ByDateAfterOrderByPointsDesc(LocalDateTime date);

    List<StoryEntity> findTop300ByOrderByPointsDesc();

    List<StoryEntity> findByHnidIn(List<Long> hnIdList);

    @Modifying
    @Query("""
            delete
            from item
            where item.id not in (
                (select id from item where item.postdate < :yesterday order by points desc limit 500)
                union all
                (select id from item where item.postdate < :lastWeek order by points desc limit 500)
                union all
                (select id from item where item.postdate < :lastMonth order by points desc limit 500)
                union all
                (select id from item where item.postdate < :lastYear order by points desc limit 500)
                union all
                (select id from item order by points desc limit 500)
            )
           """)
    int deleteExpiredStories(LocalDateTime yesterday, LocalDateTime lastWeek, LocalDateTime lastMonth, LocalDateTime lastYear);


}
