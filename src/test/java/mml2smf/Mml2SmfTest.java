/*
 * https://github.com/korinVR/Mml2Smf/blob/main/test/test.mjs
 */

package mml2smf;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import br.ufmg.dcc.nanocomp.peg.PEG;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;


// https://copilot.microsoft.com/chats/igTSsq45yhx7PZR6bWpsj
public class Mml2SmfTest {

    @BeforeAll
    static void setup() throws Exception {
        PEG peg = PEG.getInstance();
        Path pegPath = Paths.get(Mml2SmfTest.class.getResource("/mml.pegjs").toURI());
        String pegString = new String(Files.readAllBytes(pegPath));
        parser = peg.generate(pegString, Mml2Smf.Parser.class);
    }

    @SafeVarargs
    private static byte[] createSMF(int timebase, List<Integer>... trackDatas) {
        int format = trackDatas.length > 1 ? 1 : 0;
        List<Integer> smf = new ArrayList<>(List.of(0x4d, 0x54, 0x68, 0x64)); // "MThd"

        write4bytes(smf, 6); // header length
        write2bytes(smf, format);
        write2bytes(smf, trackDatas.length);
        write2bytes(smf, timebase);

        for (List<Integer> trackData : trackDatas) {
            smf.addAll(List.of(0x4d, 0x54, 0x72, 0x6b)); // "MTrk"
            write4bytes(smf, trackData.size());
            smf.addAll(trackData);
        }

        byte[] result = new byte[smf.size()];
        for (int i = 0; i < smf.size(); i++) {
            result[i] = (byte)(smf.get(i) & 0xFF);
        }
        return result;
    }

    private static void write2bytes(List<Integer> smf, int value) {
        smf.add((value >> 8) & 0xFF);
        smf.add(value & 0xFF);
    }

    private static void write4bytes(List<Integer> smf, int value) {
        smf.add((value >> 24) & 0xFF);
        smf.add((value >> 16) & 0xFF);
        smf.add((value >> 8) & 0xFF);
        smf.add(value & 0xFF);
    }

    private static List<Integer> hex2array(String hex) {
        List<Integer> array = new ArrayList<>();
        for (String byteStr : hex.split(" ")) {
            array.add(Integer.parseInt(byteStr, 16));
        }
        return array;
    }

    private static Mml2Smf.Parser parser; // Replace with your actual parser

    @Test
    void testMinimumSMF() throws Exception {
        String mml = "c";
        byte[] expected = createSMF(480, hex2array("00 90 3c 64 82 68 80 3c 00"));
        byte[] actual = Mml2Smf.convert(mml, 480, parser);
        assertArrayEquals(expected, actual);
    }

    @Test
    void testTempo() throws Exception {
        String mml = "t120";
        byte[] expected = createSMF(480, hex2array("00 ff 51 03 07 a1 20"));
        byte[] actual = Mml2Smf.convert(mml, 480, parser);
        assertArrayEquals(expected, actual);
    }

    @Test
    void testControlChange() throws Exception {
        String mml = "B10,20";
        byte[] expected = createSMF(480, hex2array("00 b0 0a 14"));
        byte[] actual = Mml2Smf.convert(mml, 480, parser);
        assertArrayEquals(expected, actual);
    }

    @Test
    void testMultitrack() throws Exception {
        String mml = "c;e;g";
        byte[] expected = createSMF(480,
                hex2array("00 90 3c 64 82 68 80 3c 00"),
                hex2array("00 91 40 64 82 68 81 40 00"),
                hex2array("00 92 43 64 82 68 82 43 00"));
        byte[] actual = Mml2Smf.convert(mml, 480, parser);
        assertArrayEquals(expected, actual);
    }

    @Test
    void testOctaveChange() throws Exception {
        String mml = "o5c";
        byte[] expected = createSMF(480, hex2array("00 90 48 64 82 68 80 48 00"));
        byte[] actual = Mml2Smf.convert(mml, 480, parser);
        assertArrayEquals(expected, actual);
    }
}
