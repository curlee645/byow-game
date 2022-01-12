package byow.Core;

import byow.TileEngine.TETile;

public class Avatar {
    private int xCoord;
    private int yCoord;
    private int maxHealth;
    private int health;
    private boolean foundGoldenDoor;
    /** True only if this Avatar is a tagger/chaser.
     * When running away from the tagger CPU or a tagger player, this is false.
     * Only true when this Avatar is a tagger, meaning their goal is to catch another Avatar who is
     * not a tagger.
     */
    private boolean isTagger;

    public Avatar(int x, int y, int maxHealth) {
        this.xCoord = x;
        this.yCoord = y;
        this.maxHealth = maxHealth;
        this.health = maxHealth;
        this.isTagger = false;
        foundGoldenDoor = false;
    }

    public Avatar(int x, int y, int maxHealth, int health) {
        this.xCoord = x;
        this.yCoord = y;
        this.maxHealth = maxHealth;
        this.health = health;
        this.isTagger = false;
    }

    public int loseHealth() {
        if (health < 1) {
            throw new RuntimeException("Game should have ended when player had zero health");
        }
        health -= 1;
        return health;
    }

    public int gainHealth() {
        if (health < maxHealth) {
            health += 1;
        }
        return health;
    }

    public void foundDoor() {
        foundGoldenDoor = true;
    }

    // Setters

    public void setXCoord(int x) {
        xCoord = x;
    }

    public void setYCoord(int y) {
        yCoord = y;
    }

    public void setTagger(boolean tagger) {
        isTagger = tagger;
    }

    // Getters

    public int getXCoord() {
        return xCoord;
    }

    public int getYCoord() {
        return yCoord;
    }

    public int getHealth() {
        return health;
    }

    public int getMaxHealth() {
        return maxHealth;
    }

    public boolean isTagger() {
        return isTagger;
    }

    public boolean won() {
        return foundGoldenDoor;
    }
}
