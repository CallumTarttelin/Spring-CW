package com.example.recycling.controller;

import com.example.recycling.service.ConstantsService;
import com.example.recycling.service.UserProvider;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

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

    @GetMapping("/signup")
    public String signUp() {
        return "signUp";
    }

    @GetMapping("/secure")
    @Secured(ConstantsService.AUTHENTICATED_USER)
    public ModelAndView secure() {
        ModelAndView result = new ModelAndView();
        result.setViewName("secure");
        result.addObject("user", UserProvider.getUsername());
        return result;
    }
}
