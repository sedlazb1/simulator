package cz.zbysulak.semesterproject.simulation;

/**
 * Corpse is any dead animal.
 * disappears after 5 rounds
 * @author Zbyšek
 */
public class Corpse implements Entity{
    
    private int toDisapear;

    public Corpse() {
        this.toDisapear = 5;
    }
    
    @Override
    public GameField doStep(GameField okoli) {
        if(toDisapear==0){
            okoli.add(null, 1, 1);
        }
        toDisapear--;
        return okoli;
    }
    
    @Override
    public String toString(){
        return "C";
    }
}
