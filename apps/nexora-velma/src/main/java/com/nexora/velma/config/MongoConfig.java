package com.nexora.velma.config;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.SimpleMongoClientDatabaseFactory;

@Configuration
public class MongoConfig {

  @Bean
  MongoClient mongoClient(@Value("${spring.data.mongodb.uri}") String mongoUri) {
    return MongoClients.create(mongoUri);
  }

  @Bean
  SimpleMongoClientDatabaseFactory mongoDatabaseFactory(
      MongoClient mongoClient, @Value("${spring.data.mongodb.database}") String database) {
    return new SimpleMongoClientDatabaseFactory(mongoClient, database);
  }
}
