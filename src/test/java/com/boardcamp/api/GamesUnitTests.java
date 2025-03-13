package com.boardcamp.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import com.boardcamp.api.dtos.GamesDTO;
import com.boardcamp.api.exceptions.GamesNameConflictException;
import com.boardcamp.api.models.GamesModel;
import com.boardcamp.api.repositories.GamesRepository;
import com.boardcamp.api.services.GamesService;

@SpringBootTest
class GamesUnitTests {

	@InjectMocks
	private GamesService gamesService;

	@Mock
	private GamesRepository gamesRepository;

	// Test create game - conflict name
	@Test
	void givenRepeatedGame_whenCreateGame_thenThrowGamesNameConflictException() {
		// Arrange
		GamesDTO body = new GamesDTO("test", "test", 10, 10);
		doReturn(true).when(gamesRepository).existsByName(any());
		// Act
		GamesNameConflictException exception = assertThrows(GamesNameConflictException.class, () -> {
			gamesService.createGames(body);
		});
		// Assert
		verify(gamesRepository, times(1)).existsByName(any());
		verify(gamesRepository, times(0)).save(any());
		assertNotNull(exception);
		assertEquals("A game with this name already exists.", exception.getMessage());
	}

	// Test create game - valid game
	@Test
	void givenValidGame_whenCreateGame_thenCreateGame() {
		// Arrange
		doReturn(false).when(gamesRepository).existsByName(any());
		GamesDTO body = new GamesDTO("test", "test", 10, 10);
		GamesModel newGame = new GamesModel(body);
		doReturn(newGame).when(gamesRepository).save(any());
		// Act
		GamesModel result = gamesService.createGames(body);
		// Assert
		verify(gamesRepository, times(1)).existsByName(any());
		verify(gamesRepository, times(1)).save(any());
		assertEquals(newGame, result);
	}

	// Test get all games
	@Test
	void givenGames_whenGetAllGames_thenReturnGames() {
		// Arrange
		GamesModel game1 = new GamesModel(new GamesDTO("test1", "test1", 10, 10));
		GamesModel game2 = new GamesModel(new GamesDTO("test2", "test2", 10, 10));
		List<GamesModel> games = List.of(game1, game2);
		doReturn(games).when(gamesRepository).findAll();
		// Act
		List<GamesModel> result = gamesService.getAllGames();
		// Assert
		verify(gamesRepository, times(1)).findAll();
		assertEquals(games, result);
		assertEquals(2, result.size());
	}
}
