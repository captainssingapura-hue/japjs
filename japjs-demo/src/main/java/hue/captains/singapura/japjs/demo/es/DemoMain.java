package hue.captains.singapura.japjs.demo.es;

import hue.captains.singapura.japjs.core.util.ReadContentFromResources;

import java.util.stream.Stream;

public class DemoMain{
    public static void main(String[] args) {
        Stream.of(Bob.INSTANCE, Alice.INSTANCE).map(
                ReadContentFromResources::new
        ).forEach(
                cp -> cp.content().stream().forEach(System.out::println)
        );
    }
}
