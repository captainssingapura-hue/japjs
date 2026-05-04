package hue.captains.singapura.js.homing.server;

import hue.captains.singapura.js.homing.demo.css.AliceStyles;
import hue.captains.singapura.js.homing.demo.css.BaseStyles;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.*;

class CssGetActionTest {

    private final CssGetAction action = new CssGetAction();

    @Test
    void execute_resolvesAliceStylesWithDeps() throws Exception {
        var query = new ModuleQuery(AliceStyles.class.getCanonicalName());
        List<CssGetAction.CssEntry> result = action.execute(query, new EmptyParam.NoHeaders()).get();

        // AliceStyles depends on BaseStyles, so BaseStyles comes first
        assertEquals(2, result.size());
        assertEquals(BaseStyles.class.getCanonicalName(), result.get(0).name());
        assertTrue(result.get(0).href().contains("/css-content?class="));
        assertEquals(AliceStyles.class.getCanonicalName(), result.get(1).name());
        assertTrue(result.get(1).href().contains("/css-content?class="));
    }

    @Test
    void execute_resolvesBaseStylesAlone() throws Exception {
        var query = new ModuleQuery(BaseStyles.class.getCanonicalName());
        List<CssGetAction.CssEntry> result = action.execute(query, new EmptyParam.NoHeaders()).get();

        assertEquals(1, result.size());
        assertEquals(BaseStyles.class.getCanonicalName(), result.getFirst().name());
    }

    @Test
    void execute_failsForNonCssGroup() {
        // Alice is an EsModule, not a CssGroup
        var query = new ModuleQuery("hue.captains.singapura.js.homing.demo.es.Alice");
        var future = action.execute(query, new EmptyParam.NoHeaders());

        var ex = assertThrows(ExecutionException.class, future::get);
        assertInstanceOf(ResourceNotFound.class, ex.getCause());
    }

    @Test
    void execute_failsForNullClassName() {
        var query = new ModuleQuery(null);
        var future = action.execute(query, new EmptyParam.NoHeaders());

        var ex = assertThrows(ExecutionException.class, future::get);
        assertInstanceOf(ResourceNotFound.class, ex.getCause());
    }
}
