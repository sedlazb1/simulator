package cz.zbysulak.semesterproject.simulation;

import cz.zbysulak.semesterproject.MyLog;
import java.util.ArrayList;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Zbyšek
 */
public class Hunter implements Entity {

    private static final Logger log = Logger.getLogger(Hunter.class.getName());
    private int food;
    private int reloading;
    Random rand = new Random();

    public Hunter() {
        MyLog.setLogging(log);
        food = rand.nextInt(4) + 3;
        reloading = rand.nextInt(2) + 1;
    }

    @Override
    public GameField doStep(GameField okoli) {
        reloading--;
        food--;
        if (food > 0) {
            if (foodNear(okoli).size() > 0) {
                ArrayList<Coordination> empty = foodNear(okoli);
                Coordination foodToEat = empty.get(rand.nextInt(empty.size()));
                okoli.add(null, foodToEat.getX(), foodToEat.getY());
                food += rand.nextInt(3) + 3;
                log.log(Level.FINEST, "eat");
            } else if (empty(okoli).size() > 0) {
                ArrayList<Coordination> empty = empty(okoli);
                if (reloading == 0) {
                    Coordination dir = empty.get(rand.nextInt(empty.size()));
                    okoli.add(new Bullet(dir.getX(), dir.getY()), dir.getX(), dir.getY());
                    log.log(Level.FINEST, "fire!");
                    reloading = rand.nextInt(2) + 1;
                } else {
                    Coordination moveTo = empty.get(rand.nextInt(empty.size()));
                    okoli.add(this, moveTo.getX(), moveTo.getY());
                    okoli.add(null, 1, 1);
                    log.log(Level.FINEST, "move");
                }
            }
        } else {
            okoli.add(new Corpse(), 1, 1);
        }
        return okoli;
    }

    private ArrayList<Coordination> empty(GameField okoli) {
        ArrayList<Coordination> empty = new ArrayList<>();
        for (int i = 0; i < okoli.getRows(); i++) {
            for (int j = 0; j < okoli.getColls(); j++) {
                if (!(i == 1 && j == 1) && !(okoli.get(i, j) instanceof Rabbit)
                        && !(okoli.get(i, j) instanceof Fox) && !(okoli.get(i, j) instanceof Hunter)
                        && !(okoli.get(i, j) instanceof Bullet) && !(okoli.get(i, j) instanceof Corpse)
                        && !(okoli.get(i, j) instanceof Grass) && !(okoli.get(i, j) instanceof Tree)) {
                    empty.add(new Coordination(i, j));
                }
            }
        }
        return empty;
    }

    private ArrayList<Coordination> foodNear(GameField okoli) {  //food - rabbit or corpse
        ArrayList<Coordination> foodNear = new ArrayList<>();
        for (int i = 0; i < okoli.getRows(); i++) {
            for (int j = 0; j < okoli.getColls(); j++) {
                if (!(i == 1 && j == 1) && (okoli.get(i, j) instanceof Rabbit || okoli.get(i, j) instanceof Corpse)) {
                    foodNear.add(new Coordination(i, j));
                }
            }
        }
        return foodNear;
    }

    public String toString() {
        return "H";
    }
}
