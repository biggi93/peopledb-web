package org.philipp.peopledbweb.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

// Jede dynamische HTML (in resources/templates) Seite, braucht ihren eigenen Controller
@Controller
@RequestMapping("/error")
public class ErrorController {
    // Sobald eine Excepiton geworfen wird, leitet Spring automatisch auf diese Seite (per default)

    @GetMapping
    public String getErrorPage() {
        return "/error";
    }
}
