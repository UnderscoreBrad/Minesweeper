/*
THIS IS FOR APCS
APCS WITH THE HANLEY

Three Hanleys for the Elven-kings under the sky,
Seven for the Dwarf-lords in their halls of stone,
Nine for Mortal Men doomed to die,
One for the Dark Lord on his dark throne
In the Land of Mordor where the Shadows lie.

One Hanley to rule them all, One Hanley to find them,
One Hanley to bring them all and in the darkness bind them
In the Land of Mordor where the Shadows lie.

Project Author:         Brad Estus
Project Group/Topic:    APCS
Project Type:           Console Application
Date Completed:         [DATE]
Date Due:               [DUE]
 */

import java.util.*;

public class Board {

    public static boolean loss = false;
    public static boolean firstTurn = true;
    //================== INSTANCE VARIABLES ==================
    private Random placementRandomizer = new Random();
    private MSTile[][] tiles;
    private int size;
    private int bombCount;

    //================== CONSTRUCTOR ==================
    /**
     * Constructor:
     * @param sz : Size
     * @param bc : Bomb Count
     */
    public Board(int sz, int bc) {
        size = sz;
        bombCount = bc;
        tiles = new MSTile[size][size];
        for (int i = 0; i < size; i++) {
            for (int f = 0; f < size; f++) {
                tiles[i][f] = new MSTile();
            }
        }
        while (MSTile.bombCt < bombCount) {
            int rY = placementRandomizer.nextInt(size);
            int rX = placementRandomizer.nextInt(size);
            if (!tiles[rY][rX].getBomb()) {
                tiles[rX][rY].placeBomb();
            }
        }
        giveNums();
    }
    //================== MUTATORS ==================
    /**
     *  sets tile numbers
     * 
     */
    public void giveNums() {                     //Assigns all non-bomb tiles a number, based on nearby bombs.
        for (int y = 0; y < size; y++) {
            for (int x = 0; x < size; x++) {
                if (!tiles[y][x].getBomb()) {
                    int count = 0;
                    for (int i = y - 1; i <= y + 1; i++) {
                        for (int f = x - 1; f <= x + 1; f++) {
                            try {
                                if (tiles[i][f].getBomb()) {
                                    count++;
                                }
                            } catch (Exception ignore) {
                            }
                        }
                    }
                    tiles[y][x].setTileNum(count);
                }
            }
        }
    }

    public void setFirstTurn() {
        firstTurn = true;
    }

    /**
     * Reveals a tile
     *
     * @param y : Y-coordinate
     * @param x : X-coordinate
     * @param protect : protect first turn
     */
    public void reveal(int y, int x, boolean protect) {
        if (tiles[y][x].getBomb()) {
            if (firstTurn && protect) {
                tiles[y][x].removeBomb();
                while (true) {
                    int rx = placementRandomizer.nextInt(size);
                    int ry = placementRandomizer.nextInt(size);
                    if ((rx != x || ry != y) && !tiles[ry][rx].getBomb()) {
                        tiles[ry][rx].placeBomb();
                        giveNums();
                        break;
                    }
                }
                firstTurn = false;
            }
        } else {
            tiles[y][x].revealTile();
            for (int i = -1; i < 2; i++) {
                for (int f = -1; f < 2; f++) {
                    if (!(i == 0 && f == 0) && tiles[y][x].getTileNum() == 0) {
                        try {
                            revealAdjacent(y + i, x + f);
                        } catch (IndexOutOfBoundsException Ignore) {
                            //Ignore it for now, come up with a better solution later.
                        }
                    }
                }
            }
            firstTurn = false;
        }
    }

    /**
     * Reveals an adjacent tile.
     *
     * @param y : Y-coordinate
     * @param x : X-coordinate
     */
    public void revealAdjacent(int y, int x) {
        if (tiles[y][x].getTileNum() == 0 && !tiles[y][x].isRevealed()) {
            reveal(y, x, false);
        } else if (!tiles[y][x].getBomb()) {
            tiles[y][x].revealTile();
        }
    }

    /**
     * @param y : Y-coordinate
     * @param x : X-coordinate
     */
    public void markTile(int y, int x, int m) {
        tiles[y][x].markTile(m);
    }

    /**
     *
     * resets the bomb count for use in creating a new board.
     */
    public void resetBombCount() {
        MSTile.bombCt = 0;
    }

    //================== ACCESSORS ==================
    /**
     *
     * @return the mark type of the tile
     */
    public int getMark(int y, int x) {
        return tiles[y][x].getMark();
    }

    /**
     *
     *
     * @return as booleans the placement of bombs
     */
    public boolean[][] getLayout() {
        boolean layout[][] = new boolean[size][size];
        for (int i = 0; i < size; i++) {
            for (int f = 0; f < size; f++) {
                layout[i][f] = tiles[i][f].getBomb();
            }
        }
        return layout;
    }

    /**
     *
     * @return a boolean array of the revealed tiles
     */
    public boolean[][] getRevealedTiles() {
        boolean layout[][] = new boolean[size][size];
        for (int i = 0; i < size; i++) {
            for (int f = 0; f < size; f++) {
                layout[i][f] = tiles[i][f].isRevealed();
            }
        }
        return layout;
    }

    /**
     *
     * @return the nearby bomb count of the given tile 9 = tile is a bomb
     */
    public int[][] getNums() {
        int layout[][] = new int[size][size];
        for (int i = 0; i < size; i++) {
            for (int f = 0; f < size; f++) {
                layout[i][f] = tiles[i][f].getTileNum();
            }
        }
        return layout;
    }

    /**
     *
     * @return in the console the placement of bombs
     */
    @Override
    public String toString() {
        String layout = "";
        for (int i = 0; i < size; i++) {
            for (int f = 0; f < size; f++) {
                if (!tiles[i][f].getBomb()) {
                    layout += "   ";
                } else {
                    layout += " X ";
                }
            }
            layout += "\n";
        }
        layout += "\nBomb Count: " + MSTile.bombCt;
        return layout;
    }
}
