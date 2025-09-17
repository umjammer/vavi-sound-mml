/*
 * Copyright © 2017-2019 Rerrah
 *
 * MIDI to MML Converter
 */

package ConvFMML.Data.MIDI.Event;


    public class Pan extends ControlChange
    {
        public Pan(int deltaTime, int channel, int value) {
 super(deltaTime, channel, value); }

        @Override protected  String GenerateString()
        {
            return $"{DeltaTime}:\tPan\t{Channel}, {Value}";
        }
    }
}
