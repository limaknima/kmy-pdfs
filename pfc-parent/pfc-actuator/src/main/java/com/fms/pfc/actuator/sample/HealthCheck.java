package com.fms.pfc.actuator.sample;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

//import com.fms.pfc.service.api.sample.MovieRatingService;

@Component
public class HealthCheck implements HealthIndicator {

//	private MovieRatingService movieRatingService;
//
//	@Autowired
//	public HealthCheck(MovieRatingService movieRatingService) {
//		super();
//		this.movieRatingService = movieRatingService;
//	}

	@Override
	public Health health() {

//		if (movieRatingService.getMovieRating("Up") == null) {
//			Health result = Health.down().withDetail("Casue", "OMDb API is not available!").build();
//			return result;
//		}

		Health build = Health.up().build();
		return build;
	}

}
