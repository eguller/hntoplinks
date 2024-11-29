package com.eguller.hntoplinks.controllers;

import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.testcontainers.containers.PostgreSQLContainer;

import com.eguller.hntoplinks.Application;
import com.eguller.hntoplinks.entities.Item;
import com.eguller.hntoplinks.repository.ItemsRepository;
import com.eguller.hntoplinks.util.DbUtil;

@SpringBootTest(classes = {Application.class})
@ActiveProfiles({"local", "test"})
@AutoConfigureMockMvc
class StoriesControllerTest {

  @Autowired private MockMvc mockMvc;

  @Autowired private ItemsRepository itemsRepository;

  @Autowired private WebApplicationContext context;

  static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:13.5");

  @BeforeAll
  static void beforeAll() {
    postgres.start();
  }

  @AfterAll
  static void afterAll() {
    postgres.stop();
  }

  @DynamicPropertySource
  static void configureProperties(DynamicPropertyRegistry registry) {
    DbUtil.updateDatabaseProperties(postgres, registry);
  }

  @BeforeEach
  void setUp() {
    mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
    itemsRepository.deleteAll();
  }

  @Test
  void shouldShowTodayStories() throws Exception {
    createStories();

    mockMvc
        .perform(get("/today").param("sort", "upvotes"))
        .andExpect(status().isOk())
        .andExpect(
            model()
                .attribute("page", hasProperty("navigation", hasProperty("activeMenu", is("day")))))
        .andExpect(view().name("index"));
  }

  @Test
  void shouldShowWeeklyStories() throws Exception {
    createStories();

    mockMvc
        .perform(get("/week").param("sort", "upvotes"))
        .andExpect(status().isOk())
        .andExpect(
            model()
                .attribute(
                    "page", hasProperty("navigation", hasProperty("activeMenu", is("week")))))
        .andExpect(view().name("index"));
  }

  @Test
  void shouldShowMonthlyStories() throws Exception {
    createStories();

    mockMvc
        .perform(get("/month").param("sort", "upvotes"))
        .andExpect(status().isOk())
        .andExpect(
            model()
                .attribute(
                    "page", hasProperty("navigation", hasProperty("activeMenu", is("month")))))
        .andExpect(view().name("index"));
  }

  @Test
  void shouldShowStoriesByYear() throws Exception {
    createStories();

    mockMvc
        .perform(get("/stories/2024"))
        .andExpect(status().isOk())
        .andExpect(model().attribute("page", hasProperty("selectedYear", is(2024))))
        .andExpect(
            model()
                .attribute(
                    "page", hasProperty("navigation", hasProperty("breadcrumbs", hasSize(1)))))
        .andExpect(view().name("index"));
  }

  @Test
  void shouldShowStoriesByMonth() throws Exception {
    createStories();

    mockMvc
        .perform(get("/stories/2024/11"))
        .andExpect(status().isOk())
        .andExpect(model().attribute("page", hasProperty("selectedYear", is(2024))))
        .andExpect(model().attribute("page", hasProperty("selectedMonth", is(11))))
        .andExpect(
            model()
                .attribute(
                    "page", hasProperty("navigation", hasProperty("breadcrumbs", hasSize(2)))))
        .andExpect(view().name("index"));
  }

  @Test
  void shouldShowStoriesByDay() throws Exception {
    createStories();

    mockMvc
        .perform(get("/stories/2024/11/15"))
        .andExpect(status().isOk())
        .andExpect(model().attribute("page", hasProperty("selectedYear", is(2024))))
        .andExpect(model().attribute("page", hasProperty("selectedMonth", is(11))))
        .andExpect(
            model()
                .attribute(
                    "page", hasProperty("navigation", hasProperty("breadcrumbs", hasSize(3)))))
        .andExpect(view().name("index"));
  }

  private void createStories() {
    var story =
        Item.builder()
            .id(System.currentTimeMillis())
            .title("Test Story")
            .score(100)
            .time(System.currentTimeMillis())
            .by("testuser")
            .descendants(10)
            .build();
    itemsRepository.save(story);
  }
}
