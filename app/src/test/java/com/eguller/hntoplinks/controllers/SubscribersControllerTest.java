package com.eguller.hntoplinks.controllers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.UUID;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.testcontainers.containers.PostgreSQLContainer;

import com.eguller.hntoplinks.Application;
import com.eguller.hntoplinks.entities.Period;
import com.eguller.hntoplinks.entities.SubscriberEntity;
import com.eguller.hntoplinks.entities.SubscriptionEntity;
import com.eguller.hntoplinks.repository.SubscribersRepository;
import com.eguller.hntoplinks.repository.SubscriptionsRepository;
import com.eguller.hntoplinks.services.EmailService;
import com.eguller.hntoplinks.util.DbUtil;

@SpringBootTest(classes = {Application.class})
@ActiveProfiles({"local", "test"})
@TestPropertySource(properties = {"hntoplinks.captcha.enabled=false"})
@AutoConfigureMockMvc
class SubscribersControllerTest {

  @Autowired private MockMvc mockMvc;

  @Autowired private SubscribersRepository subscribersRepository;

  @Autowired private SubscriptionsRepository subscriptionsRepository;

  @MockBean private EmailService emailService;

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
    subscribersRepository.deleteAll();
    subscriptionsRepository.deleteAll();
  }

  @Test
  void shouldDisplaySubscriptionForm() throws Exception {
    mockMvc
        .perform(get("/subscribers"))
        .andExpect(status().isOk())
        .andExpect(model().attributeExists("page"))
        .andExpect(model().attributeExists("subscriptionForm"))
        .andExpect(view().name("subscribe"));
  }

  @Test
  void shouldCreateNewSubscription() throws Exception {
    mockMvc
        .perform(
            post("/subscribers")
                .param("email", "test@example.com")
                .param("selectedPeriods", Period.WEEKLY.name())
                .param("timeZone", "UTC"))
        .andExpect(status().isOk())
        .andExpect(
            model().attribute("page", hasProperty("content", hasProperty("success", is(true)))))
        .andExpect(view().name("subscribe"));

    var subscriber = subscribersRepository.findAll().iterator().next();
    assertThat(subscriber.getEmail()).isEqualTo("test@example.com");
    assertThat(subscriber.getSubscriptionList()).hasSize(1);
    assertThat(subscriber.getSubscriptionList().get(0).getPeriod()).isEqualTo(Period.WEEKLY);

    verify(emailService).sendSubscriptionEmail(subscriber);
  }

  @Test
  void shouldUpdateExistingSubscription() throws Exception {
    var subscriber = subscribersRepository.save(createSubscriber("test@example.com"));

    mockMvc
        .perform(
            post("/subscribers")
                .param("subscriberId", subscriber.getSubscriberId())
                .param("email", "updated@example.com")
                .param("selectedPeriods", Period.DAILY.name()))
        .andExpect(status().isOk())
        .andExpect(
            model().attribute("page", hasProperty("content", hasProperty("success", is(true)))))
        .andExpect(view().name("subscribe"));

    var updated = subscribersRepository.findById(subscriber.getId()).get();
    assertThat(updated.getEmail()).isEqualTo("updated@example.com");
    assertThat(updated.getSubscriptionList()).hasSize(1);
    assertThat(updated.getSubscriptionList().get(0).getPeriod()).isEqualTo(Period.DAILY);
  }

  @Test
  void shouldUnsubscribe() throws Exception {
    var subscriber = subscribersRepository.save(createSubscriber("test@example.com"));

    mockMvc
        .perform(
            get("/subscribers/{subscriberId}", subscriber.getSubscriberId())
                .param("action", "unsubscribe"))
        .andExpect(status().isOk())
        .andExpect(view().name("unsubscribe"));

    assertThat(subscribersRepository.findById(subscriber.getId())).isEmpty();
  }

  @Test
  void shouldShowModifySubscriptionPage() throws Exception {
    var subscriber = subscribersRepository.save(createSubscriber("test@example.com"));

    mockMvc
        .perform(get("/subscribers/{subscriberId}", subscriber.getSubscriberId()))
        .andExpect(status().isOk())
        .andExpect(
            model().attribute("subscriptionForm", hasProperty("email", is("test@example.com"))))
        .andExpect(view().name("subscribe"));
  }

  private SubscriberEntity createSubscriber(String email) {
    return SubscriberEntity.builder()
        .email(email)
        .subscriberId(UUID.randomUUID().toString())
        .subscriptionDate(LocalDateTime.now())
        .activationDate(LocalDateTime.now())
        .timeZone("UTC")
        .subscriptionList(
            Collections.singletonList(
                SubscriptionEntity.builder()
                    .nextSendDate(LocalDateTime.now().plus(1, ChronoUnit.WEEKS))
                    .period(Period.WEEKLY)
                    .build()))
        .build();
  }
}
