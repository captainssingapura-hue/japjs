package hue.captains.singapura.js.homing.demo.theme;

import hue.captains.singapura.js.homing.core.Theme;

/**
 * The default theme for the homing-demo apps. Slug {@code "homing-default"}
 * to match the framework convention so default {@code /css-content} requests
 * (omitting {@code ?theme=}) resolve here.
 */
public record DemoDefault() implements Theme {

    public static final DemoDefault INSTANCE = new DemoDefault();

    @Override public String slug()  { return "homing-default"; }
    @Override public String label() { return "Demo default"; }
}
