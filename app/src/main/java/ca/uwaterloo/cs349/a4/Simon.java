package ca.uwaterloo.cs349.a4;
import android.util.Log;
import java.util.*;

import static ca.uwaterloo.cs349.a4.State.COMPUTER;
import static ca.uwaterloo.cs349.a4.State.HUMAN;
import static ca.uwaterloo.cs349.a4.State.LOSE;
import static ca.uwaterloo.cs349.a4.State.START;
import static ca.uwaterloo.cs349.a4.State.WIN;


public class Simon {

    // the game state and score
    State state;
    int score;

    // length of sequence
    int length;
    // number of possible buttons
    int buttons;

    // the sequence of buttons and current button
    List<Integer> sequence;
    int index;

    boolean debug;

    public void init(int _buttons, boolean _debug){

        // true will output additional information
        // (you will want to turn this off before)
        debug = _debug;

        length = 1;
        buttons = _buttons;
        state = START;
        score = 0;
        sequence = new ArrayList<>();

        if (debug) { Log.d("[DEBUG]", " Starting " + buttons + " button game"); }
    }


    public Simon(int _buttons) { init(_buttons, false); }

    public Simon(int _buttons, boolean _debug) { init(_buttons, _debug); }


    int getNumButtons() { return buttons; }

    int getScore() { return score; }

    State getState() { return state; }

    String getStateAsString() {

        switch (getState()) {

            case START:
                return "START";

            case COMPUTER:
                return "COMPUTER";

            case HUMAN:
                return "HUMAN";

            case LOSE:
                return "LOSE";

            case WIN:
                return "WIN";
            default:
                return "Unknown State";
        }
    }

    void newRound() {

        if (debug) {
            Log.d("[DEBUG]", "newRound, Simon::state " + this.getStateAsString());
        }

        // reset if they lost last time
        if (state == LOSE) {
            if (debug) { Log.d("[DEBUG]", " reset length and score after loss" ); }
            length = 1;
            score = 0;
        }

        sequence.clear();


        for (int i = 0; i < length; i++) {
            Random rand = new Random();
            int b = rand.nextInt(buttons) + 1;

            sequence.add(b);
            if (debug) { Log.d("b:", String.valueOf(b)); }
        }
        if (debug) { Log.d("", "\n"); }

        index = 0;
        state = COMPUTER;

    }

    // call this to get next button to show when computer is playing
    int nextButton() {

        if (state != COMPUTER) {
            Log.d("[WARNING]", " nextButton called in " + getStateAsString());
            return -1;
        }

        // this is the next button to show in the sequence
        int button = sequence.get(index);

        if (debug) {
            Log.d("[DEBUG]", " nextButton:  index " + index + " button " + button);
        }

        // advance to next button
        index++;

        // if all the buttons were shown, give
        // the human a chance to guess the sequence
        if (index >= sequence.size()) {
            index = 0;
            state = HUMAN;
        }

        return button;
    }

    boolean verifyButton(int button) {

        if (state != HUMAN) {
            Log.d("[WARNING]", " verifyButton called in " + getStateAsString());
            return false;
        }

        // did they press the right button?
        boolean correct = (button == sequence.get(index));

        if (debug) {
            Log.d("[DEBUG]", " verifyButton: index " + index
                    + ", pushed " + button
                    + ", sequence " + sequence.get(index));
        }

        // advance to next button
        index++;

        // pushed the wrong buttons
        if (!correct) {
            state = LOSE;
            if (debug) {
                Log.d("", ", wrong. ");
                Log.d("[DEBUG]", " state is now " + getStateAsString());
            }

            // they got it right
        } else {
            if (debug) { Log.d("", " correct."); }

            // if last button, then the win the round
            if (index == sequence.size()) {
                state = WIN;
                // update the score and increase the difficulty
                score++;
                length++;

                if (debug) {
                    Log.d("[DEBUG]", " state is now " + getStateAsString());
                    Log.d("[DEBUG]", " new score " + score
                            + ", length increased to " + length);
                }
            }
        }
        return correct;
    }

}



