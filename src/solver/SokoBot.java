package solver;

import java.util.*;

public class SokoBot {

/*
	WAYS TO IMPLEMENT THE A* ALGORITHM
	1. https://codereview.stackexchange.com/questions/143206/a-uniform-cost-and-greedy-best-first-search-implementations
	2. https://www.hackerearth.com/practice/notes/a-search-algorithm/
*/

  private static final char EMPTY=' ', TARGET='.', WALL='#';
  private static final int RIGHT=0b00, LEFT=0b01, UP=0b010, DOWN=0b11;
  private int rows, cols;
  private char[][] board;
  private Map<String, Integer> minMoves = new HashMap<>();

  public String solveSokobanPuzzle(int width, int height, char[][] mapData, char[][] itemsData) {
    this.rows = height;
    this.cols = width;

    System.out.println("Width: " + width + ", Height: " + height);
    ArrayList<Integer> playerAndBoxRCs = new ArrayList<>();

    System.out.println("Map Layout: ");
    for (int i = 0; i < height; i++){
      for (int j = 0; j < width; j++){
        System.out.print(mapData[i][j]);
      }
      System.out.println();
    }
    System.out.println();

    // Find the player's position and box positions.
    for (int i = 0; i < height; i++){
      for (int j = 0; j < width; j++){
        if (itemsData[i][j] == '@'){
          playerAndBoxRCs.add(i);
          playerAndBoxRCs.add(j);
          break;
        }
      }
    }

    // Find box positions.
    for (int i = 0; i < height; i++){
      for (int j = 0; j < width; j++){
        if (itemsData[i][j] == '$'){
          playerAndBoxRCs.add(i);
          playerAndBoxRCs.add(j);
        }
      }
    }

    // Display the player and box positions.
    for (int i = 0; i < playerAndBoxRCs.size(); i += 2) {
      int first = playerAndBoxRCs.get(i);
      int second = (i + 1 < playerAndBoxRCs.size()) ? playerAndBoxRCs.get(i+1) : -1;
      if (i == 0){
        System.out.println("Player Pos: ");
      }
      else if (i == 2){
        System.out.println("Boxes Pos: ");
      }
      System.out.println("{" + first + ", " + second + "}");
    }

    int[] convertedRCsData = playerAndBoxRCs.stream().mapToInt(i -> i).toArray();
    String solution = solve(mapData, convertedRCsData);

    return solution;
  }

  // This method initiates the solving process of the Sokoban puzzle.
  private String solve(char[][] board, int[] objectRCs) {
    this.board = board;

    // Generate the key for the target positions.
    int[] targetRCs = new int[objectRCs.length - 2];
    int i = 0;

    for (int r = 0; r < rows; r++){
      for (int c = 0; c < cols; c++){
        if (board[r][c] == TARGET){
          targetRCs[i++] = r;
          targetRCs[i++] = c;
        }
      }
    }
    if (i != targetRCs.length){
      System.out.println("Number of boxes != number of targets");
      return "-1";
    }

    String targetKey = getKey(targetRCs);
    return solve(objectRCs, targetKey);
  }

  // This method creates a unique key for a given set of object positions.
  private String getKey(int[] objectRCs) {
    StringBuilder key = new StringBuilder();

    for (int i = 0; i < objectRCs.length; i++){
      key.append((char)('0' + objectRCs[i]));
    }

    return key.toString();
  }

  // Attempts to solve the puzzle using a breadth-first search approach
  private String solve(int[] objectRCs, String targetKey) {
    Queue<Step> queue = new ArrayDeque<>();
    queue.add(new Step(objectRCs, 0, 0, null));

    while (!queue.isEmpty()){
      Step s = queue.poll();
      sort(s.objectRCs);
      String key = getKey(s.objectRCs);

      if (targetKey.equals(key.substring(2))){
        System.out.println("Number of Moves: " + s.numMove);
        return getMoves(s);
      }
      else if (minMoves.containsKey(key) && s.numMove >= minMoves.get(key)){
        continue;
      }

      minMoves.put(key, s.numMove);
      int numMovesP1 = s.numMove + 1;

      addIfValid(queue, s, -1, 0, numMovesP1, UP);
      addIfValid(queue, s, 1, 0, numMovesP1, DOWN);
      addIfValid(queue, s, 0, -1, numMovesP1, LEFT);
      addIfValid(queue, s, 0, 1, numMovesP1, RIGHT);
    }
    return "-1";
  }

  // This method sorts the object positions in an array.
  private void sort(int[] arr) {
    // Bubble sort 2,n
    int lenM2 = arr.length - 2;
    boolean isChanged;

    do {
      isChanged = false;
      for (int i = 2; i < lenM2; i += 2) {
        if (arr[i] > arr[i + 2] || (arr[i] == arr[i + 2] && arr[i + 1] > arr[i + 3])) {
          int tr = arr[i];
          int tc = arr[i + 1];
          arr[i] = arr[i + 2];
          arr[i + 1] = arr[i + 3];
          arr[i + 2] = tr;
          arr[i + 3] = tc;
          isChanged = true;
        }
      }
    } while (isChanged);
  }

  // This method adds a new step to the queue if it's a valid move.
  private void addIfValid(Queue<Step> queue, Step s, int dr, int dc, int numMovesP1, int thisMove) {
    int playerR = s.objectRCs[0] + dr;
    int playerC = s.objectRCs[1] + dc;

    // Ensure the player stays within bounds and doesn't hit a wall.
    if (playerR < 0 || playerR >= rows || playerC < 0 || playerC >= cols || board[playerR][playerC] == WALL) {
      return;
    }

    int[] newRCs = null;

    // Pushing a box
    for (int i = 2; i < s.objectRCs.length; i += 2) {
      if (playerR == s.objectRCs[i] && playerC == s.objectRCs[i + 1]) {
        int boxR = playerR + dr;
        int boxC = playerC + dc;

        // Ensure the box stays within bounds and doesn't hit a wall.
        if (boxR < 0 || boxR >= rows || boxC < 0 || boxC >= cols || board[boxR][boxC] == WALL) {
          return;
        }

        // Ensure the box doesn't hit another box.
        for (int j = 2; j < s.objectRCs.length; j += 2) {
          if (boxR == s.objectRCs[j] && boxC == s.objectRCs[j + 1]) {
            return;
          }
        }

        // Set newRCs to the updated positions.
        newRCs = new int[s.objectRCs.length];
        System.arraycopy(s.objectRCs, 0, newRCs, 0, s.objectRCs.length);
        newRCs[0] = playerR;
        newRCs[1] = playerC;
        newRCs[i] = boxR;
        newRCs[i + 1] = boxC;
        break;
      }
    }

    // If the box wasn't moved, update only the player's row and column.
    if (newRCs == null) {
      newRCs = new int[s.objectRCs.length];
      System.arraycopy(s.objectRCs, 0, newRCs, 0, s.objectRCs.length);
      newRCs[0] = playerR;
      newRCs[1] = playerC;
    }

    Step newStep = new Step(newRCs, numMovesP1, thisMove, s);
    queue.add(newStep);
  }

  // This method constructs the sequence of moves from the solved puzzle.
  private String getMoves(Step step) {
    StringBuilder s = new StringBuilder();
    for (; step.parent != null; step = step.parent) {
      switch (step.prevMove) {
        case RIGHT:
          s.append("r");
          break;
        case LEFT:
          s.append("l");
          break;
        case UP:
          s.append("u");
          break;
        case DOWN:
          s.append("d");
          break;
      }
    }

    return s.reverse().toString().trim();
  }

  // Represents a step in the puzzle-solving process.
  private class Step {
    int[] objectRCs;
    int numMove;
    int prevMove;
    Step parent;

    public Step(int[] objectRCs, int numMove, int prevMove, Step parent) {
      this.objectRCs = objectRCs;
      this.numMove = numMove;
      this.prevMove = prevMove;
      this.parent = parent;
    }
  }

}
