/*
 * Copyright © 2017-2019 Rerrah
 *
 * MIDI to MML Converter
 */

package ConvFMML;

import java.util.HashMap;
import java.util.Map;

import ConvFMML.Common.Key;


public class Tables {

    public static final Key[][] KeyTable = {
            {
                    Key.CfMaj, Common.Key.GfMaj, Common.Key.DfMaj, Common.Key.AfMaj,
                    Common.Key.EfMaj, Common.Key.BfMaj, Common.Key.FMaj, Common.Key.CMaj,
                    Common.Key.GMaj, Common.Key.DMaj, Common.Key.AMaj, Common.Key.EMaj,
                    Common.Key.BMaj, Common.Key.FsMaj, Common.Key.CsMaj
            },
            {
                    Key.Afmin, Common.Key.Efmin, Common.Key.Bfmin, Common.Key.Fmin,
                    Common.Key.Cmin, Common.Key.Gmin, Common.Key.Dmin, Key.Amin,
                    Key.Emin, Key.Bmin, Key.Fsmin, Key.Csmin,
                    Key.Gsmin, Key.Dsmin, Key.Asmin
            }
    };

    public static final Map<Key, String[]> NoteNameDictionary;

    static {
        NoteNameDictionary = new HashMap<>();
        NoteNameDictionary.put(Key.CMaj, new String[] {"c", "c+", "d", "d+", "e", "f", "f+", "g", "g+", "a", "a+", "b"});
        NoteNameDictionary.put(Key.GMaj, new String[] {"c", "c+", "d", "d+", "e", "f", "f+", "g", "g+", "a", "a+", "b"});
        NoteNameDictionary.put(Key.DMaj, new String[] {"c", "c+", "d", "d+", "e", "f", "f+", "g", "g+", "a", "a+", "b"});
        NoteNameDictionary.put(Key.AMaj, new String[] {"c", "c+", "d", "d+", "e", "f", "f+", "g", "g+", "a", "a+", "b"});
        NoteNameDictionary.put(Key.EMaj, new String[] {"c", "c+", "d", "d+", "e", "f", "f+", "g", "g+", "a", "a+", "b"});
        NoteNameDictionary.put(Key.BMaj, new String[] {"c", "c+", "d", "d+", "e", "f", "f+", "g", "g+", "a", "a+", "b"});
        NoteNameDictionary.put(Key.FsMaj, new String[] {"c", "c+", "d", "d+", "e", "e+", "f+", "g", "g+", "a", "a+", "b"});
        NoteNameDictionary.put(Key.CsMaj, new String[] {"b+", "c+", "d", "d+", "e", "e+", "f+", "g", "g+", "a", "a+", "b"});
        NoteNameDictionary.put(Key.FMaj, new String[] {"c", "c+", "d", "d+", "e", "f", "f+", "g", "g+", "a", "b-", "b"});
        NoteNameDictionary.put(Key.BfMaj, new String[] {"c", "c+", "d", "e-", "e", "f", "f+", "g", "g+", "a", "b-", "b"});
        NoteNameDictionary.put(Key.EfMaj, new String[] {"c", "c+", "d", "e-", "e", "f", "f+", "g", "a-", "a", "b-", "b"});
        NoteNameDictionary.put(Key.AfMaj, new String[] {"c", "d-", "d", "e-", "e", "f", "f+", "g", "a-", "a", "b-", "b"});
        NoteNameDictionary.put(Key.DfMaj, new String[] {"c", "d-", "d", "e-", "e", "f", "g-", "g", "a-", "a", "b-", "b"});
        NoteNameDictionary.put(Key.GfMaj, new String[] {"c", "d-", "d", "e-", "e", "f", "g-", "g", "a-", "a", "b-", "c-"});
        NoteNameDictionary.put(Key.CfMaj, new String[] {"c", "d-", "d", "e-", "f-", "f", "g-", "g", "a-", "a", "b-", "c-"});
        NoteNameDictionary.put(Key.Amin, new String[] {"c", "c+", "d", "d+", "e", "f", "f+", "g", "g+", "a", "a+", "b"});
        NoteNameDictionary.put(Key.Emin, new String[] {"c", "c+", "d", "d+", "e", "f", "f+", "g", "g+", "a", "a+", "b"});
        NoteNameDictionary.put(Key.Bmin, new String[] {"c", "c+", "d", "d+", "e", "f", "f+", "g", "g+", "a", "a+", "b"});
        NoteNameDictionary.put(Key.Fsmin, new String[] {"c", "c+", "d", "d+", "e", "f", "f+", "g", "g+", "a", "a+", "b"});
        NoteNameDictionary.put(Key.Csmin, new String[] {"c", "c+", "d", "d+", "e", "f", "f+", "g", "g+", "a", "a+", "b"});
        NoteNameDictionary.put(Key.Gsmin, new String[] {"c", "c+", "d", "d+", "e", "f", "f+", "g", "g+", "a", "a+", "b"});
        NoteNameDictionary.put(Key.Dsmin, new String[] {"c", "c+", "d", "d+", "e", "e+", "f+", "g", "g+", "a", "a+", "b"});
        NoteNameDictionary.put(Key.Asmin, new String[] {"b+", "c+", "d", "d+", "e", "e+", "f+", "g", "g+", "a", "a+", "b"});
        NoteNameDictionary.put(Key.Dmin, new String[] {"c", "c+", "d", "d+", "e", "f", "f+", "g", "g+", "a", "b-", "b"});
        NoteNameDictionary.put(Key.Gmin, new String[] {"c", "c+", "d", "e-", "e", "f", "f+", "g", "g+", "a", "a+", "b"});
        NoteNameDictionary.put(Key.Cmin, new String[] {"c", "c+", "d", "e-", "e", "f", "f+", "g", "a-", "a", "a+", "b"});
        NoteNameDictionary.put(Key.Fmin, new String[] {"c", "d-", "d", "e-", "e", "f", "f+", "g", "a-", "a", "a+", "b"});
        NoteNameDictionary.put(Key.Bfmin, new String[] {"c", "d-", "d", "e-", "e", "f", "g-", "g", "a-", "a", "a+", "b"});
        NoteNameDictionary.put(Key.Efmin, new String[] {"c", "d-", "d", "e-", "e", "f", "g-", "g", "a-", "a", "a+", "c-"});
        NoteNameDictionary.put(Key.Afmin, new String[] {"c", "d-", "d", "e-", "f-", "f", "g-", "g", "a-", "a", "a+", "c-"});
    }
}
