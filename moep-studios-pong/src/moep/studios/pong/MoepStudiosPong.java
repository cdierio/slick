/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package moep.studios.pong;

import java.awt.Font;
import moep.studios.pong.api.BallState;
import moep.studios.pong.api.Border;
import moep.studios.pong.api.State;
import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.BasicGame;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.TrueTypeFont;
import org.newdawn.slick.geom.Line;

/**
 *
 * @author chdi
 */
public class MoepStudiosPong extends BasicGame {

    public static final int WIDTH = 600;
    public static final int HEIGHT = 400;
    public static final int BALL_RADIUS = 3;
    /**
     * Punktezahl zum gewinnen
     */
    public static final int MAX_SCORE = 11;
    /**
     * Schrift für Punktestand.
     */
    public static Font SCORE_FONT;
    public static TrueTypeFont SCORE_TRUE_TYPE_FONT;
    /**
     * Mittelliene.
     */
    private static Line MIDDLE_LINE = new Line(WIDTH / 2, 0, WIDTH / 2, HEIGHT);
    /**
     * Spielkomponenten initalisieren.
     */
    private Player player1;
    private Player player2;
    private Computer pc;
    private Ball ball;
    /**
     * Zeitmesser damit der ball auchmal schneller wird.
     */
    private Timer timer;
    /**
     * WO war die letzte Kollision verhindert endlos Kollisionen.
     */
    Border lastCollision;
    /**
     * aktueller Spielezustand
     */
    State currentstState = State.Start;

    public MoepStudiosPong() {
        super("MoepPong");
    }

    /**
     * Initialiserungsmethode wird lediglich einmal beim Start aufgerufen
     *
     * @param gc
     * @throws SlickException
     */
    public void init(GameContainer gc) throws SlickException {

        // Spieler 1 wird erzeugt, Rechteckposition ( x = 5, y = 130, breite = 5, höhe = 40 );
        player1 = new Player(5, 150, 5, 40);
        // Spieler 2 wird erzeugt, Rechteckposition ( x = 390, y = 130, breite = 5, höhe = 40 );
        player2 = new Player(590, 130, 5, 40);
        //pc = new Computer(590, 150, 5, 40);

        // Ball wird erzeugt in der Mitte vom Fenster
        ball = new Ball(WIDTH / 2 - BALL_RADIUS / 2, HEIGHT / 2 - BALL_RADIUS / 2);
        // Timer um den Ball nach einer gewissen Zeit schneller zu machen, läuft
        // nach einer Sekunde ab.
        timer = new Timer(1000);

        // Es gab keine letzte Kollision
        lastCollision = Border.NONE;

        SCORE_FONT = new Font("Verdana", Font.PLAIN, 18);
        
        SCORE_TRUE_TYPE_FONT = new TrueTypeFont(SCORE_FONT, false);

    }

    @Override
    public void update(GameContainer gc, int delta) throws SlickException {
        Input input = gc.getInput();
        // START-ZUSTAND
        if (currentstState == State.Start) {
            if (input.isKeyDown(Input.KEY_ENTER)) {
                currentstState = State.Play;
            }
        }
        // SPIEL-ZUSTAND
        if (currentstState == State.Play || currentstState == State.BallIsOut) {
            // Steuerung Spieler 1
            if (input.isKeyDown(Input.KEY_W)) {
                if (player1.getShape().getMinY() > 0) {
                    double hip = 0.4f * delta;
                    player1.getShape().setY((float) (player1.getShape().getY() - (hip)));
                }
            }

            if (input.isKeyDown(Input.KEY_S)) {
                if (player1.getShape().getMaxY() < HEIGHT) {
                    double hip = 0.4f * delta;
                    player1.getShape().setY((float) (player1.getShape().getY() + (hip)));
                }
            }

            if (currentstState == State.Start) {
                if (input.isKeyPressed(Input.KEY_ENTER)) {
                    currentstState = State.Play;
                }
            }
                        
            if (currentstState == State.Play) {

                // Ball Flugbahn berechnen
                float hip = 0.3f * delta + ball.getSpeed();
                ball.getShape().setX((float) (ball.getShape().getX() + hip
                        * Math.sin(Math.toRadians(ball.getAngle()))));
                ball.getShape().setY((float) (ball.getShape().getY() - hip
                        * Math.cos(Math.toRadians(ball.getAngle()))));
                // Zeitabstand für den Timer hinzufügen, delta = Abstand in Millisekunden zu der letzten Frame
                timer.addTime(delta);
                // Wenn die Zeit abgelaufen wird die Geschwindigkeit des Balles erhöht
                // und der Timer startet von neuem
                if (timer.isTimeOver()) {
                    ball.setSpeed(ball.getSpeed() + 0.05f);
                    timer.reset();
                }

                pc.computerAction(ball);

                // Ball stoßt oben an
                if (ball.getShape().getMinY() <= 0 && lastCollision != Border.TOP) {
                    // Einfallswinkel = Ausfallswinkel
                    ball.setAngle((float) (-1 * (ball.getAngle() + Math.PI + 180)));
                    lastCollision = Border.TOP;
                }
                // Ball stoßt unten an
                if (ball.getShape().getMaxY() >= HEIGHT && lastCollision != Border.BOTTOM) {
                    // Einfallswinkel = Ausfallswinkel
                    ball.setAngle((float) (-1 * (ball.getAngle() + Math.PI + 180)));
                    lastCollision = Border.BOTTOM;
                }
                
                
                // Spieler links  trifft den Ball
                if (player1.intersects(ball.getShape()) && lastCollision != Border.LEFT) {
                    ball.setBallState(BallState.Right);
                    // Einfallswinkel = Ausfallwinkel
                    ball.setAngle((float) (-1 * (ball.getAngle() + Math.PI)));
                    lastCollision = Border.LEFT;
                }
                // Spieler rechts
                if (pc.intersects(ball.getShape()) && lastCollision != Border.RIGHT) {
                    ball.setBallState(BallState.Left);
                    // Einfallswinkel = Ausfallwinkel
                    ball.setAngle((float) (-1 * (ball.getAngle() + Math.PI)));
                    lastCollision = Border.RIGHT;
                }
                // Ball fliegt links aus dem Bildschirm -> Punkt für Spieler Rechts
                if (ball.getShape().getMaxX() < 0) {
                    pc.addPoint();
                    currentstState = State.BallIsOut;
                    lastCollision = Border.NONE;
                    if (pc.getPoints() >= MAX_SCORE) {
                        currentstState = State.Player2Wins;
                    }
                }
                // Ball fliegt rechts aus dem Bildschirm -> Punkt für Spieler Links

                if (ball.getShape().getMinX() > WIDTH) {
                    player1.addPoint();
                    currentstState = State.BallIsOut;
                    lastCollision = Border.NONE;
                    if (player1.getPoints() >= MAX_SCORE) {
                        currentstState = State.Player1Wins;
                    }
                }
            }

            if (currentstState == State.BallIsOut) {
                // Falls der Ball aus dem Spielfeld ist und man [Enter] drückt gehts weiter
                if (input.isKeyDown(Input.KEY_ENTER)) {
                    ball = new Ball(WIDTH / 2 - BALL_RADIUS / 2, HEIGHT / 2 - BALL_RADIUS / 2);
                    currentstState = State.Play;
                }

            }

            System.out.println(currentstState);
        }
        // SPIEL VORBEI - ZUSTAND

        if (currentstState == State.Player1Wins || currentstState == State.Player2Wins) {
            if (input.isKeyPressed(Input.KEY_SPACE)) {
                currentstState = State.Play;
                init(gc);
            }
        }
    }

    @Override
    public void render(GameContainer gc, Graphics grphcs) throws SlickException {


        if (currentstState == State.Start) {

            String pressEnterToStart = "[PRESS ENTER TO START]";

            SCORE_TRUE_TYPE_FONT.drawString(WIDTH / 2 - SCORE_TRUE_TYPE_FONT.getWidth(pressEnterToStart) / 2, HEIGHT / 2 - SCORE_TRUE_TYPE_FONT.getHeight(pressEnterToStart) / 2, pressEnterToStart);

        }
        // Spieler Rechtecke füllen
        grphcs.fill(player1.getShape());
        grphcs.fill(pc.getShape());

        // Spieler Rechteck zeichnen
        grphcs.draw(player1.getShape());
        grphcs.draw(pc.getShape());
        // Mittellinie Zeichnen
        grphcs.draw(MIDDLE_LINE);
        // Falls Ball im Spiel, Ball füllen und zeichnen
        if (ball != null) {
            grphcs.fill(ball.getShape());
            grphcs.draw(ball.getShape());
        }
        // Punktestand mittig zeichnen
        String scoreText = player1.getPoints() + "     " + pc.getPoints();
        SCORE_TRUE_TYPE_FONT.drawString(WIDTH / 2 - SCORE_TRUE_TYPE_FONT.getWidth(scoreText) / 2, 5, scoreText);
        // Meldung für neuen Ball mittig zeichnen
        if (currentstState == State.BallIsOut) {
            String pressEnterForNewBall = "[PRESS ENTER FOR NEW BALL]";
            SCORE_TRUE_TYPE_FONT.drawString(WIDTH / 2 - SCORE_TRUE_TYPE_FONT.getWidth(pressEnterForNewBall) / 2, HEIGHT / 2 - SCORE_TRUE_TYPE_FONT.getHeight(pressEnterForNewBall) / 2, pressEnterForNewBall);
        }
        // Meldung für Spieler1 hat gewonnen
        if (currentstState == State.Player1Wins) {
            String player1Wins = "Player1 won! [NEW GAME - SPACE]";
            SCORE_TRUE_TYPE_FONT.drawString(WIDTH / 2 - SCORE_TRUE_TYPE_FONT.getWidth(player1Wins) / 2, HEIGHT / 2 - SCORE_TRUE_TYPE_FONT.getHeight(player1Wins) / 2, player1Wins);
        }
        // Meldung für Spieler2 hat gewonnen
        if (currentstState == State.Player2Wins) {
            String player2Wins = "Player2 won! [NEW GAME - SPACE]";
            SCORE_TRUE_TYPE_FONT.drawString(WIDTH / 2 - SCORE_TRUE_TYPE_FONT.getWidth(player2Wins) / 2, HEIGHT / 2 - SCORE_TRUE_TYPE_FONT.getHeight(player2Wins) / 2, player2Wins);
        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws SlickException {
        AppGameContainer pong = new AppGameContainer(new MoepStudiosPong());

        pong.setDisplayMode(WIDTH, HEIGHT, false);

        pong.setVSync(true);

        pong.setShowFPS(false);

        pong.start();
    }
}
