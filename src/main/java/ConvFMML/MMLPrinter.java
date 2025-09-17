/*
 * Copyright © 2017-2019 Rerrah
 *
 * MIDI to MML Converter
 */

package ConvFMML;

import javax.sound.sampled.AudioFormat.Encoding;

import ConvFMML.Common.MMLCommandRelation;
import ConvFMML.Common.MMLStyle;
import ConvFMML.Common.SoundModule;
import ConvFMML.Data.MML.Bar;
import ConvFMML.Data.MML.Command.Command;
import ConvFMML.Data.MML.MML;
import ConvFMML.Data.MML.Part;
import dotnet4j.io.StreamWriter;


public class MMLPrinter {

    public void Print(MML mml, String path, Settings settings) {
        try {
            try (var sw = new StreamWriter(path, false, Encoding.GetEncoding(
                    (mml.Style == MMLStyle.Mml2vgm) ? "utf-8" : "shift_jis"))) {
                String str = GenerateHeader(mml, settings);
                sw.Write(str);


                for (Part part : mml.PartList) {
                    Note prevNote = null;
                    boolean isNewLineNote = false;
                    boolean needPartName = true;


                    for (int i = 0; i < part.Length; i++) {
                        Bar bar = part.BarList[i];
                        str = "";


                        if (bar.CommandList.size() > 0) {
                            // Print part name
                            if (needPartName) {
                                if (settings.outputPart.PrintStyle != 0) {
                                    str = part.Name + (settings.mmlExpression.UseTabAfterPartName ? "\t" : " ");
                                }
                                needPartName = false;

                                if (i == 0 &&
                                        ((mml.PartList.indexOf(part) == 0 &&
                                                (mml.Style == MMLStyle.FMP && settings.mmlExpression.PrintTimeBase == 1) ||
                                                (mml.Style == MMLStyle.PMD && settings.mmlExpression.PrintTimeBasePMD == 2))
                                                || (mml.Style == MMLStyle.MUCOM88 && settings.mmlExpression.PrintTimeBase == 1))) {
                                    str += "C" + mml.size() sPerWholeNote + " ";
                                }
                            }


                            // Print commands a bar
                            for (Command c : bar.CommandList) {
                                if (c instanceof Note) {
                                    var note = (Note) c;
                                    if (prevNote == null) {
                                        if (mml.Style == MMLStyle.Custom) {
                                            str += settings.noteRest.OctaveCommandCustom + note.Octave + " ";
                                        } else {
                                            str += "o" + note.Octave + " ";
                                        }
                                    } else {
                                        if (!isNewLineNote && settings.noteRest.OctaveInNewLine == 1) {
                                            if (mml.Style == MMLStyle.Custom) {
                                                str += settings.noteRest.OctaveCommandCustom + note.Octave + " ";
                                            } else {
                                                str += "o" + note.Octave + " ";
                                            }
                                        } else {
                                            int dif = note.Octave - prevNote.Octave;
                                            if (dif > 0) {
                                                for (int j = 0; j < dif; j++) {
                                                    switch (mml.Style) {
                                                        case MMLStyle.Custom:
                                                        case MMLStyle.MXDRV:
                                                        case MMLStyle.NRTDRV:
                                                        case MMLStyle.PMD:
                                                        case MMLStyle.Mml2vgm:
                                                            str += ((settings.noteRest.OctaveDirection == 0) ? ">" : "<");
                                                            break;
                                                        default:
                                                            str += ">";
                                                            break;
                                                    }
                                                }
                                            } else if (dif < 0) {
                                                for (int j = 0; j > dif; j--) {
                                                    switch (mml.Style) {
                                                        case MMLStyle.Custom:
                                                        case MMLStyle.MXDRV:
                                                        case MMLStyle.NRTDRV:
                                                        case MMLStyle.PMD:
                                                        case MMLStyle.Mml2vgm:
                                                            str += ((settings.noteRest.OctaveDirection == 0) ? "<" : ">");
                                                            break;
                                                        default:
                                                            str += "<";
                                                            break;
                                                    }
                                                }
                                            }
                                        }
                                        isNewLineNote = true;
                                    }
                                    prevNote = note;
                                }


                                str += c.ToString(settings, part.SoundModule);


                                if (bar.CommandList.Last.Value != c) {
                                    if (c.CommandRelation.HasFlag(MMLCommandRelation.NextControl)) {
                                        str += " ";
                                    } else if (c instanceof ControlCommand && !c.CommandRelation.HasFlag(MMLCommandRelation.NextControl)) {
                                        str += " ";
                                    }
                                }
                            }
                        }


                        if (bar == part.BarList.Last()) {
                            if (!needPartName) {
                                str += System.lineSeparator();
                            }
                            str += System.lineSeparator();
                        } else {
                            if (!needPartName) {
                                str += bar.SeperateSign;
                                if (bar.SeperateSign == System.lineSeparator()) {
                                    isNewLineNote = false;
                                    needPartName = true;
                                }
                            }
                        }


                        sw.Write(str);
                    }
                }

            }
        } catch (Exception ex) {
            throw new Exception(string.Format(Resources.ErrorMMLFailed, path), ex);
        }
    }

    private String GenerateHeader(MML mml, Settings settings) {
        String str = "";
        boolean flag = false;

        switch (mml.Style) {
            case MMLStyle.FMP7:
                if (settings.mmlExpression.TitleEnable == 1 && mml.Title.Length > 0) {
                    str += " Title=" + mml.Title + System.lineSeparator();
                    flag = true;
                }
                if (settings.mmlExpression.PrintTimeBase == 1) {
                    str += " ClockCount=" + mml.size() sPerWholeNote + System.lineSeparator();
                    flag = true;
                }
                if (settings.outputPart.PrintStyle != 0) {
                    var lookUp = mml.PartList.ToLookup(x -> x.SoundModule, x -> x.Name);
                    if (lookUp[SoundModule.FM].size() () > 0)
                    {
                        String stemp = "";
                        for (String s : lookUp[SoundModule.FM]) {
                            if (!string.IsNullOrEmpty(s)) {
                                stemp += s.Substring(1);
                            }
                        }
                        if (stemp.size() () > 0)
                        {
                            str = str + " PartOPNA=" + stemp + System.lineSeparator();
                            flag = true;
                        }
                    }
                    if (lookUp[SoundModule.SSG].size() () > 0)
                    {
                        String stemp = "";
                        for (String s : lookUp[SoundModule.SSG]) {
                            if (!string.IsNullOrEmpty(s)) {
                                stemp += s.Substring(1);
                            }
                        }
                        if (stemp.size() () > 0)
                        {
                            str = str + " PartSSG=" + stemp + System.lineSeparator();
                            flag = true;
                        }
                    }
                }
                if (flag) {
                    str = "\'{" + System.lineSeparator() +
                            str +
                            "}" + System.lineSeparator();
                }
                break;
            case MMLStyle.MXDRV:
                if (settings.mmlExpression.TitleEnable == 1 && mml.Title.Length > 0) {
                    str += "#title\t\"" + mml.Title + "\"" + System.lineSeparator();
                    flag = true;
                }
                if (settings.noteRest.OctaveDirection == 1) {
                    str = "#OCTAVE-REV" + System.lineSeparator();
                    flag = true;
                }
                break;
            case MMLStyle.NRTDRV:
                if (settings.mmlExpression.TitleEnable == 1 && mml.Title.Length > 0) {
                    str += "#TITLE\t" + mml.Title + System.lineSeparator();
                    flag = true;
                }
                if (settings.mmlExpression.PrintTimeBase == 1) {
                    str += "#COUNT\t" + mml.size() sPerWholeNote + System.lineSeparator();
                    flag = true;
                }
                if (settings.noteRest.OctaveDirection == 1) {
                    str = "#OCTAVE_REV" + System.lineSeparator();
                    flag = true;
                }
                if (settings.controlCommand.volume.Enable && settings.controlCommand.volume.CommandNRTDRV == 0) {
                    str += "#V_STEP\t" + settings.controlCommand.volume.VStep + System.lineSeparator();
                    flag = true;
                }
                break;
            case MMLStyle.PMD:
                if (settings.mmlExpression.TitleEnable == 1 && mml.Title.Length > 0) {
                    str += "#Title\t" + mml.Title + System.lineSeparator();
                    flag = true;
                }
                if (settings.mmlExpression.PrintTimeBasePMD == 1) {
                    str += "#Zenlen\t" + mml.size() sPerWholeNote + System.lineSeparator();
                    flag = true;
                }
                if (settings.outputPart.PrintStyle == 1 ||
                        (settings.outputPart.PrintStyle == 2 && settings.outputPart.AutoNamePMD == 2)) {
                    var lookUp = mml.PartList.ToLookup(x -> x.SoundModule, x -> x.Name);
                    if (lookUp[SoundModule.FM3ch].size() () > 0)
                    {
                        String stemp = "";
                        lookUp[SoundModule.FM3ch].ToList().ForEach(x -> stemp += x);
                        if (stemp.size() () > 0)
                        {
                            str = str + "#FM3Extend\t" + stemp + System.lineSeparator();
                            flag = true;
                        }
                    }
                }
                if (settings.noteRest.OctaveDirection == 1) {
                    str += "#Octave\tReverse" + System.lineSeparator();
                    flag = true;
                }
                break;
            case MMLStyle.MUCOM88:
                if (settings.mmlExpression.TitleEnable == 1 && mml.Title.Length > 0) {
                    str += "#title\t" + mml.Title + System.lineSeparator();
                    flag = true;
                }
                break;
            case MMLStyle.Mml2vgm:
                if (settings.mmlExpression.TitleEnable == 1 && mml.Title.Length > 0) {
                    str += " TitleName=" + mml.Title + System.lineSeparator();
                    flag = true;
                }
                if (settings.mmlExpression.PrintTimeBase == 1) {
                    str += " ClockCount=" + mml.size() sPerWholeNote + System.lineSeparator();
                    flag = true;
                }
                if (flag) {
                    str = "\'{" + System.lineSeparator() +
                            str +
                            "}" + System.lineSeparator();
                }
                break;
            default:
                break;
        }

        if (flag) {
            str += System.lineSeparator();
        }

        return str;
    }
}
