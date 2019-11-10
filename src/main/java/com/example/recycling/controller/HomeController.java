package com.example.recycling.controller;

import com.example.recycling.service.UserProvider;
import com.example.recycling.service.ConstantsService;
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
    @Secured(ConstantsService.AUTHENTICATED_USER)
    public ModelAndView secure() {
        ModelAndView result = new ModelAndView();
        result.setViewName("secure");
        result.addObject("user", UserProvider.getUsername());
        return result;
    }
}
