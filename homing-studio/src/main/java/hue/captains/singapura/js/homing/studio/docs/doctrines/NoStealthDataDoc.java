package hue.captains.singapura.js.homing.studio.docs.doctrines;

import hue.captains.singapura.js.homing.studio.base.ClasspathMarkdownDoc;
import hue.captains.singapura.js.homing.studio.base.DocReference;
import hue.captains.singapura.js.homing.studio.base.Reference;
import hue.captains.singapura.js.homing.studio.docs.rfcs.Rfc0001Doc;

import java.util.List;
import java.util.UUID;

/**
 * Doctrine — No Stealth Data. No client-side data gathering the user cannot
 * see and audit. No telemetry, no fingerprinting, no analytics SDKs, no
 * silent error reporters, no opaque cookies, no third-party tracker embeds.
 * Disclosed, opt-in, user-controllable collection is welcome; covert
 * collection is banned absolutely. The keyword is <i>stealth</i> —
 * visibility is the test.
 *
 * <p>Codifies what the framework already does (zero analytics, zero
 * telemetry, zero third-party trackers in the codebase) so a future
 * regression becomes a doctrine violation rather than a silent shipment.
 * Pairs with the Functional Objects doctrine — that one refused covert
 * <i>behaviour</i> surfaces, this one refuses covert <i>data</i>
 * surfaces.</p>
 */
public record NoStealthDataDoc() implements ClasspathMarkdownDoc {
    private static final UUID ID = UUID.fromString("a7f3b8c2-5e1d-4a6b-9c8f-3e2b1d7a4f50");
    public static final NoStealthDataDoc INSTANCE = new NoStealthDataDoc();

    @Override public UUID   uuid()    { return ID; }
    @Override public String title()   { return "Doctrine — No Stealth Data"; }
    @Override public String summary() { return "No client-side data gathering the user cannot see and audit. No telemetry, no fingerprinting, no analytics SDKs, no silent error reporters, no opaque cookies, no third-party tracker embeds. Disclosed, opt-in, user-controllable collection is welcome; covert collection is banned absolutely. Visibility is the test."; }
    @Override public String category(){ return "DOCTRINE"; }

    @Override public List<Reference> references() {
        return List.of(
                new DocReference("doc-ss",  StatelessServerDoc.INSTANCE),
                new DocReference("doc-qws", QualityWithoutSurveillanceDoc.INSTANCE),
                new DocReference("doc-fo",  FunctionalObjectsDoc.INSTANCE),
                new DocReference("doc-pcv", PureComponentViewsDoc.INSTANCE),
                new DocReference("doc-or",  OwnedReferencesDoc.INSTANCE),
                new DocReference("rfc-1",   Rfc0001Doc.INSTANCE)
        );
    }
}
