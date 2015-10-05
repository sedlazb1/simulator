package cz.zbysulak.semesterproject.simulation;

import cz.zbysulak.semesterproject.MyLog;
import java.util.ArrayList;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Fox is animal and entity. it can eat corpse or rabbit. from each food it can
 * live 3 more days. without headache and with fox near (max 1 cell away,
 * without headache too) it makes new fox at random empty space.
 *
 * @author Zbyšek
 */
public class Fox implements Entity, Animal {

    private static final Logger log = Logger.getLogger(Fox.class.getName());
    private int food;
    private int headache;       //noone wanna reproduce with headache :)
    Random rand = new Random();

    public Fox() {
        MyLog.setLogging(log);
        food = rand.nextInt(3) + 3;
        headache = 3;
    }

    @Override
    public GameField doStep(GameField okoli) {
        if (food > 0) {
            ArrayList<Coordination> foxes = foxesNear(okoli);
            ArrayList<Coordination> foodN = foodNear(okoli);
            ArrayList<Coordination> empty = empty(okoli);
            headache--;
            if (headache < 1 && foxes.size() > 0 && empty.size() > 0) {
                log.log(Level.FINEST, "reproducing, do not disturb.");
                Coordination choosenFox = foxes.get(rand.nextInt(foxes.size()));
                Fox fox = (Fox) okoli.get(choosenFox.getX(), choosenFox.getY());
                fox.setHeadache(5);
                Coordination freeSpace = empty.get(rand.nextInt(empty.size()));
                fox = new Fox();
                fox.setHeadache(5);
                okoli.add(fox, freeSpace.getX(), freeSpace.getY());
                headache = 3;
            } else if (foodN.size() > 0 && food < 10) {     //je prezrana chudinka
                log.log(Level.FINEST, "eatin'");
                Coordination foodToEat = foodN.get(rand.nextInt(foodN.size()));
                okoli.add(null, foodToEat.getX(), foodToEat.getY());
                food += rand.nextInt(3) + 3;
            } else if (empty.size() > 0) {
                log.log(Level.FINEST, "moving");
                Coordination placeToMove = empty.get(rand.nextInt(empty.size()));
                okoli.add(this, placeToMove.getX(), placeToMove.getY());
                okoli.add(this, 1, 1);
            }
            food--;
        } else {
            log.log(Level.FINEST, "died");
            okoli.add(new Corpse(), 1, 1);
        }
        return okoli;
    }

    //private methods
    private ArrayList<Coordination> foxesNear(GameField okoli) {
        ArrayList<Coordination> foxes = new ArrayList<>();
        for (int i = 0; i < okoli.getRows(); i++) {
            for (int j = 0; j < okoli.getColls(); j++) {
                if (!(i == 1 && j == 1) && (okoli.get(i, j) instanceof Fox)) {
                    Fox fox = (Fox) okoli.get(i, j);
                    if (fox.isHeadache() == 0) {
                        foxes.add(new Coordination(i, j));
                    }
                }
            }
        }
        return foxes;
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

    //getters and setters
    @Override
    public void setFood(int food) {
        this.food = food;
    }

    @Override
    public int getFood() {
        return food;
    }

    @Override
    public void setHeadache(int headache) {
        this.headache = headache;
    }

    @Override
    public int isHeadache() {
        return headache;
    }

    @Override
    public String toString() {
        return "F";
    }

}
