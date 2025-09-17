/*
 * Copyright © 2017-2019 Rerrah
 *
 * MIDI to MML Converter
 */

package ConvFMML.Data.MIDI.Event;


    public class MIDIEvent extends Event
    {
        public int Channel { get; }

        public MIDIEvent(int deltaTime, int channel) {
    super(deltaTime);
Channel = channel;
        }

        @Override protected  String GenerateString()
        {
            return $"{DeltaTime}:\tOther MIDI Event\t{Channel}";
        }
    }
}
