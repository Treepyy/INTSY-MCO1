package solver;

import java.util.Arrays;

/*
	THIS CLASS REPRESENTS THE STATE SPACE OF THE SOKOBAN PUZZLE. IT IMPLEMENTS THE MANHATTAN
	DISTANCE HEURISTIC COMPUTATION.
	
	THINGS TO IMPLEMENT FURTHER:
	1. SOME METHODS findPlayerPosition(), isGoal(), AND getMoveDirection() SHOULD RETURN SOMETHING.
	   UPDATE THIS AS SOKOBOT CLASS IS BEING WRITTEN.
	2. CHECK IF ALL METHODS ARE NEEDED. MAKE GOOD USE OF THIS IN THE SOKOBOT CLASS.
*/

public class GameState implements Comparable<GameState> {
    static int[] dr = { -1, 1, 0, 0 }; // vertical row direction movement
    static int[] dc = { 0, 0, -1, 1 }; // horizontal column direction movement

    int width;
    int height;
    char[][] map;
    char[][] items;
    int playerRow;
    int playerCol;
    int targetCount;
    int distance;
    char lastMove;
    int totalCrates = getTotalCrates();

    /* Constructor for the game state */
    public GameState(int width, int height, char[][] map, char[][] items) {
        this.width = width;
        this.height = height;
        this.map = map;
        this.items = items;
        findPlayerPosition();
        calculateTargetCount();
    }

    /* Find the position of the player ('@' or '+') */
    private void findPlayerPosition() {
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                if (items[i][j] == '@' || items[i][j] == '+') {
                    playerRow = i;
                    playerCol = j;
                    return; // update later!!!
                }
            }
        }
    }

    /* Count the targets */
    private void calculateTargetCount() {
        targetCount = 0;
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                if (items[i][j] == '.' || items[i][j] == '+') {
                    targetCount++;
                }
            }
        }
    }

    private int getTotalCrates(){

        int totalCrates = 0;

        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                if (items[i][j] == '$') {
                    totalCrates++;
                }
            }
        }

        return totalCrates;
    }

    /* Check if the state is a goal state */
    public boolean isGoal() {

        int success = 0;

        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                if (items[i][j] == '*') {
                    success++;
                }
            }
        }

        if (success == this.totalCrates)
            return true;

        return false;

        // return targetCount == 0; // update when needed!!!
    }

    /* Calculate the Manhattan distance heuristic */
    public int getManhattanDistance() {
        int distance = 0;
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                if (items[i][j] == '$' || items[i][j] == '*') {
                    distance += calculateManhattan(i, j, findClosestTarget(i, j));
                }
            }
        }
        return distance;
    }

    /* Manhattan distance between a position and a target */
    private int calculateManhattan(int row, int col, int targetIndex) {
        int targetRow = targetIndex / width;
        int targetCol = targetIndex % width;
        return Math.abs(targetRow - row) + Math.abs(targetCol - col);
    }

    /* Find where the closest target is based on a given position */
    private int findClosestTarget(int row, int col) {
        int closestTarget = -1;
        int minDistance = Integer.MAX_VALUE;

        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                if (items[i][j] == '.' || items[i][j] == '+') {
                    int distance = calculateManhattan(row, col, i * width + j);
                    if (distance < minDistance) {
                        minDistance = distance;
                        closestTarget = i * width + j;
                    }
                }
            }
        }
        return closestTarget;
    }

    /* Create new game state when player moves */
    public GameState movePlayer(int dr, int dc) {
        int newRow = playerRow + dr;
        int newCol = playerCol + dc;

        char newPlayerTile = items[newRow][newCol];
        char newMapTile = map[newRow][newCol];

        /* Check if move is valid */
        if (newMapTile == '#' || (newPlayerTile != ' ' && newPlayerTile != '.')) {
            return null;
        }

        /* Copy of array to represent the new state */
        char[][] newItems = copyItemsArray();
		
        if (items[playerRow][playerCol] == '+') {
			newItems[playerRow][playerCol] = '.';
		} else {
			newItems[playerRow][playerCol] = ' ';
		}

		if (newItems[newRow][newCol] == '.') {
			newItems[newRow][newCol] = '+';
		} else {
			newItems[newRow][newCol] = '@';
		}

        /* New game state */
        GameState newState = new GameState(width, height, map, newItems);
        newState.playerRow = newRow;
        newState.playerCol = newCol;
        newState.lastMove = getMoveDirection(dr, dc);

        return newState;
    }

    /* Copy of the items array - to avoid accidental changes in original puzzle */
    private char[][] copyItemsArray() {
        char[][] itemsCopy = new char[height][width];
        for (int i = 0; i < height; i++) {
            System.arraycopy(items[i], 0, itemsCopy[i], 0, width);
        }
        return itemsCopy;
    }

    /* Get the move direction */
    public char getMoveDirection(int dr, int dc) {
        if (dr == -1) {
            return 'u';
        } else if (dr == 1) {
            return 'd';
        } else if (dc == -1) {
            return 'l';
        } else if (dc == 1) {
            return 'r';
        }
        return ' '; // update later!!!
    }

    @Override
    public int compareTo(GameState other) {
        /* Comparing states based on their priority */
        int thisPriority = this.distance + this.getManhattanDistance();
        int otherPriority = other.distance + other.getManhattanDistance();
        return Integer.compare(thisPriority, otherPriority);
    }

    /* Get the last move */
    public char getLastMove() {
        return lastMove;
    }
}
