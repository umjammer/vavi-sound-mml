/*
 * Copyright © 2017-2019 Rerrah
 *
 * MIDI to MML Converter
 */

package convfmml.data.midi.event;


public class TimeSignature extends MetaEvent {

    private final int numerator;
    private final int denominatorBitShift;
    private final int midiClockPerMetronomeTick;
    private final int numberOfNotesPerClocks;

    public int getNumerator() {
        return numerator;
    }

    public int getDenominatorBitShift() {
        return denominatorBitShift;
    }

    public int getMIDIClockPerMetronomeTick() {
        return midiClockPerMetronomeTick;
    }

    public int getNumberOfNotesPerClocks() {
        return numberOfNotesPerClocks;
    }

    public TimeSignature(int deltaTime, int numerator, int denominatorBitShift, int midiClockPerMetronomeTick, int numberOfNotesPerClocks) {
        super(deltaTime);
        this.numerator = numerator;
        this.denominatorBitShift = denominatorBitShift;
        this.midiClockPerMetronomeTick = midiClockPerMetronomeTick;
        this.numberOfNotesPerClocks = numberOfNotesPerClocks;
    }

    @Override
    protected String generateString() {
        return "%d:\tTime Signature\t%d, %d, %d, %d".formatted(deltaTime, numerator, denominatorBitShift, midiClockPerMetronomeTick, numberOfNotesPerClocks);
    }
}
