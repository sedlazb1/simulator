package cz.zbysulak.semesterproject.simulation;

import cz.zbysulak.semesterproject.MyLog;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Bullet is entity. can be fired by hunter. can move in given direction. kills
 * animals, disapear in grass, tree or corpse. have limited energy
 *
 * @author Zbyšek
 */
public class Bullet implements Entity {

    private static final Logger log = Logger.getLogger(Bullet.class.getName());
    private int energy;
    private final int x;
    private final int y;

    /**
     * creates new bullet, what will travel in direction by params.
     *
     * @param x
     * @param y
     */
    public Bullet(int x, int y) {
        MyLog.setLogging(log);
        energy = 10;
        this.x = x;
        this.y = y;
    }

    @Override
    public GameField doStep(GameField okoli) {
        if (energy > 0) {
            if (okoli.get(x, y) == null) {
                okoli.add(this, x, y);
                log.log(Level.FINEST, "still flying");
            } else if (okoli.get(x, y) instanceof Animal) {
                okoli.add(new Corpse(), x, y);
                log.log(Level.FINEST, "killed someone, hahahah!");
            } else {
                log.log(Level.FINEST, "I'm stucked somewhere");
            }
        }
        okoli.add(null, 1, 1);
        energy--;
        return okoli;
    }

    @Override
    public String toString() {
        return "B";
    }
}
