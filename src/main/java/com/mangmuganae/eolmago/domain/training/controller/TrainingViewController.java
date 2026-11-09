package com.mangmuganae.eolmago.domain.training.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class TrainingViewController {

  @GetMapping("/training")
  public String index() {
    return "training/index";
  }

}
