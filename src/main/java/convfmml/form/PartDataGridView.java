/*
 * Copyright © 2017-2019 Rerrah
 *
 * MIDI to MML Converter
 */

package convfmml.form;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.ComponentEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.List;
import java.util.ResourceBundle;
import javax.swing.DefaultCellEditor;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;

import convfmml.Common;
import convfmml.Common.MMLStyle;
import convfmml.Common.SoundModule;
import convfmml.Settings;
import convfmml.data.intermediate.NotesStatus;


public class PartDataGridView extends JPanel {

    static final ResourceBundle rb = ResourceBundle.getBundle("messages");

    private List<NotesStatus> dataSource;
    private MMLStyle mmlStyle;
    private Settings.OutputPart settings;

    public List<NotesStatus> getDataSource() {
        return dataSource;
    }

    public void setDataSource(List<NotesStatus> dataSource) {
        this.dataSource = dataSource;
    }

    public MMLStyle getMmlStyle() {
        return mmlStyle;
    }

    public void setMmlStyle(MMLStyle mmlStyle) {
        this.mmlStyle = mmlStyle;
    }

    public Settings.OutputPart getSettings() {
        return settings;
    }

    public void setSettings(Settings.OutputPart settings) {
        this.settings = settings;
    }

    public PartDataGridView() {
        InitializeComponent();
    }

    protected void OnLoad(ComponentEvent e) {

        dgv.getModel().addTableModelListener(this.dgv_CurrentCellDirtyStateChanged);
        dgv.getModel().addTableModelListener(this.dgv_CellValueChanged);
    }

    public void ShowData() {
        dtm.setRowCount(0);

        if (dataSource == null) throw new NullPointerException();

        if (mmlStyle == MMLStyle.Custom && settings.getPrintStyleCustom() == 0) { // Don't print part name
            partNameColumn.setEditable(false);
            dataSource.forEach(x -> x.setName(""));
            soundModuleColumn.setEditable(true);
        } else {
            switch (settings.getPrintStyle()) {
                case 0: // Don't print
                    partNameColumn.setEditable(false);
                    dataSource.forEach(x -> x.setName(""));
                    soundModuleColumn.setEditable(true);
                    break;
                case 1: // Custom
                    partNameColumn.setEditable(true);
                    soundModuleColumn.setEditable(true);
                    break;
                case 2: // Auto
                    partNameColumn.setEditable(false);
                    dataSource.forEach(x -> x.setName(""));
                    soundModuleColumn.setEditable(mmlStyle == Common.MMLStyle.FMP7);
                    break;
            }
        }

        if (settings.isRemoveEmptyParts()) {
            dataSource.stream().filter(NotesStatus::isEmpty).forEach(x -> x.setPrintable(false));
        }

        addRows();
    }

    private void addRows() {
        DefaultTableModel model = (DefaultTableModel) dgv.getModel();

        for (NotesStatus ns : dataSource) {
            if (!settings.isRemoveEmptyParts() || !ns.isEmpty()) {
                Object[] rowData = new Object[6];

                rowData[0] = ns.isPrintable();
                rowData[1] = ns.getName();
                rowData[2] = ns.getTrackNumber();
                rowData[3] = ns.getTrackName();
                rowData[4] = ns.isEmpty() ? rb.getString("PartNoteNone") : rb.getString("PartNoteExist");

                JComboBox<SoundModule> comboBox = new JComboBox<>();
                switch (mmlStyle) {
                    case FMP7:
                    case NRTDRV:
                    case MUCOM88:
                        comboBox.addItem(SoundModule.FM);
                        comboBox.addItem(SoundModule.SSG);
                        break;
                    case FMP:
                    case PMD:
                        comboBox.addItem(SoundModule.FM);
                        comboBox.addItem(SoundModule.SSG);
                        comboBox.addItem(SoundModule.FM3ch);
                        break;
                    case MXDRV:
                        comboBox.addItem(SoundModule.FM);
                        break;
                    case Mml2vgm:
                        comboBox.addItem(SoundModule.FM);
                        comboBox.addItem(SoundModule.SSG);
                        comboBox.addItem(SoundModule.FM3ch);
                        comboBox.addItem(SoundModule.Others);
                        break;
                    default:
                        comboBox.addItem(SoundModule.FM);
                        comboBox.addItem(SoundModule.SSG);
                        comboBox.addItem(SoundModule.FM3ch);
                        comboBox.addItem(SoundModule.Others);
                        break;
                }

                comboBox.setSelectedItem(ns.getSoundModule());
                rowData[5] = comboBox;

                model.addRow(rowData);

                // Optional: Set row height and background color
                int rowIndex = model.getRowCount() - 1;
                dgv.setRowHeight(rowIndex, 18);
                dgv.prepareRenderer((table, value, isSelected, hasFocus, row, column) -> {
                    Component c = table.getCellRenderer(row, column).getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                    c.setBackground(ns.isPrintable() ? Color.WHITE : UIManager.getColor("Panel.background"));
                    return c;
                }, rowIndex, 0);
            }
        }

        dgv.clearSelection();

        if (mmlStyle != MMLStyle.Custom && settings.getPrintStyle() == 2) {
            autoModifyPart();
        }
    }

    static final Color LavenderBlush = new Color(255, 240, 245);

    private void autoModifyPart() {
        int n = 0;

        for (int row = 0; row < dtm.getRowCount(); row++) {
            NotesStatus status = (NotesStatus) dtm.getValueAt(row, 6); // assuming column 6 stores the status object

            if (!status.isPrintable()) {
                dtm.setValueAt("", row, 1); // Clear PartName
                status.setName("");
                continue;
            }

            String name;
            SoundModule module;
            Color backColor;

            switch (mmlStyle) {
                case MMLStyle.FMP7:
                    switch (settings.getAutoNameFMP7()) {
                        case 0:
                            if (n < 26) {
                                name = "'" + (char) (0x41 + n);
                                backColor = Color.white;
                            } else {
                                name = "";
                                backColor = LavenderBlush;
                            }
                            break;
                        case 1:
                            if (n < 260) {
                                name = "'" + ((char) (0x41 + n % 26) + (n / 26));
                                backColor = Color.white;
                            } else {
                                name = "";
                                backColor = LavenderBlush;
                            }
                            break;
                        case 2:
                            if (n < 260) {
                                name = "'" + ((char) (0x41 + n / 10) + (n % 10));
                                backColor = Color.white;
                            } else {
                                name = "";
                                backColor = LavenderBlush;
                            }
                            break;
                        default:
                            name = "";
                            backColor = LavenderBlush;
                            break;
                    }
                    module = SoundModule.FM;
                    break;

                case MMLStyle.FMP:
                    switch (settings.getAutoNameFMP()) {
                        case 0:
                            if (n < 3) {
                                name = "'" + (char) (0x41 + n);
                                module = SoundModule.FM;
                                backColor = Color.white;
                            } else if (n < 6) {
                                name = "'" + (char) (0x41 + n);
                                module = SoundModule.SSG;
                                backColor = Color.white;
                            } else if (n < 9) {
                                name = "'" + (char) (0x58 + n - 6);
                                module = SoundModule.FM3ch;
                                backColor = Color.white;
                            } else {
                                name = "";
                                module = SoundModule.FM;
                                backColor = LavenderBlush;
                            }
                            break;
                        case 1:
                            if (n < 3) {
                                name = "'" + (char) (0x41 + n);
                                module = SoundModule.FM;
                                backColor = Color.white;
                            } else if (n < 6) {
                                name = "'" + (char) (0x41 + n);
                                module = SoundModule.SSG;
                                backColor = Color.white;
                            } else if (n < 9) {
                                name = "'" + (char) (0x41 + n);
                                module = SoundModule.FM;
                                backColor = Color.white;
                            } else if (n < 12) {
                                name = "'" + (char) (0x58 + n - 9);
                                module = SoundModule.FM3ch;
                                backColor = Color.white;
                            } else {
                                name = "";
                                module = SoundModule.FM;
                                backColor = LavenderBlush;
                            }
                            break;
                        default:
                            name = "";
                            module = SoundModule.FM;
                            backColor = LavenderBlush;
                            break;
                    }
                    break;

                case MMLStyle.PMD:
                    switch (settings.getAutoNamePMD()) {
                        case 0:
                            if (n < 3) {
                                name = String.valueOf((char) (0x41 + n));
                                module = SoundModule.FM;
                                backColor = Color.white;
                            } else if (n < 6) {
                                name = String.valueOf((char) (0x47 + n - 3));
                                module = SoundModule.SSG;
                                backColor = Color.white;
                            } else {
                                name = "";
                                module = SoundModule.FM;
                                backColor = LavenderBlush;
                            }
                            break;
                        case 1:
                            if (n < 3) {
                                name = String.valueOf((char) (0x41 + n));
                                module = SoundModule.FM;
                                backColor = Color.white;
                            } else if (n < 6) {
                                name = String.valueOf((char) (0x41 + n));
                                module = SoundModule.FM3ch;
                                backColor = Color.white;
                            } else if (n < 9) {
                                name = String.valueOf((char) (0x41 + n));
                                module = SoundModule.SSG;
                                backColor = Color.white;
                            } else {
                                name = "";
                                module = SoundModule.FM;
                                backColor = LavenderBlush;
                            }
                            break;

                        case 2:
                            if (n < 6) {
                                name = String.valueOf((char) (0x41 + n));
                                module = SoundModule.FM;
                                backColor = Color.white;
                            } else if (n < 9) {
                                name = String.valueOf((char) (0x41 + n));
                                module = SoundModule.SSG;
                                backColor = Color.white;
                            } else if (n < 12) {
                                name = String.valueOf((char) (0x58 + n - 9));
                                module = SoundModule.FM3ch;
                                backColor = Color.white;
                            } else {
                                name = "";
                                module = SoundModule.FM;
                                backColor = LavenderBlush;
                            }
                            break;
                        case 3:
                            if (n < 6) {
                                name = String.valueOf((char) (0x41 + n));
                                module = SoundModule.FM;
                                backColor = Color.white;
                            } else {
                                name = "";
                                module = SoundModule.FM;
                                backColor = LavenderBlush;
                            }
                            break;
                        case 4:
                            if (n < 8) {
                                name = String.valueOf((char) (0x41 + n));
                                module = SoundModule.FM;
                                backColor = Color.white;
                            } else {
                                name = "";
                                module = SoundModule.FM;
                                backColor = LavenderBlush;
                            }
                            break;
                        case 5:
                            if (n < 9) {
                                name = String.valueOf((char) (0x41 + n));
                                module = SoundModule.FM;
                                backColor = Color.white;
                            } else {
                                name = "";
                                module = SoundModule.FM;
                                backColor = LavenderBlush;
                            }
                            break;
                        default:
                            name = "";
                            module = SoundModule.FM;
                            backColor = LavenderBlush;
                            break;
                    }
                    break;

                case MMLStyle.MXDRV:
                    if (n < 8) {
                        name = String.valueOf((char) (0x41 + n));
                        module = SoundModule.FM;
                        backColor = Color.white;
                    } else {
                        name = "";
                        module = SoundModule.FM;
                        backColor = LavenderBlush;
                    }
                    break;

                case MMLStyle.NRTDRV:
                    switch (settings.getAutoNameNRTDRV()) {
                        case 0:
                            if (n < 3) {
                                name = String.valueOf(n + 1);
                                module = SoundModule.SSG;
                                backColor = Color.white;
                            } else {
                                name = "";
                                module = SoundModule.FM;
                                backColor = LavenderBlush;
                            }
                            break;
                        case 1:
                            if (n < 16) {
                                name = String.valueOf((char) (0x41 + n));
                                module = SoundModule.FM;
                                backColor = Color.white;
                            } else if (n < 19) {
                                name = String.valueOf(n - 15);
                                module = SoundModule.SSG;
                                backColor = Color.white;
                            } else {
                                name = "";
                                module = SoundModule.FM;
                                backColor = LavenderBlush;
                            }
                            break;
                        case 2:
                            if (n < 8) {
                                name = String.valueOf((char) (0x41 + n));
                                module = SoundModule.FM;
                                backColor = Color.white;
                            } else if (n < 11) {
                                name = String.valueOf(n - 7);
                                module = SoundModule.SSG;
                                backColor = Color.white;
                            } else if (n < 19) {
                                name = String.valueOf((char) (0x49 + n - 11));
                                module = SoundModule.FM;
                                backColor = Color.white;
                            } else {
                                name = "";
                                module = SoundModule.FM;
                                backColor = LavenderBlush;
                            }
                            break;
                        default:
                            name = "";
                            module = SoundModule.FM;
                            backColor = LavenderBlush;
                            break;
                    }
                    break;

                case MMLStyle.MUCOM88:
                    if (n < 3) {
                        name = String.valueOf((char) (0x41 + n));
                        module = SoundModule.FM;
                        backColor = Color.white;
                    } else if (n < 6) {
                        name = String.valueOf((char) (0x41 + n));
                        module = SoundModule.SSG;
                        backColor = Color.white;
                    } else if (n < 9) {
                        name = String.valueOf((char) (0x42 + n));
                        module = SoundModule.FM;
                        backColor = Color.white;
                    } else {
                        name = "";
                        module = SoundModule.FM;
                        backColor = LavenderBlush;
                    }
                    break;

                case MMLStyle.Mml2vgm:
                    switch (settings.getAutoNameMml2vgm()) {
                        case 0:
                            if (n < 6) {
                                name = "'F" + (n + 1);
                                module = SoundModule.FM;
                                backColor = Color.white;
                            } else if (n < 10) {
                                name = "'S" + (n - 5);
                                module = SoundModule.SSG;
                                backColor = Color.white;
                            } else {
                                name = "";
                                module = SoundModule.Others;
                                backColor = LavenderBlush;
                            }
                            break;
                        case 1:
                            if (n < 6) {
                                name = "'F" + (n + 1);
                                module = SoundModule.FM;
                                backColor = Color.white;
                            } else if (n < 9) {
                                name = "'F" + (n + 1);
                                module = SoundModule.FM3ch;
                                backColor = Color.white;
                            } else if (n < 13) {
                                name = "'S" + (n - 8);
                                module = SoundModule.SSG;
                                backColor = Color.white;
                            } else {
                                name = "";
                                module = SoundModule.Others;
                                backColor = LavenderBlush;
                            }
                            break;
                        case 2:
                            if (n < 6) {
                                name = "'E{n + 1:D2}";
                                module = SoundModule.FM;
                                backColor = Color.white;
                            } else if (n < 10) {
                                name = "'S" + (n - 5);
                                module = SoundModule.SSG;
                                backColor = Color.white;
                            } else {
                                name = "";
                                module = SoundModule.Others;
                                backColor = LavenderBlush;
                            }
                            break;
                        case 3:
                            if (n < 6) {
                                name = "'E{n + 1:D2}";
                                module = SoundModule.FM;
                                backColor = Color.white;
                            } else if (n < 9) {
                                name = "'E{n + 1:D2}";
                                module = SoundModule.FM3ch;
                                backColor = Color.white;
                            } else if (n < 13) {
                                name = "'S" + (n - 8);
                                module = SoundModule.SSG;
                                backColor = Color.white;
                            } else {
                                name = "";
                                module = SoundModule.Others;
                                backColor = LavenderBlush;
                            }
                            break;
                        case 4:
                            if (n < 4) {
                                name = "'S" + (n + 1);
                                module = SoundModule.SSG;
                                backColor = Color.white;
                            } else {
                                name = "";
                                module = SoundModule.Others;
                                backColor = LavenderBlush;
                            }
                            break;
                        case 5:
                            if (n < 6) {
                                name = "'T{n + 1:D2}";
                                module = SoundModule.FM;
                                backColor = Color.white;
                            } else if (n < 9) {
                                name = "'T{n + 4:D2}";
                                module = SoundModule.SSG;
                                backColor = Color.white;
                            } else {
                                name = "";
                                module = SoundModule.Others;
                                backColor = LavenderBlush;
                            }
                            break;
                        case 6:
                            if (n < 6) {
                                name = "'T{n + 1:D2}";
                                module = SoundModule.FM;
                                backColor = Color.white;
                            } else if (n < 9) {
                                name = "'T{n + 1:D2}";
                                module = SoundModule.FM3ch;
                                backColor = Color.white;
                            } else if (n < 12) {
                                name = "'T{n + 1:D2}";
                                module = SoundModule.SSG;
                                backColor = Color.white;
                            } else {
                                name = "";
                                module = SoundModule.Others;
                                backColor = LavenderBlush;
                            }
                            break;
                        case 7:
                            if (n < 3) {
                                name = "'T{n + 10:D2}";
                                module = SoundModule.SSG;
                                backColor = Color.white;
                            } else {
                                name = "";
                                module = SoundModule.Others;
                                backColor = LavenderBlush;
                            }
                            break;
                        case 8:
                            if (n < 6) {
                                name = "'P{n + 1:D2}";
                                module = SoundModule.FM;
                                backColor = Color.white;
                            } else if (n < 9) {
                                name = "'P{n + 4:D2}";
                                module = SoundModule.SSG;
                                backColor = Color.white;
                            } else {
                                name = "";
                                module = SoundModule.Others;
                                backColor = LavenderBlush;
                            }
                            break;
                        case 9:
                            if (n < 6) {
                                name = "'P{n + 1:D2}";
                                module = SoundModule.FM;
                                backColor = Color.white;
                            } else if (n < 9) {
                                name = "'P{n + 1:D2}";
                                module = SoundModule.FM3ch;
                                backColor = Color.white;
                            } else if (n < 12) {
                                name = "'P{n + 1:D2}";
                                module = SoundModule.SSG;
                                backColor = Color.white;
                            } else {
                                name = "";
                                module = SoundModule.Others;
                                backColor = LavenderBlush;
                            }
                            break;
                        case 10:
                            if (n < 3) {
                                name = "'P{n + 10:D2}";
                                module = SoundModule.SSG;
                                backColor = Color.white;
                            } else {
                                name = "";
                                module = SoundModule.Others;
                                backColor = LavenderBlush;
                            }
                            break;
                        case 11:
                            if (n < 3) {
                                name = "'N" + (n + 1);
                                module = SoundModule.FM;
                                backColor = Color.white;
                            } else if (n < 6) {
                                name = "'N" + (n + 4);
                                module = SoundModule.SSG;
                                backColor = Color.white;
                            } else {
                                name = "";
                                module = SoundModule.Others;
                                backColor = LavenderBlush;
                            }
                            break;
                        case 12:
                            if (n < 3) {
                                name = "'N" + (n + 1);
                                module = SoundModule.FM;
                                backColor = Color.white;
                            } else if (n < 6) {
                                name = "'N" + (n + 1);
                                module = SoundModule.FM3ch;
                                backColor = Color.white;
                            } else if (n < 9) {
                                name = "'N" + (n + 1);
                                module = SoundModule.SSG;
                                backColor = Color.white;
                            } else {
                                name = "";
                                module = SoundModule.Others;
                                backColor = LavenderBlush;
                            }
                            break;
                        case 13:
                            if (n < 3) {
                                name = "'N" + (n + 7);
                                module = SoundModule.SSG;
                                backColor = Color.white;
                            } else {
                                name = "";
                                module = SoundModule.Others;
                                backColor = LavenderBlush;
                            }
                            break;
                        case 14:
                            if (n < 8) {
                                name = "'X" + (n + 1);
                                module = SoundModule.FM;
                                backColor = Color.white;
                            } else {
                                name = "";
                                module = SoundModule.Others;
                                backColor = LavenderBlush;
                            }
                            break;
                        case 15:
                            if (n < 3) {
                                name = "'A" + (n + 1);
                                module = SoundModule.SSG;
                                backColor = Color.white;
                            } else {
                                name = "";
                                module = SoundModule.Others;
                                backColor = LavenderBlush;
                            }
                            break;
                        case 16:
                            if (n < 9) {
                                name = "'L{n + 1:D2}";
                                module = SoundModule.FM;
                                backColor = Color.white;
                            } else {
                                name = "";
                                module = SoundModule.Others;
                                backColor = LavenderBlush;
                            }
                            break;
                        default:
                            name = "";
                            module = SoundModule.Others;
                            backColor = LavenderBlush;
                            break;
                    }
                    break;

                default:
                    name = "";
                    module = SoundModule.FM;
                    backColor = LavenderBlush;
                    break;
            }
            dtm.setValueAt(name, row, 1);
            status.setName(name);

            dtm.setValueAt(module, row, 5);
            status.setSoundModule(module);

            // Set background color
            dgv.prepareRenderer((tbl, value, isSelected, hasFocus, r, c) -> {
                Component comp = tbl.getCellRenderer(r, c).getTableCellRendererComponent(tbl, value, isSelected, hasFocus, r, c);
                comp.setBackground(backColor);
                return comp;
            }, row, 0);

            n++;
        }
    }

    // Handles focus entering a cell
    private final FocusListener dgv_CellEnter = new FocusAdapter() {
        @Override
        public void focusGained(FocusEvent e) {
            JTable table = (JTable) e.getComponent();
            int row = table.getSelectedRow();
            int column = table.getSelectedColumn();

            if (column == 1) {
                table.editCellAt(row, column);
                Component editor = table.getEditorComponent();
                if (editor instanceof JTextField) {
                    editor.requestFocus();
                }
            } else if (column == 5) {
                table.editCellAt(row, column);
                Component editor = table.getEditorComponent();
                if (editor instanceof JComboBox) {
                    ((JComboBox<?>) editor).showPopup();
                }
            }
        }
    };

    // Listener for cell edit commit logic
    private final TableModelListener dgv_CurrentCellDirtyStateChanged = new TableModelListener() {
        @Override
        public void tableChanged(TableModelEvent e) {
            if (e.getType() == TableModelEvent.UPDATE) {
                int row = e.getFirstRow();
                int column = e.getColumn();

                if (row >= 0 && column >= 0) {
                    switch (column) {
                        case 0: // Checkbox
                        case 1: // PartName
                        case 5: // SoundModule
                            if (dgv.isEditing()) {
                                TableCellEditor editor = dgv.getCellEditor();
                                if (editor != null) {
                                    editor.stopCellEditing(); // Commits the edit
                                }
                            }
                            break;
                        default:
                            break;
                    }
                }
            }
        }
    };

    private final TableModelListener dgv_CellValueChanged = new TableModelListener() {
        @Override
        public void tableChanged(TableModelEvent e) {
            if (e.getType() == TableModelEvent.UPDATE) {
                int row = e.getFirstRow();
                int column = e.getColumn();

                if (row >= 0 && column >= 0) {
                    NotesStatus status = (NotesStatus) dgv.getValueAt(row, 6); // assuming Tag is stored in column 6

                    switch (column) {
                        case 0: // Checkbox
                            Boolean printable = (Boolean) dgv.getValueAt(row, column);
                            status.setPrintable(printable);
                            dgv.setRowSelectionAllowed(true);
                            dgv.setSelectionBackground(printable ? Color.WHITE : UIManager.getColor("Panel.background"));
                            if (mmlStyle != MMLStyle.Custom && settings.getPrintStyle() == 2) {
                                autoModifyPart();
                            }
                            break;
                        case 1: // PartName
                            String name = (String) dgv.getValueAt(row, column);
                            status.setName(name != null ? name : "");
                            break;
                        case 5: // SoundModule
                            SoundModule module = (SoundModule) dgv.getValueAt(row, column);
                            status.setSoundModule(module);
                            break;
                        default:
                            break;
                    }
                }
            }
        }
    };

    // Handles data error fallback
    private final TableModelListener dgv_DataError = new TableModelListener() {
        @Override
        public void tableChanged(TableModelEvent e) {
            int rowIndex = e.getFirstRow();
            int columnIndex = e.getColumn();
            try {
                NotesStatus status = (NotesStatus) dgv.getValueAt(rowIndex, 6); // assuming Tag is in column 6
                if (columnIndex == 5) { // SoundModule
                    dgv.setValueAt(SoundModule.FM, rowIndex, columnIndex);
                    status.setSoundModule(SoundModule.FM);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, "Data error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    };

//#region Windows Form Designer generated code

    private void InitializeComponent() {
//        System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(PartDataGridView));
//        JTableCellStyle dataGridViewCellStyle1 = new JTableCellStyle();
        dtm = new DefaultTableModel();
        this.dgv = new JTable();
        this.dgv.setModel(dtm);
        this.printableCheckColumn = new JCheckBox();
        this.partNameColumn = new JTextField();
        this.midiTrackNumberColumn = new JTextField();
        this.midiTrackNameColumn = new JTextField();
        this.IsEnptyColumn = new JTextField();
        this.soundModuleColumn = new JComboBox<>();
        this.spaceColumn = new JTextField();
//        ((System.ComponentModel.ISupportInitialize) (this.dgv)).BeginInit();
//        this.SuspendLayout();
        //
        // dgv
        //
//        resources.ApplyResources(this.dgv, "dgv");
//        this.dgv.AllowUserToAddRows = false;
//        this.dgv.AllowUserToDeleteRows = false;
//        this.dgv.AllowUserToResizeRows = false;
//        this.dgv.ColumnHeadersHeightSizeMode = JTableColumnHeadersHeightSizeMode.DisableResizing;
        this.dgv.getColumnModel().getColumn(0).setCellEditor(new DefaultCellEditor(this.printableCheckColumn));
        this.dgv.getColumnModel().getColumn(1).setCellEditor(new DefaultCellEditor(this.partNameColumn));
        this.dgv.getColumnModel().getColumn(2).setCellEditor(new DefaultCellEditor(this.midiTrackNumberColumn));
        this.dgv.getColumnModel().getColumn(3).setCellEditor(new DefaultCellEditor(this.midiTrackNameColumn));
        this.dgv.getColumnModel().getColumn(4).setCellEditor(new DefaultCellEditor(this.IsEnptyColumn));
        this.dgv.getColumnModel().getColumn(5).setCellEditor(new DefaultCellEditor(this.soundModuleColumn));
        this.dgv.getColumnModel().getColumn(6).setCellEditor(new DefaultCellEditor(this.spaceColumn));
//        this.dgv.GridColor = SystemColors.Control;
        this.dgv.setName("dgv");
//        this.dgv.RowHeadersVisible = false;
//        this.dgv.RowTemplate.Height = 18;
//        this.dgv.SelectionMode = JTableSelectionMode.FullRowSelect;
//        this.dgv.ShowCellToolTips = false;
        dgv.addFocusListener(this.dgv_CellEnter);
        dgv.getModel().addTableModelListener(this.dgv_DataError);
        //
        // printableCheckColumn
        //
//        resources.ApplyResources(this.printableCheckColumn, "printableCheckColumn");
        this.printableCheckColumn.setName("printableCheckColumn");
        //
        // partNameColumn
        //
//        resources.ApplyResources(this.partNameColumn, "partNameColumn");
        this.partNameColumn.setName("partNameColumn");
//        this.partNameColumn.SortMode = JTableColumnSortMode.NotSortable;
        //
        // midiTrackNumberColumn
        //
//        resources.ApplyResources(this.midiTrackNumberColumn, "midiTrackNumberColumn");
        this.midiTrackNumberColumn.setName("midiTrackNumberColumn");
        this.midiTrackNumberColumn.setEditable(false);
//        this.midiTrackNumberColumn.SortMode = JTableColumnSortMode.NotSortable;
        //
        // midiTrackNameColumn
        //
//        resources.ApplyResources(this.midiTrackNameColumn, "midiTrackNameColumn");
        this.midiTrackNameColumn.setName("midiTrackNameColumn");
        this.midiTrackNameColumn.setEditable(false);
//        this.midiTrackNameColumn.SortMode = JTableColumnSortMode.NotSortable;
        //
        // IsEnptyColumn
        //
//        resources.ApplyResources(this.IsEnptyColumn, "IsEnptyColumn");
        this.IsEnptyColumn.setName("IsEnptyColumn");
        this.IsEnptyColumn.setEditable(false);
        //
        // soundModuleColumn
        //
//        this.soundModuleColumn.DisplayStyle = JTableComboBoxDisplayStyle.Nothing;
//        this.soundModuleColumn.FlatStyle = JFlatStyle.Flat;
//        resources.ApplyResources(this.soundModuleColumn, "soundModuleColumn");
        this.soundModuleColumn.setName("soundModuleColumn");
//        this.soundModuleColumn.Resizable = JTableTriState.True;
        //
        // spaceColumn
        //
//        this.spaceColumn.AutoSizeMode = JTableAutoSizeColumnMode.Fill;
//        dataGridViewCellStyle1.SelectionBackColor = Color.Transparent;
//        dataGridViewCellStyle1.SelectionForeColor = SystemColors.ControlText;
//        this.spaceColumn.DefaultCellStyle = dataGridViewCellStyle1;
//        resources.ApplyResources(this.spaceColumn, "spaceColumn");
        this.spaceColumn.setName("spaceColumn");
        this.spaceColumn.setEditable(false);
//        this.spaceColumn.SortMode = JTableColumnSortMode.NotSortable;
        //
        // PartDataGridView
        //
//        resources.ApplyResources(this, "$this");
//        this.AutoScaleMode = JAutoScaleMode.Font;
        this.add(this.dgv);
        this.setName("PartDataGridView");
//        ((System.ComponentModel.ISupportInitialize) (this.dgv)).EndInit();
//        this.ResumeLayout(false);
    }

    DefaultTableModel dtm;

//#endregion

    private JTable dgv;
    private JCheckBox printableCheckColumn;
    private JTextField partNameColumn;
    private JTextField midiTrackNumberColumn;
    private JTextField midiTrackNameColumn;
    private JTextField IsEnptyColumn;
    private JComboBox<String> soundModuleColumn;
    private JTextField spaceColumn;
}
