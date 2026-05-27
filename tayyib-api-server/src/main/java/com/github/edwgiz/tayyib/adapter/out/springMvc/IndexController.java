package com.github.edwgiz.tayyib.adapter.out.springMvc;

import com.github.edwgiz.tayyib.adapter.out.jte.model.Html;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller

public class IndexController {

    @GetMapping("/")
    public String view(Model model, HttpServletResponse response) {
        model.addAttribute("html", new Html("Hello World"));
        return "index";
    }

}
