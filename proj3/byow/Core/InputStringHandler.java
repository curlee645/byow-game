package byow.Core;

public class InputStringHandler {
    private String[] inputString;
    private int counter;
    private int length;

    public InputStringHandler(String inputString) {
        this.inputString = inputString.toLowerCase().split("");
        this.counter = 0;
        this.length = inputString.length();
    }

    public boolean hasNext() {
        return counter < length;
    }

    /** See the next letter in the input string without moving the counter forward */
    public String next() {
        return inputString[counter];
    }

    /** Get the next letter in the input */
    public String getNext() {
        if (hasNext()) {
            String next = inputString[counter];
            counter += 1;
            return next;
        } else {
            return "";
        }
    }
}
