package com.eguller.hntoplinks.util;

import org.testcontainers.containers.PostgreSQLContainer;

public class HntoplinksPostgresqlContainer extends PostgreSQLContainer<HntoplinksPostgresqlContainer> {
  private static final String IMAGE_VERSION = "postgres:13.5";
  private static HntoplinksPostgresqlContainer container;

  private HntoplinksPostgresqlContainer() {
    super(IMAGE_VERSION);
  }

  public static HntoplinksPostgresqlContainer getInstance() {
    if (container == null) {
      container = new HntoplinksPostgresqlContainer();
    }
    return container;
  }

  @Override
  public void start() {
    super.start();
    System.setProperty("DB_URL", container.getJdbcUrl());
    System.setProperty("DB_USERNAME", container.getUsername());
    System.setProperty("DB_PASSWORD", container.getPassword());
  }

  @Override
  public void stop() {
    // do nothing, JVM handles shut down
  }
}
