package hue.captains.singapura.japjs.studio;

import hue.captains.singapura.japjs.server.QueryParamResolver;
import hue.captains.singapura.japjs.studio.es.DocBrowser;
import hue.captains.singapura.japjs.studio.es.DocReader;
import hue.captains.singapura.japjs.studio.es.StudioCatalogue;
import hue.captains.singapura.tao.http.vertx.VertxActionHost;

import java.nio.file.Path;

/**
 * Entry point for the japjs studio — design &amp; project management workspace.
 *
 * <p>Live-reads markdown documents from a docs root on disk. Defaults to
 * {@code ./docs} relative to the working directory. Override with
 * {@code -Djapjs.studio.docsRoot=/path/to/docs}.</p>
 *
 * <p>Run with:</p>
 * <pre>
 * mvn -pl japjs-studio exec:java \
 *   -Dexec.mainClass="hue.captains.singapura.japjs.studio.StudioServer"
 * </pre>
 *
 * <p>Then open <code>http://localhost:8080/app?class=hue.captains.singapura.japjs.studio.es.StudioCatalogue</code></p>
 */
public class StudioServer {

    public static void main(String[] args) {
        Path docsRoot = Path.of(System.getProperty("japjs.studio.docsRoot", "docs"))
                            .toAbsolutePath().normalize();

        var nameResolver = new QueryParamResolver();
        var registry     = new StudioActionRegistry(nameResolver, docsRoot);
        var host         = new VertxActionHost(registry, 8080);

        host.start().onSuccess(server -> {
            System.out.println("japjs studio listening on port " + server.actualPort());
            System.out.println("docs root: " + docsRoot);
            System.out.println();
            System.out.println("Try:");
            System.out.println("  http://localhost:8080/app?class=" + StudioCatalogue.class.getCanonicalName() + "  ← studio launcher (start here)");
            System.out.println("  http://localhost:8080/app?class=" + DocBrowser.class.getCanonicalName());
            System.out.println("  http://localhost:8080/app?class=" + DocReader.class.getCanonicalName() + "&path=whitepaper/japjs-whitepaper.md");
            System.out.println();
            System.out.println("Endpoints:");
            System.out.println("  /app           → AppModule HTML bootstrap");
            System.out.println("  /module        → generated ES module");
            System.out.println("  /css           → resolved CSS chain");
            System.out.println("  /css-content   → raw CSS body");
            System.out.println("  /doc-content   → studio: markdown body for ?path=<rel>");
        }).onFailure(err -> {
            System.err.println("Failed to start: " + err.getMessage());
            System.exit(1);
        });
    }
}
