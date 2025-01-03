package com.eguller.hntoplinks.controllers;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.annotation.RequestScope;

import com.eguller.hntoplinks.entities.Period;
import com.eguller.hntoplinks.entities.SubscriberEntity;
import com.eguller.hntoplinks.models.EmailTarget;
import com.eguller.hntoplinks.repository.SubscribersRepository;
import com.eguller.hntoplinks.services.SubscriptionService;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestScope
public class TestController {
  @Value("${hntoplinks.test-endpoint.enabled}")
  private boolean testEndpointEnabled;

  @Autowired private SubscriptionService subscriptionService;

  @Autowired private SubscribersRepository subscribersRepository;

  @PostMapping(
      value = "/test",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Void> test(@RequestBody TestRequest request) {
    if (!testEndpointEnabled) {
      return ResponseEntity.notFound().build(); // Returns 404
    }

    var period = Period.valueOf(request.getPeriod());

    var subscriber =
        subscribersRepository
            .findByEmailIgnoreCase(request.getEmail())
            .orElseGet(
                () -> {
                  log.info("Creating new subscriber with email: {}", request.getEmail());
                  var newSubscriber =
                      SubscriberEntity.builder()
                          .subscriberId(UUID.randomUUID().toString())
                          .email(request.getEmail())
                          .subscriptionDate(LocalDateTime.now().minusDays(10))
                          .build();
                  return subscribersRepository.save(newSubscriber);
                });

    var subscription = subscriber.createNewSubscription(period);
    subscription.setSubscriberId(subscriber.getId());

    // make subscription expired
    subscription.setNextSendDate(LocalDateTime.now().minusDays(1));

    var emailTarget = new EmailTarget(subscriber, subscription);
    subscriptionService.sendSubscriptionEmail(emailTarget);
    return ResponseEntity.ok().build();
  }

  @NoArgsConstructor
  @Data
  static class TestRequest {
    private String email;
    private String period;
  }
}
