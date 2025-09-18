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


public class VolumePanel extends BasePanel {

    private Settings.ControlCommand.Volume settings;
    private MMLStyle mmlStyle;

    public VolumePanel(Settings.ControlCommand.Volume settings, MMLStyle mmlStyle) {
        InitializeComponent();

        this.settings = settings;
        this.mmlStyle = mmlStyle;
    }

    protected void OnLoad(ComponentEvent e) {

        checkBox1.setSelected(settings.isEnable());
        textBox1.setText(settings.getCommandCustom());
        // Load PMD, MXDRV and NRTDRV Command in UpdateSelections
        numericUpDown1.setValue(settings.getRangeCustom());
        numericUpDown2.setValue(settings.getVStep());
    }

    @Override
    public void updateSelections(MMLStyle mmlStyle) {
        this.mmlStyle = mmlStyle;

        panel2.setEnabled(checkBox1.isSelected());

        if (this.mmlStyle == MMLStyle.Custom) {
            comboBox1.setVisible(false);
            textBox1.setVisible(true);
            panel3.setEnabled(true);
            panel4.setEnabled(false);
        } else {
            comboBox1.setVisible(true);
            textBox1.setVisible(false);
            comboBox1.removeAllItems();
            switch (this.mmlStyle) {
                case MMLStyle.FMP7:
                case MMLStyle.FMP:
                case MMLStyle.MUCOM88:
                case MMLStyle.Mml2vgm:
                    panel2.setEnabled(false);
                    break;
                case MMLStyle.PMD:
                    cbm1.addAll(List.of("v", "V"));
                    comboBox1.setSelectedIndex(settings.getCommandPMD());
                    panel3.setEnabled(false);
                    panel4.setEnabled(false);
                    break;
                case MMLStyle.MXDRV:
                    cbm1.addAll(List.of("v", "@v"));
                    comboBox1.setSelectedIndex(settings.getCommandMXDRV());
                    panel3.setEnabled(false);
                    panel4.setEnabled(false);
                    break;
                case MMLStyle.NRTDRV:
                    cbm1.addAll(List.of("v", "V"));
                    comboBox1.setSelectedIndex(settings.getCommandNRTDRV());
                    panel3.setEnabled(false);
                    panel4.setEnabled((comboBox1.getSelectedIndex() == 0));
                    break;
                default:
                    break;
            }
        }
    }

    private void checkBox1_CheckedChanged(ChangeEvent e) {
        var cb = (JCheckBox) e.getSource();
        settings.setEnable(cb.isSelected());

        switch (mmlStyle) {
            case MMLStyle.FMP7:
            case MMLStyle.FMP:
            case MMLStyle.MUCOM88:
            case MMLStyle.Mml2vgm:
                break;
            case MMLStyle.PMD:
            case MMLStyle.MXDRV:
                panel2.setEnabled(cb.isSelected());
                if (panel2.isEnabled()) {
                    panel3.setEnabled(false);
                    panel4.setEnabled(false);
                }
                break;
            case MMLStyle.NRTDRV:
                panel2.setEnabled(cb.isSelected());
                if (panel2.isEnabled()) {
                    panel3.setEnabled(false);
                    panel4.setEnabled((comboBox1.getSelectedIndex() == 0));
                }
                break;
            case MMLStyle.Custom:
                panel2.setEnabled(cb.isSelected());
                if (panel2.isEnabled()) {
                    panel3.setEnabled(true);
                    panel4.setEnabled(false);
                }
                break;
        }
    }

    private final FocusListener textBox1_Leave = new FocusAdapter() {
        @Override
        public void focusLost(FocusEvent e) {
            var tb = (JTextField) e.getComponent();
            settings.setCommandCustom(tb.getText());
        }
    };

    private void comboBox1_SelectionChangeCommitted(ItemEvent e) {
        var cb = (JComboBox) e.getSource();
        switch (mmlStyle) {
            case MMLStyle.PMD:
                settings.setCommandPMD(cb.getSelectedIndex());
                return;
            case MMLStyle.MXDRV:
                settings.setCommandMXDRV(cb.getSelectedIndex());
                return;
            case MMLStyle.NRTDRV:
                settings.setCommandNRTDRV(cb.getSelectedIndex());
                panel4.setEnabled((cb.getSelectedIndex() == 0));
                return;
            default:
                return;
        }
    }

    private final FocusListener numericUpDown1_Leave = new FocusAdapter() {
        @Override
        public void focusLost(FocusEvent e) {
            var nud = (JSpinner) e.getComponent();
            settings.setRangeCustom(new BigDecimal((int) nud.getValue()));
        }
    };

    private final FocusListener numericUpDown2_Leave = new FocusAdapter() {
        @Override
        public void focusLost(FocusEvent e) {
            var nud = (JSpinner) e.getComponent();
            settings.setVStep(new BigDecimal((int) nud.getValue()));
        }
    };

//#region Windows Form Designer generated code

    private void InitializeComponent() {
//        System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(VolumePanel));
        this.checkBox1 = new JCheckBox();
        this.panel2 = new JPanel();
        this.panel4 = new JPanel();
        this.label4 = new JLabel();
        this.numericUpDown2 = new JSpinner();
        this.panel3 = new JPanel();
        this.label2 = new JLabel();
        this.numericUpDown1 = new JSpinner();
        this.label3 = new JLabel();
        this.comboBox1 = new JComboBox<>();
        this.textBox1 = new JTextField();
        this.label1 = new JLabel();
//        this.panel1.SuspendLayout();
//        this.panel2.SuspendLayout();
//        this.panel4.SuspendLayout();
//        ((System.ComponentModel.ISupportInitialize) (this.numericUpDown2)).BeginInit();
//        this.panel3.SuspendLayout();
//        ((System.ComponentModel.ISupportInitialize) (this.numericUpDown1)).BeginInit();
//        this.SuspendLayout();
        //
        // titleLabel
        //
//        resources.ApplyResources(this.titleLabel, "titleLabel");
        //
        // panel1
        //
//        resources.ApplyResources(this.panel1, "panel1");
        this.panel1.add(this.panel2);
        this.panel1.add(this.checkBox1);
        //
        // checkBox1
        //
//        resources.ApplyResources(this.checkBox1, "checkBox1");
        this.checkBox1.setSelected(true);
//        this.checkBox1.setSelected(JCheckState.isSelected());
        this.checkBox1.setName("checkBox1");
//        this.checkBox1.UseVisualStyleBackColor = true;
        this.checkBox1.addChangeListener(this::checkBox1_CheckedChanged);
        //
        // panel2
        //
//        resources.ApplyResources(this.panel2, "panel2");
        this.panel2.add(this.panel4);
        this.panel2.add(this.panel3);
        this.panel2.add(this.comboBox1);
        this.panel2.add(this.textBox1);
        this.panel2.add(this.label1);
        this.panel2.setName("panel2");
        //
        // panel4
        //
//        resources.ApplyResources(this.panel4, "panel4");
        this.panel4.add(this.label4);
        this.panel4.add(this.numericUpDown2);
        this.panel4.setName("panel4");
        //
        // label4
        //
//        resources.ApplyResources(this.label4, "label4");
        this.label4.setName("label4");
        //
        // numericUpDown2
        //
//        resources.ApplyResources(this.numericUpDown2, "numericUpDown2");
        SpinnerNumberModel snm2 = new SpinnerNumberModel(15, 1, 255, 1);
        this.numericUpDown2.setModel(snm2);
        this.numericUpDown2.setName("numericUpDown2");
        this.numericUpDown2.addFocusListener(this.numericUpDown2_Leave);
        //
        // panel3
        //
//        resources.ApplyResources(this.panel3, "panel3");
        this.panel3.add(this.label2);
        this.panel3.add(this.numericUpDown1);
        this.panel3.add(this.label3);
        this.panel3.setName("panel3");
        //
        // label2
        //
//        resources.ApplyResources(this.label2, "label2");
        this.label2.setName("label2");
        //
        // numericUpDown1
        //
//        resources.ApplyResources(this.numericUpDown1, "numericUpDown1");
        SpinnerNumberModel snm1 = new SpinnerNumberModel(0, 0, 127, 1);
        this.numericUpDown1.setModel(snm1);
        this.numericUpDown1.setName("numericUpDown1");
        this.numericUpDown1.addFocusListener(this.numericUpDown1_Leave);
        //
        // label3
        //
//        resources.ApplyResources(this.label3, "label3");
        this.label3.setName("label3");
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
        // textBox1
        //
//        resources.ApplyResources(this.textBox1, "textBox1");
        this.textBox1.setName("textBox1");
        this.textBox1.addFocusListener(this.textBox1_Leave);
        //
        // label1
        //
//        resources.ApplyResources(this.label1, "label1");
        this.label1.setName("label1");
        //
        // VolumePanel
        //
//        resources.ApplyResources(this, "$this");
//        this.AutoScaleMode = JAutoScaleMode.Font;
        this.setName("VolumePanel");
//        this.panel1.ResumeLayout(false);
//        this.panel1.PerformLayout();
//        this.panel2.ResumeLayout(false);
//        this.panel2.PerformLayout();
//        this.panel4.ResumeLayout(false);
//        ((System.ComponentModel.ISupportInitialize) (this.numericUpDown2)).EndInit();
//        this.panel3.ResumeLayout(false);
//        this.panel3.PerformLayout();
//        ((System.ComponentModel.ISupportInitialize) (this.numericUpDown1)).EndInit();
//        this.ResumeLayout(false);
    }

    DefaultComboBoxModel<String> cbm1;

//#endregion

    private JCheckBox checkBox1;
    private JPanel panel2;
    private JTextField textBox1;
    private JLabel label1;
    private JLabel label3;
    private JSpinner numericUpDown1;
    private JLabel label2;
    private JComboBox<String> comboBox1;
    private JPanel panel3;
    private JLabel label4;
    private JSpinner numericUpDown2;
    private JPanel panel4;
}
