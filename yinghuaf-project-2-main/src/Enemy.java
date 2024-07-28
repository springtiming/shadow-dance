import bagel.Image;
import bagel.util.Point;
import java.lang.Math;

public class Enemy {
    private double x = 100+Math.random() *800;
    private double y = 100+Math.random() *400;
    private int direction = Math.random() < 0.5 ? -1 : 1;
    private final static int SPEED = 1;
    private final static Image IMAGE = new Image("res/enemy.png");
    private boolean active = true;

    public double getX() {
        return x;
    }
    public double getY() {
        return y;
    }
    public Point getPoint(){
        return new Point(x,y);
    }
    public  void deactive(){
        active = false;
    }

    public boolean isActive() {
        return active;
    }

    public void update(){

        if (x <= 100 || x >= 900) {
            direction *= -1;
        }
        x += direction*SPEED;
        draw();
    }
    private void draw() {
        if (active) {
            IMAGE.draw(x,y);
        }
    }
}
