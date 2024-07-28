import bagel.*;
import bagel.util.Point;

/**
 * Class for normal notes
 */
public class Note {
    private final Image image;
    private final int appearanceFrame;
    private final int speed = 2;
    private int y = 100;
    private int x;
    private boolean active = false;
    private boolean completed = false;
    private final String type;
    private final String dir;

    public Point getPoint(){
        return new Point(x,y);
    }
    public Note(String dir, String type, int appearanceFrame) {
        this.type = type;
        this.dir = dir;
        if (this.type.equals("DoubleScore")){
            image = new Image("res/note2x.png");
        }else{
            image = new Image("res/note" + type + ".png");
        }
        this.appearanceFrame = appearanceFrame;
    }
    public String getType(){
        return type;
    }
    public boolean isActive() {
        return active;
    }

    public boolean isCompleted() {return completed;}

    public void deactivate() {
        active = false;
        completed = true;
    }

    public void update() {
        if (active) {
            y += speed + Accuracy.getSpeedChange();
        }
        if (ShadowDance.getCurrFrame() >= appearanceFrame && !completed) {
            active = true;
        }
    }


    public void draw(int x) {
        this.x =x;
        if (active) {
            image.draw(x, y);
        }
    }

    public int checkScore(Input input, Accuracy accuracy, int targetHeight, Keys relevantKey) {
        boolean isScored = false;
        if (isActive()) {
            // evaluate accuracy of the key press
            int score = 0;
            if(type.equals("Up")||type.equals("Down")||type.equals("Left")||type.equals("Right")){
                score = accuracy.evaluateScore(y, targetHeight, input.wasPressed(relevantKey));
                if(score != Accuracy.NOT_SCORED){
                    isScored = true;
                }
            } else{
                if (accuracy.specialNote(y, targetHeight, input.wasPressed(relevantKey),type,dir)){
                    isScored = true;
                }
            }


            if (isScored) {
                deactivate();
                return score;

            }
        }

        return 0;
    }

}

