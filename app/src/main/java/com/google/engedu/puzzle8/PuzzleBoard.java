package com.google.engedu.puzzle8;

import android.graphics.Bitmap;
import android.graphics.Canvas;

import java.util.ArrayList;


public class PuzzleBoard {

    private static final int NUM_TILES = 3;
    private static final int[][] NEIGHBOUR_COORDS = {
            { -1, 0 },
            { 1, 0 },
            { 0, -1 },
            { 0, 1 }
    };
    private ArrayList<PuzzleTile> tiles;
    private PuzzleBoard previousBoard;
    private int stepNumber=0;
    public PuzzleBoard getPreviousBoard() {
        return previousBoard;
    }

    public void setPreviousBoard(PuzzleBoard previousBoard) {
        this.previousBoard = previousBoard;
    }

    PuzzleBoard(Bitmap bitmap, int parentWidth) {
        stepNumber=0;
        tiles=new ArrayList<>();
        Bitmap resizedBitmap=Bitmap.createScaledBitmap(bitmap,parentWidth,parentWidth,false);
        for(int i=0;i<NUM_TILES;i++){
            for(int j=0;j<NUM_TILES;j++){
                int tilenumber=i*NUM_TILES+j;
                if(tilenumber!=NUM_TILES*NUM_TILES-1){
                    int tilesize=resizedBitmap.getWidth()/NUM_TILES;
                    Bitmap tileBitmap=Bitmap.createBitmap(resizedBitmap,
                            j*tilesize,
                            i*tilesize, tilesize,tilesize
                            );
                    PuzzleTile tile=new PuzzleTile(tileBitmap,tilenumber);
                    tiles.add(tile);
                }
                else{
                    tiles.add(null);
                }
            }
        }

    }

    PuzzleBoard(PuzzleBoard otherBoard, int stepNumber) {
        previousBoard=otherBoard;
        tiles = (ArrayList<PuzzleTile>) otherBoard.tiles.clone();
        this.stepNumber=stepNumber+1;}

    public void reset() {
        // Nothing for now but you may have things to reset once you implement the solver.
    }

    @Override
    public boolean equals(Object o) {
        if (o == null)
            return false;
        return tiles.equals(((PuzzleBoard) o).tiles);
    }

    public void draw(Canvas canvas) {
        if (tiles == null) {
            return;
        }
        for (int i = 0; i < NUM_TILES * NUM_TILES; i++) {
            PuzzleTile tile = tiles.get(i);
            if (tile != null) {
                tile.draw(canvas, i % NUM_TILES, i / NUM_TILES);
            }
        }
    }

    public boolean click(float x, float y) {
        for (int i = 0; i < NUM_TILES * NUM_TILES; i++) {
            PuzzleTile tile = tiles.get(i);
            if (tile != null) {
                if (tile.isClicked(x, y, i % NUM_TILES, i / NUM_TILES)) {
                    return tryMoving(i % NUM_TILES, i / NUM_TILES);
                }
            }
        }
        return false;
    }

    private boolean tryMoving(int tileX, int tileY) {
        for (int[] delta : NEIGHBOUR_COORDS) {
            int nullX = tileX + delta[0];
            int nullY = tileY + delta[1];
            if (nullX >= 0 && nullX < NUM_TILES && nullY >= 0 && nullY < NUM_TILES &&
                    tiles.get(XYtoIndex(nullX, nullY)) == null) {
                swapTiles(XYtoIndex(nullX, nullY), XYtoIndex(tileX, tileY));
                return true;
            }

        }
        return false;
    }

    public boolean resolved() {
        for (int i = 0; i < NUM_TILES * NUM_TILES - 1; i++) {
            PuzzleTile tile = tiles.get(i);
            if (tile == null || tile.getNumber() != i)
                return false;
        }
        return true;
    }

    private int XYtoIndex(int x, int y) {
        return x + y * NUM_TILES;
    }

    protected void swapTiles(int i, int j) {
        PuzzleTile temp = tiles.get(i);
        tiles.set(i, tiles.get(j));
        tiles.set(j, temp);
    }

    public ArrayList<PuzzleBoard> neighbours() {
        ArrayList<PuzzleBoard> neighbours=new ArrayList<>();
        int emptyTilei=0;
        int emptytilej=0;

        for(int x=0; x<NUM_TILES*NUM_TILES;x++){
            if(tiles.get(x)==null){
                emptytilej=x%NUM_TILES;
                emptyTilei=x/NUM_TILES;
                break;
            }
        }
        for(int[] coordinates : NEIGHBOUR_COORDS){
            int neighbourj=emptytilej+coordinates[0];
            int neighbouri=emptyTilei+coordinates[1];
            if(neighbourj>=0 && neighbourj<NUM_TILES &&
                    neighbouri>=0 && neighbouri<NUM_TILES){
                PuzzleBoard neighbourBoard=
                        new PuzzleBoard(this,stepNumber);

                neighbourBoard.swapTiles(
                        XYtoIndex(neighbourj,neighbouri),
                        XYtoIndex(emptytilej,emptyTilei)
                );
                neighbours.add(neighbourBoard);
            }

        }

        return neighbours;
    }

    public int priority()
    {
     int manhattan_Distance=0;
        for(int i=0;i<NUM_TILES*NUM_TILES;i++){
            PuzzleTile tile=tiles.get(i);
            if(tile!=null){
                int correctPosition=tile.getNumber();
                int correctX=correctPosition %NUM_TILES;
                int correctY=correctPosition/NUM_TILES;
                int currentX=i%NUM_TILES;
                int currentY=i%NUM_TILES;
                manhattan_Distance+=Math.abs(currentX-correctX)+
                        Math.abs(currentY-correctY);
            }
        }

        return manhattan_Distance+stepNumber;
    }

}
