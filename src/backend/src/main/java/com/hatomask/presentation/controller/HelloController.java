package com.hatomask.presentation.controller;

import com.hatomask.presentation.dto.HelloResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/hello")
public class HelloController {

    @GetMapping
    public ResponseEntity<HelloResponse> hello() {
        HelloResponse response = new HelloResponse("Hello, World from HatoMask Backend!");
        return ResponseEntity.ok(response);
    }
}
