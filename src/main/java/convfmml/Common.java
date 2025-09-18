/*
 * Copyright © 2017-2019 Rerrah
 *
 * MIDI to MML Converter
 */

package convfmml;

import java.io.IOException;
import java.io.InputStream;
import java.util.jar.Attributes;
import javax.swing.ImageIcon;


public class Common {

    private static final Attributes mainManifestAttributes = getMainManifestAttributes();

    private static Attributes getMainManifestAttributes() {
        try (InputStream is = Common.class.getResourceAsStream("/META-INF/MANIFEST.MF")) {
            if (is != null) {
//                Manifest manifest = new Manifest(is);
//                return manifest.getMainAttributes();
            }
        } catch (IOException e) {
            // Log or handle exception
            e.printStackTrace();
        }
        // Return empty attributes if manifest not found or on error
        return new Attributes();
    }

    public static String getAssemblyTitle() {
        // Corresponds to "Implementation-Title" in MANIFEST.MF
        return mainManifestAttributes.getValue(Attributes.Name.IMPLEMENTATION_TITLE);
    }

    public static String getAssemblyDescription() {
        // No standard manifest entry for Description. Using a custom one.
        return mainManifestAttributes.getValue("Implementation-Description");
    }

    public static String getAssemblyCompany() {
        // Corresponds to "Implementation-Vendor" in MANIFEST.MF
        return mainManifestAttributes.getValue(Attributes.Name.IMPLEMENTATION_VENDOR);
    }

    public static String getAssemblyCopyright() {
        // No standard manifest entry for Copyright. Using a custom one.
        return mainManifestAttributes.getValue("Implementation-Copyright");
    }

    public static String getAssemblyFileVersion() {
        // Corresponds to "Implementation-Version" in MANIFEST.MF
        return mainManifestAttributes.getValue(Attributes.Name.IMPLEMENTATION_VERSION);
    }

    public static ImageIcon getIcon() {
        // In Java, icons are typically loaded as resources from the classpath.
        // Assuming you have an icon file (e.g., "icon.png") in your resources.
        java.net.URL iconURL = Common.class.getResource("/icon.png");
        if (iconURL != null) {
            return new ImageIcon(iconURL);
        }
        return null;
    }

    public enum MMLStyle {
        Custom,
        FMP7,
        FMP,
        PMD,
        MXDRV,
        NRTDRV,
        MUCOM88,
        Mml2vgm
    }

    public enum Key {
        CMaj, GMaj, DMaj, AMaj, EMaj, BMaj, FsMaj, CsMaj, FMaj, BfMaj, EfMaj, AfMaj, DfMaj, GfMaj, CfMaj,
        Amin, Emin, Bmin, Fsmin, Csmin, Gsmin, Dsmin, Asmin, Dmin, Gmin, Cmin, Fmin, Bfmin, Efmin, Afmin
    }

    public enum SoundModule {
        FM,
        SSG,
        FM3ch,
        Others
    }

    public enum MMLCommandRelation {
        Clear(0x00),
        TieBefore(0x01),
        TieAfter(0x02),
        PrevControl(0x04),
        NextControl(0x08);
        final int value;

        MMLCommandRelation(int value) {
            this.value = value;
        }
    }
}
