package dev.lucasmendes.pagesession.controller;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

import dev.lucasmendes.pagesession.annotation.PageSessionAttributes;

@Controller
@PageSessionAttributes("items")
public class BarController {

    @GetMapping("/bar/set")
    public String setBar(Model model) {
        model.addAttribute("items", "value-from-bar");
        return "view";
    }

    @GetMapping("/bar/get")
    public String getBar(@ModelAttribute("items") String items) {
        return "view";
    }
}
