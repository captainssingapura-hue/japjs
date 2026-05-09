package hue.captains.singapura.js.homing.demo;

import hue.captains.singapura.js.homing.core.AppModule;
import hue.captains.singapura.js.homing.core.SimpleAppResolver;
import hue.captains.singapura.js.homing.demo.es.DancingAnimals;
import hue.captains.singapura.js.homing.demo.es.DecomposedSvgDemo;
import hue.captains.singapura.js.homing.demo.es.DemoCatalogue;
import hue.captains.singapura.js.homing.demo.es.ExtrudedSvgDemo;
import hue.captains.singapura.js.homing.demo.es.ExtrudedTurtleDemo;
import hue.captains.singapura.js.homing.demo.es.MovingAnimal;
import hue.captains.singapura.js.homing.demo.es.SpinningAnimals;
import hue.captains.singapura.js.homing.demo.es.TurtleDemo;
import hue.captains.singapura.js.homing.demo.es.WonderlandDemo;
import hue.captains.singapura.js.homing.server.QueryParamResolver;
import hue.captains.singapura.tao.http.vertx.VertxActionHost;

import java.util.List;

public class WonderlandDemoServer {

    public static void main(String[] args) {
        // RFC 0001 Step 11: single entry app — DemoCatalogue links to every other
        // demo via typed AppLink imports, so the resolver discovers the whole
        // graph transitively. (Pre-Step-11 we had to enumerate every app.)
        var appResolver = new SimpleAppResolver(List.<AppModule<?>>of(
                DemoCatalogue.INSTANCE
        ));

        var nameResolver = new QueryParamResolver();
        var registry = new DemoActionRegistry(nameResolver, appResolver);
        var host = new VertxActionHost(registry, 8080);

        host.start().onSuccess(server -> {
            System.out.println("Homing server listening on port " + server.actualPort());
            System.out.println("Registered apps (RFC 0001): " + appResolver.apps().size()
                             + " · proxies: " + appResolver.proxies().size());
            System.out.println();
            System.out.println("Try (new ?app= contract — RFC 0001 Step 07):");
            for (var app : appResolver.apps()) {
                System.out.println("  http://localhost:8080/app?app=" + app.simpleName()
                                 + "    (" + app.title() + ")");
            }
            System.out.println();
            System.out.println("Legacy ?class= URLs continue to work during the Step 11 migration window.");
            System.out.println("Endpoints:");
            System.out.println("  /app?app=<simpleName>           ← new RFC 0001 contract");
            System.out.println("  /app?class=<canonical>          ← legacy fallback");
            System.out.println("  /module?class=<canonical>");
            System.out.println("  /css?class=<canonical>");
            System.out.println("  /css-content?class=<canonical>");
        }).onFailure(err -> {
            System.err.println("Failed to start: " + err.getMessage());
            System.exit(1);
        });
    }
}
