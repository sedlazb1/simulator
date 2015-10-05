package cz.zbysulak.semesterproject.simulation;

/**
 * coordination is object with 2 integers - X and Y coordination.
 * @author Zbyšek
 */
public class Coordination {
    
    private int x;
    private int y;

    /**
     * constructor of Coordination.
     * @param x X coordination
     * @param y Y coordination
     */
    public Coordination(int x, int y) {
        this.x = x;
        this.y = y;
    }

    /**
     *
     * @return X coordination
     */
    public int getX() {
        return x;
    }

    /**
     *
     * @return Y coordination
     */
    public int getY() {
        return y;
    }

    /**
     * sets new value of x coordination
     * @param x 
     */
    public void setX(int x) {
        this.x = x;
    }

    /**
     * sets new value of y coordination
     * @param y
     */
    public void setY(int y) {
        this.y = y;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Coordination other = (Coordination) obj;
        if (this.x != other.getX()) {
            return false;
        }
        if (this.y != other.getY()) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Coordination{" + "x=" + x + ", y=" + y + '}';
    }

}
