import bagel.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Sample solution for SWEN20003 Project 1, Semester 2, 2023
 *
 * @author Stella Li
 */
public class ShadowDance extends AbstractGame  {
    private final static int WINDOW_WIDTH = 1024;
    private final static int WINDOW_HEIGHT = 768;
    private final static String GAME_TITLE = "SHADOW DANCE";
    private final Image BACKGROUND_IMAGE = new Image("res/background.png");
    public final static String FONT_FILE = "res/FSO8BITR.TTF";
    private final static int TITLE_X = 220;
    private final static int TITLE_Y = 250;
    private final static int END_INSTRUCTION_Y = 500;
    private final static int INS_X_OFFSET = 100;
    private final static int INS_Y_OFFSET = 190;
    private final static int SCORE_LOCATION = 35;
    private final Font TITLE_FONT = new Font(FONT_FILE, 64);
    private final Font INSTRUCTION_FONT = new Font(FONT_FILE, 24);
    private final Font SCORE_FONT = new Font(FONT_FILE, 30);
    private static final String INSTRUCTIONS = "SELCET LEVELS WITH\nNUMBER KEYS\n\n      1      2      3";
    private static final int CLEAR_SCORE_1 = 150;
    private static final int CLEAR_SCORE_2 = 400;
    private static final int CLEAR_SCORE_3 = 350;
    private static final String CLEAR_MESSAGE = "CLEAR!";
    private static final String END_INSTRUCTION_MESSAGE = "PRESS SPACE TO RETURN TO LEVEL SELECTION";
    private static final String TRY_AGAIN_MESSAGE = "TRY AGAIN";
    private final Accuracy accuracy = new Accuracy();
    private final Lane[] lanes = new Lane[4];
    private int numLanes = 0;
    private static int score = 0;
    private static int currFrame = 0;
    private Track track = new Track("res/track1.wav");
    private boolean started = false;
    private boolean finished = false;
    private boolean paused = false;
    private boolean track_started = false;
    private int level = 1;
    private final int SPECIAL_NOTE_SCORE = 15;
    private static boolean specialNoteScored = false;
    private static boolean bombActivated = false;
    private static String bombActivatedLane;
    private static final List<Enemy> enemies = new ArrayList<>();
    private final Guardian guardian = new Guardian();

    public static void setBombActivated(String dir){
        bombActivatedLane = dir;
        bombActivated = true;
    }
    public ShadowDance(){
        super(WINDOW_WIDTH, WINDOW_HEIGHT, GAME_TITLE);

    }
    public static void setSpecialNoteScored(){
        specialNoteScored = true;
    }

    /**
     * The entry point for the program.
     */
    public static void main(String[] args) {
        ShadowDance game = new ShadowDance();
        game.run();

    }



    private void readCsv(int level) {
        String CSV_FILE = "res/level"+level+".csv";
        try (BufferedReader br = new BufferedReader(new FileReader(CSV_FILE))) {
            String textRead;
            while ((textRead = br.readLine()) != null) {
                String[] splitText = textRead.split(",");

                if (splitText[0].equals("Lane")) {
                    // reading lanes
                    String laneType = splitText[1];
                    int pos = Integer.parseInt(splitText[2]);
                    Lane lane = new Lane(laneType, pos);
                    lanes[numLanes++] = lane;
                } else {
                    // reading notes
                    String dir = splitText[0];
                    String type = splitText[1];
                    Lane lane = null;
                    for (int i = 0; i < numLanes; i++) {
                        if (lanes[i].getType().equals(dir)) {
                            lane = lanes[i];
                        }
                    }

                    if (lane != null) {
                        switch (splitText[1]) {
                            case "Hold":
                                HoldNote holdNote = new HoldNote(dir, Integer.parseInt(splitText[2]));
                                lane.addHoldNote(holdNote);
                                break;
                            case "Normal": {
                                Note note = new Note(dir, dir, Integer.parseInt(splitText[2]));
                                lane.addNote(note);
                                break;
                            }
                            case "Bomb": {
                                Note note = new Note(dir, "Bomb", Integer.parseInt(splitText[2]));
                                lane.addNote(note);
                                break;
                            }
                            default: {
                                Note note = new Note(null, type, Integer.parseInt(splitText[2]));
                                lane.addNote(note);
                                break;
                            }
                        }
                    }

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }

    }


    /**
     * Performs a state update.
     * Allows the game to exit when the escape key is pressed.
     */
    @Override
    protected void update(Input input) {

        if (input.wasPressed(Keys.ESCAPE)){
            Window.close();
        }

        BACKGROUND_IMAGE.draw(Window.getWidth()/2.0, Window.getHeight()/2.0);

        if (!started) {
            // starting screen
            TITLE_FONT.drawString(GAME_TITLE, TITLE_X, TITLE_Y);
            INSTRUCTION_FONT.drawString(INSTRUCTIONS,
                    TITLE_X + INS_X_OFFSET, TITLE_Y + INS_Y_OFFSET);

            if (input.wasPressed(Keys.NUM_1)) {
                started = true;
                level = 1;
                readCsv(level);
                track = new Track("res/track1.wav");
                if(!track_started){
                    track.start();
                    track_started = true;
                }
            } else if (input.wasPressed(Keys.NUM_2)) {
                started = true;
                level = 2;
                track = new Track("res/track2.wav");
                readCsv(level);
                if(!track_started){
                    track.start();
                    track_started = true;
                }
            } else if (input.wasPressed(Keys.NUM_3)) {
                started = true;
                level = 3;
                readCsv(level);
                track = new Track("res/track3.wav");
                if(!track_started){
                    track.start();
                    track_started = true;
                }
            }
        } else if (finished) {
            int clearScore = CLEAR_SCORE_1;
            switch(level){
                case 2:
                    clearScore = CLEAR_SCORE_2;
                    break;
                case 3:
                    clearScore = CLEAR_SCORE_3;
                    break;
            }
            // end screen
            if (score >= clearScore) {
                TITLE_FONT.drawString(CLEAR_MESSAGE,
                        WINDOW_WIDTH/2 - TITLE_FONT.getWidth(CLEAR_MESSAGE)/2,
                        WINDOW_HEIGHT/2);
                INSTRUCTION_FONT.drawString(END_INSTRUCTION_MESSAGE,
                        WINDOW_WIDTH/2 - INSTRUCTION_FONT.getWidth(END_INSTRUCTION_MESSAGE)/2, END_INSTRUCTION_Y);
            } else {
                TITLE_FONT.drawString(TRY_AGAIN_MESSAGE,
                        WINDOW_WIDTH/2 - TITLE_FONT.getWidth(TRY_AGAIN_MESSAGE)/2,
                        WINDOW_HEIGHT/2);
                INSTRUCTION_FONT.drawString(END_INSTRUCTION_MESSAGE,
                        WINDOW_WIDTH/2 - INSTRUCTION_FONT.getWidth(END_INSTRUCTION_MESSAGE)/2, END_INSTRUCTION_Y);
            }
            // back to start screen
            if (input.wasPressed(Keys.SPACE)){
                started = false;
                finished = false;
                numLanes = 0;
                currFrame = 0;
                score = 0;
                Accuracy.reSetSpeedChange();
                Accuracy.reSetScoreMultiplier();
                Accuracy.reSetDoubleScoreEndFrame();
                Accuracy.reSetCurrAccuracy();
                enemies.clear();
                guardian.clearArrow();
            }
        } else {
            // gameplay
            SCORE_FONT.drawString("Score " + score, SCORE_LOCATION, SCORE_LOCATION);

            if (paused) {
                if (input.wasPressed(Keys.TAB)) {
                    paused = false;
                    track.run();
                }

                for (int i = 0; i < numLanes; i++) {
                    lanes[i].draw();
                }

            } else {
                currFrame++;
                if(level== 3){
                    guardian.update();
                    if(currFrame!=0 && currFrame%600 == 0){
                        enemies.add(new Enemy());
                    }
                    for (Enemy enemy : enemies) {
                        if(enemy.isActive()){
                            enemy.update();
                            for (int i = 0; i < numLanes; i++) {
                                if(enemy.isActive()){
                                    lanes[i].checkCollide(enemy.getPoint());
                                }


                            }
                        }

                        if (enemy.isActive()&&guardian.checkCollide(enemy.getPoint())){
                            enemy.deactive();
                        }
                    }
                    //calculate the distance and angle anf shoot the arrow
                    if(input.wasPressed(Keys.LEFT_SHIFT)&&!enemies.isEmpty()){
                        double minDis = -1;
                        Enemy minEnemy = new Enemy();
                        for (Enemy enemy : enemies){
                            if (enemy.isActive()&&(enemy.getPoint().distanceTo(guardian.getPoint())<minDis
                                    ||minDis == -1)){
                                minDis = enemy.getPoint().distanceTo(guardian.getPoint());
                                minEnemy = enemy;
                            }
                        }
                        double dx = minEnemy.getX()-Guardian.getGuardianX();
                        double dy = minEnemy.getY()-Guardian.getGuardianY();
                        guardian.firProjectile(dx/minDis,
                                dy/minDis,Math.atan2(dy,dx));
                    }
                }
                if(bombActivated){
                    for (int i = 0; i < numLanes; i++) {
                        if(lanes[i].getType().equals(bombActivatedLane)){
                            lanes[i].bombEffect();
                        }
                    }
                    bombActivated = false;
                }
                if (specialNoteScored){
                    score += SPECIAL_NOTE_SCORE*Accuracy.getScoreMultiplier();

                    specialNoteScored = false;
                }
                for (int i = 0; i < numLanes; i++) {
                    score += lanes[i].update(input, accuracy)*Accuracy.getScoreMultiplier();
                }

                accuracy.update();
                finished = checkFinished();
                if (input.wasPressed(Keys.TAB)) {
                    paused = true;
                    track.pause();
                }
            }
        }

    }

    public static int getCurrFrame() {
        return currFrame;
    }

    private boolean checkFinished() {
        for (int i = 0; i < numLanes; i++) {
            if (!lanes[i].isFinished()) {
                return false;
            }
        }
        return true;
    }
}
