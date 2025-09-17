/*
 * Copyright © 2017-2019 Rerrah
 *
 * MIDI to MML Converter
 */

package ConvFMML;

import ConvFMML.Common.MMLStyle;

import java.io.File;
import java.io.Serializable;
import java.math.BigDecimal;

@XmlRootElement(name = "Settings")
@XmlAccessorType(XmlAccessType.FIELD)
public class Settings implements Serializable {
    @XmlElement(name = "mmlExpression")
    private MMLExpression mmlExpression = new MMLExpression();
    @XmlElement(name = "noteRest")
    private NoteRest noteRest = new NoteRest();
    @XmlElement(name = "controlCommand")
    private ControlCommand controlCommand = new ControlCommand();
    @XmlElement(name = "outputPart")
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

    @XmlAccessorType(XmlAccessType.FIELD)
    public class MMLExpression implements Serializable {
        @XmlElement(name = "MMLStyle")
        private MMLStyle mmlStyle = MMLStyle.FMP7;
        @XmlElement(name = "ExtensionCustom")
        private String extensionCustom = ".mml";
        @XmlElement(name = "ExtensionFMP")
        private int extensionFMP = 1;
        @XmlElement(name = "TimeBase")
        private BigDecimal timeBase = new BigDecimal("192");
        @XmlElement(name = "PrintTimeBase")
        private int printTimeBase = 1;
        @XmlElement(name = "PrintTimeBasePMD")
        private int printTimeBasePMD = 1;
        @XmlElement(name = "NewBlockByBar")
        private int newBlockByBar = 1;
        @XmlElement(name = "NewLineBarCount")
        private BigDecimal newLineBarCount = new BigDecimal("2");
        @XmlElement(name = "NewLineByTimeSignature")
        private int newLineByTimeSignature = 1;
        @XmlElement(name = "TitleEnable")
        private int titleEnable = 1;
        @XmlElement(name = "UseTabAfterPartName")
        private boolean useTabAfterPartName = false;

        @XmlTransient
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

    @XmlAccessorType(XmlAccessType.FIELD)
    public class NoteRest implements Serializable {
        @XmlElement(name = "OctaveInNewLine")
        private int octaveInNewLine = 0;
        @XmlElement(name = "OctaveCommandCustom")
        private String octaveCommandCustom = "";
        @XmlElement(name = "OctaveDirection")
        private int octaveDirection = 0;
        @XmlElement(name = "LengthStyle")
        private int lengthStyle = 0;
        @XmlElement(name = "DotEnable")
        private boolean dotEnable = true;
        @XmlElement(name = "DotLength")
        private BigDecimal dotLength = BigDecimal.ZERO;
        @XmlElement(name = "CutByBar")
        private int cutByBar = 1;
        @XmlElement(name = "NewBlockInCutted")
        private boolean newBlockInCutted = true;
        @XmlElement(name = "TieCommandCustom")
        private String tieCommandCustom = "";
        @XmlElement(name = "TieStyle")
        private int tieStyle = 0;
        @XmlElement(name = "UnuseTiedRest")
        private boolean unuseTiedRest = false;
        @XmlElement(name = "DefaultLength")
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

    @XmlAccessorType(XmlAccessType.FIELD)
    public class ControlCommand implements Serializable {
        @XmlElement(name = "generic")
        private Generic generic = new Generic();
        @XmlElement(name = "volume")
        private Volume volume = new Volume();
        @XmlElement(name = "pan")
        private Pan pan = new Pan();
        @XmlElement(name = "programChange")
        private ProgramChange programChange = new ProgramChange();
        @XmlElement(name = "tempo")
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

        @XmlAccessorType(XmlAccessType.FIELD)
        public class Generic implements Serializable {
            @XmlElement(name = "Invalid")
            private int invalid = 1;
            @XmlElement(name = "SamePosition")
            private int samePosition = 1;
            @XmlElement(name = "Predeclared")
            private int predeclared = 1;

            // Getters and Setters
            public int getInvalid() { return invalid; }
            public void setInvalid(int invalid) { this.invalid = invalid; }
            public int getSamePosition() { return samePosition; }
            public void setSamePosition(int samePosition) { this.samePosition = samePosition; }
            public int getPredeclared() { return predeclared; }
            public void setPredeclared(int predeclared) { this.predeclared = predeclared; }
        }

        @XmlAccessorType(XmlAccessType.FIELD)
        public class Volume implements Serializable {
            @XmlElement(name = "Enable")
            private boolean enable = true;
            @XmlElement(name = "CommandPMD")
            private int commandPMD = 0;
            @XmlElement(name = "CommandMXDRV")
            private int commandMXDRV = 0;
            @XmlElement(name = "CommandNRTDRV")
            private int commandNRTDRV = 0;
            @XmlElement(name = "CommandCustom")
            private String commandCustom = "";
            @XmlElement(name = "RangeCustom")
            private BigDecimal rangeCustom = BigDecimal.ZERO;
            @XmlElement(name = "VStep")
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

        @XmlAccessorType(XmlAccessType.FIELD)
        public class Pan implements Serializable {
            @XmlElement(name = "Enable")
            private boolean enable = true;
            @XmlElement(name = "CommandCustom")
            private int commandCustom = 1;
            @XmlElement(name = "CommandFMP7")
            private int commandFMP7 = 1;
            @XmlElement(name = "MIDICommandCustom")
            private String midiCommandCustom = "";
            @XmlElement(name = "LeftCommandCustom")
            private String leftCommandCustom = "";
            @XmlElement(name = "CenterCommandCustom")
            private String centerCommandCustom = "";
            @XmlElement(name = "RightCommandCustom")
            private String rightCommandCustom = "";
            @XmlElement(name = "BorderLeft")
            private BigDecimal borderLeft = new BigDecimal("32");
            @XmlElement(name = "BorderRight")
            private BigDecimal borderRight = new BigDecimal("96");
            @XmlElement(name = "BorderUsingNegative")
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

        @XmlAccessorType(XmlAccessType.FIELD)
        public class ProgramChange implements Serializable {
            @XmlElement(name = "Enable")
            private boolean enable = true;
            @XmlElement(name = "CommandCustom")
            private String commandCustom = "";

            // Getters and Setters
            public boolean isEnable() { return enable; }
            public void setEnable(boolean enable) { this.enable = enable; }
            public String getCommandCustom() { return commandCustom; }
            public void setCommandCustom(String commandCustom) { this.commandCustom = commandCustom; }
        }

        @XmlAccessorType(XmlAccessType.FIELD)
        public class Tempo implements Serializable {
            @XmlElement(name = "Enable")
            private boolean enable = true;
            @XmlElement(name = "CommandCustom")
            private String commandCustom = "";

            // Getters and Setters
            public boolean isEnable() { return enable; }
            public void setEnable(boolean enable) { this.enable = enable; }
            public String getCommandCustom() { return commandCustom; }
            public void setCommandCustom(String commandCustom) { this.commandCustom = commandCustom; }
        }
    }

    @XmlAccessorType(XmlAccessType.FIELD)
    public class OutputPart implements Serializable {
        @XmlElement(name = "PrintStyle")
        private int printStyle = 2;
        @XmlElement(name = "PrintStyleCustom")
        private int printStyleCustom = 1;
        @XmlElement(name = "AutoNameFMP7")
        private int autoNameFMP7 = 0;
        @XmlElement(name = "AutoNameFMP")
        private int autoNameFMP = 0;
        @XmlElement(name = "AutoNamePMD")
        private int autoNamePMD = 2;
        @XmlElement(name = "AutoNameNRTDRV")
        private int autoNameNRTDRV = 1;
        @XmlElement(name = "AutoNameMml2vgm")
        private int autoNameMml2vgm = 0;
        @XmlElement(name = "RemoveEmptyParts")
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

    public static Settings load() {
        File settingsFile = getSettingsFile();
        if (!settingsFile.exists()) {
            return new Settings(); // Return default settings if file doesn't exist
        }

        try {
            JAXBContext context = JAXBContext.newInstance(Settings.class);
            Unmarshaller unmarshaller = context.createUnmarshaller();
            return (Settings) unmarshaller.unmarshal(settingsFile);
        } catch (JAXBException e) {
            e.printStackTrace(); // Log the exception
            return new Settings(); // Return default settings on read failure
        }
    }

    public void save() {
        File settingsFile = getSettingsFile();
        try {
            JAXBContext context = JAXBContext.newInstance(Settings.class);
            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            marshaller.marshal(this, settingsFile);
        } catch (JAXBException e) {
            e.printStackTrace(); // Log the exception: Failed to save settings
        }
    }
}
