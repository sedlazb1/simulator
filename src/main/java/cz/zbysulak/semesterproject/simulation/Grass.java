package cz.zbysulak.semesterproject.simulation;

import cz.zbysulak.semesterproject.MyLog;
import java.util.ArrayList;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * grass implements entity. grass expands to random empty field near every 2-4 
 * rounds (1-3 after initialization). after 5-9 rounds from initialization grass 
 * becomes a tree
 * @author Zbyšek
 */
public class Grass implements Entity {

    private static final Logger log = Logger.getLogger(Grass.class.getName());
    private int timeToExpand;
    private int timeToGrow;

    Random rand = new Random();

    public Grass() {
        MyLog.setLogging(log);
        timeToExpand = rand.nextInt(3) + 1;
        timeToGrow = rand.nextInt(5) + 5;
    }

    @Override
    public GameField doStep(GameField okoli) {
        timeToExpand--;
        timeToGrow--;
        if (timeToGrow == 0) {
            okoli.add(new Tree(), 1, 1);
            log.log(Level.FINEST, "I've became a Tree B|");
        } else if (timeToExpand == 0) {
            ArrayList<Coordination> empty = empty(okoli);
            if (empty.size() > 0) {
                Coordination place = empty.get(rand.nextInt(empty.size()));
                okoli.add(new Grass(), place.getX(), place.getY());
                timeToExpand = rand.nextInt(3) + 2;
                log.log(Level.FINEST, "expanding");
            }
            timeToExpand = rand.nextInt(3)+1;
        } else {
            log.log(Level.FINEST, "doing nothing");
        }
        return okoli;
    }

    @Override
    public String toString() {
        return "G";
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
}
