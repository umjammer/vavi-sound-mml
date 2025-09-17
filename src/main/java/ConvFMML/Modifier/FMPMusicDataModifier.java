/*
 * Copyright © 2017-2019 Rerrah
 *
 * MIDI to MML Converter
 */

package ConvFMML.Modifier;


    public class FMPMusicDataModifier extends MusicDataModifier
    {
        @Override protected  LinkedList<Pan> ClonePanList(LinkedList<Pan> newList, LinkedList<Pan> srcList, Settings settings, SoundModule module)
        {
            if (settings.mmlExpression.ExtensionFMP == 2)
            {
                return super.ClonePanList(newList, srcList, settings, module);
            }
            else
            {
                return newList;
            }
        }

        @Override protected  ChangeEventSet CreateInstrumentSet(Instrument i)
        {
            return new ChangeEventSet(i, new FMPInstrument((i.Value + 1), MMLCommandRelation.Clear));
        }

        @Override protected  ChangeEventSet CreatePanSet(Pan p)
        {
            return new ChangeEventSet(p, new FMPPan(p.Value, MMLCommandRelation.Clear));
        }

        @Override protected  TempoSet CreateTempoSet(Tempo t)
        {
            return new TempoSet(t, new FMPTempo(t.Value, MMLCommandRelation.Clear));
        }

        @Override protected  ChangeEventSet CreateVolumeSet(Volume v)
        {
            return new ChangeEventSet(v, new FMPVolume(v.Value, MMLCommandRelation.Clear));
        }
    }
}
