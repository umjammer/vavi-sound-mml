/*
 * Copyright © 2017-2019 Rerrah
 *
 * MIDI to MML Converter
 */

package convfmml.data.midi.event;


public class MIDIEvent extends Event {

    protected int channel;

    public int getChannel() {
        return channel;
    }

    public MIDIEvent(int deltaTime, int channel) {
        super(deltaTime);
        this.channel = channel;
    }

    @Override
    protected String generateString() {
        return "%d:\tOther MIDI Event\t%d".formatted(deltaTime, channel);
    }
}
