package solver;

public class SokoBot {

/*
	WAYS TO IMPLEMENT THE A* ALGORITHM
	1. https://codereview.stackexchange.com/questions/143206/a-uniform-cost-and-greedy-best-first-search-implementations
	2. https://www.hackerearth.com/practice/notes/a-search-algorithm/
*/

  public String solveSokobanPuzzle(int width, int height, char[][] mapData, char[][] itemsData) {
    
	/* 15-second thinking time limit */
	try {
      Thread.sleep(15000);
    } catch (Exception ex) {
      ex.printStackTrace();
    }
	
    return "lrlrlrlrlrlrlrlrlrlrlrlrlrlrlrlrlrlrlrlrlrlrlrlrlrlrlrlrlrlrlrlrlrlrlrlrlrlr";
  }
}
