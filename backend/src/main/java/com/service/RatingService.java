package com.service;

import com.entity.Rating;
import com.entity.User;
import com.repository.RatingRepository;
import com.repository.UserRepository;
import com.request.RatingRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class RatingService {

    @Autowired
    private RatingRepository ratingRepository;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserRepository userRepository;

    public Double calculateRating(Integer productId) {
        List<Rating> ratings = ratingRepository.findByProductId(productId);
        Double ratingSum = 0.0;
        for (Rating rating : ratings) {
            ratingSum += rating.getRating();
        }
        return Math.round((ratingSum / ratings.size()) * 10.0) / 10.0;
    }

    public List<Rating> findByProductId(Integer productId) {
        return ratingRepository.findByProductId(productId);
    }

    public Rating submitRating(String token, RatingRequest request) {
        String username = jwtService.extractUsername(token);
        User user = userRepository.findByUsername(username).get();
        Rating rating = new Rating();
        rating.setUserId(user.getUserId());
        rating.setProductId(request.getProductId());
        rating.setEmail(user.getEmail());
        rating.setRating(request.getRating());
        rating.setRateComment(request.getRatingComment());
        rating.setRatingDate(LocalDateTime.now());
        return ratingRepository.save(rating);
    }
}
