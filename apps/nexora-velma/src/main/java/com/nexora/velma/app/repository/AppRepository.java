package com.nexora.velma.app.repository;

import com.nexora.velma.app.model.App;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface AppRepository extends MongoRepository<App, String> {

  Optional<App> findByKey(String key);

  boolean existsByKey(String key);

  void deleteByKey(String key);
}
