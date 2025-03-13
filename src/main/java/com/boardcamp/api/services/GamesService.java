package com.boardcamp.api.services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.boardcamp.api.dtos.GamesDTO;
import com.boardcamp.api.exceptions.GamesNameConflictException;
import com.boardcamp.api.models.GamesModel;
import com.boardcamp.api.repositories.GamesRepository;

@Service
public class GamesService {
  
  final GamesRepository gamesRepository;

  // Constructor injection
  public GamesService(GamesRepository gamesRepository) {
    this.gamesRepository = gamesRepository;
  }

  // Method to create a new game
  public GamesModel createGames(GamesDTO body) {
    if (gamesRepository.existsByName(body.getName())) {
      throw new GamesNameConflictException("A game with this name already exists.");
    }

    return gamesRepository.save(new GamesModel(body));
  }

  // Method to get all games
  public List<GamesModel> getAllGames() {
    return gamesRepository.findAll();
  }

}
