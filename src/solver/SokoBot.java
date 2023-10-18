package solver;

import java.util.*;

public class SokoBot {

/*
	WAYS TO IMPLEMENT THE A* ALGORITHM
	1. https://codereview.stackexchange.com/questions/143206/a-uniform-cost-and-greedy-best-first-search-implementations
	2. https://www.hackerearth.com/practice/notes/a-search-algorithm/
*/

  public String solveSokobanPuzzle(int width, int height, char[][] mapData, char[][] itemsData) {

    GameState initial = new GameState(width, height, mapData, itemsData);
    initial.distance = 0;

    PriorityQueue<GameState> openSet = new PriorityQueue<>();
    openSet.add(initial);

    Map<GameState, GameState> cameFrom = new HashMap<>();
    Map<GameState, Integer> gScore = new HashMap<>();
    gScore.put(initial, 0);

    while (!openSet.isEmpty()) {

      if (current.isGoal()) {
        return "";
      }

    }

    return null; // no solution

  }

}
