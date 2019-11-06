package com.example.recycling.controller;

import com.example.recycling.service.RecyclingUserProvider;
import com.example.recycling.service.RolesService;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class HomeController {
    @GetMapping({"/", ""})
    public String index() {
        return "index";
    }

    @GetMapping("/signup")
    public String signUp() {
        return "signUp";
    }

    @GetMapping("/secure")
    @Secured(RolesService.AUTHENTICATED_USER)
    public ModelAndView secure() {
        ModelAndView result = new ModelAndView();
        result.setViewName("secure");
        result.addObject("user", RecyclingUserProvider.getUsername());
        return result;
    }
}
