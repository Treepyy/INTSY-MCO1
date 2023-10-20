package solver;

import java.util.*;

public class SokoBot {

/*
	WAYS TO IMPLEMENT THE A* ALGORITHM
	1. https://codereview.stackexchange.com/questions/143206/a-uniform-cost-and-greedy-best-first-search-implementations
	2. https://www.hackerearth.com/practice/notes/a-search-algorithm/
*/

  public String solveSokobanPuzzle(int width, int height, char[][] mapData, char[][] itemsData) {


    // doesn't work, feel free to rewrite || remake all

    GameState initial = new GameState(width, height, mapData, itemsData);
    initial.distance = 0;

    PriorityQueue<GameState> openSet = new PriorityQueue<>(Comparator.comparingInt(
            state -> state.distance + state.getManhattanDistance()
    ));
    openSet.offer(initial);

    Map<GameState, GameState> cameFrom = new HashMap<>();
    Map<GameState, Integer> gScore = new HashMap<>();
    gScore.put(initial, 0);

    while (!openSet.isEmpty()) {

      GameState current = openSet.poll();

      if (current.isGoal()) {
        return reconstructPath(current, cameFrom);
      }

      for (int i = 0; i < 4; i++) {
        int newRow = current.playerRow + GameState.dr[i];
        int newCol = current.playerCol + GameState.dc[i];

        GameState neighbor = current.movePlayer(GameState.dr[i], GameState.dc[i]);

        if (neighbor == null) {
          continue; // Invalid move
        }

        int tentativeGScore = gScore.get(current) + 1;
        System.out.println(tentativeGScore);

        if (!gScore.containsKey(neighbor) || tentativeGScore < gScore.get(neighbor)) {
          cameFrom.put(neighbor, current);
          gScore.put(neighbor, tentativeGScore);
          neighbor.distance = tentativeGScore + neighbor.getManhattanDistance();
          openSet.offer(neighbor);
        }
      }
    }

    return null;
  }

  private static String reconstructPath(GameState current, Map<GameState, GameState> cameFrom) {
    StringBuilder path = new StringBuilder();

    while (cameFrom.containsKey(current)) {
      char moveDirection = current.getLastMove();
      path.insert(0, moveDirection);
      current = cameFrom.get(current);
    }

    return path.toString();
  }


}
