/*
 * Copyright © 2017-2019 Rerrah
 *
 * MIDI to MML Converter
 */

package convfmml.data.mml;

import java.util.List;

import convfmml.Common.SoundModule;


public class Part {

    private final List<Bar> barList;
    private final SoundModule soundModule;
    private final String name;

    public List<Bar> getBarList() {
        return barList;
    }

    public SoundModule getSoundModule() {
        return soundModule;
    }

    public String getName() {
        return name;
    }

    public int getLength() {
        return barList != null ? barList.size() : 0;
    }

    public Part(List<Bar> barList, SoundModule module, String name) {
        this.barList = barList;
        this.soundModule = module;
        this.name = name;
    }
}
