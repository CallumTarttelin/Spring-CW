package com.example.recycling.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class HomeController {

    @RequestMapping("/api/**")
    public ResponseEntity<Void> invalidApi() {
        return ResponseEntity.notFound().build();
    }

    @GetMapping(value = {"/{regex:\\w+}", "/**/{regex:\\w+}"})
    public String catch404() {
        return "forward:/";
    }

}
