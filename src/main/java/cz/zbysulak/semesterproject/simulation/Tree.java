package cz.zbysulak.semesterproject.simulation;

import cz.zbysulak.semesterproject.MyLog;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * tree is entity. does nothing.
 * @author Zbyšek
 */
public class Tree implements Entity{
    private static final Logger log = Logger.getLogger(Tree.class.getName());

    public Tree() {
        MyLog.setLogging(log);
    }
    
    @Override
    public GameField doStep(GameField okoli) {
        log.log(Level.FINEST, "I am Tree, doing nothing as usual");
        return okoli;
    }   
    
    @Override
    public String toString(){
        return "T";
    }
}
