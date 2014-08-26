/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package moep.studios.pong;

import java.util.Random;
import moep.studios.pong.api.BallState;
import org.newdawn.slick.geom.Circle;
import org.newdawn.slick.geom.Shape;

/**
 *
 * @author chdi
 */
public class Ball {

    private static final int ballRadius = 3;
    private Circle ball;
    private float speed;
    private float angle;
    private BallState ballState;

    
    
    public Ball(int x, int y) {

        Random r = new Random();

        ball = new Circle(x, y, ballRadius);

        this.speed = 1;

        // Ball fliegt zu Spieler1 (40°-120°)

        if (r.nextBoolean()) {

            this.angle = r.nextInt(80) + 40;
           
            this.setBallState(BallState.Right);
            
        } // Ball fliegt zu Spieler2 (230°-310°)
        else {
            
            this.angle = r.nextInt(80) + 230;
           this.setBallState(BallState.Left);
        }
    }

    public void setBallState(BallState ballState) {
        this.ballState = ballState;
    }
    
    public BallState getBallState() {
       
        return ballState;
    }

    public Shape getShape() {
        return ball;
    }

    public float getSpeed() {
        return speed;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    public float getAngle() {
        return angle;
    }

    public void setAngle(float angle) {
        this.angle = angle;
    }
}