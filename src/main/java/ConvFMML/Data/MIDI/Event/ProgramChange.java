/*
 * Copyright © 2017-2019 Rerrah
 *
 * MIDI to MML Converter
 */

package ConvFMML.Data.MIDI.Event;


    public class ProgramChange extends MIDIEvent
    {
        public int Number { get; }

        public ProgramChange(int deltaTime, int channel, int number) {
    super(deltaTime, channel);
Number = number;
        }

        @Override protected  String GenerateString()
        {
            return $"{DeltaTime}:\tProgram Change\t{Channel}, {Number}";
        }
    }
}
