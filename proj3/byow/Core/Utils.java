package byow.Core;

import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

import java.io.File;
import java.io.IOException;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;

/** @source proj 2 - Gitlet */
public class Utils {

    /** The file that stores the most recent saved state of the game */
    public static final File WORLD = new File("world.txt");

    /** The file that stores the most recent saved state of the avatar */
    public static final File AVATAR = new File("avatar.txt");

    /** saves the String representation of the world array */
    public static void save(TETile[][] world, Avatar ava) {
        // save the world
        writeContents(WORLD, TETile.toString(world));

        // save the avatar
        HashMap<String, Integer> avatar = new HashMap<>();
        avatar.put("health", ava.getHealth());
        avatar.put("maxHealth", ava.getMaxHealth());

        writeObject(AVATAR, avatar);

    }

    public static Avatar loadAvatar() {
        if (!AVATAR.exists() || !AVATAR.isFile()) {
            return null;
        }
        HashMap<String, Integer> avatar = readObject(AVATAR, HashMap.class);
        int health = avatar.get("health");
        int maxHealth = avatar.get("maxHealth");
        TETile[][] world = loadWorld();
        for (int x = 0; x < world.length; x++) {
            for (int y = 0; y < world[0].length; y++) {
                if (world[x][y].equals(Tileset.AVATAR)) {
                    return new Avatar(x, y, maxHealth, health);
                }
            }

        }
        return null;
    }

    /** loads the world from its saved String representation */
    public static TETile[][] loadWorld() {
        if (!WORLD.exists() || !WORLD.isFile()) {
            return null;
        }
        String savedWorldString = readContentsAsString(WORLD);
        // split the multi line string into an array of strings by line separator
        String[] lines = savedWorldString.split("\\r?\\n");
        // reverse the lines so the bottom row is first and the top row is last
        Collections.reverse(Arrays.asList(lines));
        // construct the 2D world array
        int height = lines.length;
        int width = lines[0].length();
        TETile[][] world = new TETile[width][height];
        // for every character in the string, construct the matching tile from Tileset in the world
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                String line = lines[y];
                char tileChar = line.toCharArray()[x];
                world[x][y] = Tileset.getTileFromChar(tileChar);
            }
        }
        return world;
    }


    /* ----------------------------------- FILE UTILITIES ----------------------------------- */

    /** Return the concatentation of FIRST and OTHERS into a File designator,
     *  analogous to the {@link java.nio.file.Paths#get(String, String[])}
     *  method. */
    public static File join(String first, String... others) {
        return Paths.get(first, others).toFile();
    }

    /** Return the concatentation of FIRST and OTHERS into a File designator,
     *  analogous to the {@link java.nio.file.Paths#get(String, String[])}
     *  method. */
    static File join(File first, String... others) {
        return Paths.get(first.getPath(), others).toFile();
    }

    /* READING AND WRITING FILE CONTENTS */

    /** Return the entire contents of FILE as a byte array.  FILE must
     *  be a normal file.  Throws IllegalArgumentException
     *  in case of problems. */
    static byte[] readContents(File file) {
        if (!file.isFile()) {
            throw new IllegalArgumentException("must be a normal file");
        }
        try {
            return Files.readAllBytes(file.toPath());
        } catch (IOException excp) {
            throw new IllegalArgumentException(excp.getMessage());
        }
    }

    /** Return the entire contents of FILE as a String.  FILE must
     *  be a normal file.  Throws IllegalArgumentException
     *  in case of problems. */
    static String readContentsAsString(File file) {
        return new String(readContents(file), StandardCharsets.UTF_8);
    }

    /** Write the result of concatenating the bytes in CONTENTS to FILE,
     *  creating or overwriting it as needed.  Each object in CONTENTS may be
     *  either a String or a byte array.  Throws IllegalArgumentException
     *  in case of problems. */
    static void writeContents(File file, Object... contents) {
        try {
            if (file.isDirectory()) {
                throw
                        new IllegalArgumentException("cannot overwrite directory");
            }
            BufferedOutputStream str =
                    new BufferedOutputStream(Files.newOutputStream(file.toPath()));
            for (Object obj : contents) {
                if (obj instanceof byte[]) {
                    str.write((byte[]) obj);
                } else {
                    str.write(((String) obj).getBytes(StandardCharsets.UTF_8));
                }
            }
            str.close();
        } catch (IOException | ClassCastException excp) {
            throw new IllegalArgumentException(excp.getMessage());
        }
    }

    /** Return an object of type T read from FILE, casting it to EXPECTEDCLASS.
     *  Throws IllegalArgumentException in case of problems. */
    static <T extends Serializable> T readObject(File file,
                                                 Class<T> expectedClass) {
        try {
            ObjectInputStream in =
                    new ObjectInputStream(new FileInputStream(file));
            T result = expectedClass.cast(in.readObject());
            in.close();
            return result;
        } catch (IOException | ClassCastException
                | ClassNotFoundException excp) {
            throw new IllegalArgumentException(excp.getMessage());
        }
    }

    /** Write OBJ to FILE. */
    static void writeObject(File file, Serializable obj) {
        writeContents(file, serialize(obj));
    }

    /* SERIALIZATION UTILITIES */

    /** Returns a byte array containing the serialized contents of OBJ. */
    static byte[] serialize(Serializable obj) {
        try {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            ObjectOutputStream objectStream = new ObjectOutputStream(stream);
            objectStream.writeObject(obj);
            objectStream.close();
            return stream.toByteArray();
        } catch (IOException excp) {
            throw new RuntimeException("Internal error serializing");
        }
    }
}
