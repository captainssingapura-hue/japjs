package hue.captains.singapura.js.homing.demo.theme;

import hue.captains.singapura.js.homing.core.Theme;

public record Dracula() implements Theme {

    public static final Dracula INSTANCE = new Dracula();

    @Override public String slug()  { return "dracula"; }
    @Override public String label() { return "Dracula"; }
}
