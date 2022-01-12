package byow.lab12;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;


/**
 * Draws a world consisting of hexagonal regions.
 */
public class HexWorld {

    public static void main(String[] args) {
        TERenderer ter = new TERenderer();
        ter.initialize(50, 50);
        Hexagon test = new Hexagon(3, 0, 0, Tileset.WALL, ter);
    }

    public static class Hexagon {
        int size;
        int x;
        int y;
        int width;
        TETile terrain;

        public Hexagon(int size, int x, int y, TETile terrain, TERenderer ter) {
            this.size = size;
            this.x = x;
            this.y = y;
            this.terrain = terrain;

            this.width = size * 3 - 2;

            TETile[][] canvas = new TETile[50][50];
            // initialize tiles
            for (int i = 0; i < 50; i++) {
                for (int j = 0; j < 50; j++) {
                    canvas[i][j] = Tileset.NOTHING;
                }
            }

            // draw the first half of the hexagon
            for (int row = 0 + 10; row < size + 10; row++) {
                int blankSpace = (width - size) / 2;
//                for (int col = 0; col < blankSpace; col++) {
//                    canvas[col][row] = Tileset.NOTHING;
//                }
                for (int col = blankSpace; col < size + 2 * row - blankSpace; col++) {
                    canvas[col][row] = terrain;
                }
//                for (int col = 0; col < blankSpace; col++) {
//                    canvas[col][row] = Tileset.NOTHING;
//                }
            }
            ter.renderFrame(canvas);
        }

        // 2 4 4 2 - 4 rows 4+0
        // 3 5 7 7 5 3 - 6 rows 6+1
        // 4 6 8 10 10 8 6 4 - 8 rows 8+2
        // 5 7 9 11 13 13 11 9 7 5 - 10 rows 10+3
    }

    public static void addHexagon(int s) {
        int numRows = s * 2; // always even
        int rowLength = s * 3 - 2;
        for (int i = 0; i < s; i++) {
            for (int j = 0; j < (2 * i + s); j++) {
                System.out.print("a");
            }
            System.out.println();
        }
        for (int i = 0; i < s; i++) {
            for (int j = rowLength - 2 * i; j > 0; j--) {
                System.out.print("a");
            }
            System.out.println();
        }
    }
}
