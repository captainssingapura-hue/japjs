package hue.captains.singapura.js.homing.studio.docs.brand;

import hue.captains.singapura.js.homing.studio.base.ClasspathMarkdownDoc;
import hue.captains.singapura.js.homing.studio.base.ImageReference;
import hue.captains.singapura.js.homing.studio.base.Reference;

import java.util.List;
import java.util.UUID;

public record BrandReadmeDoc() implements ClasspathMarkdownDoc {
    private static final UUID ID = UUID.fromString("0eedd6b6-ecb6-4709-873c-24d509ef1793");
    public static final BrandReadmeDoc INSTANCE = new BrandReadmeDoc();

    @Override public UUID   uuid()    { return ID; }
    @Override public String title()   { return "Brand Guide"; }
    @Override public String summary() { return "Logo concept, asset inventory, palette, typography, usage rules."; }
    @Override public String category(){ return "BRAND"; }

    @Override public List<Reference> references() {
        // Image rendering is deferred per RFC 0004-ext1 §4.6 (needs /asset endpoint).
        // Declared here so the markdown's #ref:logo-* citations resolve; the renderer
        // currently emits a text-only placeholder per ImageReference.
        String dir = "docs/hue/captains/singapura/js/homing/studio/docs/brand/";
        return List.of(
                new ImageReference("logo-primary",     dir + "logo-primary.svg",     "Homing primary logo on light",  "Mark + wordmark"),
                new ImageReference("logo-light",       dir + "logo-light.svg",       "Homing logo on dark",            "Mark + wordmark, light treatment"),
                new ImageReference("logo-extended",    dir + "logo-extended.svg",    "Homing extended logo",           "Mark + wordmark + tagline"),
                new ImageReference("logo-wordmark",    dir + "logo-wordmark.svg",    "Homing wordmark only",           "Wordmark only"),
                new ImageReference("logo-mark",        dir + "logo-mark.svg",        "Homing mark only",                "Mark only, transparent background"),
                new ImageReference("logo-mono-dark",   dir + "logo-mono-dark.svg",   "Homing mono-dark logo",          "Navy on transparent"),
                new ImageReference("logo-mono-light",  dir + "logo-mono-light.svg",  "Homing mono-light logo",         "White on navy"),
                new ImageReference("favicon",          dir + "favicon.svg",          "Homing favicon",                  "Tile mark for browser / OS icon")
        );
    }
}
