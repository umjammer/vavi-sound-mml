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
import javax.swing.JComboBox;
import javax.swing.JLabel;

import convfmml.Settings;


public class ControlCommandGenericPanel extends BasePanel {

    private Settings.ControlCommand.Generic settings;

    public ControlCommandGenericPanel(Settings.ControlCommand.Generic settings) {
        InitializeComponent();

        this.settings = settings;
    }

    protected void onLoad(ComponentEvent e) {

        comboBox1.setSelectedIndex(settings.getInvalid());
        comboBox2.setSelectedIndex(settings.getSamePosition());
        comboBox3.setSelectedIndex(settings.getPredeclared());
    }

    private void comboBox1_SelectionChangeCommitted(ItemEvent e) {
        var cb = (JComboBox<?>) e.getSource();
        settings.setInvalid(cb.getSelectedIndex());
    }

    private void comboBox2_SelectionChangeCommitted(ItemEvent e) {
        var cb = (JComboBox<?>) e.getSource();
        settings.setSamePosition(cb.getSelectedIndex());
    }

    private void comboBox3_SelectionChangeCommitted(ItemEvent e) {
        var cb = (JComboBox<?>) e.getSource();
        settings.setPredeclared(cb.getSelectedIndex());
    }

//#region Windows Form Designer generated code

    private void InitializeComponent() {
//        System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(ControlCommandGenericPanel));
        this.comboBox1 = new JComboBox<>();
        this.label1 = new JLabel();
        this.comboBox2 = new JComboBox<>();
        this.label3 = new JLabel();
        this.comboBox3 = new JComboBox<>();
        this.label4 = new JLabel();
        this.label2 = new JLabel();
//        this.panel1.SuspendLayout();
//        this.SuspendLayout();
        //
        // titleLabel
        //
//        resources.ApplyResources(this.titleLabel, "titleLabel");
        //
        // panel1
        //
        this.panel1.add(this.label2);
        this.panel1.add(this.comboBox3);
        this.panel1.add(this.label4);
        this.panel1.add(this.comboBox2);
        this.panel1.add(this.label3);
        this.panel1.add(this.comboBox1);
        this.panel1.add(this.label1);
        //
        // comboBox1
        //
//        this.comboBox1.DropDownStyle = JComboBoxStyle.DropDownList;
//        this.comboBox1.FormattingEnabled = true;
        DefaultComboBoxModel<String> cbm1 = new DefaultComboBoxModel<>();
        cbm1.addAll(List.of()); // TODO
        this.comboBox1.setModel(cbm1);
//                resources.GetString("comboBox1.Items"),
//                resources.GetString("comboBox1.Items1")});
//        resources.ApplyResources(this.comboBox1, "comboBox1");
        this.comboBox1.setName("comboBox1");
        this.comboBox1.addItemListener(this::comboBox1_SelectionChangeCommitted);
        //
        // label1
        //
//        resources.ApplyResources(this.label1, "label1");
        this.label1.setName("label1");
        //
        // comboBox2
        //
//        this.comboBox2.DropDownStyle = JComboBoxStyle.DropDownList;
//        this.comboBox2.FormattingEnabled = true;
        DefaultComboBoxModel<String> cbm2 = new DefaultComboBoxModel<>();
        cbm2.addAll(List.of()); // TODO
        this.comboBox2.setModel(cbm2);
//        this.comboBox2.Items.addRange(new Object[] {
//                resources.GetString("comboBox2.Items"),
//                resources.GetString("comboBox2.Items1")});
//        resources.ApplyResources(this.comboBox2, "comboBox2");
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
//        this.comboBox3.DropDownStyle = JComboBoxStyle.DropDownList;
//        this.comboBox3.FormattingEnabled = true;
        DefaultComboBoxModel<String> cbm3 = new DefaultComboBoxModel<>();
        cbm3.addAll(List.of()); // TODO
        this.comboBox3.setModel(cbm3);
//        this.comboBox3.Items.addRange(new Object[] {
//                resources.GetString("comboBox3.Items"),
//                resources.GetString("comboBox3.Items1")});
//        resources.ApplyResources(this.comboBox3, "comboBox3");
        this.comboBox3.setName("comboBox3");
        this.comboBox3.addItemListener(this::comboBox3_SelectionChangeCommitted);
        //
        // label4
        //
//        resources.ApplyResources(this.label4, "label4");
        this.label4.setName("label4");
        //
        // label2
        //
//        resources.ApplyResources(this.label2, "label2");
        this.label2.setName("label2");
        //
        // ControlCommandGenericPanel
        //
//        resources.ApplyResources(this, "$this");
//        this.AutoScaleMode = JAutoScaleMode.Font;
        this.setName("ControlCommandGenericPanel");
//        this.panel1.ResumeLayout(false);
//        this.panel1.PerformLayout();
//        this.ResumeLayout(false);
    }

//#endregion

    private JComboBox<String> comboBox3;
    private JLabel label4;
    private JComboBox<String> comboBox2;
    private JLabel label3;
    private JComboBox<String> comboBox1;
    private JLabel label1;
    private JLabel label2;
}
