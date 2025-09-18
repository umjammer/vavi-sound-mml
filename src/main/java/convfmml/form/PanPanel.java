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
import java.util.ResourceBundle;
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


public class PanPanel extends BasePanel {

    static final ResourceBundle rb = ResourceBundle.getBundle("messages");

    private final Settings.ControlCommand.Pan settings;
    private MMLStyle mmlStyle;

    public PanPanel(Settings.ControlCommand.Pan settings, MMLStyle mmlStyle) {
        InitializeComponent();

        this.settings = settings;
        this.mmlStyle = mmlStyle;
    }

    protected void onLoad(ComponentEvent e) {

        checkBox1.setSelected(settings.isEnable());
        // Load FMP and Custom Command in UpdateSelections
        textBox1.setText(settings.getMidiCommandCustom());
        textBox2.setText(settings.getLeftCommandCustom());
        textBox3.setText(settings.getCenterCommandCustom());
        textBox4.setText(settings.getRightCommandCustom());
        checkBox2.setSelected(settings.isBorderUsingNegative());

        if (checkBox2.isSelected()) {
            snm1.setMinimum(-64);
            snm1.setMaximum(settings.getBorderRight().intValue() - 65);
            snm2.setMinimum(settings.getBorderLeft().intValue() - 63);
            snm2.setMaximum(63);
            snm1.setValue(settings.getBorderLeft().intValue() - 64);
            snm2.setValue(settings.getBorderRight().intValue() - 64);
        } else {
            snm1.setMinimum(0);
            snm1.setMaximum(settings.getBorderRight().intValue() - 1);
            snm2.setMinimum(settings.getBorderLeft().intValue() + 1);
            snm2.setMaximum(127);
            snm1.setValue(settings.getBorderLeft());
            snm2.setValue(settings.getBorderRight());
        }

        checkBox2.addItemListener(this::checkBox2_CheckedChanged);
        numericUpDown1.addChangeListener(this::numericUpDown1_ValueChanged);
        numericUpDown2.addChangeListener(this::numericUpDown2_ValueChanged);
    }

    @Override
    public void updateSelections(MMLStyle mmlStyle) {
        this.mmlStyle = mmlStyle;

        panel2.setEnabled(checkBox1.isSelected());

        groupBox1.setEnabled(false);
        comboBox1.removeAllItems();
        switch (mmlStyle) {
            case MMLStyle.FMP7:
                cbm1.addAll(List.of(rb.getString("PanP"), rb.getString("PanPLPCPR")));
                comboBox1.setSelectedIndex(settings.getCommandFMP7());
                groupBox1.setEnabled(true);
                label2.setEnabled(false);
                textBox1.setEnabled(false);
                label3.setEnabled(false);
                textBox2.setEnabled(false);
                label4.setEnabled(false);
                textBox3.setEnabled(false);
                label5.setEnabled(false);
                textBox4.setEnabled(false);
                groupBox2.setEnabled(false);
                break;
            case MMLStyle.Custom:
                cbm1.addAll(List.of(rb.getString("PanMIDI"), rb.getString("PanLCR")));
                comboBox1.setSelectedIndex(settings.getCommandCustom());
                groupBox1.setEnabled(true);
                if (comboBox1.getSelectedIndex() == 0) {
                    label2.setEnabled(true);
                    textBox1.setEnabled(true);
                    label3.setEnabled(false);
                    textBox2.setEnabled(false);
                    label4.setEnabled(false);
                    textBox3.setEnabled(false);
                    label5.setEnabled(false);
                    textBox4.setEnabled(false);
                    groupBox2.setEnabled(false);
                } else if (comboBox1.getSelectedIndex() == 1) {
                    label2.setEnabled(false);
                    textBox1.setEnabled(false);
                    label3.setEnabled(true);
                    textBox2.setEnabled(true);
                    label4.setEnabled(true);
                    textBox3.setEnabled(true);
                    label5.setEnabled(true);
                    textBox4.setEnabled(true);
                    groupBox2.setEnabled(true);
                }
                break;
            default:
                groupBox2.setEnabled(true);
                break;
        }
    }

    private void checkBox1_CheckedChanged(ItemEvent e) {
        var cb = (JCheckBox) e.getSource();
        settings.setEnable(cb.isSelected());

        switch (mmlStyle) {
            case MMLStyle.FMP7:
                panel2.setEnabled(cb.isSelected());
                if (panel2.isEnabled()) {
                    groupBox1.setEnabled(true);
                    label2.setEnabled(false);
                    textBox1.setEnabled(false);
                    label3.setEnabled(false);
                    textBox2.setEnabled(false);
                    label4.setEnabled(false);
                    textBox3.setEnabled(false);
                    label5.setEnabled(false);
                    textBox4.setEnabled(false);
                    groupBox2.setEnabled(false);
                }
                break;
            case MMLStyle.Custom:
                panel2.setEnabled(cb.isSelected());
                if (panel2.isEnabled()) {
                    groupBox1.setEnabled(true);
                    if (comboBox1.getSelectedIndex() == 0) {
                        label2.setEnabled(true);
                        textBox1.setEnabled(true);
                        label3.setEnabled(false);
                        textBox2.setEnabled(false);
                        label4.setEnabled(false);
                        textBox3.setEnabled(false);
                        label5.setEnabled(false);
                        textBox4.setEnabled(false);
                        groupBox2.setEnabled(false);
                    } else if (comboBox1.getSelectedIndex() == 1) {
                        label2.setEnabled(false);
                        textBox1.setEnabled(false);
                        label3.setEnabled(true);
                        textBox2.setEnabled(true);
                        label4.setEnabled(true);
                        textBox3.setEnabled(true);
                        label5.setEnabled(true);
                        textBox4.setEnabled(true);
                        groupBox2.setEnabled(true);
                    }
                }
                break;
            default:
                panel2.setEnabled(checkBox1.isSelected());
                groupBox1.setEnabled(false);
                break;
        }
    }

    private void comboBox1_SelectionChangeCommitted(ItemEvent e) {
        var cb = (JComboBox) e.getSource();
        switch (mmlStyle) {
            case MMLStyle.FMP7:
                settings.setCommandFMP7(cb.getSelectedIndex());
                break;
            case MMLStyle.Custom:
                settings.setCommandCustom(cb.getSelectedIndex());
                if (cb.getSelectedIndex() == 0) {
                    label2.setEnabled(true);
                    textBox1.setEnabled(true);
                    label3.setEnabled(false);
                    textBox2.setEnabled(false);
                    label4.setEnabled(false);
                    textBox3.setEnabled(false);
                    label5.setEnabled(false);
                    textBox4.setEnabled(false);
                    groupBox2.setEnabled(false);
                } else if (cb.getSelectedIndex() == 1) {
                    label2.setEnabled(false);
                    textBox1.setEnabled(false);
                    label3.setEnabled(true);
                    textBox2.setEnabled(true);
                    label4.setEnabled(true);
                    textBox3.setEnabled(true);
                    label5.setEnabled(true);
                    textBox4.setEnabled(true);
                    groupBox2.setEnabled(true);
                }
                break;
            default:
                break;
        }
    }

    private final FocusListener textBox1_Leave = new FocusAdapter() {
        @Override
        public void focusLost(FocusEvent e) {
            var tb = (JTextField) e.getComponent();
            settings.setMidiCommandCustom(tb.getText());
        }
    };

    private final FocusListener textBox2_Leave = new FocusAdapter() {
        @Override
        public void focusLost(FocusEvent e) {
            var tb = (JTextField) e.getComponent();
            settings.setLeftCommandCustom(tb.getText());
        }
    };

    private final FocusListener textBox3_Leave = new FocusAdapter() {
        @Override
        public void focusLost(FocusEvent e) {
            var tb = (JTextField) e.getComponent();
            settings.setCenterCommandCustom(tb.getText());
        }
    };

    private final FocusListener textBox4_Leave = new FocusAdapter() {
        @Override
        public void focusLost(FocusEvent e) {
            var tb = (JTextField) e.getComponent();
            settings.setRightCommandCustom(tb.getText());
        }
    };

    private void numericUpDown1_ValueChanged(ChangeEvent e) {
        var nud = (JSpinner) e.getSource();
        if (checkBox2.isSelected()) {
            settings.setBorderLeft(new BigDecimal((int) nud.getValue() + 64));
        } else {
            settings.setBorderLeft(new BigDecimal((int) nud.getValue()));
        }
        snm2.setMinimum((int) nud.getValue() + 1);
    }

    private void numericUpDown2_ValueChanged(ChangeEvent e) {
        var nud = (JSpinner) e.getSource();
        if (checkBox2.isSelected()) {
            settings.setBorderRight(new BigDecimal((int) nud.getValue() + 64));
        } else {
            settings.setBorderRight(new BigDecimal((int) nud.getValue()));
        }
        snm1.setMaximum((int) nud.getValue() - 1);
    }

    private void checkBox2_CheckedChanged(ItemEvent e) {
        var cb = (JCheckBox) e.getSource();
        settings.setBorderUsingNegative(cb.isSelected());

        if (cb.isSelected()) {
            snm1.setMinimum(((Number) snm1.getMinimum()).intValue() - 64);
            snm1.setValue(((Number) snm1.getValue()).intValue() - 64);
            snm1.setMaximum(((Number) snm1.getMaximum()).intValue() - 64);
            snm2.setMinimum(((Number) snm2.getMinimum()).intValue() - 64);
            snm2.setValue(((Number) snm2.getValue()).intValue() - 64);
            snm2.setMaximum(((Number) snm2.getMaximum()).intValue() - 64);
        } else {
            snm2.setMaximum(((Number) snm2.getMaximum()).intValue() + 64);
            snm2.setValue(((Number) snm2.getValue()).intValue() + 64);
            snm2.setMinimum(((Number) snm2.getMinimum()).intValue() + 64);
            snm1.setMaximum(((Number) snm1.getMaximum()).intValue() + 64);
            snm1.setValue(((Number) snm1.getValue()).intValue() + 64);
            snm1.setMinimum(((Number) snm1.getMinimum()).intValue() + 64);
        }
    }

//#region Windows Form Designer generated code

    private void InitializeComponent() {
//        System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(PanPanel));
        this.panel2 = new JPanel();
        this.groupBox2 = new JPanel();
        this.checkBox2 = new JCheckBox();
        this.numericUpDown2 = new JSpinner();
        this.label7 = new JLabel();
        this.label6 = new JLabel();
        this.numericUpDown1 = new JSpinner();
        this.groupBox1 = new JPanel();
        this.textBox4 = new JTextField();
        this.label5 = new JLabel();
        this.textBox3 = new JTextField();
        this.label4 = new JLabel();
        this.textBox2 = new JTextField();
        this.label3 = new JLabel();
        this.textBox1 = new JTextField();
        this.label2 = new JLabel();
        this.comboBox1 = new JComboBox<>();
        this.label1 = new JLabel();
        this.checkBox1 = new JCheckBox();
//        this.panel1.SuspendLayout();
//        this.panel2.SuspendLayout();
//        this.groupBox2.SuspendLayout();
//        ((System.ComponentModel.ISupportInitialize) (this.numericUpDown2)).BeginInit();
//        ((System.ComponentModel.ISupportInitialize) (this.numericUpDown1)).BeginInit();
//        this.groupBox1.SuspendLayout();
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
        // panel2
        //
//        resources.ApplyResources(this.panel2, "panel2");
        this.panel2.add(this.groupBox2);
        this.panel2.add(this.groupBox1);
        this.panel2.setName("panel2");
        //
        // groupBox2
        //
//        resources.ApplyResources(this.groupBox2, "groupBox2");
        this.groupBox2.add(this.checkBox2);
        this.groupBox2.add(this.numericUpDown2);
        this.groupBox2.add(this.label7);
        this.groupBox2.add(this.label6);
        this.groupBox2.add(this.numericUpDown1);
        this.groupBox2.setName("groupBox2");
//        this.groupBox2.TabStop = false;
        //
        // checkBox2
        //
//        resources.ApplyResources(this.checkBox2, "checkBox2");
        this.checkBox2.setName("checkBox2");
//        this.checkBox2.UseVisualStyleBackColor = true;
        //
        // numericUpDown2
        //
//        resources.ApplyResources(this.numericUpDown2, "numericUpDown2");
        snm2 = new SpinnerNumberModel(95, 0, 127, 1);
        this.numericUpDown2.setModel(snm2);
        this.numericUpDown2.setName("numericUpDown2");
        //
        // label7
        //
//        resources.ApplyResources(this.label7, "label7");
        this.label7.setName("label7");
        //
        // label6
        //
//        resources.ApplyResources(this.label6, "label6");
        this.label6.setName("label6");
        //
        // numericUpDown1
        //
//        resources.ApplyResources(this.numericUpDown1, "numericUpDown1");
        snm1 = new SpinnerNumberModel(33, 0, 127, 1);
        this.numericUpDown1.setModel(snm1);
        this.numericUpDown1.setName("numericUpDown1");
        //
        // groupBox1
        //
//        resources.ApplyResources(this.groupBox1, "groupBox1");
        this.groupBox1.add(this.textBox4);
        this.groupBox1.add(this.label5);
        this.groupBox1.add(this.textBox3);
        this.groupBox1.add(this.label4);
        this.groupBox1.add(this.textBox2);
        this.groupBox1.add(this.label3);
        this.groupBox1.add(this.textBox1);
        this.groupBox1.add(this.label2);
        this.groupBox1.add(this.comboBox1);
        this.groupBox1.add(this.label1);
        this.groupBox1.setName("groupBox1");
//        this.groupBox1.TabStop = false;
        //
        // textBox4
        //
//        resources.ApplyResources(this.textBox4, "textBox4");
        this.textBox4.setName("textBox4");
        this.textBox4.addFocusListener(this.textBox4_Leave);
        //
        // label5
        //
//        resources.ApplyResources(this.label5, "label5");
        this.label5.setName("label5");
        //
        // textBox3
        //
//        resources.ApplyResources(this.textBox3, "textBox3");
        this.textBox3.setName("textBox3");
        this.textBox3.addFocusListener(this.textBox3_Leave);
        //
        // label4
        //
//        resources.ApplyResources(this.label4, "label4");
        this.label4.setName("label4");
        //
        // textBox2
        //
//        resources.ApplyResources(this.textBox2, "textBox2");
        this.textBox2.setName("textBox2");
        this.textBox2.addFocusListener(this.textBox2_Leave);
        //
        // label3
        //
//        resources.ApplyResources(this.label3, "label3");
        this.label3.setName("label3");
        //
        // textBox1
        //
//        resources.ApplyResources(this.textBox1, "textBox1");
        this.textBox1.setName("textBox1");
        this.textBox1.addFocusListener(this.textBox1_Leave);
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
        this.checkBox1.setSelected(true);
//        this.checkBox1.CheckState = JCheckState.isSelected();
        this.checkBox1.setName("checkBox1");
//        this.checkBox1.UseVisualStyleBackColor = true;
        this.checkBox1.addItemListener(this::checkBox1_CheckedChanged);
        //
        // PanPanel
        //
//        resources.ApplyResources(this, "$this");
//        this.AutoScaleMode = JAutoScaleMode.Font;
        this.setName("PanPanel");
//        this.panel1.ResumeLayout(false);
//        this.panel1.PerformLayout();
//        this.panel2.ResumeLayout(false);
//        this.groupBox2.ResumeLayout(false);
//        this.groupBox2.PerformLayout();
//        ((System.ComponentModel.ISupportInitialize) (this.numericUpDown2)).EndInit();
//        ((System.ComponentModel.ISupportInitialize) (this.numericUpDown1)).EndInit();
//        this.groupBox1.ResumeLayout(false);
//        this.groupBox1.PerformLayout();
//        this.ResumeLayout(false);
    }

    SpinnerNumberModel snm1;
    SpinnerNumberModel snm2;
    DefaultComboBoxModel<String> cbm1;

//#endregion

    private JPanel panel2;
    private JPanel groupBox2;
    private JPanel groupBox1;
    private JTextField textBox4;
    private JLabel label5;
    private JTextField textBox3;
    private JLabel label4;
    private JTextField textBox2;
    private JLabel label3;
    private JTextField textBox1;
    private JLabel label2;
    private JComboBox<String> comboBox1;
    private JLabel label1;
    private JCheckBox checkBox1;
    private JLabel label7;
    private JLabel label6;
    private JSpinner numericUpDown1;
    private JCheckBox checkBox2;
    private JSpinner numericUpDown2;
}

