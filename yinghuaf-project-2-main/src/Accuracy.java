import bagel.*;
import java.util.LinkedList;

/**
 * Class for dealing with accuracy of pressing the notes
 */
public class Accuracy {
    public static final int PERFECT_SCORE = 10;
    public static final int GOOD_SCORE = 5;
    public static final int BAD_SCORE = -1;
    public static final int MISS_SCORE = -5;
    public static final int NOT_SCORED = 0;
    public static final String PERFECT = "PERFECT";
    public static final String GOOD = "GOOD";
    public static final String BAD = "BAD";
    public static final String MISS = "MISS";
    public static final String DOUBLE_SCORE = "Double Score";
    public static final String SPEED_UP = "Speed Up";
    public static final String SLOW_DOWN = "Slow Down";
    public static final String BOMB = "Lane Clear";
    private static final int SPECIAL_RADIUS = 50;
    private static final int PERFECT_RADIUS = 15;
    private static final int GOOD_RADIUS = 50;
    private static final int BAD_RADIUS = 100;
    private static final int MISS_RADIUS = 200;
    private static final Font ACCURACY_FONT = new Font(ShadowDance.FONT_FILE, 40);
    private static final int RENDER_FRAMES = 30;
    private static String currAccuracy = null;
    private int frameCount = 0;
    private static int speedChange = 0;
    private static int scoreMultiplier = 1;
    private final int DOUBLE_SCORE_CONTINUE_FRAME = 480;
    private static final LinkedList<Integer> doubleScoreEndFrames = new LinkedList<>();
    private boolean specialNoteMissing = false;

    public static int getSpeedChange(){
        return speedChange;
    }
    public static void reSetSpeedChange(){
        speedChange = 0;
    }
    public static int getScoreMultiplier(){
        scoreMultiplier = 1 << doubleScoreEndFrames.size();
        return scoreMultiplier;
    }
    public static void reSetScoreMultiplier(){
        scoreMultiplier = 1;
    }
    public static void reSetDoubleScoreEndFrame(){
        doubleScoreEndFrames.clear();
    }

    public void triggerDoubleScore() {
        // if doubleScore note is active then add the end frame into the link list
        doubleScoreEndFrames.add(ShadowDance.getCurrFrame() + DOUBLE_SCORE_CONTINUE_FRAME);
    }

    public void setAccuracy(String accuracy) {
        currAccuracy = accuracy;
        frameCount = 0;
    }
    public static void reSetCurrAccuracy(){
        currAccuracy = null;
    }
    public int evaluateScore(int height, int targetHeight, boolean triggered) {
        int distance = Math.abs(height - targetHeight);
        if (triggered) {
            if (distance <= PERFECT_RADIUS) {
                setAccuracy(PERFECT);
                return PERFECT_SCORE;
            } else if (distance <= GOOD_RADIUS) {
                setAccuracy(GOOD);
                return GOOD_SCORE;
            } else if (distance <= BAD_RADIUS) {
                setAccuracy(BAD);
                return BAD_SCORE;
            } else if (distance <= MISS_RADIUS) {
                setAccuracy(MISS);
                return MISS_SCORE;
            }

        } else if (height >= (Window.getHeight())) {
            setAccuracy(MISS);
            return MISS_SCORE;
        }

        return NOT_SCORED;

    }

    /**
     * implement the special effect of special note
     */
    public boolean specialNote(int height, int targetHeight, boolean triggered, String type,String dir){
        int distance = Math.abs(height - targetHeight);

        if (triggered) {
            if (distance <= SPECIAL_RADIUS) {
                switch(type){
                    case "DoubleScore":
                        // if double score note has been activated, then add the end frame to link list
                        triggerDoubleScore();
                        setAccuracy(DOUBLE_SCORE);
                        break;
                    case "SpeedUp":
                        speedChange +=1;
                        setAccuracy(SPEED_UP);
                        ShadowDance.setSpecialNoteScored();
                        break;
                    case "SlowDown":
                        speedChange -=1;
                        setAccuracy(SLOW_DOWN);
                        ShadowDance.setSpecialNoteScored();
                        break;
                    case "Bomb":
                        ShadowDance.setBombActivated(dir);
                        setAccuracy(BOMB);
                        break;
                }
                return true;
            }

        } else if (height >= (Window.getHeight())) {
            specialNoteMissing = true;
            setAccuracy(MISS);
            return true;
        }
        return false;
    }

    public void update() {
        //remove the overtime double score effect
        while (!doubleScoreEndFrames.isEmpty() && doubleScoreEndFrames.peek() <= ShadowDance.getCurrFrame()) {
            doubleScoreEndFrames.poll();
        }
        frameCount++;
        if (currAccuracy != null && frameCount < RENDER_FRAMES) {
            if(specialNoteMissing){
                currAccuracy = null;
                specialNoteMissing = false;
            } else{
                ACCURACY_FONT.drawString(currAccuracy,
                        Window.getWidth()/2 - ACCURACY_FONT.getWidth(currAccuracy)/2,
                        Window.getHeight()/2);
            }

        }
    }
}
