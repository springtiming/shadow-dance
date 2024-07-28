import bagel.Image;
import bagel.util.Point;
import bagel.DrawOptions;
import bagel.Window;

public class Projectile {
    private double x = Guardian.getGuardianX();
    private double y = Guardian.getGuardianY();
    private final double xSpeed;
    private final double ySpeed;
    private final double angle;
    private final static int SPEED = 6;
    private final static Image IMAGE = new Image("res/arrow.png");
    private boolean active = true;
    private final DrawOptions option = new DrawOptions();
    public boolean isActive() {
        return active;
    }
    public  void deactive(){
        active = false;
    }
    public Point getPoint(){
        return new Point(x,y);
    }
    public Projectile(double xSpeed,double ySpeed,double angle){
        this.xSpeed = xSpeed;
        this.ySpeed = ySpeed;
        this.angle = angle;
    }

    public void update() {
        if (active) {
            x += xSpeed *SPEED;
            y += ySpeed *SPEED;
            option.setRotation(angle);
        }
        draw();
        if ( x- Window.getWidth()>=0||y - Window.getHeight()>=0||y<=0 ) {
            active = false;
        }

    }
    private void draw() {
        if (active) {
            IMAGE.draw(x,y,option);
        }
    }


}
