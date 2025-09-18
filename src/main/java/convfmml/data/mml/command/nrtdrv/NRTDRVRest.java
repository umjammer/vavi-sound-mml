/*
 * Copyright © 2017-2019 Rerrah
 *
 * MIDI to MML Converter
 */

package convfmml.data.mml.command.nrtdrv;

import java.util.EnumSet;
import java.util.List;

import convfmml.Common.MMLCommandRelation;
import convfmml.Common.SoundModule;
import convfmml.data.mml.command.Rest;
import convfmml.Settings;


public class NRTDRVRest extends Rest {

    public NRTDRVRest(List<Integer> length, EnumSet<MMLCommandRelation> relation) {
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
        str.append(len);

        int dotlen = 0;
        for (int i = 1; i < length.size(); i++) {
            if (len * 2 == length.get(i) && nrSettings.isDotEnable() &&
                    (nrSettings.getDotLength().intValue() == 0 || dotlen < nrSettings.getDotLength().intValue())) {
                str.append(".");
                dotlen++;
            } else {
                if (nrSettings.isUnuseTiedRest()) { // No Tie
                    str.append("r").append(length.get(i));
                } else {
                    if (nrSettings.getTieStyle() == 0) { // Tie and Name
                        str.append("&").append("r").append(length.get(i));
                    } else { // Tie only
                        str.append("^").append(length.get(i));
                    }
                }
                dotlen = 0;
            }
            len = length.get(i);
        }

        if (!nrSettings.isUnuseTiedRest() &&
                getCommandRelation().contains(MMLCommandRelation.TieAfter) &&
                !getCommandRelation().contains(MMLCommandRelation.NextControl)) {
            if (nrSettings.getTieStyle() == 0) {
                str.append("&");
            } else {
                str.append("^");
            }
        }

        return str.toString();
    }
}
