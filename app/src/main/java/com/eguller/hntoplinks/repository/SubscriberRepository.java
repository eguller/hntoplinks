package com.eguller.hntoplinks.repository;

import com.eguller.hntoplinks.entities.SubscriberEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface SubscriberRepository extends CrudRepository<SubscriberEntity, Long> {
  Optional<SubscriberEntity> findBySubsUUID(String subscriptionId);

  Optional<SubscriberEntity> findByEmail(String email);
}
