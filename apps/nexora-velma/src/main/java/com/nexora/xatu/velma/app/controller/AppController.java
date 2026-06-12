package com.nexora.xatu.velma.app.controller;

import com.nexora.xatu.velma.app.dto.request.CreateAppRequest;
import com.nexora.xatu.velma.app.dto.request.UpdateAppRequest;
import com.nexora.xatu.velma.app.dto.response.AppResponse;
import com.nexora.xatu.velma.app.service.AppService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/apps")
public class AppController {

  private final AppService appService;

  public AppController(AppService appService) {
    this.appService = appService;
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public AppResponse create(@Valid @RequestBody CreateAppRequest request) {
    return appService.create(request);
  }

  @GetMapping
  public List<AppResponse> findAll() {
    return appService.findAll();
  }

  @GetMapping("/{key}")
  public AppResponse findByKey(@PathVariable String key) {
    return appService.findByKey(key);
  }

  @PutMapping("/{key}")
  public AppResponse update(
      @PathVariable String key, @Valid @RequestBody UpdateAppRequest request) {
    return appService.update(key, request);
  }

  @PatchMapping("/{key}/enable")
  public AppResponse enable(@PathVariable String key) {
    return appService.enable(key);
  }

  @PatchMapping("/{key}/disable")
  public AppResponse disable(@PathVariable String key) {
    return appService.disable(key);
  }

  @DeleteMapping("/{key}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void delete(@PathVariable String key) {
    appService.delete(key);
  }
}
