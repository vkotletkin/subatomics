package ru.kotletkin.aard.common.client;

import org.springframework.web.service.annotation.HttpExchange;

@HttpExchange(url = "/api/v1", contentType = "application/json")
public interface ArgoCDClient {

//    @GetExchange("/users/{id}")
//    User findById(@PathVariable Long id);
//
//    @PostExchange("/users")
//    User create(@RequestBody User user);
}
