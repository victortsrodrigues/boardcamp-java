package com.boardcamp.api.services;

import org.springframework.stereotype.Service;

import com.boardcamp.api.dtos.GamesDTO;
import com.boardcamp.api.exceptions.GamesNameConflictException;
import com.boardcamp.api.models.GamesModel;
import com.boardcamp.api.repositories.GamesRepository;

@Service
public class GamesService {
  
  final GamesRepository gamesRepository;

  public GamesService(GamesRepository gamesRepository) {
    this.gamesRepository = gamesRepository;
  }

  public GamesModel createGames(GamesDTO body) {
    if (gamesRepository.existsByName(body.getName())) {
      throw new GamesNameConflictException("Game with this name already exists.");
    }

    return gamesRepository.save(new GamesModel(body));
  }

}
