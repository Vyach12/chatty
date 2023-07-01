package ru.gusarov.messenger.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MessengerController {

    @GetMapping("/hello")
    public String getHello() {
        return "hello";
    }
}
