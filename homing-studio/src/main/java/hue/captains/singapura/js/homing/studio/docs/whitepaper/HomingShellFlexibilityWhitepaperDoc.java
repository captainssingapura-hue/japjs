package hue.captains.singapura.js.homing.studio.docs.whitepaper;

import hue.captains.singapura.js.homing.studio.base.ClasspathMarkdownDoc;
import hue.captains.singapura.js.homing.studio.base.ExternalReference;
import hue.captains.singapura.js.homing.studio.base.Reference;

import java.util.List;
import java.util.UUID;

public record HomingShellFlexibilityWhitepaperDoc() implements ClasspathMarkdownDoc {
    private static final UUID ID = UUID.fromString("e58f540f-9be2-498b-bca7-188c53d09701");
    public static final HomingShellFlexibilityWhitepaperDoc INSTANCE = new HomingShellFlexibilityWhitepaperDoc();

    @Override public UUID   uuid()    { return ID; }
    @Override public String title()   { return "Shell Flexibility — Exploration"; }
    @Override public String summary() { return "Homing's output is shell-agnostic. The Java backend with CLI parity is the killer feature."; }
    @Override public String category(){ return "WHITEPAPER"; }

    @Override public List<Reference> references() {
        return List.of(
                new ExternalReference("picocli", "https://picocli.info/",
                        "Picocli", "Java framework for building CLI command trees, cited as an example shell-mode dispatcher.")
        );
    }
}
