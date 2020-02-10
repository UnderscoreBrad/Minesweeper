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

public class MSTile {

    public static int bombCt = 0;
    //================== INSTANCE VARIABLES ==================
    private boolean bomb;
    private int markType;
    private int tileNum;
    private boolean revealed;

    //================== CONSTRUCTOR ==================
    public MSTile() {
        bomb = false;
        markType = 0;
        tileNum = 9;
        revealed = false;
    }

    //================== MUTATORS ==================
    /**
     * Places bomb at tile
     */
    public void placeBomb() {
        bomb = true;
        tileNum = 9;
        bombCt++;
    }

    /**
     * Removes bomb from tile
     */
    public void removeBomb() {
        bomb = false;
        bombCt--;
    }

    /**
     * @param num : tile number given by Board Sets the tile number
     */
    public void setTileNum(int num) {
        tileNum = num;
    }

    /**
     * Sets tile as revealed.
     */
    public void revealTile() {
        revealed = true;
    }

    /**
     * Sets the tile mark to the given setting
     *
     * @param m : mark type 0 = none, 1 = flag, 2 = ?
     */
    public void markTile(int m) {
        markType = m;
    }

    //================== ACCESSORS ==================
    /**
     * @return boolean if tile is revealed
     */
    public boolean isRevealed() {
        return revealed;
    }

    /**
     * @return boolean if tile is bombed
     */
    public boolean getBomb() {
        return (bomb);
    }

    /**
     *
     * @return integer for the tile mark type
     */
    public int getMark() {
        return markType;    //0 == no mark, 1 == flag, 2 == ?
    }

    /**
     * @return the number of adjacent bombs
     */
    public int getTileNum() {
        return tileNum;
    }
}
