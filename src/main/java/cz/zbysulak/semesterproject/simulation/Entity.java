package cz.zbysulak.semesterproject.simulation;

/**
 * entity is main object in GameField. 
 * @author Zbyšek
 */
public interface Entity {
    /**
     * does one step with given surroundings. 
     * @param okoli GameField of size 3x3 around actual entity.
     * @return modified surroundings (GameField)
     */
    GameField doStep(GameField okoli);
}
