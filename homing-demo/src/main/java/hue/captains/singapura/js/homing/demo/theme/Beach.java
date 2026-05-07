package hue.captains.singapura.js.homing.demo.theme;

import hue.captains.singapura.js.homing.core.Theme;

public record Beach() implements Theme {

    public static final Beach INSTANCE = new Beach();

    @Override public String slug()  { return "beach"; }
    @Override public String label() { return "Beach"; }
}
