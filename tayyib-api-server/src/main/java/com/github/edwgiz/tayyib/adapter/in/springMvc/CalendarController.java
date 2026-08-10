package com.github.edwgiz.tayyib.adapter.in.springMvc;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;


@Controller
public class CalendarController {

    @GetMapping("/calendar")
    public String get() {
        return "calendar";
    }
}
