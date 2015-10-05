package cz.zbysulak.semesterproject.simulation;

import cz.zbysulak.semesterproject.MyLog;
import cz.zbysulak.semesterproject.network.MyClient;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

public class World {

    private static final Logger log = Logger.getLogger(World.class.getName());
    private GameField field;
    Random rand = new Random();
    private MyClient client;

    /**
     * creates new Gamefield for simulation
     *
     * @param rows
     * @param colls sizes of simulated world
     * @param foxes
     * @param rabbit
     * @param hunter
     * @param grass
     * @param trees count of entities in percent
     */
    public World(int rows, int colls, int foxes, int rabbit, int hunter, int grass, int trees) {
        MyLog.setLogging(log);

        field = new GameField(rows, colls);
        double numberOfCells = (rows * colls) / 100.0;
        generateField((int) (numberOfCells * foxes), (int) (numberOfCells * rabbit), (int) (numberOfCells * grass), (int) (numberOfCells * hunter), (int) (numberOfCells * trees));
        log.log(Level.FINE, "field has been generated :\n" + print());
    }

    /**
     * simulates next round in this field
     */
    public void nextRound() {
        LinkedList<Coordination> usedCoords = getUsedCoords();

        while (!usedCoords.isEmpty()) {
            Coordination actualCoords = usedCoords.removeLast();
            Entity actualEntity = field.get(actualCoords.getX(), actualCoords.getY());
            GameField actualSurround = getSurround(actualCoords.getX(), actualCoords.getY());
            GameField newSurround = actualSurround;
            try {
                newSurround = actualEntity.doStep(actualSurround);
            } catch (NullPointerException e) {
                log.log(Level.FINE, "This entity is not here anymore." + e.getMessage());
            }
            addSurround(newSurround, actualCoords.getX(), actualCoords.getY());
        }
        log.log(Level.FINER, "new field :\n" + print());
    }

    private LinkedList<Coordination> getUsedCoords() {
        LinkedList<Coordination> ret = new LinkedList<>();
        for (int i = 0; i < field.getRows(); i++) {
            for (int j = 0; j < field.getColls(); j++) {
                if (field.get(i, j) != null) {
                    ret.add(new Coordination(i, j));
                }
            }
        }
        Collections.shuffle(ret);
        return ret;
    }

    private GameField getSurround(int x, int y) {
        GameField okoli = new GameField(3, 3);
        int k = 0;
        int l = 0;
        for (int i = x - 1; i <= x + 1; i++) {
            for (int j = y - 1; j <= y + 1; j++) {
                if (i < 0 || j < 0 || i > field.getRows() - 1 || j > field.getColls() - 1) {
                    log.log(Level.FINER, "need something from neightbors.");
                    okoli.add(getNeightbor(i, j), l, k);
                } else {
                    okoli.add(field.get(i, j), l, k);
                }
                k++;
            }
            k = 0;
            l++;
        }
        return okoli;
    }

    private void addSurround(GameField newSur, int x, int y) {
        int k = 0;
        int l = 0;
        for (int i = x - 1; i <= x + 1; i++) {
            for (int j = y - 1; j <= y + 1; j++) {
                if (i < 0 || j < 0 || i > field.getRows() - 1 || j > field.getColls() - 1) {
                    setNeighbor(newSur.get(l, k), i, j);
                } else {
                    field.add(newSur.get(l, k), i, j);
                }
                k++;
            }
            k = 0;
            l++;
        }
    }

    private void generateField(int fox, int rabbit, int grass, int hunter, int trees) {
        LinkedList<Coordination> coords = getCoords();
        Coordination actual;
        for (int i = 0; i < fox; i++) {
            actual = coords.removeLast();
            log.log(Level.FINEST, "generating fox");
            field.add(new Fox(), actual.getX(), actual.getY());
        }
        for (int i = 0; i < rabbit; i++) {
            actual = coords.removeLast();
            log.log(Level.FINEST, "generating rabbit");
            field.add(new Rabbit(), actual.getX(), actual.getY());
        }
        for (int i = 0; i < grass; i++) {
            actual = coords.removeLast();
            log.log(Level.FINEST, "generating grass");
            field.add(new Grass(), actual.getX(), actual.getY());
        }
        for (int i = 0; i < hunter; i++) {
            actual = coords.removeLast();
            log.log(Level.FINEST, "generating hunter");
            field.add(new Hunter(), actual.getX(), actual.getY());
        }
        for (int i = 0; i < trees; i++) {
            actual = coords.removeLast();
            log.log(Level.FINEST, "generating tree");
            field.add(new Tree(), actual.getX(), actual.getY());
        }
        log.log(Level.FINEST, "generating done");
    }

    private LinkedList<Coordination> getCoords() {
        LinkedList<Coordination> coords = new LinkedList<>();
        for (int i = 0; i < field.getRows(); i++) {
            for (int j = 0; j < field.getColls(); j++) {
                coords.add(new Coordination(i, j));
            }
        }
        Collections.shuffle(coords);
        return coords;
    }

    private Entity getNeightbor(int row, int coll) {         //return Monster or null at position...
        Entity ret = new Tree();
        if (client != null) {
            if (row < 0 || row > field.getRows() - 1) {
                ret = new Tree();
                log.log(Level.FINER, " above and bellow are Trees");
            } else {
                char answer = 0;
                if (coll < 0) {
                    answer = client.getLeft(row);
                } else if (coll > field.getColls() - 1) {
                    answer = client.getRight(row);
                }
                log.log(Level.FINE, " received:" + answer);
                switch (answer) {
                    case 'F':
                        ret = new Fox();
                        break;
                    case 'R':
                        ret = new Rabbit();
                        break;
                    case 'C':
                        ret = new Corpse();
                        break;
                    case 'H':
                        ret = new Hunter();
                        break;
                    case 'T':
                        ret = new Tree();
                        break;
                    case 'G':
                        ret = new Grass();
                        break;
                    default:
                        ret = null;
                        break;
                }
            }
        }
        return ret;
    }

    private void setNeighbor(Entity e, int row, int coll) {
        if (client != null) {
            if (row >= 0 && row < field.getRows()) {
                if (coll < 0) {
                    client.setLeft(row, e == null ? "." : e.toString());
                } else {
                    client.setRight(row, e == null ? "." : e.toString());
                }
            }
        }
    }

    /**
     * this method sets new borders
     * @param left String what represents left side of field 
     * @param right String what represents right side of field 
     */
    public void repairBorders(String left, String right) {
        if (left == null) {
            for (int i = 0; i < field.getRows(); i++) {
                if (rand.nextBoolean()) {
                    field.add(getEntity(right.charAt(i)), i, field.getColls()-1);
                }
            }
        } else if (right == null) {
            for (int i = 0; i < field.getRows(); i++) {
                if (rand.nextBoolean()) {
                    field.add(getEntity(left.charAt(i)), i, 0);
                }
            }
        } else {
            for (int i = 0; i < field.getRows(); i++) {
                if (rand.nextBoolean()) {
                    field.add(getEntity(right.charAt(i)), i, field.getColls()-1);
                }
                if (rand.nextBoolean()) {
                    field.add(getEntity(left.charAt(i)), i, 0);
                }
            }
        }
    }

    private Entity getEntity(char c) {
        Entity ent;
        switch (c) {
            case 'F':
                ent = new Fox();
                break;
            case 'R':
                ent = new Rabbit();
                break;
            case 'C':
                ent = new Corpse();
                break;
            case 'H':
                ent = new Hunter();
                break;
            case 'T':
                ent = new Tree();
                break;
            case 'G':
                ent = new Grass();
                break;
            default:
                ent = null;
                break;
        }
        return ent;
    }

    /**
     * set client to comunicate with server
     *
     * @param client
     */
    public void setClient(MyClient client) {
        this.client = client;
    }

    /**
     * @return String which represents current game field
     */
    public String print() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < field.getRows(); i++) {
            for (int j = 0; j < field.getColls(); j++) {
                sb.append(field.get(i, j) == null ? "." : field.get(i, j).toString());
            }
            //sb.append(";");
        }
        return sb.toString();
    }

    /**
     * @return field of this simulator
     */
    public GameField getField() {
        return field;
    }
}
