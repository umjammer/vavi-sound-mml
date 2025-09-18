/*
 * Copyright © 2017-2019 Rerrah
 *
 * MIDI to MML Converter
 */

package convfmml.form;

import java.awt.event.ComponentEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.ItemEvent;
import java.math.BigDecimal;
import java.util.List;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;

import convfmml.Common.MMLStyle;
import convfmml.Settings;


public class NoteRestPanel1 extends BasePanel {

    private Settings.NoteRest settings;

    public NoteRestPanel1(Settings.NoteRest settings) {
        InitializeComponent();

        this.settings = settings;
    }

    protected void OnLoad(ComponentEvent e) {

        // Exclude HeaderCheckBox from GroupBox
        if (checkBox1.getParent() == groupBox1) {
            groupBox1.getParent().add(checkBox1);
            checkBox1.setLocation(checkBox1.getX() + groupBox1.getX(), checkBox1.getY() + groupBox1.getY());
            checkBox1.getParent().setComponentZOrder(checkBox1, 0);
        }

        comboBox1.setSelectedIndex(settings.getOctaveInNewLine());
        textBox1.setText(settings.getOctaveCommandCustom());
        comboBox2.setSelectedIndex(settings.getOctaveDirection());
        comboBox3.setSelectedIndex(settings.getLengthStyle());
        checkBox1.setSelected(settings.isDotEnable());
        numericUpDown1.setValue(settings.getDotLength());
        numericUpDown2.setValue(settings.getDefaultLength());
    }

    @Override
    public void updateSelections(MMLStyle mmlstyle) {
        if (mmlstyle == MMLStyle.Custom) {
            label2.setEnabled(true);
            textBox1.setEnabled(true);
        } else {
            label2.setEnabled(false);
            textBox1.setEnabled(false);
        }

        switch (mmlstyle) {
            case MMLStyle.MXDRV:
            case MMLStyle.NRTDRV:
            case MMLStyle.PMD:
            case MMLStyle.Mml2vgm:
            case MMLStyle.Custom:
                label3.setEnabled(true);
                comboBox2.setEnabled(true);
                break;
            default:
                label3.setEnabled(false);
                comboBox2.setEnabled(false);
                break;
        }

        if (mmlstyle == MMLStyle.MUCOM88) {
            label6.setEnabled(false);
            numericUpDown1.setEnabled(false);
            label5.setEnabled(false);
        } else {
            label6.setEnabled(true);
            numericUpDown1.setEnabled(true);
            label5.setEnabled(true);
        }
        groupBox1.setEnabled(checkBox1.isSelected());
    }

    private void comboBox1_SelectionChangeCommitted(ItemEvent e) {
        var cb = (JComboBox) e.getSource();
        settings.setOctaveInNewLine(cb.getSelectedIndex());
    }

    private final FocusListener textBox1_Leave = new FocusAdapter() {
        @Override
        public void focusLost(FocusEvent e) {
            var tb = (JTextField) e.getComponent();
            settings.setOctaveCommandCustom(tb.getText());
        }
    };

    private void comboBox2_SelectionChangeCommitted(ItemEvent e) {
        var cb = (JComboBox) e.getSource();
        settings.setOctaveDirection(cb.getSelectedIndex());
    }

    private void comboBox3_SelectionChangeCommitted(ItemEvent e) {
        var cb = (JComboBox) e.getSource();
        settings.setLengthStyle(cb.getSelectedIndex());
    }

    private void checkBox1_CheckedChanged(ItemEvent e) {
        var cb = (JCheckBox) e.getSource();
        settings.setDotEnable(cb.isSelected());
        groupBox1.setEnabled(cb.isSelected());
    }

    private final FocusListener numericUpDown1_Leave = new FocusAdapter() {
        @Override
        public void focusLost(FocusEvent e) {
            var nud = (JSpinner) e.getSource();
            settings.setDotLength(new BigDecimal((int) nud.getValue()));
        }
    };

    private void numericUpDown2_ValueChanged(ChangeEvent e) {
        var nud = (JSpinner) e.getSource();
        settings.setDefaultLength(new BigDecimal((int) nud.getValue()));
    }

//#region Windows Form Designer generated code

    private void InitializeComponent() {
//        System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(NoteRestPanel1));
        this.comboBox1 = new JComboBox<>();
        this.label1 = new JLabel();
        this.label2 = new JLabel();
        this.textBox1 = new JTextField();
        this.comboBox2 = new JComboBox<>();
        this.label3 = new JLabel();
        this.comboBox3 = new JComboBox<>();
        this.label4 = new JLabel();
        this.groupBox1 = new JPanel();
        this.label5 = new JLabel();
        this.numericUpDown1 = new JSpinner();
        this.label6 = new JLabel();
        this.checkBox1 = new JCheckBox();
        this.label7 = new JLabel();
        this.numericUpDown2 = new JSpinner();
//        this.panel1.SuspendLayout();
//        this.groupBox1.SuspendLayout();
//        ((System.ComponentModel.ISupportInitialize) (this.numericUpDown1)).BeginInit();
//        ((System.ComponentModel.ISupportInitialize) (this.numericUpDown2)).BeginInit();
//        this.SuspendLayout();
        //
        // titleLabel
        //
//        resources.ApplyResources(this.titleLabel, "titleLabel");
        //
        // panel1
        //
//        resources.ApplyResources(this.panel1, "panel1");
        this.panel1.add(this.label7);
        this.panel1.add(this.numericUpDown2);
        this.panel1.add(this.groupBox1);
        this.panel1.add(this.comboBox3);
        this.panel1.add(this.label4);
        this.panel1.add(this.comboBox2);
        this.panel1.add(this.label3);
        this.panel1.add(this.textBox1);
        this.panel1.add(this.label2);
        this.panel1.add(this.comboBox1);
        this.panel1.add(this.label1);
        //
        // comboBox1
        //
//        resources.ApplyResources(this.comboBox1, "comboBox1");
//        this.comboBox1.DropDownStyle = JComboBoxStyle.DropDownList;
//        this.comboBox1.FormattingEnabled = true;
        DefaultComboBoxModel<String> cbm1 = new DefaultComboBoxModel<>();
        cbm1.addAll(List.of(
                "Relative",
                "Absolute"));
        this.comboBox1.setModel(cbm1);
        this.comboBox1.setName("comboBox1");
        this.comboBox1.addItemListener(this::comboBox1_SelectionChangeCommitted);
        //
        // label1
        //
//        resources.ApplyResources(this.label1, "label1");
        this.label1.setName("label1");
        //
        // label2
        //
//        resources.ApplyResources(this.label2, "label2");
        this.label2.setName("label2");
        //
        // textBox1
        //
//        resources.ApplyResources(this.textBox1, "textBox1");
        this.textBox1.setName("textBox1");
        this.textBox1.addFocusListener(this.textBox1_Leave);
        //
        // comboBox2
        //
//        resources.ApplyResources(this.comboBox2, "comboBox2");
//        this.comboBox2.DropDownStyle = JComboBoxStyle.DropDownList;
//        this.comboBox2.FormattingEnabled = true;
        DefaultComboBoxModel<String> cbm2 = new DefaultComboBoxModel<>();
        cbm2.addAll(List.of(
                ">: Octave up",
                "<: Octave down"));
        this.comboBox2.setModel(cbm2);
        this.comboBox2.setName("comboBox2");
        this.comboBox2.addItemListener(this::comboBox2_SelectionChangeCommitted);
        //
        // label3
        //
//        resources.ApplyResources(this.label3, "label3");
        this.label3.setName("label3");
        //
        // comboBox3
        //
//        resources.ApplyResources(this.comboBox3, "comboBox3");
//        this.comboBox3.DropDownStyle = JComboBoxStyle.DropDownList;
//        this.comboBox3.FormattingEnabled = true;
        DefaultComboBoxModel<String> cbm3 = new DefaultComboBoxModel<>();
        cbm3.addAll(List.of(
                "Shortest",
                "2^n length based"));
        this.comboBox3.setModel(cbm3);
        this.comboBox3.setName("comboBox3");
        this.comboBox3.addItemListener(this::comboBox3_SelectionChangeCommitted);
        //
        // label4
        //
//        resources.ApplyResources(this.label4, "label4");
        this.label4.setName("label4");
        //
        // groupBox1
        //
//        resources.ApplyResources(this.groupBox1, "groupBox1");
        this.groupBox1.add(this.label5);
        this.groupBox1.add(this.numericUpDown1);
        this.groupBox1.add(this.label6);
        this.groupBox1.add(this.checkBox1);
        this.groupBox1.setName("groupBox1");
//        this.groupBox1.TabStop = false;
        //
        // label5
        //
//        resources.ApplyResources(this.label5, "label5");
        this.label5.setName("label5");
        //
        // numericUpDown1
        //
//        resources.ApplyResources(this.numericUpDown1, "numericUpDown1");
        this.numericUpDown1.setName("numericUpDown1");
        this.numericUpDown1.addFocusListener(this.numericUpDown1_Leave);
        //
        // label6
        //
//        resources.ApplyResources(this.label6, "label6");
        this.label6.setName("label6");
        //
        // checkBox1
        //
//        resources.ApplyResources(this.checkBox1, "checkBox1");
        this.checkBox1.setSelected(true);
//        this.checkBox1.CheckState = JCheckState.isSelected();
        this.checkBox1.setName("checkBox1");
//        this.checkBox1.UseVisualStyleBackColor = true;
        this.checkBox1.addItemListener(this::checkBox1_CheckedChanged);
        //
        // label7
        //
//        resources.ApplyResources(this.label7, "label7");
        this.label7.setName("label7");
        //
        // numericUpDown2
        //
//        resources.ApplyResources(this.numericUpDown2, "numericUpDown2");
        SpinnerNumberModel snm2 = new SpinnerNumberModel(8, 0, 100, 1);
        this.numericUpDown2.setModel(snm2);
        this.numericUpDown2.setName("numericUpDown2");
//        this.numericUpDown2.setValue(new decimal(new int[] {
//                8,
//                0,
//                0,
//                0});
        this.numericUpDown2.addChangeListener(this::numericUpDown2_ValueChanged);
        this.numericUpDown2.addFocusListener(this.numericUpDown1_Leave);
        //
        // NoteRestPanel1
        //
//        resources.ApplyResources(this, "$this");
//        this.AutoScaleMode = JAutoScaleMode.Font;
        this.setName("NoteRestPanel1");
//        this.panel1.ResumeLayout(false);
//        this.panel1.PerformLayout();
//        this.groupBox1.ResumeLayout(false);
//        this.groupBox1.PerformLayout();
//        ((System.ComponentModel.ISupportInitialize) (this.numericUpDown1)).EndInit();
//        ((System.ComponentModel.ISupportInitialize) (this.numericUpDown2)).EndInit();
//        this.ResumeLayout(false);
    }

//#endregion

    private JComboBox<String> comboBox1;
    private JLabel label1;
    private JTextField textBox1;
    private JLabel label2;
    private JComboBox<String> comboBox2;
    private JLabel label3;
    private JComboBox<String> comboBox3;
    private JLabel label4;
    private JPanel groupBox1;
    private JLabel label5;
    private JSpinner numericUpDown1;
    private JLabel label6;
    private JCheckBox checkBox1;
    private JLabel label7;
    private JSpinner numericUpDown2;
}

