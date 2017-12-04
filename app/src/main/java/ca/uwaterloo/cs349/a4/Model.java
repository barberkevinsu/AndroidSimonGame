package ca.uwaterloo.cs349.a4;

import android.util.Log;
import android.widget.Button;

import java.security.MessageDigest;
import java.util.Observable;
import java.util.Observer;


class Model extends Observable
{
    // Create static instance of this Model
    private static final Model ourInstance = new Model();
    static Model getInstance()
    {
        return ourInstance;
    }

    int n_btn;//number of simon buttons
    Simon simon;
    int difficultyId;//diffcult id: easy :100; normal:200, hard: 300
    String message_to_show;
    String score_to_show;
    int btn_need_click;//btn id that need to be clicked in playactivity
    int playback_speed;//different playback speed according to diffculty id
    char mode;// m or h or f: machine or human or free mode
    int btn_setup;//used to avoid crashing from the first setEnabled due to unfinished buttons initialization

    Model(){
        n_btn = 4;//default
        playback_speed = 800;
        difficultyId = 200;//default
        btn_setup = 0;
        simon = new Simon(n_btn, true);
    }


    public void setDifficultyId(int difficultyId) {
        this.difficultyId = difficultyId;
        if(difficultyId == 100){//easy
            playback_speed = 1200;
        }else if(difficultyId == 200){//normal
            playback_speed = 800;
        }else{//hard
            playback_speed = 400;
        }
        Log.d("Changed: ", "Difficulty is:" + difficultyId);
        updateAll();
    }

    public void setN_btn(int n_btn) {
        if(n_btn >= 1 && n_btn <= 6) {
            this.n_btn = n_btn;
            simon = new Simon(n_btn, true);
            Log.d("Changed: ", "n_btn is " + n_btn);
            updateAll();
        }
    }

    public int getBtn_setup() {
        return btn_setup;
    }

    public void setBtn_setup(int btn_setup) {
        this.btn_setup = btn_setup;
    }

    public int getPlayback_speed() {
        return playback_speed;
    }

    public void setPlayback_speed(int playback_speed) {
        this.playback_speed = playback_speed;
    }

    public int getN_btn() {
        return n_btn;
    }

    public int getDifficultyId(){
        return difficultyId;
    }

    public String getMessage_to_show(){
        return message_to_show;
    }

    public String getScore_to_show() {
        return score_to_show;
    }

    public int getBtn_need_click() {
        return btn_need_click;
    }


    public void setMode(char mode) {
        this.mode = mode;
    }

    public char getMode() {
        return mode;
    }

    public void updateAll()
    {
        setChanged();
        notifyObservers();
    }

    public void setTextAndScore(){
        switch (simon.getState()){
            case START:
                message_to_show = "Press CONTINUE to play or HOME to quit";
                break;
            case WIN:
                message_to_show = "You Won! Press CONTINUE to next round or HOME to quit";
                break;
            case LOSE:
                message_to_show = "You lose. Press CONTINUE to retry or HOME to quit";
                break;
            case HUMAN:
                message_to_show = "Your turn...";
                break;
            case COMPUTER:
                message_to_show = "Watch what I do...";
                break;
            default:
                //should never go here
                break;
        }
        score_to_show = "Your score: " + String.valueOf(simon.getScore());
    }


    //Request nextButton then update state, let view click that button
    public void updateNextButton(){
        if(simon.getState() == State.COMPUTER) {
            setMode('m');//set to CPU mode
            setTextAndScore();
            btn_need_click = simon.nextButton();
            updateAll();
        }else if(simon.getState() == State.HUMAN){
            setMode('h');//set to human mode
            setTextAndScore();
            updateAll();
        }else{
            setMode('f');//ste to free mode
            setTextAndScore();
            updateAll();
        }
    }


    /**
     * Deletes an observer from the set of observers of this object.
     * Passing <CODE>null</CODE> to this method will have no effect.
     *
     * @param o the observer to be deleted.
     */
    @Override
    public synchronized void deleteObserver(Observer o)
    {
        super.deleteObserver(o);
    }

    /**
     * Adds an observer to the set of observers for this object, provided
     * that it is not the same as some observer already in the set.
     * The order in which notifications will be delivered to multiple
     * observers is not specified. See the class comment.
     *
     * @param o an observer to be added.
     * @throws NullPointerException if the parameter o is null.
     */
    @Override
    public synchronized void addObserver(Observer o)
    {
        super.addObserver(o);
    }

    /**
     * Clears the observer list so that this object no longer has any observers.
     */
    @Override
    public synchronized void deleteObservers()
    {
        super.deleteObservers();
    }

    /**
     * If this object has changed, as indicated by the
     * <code>hasChanged</code> method, then notify all of its observers
     * and then call the <code>clearChanged</code> method to
     * indicate that this object has no longer changed.
     * <p>
     * Each observer has its <code>update</code> method called with two
     * arguments: this observable object and <code>null</code>. In other
     * words, this method is equivalent to:
     * <blockquote><tt>
     * notifyObservers(null)</tt></blockquote>
     *
     * @see Observable#clearChanged()
     * @see Observable#hasChanged()
     * @see Observer#update(Observable, Object)
     */
    @Override
    public void notifyObservers()
    {
        super.notifyObservers();
    }
}
