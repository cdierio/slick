/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package moep.studios.pong;

import moep.studios.pong.api.BallState;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.Shape;

/**
 *
 * @author chdi
 */
public class Computer {

    private Rectangle computer;
    private int points;

    public Computer(int x, int y, int width, int height) {
        computer = new Rectangle(x, y, width, height);
        this.points = 0;
    }

    public boolean intersects(Shape shape) {
        return this.computer.intersects(shape);
    }

    /**
     * Spieler als geometrische Form
     *
     * @return Shape - Spielerform
     */
    public Computer computerAction(Ball ball) {

        float delta = 5f;
        int height = 400;

        if (ball.getBallState() == BallState.Left) {

            if (getShape().getCenterY() > 190 && getShape().getCenterY() < 210) {
            } else {
                if (getShape().getCenterY() > 190) {
                    double hip = 0.4f * delta;
                    getShape().setY((float) (getShape().getY() - (hip)));
                } else if (getShape().getCenterY() < 210) {
                    double hip = 0.4f * delta;
                    getShape().setY((float) (getShape().getY() + (hip)));
                }
            }
        } else if (ball.getBallState() == BallState.Right) {

            if (ball.getShape().getCenterY() < getShape().getMinY()
                    && getShape().getMinY() > 0) {
                if (getShape().getMinY() > 0) {
                    double hip = 0.4f * delta;
                    getShape().setY((float) (getShape().getY() - (hip)));

                }
            }
            if (ball.getShape().getCenterY() > getShape().getMaxY()
                    && getShape().getMaxY() < height) {
                if (getShape().getMaxY() < height) {
                    double hip = 0.4f * delta;
                    getShape().setY((float) (getShape().getY() + (hip)));

                }
            }
        }
        return this;
    }

    public Rectangle getShape() {
        return computer;
    }

    public int getPoints() {
        return points;
    }

    public void addPoint() {
        this.points = points + 1;
    }
}
