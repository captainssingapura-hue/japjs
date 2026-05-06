package hue.captains.singapura.js.homing.studio.base;

import hue.captains.singapura.js.homing.server.EmptyParam;
import hue.captains.singapura.js.homing.server.ResourceNotFound;
import org.junit.jupiter.api.Test;

import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.*;

class DocGetActionTest {

    private final DocGetAction action = new DocGetAction();

    @Test
    void execute_returnsMarkdownBytes() throws Exception {
        var result = action.execute(
                new DocGetAction.Query("homing/md/test/sentinel-doc.md"),
                new EmptyParam.NoHeaders()).get();

        assertNotNull(result);
        assertEquals("text/markdown; charset=utf-8", result.contentType());
        assertTrue(result.body().contains("# Sentinel Doc"),
                "Should contain markdown header verbatim");
        assertTrue(result.body().contains("the wiring works end-to-end"),
                "Should contain prose verbatim");
    }

    @Test
    void execute_rejectsPathTraversal() {
        var future = action.execute(
                new DocGetAction.Query("../../etc/passwd"),
                new EmptyParam.NoHeaders());
        var ex = assertThrows(ExecutionException.class, future::get);
        assertInstanceOf(ResourceNotFound.class, ex.getCause());
    }

    @Test
    void execute_rejectsNonMarkdown() {
        var future = action.execute(
                new DocGetAction.Query("homing/md/test/sentinel-doc.txt"),
                new EmptyParam.NoHeaders());
        var ex = assertThrows(ExecutionException.class, future::get);
        assertInstanceOf(ResourceNotFound.class, ex.getCause());
    }

    @Test
    void execute_rejectsLeadingSlash() {
        var future = action.execute(
                new DocGetAction.Query("/homing/md/test/sentinel-doc.md"),
                new EmptyParam.NoHeaders());
        var ex = assertThrows(ExecutionException.class, future::get);
        assertInstanceOf(ResourceNotFound.class, ex.getCause());
    }

    @Test
    void execute_failsOnMissingResource() {
        var future = action.execute(
                new DocGetAction.Query("homing/md/does-not-exist.md"),
                new EmptyParam.NoHeaders());
        var ex = assertThrows(ExecutionException.class, future::get);
        assertInstanceOf(ResourceNotFound.class, ex.getCause());
    }

    @Test
    void execute_failsOnNullPath() {
        var future = action.execute(
                new DocGetAction.Query(null),
                new EmptyParam.NoHeaders());
        var ex = assertThrows(ExecutionException.class, future::get);
        assertInstanceOf(ResourceNotFound.class, ex.getCause());
    }
}
