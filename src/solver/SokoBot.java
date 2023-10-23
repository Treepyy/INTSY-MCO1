package solver;

import java.util.*;

public class SokoBot {
    private static final char EMPTY = ' ', TARGET = '.', WALL = '#';
    private static final char PLAYER = '@', BOX = '$';
    private static final int RIGHT = 0, LEFT = 1, UP = 2, DOWN = 3;

    private int rows, cols;
    private char[][] board;
    private char[][] itemsData;
    private Map<String, Integer> minMoves = new HashMap<>();

	/* Initialize and print the puzzle, locate the player and box positions, and solve the puzzle */
    public String solveSokobanPuzzle(int width, int height, char[][] mapData, char[][] itemsData) {
      initialize(width, height, mapData, itemsData);
      int[] playerAndBoxRCs = findPlayerAndBoxPositions(height, width, itemsData);
      return solve(mapData, playerAndBoxRCs);
    }

	/* Initialize the rows, cols, items of the puzzle, and the map */
    private void initialize(int width, int height, char[][] mapData, char[][] itemsData) {
      this.rows = height;
      this.cols = width;
      this.itemsData = itemsData;
      this.board = mapData;

      printInitializationInfo(width, height, mapData);
    }

	/* Display the Sokoban puzzle to be solved */
    private void printInitializationInfo(int width, int height, char[][] mapData) {
      System.out.println("Width: " + width + ", Height: " + height);
      System.out.println("Map Layout: ");
      for (char[] row : mapData) {
        System.out.println(new String(row));
      }
      System.out.println();
    }
	
	/* Locates the player and box positions in the given level */
    private int[] findPlayerAndBoxPositions(int height, int width, char[][] itemsData) {
      List<Integer> positions = new ArrayList<>();
      char[] itemTypes = {PLAYER, BOX};

      for (char itemType : itemTypes) {
        for (int i = 0; i < height; i++) {
          for (int j = 0; j < width; j++) {
            if (itemsData[i][j] == itemType) {
              positions.add(i);
              positions.add(j);
            }
          }
        }
        printPositionsInfo(itemType, positions);
      }

      return positions.stream().mapToInt(Integer::intValue).toArray();
    }

	/* Displays the initial location of the player and boxes */
    private void printPositionsInfo(char itemType, List<Integer> positions) {
      String label = (itemType == PLAYER) ? "Player Initial Pos" : "Boxes Initial Pos";
      System.out.println(label + ": ");
      for (int i = 0; i < positions.size(); i += 2) {
        System.out.println("{" + positions.get(i) + ", " + positions.get(i + 1) + "}");
      }
    }

    // initiates the solving process of the Sokoban level
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
		  System.out.println("Invalid level. Number of boxes != number of targets.");
		  return null;
		}

		String targetKey = getKey(targetRCs);
		
		return solve(objectRCs, targetKey);
    }

    // This method creates a unique key for any given set of object positions.
    private String getKey(int[] objectRCs) {
		StringBuilder key = new StringBuilder();

		for (int i = 0; i < objectRCs.length; i++){
		  key.append((char)('K' + objectRCs[i]));
		}

		return key.toString();
    }

    // second part of solving, attempts to solve the puzzle using a breadth-first search approach
    private String solve(int[] objectRCs, String targetKey) {

		// creates a queue to process the steps
		Queue<Step> queue = new ArrayDeque<>();
		// adds the initial position of the objects as the first step, since it has no parent, the parent parameter is set to null
		queue.add(new Step(objectRCs, 0, 0, null, calculateManhattanHeuristic(objectRCs)));

		// continues processing while there are still steps in the queue
		while (!queue.isEmpty()){
		  Step s = queue.poll();
		  sort(s.objectRCs);
		  String key = getKey(s.objectRCs);

		  if (targetKey.equals(key.substring(2))){
			System.out.println("Number of Moves: " + s.numMove);
			System.out.println("Solution String: " + getMoves(s));
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

		return null; // no solution found
    }

    // This method sorts the object positions in ascending order using bubble sort.
    // Prioritizes the row coordinate, then column e.g. (5,1 > 4,2, 3,1 < 3,2)
    // Ignores the player position indices (0,1) to ensure that it still stays at the beginning of the array
    private void sort(int[] arr) {

    boolean isChanged;

		do {
		  isChanged = false;
		  for (int i = 2; i < arr.length - 2; i += 2) {
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

		int hcost = calculateManhattanHeuristic(newRCs);
		Step newStep = new Step(newRCs, numMovesP1, thisMove, s, hcost);
		queue.add(newStep);
    }

    // This method constructs the sequence of moves from the solved puzzle.
    private String getMoves(Step step) {
		StringBuilder s = new StringBuilder();
		for (; step.parent != null; step = step.parent) {
		  switch (step.prevMove) {
			case RIGHT -> s.append("r");
			case LEFT -> s.append("l");
			case UP -> s.append("u");
			case DOWN -> s.append("d");
		  }
		}

    return s.reverse().toString().trim();
    }

    // Represents a step in the puzzle-solving process.
    private class Step {
		int[] objectRCs;
		int numMove;
		int prevMove;
		int hcost;
		int gcost;
		Step parent;

		public Step(int[] objectRCs, int numMove, int prevMove, Step parent, int hcost) {
			this.objectRCs = objectRCs;
            this.numMove = numMove;
            this.prevMove = prevMove;
            this.parent = parent;
            this.hcost = hcost;
            this.gcost = numMove;
		}
    }
	
	/* Calculate the Manhattan distance heuristic */
    private int calculateManhattanHeuristic(int[] objectRCs) {
        int totalManhattan = 0;
        int numBoxes = (objectRCs.length - 2) / 2; // Calculate the number of boxes
        int playerR = objectRCs[0];
        int playerC = objectRCs[1];

        for (int i = 2; i < objectRCs.length; i += 2) {
            int boxR = objectRCs[i];
            int boxC = objectRCs[i + 1];
            int minDistance = Integer.MAX_VALUE;

            for (int j = 2; j < objectRCs.length; j += 2) {
                if (itemsData[j / 2][j % 2] == TARGET) {
                    int targetR = objectRCs[j];
                    int targetC = objectRCs[j + 1];
                    int playerToBox = Math.abs(boxR - playerR) + Math.abs(boxC - playerC);
                    int boxToTarget = Math.abs(boxR - targetR) + Math.abs(boxC - targetC);
                    minDistance = Math.min(minDistance, playerToBox + boxToTarget);
                }
            }

            totalManhattan += minDistance;
        }

        return totalManhattan;
    }
}