/*
 * program that draws map based on given data, finds min and max elevations, and draws lowest
 * elevation change pathways
 * 
 * @author Mackenzie Schaich
 * due 3.39.17
 */
import java.util.*;
import java.io.*;
import java.awt.*;

public class MapDataDrawer
{
  // store map data in grid array
  private int[][] grid; 
  
  // Read 2D array into grid. Data in file "filename", grid is rows x cols
  public MapDataDrawer(String filename, int rows, int cols) throws Exception{
      //set up file scanner 
      File datafile = new File(filename);
      Scanner fileScan = new Scanner(datafile);
      grid = new int [rows][cols];
      //read data from file into grid array
      for(int row =0; row<rows; row++){
          for(int col =0; col<grid[row].length; col++){
              grid[row][col]=fileScan.nextInt();
            }
        }
  }
  
  //simpler constructor to be used for testing methods
  public MapDataDrawer(){
      grid = new int[][] {{3,4,5}, {5,6,7}, {9,2,3}, {10, 40, 0}, {7, 4, 9}};
    }
  
  //prints elevation data in the grid
  public void printData(){
      for(int row = 0; row<grid.length; row++){
          for(int col =0; col<grid[row].length; col++){
              System.out.print(grid[row][col]+ "\t");
            }
          System.out.println();
        }
      
    }
  
  /**
   * @return the min value in the entire grid
   */
  public int findMin(){
      int min = grid[0][0];
      for(int row =0; row<grid.length; row++){
          for(int col =0; col<grid[row].length; col++){
              if(grid[row][col]<min){
                  min = grid[row][col];
                }
            }
        }
      return min;
  }
  
  /**
   * @return the max value in the entire grid
   */
  public int findMax(){
      int max = grid[0][0];
      for(int row =0; row<grid.length; row++){
          for(int col =0; col<grid[row].length; col++){
              if(grid[row][col]>max){
                  max = grid[row][col];
                }
            }
        }
      return max;
  }
  
  /**
   * @param col the column of the grid to check
   * @return the index of the row with the lowest value in the given col for the grid
   */
  public  int indexOfMinRow(int col){
      int min = grid[0][col];
      int minInd = 0;
      for(int row =0;row<grid.length; row++){
          if(grid[row][col]<min){
              min = grid[row][col];
              minInd = row;
        }
    }
    return minInd;
  }
  
  
  /**
   * DON'T CHANGE THIS CODE, except to uncomment it when you instantiate the grid
   * Draws the grid using the given Graphics object. 
   * Colors should be grayscale values 0-255, scaled based on min/max values in grid
   */
  
  // ******ALERT******
  // Note - until you instantiate a grid, through the constructor, this
  // method will generate a null pointer exception, since there is no grid.length
  // ********************
  public void drawMap(Graphics g){
      
    int minVal = findMin();
    int maxVal = findMax();
    double range = maxVal - minVal;
    
    for(int row=0; row < grid.length; row++){
      for(int col=0; col<grid[0].length; col++){
         int val = (int)(((grid[row][col]-minVal)/range) * 255);
         g.setColor(new Color(val,255-val,255-val));
         g.setColor(new Color(val,val,val));
         g.fillRect(col,row,1,1);
        }
    }      
  }
  

   /**
   * Find a path from West-to-East starting at given row.
   * Choose a foward step out of 3 possible forward locations, using greedy method described in assignment.
   * @return the total change in elevation traveled from West-to-East
   */
  public int drawLowestElevPath(Graphics g, int row){
    int currY = row; // row in grid of step one
    // draw initial step - column 0, current row (sent in as parameter)
    g.fillRect(0,row,1,1);

    //variables to keep track of current cell, next cells, and differences
    int currentCell, straight, up, down;
    int straightDif, upDif, downDif, minDif, altDif=0;
    for(int col=0; col<grid[0].length-1; col++){
        //if at top of grid
        if(currY==0){
            straight = grid[currY][col+1];
            up = 100000000;
            down = grid[currY+1][col+1];
        }
        //if at bottom of grid
        else if(currY==grid.length-1){
            straight = grid[currY][col+1];
            up = grid[currY-1][col+1];
            down = 10000000;
        }
        //if at any other row
        else{
            straight = grid[currY][col+1];
            up = grid[currY-1][col+1];
            down = grid[currY+1][col+1];
        }
        //set currentCell and calculate differences in elevation
        currentCell = grid[currY][col];
        straightDif = Math.abs(straight - currentCell);
        upDif = Math.abs(up - currentCell);
        downDif = Math.abs(down - currentCell);
        //if you go straight
        if(straightDif <= upDif && straightDif <= downDif){
            altDif += straightDif;
            g.fillRect(col+1,currY,1,1);
        }
        //if upDif and downDif are equal, "flip coin"
        else if(upDif == downDif){ //&& upDif<straightDif){
            Random rnd = new Random();
            int rand = rnd.nextInt(10)+1;
            if(rand>5){
                altDif+=upDif;
                currY --;
                g.fillRect(col+1,currY,1,1);
            }
            else{
                altDif+=downDif;
                currY++;
                g.fillRect(col+1,currY,1,1);
            }
        }
            
         //if you go up   
        else if(upDif < straightDif && upDif < downDif){
            altDif += upDif;
            currY --;
            g.fillRect(col+1,currY,1,1);
        }
        //if you go down
        else{
            altDif += downDif;
            currY ++;
            g.fillRect(col+1,currY,1,1);
        }
        
    } 
    //System.out.println(altDif);
    return altDif; // computed change in elevation
  }
  
  /**
   * @return the index of the starting row for the lowest-elevation-change path in the entire grid.
   */
  public int indexOfLowestElevPath(Graphics g){
      int minChange = 100000000;
      int minRow = 0;
      for(int row = 0; row<grid.length; row++){
          this.drawLowestElevPath(g, row);
          if(this.drawLowestElevPath(g, row) < minChange){
              minChange = this.drawLowestElevPath(g,row);
              minRow = row;
            }
          //System.out.println(row);
        }
      return minRow; // row of path with lowest elevation
    }  
}