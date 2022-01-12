package byow.Core;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;
import edu.princeton.cs.introcs.StdDraw;

import java.awt.Color;
import java.awt.Font;
import java.util.ArrayList;
import java.util.Random;
import java.util.TreeMap;

/**
 * Method used for exploring a fresh world. This method should handle all inputs,
 * including inputs from the main menu.
 */
public class Engine {

    private final int WIDTH = 80;
    private final int HEIGHT = 40;

    /** Border boundary */
    private final int BOUND = 1;

    /** Top boundary to make room for the HUD */
    private final int TOP_BOUND = 8;

    /** Minimum square room-interior size, meaning smallest possible room is min x min square */
    private final int MIN_ROOM_SIZE = 2;

    /** Number of rooms */
    private final int MAX_NUM_ROOMS = 50;
    
    private boolean interactingWithKeyboard = true;

    //----------------------------------- GUI-related variables -----------------------------------

    private final double guiWidth = 40;
    private final double guiHeight = 40;

    /** The length of the game in seconds */
    private final int gameLength = 60; // one minute is a fun game length

    /** True when the game is over. */
    private boolean gameOver = false;
    private final String gameOverMessage = "Game Over";

    /** True if the user quits the game, either from the main menu or during gameplay */
    private boolean userQuitGame = false;
    private final String quitMessage = "Game has been quit";

    private boolean wonGame = false;
    private final String wonGameMessage = "You Win!";

    private boolean taggerWon = false;
    private final String taggerWonMessage = "Tagger wins!";
    private boolean runnerWon = false;
    private final String runnerWonMessage = "Runner wins!";

    private boolean singlePlayer;


    public static void main(String[] args) {
        Engine engine = new Engine();
//        engine.interactWithInputString(args[0]);
        engine.interactWithKeyboard();
    }

    /**
     * Method used for autograding and testing your code. The input string will be a series
     * of characters (for example, "n123sswwdasdassadwas", "n123sss:q", "lwww". The engine should
     * behave exactly as if the user typed these characters into the engine using
     * interactWithKeyboard.
     * <p>
     * Recall that strings ending in ":q" should cause the game to quite save. For example,
     * if we do interactWithInputString("n123sss:q"), we expect the game to run the first
     * 7 commands (n123sss) and then quit and save. If we then do
     * interactWithInputString("l"), we should be back in the exact same state.
     * <p>
     * In other words, both of these calls:
     * - interactWithInputString("n123sss:q")
     * - interactWithInputString("lww")
     * <p>
     * should yield the exact same world state as:
     * - interactWithInputString("n123sssww")
     *
     * @param input the input string to feed to your program
     * @return the 2D TETile[][] representing the state of the world
     */
    public TETile[][] interactWithInputString(String input) {
        // passed in as an argument, and return a 2D tile representation of the
        // world that would have been drawn if the same inputs had been given
        // to interactWithKeyboard().
        //
        // See proj3.byow.InputDemo for a demo of how you can make a nice clean interface
        // that works for many different input types.

        //* N - create a new world with seed:
        //* S - indicates end of seed
        //* L - load
        //* :Q - quit - saves the world then terminates the program

        interactingWithKeyboard = false;

        InputStringHandler inputHandler = new InputStringHandler(input);

        String first = inputHandler.getNext();

        if (first.equals("n")) {
            singlePlayer = true;
            StringBuilder seedString = new StringBuilder();
            String nextChar = "";
            while (!nextChar.equals("s")) {
                // update the input to await next "key press"
                nextChar = inputHandler.getNext();
                // if key pressed isn't S and shorter than max seed length, update the seed
                if (!nextChar.equals("s") && seedString.length() < 19) {
                    seedString.append(nextChar);
                }
            }
            try {
                long seed = Long.parseLong(seedString.toString());
                return generateWorld(new Random(seed), inputHandler);
            } catch (NumberFormatException e) {
                System.out.println("Seed must consist of numbers");
                return null;
            }
        }


        if (first.equals("l")) {
            return load(inputHandler);
        }

        if (first.equals(":") && inputHandler.next().equals("q")) { // TODO
            gameOver = true;
        }

        return null;
    }

    // --------------------------- End of Interacting with Input String ---------------------------

    public void interactWithKeyboard() {
        //create main menu
        setGUIFrame(guiWidth, guiHeight);
        drawMainMenuPage();
    }

    /**
     * Generates a world of random connected rooms and hallways.
     * Don't need this function since renderFrame generate the world already
     */
    public TETile[][] generateWorld(Random random, InputStringHandler inputHandler) {
        TERenderer ter = new TERenderer();
        if (interactingWithKeyboard) {
            ter.initialize(WIDTH, HEIGHT);
        }

        // initialize tiles
        TETile[][] world = new TETile[WIDTH][HEIGHT];

        // fill world with nothing first
        fillWithNothing(world);

        // Randomly generate 35 to (35+MAX_NUM_ROOMS) random rooms at random coordinates
        ArrayList<Room> rooms = new ArrayList<>(); // keeps track of all rooms created
        int numRooms = 35 + random.nextInt(MAX_NUM_ROOMS);
        for (int x = 0; x < numRooms; x++) {
            // BOUND + and - BOUND is a hardcoded edge/border
            int randomX = BOUND + random.nextInt(WIDTH - BOUND - MIN_ROOM_SIZE);
            int randomY = BOUND + random.nextInt(HEIGHT - BOUND - MIN_ROOM_SIZE - TOP_BOUND);
            rooms.add(drawRoomInterior(randomX, randomY, world, random));
        }

        // connect rooms
        connectRooms(world, random, rooms);

        // generate walls
        drawWalls(world);

        if (singlePlayer) {
            // generate Golden Door
            placeGoldenDoor(world, random);
        }

        // generate avatar
        Avatar ava = generateAvatarInRandomLocation(world, random, Tileset.AVATAR);

        Avatar ava2 = null;
        // generate second avatar if game is multiplayer
        if (!singlePlayer) {
            ava2 = generateAvatarInRandomLocation(world, random, Tileset.TAGGER);
            ava2.setTagger(true);
        }

        if (interactingWithKeyboard) {
            // render world
            ter.renderFrame(world);
        }

        // interact with the avatar
        interactWithWorld(world, ava, ava2, inputHandler);

        return world;

    }

    private TETile[][] load(InputStringHandler inputHandler) {
        TERenderer ter = new TERenderer();
        if (interactingWithKeyboard) {
            ter.initialize(WIDTH, HEIGHT);
        }

        // load the world and avatar
        TETile[][] world = Utils.loadWorld();
        Avatar ava = Utils.loadAvatar();

        // if either is null, quit the game
        if (world == null || ava == null) {
            drawGameOverPage("No game to load", guiWidth, guiHeight);
        } else {
            if (interactingWithKeyboard) {
                // render world
                ter.renderFrame(world);
            }
            // interact with the avatar
            interactWithWorld(world, ava, null, inputHandler);
        }

        return world;
    }

    /**
     * Fills the world with NOTHING tiles.
     */
    public void fillWithNothing(TETile[][] world) {
        int width = world.length;
        int height = world[0].length;
        for (int x = 0; x < width; x += 1) {
            for (int y = 0; y < height; y += 1) {
                world[x][y] = Tileset.NOTHING;
            }
        }
    }

    /**
     * Fills the world with NOTHING tiles.
     */
    public void fillWithBlack(TETile[][] world) {
        int width = world.length;
        int height = world[0].length;
        for (int x = 0; x < width; x += 1) {
            for (int y = 0; y < height; y += 1) {
                world[x][y] = new TETile(' ', Color.black, Color.black, "nothing");
            }
        }
    }

    /**
     * Draws a random room with bottom left corner at (x, y)
     *
     * @param x The x-coordinate of the room (bottom left corner of the interior)
     * @param y The y-coordinate of the room (bottom left corner of the interior)
     */
    public Room drawRoomInterior(int x, int y, TETile[][] world, Random random) {
        // Random X and Y lengths of the room
//        int lengthBound = 1 + RANDOM.nextInt(3);
        int xLength = MIN_ROOM_SIZE + random.nextInt(4);
        int yLength = MIN_ROOM_SIZE + random.nextInt(4);

        // fill in the room's interior
        for (int xCoord = x; xCoord < xLength + x; xCoord += 1) {
            if (xCoord >= WIDTH - BOUND) {
                // Room's x-coordinate is out of bounds if xCoord = roomX
                continue;
            }
            for (int yCoord = y; yCoord < yLength + y; yCoord += 1) {
                if (yCoord >= HEIGHT - BOUND) {
                    // Room's y-coordinate is out of bounds if yCoord = roomY
                    continue;
                }
                // switch case for hazard tiles for 1/20 chance
                int specialChance = random.nextInt(100);
                if (specialChance == 0) {
                    // ^ Heal tiles have a 1% chance of generating
                    world[xCoord][yCoord] = Tileset.HEAL;
                } else if (1 < specialChance && specialChance < 7) {
                    // ^ Spike tiles have a 5% chance of generating
                    world[xCoord][yCoord] = Tileset.SPIKES;
                } else {
                    world[xCoord][yCoord] = Tileset.FLOOR;
                }
            }
        }

        // Create the Room and add it to the list of rooms
        Room newRoom = new Room(x, y, xLength, yLength);
        return newRoom;
    }

    /**
     * Connects generated rooms with 1-width hallways.
     */
    // can be optimized by stopping on the way of generating a hallway if we bump into
    //  another floor tile
    public void connectRooms(TETile[][] world, Random random, ArrayList<Room> rooms) {
        for (Room room : rooms) {
            Room closestRoom = getClosestRoomTo(room, rooms);
            int startingX = room.getXCoord();
            int startingY = room.getYCoord();
            int endingX = closestRoom.getXCoord();
            int endingY = closestRoom.getYCoord();

            int choice = random.nextInt(3);
            switch (choice) {
                case 0:
                    connectXthenY(startingX, startingY, endingX, endingY, world);
                    room.setConnected(true);
                    break;

                case 1:
                    connectYthenX(startingX, startingY, endingX, endingY, world);
                    room.setConnected(true);
                    break;

                case 2:
                    connectXthenY(startingX, startingY, endingX, endingY, world);
                    connectYthenX(startingX, startingY, endingX, endingY, world);
                    room.setConnected(true);
                    break;

                default:
                    break;
            }
        }
    }

    private void connectXthenY(
            int startingX, int startingY, int endingX, int endingY, TETile[][] world) {
        // go from starting x to ending x,
        if (endingX < startingX) {
            for (int xCoord = startingX; xCoord > endingX; xCoord--) {
//                            if (world[xCoord][startingY].equals(Tileset.FLOOR)) { // TODO
//                                continue;
//                            }
                world[xCoord][startingY] = Tileset.FLOOR;
            }
        } else {
            for (int xCoord = startingX; xCoord < endingX; xCoord++) {
                world[xCoord][startingY] = Tileset.FLOOR;
            }
        }
        // then starting y to ending y
        if (endingY < startingY) {
            for (int yCoord = startingY; yCoord > endingY; yCoord--) {
                world[endingX][yCoord] = Tileset.FLOOR;
            }
        } else {
            for (int yCoord = startingY; yCoord < endingY; yCoord++) {
                world[endingX][yCoord] = Tileset.FLOOR;
            }
        }
    }

    private void connectYthenX(
            int startingX, int startingY, int endingX, int endingY, TETile[][] world) {
        // go from starting y to ending y,
        if (endingY < startingY) {
            for (int yCoord = startingY; yCoord > endingY; yCoord--) {
                world[startingX][yCoord] = Tileset.FLOOR;
            }
        } else {
            for (int yCoord = startingY; yCoord < endingY; yCoord++) {
                world[startingX][yCoord] = Tileset.FLOOR;
            }
        }
        // then starting x to ending x
        if (endingX < startingX) {
            for (int xCoord = startingX; xCoord > endingX; xCoord--) {
                world[xCoord][endingY] = Tileset.FLOOR;
            }
        } else {
            for (int xCoord = startingX; xCoord < endingX; xCoord++) {
                world[xCoord][endingY] = Tileset.FLOOR;
            }
        }
    }

    /**
     * Returns the room closest in distance to the given room
     */
    private Room getClosestRoomTo(Room room, ArrayList<Room> rooms) {
        TreeMap<Double, Room> distances = new TreeMap<>();
        rooms.forEach(r -> {
            if (!r.equals(room)) {
                distances.put(room.distanceTo(r), r);
            }
        });
        // return first key in distances for which the value.isConnected() is false
        for (Double key : distances.keySet()) {
            Room r = distances.get(key);
            if (!r.isConnected()) {
                return distances.get(key);
            }
        }
        return room;
    }

    public void drawWalls(TETile[][] world) {
        //if it is a floor tile, then draw wall tiles adjacent to the floor tiles
        for (int y = 0; y < HEIGHT; y++) {
            for (int x = 0; x < WIDTH; x++) {
                if (world[x][y].equals(Tileset.FLOOR) || world[x][y].equals(Tileset.SPIKES)
                        || world[x][y].equals(Tileset.HEAL)) {
                    for (int surrY = y + 1; surrY >= y - 1; surrY--) {
                        for (int surrX = x - 1; surrX <= x + 1; surrX++) {
                            if (world[surrX][surrY].equals(Tileset.NOTHING)) {
                                world[surrX][surrY] = Tileset.WALL;
                            }
                        }
                    }
                }
            }
        }
    }

    public void placeGoldenDoor(TETile[][] world, Random rand) {
        int doorXCoord = rand.nextInt(WIDTH);
        int doorYCoord = rand.nextInt(HEIGHT);
        while (!world[doorXCoord][doorYCoord].equals(Tileset.WALL)
                /*|| checkIfWallIsCorner(world, doorXCoord, doorYCoord)*/) {
            doorXCoord = rand.nextInt(WIDTH);
            doorYCoord = rand.nextInt(HEIGHT);
        }
        world[doorXCoord][doorYCoord] = Tileset.GOLDEN_DOOR;
    }

    /**
     * Updates the HUD
     */
    public void updateHUD(String tile, TETile[][] world, Avatar ava, Avatar ava2,
                          long elapsedTime) {
        long seconds = (gameLength * 1000 - elapsedTime) / 1000;
        double minutes = seconds / 60.0;

        String formattedTime =
                (int) Math.floor(minutes) + ":" + (int) ((minutes - Math.floor(minutes)) * 60);

        StdDraw.clear(Color.BLACK);
        // instead of rendering the world here, we call tunnelVision because tunnelVision renders
        // the correct version of the world (with our without tunnel vision)
        if (ava2 != null) {
            tunnelVision(world, ava, ava2);
        } else {
            tunnelVision(world, ava);
        }
        // create a black HUD background
        for (int x = 0; x < WIDTH; x++) {
            for (int y = HEIGHT - BOUND; y > HEIGHT - 5; y--) {
                world[x][y] = new TETile(' ', Color.black, Color.black, "nothing");
            }
        }
        // draw the text
        StdDraw.setPenColor(Color.WHITE);
        StdDraw.setFont(new Font("Arial", Font.BOLD, 20));
        // draw avatar health
        for (int i = 0; i < ava.getHealth(); i++) {
            StdDraw.text((double) WIDTH / 8 + i * 2,
                    HEIGHT - (double) TOP_BOUND / 4, "♡");
        }
        if (!singlePlayer) {
            StdDraw.setFont(new Font("Arial", Font.BOLD, 15));
            StdDraw.text((double) WIDTH / 2, HEIGHT - (double) TOP_BOUND / 6, formattedTime);
        }

        StdDraw.setFont(new Font("Arial", Font.BOLD, 20));
        StdDraw.text((double) WIDTH / 2, HEIGHT - (double) TOP_BOUND / 3, tile);
        // draw avatar 2 health
        if (ava2 != null) {
            for (int i = 0; i < ava2.getHealth(); i++) {
                StdDraw.text((double) WIDTH / 8 * 7 + i * 2, HEIGHT - (double) TOP_BOUND / 4, "♡");
            }
        }
        StdDraw.setFont();
        StdDraw.show();
    }


    /**
     * Debugging method for comparing two worlds
     */
    private boolean compareTwoWorlds(TETile[][] test1, TETile[][] test2) {
        boolean same = true;
        for (int i = 0; i < test1.length; i++) {
            for (int j = 0; j < test1[0].length; j++) {
                if (!test1[i][j].equals(test2[i][j])) {
                    System.out.println("arrays were different at: (" + i + ", " + j + ")");
                    System.out.println("1: " + test1[i][j] + " desc: " + test1[i][j].description());
                    System.out.println("2: " + test2[i][j] + " desc: " + test2[i][j].description());
                    System.out.println("-------------------------------------------------");
                    if (same) {
                        same = false;
                    }
                }
            }
        }
        return same;
    }

    /**
     * when using an input string, we want awaitKeyPress to just return the next character in the
     * input string. This way we can actually simulate keyboard interactions instead of
     * hardcoding the desired behavior twice for every command,
     * once for interactWithInputString and once for interactWithKeyboard.
     */
    public String simulateAwaitKeyPress(String inputString) {
        throw new UnsupportedOperationException();
    }


    /**
     * awaits a key press from the user
     */
    public String awaitKeyPress() {
        while (!StdDraw.hasNextKeyTyped()) {
//            StdDraw.pause(1);
        }
        return String.valueOf(StdDraw.nextKeyTyped()).toLowerCase();
    }

    /**
     * gets a key press from the user (without waiting)
     */
    public String getKeyPress() {
        if (StdDraw.hasNextKeyTyped()) {
            return String.valueOf(StdDraw.nextKeyTyped()).toLowerCase();
        }
        return "";
    }

    private void processSeed() {
        // go to different screen and prompt user to input a seed
        String seedString = "";
        drawSeedPage("");
        String input = "";
        while (!input.equals("s")) {
            // update the input to await key press
            input = awaitKeyPress();
            // input must be a number
            try {
                if (!input.equals("s")) {
                    int in = Integer.parseInt(input);
                } else if (seedString.isBlank()) {
                    throw new RuntimeException();
                }
            } catch (NumberFormatException e) {
                drawSeedPage("Seed must consist only of numbers.");
                StdDraw.pause(1000);
                input = "";
                seedString = "";
                drawSeedPage("");
            } catch (RuntimeException e) {
                drawSeedPage("Seed must not be blank.");
                StdDraw.pause(1000);
                input = "";
                seedString = "";
                drawSeedPage("");
            }
            // if key pressed isn't S and shorter than max seed length, update the screen
            if (!input.equals("s") && seedString.length() < 19) {
                seedString += input;
                drawSeedPage(seedString);
            }
        }
        // redundant try/catch because the seed is already checked but may as well keep it
        try {
            long seed = Long.parseLong(seedString);
            generateWorld(new Random(seed), null);
        } catch (NumberFormatException e) {
            drawSeedPage("Invalid seed. World generation failed.");
        }
    }

    public void executeMenuUserInput() {
        // if key pressed is `n`, await and get next keys that are ints until key pressed is `s`
        String userInput = awaitKeyPress();

        if (userInput.equals("n")) {
            singlePlayer = true;
            processSeed();
        }

        if (userInput.equals("m")) {
            singlePlayer = false;
            tunnelVisionOn = true;
            processSeed();
        }


        if (userInput.equals("l")) {
            load(null);
        }

        if (userInput.equals("i")) {
            drawInstructionsPage();
        }

        if (userInput.equals("q")) {
            drawGameOverPage(quitMessage, guiWidth, guiHeight);
            gameOver = true;
        }
    }

//    public TETile getTileOfAvatar(TETile[][] world, Avatar ava) {
//        TETile avatarTile = world[ava.getXCoord()][ava.getYCoord()];
//        if (avatarTile.equals(Tileset.AVATAR)) {
//            return avatarTile;
//        } else {
//            throw new RuntimeException("tile at Avatar's (x, y) is not a Tileset.AVATAR");
//        }
//    }

    public void interactWithWorld(TETile[][] world, Avatar ava, Avatar ava2,
                                  InputStringHandler inputHandler) {

        long startTime = System.currentTimeMillis();

        while (!gameOver) {
            // takes in all updates that change the world and updates gameOver accordingly

            long currTime = System.currentTimeMillis();
            long elapsedTime = currTime - startTime;
            if (elapsedTime >= gameLength * 1000) {
                runnerWon = true;
                gameOver = true;
            }

            if (ava.getHealth() == 0 || (ava2 != null && ava2.getHealth() == 0)) {
                gameOver = true;
            }
            if (singlePlayer) {
                if (ava.getHealth() == 0) {
                    gameOver = true;
                }
            } else {
                if (ava.getHealth() == 0) {
                    if (ava.isTagger()) {
                        runnerWon = true;
                    } else {
                        taggerWon = true;
                    }
                }
                if (ava2 != null && ava2.getHealth() == 0) {
                    if (ava2.isTagger()) {
                        runnerWon = true;
                    } else {
                        taggerWon = true;
                    }
                }
            }

            if (ava.won() || (ava2 != null && ava2.won())) {
                wonGame = true;
                gameOver = true;
            }

            if (taggerWon || runnerWon) {
                gameOver = true;
            }

            /** Player gameplay Interaction **/
            String userInput;
            if (inputHandler == null) {
                userInput = getKeyPress();
            } else {
                userInput = inputHandler.getNext();
                if (userInput.isBlank()) {
                    gameOver = true;
                }
            }

            if (userInput.equals("w") || userInput.equals("a") || userInput.equals("s")
                    || userInput.equals("d")) {
                showMovement(userInput, world, ava);
            }
            if (ava2 != null) {
                if (userInput.equals("i") || userInput.equals("j") || userInput.equals("k")
                        || userInput.equals("l")) {
                    showMovement(userInput, world, ava2);
                }
            }

            if (inputHandler == null) {
                /** Toggle tunnel vision **/
                if (userInput.equals("t")) {
                    if (tunnelVisionOn) {
                        tunnelVisionOn = false;
                    } else {
                        tunnelVisionOn = true;
                    }
                }

                /** HUD **/
                // get the mouse location
                int x = (int) Math.abs(Math.ceil(StdDraw.mouseX() - 1));
                int y = (int) Math.abs(Math.ceil(StdDraw.mouseY() - 1));
                // update the HUD with the tile at mouse location
                updateHUD(world[x][y].description(), world, ava, ava2, elapsedTime);
            }

            /** Quit Interaction **/
            if (userInput.equals(":")) {
                if (inputHandler == null) {
                    if (awaitKeyPress().equals("q")) {
                        Utils.save(world, ava);
                        gameOver = true;
                        userQuitGame = true;
                    }
                } else {
                    if (inputHandler.getNext().equals("q")) {
                        Utils.save(world, ava);
                        gameOver = true;
                        userQuitGame = true;
                    }
                }
            }
        }
        if (inputHandler == null) {
            if (userQuitGame) {
                drawGameOverPage(quitMessage, WIDTH, HEIGHT);
            } else if (!singlePlayer) {
                if (taggerWon) {
                    drawGameOverPage(taggerWonMessage, WIDTH, HEIGHT);
                }
                if (runnerWon) {
                    drawGameOverPage(runnerWonMessage, WIDTH, HEIGHT);
                }
            } else if (wonGame) {
                drawGameOverPage(wonGameMessage, WIDTH, HEIGHT);
            } else {
                drawGameOverPage(gameOverMessage, WIDTH, HEIGHT);
            }
        }
    }

    public void setGUIFrame(double width, double height) {
        /* Sets up StdDraw so that it has a width by height grid of 16 by 16 squares as its canvas
         * Also sets up the scale so the top left is (0,0) and the bottom right is (width, height)
         */
        StdDraw.setCanvasSize((int) width * 16, (int) height * 16);
        Font font = new Font("Monaco", Font.BOLD, 30);
        StdDraw.setFont(font);
        StdDraw.setXscale(0, width);
        StdDraw.setYscale(0, height);
        StdDraw.clear(Color.BLACK);
        StdDraw.enableDoubleBuffering();
    }

    public void drawMainMenuPage() {
        // clears the canvas
        StdDraw.clear(Color.BLACK);
        StdDraw.setPenColor(Color.WHITE);

        // draw everything necessary for the next frame
        Font titleFont = new Font("Arial", Font.BOLD, 30);
        StdDraw.setFont(titleFont);
        StdDraw.text(guiWidth / 2, 3 * guiHeight / 4, "CS61B: The Game");

        StdDraw.setFont(new Font("Arial", Font.PLAIN, 15));
        StdDraw.text(guiWidth / 2, 3 * guiHeight / 4 - guiHeight / 20,
                "By Ravi Riley and Curtis Lee");

        Font menuFont = new Font("Arial", Font.BOLD, 20);
        StdDraw.setFont(menuFont);
        StdDraw.text(guiWidth / 2, guiHeight / 2 + 2 * guiHeight / 20, "New Game (N)");
        StdDraw.text(guiWidth / 2, guiHeight / 2 + 1 * guiHeight / 20, "New Multiplayer Game (M)");
        StdDraw.text(guiWidth / 2, guiHeight / 2, "Load Game (L)");
        StdDraw.text(guiWidth / 2, guiHeight / 2 - 1 * guiHeight / 20, "Instructions (I)");
        StdDraw.text(guiWidth / 2, guiHeight / 2 - 2 * guiHeight / 20, "Quit (Q)");

        // show canvas
        StdDraw.show();

        //process input from main menu
        executeMenuUserInput();
    }

    public void drawInstructionsPage() {
        StdDraw.clear(Color.BLACK);
        StdDraw.setPenColor(Color.WHITE);

        //draw everything necessary for the next frame
        Font titleFont = new Font("Arial", Font.BOLD, 30);
        StdDraw.setFont(titleFont);
        StdDraw.text(guiWidth / 2, 3 * guiHeight / 4, "Game Instructions");

        Font menuFont = new Font("Arial", Font.BOLD, 15);
        StdDraw.setFont(menuFont);
        StdDraw.text(guiWidth / 2, guiHeight / 2 + 2 * guiHeight / 20, "Welcome to INSANE TAG!");
        StdDraw.text(guiWidth / 2, guiHeight / 2 + guiHeight / 20, "You will spawn randomly on "
                + "the map and your goal is to reach one of the golden doors.");
        StdDraw.text(guiWidth / 2, guiHeight / 2, //
                "A CPU will try and catch you. Try to not get tagged ;)");
        StdDraw.text(guiWidth / 2, guiHeight / 2 - 1 * guiHeight / 20,
                "Along the way, keep an eye out for power ups but beware of booby traps.");
        StdDraw.text(guiWidth / 2, guiHeight / 2 - 2 * guiHeight / 20, "Good luck and have fun!");
        StdDraw.text(guiWidth / 2, guiHeight / 2 - 4 * guiHeight / 20, "Back to Main Menu (B)");

        StdDraw.show();


        while (!awaitKeyPress().equals("b")) {
            // wait until its b
        }
        drawMainMenuPage();
    }

    public void drawSeedPage(String s) {
        StdDraw.clear(Color.BLACK);
        StdDraw.setPenColor(Color.WHITE);

        //draw everything necessary for the next frame
        Font titleFont = new Font("Arial", Font.BOLD, 20);
        StdDraw.setFont(titleFont);
        StdDraw.text(guiWidth / 2, 3 * guiHeight / 4, "Input Seed Number from");
        StdDraw.text(guiWidth / 2, 3 * guiHeight / 4 - guiHeight / 20,
                "-9,223,372,036,854,775,807 to 9,223,372,036,854,775,807");
        StdDraw.text(guiWidth / 2, 3 * guiHeight / 4 - 2 * guiHeight / 20,
                "then press (S) to start the game");

        StdDraw.text(guiWidth / 2, guiHeight / 2, s);

        StdDraw.show();
    }

    public void drawGameOverPage(String message, double width, double height) {
        StdDraw.clear(Color.BLACK);
        StdDraw.setPenColor(Color.WHITE);

        Font titleFont = new Font("Arial", Font.BOLD, 30);
        StdDraw.setFont(titleFont);
        StdDraw.text(width / 2, height / 2, message);

        StdDraw.show();
    }

    /**
     * Generates the avatar in a valid random location on the map
     */
    public Avatar generateAvatarInRandomLocation(TETile[][] world, Random rand, TETile avatarTile) {
        int randXCoord = rand.nextInt(WIDTH);
        int randYCoord = rand.nextInt(HEIGHT);
        while (!world[randXCoord][randYCoord].equals(Tileset.FLOOR)) {
            randXCoord = rand.nextInt(WIDTH);
            randYCoord = rand.nextInt(HEIGHT);
        }
        world[randXCoord][randYCoord] = avatarTile;
        Avatar ava = new Avatar(randXCoord, randYCoord, 3);
        return ava;
    }

    /** Tunnel Vision feature */
    private int visionRadius = 3;
    private boolean tunnelVisionOn = false;

    public void tunnelVision(TETile[][] world, Avatar ava) {
        TERenderer ter = new TERenderer();
        TETile[][] currWorld = TETile.copyOf(world);
        if (tunnelVisionOn) {
            fillWithBlack(currWorld);
            for (int y = ava.getYCoord() - visionRadius; y <= ava.getYCoord() + visionRadius; y++) {
                for (int x = ava.getXCoord() - visionRadius; x <= ava.getXCoord() + visionRadius;
                     x++) {
                    if (x < 0 || x >= WIDTH || y < 0 || y >= HEIGHT) {
                        // if x or y is out of the world, skip it
                        continue;
                    }
                    currWorld[x][y] = world[x][y];
                }
            }
            ter.renderFrame(currWorld);
        } else {
            ter.renderFrame(world);
        }
    }

    public void tunnelVision(TETile[][] world, Avatar ava, Avatar ava2) {
        TERenderer ter = new TERenderer();
        TETile[][] currWorld = TETile.copyOf(world);
        if (tunnelVisionOn) {
            fillWithBlack(currWorld);
            for (int y = ava.getYCoord() - visionRadius; y <= ava.getYCoord() + visionRadius; y++) {
                for (int x = ava.getXCoord() - visionRadius; x <= ava.getXCoord() + visionRadius;
                     x++) {
                    if (x < 0 || x >= WIDTH || y < 0 || y >= HEIGHT) {
                        // if x or y is out of the world, skip it
                        continue;
                    }
                    currWorld[x][y] = world[x][y];
                }
            }
            for (int y = ava2.getYCoord() - visionRadius; y <= ava2.getYCoord() + visionRadius; y++) {
                for (int x = ava2.getXCoord() - visionRadius; x <= ava2.getXCoord() + visionRadius;
                     x++) {
                    if (x < 0 || x >= WIDTH || y < 0 || y >= HEIGHT) {
                        // if x or y is out of the world, skip it
                        continue;
                    }
                    currWorld[x][y] = world[x][y];
                }
            }
            ter.renderFrame(currWorld);
        } else {
            ter.renderFrame(world);
        }
    }

    public void showMovement(String s, TETile[][] world, Avatar ava) {
        if (s.equals("w") || s.equals("i")) {
            int nextXCoord = ava.getXCoord();
            int nextYCoord = ava.getYCoord() + 1;
            moveAvatar(nextXCoord, nextYCoord, world, ava);
        }

        if (s.equals("a") || s.equals("j")) {
            int nextXCoord = ava.getXCoord() - 1;
            int nextYCoord = ava.getYCoord();
            moveAvatar(nextXCoord, nextYCoord, world, ava);
        }

        if (s.equals("s") || s.equals("k")) {
            int nextXCoord = ava.getXCoord();
            int nextYCoord = ava.getYCoord() - 1;
            moveAvatar(nextXCoord, nextYCoord, world, ava);
        }

        if (s.equals("d") || s.equals("l")) {
            int nextXCoord = ava.getXCoord() + 1;
            int nextYCoord = ava.getYCoord();
            moveAvatar(nextXCoord, nextYCoord, world, ava);
        }

        // render world after moving avatar
//        TERenderer ter = new TERenderer();
//        ter.renderFrame(world);
        // this rendering code is redundant because the world is rendered by updateHUD
        // so it was causing the HUD to flash when the player moves
    }

    /** Moves the avatar to (nextXCoord, nextYCoord) */
    private void moveAvatar(int nextXCoord, int nextYCoord, TETile[][] world, Avatar ava) {
        if (!checkIfWall(world, nextXCoord, nextYCoord)) {
            // set current tile to floor
            world[ava.getXCoord()][ava.getYCoord()] = Tileset.FLOOR;
            // replace the tile the avatar is moving to with the avatar
            if (world[nextXCoord][nextYCoord].equals(Tileset.HEAL)) {
                ava.gainHealth();
            }
            if (world[nextXCoord][nextYCoord].equals(Tileset.SPIKES)) {
                ava.loseHealth();
            }
            if (world[nextXCoord][nextYCoord].equals(Tileset.GOLDEN_DOOR)) {
                ava.foundDoor();
            }
            if (world[nextXCoord][nextYCoord].equals(Tileset.AVATAR)
                    || world[nextXCoord][nextYCoord].equals(Tileset.TAGGER)) {
                taggerWon = true;
            }
            if (ava.isTagger()) {
                world[nextXCoord][nextYCoord] = Tileset.TAGGER;
            } else {
                world[nextXCoord][nextYCoord] = Tileset.AVATAR;
            }

            // update the avatar's coordinates
            ava.setXCoord(nextXCoord);
            ava.setYCoord(nextYCoord);
        }
    }

    public boolean checkIfWall(TETile[][] world, int x, int y) {
        if (world[x][y].equals(Tileset.WALL)) {
            return true;
        }
        return false;
    }

    public boolean checkIfWallIsCorner(TETile[][] world, int x, int y) {
        if (world[x][y].equals(Tileset.WALL)) {
            if (
                    world[x + 1][y] != null
                    && world[x - 1][y] != null
                    && world[x][y + 1] != null
                    && world[x][y - 1] != null
                    && !world[x + 1][y].equals(Tileset.FLOOR)
                    && !world[x - 1][y].equals(Tileset.FLOOR)
                    && !world[x][y + 1].equals(Tileset.FLOOR)
                    && !world[x][y - 1].equals(Tileset.FLOOR)
            ) {
                return true;
            }
        }
        return false;
    }
}
