package hue.captains.singapura.js.homing.studio.docs.doctrines;

import hue.captains.singapura.js.homing.studio.base.ClasspathMarkdownDoc;
import hue.captains.singapura.js.homing.studio.base.DocReference;
import hue.captains.singapura.js.homing.studio.base.Reference;

import java.util.List;
import java.util.UUID;

/**
 * Doctrine — Stateless Server. The server is stateless by default. Its role
 * is to provide sufficient information, code, and — when explicit — remote
 * functions for the client to run the app on their own machine. Any
 * client-to-server communication flows through transparent, auditable
 * channels (a visible URL, an inspectable payload, no covert sidebands).
 * Storing client data on the server is the exception that requires explicit
 * justification, disclosure, audit, erasability, and minimisation — not the
 * default.
 *
 * <p>Server-side counterpart to {@link NoStealthDataDoc}. That doctrine
 * refuses covert <i>gathering</i> on the client; this one refuses covert
 * <i>retention</i> on the server. Together: client data is the client's;
 * the server's job is delivery, not memory.</p>
 *
 * <p>Codifies what the framework's current server already does (every
 * endpoint is a stateless GET keyed by request content; no sessions, no
 * user-identity cookies, no per-user storage) so future regressions become
 * doctrine violations.</p>
 */
public record StatelessServerDoc() implements ClasspathMarkdownDoc {
    private static final UUID ID = UUID.fromString("b8e4c5d7-2a1f-4b6e-9c3d-7f5a8e2b1d40");
    public static final StatelessServerDoc INSTANCE = new StatelessServerDoc();

    @Override public UUID   uuid()    { return ID; }
    @Override public String title()   { return "Doctrine — Stateless Server"; }
    @Override public String summary() { return "The server is stateless by default — it provides information, code, and optionally explicit remote functions for the client to run the app locally. All client communication flows through transparent, auditable channels (visible URLs, inspectable payloads). Storing client data server-side is the exception requiring justification + disclosure + audit + erasability + minimisation; the default is to retain nothing about any user, session, or device."; }
    @Override public String category(){ return "DOCTRINE"; }

    @Override public List<Reference> references() {
        return List.of(
                new DocReference("doc-nsd", NoStealthDataDoc.INSTANCE),
                new DocReference("doc-qws", QualityWithoutSurveillanceDoc.INSTANCE),
                new DocReference("doc-fo",  FunctionalObjectsDoc.INSTANCE),
                new DocReference("doc-pcv", PureComponentViewsDoc.INSTANCE),
                new DocReference("doc-or",  OwnedReferencesDoc.INSTANCE)
        );
    }
}
