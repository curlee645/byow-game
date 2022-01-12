package byow.Core;

import java.util.Objects;

public class Room {
    /** Keeps track of each room. */

    /** x value of the bottom left corner tile of the interior/floor of the room */
    private int xCoord;

    /** y value of the bottom left corner tile of the interior/floor of the room */
    private int yCoord;

    /** length of the interior of the room in the x direction starting at xCoord */
    private int xLength;

    /** length of the interior of the room in the y direction starting at yCoord */
    private int yLength;

    /** true if this room is connected to a hallway */
    private boolean isConnected;

    public Room(int x, int y, int xLength, int yLength) {
        this.xCoord = x;
        this.yCoord = y;
        this.xLength = xLength;
        this.yLength = yLength;
        this.isConnected = false;
    }

    /** Finds the distance from this room to another room */
    public double distanceTo(Room otherRoom) {
        return Math.sqrt(
                Math.pow((otherRoom.getXCoord() - this.xCoord), 2)   // (x2 - x1)^2
                + Math.pow((otherRoom.getYCoord() - this.yCoord), 2) // (y2 - y1)^2
        );
    }

    // Getters

    public int getXCoord() {
        return xCoord;
    }

    public int getYCoord() {
        return yCoord;
    }

    public int getXLength() {
        return xLength;
    }

    public int getYLength() {
        return yLength;
    }

    public boolean isConnected() {
        return isConnected;
    }

    // Setters

    public void setXCoord(int xCoordinate) {
        this.xCoord = xCoordinate;
    }

    public void setYCoord(int yCoordinate) {
        this.yCoord = yCoordinate;
    }

    public void setXLength(int newXLength) {
        this.xLength = newXLength;
    }

    public void setYLength(int newYLength) {
        this.yLength = newYLength;
    }

    public void setConnected(boolean connected) {
        isConnected = connected;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Room room = (Room) o;
        return xCoord == room.xCoord && yCoord == room.yCoord
                && xLength == room.xLength && yLength == room.yLength;
    }

    @Override
    public int hashCode() {
        return Objects.hash(xCoord, yCoord, xLength, yLength);
    }

    @Override
    public String toString() {
        return "Room{"
                + "xCoord=" + xCoord
                + ", yCoord=" + yCoord
                + ", xLength=" + xLength
                + ", yLength=" + yLength
                + ", isConnected=" + isConnected
                + '}';
    }
}
