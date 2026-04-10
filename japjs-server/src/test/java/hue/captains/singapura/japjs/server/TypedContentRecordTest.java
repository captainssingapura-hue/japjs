package hue.captains.singapura.japjs.server;

import hue.captains.singapura.tao.http.action.TypedContent;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TypedContentRecordTest {

    @Test
    void jsModuleContent_hasCorrectContentType() {
        var content = new JsModuleContent("const x = 1;");
        assertInstanceOf(TypedContent.class, content);
        assertInstanceOf(TypedContent.Js.class, content);
        assertEquals("application/javascript", content.contentType());
        assertEquals("const x = 1;", content.body());
    }

    @Test
    void htmlPageContent_hasCorrectContentType() {
        var content = new HtmlPageContent("<html></html>");
        assertInstanceOf(TypedContent.class, content);
        assertInstanceOf(TypedContent.Html.class, content);
        assertEquals("text/html", content.contentType());
        assertEquals("<html></html>", content.body());
    }
}
