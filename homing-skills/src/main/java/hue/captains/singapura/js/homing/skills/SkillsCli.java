package hue.captains.singapura.js.homing.skills;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Entry point for the {@code homing-skills} bundle. Two modes — both required
 * by the Dual-Audience Skills doctrine, both shipped from the same classpath
 * source. Consumption is Maven-native: downstream projects depend on the
 * {@code homing-skills} artifact and invoke this main class via
 * {@code mvn exec:java} — no plugin config required.
 *
 * <pre>
 *   mvn exec:java -Dexec.mainClass=hue.captains.singapura.js.homing.skills.SkillsCli
 *                                                          # default — serve the mini-studio
 *   mvn exec:java -Dexec.mainClass=… -Dexec.args="--dump"  # dump skills to .claude/skills/
 *   mvn exec:java -Dexec.mainClass=… -Dexec.args="--port 9000"
 * </pre>
 *
 * <p>Dump auto-targets {@code .claude/skills/} when a {@code .claude} folder
 * exists in the current directory; otherwise it requires an explicit path —
 * keeps us from littering an unrelated working directory by accident.</p>
 */
public final class SkillsCli {

    private static final int DEFAULT_PORT = 8083;
    private static final String DEFAULT_DUMP_PARENT = ".claude";
    private static final String DEFAULT_DUMP_NAME   = "skills";

    public static void main(String[] args) throws IOException {
        // Walk args once; the two flags are mutually exclusive but we don't enforce
        // that strictly — `--dump` wins if both appear (non-interactive mode is
        // the safer interpretation when ambiguity arises in CI / scripts).
        boolean dump   = false;
        String  target = null;
        int     port   = DEFAULT_PORT;
        boolean help   = false;

        for (int i = 0; i < args.length; i++) {
            String arg = args[i];
            switch (arg) {
                case "--dump", "-d" -> {
                    dump = true;
                    if (i + 1 < args.length && !args[i + 1].startsWith("-")) {
                        target = args[++i];
                    }
                }
                case "--port", "-p" -> {
                    if (i + 1 >= args.length) {
                        System.err.println("Missing value for " + arg);
                        System.exit(2);
                    }
                    try {
                        port = Integer.parseInt(args[++i]);
                    } catch (NumberFormatException e) {
                        System.err.println("Invalid port: " + args[i]);
                        System.exit(2);
                    }
                }
                case "--help", "-h" -> help = true;
                default -> {
                    System.err.println("Unknown argument: " + arg);
                    System.err.println();
                    printUsage(System.err);
                    System.exit(2);
                }
            }
        }

        if (help) {
            printUsage(System.out);
            return;
        }

        if (dump) {
            runDump(target);
        } else {
            runServe(port);
        }
    }

    private static void runDump(String explicitTarget) throws IOException {
        Path target;
        if (explicitTarget != null) {
            target = Path.of(explicitTarget);
        } else {
            // Auto-target: ./.claude/skills only when a .claude/ folder already
            // exists in the cwd. Otherwise require an explicit path to avoid
            // creating a .claude/ in someone's unrelated working directory.
            Path cwd = Path.of("").toAbsolutePath();
            Path claude = cwd.resolve(DEFAULT_DUMP_PARENT);
            if (!Files.isDirectory(claude)) {
                System.err.println("No .claude/ directory found in the current path —");
                System.err.println("pass an explicit target to avoid surprising side-effects:");
                System.err.println();
                System.err.println("  mvn exec:java@skills-dump  (with -Dexec.args=\"--dump <dir>\")");
                System.exit(2);
                return;
            }
            target = claude.resolve(DEFAULT_DUMP_NAME);
        }

        System.out.println("Dumping " + SkillsManifest.ALL.size() + " skill(s) to " + target.toAbsolutePath() + ":");
        int written = new SkillsDumper(target).dump();
        System.out.println();
        System.out.printf("Done. %d skill(s) written.%n", written);
    }

    private static void runServe(int port) {
        System.out.println("Starting Homing skills studio on port " + port + " …");
        System.out.println("Open  http://localhost:" + port + "/  in a browser.");
        System.out.println("Press Ctrl+C to stop.");
        SkillsStudioServer.start(port);
    }

    private static void printUsage(java.io.PrintStream out) {
        out.println("homing-skills — recipes for working with the Homing framework.");
        out.println();
        out.println("Args (passed via -Dexec.args from `mvn exec:java`):");
        out.println("  (no args)         Serve the mini-studio on port " + DEFAULT_PORT);
        out.println("  --port <N>        Serve on a specific port");
        out.println("  --dump            Dump SKILL.md files to ./.claude/skills/");
        out.println("  --dump <dir>      Dump SKILL.md files to <dir>");
        out.println("  --help            This message");
        out.println();
        out.println("Typical invocation from a host Maven project (with homing-skills as a dependency):");
        out.println("  mvn exec:java -Dexec.mainClass=hue.captains.singapura.js.homing.skills.SkillsCli \\");
        out.println("                -Dexec.args=\"--dump\"");
        out.println();
        out.println("Both modes read from the same single source of truth. See the");
        out.println("Dual-Audience Skills doctrine in the main Homing studio for the why.");
    }

    private SkillsCli() {}
}
