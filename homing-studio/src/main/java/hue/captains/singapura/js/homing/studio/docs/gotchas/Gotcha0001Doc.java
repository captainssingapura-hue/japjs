package hue.captains.singapura.js.homing.studio.docs.gotchas;

import hue.captains.singapura.js.homing.studio.base.ClasspathMarkdownDoc;
import hue.captains.singapura.js.homing.studio.base.Reference;

import java.util.List;
import java.util.UUID;

public record Gotcha0001Doc() implements ClasspathMarkdownDoc {
    private static final UUID ID = UUID.fromString("3bc1dac1-6159-4b03-9668-06dbe58bbd58");
    public static final Gotcha0001Doc INSTANCE = new Gotcha0001Doc();

    @Override public UUID   uuid()    { return ID; }
    @Override public String title()   { return "Gotcha 0001 — String.formatted() in code-gen text blocks"; }
    @Override public String summary() { return "Java text blocks emitting JS/CSS/HTML with `String.formatted()` parse every `%` as a printf format specifier. CSS keyframes (0%, 30%), JS modulo (a % b), URL-encoded chars (%23) all silently break the build and surface as opaque IllegalFormatFlagsException at request time. Bit us three times in a single session. Fix: named-placeholder substitution via String.replace() — makes `%` an ordinary character."; }
    @Override public String category(){ return "GOTCHA"; }

    @Override public List<Reference> references() { return List.of(); }
}
