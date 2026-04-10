package hue.captains.singapura.japjs.demo;

import hue.captains.singapura.japjs.demo.es.TurtleDemo;
import hue.captains.singapura.japjs.demo.es.WonderlandDemo;
import hue.captains.singapura.japjs.server.JapjsActionRegistry;
import hue.captains.singapura.japjs.server.QueryParamResolver;
import hue.captains.singapura.tao.http.vertx.VertxActionHost;

public class WonderlandDemoServer {

    public static void main(String[] args) {
        var nameResolver = new QueryParamResolver();
        var registry = new JapjsActionRegistry(nameResolver);
        var host = new VertxActionHost(registry, 8080);

        host.start().onSuccess(server -> {
            System.out.println("japjs server listening on port " + server.actualPort());
            System.out.println();
            System.out.println("Try:");
            System.out.println("  http://localhost:8080/app?class=" + WonderlandDemo.class.getCanonicalName());
            System.out.println("  http://localhost:8080/app?class=" + TurtleDemo.class.getCanonicalName());
            System.out.println();
            System.out.println("Modules served dynamically at:");
            System.out.println("  /module?class=<canonical.class.name>");
            System.out.println("  /app?class=<AppModule.class.name>");
        }).onFailure(err -> {
            System.err.println("Failed to start: " + err.getMessage());
            System.exit(1);
        });
    }
}
