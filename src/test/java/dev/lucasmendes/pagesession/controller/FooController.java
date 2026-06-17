package dev.lucasmendes.pagesession.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

import dev.lucasmendes.pagesession.annotation.PageSessionAttributes;

@Controller
@PageSessionAttributes("items")
public class FooController {

    @GetMapping("/foo/set")
    public String setFoo(Model model) {
        model.addAttribute("items", "value-from-foo");
        return "view";
    }

    @GetMapping("/foo/get")
    public String getFoo(@ModelAttribute("items") String items) {
        return "view";
    }
}
