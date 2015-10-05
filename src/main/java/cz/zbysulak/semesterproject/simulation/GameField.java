package cz.zbysulak.semesterproject.simulation;

import cz.zbysulak.semesterproject.MyLog;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 2d array of entities.
 *
 * @author Zbyšek
 */
public class GameField {

    private static final Logger log = Logger.getLogger(GameField.class.getName());
    private Entity[][] field;
    private final int rows;
    private final int colls;

    /**
     * generates new field.
     *
     * @param rows
     * @param colls
     */
    public GameField(int rows, int colls) {
        MyLog.setLogging(log);
        this.field = new Entity[rows][colls];
        this.rows = rows;
        this.colls = colls;
    }

    public int getRows() {
        return rows;
    }

    public int getColls() {
        return colls;
    }

    /**
     * add entity e at given coordinations
     *
     * @param e
     * @param row
     * @param coll
     */
    public void add(Entity e, int row, int coll) {
        if (row < 0 || coll < 0 || row > rows - 1 || coll > colls - 1) {
            log.log(Level.INFO, "out of bounds" + e.toString() + row + coll);
            return;
        }
        field[row][coll] = e;
    }

    /**
     * return entity at given coordinations. null if empty
     *
     * @param row
     * @param coll
     * @return
     */
    public Entity get(int row, int coll) {
        if (row < 0 || coll < 0 || row > rows - 1 || coll > colls - 1) {
            log.log(Level.INFO, "out of bounds" + row + coll);
            return null;
        }
        return field[row][coll];
    }

}
