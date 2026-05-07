package hue.captains.singapura.js.homing.demo.theme;

import hue.captains.singapura.js.homing.core.Theme;

public record Alpine() implements Theme {

    public static final Alpine INSTANCE = new Alpine();

    @Override public String slug()  { return "alpine"; }
    @Override public String label() { return "Alpine"; }
}
