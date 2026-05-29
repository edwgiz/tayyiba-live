package com.github.edwgiz.tayyib.adapter.in.springMvc;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Locale;

@Controller

public class IndexController {

    @GetMapping("/")
    public String get(Model model, Locale locale) {
        model.addAttribute("$", new Object());
        return "index";
    }
}
