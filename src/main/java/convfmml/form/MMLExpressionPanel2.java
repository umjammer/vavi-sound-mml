/*
 * Copyright © 2017-2019 Rerrah
 *
 * MIDI to MML Converter
 */

package convfmml.form;

import java.awt.event.ComponentEvent;
import java.awt.event.ItemEvent;
import java.util.List;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;

import convfmml.Common.MMLStyle;
import convfmml.Settings;


public class MMLExpressionPanel2 extends BasePanel {

    private Settings.MMLExpression settings;

    public MMLExpressionPanel2(Settings.MMLExpression settings) {
        InitializeComponent();

        this.settings = settings;
    }

    protected void onLoad(ComponentEvent e) {

        comboBox1.setSelectedIndex(settings.getTitleEnable());
        UpdatePanel2(settings.getMmlStyle());
        checkBox1.setSelected(settings.isUseTabAfterPartName());
    }

    @Override
    public void updateSelections(MMLStyle mmlStyle) {
        UpdatePanel2(mmlStyle);
    }

    private void UpdatePanel2(MMLStyle mmlStyle) {
        switch (mmlStyle) {
            case MMLStyle.Custom:
            case MMLStyle.FMP:
                panel2.setEnabled(false);
                break;
            default:
                panel2.setEnabled(true);
                break;
        }
    }

    private void comboBox1_SelectionChangeCommitted(ItemEvent e) {
        var cb = (JComboBox<?>) e.getSource();
        settings.setTitleEnable(cb.getSelectedIndex());
    }

    private void CheckBox1_CheckedChanged(ChangeEvent e) {
        var cb = (JCheckBox) e.getSource();
        settings.setUseTabAfterPartName(cb.isSelected());
    }

//#region Windows Form Designer generated code

    private void InitializeComponent() {
//        System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(MMLExpressionPanel2));
        this.comboBox1 = new JComboBox<>();
        this.label1 = new JLabel();
        this.panel2 = new JPanel();
        this.checkBox1 = new JCheckBox();
//        this.panel1.SuspendLayout();
//        this.panel2.SuspendLayout();
//        this.SuspendLayout();
        //
        // titleLabel
        //
//        resources.ApplyResources(this.titleLabel, "titleLabel");
        //
        // panel1
        //
//        resources.ApplyResources(this.panel1, "panel1");
        this.panel1.add(this.checkBox1);
        this.panel1.add(this.panel2);
        //
        // comboBox1
        //
//        resources.ApplyResources(this.comboBox1, "comboBox1");
//        this.comboBox1.DropDownStyle = JComboBoxStyle.DropDownList;
//        this.comboBox1.FormattingEnabled = true;
        DefaultComboBoxModel<String> cbm1 = new DefaultComboBoxModel<>();
        cbm1.addAll(List.of(
                "Disabled",
                "Enabled"));
        this.comboBox1.setModel(cbm1);
        this.comboBox1.setName("comboBox1");
        this.comboBox1.addItemListener(this::comboBox1_SelectionChangeCommitted);
        //
        // label1
        //
//        resources.ApplyResources(this.label1, "label1");
        this.label1.setName("label1");
        //
        // panel2
        //
//        resources.ApplyResources(this.panel2, "panel2");
        this.panel2.add(this.label1);
        this.panel2.add(this.comboBox1);
        this.panel2.setName("panel2");
        //
        // checkBox1
        //
//        resources.ApplyResources(this.checkBox1, "checkBox1");
        this.checkBox1.setName("checkBox1");
//        this.checkBox1.UseVisualStyleBackColor = true;
        this.checkBox1.addChangeListener(this::CheckBox1_CheckedChanged);
        //
        // MMLExpressionPanel2
        //
//        resources.ApplyResources(this, "$this");
//        this.AutoScaleMode = JAutoScaleMode.Font;
        this.setName("MMLExpressionPanel2");
//        this.panel1.ResumeLayout(false);
//        this.panel1.PerformLayout();
//        this.panel2.ResumeLayout(false);
//        this.ResumeLayout(false);
    }

//#endregion

    private JComboBox<String> comboBox1;
    private JLabel label1;
    private JPanel panel2;
    private JCheckBox checkBox1;
}

