import cz.zbysulak.semesterproject.simulation.*;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;

/**
 *
 * @author Uživatel
 */
public class WorldTest {

    World world;

    public WorldTest() {
        world = new World(5, 5, 0, 0, 0, 0, 0);
    }

    @Before
    public void clear() {
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                world.getField().add(null, i, j);

            }
        }
    }

    @Test
    public void entityDies() {
        fillByTrees();
        world.getField().add(new Fox(), 1, 1);
        world.getField().add(new Rabbit(), 3, 1);
        world.getField().add(new Hunter(), 1, 3);
        world.nextRound();
        world.nextRound();
        world.nextRound();
        world.nextRound();
        world.nextRound();
        world.nextRound();
        assertTrue("(Fox)Should be C, not " + world.getField().get(1, 1).toString(), world.getField().get(1, 1).toString().equals("C"));
        world.nextRound();
        assertTrue("(Rabbit)Should be C, not " + world.getField().get(3, 1).toString(), world.getField().get(3, 1).toString().equals("C"));
        assertTrue("(Hunter)Should be C, not " + world.getField().get(1, 3).toString(), world.getField().get(1, 3).toString().equals("C"));
    }

    @Test
    public void entityEats() {
        fillByTrees();
        world.getField().add(new Fox(), 0, 0);
        world.getField().add(new Rabbit(), 0, 1);
        world.getField().add(new Fox(), 0, 3);
        world.getField().add(new Corpse(), 0, 4);
        world.getField().add(new Hunter(), 2, 0);
        world.getField().add(new Rabbit(), 2, 1);
        world.getField().add(new Hunter(), 2, 3);
        world.getField().add(new Corpse(), 2, 4);
        world.getField().add(new Rabbit(), 4, 1);
        world.getField().add(new Grass(), 4, 0);
        System.out.println(world.print());
        world.nextRound();
        assertNull("(Fox)Should be null", world.getField().get(0, 1));
        assertNull("(Fox)Should be null", world.getField().get(0, 4));
        assertNull("(hunter)Should be null", world.getField().get(2, 1));
        assertNull("(Hunter)Should be null", world.getField().get(2, 4));
        assertNull("(rabbit)Should be null", world.getField().get(4, 0));
    }

    @Test
    public void Bulletfly() {
        world.getField().add(new Bullet(2, 2), 0, 0);
        world.nextRound();
        world.nextRound();
        world.nextRound();
        world.nextRound();
        assertNotNull("should be bullet", world.getField().get(4, 4));
    }

    @Test
    public void grass() {
        fillByTrees();
        world.getField().add(new Grass(), 1, 1);
        world.getField().add(null, 1, 2);
        world.nextRound();
        world.nextRound();
        world.nextRound();
        assertTrue("should be grass", world.getField().get(1, 2).toString().equals("G"));
        world.nextRound();
        world.nextRound();
        world.nextRound();
        world.nextRound();
        world.nextRound();
        world.nextRound();
        assertTrue("should be tree", world.getField().get(1, 1).toString().equals("T"));
    }

    @Test
    public void animalReproduce() {
        fillByTrees();
        Fox f1 = new Fox();
        f1.setFood(6);
        Fox f2 = new Fox();
        f2.setFood(6);
        world.getField().add(f1, 0, 0);
        world.getField().add(f2, 0, 1);
        world.getField().add(null, 1, 1);
        world.getField().add(new Rabbit(), 4, 4);
        world.getField().add(new Rabbit(), 4, 3);
        world.getField().add(null, 3, 3);
        world.nextRound();
        assertTrue("somewhere should be new Rabbit",
                (world.getField().get(4, 4) == null ? false : world.getField().get(4, 4).toString().equals("R"))
                && (world.getField().get(4, 3) == null ? false : world.getField().get(4, 3).toString().equals("R"))
                && (world.getField().get(3, 3) == null ? false : world.getField().get(3, 3).toString().equals("R")));
        world.nextRound();
        world.nextRound();
        assertTrue("somewhere should be new Fox",
                (world.getField().get(0, 0) == null ? false : world.getField().get(0, 0).toString().equals("F"))
                && (world.getField().get(0, 1) == null ? false : world.getField().get(0, 1).toString().equals("F"))
                && (world.getField().get(1, 1) == null ? false : world.getField().get(1, 1).toString().equals("F")));
    }

    public void fillByTrees() {
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                world.getField().add(new Tree(), i, j);
            }
        }
    }
}
