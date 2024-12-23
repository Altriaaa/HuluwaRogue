import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Stage;
import io.github.altriaaa.huluwarogue.GameWorld;
import io.github.altriaaa.huluwarogue.creatures.Knight;
import io.github.altriaaa.huluwarogue.creatures.Orc;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestApplication extends ApplicationAdapter
{
    @Override
    public void create()
    {
        // 执行测试逻辑
        try
        {
            GameWorld.getInstance().assetInit();
            GameWorld.getInstance().worldInit();
            performTests();
//            GameWorld.getInstance().save();
//            GameWorld.getInstance().load();
        } finally
        {
            // 测试完成后退出
            Gdx.app.exit();
        }
    }

    private void performTests()
    {
        Stage stage = new Stage();

        Orc orc = new Orc();
        Knight knight = new Knight();

        stage.addActor(orc);
        stage.addActor(knight);

        orc.setPosition(0, 0);
        orc.setBox();
        knight.setPosition(0, 0);
        knight.setBox();

        knight.vulDamage(1);
        orc.vulDamage(1);

        knight.remove();

        boolean collision = orc.isAttacking(knight.getBoundingBox());
        assertTrue(collision, "Orc should collide with Knight.");
    }
}
