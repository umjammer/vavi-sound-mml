/*
 * Copyright © 2017-2019 Rerrah
 *
 * MIDI to MML Converter
 */

package convfmml;

import java.nio.charset.Charset;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import convfmml.Common.MMLCommandRelation;
import convfmml.Common.MMLStyle;
import convfmml.Common.SoundModule;
import convfmml.data.mml.Bar;
import convfmml.data.mml.command.Command;
import convfmml.data.mml.command.ControlCommand;
import convfmml.data.mml.command.Note;
import convfmml.data.mml.MML;
import convfmml.data.mml.Part;
import dotnet4j.io.FileMode;
import dotnet4j.io.FileStream;
import dotnet4j.io.StreamWriter;
import dotnet4j.util.compat.StringUtilities;


public class MMLPrinter {

    static final ResourceBundle rb = ResourceBundle.getBundle("messages");

    public void print(MML mml, String path, Settings settings) {
        try {
            try (var st = new FileStream(path, FileMode.Create);
                 var sw = new StreamWriter(st, Charset.forName
                         (mml.getStyle() == MMLStyle.Mml2vgm ? "utf-8" : "shift_jis"))) {
                StringBuilder str = new StringBuilder(generateHeader(mml, settings));
                sw.write(str.toString());


                for (Part part : mml.getPartList()) {
                    Note prevNote = null;
                    boolean isNewLineNote = false;
                    boolean needPartName = true;


                    for (int i = 0; i < part.getLength(); i++) {
                        Bar bar = part.getBarList().get(i);
                        str = new StringBuilder();


                        if (!bar.getCommandList().isEmpty()) {
                            // Print part name
                            if (needPartName) {
                                if (settings.getOutputPart().getPrintStyle() != 0) {
                                    str = new StringBuilder(part.getName() + (settings.getMmlExpression().isUseTabAfterPartName() ? "\t" : " "));
                                }
                                needPartName = false;

                                if (i == 0 &&
                                        ((mml.getPartList().indexOf(part) == 0 &&
                                                (mml.getStyle() == MMLStyle.FMP && settings.getMmlExpression().getPrintTimeBase() == 1) ||
                                                (mml.getStyle() == MMLStyle.PMD && settings.getMmlExpression().getPrintTimeBasePMD() == 2))
                                                || (mml.getStyle() == MMLStyle.MUCOM88 && settings.getMmlExpression().getPrintTimeBase() == 1))) {
                                    str.append("C").append(mml.getCountsPerWholeNote()).append(" ");
                                }
                            }


                            // Print commands a bar
                            for (Command c : bar.getCommandList()) {
                                if (c instanceof Note note) {
                                    if (prevNote == null) {
                                        if (mml.getStyle() == MMLStyle.Custom) {
                                            str.append(settings.getNoteRest().getOctaveCommandCustom()).append(note.getOctave()).append(" ");
                                        } else {
                                            str.append("o").append(note.getOctave()).append(" ");
                                        }
                                    } else {
                                        if (!isNewLineNote && settings.getNoteRest().getOctaveInNewLine() == 1) {
                                            if (mml.getStyle() == MMLStyle.Custom) {
                                                str.append(settings.getNoteRest().getOctaveCommandCustom()).append(note.getOctave()).append(" ");
                                            } else {
                                                str.append("o").append(note.getOctave()).append(" ");
                                            }
                                        } else {
                                            int dif = note.getOctave() - prevNote.getOctave();
                                            if (dif > 0) {
                                                for (int j = 0; j < dif; j++) {
                                                    switch (mml.getStyle()) {
                                                        case Custom:
                                                        case MXDRV:
                                                        case NRTDRV:
                                                        case PMD:
                                                        case Mml2vgm:
                                                            str.append((settings.getNoteRest().getOctaveDirection() == 0) ? ">" : "<");
                                                            break;
                                                        default:
                                                            str.append(">");
                                                            break;
                                                    }
                                                }
                                            } else if (dif < 0) {
                                                for (int j = 0; j > dif; j--) {
                                                    switch (mml.getStyle()) {
                                                        case Custom:
                                                        case MXDRV:
                                                        case NRTDRV:
                                                        case PMD:
                                                        case Mml2vgm:
                                                            str.append((settings.getNoteRest().getOctaveDirection() == 0) ? "<" : ">");
                                                            break;
                                                        default:
                                                            str.append("<");
                                                            break;
                                                    }
                                                }
                                            }
                                        }
                                        isNewLineNote = true;
                                    }
                                    prevNote = note;
                                }


                                str.append(c.toString(settings, part.getSoundModule()));


                                if (bar.getCommandList().getLast() != c) {
                                    if (c.getCommandRelation().contains(MMLCommandRelation.NextControl)) {
                                        str.append(" ");
                                    } else if (c instanceof ControlCommand && !c.getCommandRelation().contains(MMLCommandRelation.NextControl)) {
                                        str.append(" ");
                                    }
                                }
                            }
                        }


                        if (bar == part.getBarList().getLast()) {
                            if (!needPartName) {
                                str.append(System.lineSeparator());
                            }
                            str.append(System.lineSeparator());
                        } else {
                            if (!needPartName) {
                                str.append(bar.getSeparateSign());
                                if (bar.getSeparateSign().equals(System.lineSeparator())) {
                                    isNewLineNote = false;
                                    needPartName = true;
                                }
                            }
                        }


                        sw.write(str.toString());
                    }
                }

            }
        } catch (Exception ex) {
            throw new IllegalArgumentException(String.format(rb.getString("ErrorMMLFailed"), path), ex);
        }
    }

    private static String generateHeader(MML mml, Settings settings) {
        String str = "";
        boolean flag = false;

        switch (mml.getStyle()) {
            case MMLStyle.FMP7:
                if (settings.getMmlExpression().getTitleEnable() == 1 && !mml.getTitle().isEmpty()) {
                    str += " Title=" + mml.getTitle() + System.lineSeparator();
                    flag = true;
                }
                if (settings.getMmlExpression().getPrintTimeBase() == 1) {
                    str += " ClockCount=" + mml.getCountsPerWholeNote() + System.lineSeparator();
                    flag = true;
                }
                if (settings.getOutputPart().getPrintStyle() != 0) {
                    var lookUp = mml.getPartList().stream().collect(Collectors.groupingBy(
                            Part::getSoundModule, Collectors.mapping(Part::getName, Collectors.toList())));
                    if (lookUp.containsKey(SoundModule.FM) && !lookUp.get(SoundModule.FM).isEmpty()) {
                        StringBuilder stemp = new StringBuilder();
                        for (String s : lookUp.get(SoundModule.FM)) {
                            if (!StringUtilities.isNullOrEmpty(s)) {
                                stemp.append(s.substring(1));
                            }
                        }
                        if (!stemp.isEmpty()) {
                            str = str + " PartOPNA=" + stemp + System.lineSeparator();
                            flag = true;
                        }
                    }
                    if (!lookUp.get(SoundModule.SSG).isEmpty()) {
                        StringBuilder stemp = new StringBuilder();
                        for (String s : lookUp.get(SoundModule.SSG)) {
                            if (!StringUtilities.isNullOrEmpty(s)) {
                                stemp.append(s.substring(1));
                            }
                        }
                        if (!stemp.toString().isEmpty()) {
                            str = str + " PartSSG=" + stemp + System.lineSeparator();
                            flag = true;
                        }
                    }
                }
                if (flag) {
                    str = "'{" + System.lineSeparator() +
                            str +
                            "}" + System.lineSeparator();
                }
                break;
            case MMLStyle.MXDRV:
                if (settings.getMmlExpression().getTitleEnable() == 1 && !mml.getTitle().isEmpty()) {
                    str += "#title\t\"" + mml.getTitle() + "\"" + System.lineSeparator();
                    flag = true;
                }
                if (settings.getNoteRest().getOctaveDirection() == 1) {
                    str = "#OCTAVE-REV" + System.lineSeparator();
                    flag = true;
                }
                break;
            case MMLStyle.NRTDRV:
                if (settings.getMmlExpression().getTitleEnable() == 1 && !mml.getTitle().isEmpty()) {
                    str += "#TITLE\t" + mml.getTitle() + System.lineSeparator();
                    flag = true;
                }
                if (settings.getMmlExpression().getPrintTimeBase() == 1) {
                    str += "#COUNT\t" + mml.getCountsPerWholeNote() + System.lineSeparator();
                    flag = true;
                }
                if (settings.getNoteRest().getOctaveDirection() == 1) {
                    str = "#OCTAVE_REV" + System.lineSeparator();
                    flag = true;
                }
                if (settings.getControlCommand().getVolume().isEnable() && settings.getControlCommand().getVolume().getCommandNRTDRV() == 0) {
                    str += "#V_STEP\t" + settings.getControlCommand().getVolume().getVStep() + System.lineSeparator();
                    flag = true;
                }
                break;
            case MMLStyle.PMD:
                if (settings.getMmlExpression().getTitleEnable() == 1 && !mml.getTitle().isEmpty()) {
                    str += "#Title\t" + mml.getTitle() + System.lineSeparator();
                    flag = true;
                }
                if (settings.getMmlExpression().getPrintTimeBasePMD() == 1) {
                    str += "#Zenlen\t" + mml.getCountsPerWholeNote() + System.lineSeparator();
                    flag = true;
                }
                if (settings.getOutputPart().getPrintStyle() == 1 ||
                        (settings.getOutputPart().getPrintStyle() == 2 && settings.getOutputPart().getAutoNamePMD() == 2)) {
                    var lookUp = mml.getPartList().stream().collect(Collectors.groupingBy(
                            Part::getSoundModule, Collectors.mapping(Part::getName, Collectors.toList())));
                    if (!lookUp.get(SoundModule.FM3ch).isEmpty()) {
                        StringBuilder stemp = new StringBuilder();
                        lookUp.get(SoundModule.FM3ch).forEach(stemp::append);
                        if (!stemp.isEmpty()) {
                            str = str + "#FM3Extend\t" + stemp + System.lineSeparator();
                            flag = true;
                        }
                    }
                }
                if (settings.getNoteRest().getOctaveDirection() == 1) {
                    str += "#Octave\tReverse" + System.lineSeparator();
                    flag = true;
                }
                break;
            case MMLStyle.MUCOM88:
                if (settings.getMmlExpression().getTitleEnable() == 1 && !mml.getTitle().isEmpty()) {
                    str += "#title\t" + mml.getTitle() + System.lineSeparator();
                    flag = true;
                }
                break;
            case MMLStyle.Mml2vgm:
                if (settings.getMmlExpression().getTitleEnable() == 1 && !mml.getTitle().isEmpty()) {
                    str += " TitleName=" + mml.getTitle() + System.lineSeparator();
                    flag = true;
                }
                if (settings.getMmlExpression().getPrintTimeBase() == 1) {
                    str += " ClockCount=" + mml.getCountsPerWholeNote() + System.lineSeparator();
                    flag = true;
                }
                if (flag) {
                    str = "'{" + System.lineSeparator() +
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
