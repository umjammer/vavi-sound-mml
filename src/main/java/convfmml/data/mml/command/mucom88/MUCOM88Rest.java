/*
 * Copyright © 2017-2019 Rerrah
 *
 * MIDI to MML Converter
 */

package convfmml.data.mml.command.mucom88;

import java.util.EnumSet;
import java.util.List;

import convfmml.Common.MMLCommandRelation;
import convfmml.Common.SoundModule;
import convfmml.data.mml.command.Rest;
import convfmml.Settings;


public class MUCOM88Rest extends Rest {

    public MUCOM88Rest(List<Integer> length, EnumSet<MMLCommandRelation> relation) {
        super(length, relation);
    }

    @Override
    protected String generateString(Settings settings, SoundModule module) {
        int len = length.getFirst();
        StringBuilder str = new StringBuilder();
        Settings.NoteRest nrSettings = settings.getNoteRest();

        if (nrSettings.isUnuseTiedRest() || nrSettings.getTieStyle() == 0 ||
                (!getCommandRelation().contains(MMLCommandRelation.TieBefore) ||
                        getCommandRelation().contains(MMLCommandRelation.PrevControl))) {
            str.append("r");
        }
        if (len != nrSettings.getDefaultLength().intValue())
            str.append(len);

        int dotlen = 0;
        for (int i = 1; i < length.size(); i++) {
            if (len * 2 == length.get(i) && nrSettings.isDotEnable() && dotlen < 1) {
                str.append(".");
                dotlen++;
            } else {
                if (nrSettings.isUnuseTiedRest() || nrSettings.getTieStyle() == 0) { // No Tie
                    if (length.get(i) != nrSettings.getDefaultLength().intValue())
                        str.append("r").append(length.get(i));
                    else
                        str.append("r");
                } else { // Tie only
                    str.append("^").append(length.get(i));
                }
                dotlen = 0;
            }
            len = length.get(i);
        }

        if (!nrSettings.isUnuseTiedRest() &&
                getCommandRelation().contains(MMLCommandRelation.TieAfter) &&
                !getCommandRelation().contains(MMLCommandRelation.NextControl)) {
            if (nrSettings.getTieStyle() == 1) {
                str.append("^");
            }
        }

        return str.toString();
    }
}
