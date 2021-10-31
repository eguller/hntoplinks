package com.eguller.hntoplinks.repository;

import com.eguller.hntoplinks.entities.StoryEntity;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.Repository;

public interface Sequence extends Repository<StoryEntity, Long> {
    @Query(value = "select hibernate_sequence.nextval from dual")
    Long getNextId();
}
