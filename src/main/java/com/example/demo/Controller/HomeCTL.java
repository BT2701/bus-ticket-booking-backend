package com.example.demo.Controller;


import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
//@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
public class HomeCTL {
    @GetMapping("/api/homepage")
    public String home() {
        return "Hello World";
    }
}
