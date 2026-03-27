package com.app.safetybackend.controller;

import com.app.safetybackend.entity.Feedback;
import com.app.safetybackend.service.FeedbackService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/feedback")
@CrossOrigin // allow Android app
public class FeedbackController {

    private final FeedbackService service;

    public FeedbackController(FeedbackService service) {
        this.service = service;
    }

    @PostMapping
    public Feedback save(@RequestBody Feedback feedback) {
        System.out.println("Origin: " + feedback.getOrigin());
        System.out.println("Message: " + feedback.getMessage());
        return service.save(feedback);
    }

    @GetMapping
    public List<Feedback> getAll() {
        return service.getAll();
    }
}