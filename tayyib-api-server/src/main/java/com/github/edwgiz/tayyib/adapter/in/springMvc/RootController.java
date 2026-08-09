package com.github.edwgiz.tayyib.adapter.in.springMvc;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.net.URI;

@Controller
public class RootController {

    private static final ResponseEntity<Void> DEFAULT_PAGE = ResponseEntity
            .status(HttpStatus.PERMANENT_REDIRECT)
            .location(URI.create("/calendar"))
            .build();


    @GetMapping("/")
    ResponseEntity<Void> redirectFromRoot() {
        return DEFAULT_PAGE;
    }
}
