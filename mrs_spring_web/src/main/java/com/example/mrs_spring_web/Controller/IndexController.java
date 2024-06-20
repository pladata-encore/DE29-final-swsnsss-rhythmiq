package com.example.mrs_spring_web.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class IndexController {
    @GetMapping("/")
    public String index(@RequestParam(value = "error", required = false) String error,
    @RequestParam(value = "exception", required = false) String exception, Model model) throws Exception{
        model.addAttribute("error", error);
        model.addAttribute("exception", exception);
        return "index";
    }
}
