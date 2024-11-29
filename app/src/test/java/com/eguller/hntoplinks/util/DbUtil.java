package com.eguller.hntoplinks.util;

import java.util.HashMap;

import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.testcontainers.containers.PostgreSQLContainer;

public class DbUtil {
  public static void deleteSubscriber(
      NamedParameterJdbcTemplate namedParameterJdbcTemplate, String email) {
    var queryParams = new HashMap<String, String>();
    queryParams.put("email", email);
    namedParameterJdbcTemplate.update(
        "delete from subscription where subscriber_id in (select id from subscriber where email=:email)",
        queryParams);
    namedParameterJdbcTemplate.update("delete from subscriber where email=:email", queryParams);
  }

  public static void updateDatabaseProperties(
      PostgreSQLContainer postgres, DynamicPropertyRegistry registry) {
    registry.add("spring.datasource.url", postgres::getJdbcUrl);
    registry.add("spring.datasource.username", postgres::getUsername);
    registry.add("spring.datasource.password", postgres::getPassword);
    registry.add("spring.flyway.url", postgres::getJdbcUrl);
    registry.add("spring.flyway.user", postgres::getUsername);
    registry.add("spring.flyway.password", postgres::getPassword);
  }
}
