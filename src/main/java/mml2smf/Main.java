/*
 * https://github.com/korinVR/mml2smf/blob/main/src/main.mjs
 *
 * https://github.com/korinVR/mml2smf/blob/main/LICENSE
 */

package mml2smf;

import java.io.IOException;
import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

import br.ufmg.dcc.nanocomp.peg.PEG;
import mml2smf.Mml2Smf.MMLParseException;

import static java.lang.System.getLogger;


/**
 * Main.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (nsano)
 * @version 0.00 2025-09-20 nsano initial version <br>
 */
public class Main {

    private static final Logger logger = getLogger(Main.class.getName());

    public static void main(String[] args) throws Exception {
        Map<String, String> argv = parseArgs(args);

        String smfFile = "output.mid";
        int timebase = 480;
        String mml = null;

        if (argv.containsKey("m")) {
            mml = argv.get("m");
        } else {
            String mmlFile = argv.get("_");
            if (mmlFile != null) {
                try {
                    mml = Files.readString(Paths.get(mmlFile));
                } catch (IOException e) {
                    logger.log(Level.ERROR, e.getMessage(), e);
                    System.out.println("error: " + mmlFile + " not found");
                    System.exit(1);
                }
                smfFile = changeExtension(mmlFile, "mid");
            }
        }

        if (argv.containsKey("o")) {
            smfFile = argv.get("o");
        }

        if (argv.containsKey("timebase")) {
            try {
                timebase = Integer.parseInt(argv.get("timebase"));
            } catch (NumberFormatException e) {
                System.out.println("error: invalid timebase");
                System.exit(1);
            }
        }

        if (mml == null) {
            System.out.println("""
                mml2smf version 0.3.0 - MML to Standard MIDI File converter

                usage:
                    mml2smf [MML file]
                    mml2smf [MML file] -o [.mid file]
                    mml2smf -m [MML] -o [.mid file]
                options:
                    --timebase [timebase] (default=480)
                """);
            System.exit(1);
        }

        PEG peg = PEG.getInstance();
        Path pegPath = Paths.get(Main.class.getResource("/mml.pegjs").toURI());
        String pegString = new String(Files.readAllBytes(pegPath));
        Mml2Smf.Parser parser = peg.generate(pegString, Mml2Smf.Parser.class);

        byte[] smf;
        try {
            smf = Mml2Smf.convert(mml, timebase, parser); // You must implement this
        } catch (MMLParseException e) {
//            System.out.println("error: line " + e.getLine() + " column " + e.getColumn() + ": " + e.getMessage());
            System.err.println(e.getMessage());
            System.exit(1);
            return;
        }

        try {
            Files.write(Paths.get(smfFile), smf);
        } catch (IOException e) {
            System.out.println("error: failed to write " + smfFile);
            System.exit(1);
        }
    }

    private static String changeExtension(String filename, String ext) {
        int n = filename.lastIndexOf('.');
        if (n < 0) n = filename.length();
        return filename.substring(0, n) + "." + ext;
    }

    private static Map<String, String> parseArgs(String[] args) {
        // Simple parser: --key=value or -k value
        var map = new java.util.HashMap<String, String>();
        for (int i = 0; i < args.length; i++) {
            String arg = args[i];
            if (arg.startsWith("--")) {
                String[] parts = arg.substring(2).split("=", 2);
                map.put(parts[0], parts.length > 1 ? parts[1] : args[++i]);
            } else if (arg.startsWith("-")) {
                String key = arg.substring(1);
                map.put(key, args[++i]);
            } else {
                map.put("_", arg); // positional MML file
            }
        }
        return map;
    }
}
