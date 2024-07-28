import bagel.*;
import bagel.util.Point;

import java.util.ArrayList;
import java.util.List;

public class Guardian {
    private final static int GUARDIAN_X = 800;
    private final static int GUARDIAN_Y = 600;
    private static final int COLLIDE_DISTANCE = 62;
    private final static Image IMAGE = new Image("res/guardian.png");
    private static final List<Projectile> projectiles= new ArrayList<>();
    private final static Point point = new Point(GUARDIAN_X,GUARDIAN_Y);

    public static int getGuardianY() {
        return GUARDIAN_Y;
    }
    public static int getGuardianX(){
        return GUARDIAN_X;
    }
    public void clearArrow(){
        projectiles.clear();
    }
    public Point getPoint(){
        return point;
    }
    public void firProjectile (double xSpeed,double ySpeed, double angle){
        projectiles.add(new Projectile(xSpeed,ySpeed,angle));
    }

    public boolean checkCollide(Point point){
        for(Projectile projectile:projectiles){
            if (point.distanceTo(projectile.getPoint())<= COLLIDE_DISTANCE&&projectile.isActive()){
                projectile.deactive();
                return true;
            }
        }
        return false;
    }
    public void update() {
        IMAGE.draw(GUARDIAN_X,GUARDIAN_Y);
        for(Projectile projectile :projectiles){
            projectile.update();
        }
    }
}
