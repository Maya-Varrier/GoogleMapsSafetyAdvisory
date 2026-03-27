package com.app.safetybackend.service;

import com.app.safetybackend.entity.Feedback;
import com.app.safetybackend.repository.FeedbackRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FeedbackService {

    private final FeedbackRepository repo;

    public FeedbackService(FeedbackRepository repo) {
        this.repo = repo;
    }

    public Feedback save(Feedback feedback) {
        return repo.save(feedback);
    }

    public List<Feedback> getAll() {
        return repo.findAll();
    }
}