package byow.TileEngine;

import byow.Core.Utils;

import java.awt.Color;

/**
 * Contains constant tile objects, to avoid having to remake the same tiles in different parts of
 * the code.
 *
 * You are free to (and encouraged to) create and add your own tiles to this file. This file will
 * be turned in with the rest of your code.
 *
 * Ex:
 *      world[x][y] = Tileset.FLOOR;
 *
 * The style checker may crash when you try to style check this file due to use of unicode
 * characters. This is OK.ddfdddawd
 *
 * NOTE: no two TETiles can have the same character representaion
 */

public class Tileset {
    private static final String TILE_FOLDER = "tiles";

    public static String getTilePath(String tile) {
        return Utils.join(TILE_FOLDER, tile).getPath();
    }

    public static final TETile AVATAR = new TETile('@', Color.RED, new Color(0, 255, 255),
            "Its a me! Mario!", getTilePath("mario.png"));
    public static final TETile TAGGER = new TETile('b', Color.RED, new Color(207, 16, 32),
            "ROAR!", getTilePath("bowser.png"));
    public static final TETile WALL = new TETile('#', new Color(216, 128, 128), Color.darkGray,
            "wall", getTilePath("wall-dark.png"));
    public static final TETile FLOOR = new TETile('·', new Color(128, 192, 128), Color.black,
            "floor");
    public static final TETile LAVA = new TETile('!', Color.pink, new Color(207, 16, 32),
            "lava - watch out! lava hurts!", getTilePath("lava.png"));
    public static final TETile SPIKES = new TETile('^', Color.red, Color.black,
            "spikes - watch out! these hurt!", getTilePath("spikes.png"));
    public static final TETile HEAL = new TETile('+', Color.white, Color.red,
            "medkit - heals one heart!", getTilePath("healthmushroom.png"));
    public static final TETile GOLDEN_DOOR = new TETile('g', Color.YELLOW, new Color(255, 255,
            32), "Gold Door: Your Way Out!", getTilePath("goldDoor.png"));
    public static final TETile NOTHING = new TETile(' ', Color.RED, new Color(207, 16, 32),
            "HOT!", getTilePath("lava.png"));
    public static final TETile GRASS = new TETile('"', Color.green, Color.black, "grass");
    public static final TETile WATER = new TETile('≈', Color.blue, Color.black, "water");
    public static final TETile FLOWER = new TETile('❀', Color.magenta, Color.pink, "flower");
    public static final TETile LOCKED_DOOR = new TETile('█', Color.orange, Color.black,
            "locked door");
    public static final TETile UNLOCKED_DOOR = new TETile('▢', Color.orange, Color.black,
            "unlocked door");
    public static final TETile SAND = new TETile('▒', Color.yellow, Color.black, "sand");
    public static final TETile MOUNTAIN = new TETile('▲', Color.gray, Color.black, "mountain");
    public static final TETile TREE = new TETile('♠', Color.green, Color.black, "tree");


    public static TETile getTileFromChar(char c) {
//        String[] tiles = {"AVATAR", "WALL", "FLOOR", "LAVA", "SPIKES", "NOTHING", "GRASS",
//                "WATER", "LOCKED_DOOR", "UNLOCKED_DOOR", "SAND", "MOUNTAIN", "TREE"};
        TETile[] tileset = {AVATAR, TAGGER, WALL, FLOOR, LAVA, SPIKES, HEAL, GOLDEN_DOOR, NOTHING, GRASS,
            WATER, FLOWER, LOCKED_DOOR, UNLOCKED_DOOR, SAND, MOUNTAIN, TREE};
        for (TETile tile: tileset) {
            if (tile.character() == c) {
                return tile;
            }
        }
        throw new RuntimeException("No tile in Tileset with character " + c);
    }

}


