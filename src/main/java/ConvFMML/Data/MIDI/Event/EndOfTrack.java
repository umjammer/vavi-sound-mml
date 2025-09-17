/*
 * Copyright © 2017-2019 Rerrah
 *
 * MIDI to MML Converter
 */

package ConvFMML.Data.MIDI.Event;


    public class EndOfTrack extends MetaEvent
    {
        public EndOfTrack(int deltaTime) {
 super(deltaTime); }

        @Override protected  String GenerateString()
        {
            return $"{DeltaTime}:\tEnd of Track";
        }
    }
}
