package com.boardcamp.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import com.boardcamp.api.dtos.GamesDTO;
import com.boardcamp.api.models.GamesModel;
import com.boardcamp.api.repositories.GamesRepository;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class GamesIntegrationTests {

  @Autowired
  private TestRestTemplate restTemplate;

  @Autowired
  private GamesRepository gamesRepository;

  @BeforeEach
  void cleanUp() {
    gamesRepository.deleteAll();
  }

  @Test
  void givenRepeatedGame_whenCreateGame_thenThrowGamesNameConflictException() {
    // Arrange
    GamesModel createdGame = gamesRepository.save(new GamesModel(null, "test", "test", 10, 10));
    GamesDTO gamesDTO = new GamesDTO(createdGame.getName(), createdGame.getImage(), createdGame.getStockTotal(),
        createdGame.getPricePerDay());
    HttpEntity<GamesDTO> body = new HttpEntity<>(gamesDTO);
    // Act
    ResponseEntity<String> response = restTemplate.exchange("/games", HttpMethod.POST, body, String.class);
    // Assert
    assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
    assertEquals("A game with this name already exists.", response.getBody());
    assertEquals(1, gamesRepository.count());
  }

  @Test
  void givenValidGame_whenCreateGame_thenCreateGame() {
    // Arrange
    GamesDTO gamesDTO = new GamesDTO("test", "test", 10, 10);
    HttpEntity<GamesDTO> body = new HttpEntity<>(gamesDTO);
    // Act
    ResponseEntity<GamesModel> response = restTemplate.exchange("/games", HttpMethod.POST, body, GamesModel.class);
    // Assert
    assertEquals(HttpStatus.CREATED, response.getStatusCode());
    assertEquals(gamesDTO.getName(), response.getBody().getName());
    assertEquals(1, gamesRepository.count());
  }

  @Test
  void givenGames_whenGetAllGames_thenReturnGames() {
    // Arrange
    gamesRepository.save(new GamesModel(new GamesDTO("test1", "test1", 10, 10)));
    gamesRepository.save(new GamesModel(new GamesDTO("test2", "test2", 10, 10)));
    // Act
    ResponseEntity<List<GamesModel>> response = restTemplate.exchange(
        "/games",
        HttpMethod.GET,
        null,
        new ParameterizedTypeReference<>() {}
    );
    // Assert
    assertEquals(HttpStatus.OK, response.getStatusCode());
    List<GamesModel> games = response.getBody();
    assertNotNull(games);
    assertEquals(2, games.size());
  }

}
