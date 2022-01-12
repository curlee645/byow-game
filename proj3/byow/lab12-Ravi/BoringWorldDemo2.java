package byow.lab12;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

import java.util.Random;

/**
 *  Draws a world that is mostly empty except for a small region.
 */
public class BoringWorldDemo2 {

    private static final int WIDTH = 60;
    private static final int HEIGHT = 30;

    private static final long SEED = 1873123;
    private static final Random RANDOM = new Random(SEED);

    public static void main(String[] args) {
        // initialize the tile rendering engine with a window of size WIDTH x HEIGHT
        TERenderer ter = new TERenderer();
        ter.initialize(WIDTH, HEIGHT);

        // initialize tiles
        TETile[][] world = new TETile[WIDTH][HEIGHT];
        for (int x = 0; x < WIDTH; x += 1) {
            for (int y = 0; y < HEIGHT; y += 1) {
                world[x][y] = Tileset.NOTHING;
            }
        }

        // make a room
        int min = 3;
        int xAnchor = 10;
        int yAnchor = 10;
        drawRoom(xAnchor, yAnchor, world);
        drawHorizontalHallway(20, 20, world);
        drawVerticalHallway(5, 5, world);



        // draws the world to the screen
        ter.renderFrame(world);
    }

    private static TETile[][] drawRoom(int x, int y, TETile[][] world) {
        int min = 3;
        int xLength = min + x + RANDOM.nextInt(20);
        int yLength = min + y + RANDOM.nextInt(5);
        return drawHelper(x, y, xLength, yLength, world);
    }

    private static TETile[][] drawHorizontalHallway(int x, int y, TETile[][] world) {
        int min = 3;
        int xLength = min + x + RANDOM.nextInt(20);
        int yLength = min;
        return drawHelper(x, y, xLength, yLength, world);
    }

    private static TETile[][] drawVerticalHallway(int x, int y, TETile[][] world) {
        int min = 3;
        int xLength = min;
        int yLength = min + x + RANDOM.nextInt(20);
        return drawHelper(x, y, xLength, yLength, world);
    }

    private static TETile[][] drawHelper(int x, int y, int xLength, int yLength, TETile[][] world) {
        for (int xx = x; xx < xLength; xx += 1) {
            for (int yy = y; yy < yLength; yy += 1) {
                world[x][y] = Tileset.WALL;
            }
        }
        for (int xx = x + 1; xx < xLength - 1; xx += 1) {
            for (int yy = y + 1; yy < yLength - 1; yy += 1) {
                world[x][y] = Tileset.FLOOR;
            }
        }
        return world;
    }


}
