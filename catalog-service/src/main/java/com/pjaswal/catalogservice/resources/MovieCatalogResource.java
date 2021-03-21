package com.pjaswal.catalogservice.resources;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.pjaswal.catalogservice.model.CatalogItem;
import com.pjaswal.catalogservice.model.Movie;
import com.pjaswal.catalogservice.model.UserRating;

@RestController
@RequestMapping("/catalog")
public class MovieCatalogResource {
	
    @Autowired
    private RestTemplate restTemplate;


	@GetMapping("/{userId}")
    public List<CatalogItem> getCatalog(@PathVariable("userId") String userId) {

        UserRating userRating = restTemplate.getForObject("http://data-service/ratingsData/user/" + userId, UserRating.class);

        return userRating.getRatings().stream()
                .map(rating -> {
                	//For each movie ID, call movie info service and get details
                    Movie movie = restTemplate.getForObject("http://info-service/movies/" + rating.getMovieId(), Movie.class);
                    //Put them all together.
                    return new CatalogItem(movie.getName(), movie.getDescription(), rating.getRating());
                })
                .collect(Collectors.toList());
    }
}
