package com.eguller.hntoplinks.repository;

import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class CheckPointRepository {
  private final NamedParameterJdbcTemplate jdbcTemplate;
  public CheckPointRepository(NamedParameterJdbcTemplate jdbcTemplate){
    this.jdbcTemplate = jdbcTemplate;
  }
}
