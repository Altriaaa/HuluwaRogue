import io.github.altriaaa.huluwarogue.creatures.Knight;
import io.github.altriaaa.huluwarogue.creatures.Orc;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class testgamelogic
{
    @Test
    public void testCollisionDetection()
    {
        Orc orc = new Orc();
        Knight knight = new Knight();

        orc.setPosition(0,0);
        knight.setPosition(0,0);

        boolean collision = orc.isAttacking(knight.getBoundingBox());
        assertTrue("Orc should collide with Knight.", collision);
    }
}


