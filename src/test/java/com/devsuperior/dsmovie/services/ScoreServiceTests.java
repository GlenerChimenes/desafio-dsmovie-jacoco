package com.devsuperior.dsmovie.services;

import static org.mockito.ArgumentMatchers.any;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.devsuperior.dsmovie.dto.MovieDTO;
import com.devsuperior.dsmovie.dto.ScoreDTO;
import com.devsuperior.dsmovie.entities.MovieEntity;
import com.devsuperior.dsmovie.entities.ScoreEntity;
import com.devsuperior.dsmovie.entities.ScoreEntityPK;
import com.devsuperior.dsmovie.entities.UserEntity;
import com.devsuperior.dsmovie.repositories.MovieRepository;
import com.devsuperior.dsmovie.repositories.ScoreRepository;
import com.devsuperior.dsmovie.services.exceptions.ResourceNotFoundException;
import com.devsuperior.dsmovie.tests.MovieFactory;
import com.devsuperior.dsmovie.tests.ScoreFactory;
import com.devsuperior.dsmovie.tests.UserFactory;

import jakarta.persistence.EntityNotFoundException;

@ExtendWith(SpringExtension.class)
public class ScoreServiceTests {
	
	@InjectMocks
	private ScoreService service;
	
	@Mock
	private UserService userService;
	
	@Mock
	private MovieRepository movieRepository;
	
	@Mock
	private ScoreRepository scoreRepository;
	
	private long existingId;
	private long nonExistingId;
	
	private MovieEntity movie;
	private ScoreDTO scoreDTO;
	
	private ScoreEntity scoreEntity;
	
	private UserEntity admin;
	
	@BeforeEach
	void setup() throws Exception {
		existingId = 1L;
		nonExistingId = 300L;

		scoreEntity = ScoreFactory.createScoreEntity();
	    scoreDTO = ScoreFactory.createScoreDTO();
		movie = MovieFactory.createMovieEntity();
		//movieDTO = MovieFactory.createMovieDTO();
		admin = UserFactory.createUserEntity();

		Mockito.when(movieRepository.findById(existingId)).thenReturn(Optional.of(movie));
		Mockito.when(movieRepository.findById(nonExistingId)).thenReturn(Optional.empty());

		Mockito.when(movieRepository.save(any())).thenReturn(movie);
		
		Mockito.when(movieRepository.saveAndFlush(any())).thenReturn(scoreEntity);

		Mockito.when(movieRepository.getReferenceById(nonExistingId)).thenThrow(EntityNotFoundException.class);
		
		Mockito.when(userService.authenticated()).thenReturn(admin);
		
	}
	
	@Test
	public void saveScoreShouldReturnMovieDTO() {
		
		MovieDTO result = service.saveScore(scoreDTO);
		
		Assertions.assertNotNull(result);
		Assertions.assertEquals(result.getId(), existingId);
		
	}
	
	@Test
	public void saveScoreShouldThrowResourceNotFoundExceptionWhenNonExistingMovieId() {
		
		movie.setId(nonExistingId);
		ScoreEntityPK pk = new ScoreEntityPK();
		pk.setMovie(movie);
		scoreEntity.setId(pk);
		ScoreDTO scoreDTO = new ScoreDTO(scoreEntity);
		
		Assertions.assertThrows(ResourceNotFoundException.class,  () -> {
			service.saveScore(scoreDTO);
		});
	}
}
