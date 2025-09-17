/*
 * Copyright © 2017-2019 Rerrah
 *
 * MIDI to MML Converter
 */

package ConvFMML.Data.Intermediate;

import ConvFMML.Common.SoundModule;


public class NotesStatus implements Cloneable {

    private String name = "";
    private final int trackNumber;
    private final String trackName;
    private final int numberInTrack;
    private boolean printable = true;
    private final boolean isEmpty;
    private SoundModule soundModule = SoundModule.FM;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getTrackNumber() {
        return trackNumber;
    }

    public String getTrackName() {
        return trackName;
    }

    public int getNumberInTrack() {
        return numberInTrack;
    }

    public boolean isPrintable() {
        return printable;
    }

    public void setPrintable(boolean printable) {
        this.printable = printable;
    }

    public boolean isEmpty() {
        return isEmpty;
    }

    public SoundModule getSoundModule() {
        return soundModule;
    }

    public void setSoundModule(SoundModule soundModule) {
        this.soundModule = soundModule;
    }

    public NotesStatus(int trackNumber, String trackName, int numberInTrack, boolean isEmpty) {
        this.trackNumber = trackNumber;
        this.trackName = trackName;
        this.numberInTrack = numberInTrack;
        this.isEmpty = isEmpty;
    }

    @Override
    public NotesStatus clone() {
        return (NotesStatus) MemberwiseClone();
    }
}
