/*
 * Copyright © 2017-2019 Rerrah
 *
 * MIDI to MML Converter
 */

package convfmml.form;

import java.awt.event.ItemEvent;
import java.util.List;
import java.util.ResourceBundle;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.event.ChangeEvent;

import convfmml.Common.MMLStyle;
import convfmml.Settings;
import convfmml.data.intermediate.NotesStatus;


public class OutputPartPanel extends BasePanel {

    static final ResourceBundle rb = ResourceBundle.getBundle("messages");

    private Settings.OutputPart settings;
    private MMLStyle mmlStyle;

    public OutputPartPanel(Settings.OutputPart settings, MMLStyle mmlStyle) {
        InitializeComponent();

        this.settings = settings;
        this.mmlStyle = mmlStyle;

        partDataGridView1.setSettings(settings);
        partDataGridView1.setMmlStyle(mmlStyle);
    }

    @Override
    public void updateSelections(MMLStyle mmlStyle) {
        this.mmlStyle = mmlStyle;

        comboBox1.removeAllItems();
        if (this.mmlStyle == MMLStyle.Custom) {
            cbm1.addAll(List.of(rb.getString("PartDisabled"), rb.getString("PartCustom")));
            comboBox1.setSelectedIndex(settings.getPrintStyleCustom());
        } else {
            cbm1.addAll(List.of(rb.getString("PartDisabled"), rb.getString("PartCustom"), rb.getString("PartAuto")));
            comboBox1.setSelectedIndex(settings.getPrintStyle());
        }

        if (comboBox1.getSelectedIndex() == 2) {
            if (this.mmlStyle == MMLStyle.MXDRV || this.mmlStyle == MMLStyle.MUCOM88) {
                label2.setEnabled(false);
                comboBox2.setEnabled(false);
            } else {
                label2.setEnabled(true);
                comboBox2.setEnabled(true);
            }
        } else {
            label2.setEnabled(false);
            comboBox2.setEnabled(false);
        }

        comboBox2.removeAllItems();
        switch (this.mmlStyle) {
            case MMLStyle.FMP7:
                cbm2.addAll(List.of(
                        "ABC...XYZ",
                        "A0B0C0...X0Y0Z0A1B1C1...",
                        "A0A1A2...A7A8A9B0B1B2..."
                ));
                comboBox2.setSelectedIndex(settings.getAutoNameFMP7());
                break;
            case MMLStyle.FMP:
                cbm2.addAll(List.of(
                        "FM: A～C | SSG: D～F | FM3ch: X～Z",
                        "FM: A～C | SSG: D～F | FM: G～I | FM3ch: X～Z"
                ));
                comboBox2.setSelectedIndex(settings.getAutoNameFMP());
                break;
            case MMLStyle.PMD:
                cbm2.addAll(List.of(
                        "FM: A～C | SSG: G～I",
                        "FM: A～C | FM3ch: D～F | SSG: G～I",
                        "FM: A～F | SSG: G～I | FM3ch: X～Z",
                        "FM: A～F",
                        "FM: A～H",
                        "FM: A～I"
                ));
                comboBox2.setSelectedIndex(settings.getAutoNamePMD());
                break;
            case MMLStyle.NRTDRV:
                cbm2.addAll(List.of(
                        "SSG: 1～3",
                        "FM: A～P | SSG:  1～3",
                        "FM: A～H | SSG:  1～3 | FM: I～P"
                ));
                comboBox2.setSelectedIndex(settings.getAutoNameNRTDRV());
                break;
            case MMLStyle.Mml2vgm:
                cbm2.addAll(List.of(
                        "FM: F1～F6   | SSG: S1～S4",
                        "FM: F1～F6   | FM3ch: F7～F9   | SSG: S1～S4",
                        "FM: E01～E06 | SSG: S1～S4",
                        "FM: E01～E06 | FM3ch: E07～E09 | SSG: S1～S4",
                        "SSG: S1～S4",
                        "FM: T01～T06 | SSG: T10～T12",
                        "FM: T01～T06 | FM3ch: T07～T09 | SSG: T10～T12",
                        "SSG: T10～T12",
                        "FM: P01～P06 | SSG: P10～P12",
                        "FM: P01～T06 | FM3ch: P07～P09 | SSG: P10～P12",
                        "SSG: P10～P12",
                        "FM: N1～N3   | SSG: N7～N9",
                        "FM: N1～N3   | FM3ch: N4～N6   | SSG: N7～N9",
                        "SSG: N7～N9",
                        "FM: X1～X8",
                        "SSG: A1～A3",
                        "FM: L01～L09"
                ));
                comboBox2.setSelectedIndex(settings.getAutoNameMml2vgm());
                break;
            default:
                break;
        }

        checkBox1.setSelected(settings.isRemoveEmptyParts());

        changeOutputPartMMLSyle(mmlStyle);
    }

    @Override
    public void loadMusicData(List<NotesStatus> notesStatusList) {
        partDataGridView1.setDataSource(notesStatusList);
        partDataGridView1.ShowData();
    }

    public void changeOutputPartMMLSyle(MMLStyle style) {
        partDataGridView1.setMmlStyle(style);
        partDataGridView1.ShowData();
    }

    public List<NotesStatus> getOutputPartSettings() {
        return partDataGridView1.getDataSource();
    }

    private void comboBox1_SelectionChangeCommitted(ItemEvent e) {
        var cb = (JComboBox<?>) e.getSource();
        if (mmlStyle == MMLStyle.Custom) {
            settings.setPrintStyleCustom(cb.getSelectedIndex());
        } else {
            settings.setPrintStyle(cb.getSelectedIndex());
        }

        if (cb.getSelectedIndex() == 0) {
            label2.setEnabled(false);
            comboBox2.setEnabled(false);
        } else if (cb.getSelectedIndex() == 1) {
            label2.setEnabled(false);
            comboBox2.setEnabled(false);
        } else {
            if (mmlStyle == MMLStyle.MXDRV || mmlStyle == MMLStyle.MUCOM88) {
                label2.setEnabled(false);
                comboBox2.setEnabled(false);
            } else {
                label2.setEnabled(true);
                comboBox2.setEnabled(true);
            }
        }

        partDataGridView1.ShowData();
    }

    private void comboBox2_SelectionChangeCommitted(ItemEvent e) {
        var cb = (JComboBox<?>) e.getSource();
        switch (mmlStyle) {
            case MMLStyle.FMP7:
                settings.setAutoNameFMP7(cb.getSelectedIndex());
                break;
            case MMLStyle.FMP:
                settings.setAutoNameFMP(cb.getSelectedIndex());
                break;
            case MMLStyle.PMD:
                settings.setAutoNamePMD(cb.getSelectedIndex());
                break;
            case MMLStyle.NRTDRV:
                settings.setAutoNameNRTDRV(cb.getSelectedIndex());
                break;
            case MMLStyle.Mml2vgm:
                settings.setAutoNameMml2vgm(cb.getSelectedIndex());
                break;
            default:
                break;
        }

        partDataGridView1.ShowData();
    }

    private void checkBox1_CheckedChanged(ChangeEvent e) {
        var cb = (JCheckBox) e.getSource();
        settings.setRemoveEmptyParts(cb.isSelected());
        partDataGridView1.ShowData();
    }

//#region Windows Form Designer generated code

    private void InitializeComponent() {
//        System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(OutputPartPanel));
        this.label3 = new JLabel();
        this.comboBox2 = new JComboBox<>();
        this.label2 = new JLabel();
        this.comboBox1 = new JComboBox<>();
        this.label1 = new JLabel();
        this.checkBox1 = new JCheckBox();
        this.partDataGridView1 = new convfmml.form.PartDataGridView();
//        this.panel1.SuspendLayout();
//        this.SuspendLayout();
        //
        // titleLabel
        //
//        resources.ApplyResources(this.titleLabel, "titleLabel");
        //
        // panel1
        //
//        resources.ApplyResources(this.panel1, "panel1");
        this.panel1.add(this.partDataGridView1);
        this.panel1.add(this.checkBox1);
        this.panel1.add(this.label3);
        this.panel1.add(this.comboBox2);
        this.panel1.add(this.label2);
        this.panel1.add(this.comboBox1);
        this.panel1.add(this.label1);
        //
        // label3
        //
//        resources.ApplyResources(this.label3, "label3");
        this.label3.setName("label3");
        //
        // comboBox2
        //
//        resources.ApplyResources(this.comboBox2, "comboBox2");
//        this.comboBox2.DropDownStyle = JComboBoxStyle.DropDownList;
//        this.comboBox2.FormattingEnabled = true;
        cbm2 = new DefaultComboBoxModel<>();
        this.comboBox2.setModel(cbm2);
        this.comboBox2.setName("comboBox2");
        this.comboBox2.addItemListener(this::comboBox2_SelectionChangeCommitted);
        //
        // label2
        //
//        resources.ApplyResources(this.label2, "label2");
        this.label2.setName("label2");
        //
        // comboBox1
        //
//        resources.ApplyResources(this.comboBox1, "comboBox1");
//        this.comboBox1.DropDownStyle = JComboBoxStyle.DropDownList;
//        this.comboBox1.FormattingEnabled = true;
        cbm1 = new DefaultComboBoxModel<>();
        this.comboBox1.setModel(cbm1);
        this.comboBox1.setName("comboBox1");
        this.comboBox1.addItemListener(this::comboBox1_SelectionChangeCommitted);
        //
        // label1
        //
//        resources.ApplyResources(this.label1, "label1");
        this.label1.setName("label1");
        //
        // checkBox1
        //
//        resources.ApplyResources(this.checkBox1, "checkBox1");
        this.checkBox1.setName("checkBox1");
//        this.checkBox1.UseVisualStyleBackColor = true;
        this.checkBox1.addChangeListener(this::checkBox1_CheckedChanged);
        //
        // partDataGridView1
        //
//        resources.ApplyResources(this.partDataGridView1, "partDataGridView1");
//        this.partDataGridView1.DataSource = null;
//        this.partDataGridView1.MMLStyle = convfmml.MMLStyle.FMP7;
        this.partDataGridView1.setName("partDataGridView1");
//        this.partDataGridView1.Settings = null;
        //
        // OutputPartPanel
        //
//        resources.ApplyResources(this, "$this");
//        this.AutoScaleMode = JAutoScaleMode.Font;
        this.setName("OutputPartPanel");
//        this.panel1.ResumeLayout(false);
//        this.panel1.PerformLayout();
//        this.ResumeLayout(false);
    }

    DefaultComboBoxModel<String> cbm1;
    DefaultComboBoxModel<String> cbm2;

    //#endregion

    private JLabel label3;
    private JComboBox<String> comboBox2;
    private JLabel label2;
    private JComboBox<String> comboBox1;
    private JLabel label1;
    private JCheckBox checkBox1;
    private PartDataGridView partDataGridView1;
}

