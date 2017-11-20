import java.util.ArrayList;
import java.util.HashMap;
import java.util.Collections;
import java.util.Comparator;
import java.util.Random;

import tester.*;
import javalib.funworld.World;
import javalib.funworld.WorldScene;
import java.awt.Color;
import javalib.worldimages.*;

/*DOCUMENTATION
 * Instructions:
 * The maze game will start on a canvas upon run. A random maze will be generated, through which
 * the player may traverse using the arrow keys. The player is a green square at the top left 
 * corner of the maze, and the goal is to reach the purple square at the bottom right.
 * 
 * Keys:
 * - Press arrow keys to move around the maze
 * - Press "d" to conduct a depth-first search
 * - Press "b" to conduct a breadth-first search
 * - Press "n" to restart the game with a newly constructed maze
 * 
 * Whistles:
 * - Users can start a new maze by pressing the "n" key, which will restart the game with 
 * the player in the initial position and reconstruct a new random maze
 * 
 * Bells:
 * - Mazes are constructed with a horizontal direction bias. This is accomplished by greatly
 * reducing the value that an edge weight can be randomly generated; this is done by division
 * in line 56. A fairly constructed maze can be reverted to by removing the division by 3, and
 * setting this.weight to rand.nextInt(100)
 */


// to compare 2 edges
class WeightComp implements Comparator<Edge> {
  @Override
  public int compare(Edge e1, Edge e2) {
    return e1.weight - e2.weight;
  }
}

// represent an edge
class Edge {
  Cell c1;
  Cell c2;

  int weight;

  Edge(Cell c1, Cell c2) {
    this.c1 = c1;
    this.c2 = c2;
    Random rand = new Random();
    this.weight = rand.nextInt(4) / 3;
  }

  // render this cell
  WorldImage edgeImage() {
    if (c1.x == c2.x) {
      return new RectangleImage(Maze.MAZE_SIZE, 1, "solid", Color.BLACK);
    }
    else {
      return new RectangleImage(1, Maze.MAZE_SIZE, "solid", Color.BLACK);
    }
  }
}

// represents a cell
class Cell {
  int x;
  int y;

  boolean visited;

  ArrayList<Edge> edges;
  Color color;

  Cell(int x, int y) {
    this.x = x;
    this.y = y;
    this.visited = false;
    this.edges = new ArrayList<Edge>();
    this.color = new Color(100, 100, 100);
  }

  // render this cell
  WorldImage cellImage() {
    // last cell
    if (this.x == Maze.MAZE_SIZE - 1 && this.y == Maze.MAZE_SIZE - 1) {
      return new RectangleImage(Maze.MAZE_SIZE, Maze.MAZE_SIZE, "solid", Color.MAGENTA);
    }
    // first cell
    else if (this.x == 0 && this.y == 0) {
      return new RectangleImage(Maze.MAZE_SIZE, Maze.MAZE_SIZE, "solid", new Color(150, 200, 255));
    }
    else {
      return new RectangleImage(Maze.MAZE_SIZE, Maze.MAZE_SIZE, "solid", this.color);
    }
  }

  // add given edge to this cells list of edges
  void updateEdges(Edge e) {
    this.edges.add(e);
  }

  // is this cell the same as the other cell
  boolean sameCell(Cell other) {
    return this.x == other.x && this.y == other.y;
  }

  // does this cell have a wall to its right
  boolean hasRightWall(ArrayList<Edge> walls) {

    if (this.x == Maze.MAZE_SIZE - 1) {
      return true;
    }

    for (Edge e : walls) {
      if (e.c1.sameCell(this) && e.c2.x == this.x + 1) {
        return true;
      }
      else if (e.c2.sameCell(this) && e.c1.x == this.x + 1) {
        return true;
      }
    }

    return false;
  }

  // does this cell have a wall to its left
  boolean hasLeftWall(ArrayList<Edge> walls) {

    if (this.x == 0) {
      return true;
    }

    for (Edge e : walls) {
      if (e.c1.sameCell(this) && e.c2.x == this.x - 1) {
        return true;
      }
      else if (e.c2.sameCell(this) && e.c1.x == this.x - 1) {
        return true;
      }
    }

    return false;
  }

  // does this cell have a wall above it
  boolean hasTopWall(ArrayList<Edge> walls) {

    if (this.y == 0) {
      return true;
    }

    for (Edge e : walls) {
      if (e.c1.sameCell(this) && e.c2.y == this.y - 1) {
        return true;
      }
      else if (e.c2.sameCell(this) && e.c1.y == this.y - 1) {
        return true;
      }
    }

    return false;
  }

  // does this cell have a wall below it
  boolean hasBottomWall(ArrayList<Edge> walls) {

    if (this.y == Maze.MAZE_SIZE - 1) {
      return true;
    }

    for (Edge e : walls) {
      if (e.c1.sameCell(this) && e.c2.y == this.y + 1) {
        return true;
      }
      else if (e.c2.sameCell(this) && e.c1.y == this.y + 1) {
        return true;
      }
    }

    return false;
  }
}

// represents a player
class Player {
  Cell c;

  Player() {
    this.c = new Cell(0, 0);
  }

  // image of this player
  WorldImage playerImage() {
    return new RectangleImage(Maze.MAZE_SIZE, Maze.MAZE_SIZE, "solid", Color.GREEN);
  }

  // moves player
  // changes color of cells visited by player
  void movePlayer(String ke, ArrayList<Edge> walls, ArrayList<ArrayList<Cell>> cells) {
    Cell cell = cells.get(this.c.x).get(this.c.y);

    if (ke.equals("right")) {
      if (this.c.hasRightWall(walls)) {
        // do nothing
      }
      else {
        this.c.x += 1;
        cell.color = new Color(150, 200, 255);
      }
    }
    else if (ke.equals("left")) {
      if (this.c.hasLeftWall(walls)) {
        // do nothing
      }
      else {
        this.c.x -= 1;
        cell.color = new Color(150, 200, 255);
      }
    }
    else if (ke.equals("up")) {
      if (this.c.hasTopWall(walls)) {
        // do nothing
      }
      else {
        this.c.y -= 1;
        cell.color = new Color(150, 200, 255);
      }
    }
    else if (ke.equals("down")) {
      if (this.c.hasBottomWall(walls)) {
        // do nothing
      }
      else {
        this.c.y += 1;
        cell.color = new Color(150, 200, 255);
      }
    }
    else {
      // do nothing
    }
  }

  // did the player reach the end?
  boolean endGame() {
    return this.c.x == Maze.MAZE_SIZE - 1 && this.c.y == Maze.MAZE_SIZE - 1;
  }
}

// represents a UnionFind
class UnionFind {
  HashMap<Cell, Cell> reps;

  UnionFind(ArrayList<ArrayList<Cell>> cells) {
    HashMap<Cell, Cell> hash = new HashMap<Cell, Cell>();
    for (int i = 0; i < Maze.MAZE_SIZE; i++) {
      for (int j = 0; j < Maze.MAZE_SIZE; j++) {
        hash.put(cells.get(i).get(j), cells.get(i).get(j));
      }
    }
    this.reps = hash;
  }

  // find the rep of given cell
  Cell find(Cell c) {
    while (!this.reps.get(c).sameCell(c)) {
      c = this.reps.get(c);
    }
    return c;
  }

  // make c1 the rep of c2
  void union(Cell c1, Cell c2) {
    reps.put(find(c1), find(c2));
  }
}

// represents the maze
class Maze extends World {

  static final int MAZE_SIZE = 20;

  ArrayList<ArrayList<Cell>> cells;
  ArrayList<Edge> edges;
  ArrayList<Edge> sortedEdges;

  Player player;

  boolean breadthFirst;
  boolean depthFirst;

  HashMap<Cell, Cell> cameFromEdge;
  ArrayList<Cell> worklist;
  ArrayList<Cell> steps;

  boolean done = false;

  Maze() {
    this.cells = new ArrayList<ArrayList<Cell>>();
    this.edges = new ArrayList<Edge>();
    this.sortedEdges = new ArrayList<Edge>();
    this.player = new Player();
    this.breadthFirst = false;
    this.depthFirst = false;
    setCells();
  }

  // create minimum spanning tree using Kruskal's algorithm
  void kruskal() {
    ArrayList<Edge> walls = new ArrayList<Edge>();

    // initialize reps to themselves
    UnionFind uf = new UnionFind(this.cells);

    while (!this.sortedEdges.isEmpty()) {
      Edge e = this.sortedEdges.remove(0);
      Cell c1 = e.c1;
      Cell c2 = e.c2;
      if (uf.find(c1).sameCell(uf.find(c2))) {
        walls.add(e);
      }
      else {
        uf.union(c1, c2);
      }
    }
    this.edges = walls;
  }

  // initializes values of cells
  void setCells() {
    ArrayList<ArrayList<Cell>> result = new ArrayList<ArrayList<Cell>>();
    for (int row = 0; row < Maze.MAZE_SIZE; row++) {
      ArrayList<Cell> temp = new ArrayList<Cell>();
      for (int col = 0; col < Maze.MAZE_SIZE; col++) {
        Cell c = new Cell(row, col);
        temp.add(c);
      }
      result.add(temp);
    }
    result = this.setEdges(result);
    this.cells = result;
  }

  // initializes values for the edges
  ArrayList<ArrayList<Cell>> setEdges(ArrayList<ArrayList<Cell>> cells) {
    ArrayList<Edge> temp = new ArrayList<Edge>();
    for (int i = 0; i < Maze.MAZE_SIZE; i++) {
      for (int j = 0; j < Maze.MAZE_SIZE; j++) {
        Cell c = cells.get(i).get(j);
        if (j == Maze.MAZE_SIZE - 1) {
          if (i == Maze.MAZE_SIZE - 1) {
            // last cell, do nothing
          }
          else {
            // cell on right side, not at bottom
            Edge e1 = new Edge(c, cells.get(i + 1).get(j));
            c.updateEdges(e1);
            temp.add(e1);
          }
        }
        else {
          // cell at bottom
          if (i == Maze.MAZE_SIZE - 1) {
            Edge e1 = new Edge(c, cells.get(i).get(j + 1));
            c.updateEdges(e1);
            temp.add(e1);
          }
          else {
            // cell in middle
            Edge e1 = new Edge(c, cells.get(i).get(j + 1));
            Edge e2 = new Edge(c, cells.get(i + 1).get(j));
            c.updateEdges(e1);
            c.updateEdges(e2);
            temp.add(e1);
            temp.add(e2);
          }
        }
      }
    }
    Collections.sort(temp, new WeightComp());
    sortedEdges = temp;
    this.edges = temp;
    return cells;
  }

  // handles breadth-first and depth-first searches
  void search() {

    Cell next = this.worklist.remove(0);

    if (next.visited) {
      // do nothing
    }
    else if (next.y == (Maze.MAZE_SIZE - 1) && next.x == (Maze.MAZE_SIZE - 1)) {
      done = true;
    }
    else {
      if (!next.hasBottomWall(this.edges)) {
        Cell c = cells.get(next.x).get(next.y + 1);
        steps.add(c);

        if (breadthFirst) {
          // add to end
          worklist.add(c);
        }
        else {
          // depthFirst: add to front
          worklist.add(0, c);
        }
        if (cameFromEdge.containsKey(c)) {
          // do nothing
        }
        else {
          cameFromEdge.put(c, next);
        }
      }
      if (!next.hasRightWall(this.edges)) {
        Cell c = cells.get(next.x + 1).get(next.y);
        steps.add(c);
        if (breadthFirst) {
          // add to end
          worklist.add(c);
        }
        else {
          // depthFirst: add to front
          worklist.add(0, c);
        }
        if (cameFromEdge.containsKey(c)) {
          // do nothing
        }
        else {
          cameFromEdge.put(c, next);
        }
      }
      if (!next.hasTopWall(this.edges)) {
        Cell c = cells.get(next.x).get(next.y - 1);
        steps.add(c);
        if (breadthFirst) {
          // add to end
          worklist.add(c);
        }
        else {
          // depthFirst: add to front
          worklist.add(0, c);
        }
        if (cameFromEdge.containsKey(c)) {
          // do nothing
        }
        else {
          cameFromEdge.put(c, next);
        }
      }
      if (!next.hasLeftWall(this.edges)) {
        Cell c = cells.get(next.x - 1).get(next.y);
        steps.add(c);
        if (breadthFirst) {
          // add to end
          worklist.add(c);
        }
        else {
          // depthFirst: add to front
          worklist.add(0, c);
        }
        if (cameFromEdge.containsKey(c)) {
          // do nothing
        }
        else {
          cameFromEdge.put(c, next);
        }
      }
    }
    next.visited = true;
  }

  // reconstructs solution path from search
  ArrayList<Cell> reconstruct(HashMap<Cell, Cell> cameFromEdge, Cell c) {
    ArrayList<Cell> path = new ArrayList<Cell>();
    while (!(player.c.y == c.y && player.c.x == c.x)) {
      path.add(c);
      c = cameFromEdge.get(c);
    }
    // add last cell (i.e. the player's cell)
    path.add(c);
    return path;
  }

  // colors the solution path white
  void colorPath(ArrayList<Cell> steps) {
    for (Cell c : steps) {
      cells.get(c.x).get(c.y).color = new Color(255, 255, 255);
    }
  }

  // colors the the cells that are currently being searched red
  void colorCells(ArrayList<Cell> steps) {
    for (Cell c : steps) {
      this.cells.get(c.x).get(c.y).color = new Color(250, 150, 150);
    }
  }

  // animates search
  public World onTick() {
    if (breadthFirst || depthFirst) {
      this.worklist.add(this.player.c);
      search();
      colorCells(steps);

      if (done) {
        colorPath(
            reconstruct(cameFromEdge, this.cells.get(Maze.MAZE_SIZE - 1).get(Maze.MAZE_SIZE - 1)));
        this.breadthFirst = false;
        this.depthFirst = false;
      }
    }
    return this;
  }

  // handles user input
  public World onKeyEvent(String ke) {
    if (ke.equals("n")) {
      setCells();
      kruskal();
      this.player = new Player();
      this.breadthFirst = false;
      this.depthFirst = false;
      this.done = false;
    }
    else if (ke.equals("d")) {
      cameFromEdge = new HashMap<Cell, Cell>();
      worklist = new ArrayList<Cell>();
      steps = new ArrayList<Cell>();
      depthFirst = true;
      breadthFirst = false;
    }
    else if (ke.equals("b")) {
      cameFromEdge = new HashMap<Cell, Cell>();
      worklist = new ArrayList<Cell>();
      steps = new ArrayList<Cell>();
      breadthFirst = true;
      depthFirst = false;
    }
    this.player.movePlayer(ke, this.edges, this.cells);
    return this;
  }

  // draws world
  public WorldScene makeScene() {

    WorldScene w = new WorldScene(Maze.MAZE_SIZE * Maze.MAZE_SIZE, Maze.MAZE_SIZE * Maze.MAZE_SIZE);
    if (this.edges.isEmpty()) {
      return w;
    }
    else {
      // draw cells
      for (int i = 0; i < Maze.MAZE_SIZE; i++) {
        for (int j = 0; j < Maze.MAZE_SIZE; j++) {
          Cell c = this.cells.get(i).get(j);
          w = w.placeImageXY(c.cellImage(), c.x * Maze.MAZE_SIZE + Maze.MAZE_SIZE / 2,
              c.y * Maze.MAZE_SIZE + Maze.MAZE_SIZE / 2);
        }
      }
      // draw walls
      for (Edge e : this.edges) {
        if (e.c1.x == e.c2.x) {
          w = w.placeImageXY(e.edgeImage(), e.c1.x * Maze.MAZE_SIZE + Maze.MAZE_SIZE / 2,
              e.c1.y * Maze.MAZE_SIZE + Maze.MAZE_SIZE);
        }
        else {
          w = w.placeImageXY(e.edgeImage(), e.c1.x * Maze.MAZE_SIZE + Maze.MAZE_SIZE,
              e.c1.y * Maze.MAZE_SIZE + Maze.MAZE_SIZE / 2);
        }
      }

      // draw player
      return w.placeImageXY(this.player.playerImage(),
          this.player.c.x * Maze.MAZE_SIZE + Maze.MAZE_SIZE / 2,
          this.player.c.y * Maze.MAZE_SIZE + Maze.MAZE_SIZE / 2);
    }
  }

  // instructions for when the game is over and the lastScene to be depicted in
  // each scenario
  public WorldEnd worldEnds() {
    if (this.player.endGame()) {
      this.depthFirst = true;
      return new WorldEnd(true, this.lastScene("Win!"));
    }
    return new WorldEnd(false, this.makeScene());
  }

  // represents the last image displayed to the user
  public WorldScene lastScene(String s) {
    return this.makeScene().placeImageXY(new TextImage(s, Color.white), Maze.MAZE_SIZE * 4,
        Maze.MAZE_SIZE);
  }
}

// examples and tests
class ExamplesMaze {
  Maze m = new Maze();

  WeightComp wc = new WeightComp();

  Cell c1 = new Cell(0, 0);
  Cell c2 = new Cell(1, 0);
  Cell c3 = new Cell(0, 1);
  Cell c4 = new Cell(1, 1);

  ArrayList<Cell> testCells = new ArrayList<Cell>();

  Edge e0 = new Edge(c1, c1);
  Edge e1 = new Edge(c1, c2);
  Edge e2 = new Edge(c1, c3);
  Edge e3 = new Edge(c2, c4);
  Edge e4 = new Edge(c3, c4);

  Player p0 = new Player();

  HashMap<Cell, Cell> hash = new HashMap<Cell, Cell>();

  void initConditions() {
    testCells.add(c1);
    testCells.add(c2);
    testCells.add(c3);
    testCells.add(c4);

    hash.put(c1, c1);
    hash.put(c1, c2);
    hash.put(c2, c4);

    m.setCells();
    m.kruskal();
  }

  void testCompare(Tester t) {
    t.checkExpect(wc.compare(e1, e1), 0);
    t.checkExpect(wc.compare(e2, e2), 0);
    t.checkRange(wc.compare(e2, e3), -100, 100);
    t.checkRange(wc.compare(e3, e4), -100, 100);
  }

  void testedgeImage(Tester t) {
    t.checkExpect(e0.edgeImage(), new RectangleImage(Maze.MAZE_SIZE, 1, "solid", Color.BLACK));
    t.checkExpect(e1.edgeImage(), new RectangleImage(1, Maze.MAZE_SIZE, "solid", Color.BLACK));
    t.checkExpect(e4.edgeImage(), new RectangleImage(1, Maze.MAZE_SIZE, "solid", Color.BLACK));
  }

  void testCellImage(Tester t) {
    t.checkExpect(new Cell((Maze.MAZE_SIZE - 1), (Maze.MAZE_SIZE - 1)).cellImage(),
        new RectangleImage(Maze.MAZE_SIZE, Maze.MAZE_SIZE, "solid", Color.MAGENTA));
    t.checkExpect(c3.cellImage(),
        new RectangleImage(Maze.MAZE_SIZE, Maze.MAZE_SIZE, "solid", c3.color));
  }

  void testUpdateEdges(Tester t) {
    c1.edges = new ArrayList<Edge>();
    c1.updateEdges(e1);
    t.checkExpect(c1.edges.get(0), e1);
    c1.updateEdges(e2);
    t.checkExpect(c1.edges.get(1), e2);
  }

  void testSameCell(Tester t) {
    t.checkExpect(c1.sameCell(c2), false);
    t.checkExpect(c3.sameCell(c3), true);
  }

  void testSetCells(Tester t) {
    this.initConditions();

    t.checkExpect(m.sortedEdges.size(), 0);
    t.checkExpect(m.cells.size(), Maze.MAZE_SIZE);
    t.checkExpect(m.edges.size(), Maze.MAZE_SIZE * Maze.MAZE_SIZE - (Maze.MAZE_SIZE * 2 - 1));

    for (Edge e : m.edges) {
      t.checkNumRange(e.weight, 0, 100);
    }
  }

  // tests playerImage
  void testPlayerImage(Tester t) {
    t.checkExpect(p0.playerImage(),
        new RectangleImage(Maze.MAZE_SIZE, Maze.MAZE_SIZE, "solid", Color.GREEN));
  }

  // tests endGame
  void testEndGame(Tester t) {
    t.checkExpect(p0.endGame(), false);
    p0.c = new Cell(Maze.MAZE_SIZE - 1, Maze.MAZE_SIZE - 1);
    t.checkExpect(p0.endGame(), true);

  }

  void testUnionFind(Tester t) {
    this.initConditions();

    Random rand = new Random();
    int r = rand.nextInt(Maze.MAZE_SIZE - 1);
    int s = rand.nextInt(Maze.MAZE_SIZE - 1);

    Cell c = m.cells.get(r).get(s);
    Cell d = m.cells.get(s).get(r);

    UnionFind uf = new UnionFind(m.cells);

    t.checkExpect(uf.find(c).sameCell(uf.find(d)), false);

    uf.union(c, d);

    t.checkExpect(uf.find(c).sameCell(uf.find(d)), true);
  }

  // tests colorPath
  void testColorPath(Tester t) {
    Maze m1 = new Maze();
    ArrayList<Cell> cp1 = new ArrayList<Cell>();
    cp1.add(c1);
    cp1.add(c3);
    m1.colorPath(cp1);
    t.checkExpect(m1.cells.get(c1.x).get(c1.y).color, new Color(255, 255, 255));
    t.checkExpect(m1.cells.get(c3.x).get(c3.y).color, new Color(255, 255, 255));
    t.checkExpect(m1.cells.get(c2.x).get(c2.y).color, new Color(100, 100, 100));
  }

  // tests colorCells
  void testColorCells(Tester t) {
    Maze m2 = new Maze();
    ArrayList<Cell> cc1 = new ArrayList<Cell>();
    cc1.add(c2);
    cc1.add(c1);
    m2.colorCells(cc1);
    t.checkExpect(m2.cells.get(c2.x).get(c2.y).color, new Color(250, 150, 150));
    t.checkExpect(m2.cells.get(c1.x).get(c1.y).color, new Color(250, 150, 150));
    t.checkExpect(m2.cells.get(c3.x).get(c3.y).color, new Color(100, 100, 100));
  }

  void testSearch(Tester t) {
    Maze maze = new Maze();
    maze.setCells();
    maze.kruskal();
    maze.player = new Player();
    maze.onKeyEvent("d");

    while (!maze.done) {
      maze.onTick();
      t.checkExpect(maze.worklist.size() <= maze.steps.size(), true);
    }

    ArrayList<Cell> r = maze.reconstruct(maze.cameFromEdge,
        maze.cells.get(Maze.MAZE_SIZE - 1).get(Maze.MAZE_SIZE - 1));

    for (Cell c : r) {
      if (c.x != 0 && c.y != 0) {
        t.checkExpect(maze.steps.contains(c), true);
      }
    }

    t.checkExpect(maze.done, true);
    t.checkExpect(maze.cells.get(Maze.MAZE_SIZE - 1).get(Maze.MAZE_SIZE - 1).color, Color.WHITE);
  }

  // tests game itself
  boolean testBigBang(Tester t) {
    m.bigBang(Maze.MAZE_SIZE * Maze.MAZE_SIZE, Maze.MAZE_SIZE * Maze.MAZE_SIZE, .01);
    return true;
  }
}