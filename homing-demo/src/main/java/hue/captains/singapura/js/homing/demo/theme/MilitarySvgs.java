package hue.captains.singapura.js.homing.demo.theme;

/**
 * Vehicle silhouette SVGs for the three military themes — used as
 * {@code background-image} data-URIs on platform variants in the
 * MovingAnimal demo. Side-view, monochrome.
 *
 * <p><b>Geometry contract:</b> each SVG's top edge ({@code y=0}) is the
 * "standable line" — the highest point of the vehicle the animal lands on.
 * The vehicle silhouette extends downward from {@code y=0}. The viewBox's
 * width:height aspect dictates the vehicle's natural footprint and is
 * paired with the platform width bands in {@code PlatformEngine.js}
 * (v1 = largest ~150, v2 = medium ~120, v3 = smallest ~90).</p>
 */
final class MilitarySvgs {

    private MilitarySvgs() {}

    private static String url(String svg) {
        return "url('data:image/svg+xml;utf8," + svg.replace("\n", "") + "')";
    }

    // -------------------------------------------------------------------
    // ARMY — green/khaki silhouettes.
    //   v1 = Tank        (longest)
    //   v2 = Armored truck
    //   v3 = Humvee       (shortest)
    // -------------------------------------------------------------------

    static final String ARMY_TANK = url(
            "<svg xmlns=\"http://www.w3.org/2000/svg\" viewBox=\"0 0 150 30\" preserveAspectRatio=\"none\">"
          + "<rect x=\"55\" y=\"0\" width=\"40\" height=\"10\" rx=\"2\" fill=\"%234a5a30\"/>"
          + "<rect x=\"93\" y=\"4\" width=\"42\" height=\"3\" fill=\"%234a5a30\"/>"
          + "<rect x=\"10\" y=\"10\" width=\"130\" height=\"14\" fill=\"%234a5a30\"/>"
          + "<rect x=\"3\" y=\"22\" width=\"144\" height=\"6\" rx=\"3\" fill=\"%231a2010\"/>"
          + "<circle cx=\"15\" cy=\"26\" r=\"3\" fill=\"%230a0e05\"/>"
          + "<circle cx=\"35\" cy=\"26\" r=\"3\" fill=\"%230a0e05\"/>"
          + "<circle cx=\"55\" cy=\"26\" r=\"3\" fill=\"%230a0e05\"/>"
          + "<circle cx=\"80\" cy=\"26\" r=\"3\" fill=\"%230a0e05\"/>"
          + "<circle cx=\"105\" cy=\"26\" r=\"3\" fill=\"%230a0e05\"/>"
          + "<circle cx=\"130\" cy=\"26\" r=\"3\" fill=\"%230a0e05\"/>"
          + "</svg>");

    static final String ARMY_TRUCK = url(
            "<svg xmlns=\"http://www.w3.org/2000/svg\" viewBox=\"0 0 120 30\" preserveAspectRatio=\"none\">"
          + "<rect x=\"10\" y=\"0\" width=\"100\" height=\"22\" fill=\"%234a5a30\"/>"
          + "<rect x=\"42\" y=\"0\" width=\"68\" height=\"6\" fill=\"%233a4a25\"/>"
          + "<rect x=\"40\" y=\"2\" width=\"2\" height=\"20\" fill=\"%232a3018\"/>"
          + "<rect x=\"14\" y=\"4\" width=\"22\" height=\"9\" fill=\"%231a2510\"/>"
          + "<rect x=\"8\" y=\"22\" width=\"105\" height=\"3\" fill=\"%232a3018\"/>"
          + "<circle cx=\"22\" cy=\"26\" r=\"4\" fill=\"%230a0e05\"/>"
          + "<circle cx=\"60\" cy=\"26\" r=\"4\" fill=\"%230a0e05\"/>"
          + "<circle cx=\"78\" cy=\"26\" r=\"4\" fill=\"%230a0e05\"/>"
          + "<circle cx=\"98\" cy=\"26\" r=\"4\" fill=\"%230a0e05\"/>"
          + "</svg>");

    static final String ARMY_HUMVEE = url(
            "<svg xmlns=\"http://www.w3.org/2000/svg\" viewBox=\"0 0 90 30\" preserveAspectRatio=\"none\">"
          + "<rect x=\"18\" y=\"0\" width=\"45\" height=\"10\" fill=\"%234a5a30\"/>"
          + "<rect x=\"8\" y=\"10\" width=\"75\" height=\"12\" fill=\"%234a5a30\"/>"
          + "<rect x=\"68\" y=\"14\" width=\"15\" height=\"8\" fill=\"%233a4a25\"/>"
          + "<rect x=\"22\" y=\"2\" width=\"18\" height=\"6\" fill=\"%231a2510\"/>"
          + "<rect x=\"42\" y=\"2\" width=\"18\" height=\"6\" fill=\"%231a2510\"/>"
          + "<polygon points=\"60,2 68,14 60,14\" fill=\"%231a2510\"/>"
          + "<circle cx=\"22\" cy=\"25\" r=\"5\" fill=\"%230a0e05\"/>"
          + "<circle cx=\"68\" cy=\"25\" r=\"5\" fill=\"%230a0e05\"/>"
          + "</svg>");

    // -------------------------------------------------------------------
    // NAVY — gray/blue ship silhouettes.
    //   v1 = Aircraft carrier (longest)
    //   v2 = Destroyer
    //   v3 = Submarine        (shortest, low profile)
    // -------------------------------------------------------------------

    static final String NAVY_CARRIER = url(
            "<svg xmlns=\"http://www.w3.org/2000/svg\" viewBox=\"0 0 160 30\" preserveAspectRatio=\"none\">"
          + "<rect x=\"110\" y=\"0\" width=\"18\" height=\"12\" fill=\"%235a6a7a\"/>"
          + "<rect x=\"4\" y=\"10\" width=\"152\" height=\"6\" fill=\"%234a5a6a\"/>"
          + "<path d=\"M 8 16 L 12 12 L 150 12 L 156 16 L 150 26 L 14 26 Z\" fill=\"%233a4a5a\"/>"
          + "<polygon points=\"20,10 26,10 28,8 22,8\" fill=\"%236a7a8a\"/>"
          + "<polygon points=\"40,10 46,10 48,8 42,8\" fill=\"%236a7a8a\"/>"
          + "<polygon points=\"60,10 66,10 68,8 62,8\" fill=\"%236a7a8a\"/>"
          + "<polygon points=\"80,10 86,10 88,8 82,8\" fill=\"%236a7a8a\"/>"
          + "<line x1=\"0\" y1=\"26\" x2=\"160\" y2=\"26\" stroke=\"%231a2530\" stroke-width=\"1\"/>"
          + "</svg>");

    static final String NAVY_DESTROYER = url(
            "<svg xmlns=\"http://www.w3.org/2000/svg\" viewBox=\"0 0 130 30\" preserveAspectRatio=\"none\">"
          + "<rect x=\"55\" y=\"0\" width=\"20\" height=\"12\" fill=\"%234a5a6a\"/>"
          + "<rect x=\"40\" y=\"10\" width=\"35\" height=\"10\" fill=\"%234a5a6a\"/>"
          + "<path d=\"M 5 22 L 10 18 L 115 18 L 125 22 L 115 28 L 15 28 Z\" fill=\"%233a4a5a\"/>"
          + "<rect x=\"20\" y=\"14\" width=\"12\" height=\"4\" fill=\"%235a6a7a\"/>"
          + "<line x1=\"32\" y1=\"16\" x2=\"40\" y2=\"16\" stroke=\"%235a6a7a\" stroke-width=\"2\"/>"
          + "<line x1=\"0\" y1=\"28\" x2=\"130\" y2=\"28\" stroke=\"%231a2530\" stroke-width=\"1\"/>"
          + "</svg>");

    static final String NAVY_SUBMARINE = url(
            "<svg xmlns=\"http://www.w3.org/2000/svg\" viewBox=\"0 0 130 30\" preserveAspectRatio=\"none\">"
          + "<rect x=\"54\" y=\"0\" width=\"22\" height=\"14\" fill=\"%232a3540\"/>"
          + "<ellipse cx=\"65\" cy=\"20\" rx=\"60\" ry=\"7\" fill=\"%232a3540\"/>"
          + "<line x1=\"0\" y1=\"26\" x2=\"130\" y2=\"26\" stroke=\"%231a2530\" stroke-width=\"1\"/>"
          + "</svg>");

    // -------------------------------------------------------------------
    // AIR FORCE — blue/gray aircraft silhouettes.
    //   v1 = A-10 Warthog (largest)
    //   v2 = Fighter jet
    //   v3 = Apache       (compact)
    // -------------------------------------------------------------------

    static final String AF_A10 = url(
            "<svg xmlns=\"http://www.w3.org/2000/svg\" viewBox=\"0 0 130 28\" preserveAspectRatio=\"none\">"
          + "<ellipse cx=\"40\" cy=\"5\" rx=\"22\" ry=\"4\" fill=\"%234a5a4a\"/>"
          + "<polygon points=\"3,8 10,0 16,8\" fill=\"%233a4a3a\"/>"
          + "<rect x=\"15\" y=\"10\" width=\"100\" height=\"9\" rx=\"3\" fill=\"%233a4a3a\"/>"
          + "<rect x=\"40\" y=\"19\" width=\"55\" height=\"4\" fill=\"%233a4a3a\"/>"
          + "<polygon points=\"115,11 125,13 125,16 115,18\" fill=\"%233a4a3a\"/>"
          + "<line x1=\"125\" y1=\"14\" x2=\"130\" y2=\"14\" stroke=\"%231a2010\" stroke-width=\"2\"/>"
          + "<ellipse cx=\"100\" cy=\"12\" rx=\"8\" ry=\"2.5\" fill=\"%231a2010\"/>"
          + "</svg>");

    static final String AF_FIGHTER = url(
            "<svg xmlns=\"http://www.w3.org/2000/svg\" viewBox=\"0 0 110 28\" preserveAspectRatio=\"none\">"
          + "<ellipse cx=\"70\" cy=\"4\" rx=\"14\" ry=\"4\" fill=\"%232a3540\"/>"
          + "<path d=\"M 5 10 L 15 6 L 100 6 L 110 10 L 100 14 L 15 14 Z\" fill=\"%235a6a7a\"/>"
          + "<polygon points=\"35,10 25,20 60,20 65,10\" fill=\"%234a5a6a\"/>"
          + "<polygon points=\"20,6 28,0 32,6\" fill=\"%234a5a6a\"/>"
          + "<rect x=\"5\" y=\"9\" width=\"10\" height=\"5\" fill=\"%231a2530\"/>"
          + "</svg>");

    static final String AF_APACHE = url(
            "<svg xmlns=\"http://www.w3.org/2000/svg\" viewBox=\"0 0 100 28\" preserveAspectRatio=\"none\">"
          + "<line x1=\"15\" y1=\"0\" x2=\"95\" y2=\"0\" stroke=\"%232a3530\" stroke-width=\"2\"/>"
          + "<line x1=\"50\" y1=\"0\" x2=\"50\" y2=\"10\" stroke=\"%232a3530\" stroke-width=\"2\"/>"
          + "<path d=\"M 13 10 L 23 6 L 70 6 L 75 10 L 70 16 L 13 16 Z\" fill=\"%233a4a3a\"/>"
          + "<rect x=\"73\" y=\"10\" width=\"24\" height=\"4\" fill=\"%233a4a3a\"/>"
          + "<line x1=\"97\" y1=\"6\" x2=\"97\" y2=\"20\" stroke=\"%232a3530\" stroke-width=\"2\"/>"
          + "<polygon points=\"92,10 97,3 99,10\" fill=\"%233a4a3a\"/>"
          + "<ellipse cx=\"27\" cy=\"10\" rx=\"4\" ry=\"2\" fill=\"%231a2010\"/>"
          + "<ellipse cx=\"43\" cy=\"11\" rx=\"4\" ry=\"2\" fill=\"%231a2010\"/>"
          + "<line x1=\"20\" y1=\"18\" x2=\"65\" y2=\"18\" stroke=\"%232a3530\" stroke-width=\"2\"/>"
          + "<line x1=\"25\" y1=\"16\" x2=\"25\" y2=\"20\" stroke=\"%232a3530\" stroke-width=\"1.5\"/>"
          + "<line x1=\"60\" y1=\"16\" x2=\"60\" y2=\"20\" stroke=\"%232a3530\" stroke-width=\"1.5\"/>"
          + "</svg>");
}
