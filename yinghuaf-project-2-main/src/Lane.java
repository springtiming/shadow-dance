import bagel.*;
import bagel.util.Point;

/**
 * Class for the lanes which notes fall down
 */
public class Lane {
    private static final int HEIGHT = 384;
    private static final int TARGET_HEIGHT = 657;
    private final String type;
    private final Image image;
    private final Note[] notes = new Note[100];
    private int numNotes = 0;
    private final HoldNote[] holdNotes = new HoldNote[20];
    private int numHoldNotes = 0;
    private Keys relevantKey;
    private final int location;
    private int currNote = 0;
    private int currHoldNote = 0;
    private final static int COLLIDE_DISTANCE = 62;



    public void checkCollide(Point point){
        for (int i = currNote; i < numNotes; i++) {
            if (notes[i].getType().equals("Up") || notes[i].getType().equals("Down") ||
                    notes[i].getType().equals("Left") || notes[i].getType().equals("Right")) {
                if (notes[i].getPoint().distanceTo(point)<=COLLIDE_DISTANCE) {
                    notes[i].deactivate();
                }
            }
        }
    }
    public Lane(String dir, int location) {
        this.type = dir;
        this.location = location;
        image = new Image("res/lane" + dir + ".png");
        switch (dir) {
            case "Left":
                relevantKey = Keys.LEFT;
                break;
            case "Right":
                relevantKey = Keys.RIGHT;
                break;
            case "Up":
                relevantKey = Keys.UP;
                break;
            case "Down":
                relevantKey = Keys.DOWN;
                break;
            case "Special":
                relevantKey = Keys.SPACE;
                break;
        }
    }
    public String getType() {
        return type;
    }

    public void bombEffect(){
        for (int i = currNote; i < numNotes; i++) {
            if(notes[i].isActive()){
                notes[i].deactivate();
            }
        }
        for (int j = currHoldNote; j < numHoldNotes; j++) {
            if(holdNotes[j].isActive()) {
                holdNotes[j].deactivate();
            }
        }
    }

    /**
     * updates all the notes in the lane
     */
    public int update(Input input, Accuracy accuracy) {
        draw();

        for (int i = currNote; i < numNotes; i++) {
            notes[i].update();
        }


        for (int j = currHoldNote; j < numHoldNotes; j++) {
            holdNotes[j].update();
        }


        if (currNote < numNotes) {
            int score = notes[currNote].checkScore(input, accuracy, TARGET_HEIGHT, relevantKey);
            if (notes[currNote].isCompleted()) {
                currNote++;
                return score;
            }
        }

        if (currHoldNote < numHoldNotes) {
            int score = holdNotes[currHoldNote].checkScore(input, accuracy, TARGET_HEIGHT, relevantKey);
            if (holdNotes[currHoldNote].isCompleted()) {
                currHoldNote++;
            }
            return score;
        }

        return Accuracy.NOT_SCORED;
    }

    public void addNote(Note n) {
        notes[numNotes++] = n;
    }

    public void addHoldNote(HoldNote hn) {
        holdNotes[numHoldNotes++] = hn;
    }

    /**
     * Finished when all the notes have been pressed or missed
     */
    public boolean isFinished() {
        for (int i = 0; i < numNotes; i++) {
            if (!notes[i].isCompleted()) {
                return false;
            }
        }

        for (int j = 0; j < numHoldNotes; j++) {
            if (!holdNotes[j].isCompleted()) {
                return false;
            }
        }

        return true;
    }

    /**
     * draws the lane and the notes
     */
    public void draw() {
        image.draw(location, HEIGHT);

        for (int i = currNote; i < numNotes; i++) {
            notes[i].draw(location);
        }

        for (int j = currHoldNote; j < numHoldNotes; j++) {
            holdNotes[j].draw(location);
        }
    }

}
