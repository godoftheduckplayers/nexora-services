package com.nexora.fred;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients
@SpringBootApplication
public class NexoraFredApplication {

  public static void main(String[] args) {
    SpringApplication.run(NexoraFredApplication.class, args);
  }
}
