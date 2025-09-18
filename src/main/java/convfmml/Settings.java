/*
 * Copyright © 2017-2019 Rerrah
 *
 * MIDI to MML Converter
 */

package convfmml;

import convfmml.Common.MMLStyle;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import vavi.util.serdes.JacksonXMLBeanBinder;
import vavi.util.serdes.Serdes;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;


@Serdes(beanBinder = JacksonXMLBeanBinder.class)
public class Settings implements Serializable {
    private MMLExpression mmlExpression = new MMLExpression();
    private NoteRest noteRest = new NoteRest();
    private ControlCommand controlCommand = new ControlCommand();
    private OutputPart outputPart = new OutputPart();

    // Getters and Setters
    public MMLExpression getMmlExpression() { return mmlExpression; }
    public void setMmlExpression(MMLExpression mmlExpression) { this.mmlExpression = mmlExpression; }
    public NoteRest getNoteRest() { return noteRest; }
    public void setNoteRest(NoteRest noteRest) { this.noteRest = noteRest; }
    public ControlCommand getControlCommand() { return controlCommand; }
    public void setControlCommand(ControlCommand controlCommand) { this.controlCommand = controlCommand; }
    public OutputPart getOutputPart() { return outputPart; }
    public void setOutputPart(OutputPart outputPart) { this.outputPart = outputPart; }

    static Path config = Path.of(System.getProperty("user.home"), ".config/ConvFMML/Settings.xml");

    public static Settings load() throws IOException {
        Settings settings = new Settings();
        if (Files.exists(config))
            Serdes.Util.deserialize(Files.newInputStream(config), settings);
        return settings;
    }

    public void save() throws IOException {
        if (!Files.exists(config.getParent())) Files.createDirectories(config.getParent());
        Serdes.Util.serialize(this, Files.newOutputStream(config));
    }

    @Serdes(beanBinder = JacksonXMLBeanBinder.class)
    public static class MMLExpression implements Serializable {
        @JacksonXmlProperty(localName = "MMLStyle")
        private MMLStyle mmlStyle = MMLStyle.FMP7;
        @JacksonXmlProperty(localName = "ExtensionCustom")
        private String extensionCustom = ".mml";
        @JacksonXmlProperty(localName = "ExtensionFMP")
        private int extensionFMP = 1;
        @JacksonXmlProperty(localName = "TimeBase")
        private BigDecimal timeBase = new BigDecimal("192");
        @JacksonXmlProperty(localName = "PrintTimeBase")
        private int printTimeBase = 1;
        @JacksonXmlProperty(localName = "PrintTimeBasePMD")
        private int printTimeBasePMD = 1;
        @JacksonXmlProperty(localName = "NewBlockByBar")
        private int newBlockByBar = 1;
        @JacksonXmlProperty(localName = "NewLineBarCount")
        private BigDecimal newLineBarCount = new BigDecimal("2");
        @JacksonXmlProperty(localName = "NewLineByTimeSignature")
        private int newLineByTimeSignature = 1;
        @JacksonXmlProperty(localName = "TitleEnable")
        private int titleEnable = 1;
        @JacksonXmlProperty(localName = "UseTabAfterPartName")
        private boolean useTabAfterPartName = false;

        public String getExtension() {
            switch (mmlStyle) {
                case Custom:
                    return extensionCustom;
                case FMP7:
                    return ".mwi";
                case FMP:
                    switch (extensionFMP) {
                        case 1: return ".mpi";
                        case 2: return ".mvi";
                        case 3: return ".mzi";
                        default: return null;
                    }
                case MXDRV:
                    return ".mus";
                case PMD:
                case NRTDRV:
                    return ".mml";
                case MUCOM88:
                    return ".muc";
                case Mml2vgm:
                    return ".gwi";
                default:
                    return null;
            }
        }

        // Getters and Setters
        public MMLStyle getMmlStyle() { return mmlStyle; }
        public void setMmlStyle(MMLStyle mmlStyle) { this.mmlStyle = mmlStyle; }
        public String getExtensionCustom() { return extensionCustom; }
        public void setExtensionCustom(String extensionCustom) { this.extensionCustom = extensionCustom; }
        public int getExtensionFMP() { return extensionFMP; }
        public void setExtensionFMP(int extensionFMP) { this.extensionFMP = extensionFMP; }
        public BigDecimal getTimeBase() { return timeBase; }
        public void setTimeBase(BigDecimal timeBase) { this.timeBase = timeBase; }
        public int getPrintTimeBase() { return printTimeBase; }
        public void setPrintTimeBase(int printTimeBase) { this.printTimeBase = printTimeBase; }
        public int getPrintTimeBasePMD() { return printTimeBasePMD; }
        public void setPrintTimeBasePMD(int printTimeBasePMD) { this.printTimeBasePMD = printTimeBasePMD; }
        public int getNewBlockByBar() { return newBlockByBar; }
        public void setNewBlockByBar(int newBlockByBar) { this.newBlockByBar = newBlockByBar; }
        public BigDecimal getNewLineBarCount() { return newLineBarCount; }
        public void setNewLineBarCount(BigDecimal newLineBarCount) { this.newLineBarCount = newLineBarCount; }
        public int getNewLineByTimeSignature() { return newLineByTimeSignature; }
        public void setNewLineByTimeSignature(int newLineByTimeSignature) { this.newLineByTimeSignature = newLineByTimeSignature; }
        public int getTitleEnable() { return titleEnable; }
        public void setTitleEnable(int titleEnable) { this.titleEnable = titleEnable; }
        public boolean isUseTabAfterPartName() { return useTabAfterPartName; }
        public void setUseTabAfterPartName(boolean useTabAfterPartName) { this.useTabAfterPartName = useTabAfterPartName; }
    }

    @Serdes(beanBinder = JacksonXMLBeanBinder.class)
    public static class NoteRest implements Serializable {
        @JacksonXmlProperty(localName = "OctaveInNewLine")
        private int octaveInNewLine = 0;
        @JacksonXmlProperty(localName = "OctaveCommandCustom")
        private String octaveCommandCustom = "";
        @JacksonXmlProperty(localName = "OctaveDirection")
        private int octaveDirection = 0;
        @JacksonXmlProperty(localName = "LengthStyle")
        private int lengthStyle = 0;
        @JacksonXmlProperty(localName = "DotEnable")
        private boolean dotEnable = true;
        @JacksonXmlProperty(localName = "DotLength")
        private BigDecimal dotLength = BigDecimal.ZERO;
        @JacksonXmlProperty(localName = "CutByBar")
        private int cutByBar = 1;
        @JacksonXmlProperty(localName = "NewBlockInCutted")
        private boolean newBlockInCutted = true;
        @JacksonXmlProperty(localName = "TieCommandCustom")
        private String tieCommandCustom = "";
        @JacksonXmlProperty(localName = "TieStyle")
        private int tieStyle = 0;
        @JacksonXmlProperty(localName = "UnuseTiedRest")
        private boolean unuseTiedRest = false;
        @JacksonXmlProperty(localName = "DefaultLength")
        private BigDecimal defaultLength = new BigDecimal("8");

        // Getters and Setters
        public int getOctaveInNewLine() { return octaveInNewLine; }
        public void setOctaveInNewLine(int octaveInNewLine) { this.octaveInNewLine = octaveInNewLine; }
        public String getOctaveCommandCustom() { return octaveCommandCustom; }
        public void setOctaveCommandCustom(String octaveCommandCustom) { this.octaveCommandCustom = octaveCommandCustom; }
        public int getOctaveDirection() { return octaveDirection; }
        public void setOctaveDirection(int octaveDirection) { this.octaveDirection = octaveDirection; }
        public int getLengthStyle() { return lengthStyle; }
        public void setLengthStyle(int lengthStyle) { this.lengthStyle = lengthStyle; }
        public boolean isDotEnable() { return dotEnable; }
        public void setDotEnable(boolean dotEnable) { this.dotEnable = dotEnable; }
        public BigDecimal getDotLength() { return dotLength; }
        public void setDotLength(BigDecimal dotLength) { this.dotLength = dotLength; }
        public int getCutByBar() { return cutByBar; }
        public void setCutByBar(int cutByBar) { this.cutByBar = cutByBar; }
        public boolean isNewBlockInCutted() { return newBlockInCutted; }
        public void setNewBlockInCutted(boolean newBlockInCutted) { this.newBlockInCutted = newBlockInCutted; }
        public String getTieCommandCustom() { return tieCommandCustom; }
        public void setTieCommandCustom(String tieCommandCustom) { this.tieCommandCustom = tieCommandCustom; }
        public int getTieStyle() { return tieStyle; }
        public void setTieStyle(int tieStyle) { this.tieStyle = tieStyle; }
        public boolean isUnuseTiedRest() { return unuseTiedRest; }
        public void setUnuseTiedRest(boolean unuseTiedRest) { this.unuseTiedRest = unuseTiedRest; }
        public BigDecimal getDefaultLength() { return defaultLength; }
        public void setDefaultLength(BigDecimal defaultLength) { this.defaultLength = defaultLength; }
    }

    @Serdes(beanBinder = JacksonXMLBeanBinder.class)
    public static class ControlCommand implements Serializable {
        private Generic generic = new Generic();
        private Volume volume = new Volume();
        private Pan pan = new Pan();
        private ProgramChange programChange = new ProgramChange();
        private Tempo tempo = new Tempo();

        // Getters and Setters
        public Generic getGeneric() { return generic; }
        public void setGeneric(Generic generic) { this.generic = generic; }
        public Volume getVolume() { return volume; }
        public void setVolume(Volume volume) { this.volume = volume; }
        public Pan getPan() { return pan; }
        public void setPan(Pan pan) { this.pan = pan; }
        public ProgramChange getProgramChange() { return programChange; }
        public void setProgramChange(ProgramChange programChange) { this.programChange = programChange; }
        public Tempo getTempo() { return tempo; }
        public void setTempo(Tempo tempo) { this.tempo = tempo; }

        @Serdes(beanBinder = JacksonXMLBeanBinder.class)
        public static class Generic implements Serializable {
            @JacksonXmlProperty(localName = "Invalid")
            private int invalid = 1;
            @JacksonXmlProperty(localName = "SamePosition")
            private int samePosition = 1;
            @JacksonXmlProperty(localName = "Predeclared")
            private int predeclared = 1;

            // Getters and Setters
            public int getInvalid() { return invalid; }
            public void setInvalid(int invalid) { this.invalid = invalid; }
            public int getSamePosition() { return samePosition; }
            public void setSamePosition(int samePosition) { this.samePosition = samePosition; }
            public int getPredeclared() { return predeclared; }
            public void setPredeclared(int predeclared) { this.predeclared = predeclared; }
        }

        @Serdes(beanBinder = JacksonXMLBeanBinder.class)
        public static class Volume implements Serializable {
            @JacksonXmlProperty(localName = "Enable")
            private boolean enable = true;
            @JacksonXmlProperty(localName = "CommandPMD")
            private int commandPMD = 0;
            @JacksonXmlProperty(localName = "CommandMXDRV")
            private int commandMXDRV = 0;
            @JacksonXmlProperty(localName = "CommandNRTDRV")
            private int commandNRTDRV = 0;
            @JacksonXmlProperty(localName = "CommandCustom")
            private String commandCustom = "";
            @JacksonXmlProperty(localName = "RangeCustom")
            private BigDecimal rangeCustom = BigDecimal.ZERO;
            @JacksonXmlProperty(localName = "VStep")
            private BigDecimal vStep = new BigDecimal("15");

            // Getters and Setters
            public boolean isEnable() { return enable; }
            public void setEnable(boolean enable) { this.enable = enable; }
            public int getCommandPMD() { return commandPMD; }
            public void setCommandPMD(int commandPMD) { this.commandPMD = commandPMD; }
            public int getCommandMXDRV() { return commandMXDRV; }
            public void setCommandMXDRV(int commandMXDRV) { this.commandMXDRV = commandMXDRV; }
            public int getCommandNRTDRV() { return commandNRTDRV; }
            public void setCommandNRTDRV(int commandNRTDRV) { this.commandNRTDRV = commandNRTDRV; }
            public String getCommandCustom() { return commandCustom; }
            public void setCommandCustom(String commandCustom) { this.commandCustom = commandCustom; }
            public BigDecimal getRangeCustom() { return rangeCustom; }
            public void setRangeCustom(BigDecimal rangeCustom) { this.rangeCustom = rangeCustom; }
            public BigDecimal getVStep() { return vStep; }
            public void setVStep(BigDecimal vStep) { this.vStep = vStep; }
        }

        @Serdes(beanBinder = JacksonXMLBeanBinder.class)
        public static class Pan implements Serializable {
            @JacksonXmlProperty(localName = "Enable")
            private boolean enable = true;
            @JacksonXmlProperty(localName = "CommandCustom")
            private int commandCustom = 1;
            @JacksonXmlProperty(localName = "CommandFMP7")
            private int commandFMP7 = 1;
            @JacksonXmlProperty(localName = "MIDICommandCustom")
            private String midiCommandCustom = "";
            @JacksonXmlProperty(localName = "LeftCommandCustom")
            private String leftCommandCustom = "";
            @JacksonXmlProperty(localName = "CenterCommandCustom")
            private String centerCommandCustom = "";
            @JacksonXmlProperty(localName = "RightCommandCustom")
            private String rightCommandCustom = "";
            @JacksonXmlProperty(localName = "BorderLeft")
            private BigDecimal borderLeft = new BigDecimal("32");
            @JacksonXmlProperty(localName = "BorderRight")
            private BigDecimal borderRight = new BigDecimal("96");
            @JacksonXmlProperty(localName = "BorderUsingNegative")
            private boolean borderUsingNegative = false;

            // Getters and Setters
            public boolean isEnable() { return enable; }
            public void setEnable(boolean enable) { this.enable = enable; }
            public int getCommandCustom() { return commandCustom; }
            public void setCommandCustom(int commandCustom) { this.commandCustom = commandCustom; }
            public int getCommandFMP7() { return commandFMP7; }
            public void setCommandFMP7(int commandFMP7) { this.commandFMP7 = commandFMP7; }
            public String getMidiCommandCustom() { return midiCommandCustom; }
            public void setMidiCommandCustom(String midiCommandCustom) { this.midiCommandCustom = midiCommandCustom; }
            public String getLeftCommandCustom() { return leftCommandCustom; }
            public void setLeftCommandCustom(String leftCommandCustom) { this.leftCommandCustom = leftCommandCustom; }
            public String getCenterCommandCustom() { return centerCommandCustom; }
            public void setCenterCommandCustom(String centerCommandCustom) { this.centerCommandCustom = centerCommandCustom; }
            public String getRightCommandCustom() { return rightCommandCustom; }
            public void setRightCommandCustom(String rightCommandCustom) { this.rightCommandCustom = rightCommandCustom; }
            public BigDecimal getBorderLeft() { return borderLeft; }
            public void setBorderLeft(BigDecimal borderLeft) { this.borderLeft = borderLeft; }
            public BigDecimal getBorderRight() { return borderRight; }
            public void setBorderRight(BigDecimal borderRight) { this.borderRight = borderRight; }
            public boolean isBorderUsingNegative() { return borderUsingNegative; }
            public void setBorderUsingNegative(boolean borderUsingNegative) { this.borderUsingNegative = borderUsingNegative; }
        }

        @Serdes(beanBinder = JacksonXMLBeanBinder.class)
        public static class ProgramChange implements Serializable {
            @JacksonXmlProperty(localName = "Enable")
            private boolean enable = true;
            @JacksonXmlProperty(localName = "CommandCustom")
            private String commandCustom = "";

            // Getters and Setters
            public boolean isEnable() { return enable; }
            public void setEnable(boolean enable) { this.enable = enable; }
            public String getCommandCustom() { return commandCustom; }
            public void setCommandCustom(String commandCustom) { this.commandCustom = commandCustom; }
        }

        @Serdes(beanBinder = JacksonXMLBeanBinder.class)
        public static class Tempo implements Serializable {
            @JacksonXmlProperty(localName = "Enable")
            private boolean enable = true;
            @JacksonXmlProperty(localName = "CommandCustom")
            private String commandCustom = "";

            // Getters and Setters
            public boolean isEnable() { return enable; }
            public void setEnable(boolean enable) { this.enable = enable; }
            public String getCommandCustom() { return commandCustom; }
            public void setCommandCustom(String commandCustom) { this.commandCustom = commandCustom; }
        }
    }

    @Serdes(beanBinder = JacksonXMLBeanBinder.class)
    public static class OutputPart implements Serializable {
        @JacksonXmlProperty(localName = "PrintStyle")
        private int printStyle = 2;
        @JacksonXmlProperty(localName = "PrintStyleCustom")
        private int printStyleCustom = 1;
        @JacksonXmlProperty(localName = "AutoNameFMP7")
        private int autoNameFMP7 = 0;
        @JacksonXmlProperty(localName = "AutoNameFMP")
        private int autoNameFMP = 0;
        @JacksonXmlProperty(localName = "AutoNamePMD")
        private int autoNamePMD = 2;
        @JacksonXmlProperty(localName = "AutoNameNRTDRV")
        private int autoNameNRTDRV = 1;
        @JacksonXmlProperty(localName = "AutoNameMml2vgm")
        private int autoNameMml2vgm = 0;
        @JacksonXmlProperty(localName = "RemoveEmptyParts")
        private boolean removeEmptyParts = true;

        // Getters and Setters
        public int getPrintStyle() { return printStyle; }
        public void setPrintStyle(int printStyle) { this.printStyle = printStyle; }
        public int getPrintStyleCustom() { return printStyleCustom; }
        public void setPrintStyleCustom(int printStyleCustom) { this.printStyleCustom = printStyleCustom; }
        public int getAutoNameFMP7() { return autoNameFMP7; }
        public void setAutoNameFMP7(int autoNameFMP7) { this.autoNameFMP7 = autoNameFMP7; }
        public int getAutoNameFMP() { return autoNameFMP; }
        public void setAutoNameFMP(int autoNameFMP) { this.autoNameFMP = autoNameFMP; }
        public int getAutoNamePMD() { return autoNamePMD; }
        public void setAutoNamePMD(int autoNamePMD) { this.autoNamePMD = autoNamePMD; }
        public int getAutoNameNRTDRV() { return autoNameNRTDRV; }
        public void setAutoNameNRTDRV(int autoNameNRTDRV) { this.autoNameNRTDRV = autoNameNRTDRV; }
        public int getAutoNameMml2vgm() { return autoNameMml2vgm; }
        public void setAutoNameMml2vgm(int autoNameMml2vgm) { this.autoNameMml2vgm = autoNameMml2vgm; }
        public boolean isRemoveEmptyParts() { return removeEmptyParts; }
        public void setRemoveEmptyParts(boolean removeEmptyParts) { this.removeEmptyParts = removeEmptyParts; }
    }

    private static File getSettingsFile() {
        String appData = System.getenv("LOCALAPPDATA");
        if (appData == null) {
            // Fallback for non-Windows or if LOCALAPPDATA instanceof not set
            appData = System.getProperty("user.home") + File.separator + ".config";
        }
        String company = Common.getAssemblyCompany();
        String title = Common.getAssemblyTitle();

        // Ensure company and title are not null to avoid NullPointerException
        company = (company != null) ? company : "DefaultCompany";
        title = (title != null) ? title : "DefaultApp";

        File path = new File(appData, company + File.separator + title);
        if (!path.exists()) {
            path.mkdirs();
        }
        return new File(path, "Settings.xml");
    }
}
