package com.eguller.hntoplinks.config;

import java.sql.SQLException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.relational.core.mapping.event.BeforeConvertCallback;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import com.eguller.hntoplinks.entities.HnEntity;

@Component
public class AssignId implements BeforeConvertCallback<HnEntity> {
  @Autowired private JdbcTemplate jdbcTemplate;

  @Override
  public HnEntity onBeforeConvert(HnEntity aggregate) {
    if (aggregate.getId() == null) {
      Long id =
          jdbcTemplate.query(
              "SELECT nextval('hibernate_sequence')",
              rs -> {
                if (rs.next()) {
                  return rs.getLong(1);
                } else {
                  throw new SQLException(
                      "Unable to retrieve value from sequence hibernate_sequence.");
                }
              });
      aggregate.setId(id);
    }
    return aggregate;
  }
}
