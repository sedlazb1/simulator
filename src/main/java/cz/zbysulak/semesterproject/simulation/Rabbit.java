package cz.zbysulak.semesterproject.simulation;

import cz.zbysulak.semesterproject.MyLog;
import java.util.ArrayList;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Rabbit is entity and animal. eats grass. from each grass he can live 5 days
 * more. without food he becomes a corpse. with rabbit near (both without
 * headache) makes new rabbit at random empty cell 1 cell away
 *
 * @author Zbyšek
 */
public class Rabbit implements Entity, Animal {

    private static final Logger log = Logger.getLogger(Rabbit.class.getName());
    private int food;
    private int headache;
    Random rand = new Random();

    public Rabbit() {
        MyLog.setLogging(log);
        food = 3 + rand.nextInt(4);
        headache = 1;
    }

    @Override
    public GameField doStep(GameField okoli) {
        if (food > 0) {
            ArrayList<Coordination> rabbits = rabbitNear(okoli);
            ArrayList<Coordination> foodN = foodNear(okoli);
            ArrayList<Coordination> empty = empty(okoli);
            headache--;
            if (headache<1 && rabbits.size() > 0 && empty.size() > 0) {
                Coordination choosenRabbit = rabbits.get(rand.nextInt(rabbits.size()));
                Rabbit rabbit = (Rabbit) okoli.get(choosenRabbit.getX(), choosenRabbit.getY());
                rabbit.setHeadache(2);
                Coordination freeSpace = empty.get(rand.nextInt(empty.size()));
                Rabbit newRabbit = new Rabbit();
                newRabbit.setHeadache(2);
                okoli.add(newRabbit, freeSpace.getX(), freeSpace.getY());
                headache = 1;
                log.log(Level.FINEST, "reproduced");
            } else if (foodN.size() > 0 && food < 15) {  //kdyz ma hodne jidla tak travu nezere
                Coordination foodToEat = foodN.get(rand.nextInt(foodN.size()));
                okoli.add(null, foodToEat.getX(), foodToEat.getY());
                food += 2 + rand.nextInt(4);
                log.log(Level.FINEST, "ate grass");
            } else if (empty.size() > 0) {
                Coordination placeToMove = empty.get(rand.nextInt(empty.size()));
                okoli.add(this, placeToMove.getX(), placeToMove.getY());
                okoli.add(null, 1, 1);
                log.log(Level.FINEST, "move");
            }
            food--;
        } else {
            log.log(Level.FINEST, "died");
            okoli.add(new Corpse(), 1, 1);
        }
        return okoli;
    }

    @Override
    public String toString() {
        return "R";
    }

    private ArrayList<Coordination> rabbitNear(GameField okoli) {
        ArrayList<Coordination> rabbits = new ArrayList<>();
        for (int i = 0; i < okoli.getRows(); i++) {
            for (int j = 0; j < okoli.getColls(); j++) {
                if (!(i == 1 && j == 1) && okoli.get(i, j) instanceof Rabbit) {
                    Rabbit rabbit = (Rabbit) okoli.get(i, j);
                    if (rabbit.isHeadache()==0) {
                        rabbits.add(new Coordination(i, j));
                    }
                }
            }
        }
        return rabbits;
    }

    private ArrayList<Coordination> foodNear(GameField okoli) {  //food - rabbit or corpse
        ArrayList<Coordination> foodNear = new ArrayList<>();
        for (int i = 0; i < okoli.getRows(); i++) {
            for (int j = 0; j < okoli.getColls(); j++) {
                if (!(i == 1 && j == 1) && (okoli.get(i, j) instanceof Grass)) {
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
                        && !(okoli.get(i, j) instanceof Tree) && !(okoli.get(i, j) instanceof Grass)) {
                    empty.add(new Coordination(i, j));
                }
            }
        }
        return empty;
    }

    @Override
    public int isHeadache() {
        return headache;
    }

    @Override
    public void setHeadache(int headache) {
        this.headache = headache;
    }

    @Override
    public int getFood() {
        return food;
    }

    @Override
    public void setFood(int food) {
        this.food = food;
    }
}
