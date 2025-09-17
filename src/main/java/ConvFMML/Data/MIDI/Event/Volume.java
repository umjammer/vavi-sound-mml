/*
 * Copyright © 2017-2019 Rerrah
 *
 * MIDI to MML Converter
 */

package ConvFMML.Data.MIDI.Event;


    public class Volume extends ControlChange
    {
        public Volume(int deltaTime, int channel, int value) {
 super(deltaTime, channel, value); }

        @Override protected  String GenerateString()
        {
            return $"{DeltaTime}:\tVolume\t{Channel}, {Value}";
        }
    }
}
