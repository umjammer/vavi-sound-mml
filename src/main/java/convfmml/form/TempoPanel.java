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
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;

import convfmml.Common.MMLStyle;
import convfmml.Settings;


public class TempoPanel extends BasePanel {

    private Settings.ControlCommand.Tempo settings;

    public TempoPanel(Settings.ControlCommand.Tempo settings) {
        InitializeComponent();

        this.settings = settings;
    }

    protected void OnLoad(ComponentEvent e) {

        checkBox1.setSelected(settings.isEnable());
        textBox1.setText(settings.getCommandCustom());
    }

    @Override
    public void updateSelections(MMLStyle mmlStyle) {
        panel2.setEnabled(checkBox1.isSelected());
        panel3.setEnabled((mmlStyle == MMLStyle.Custom));
    }

    private void checkBox1_CheckedChanged(ChangeEvent e) {
        var cb = (JCheckBox) e.getSource();
        settings.setEnable(cb.isSelected());
        panel2.setEnabled(cb.isSelected());
    }

    private final FocusListener textBox1_Leave = new FocusAdapter() {
        @Override
        public void focusLost(FocusEvent e) {
            var tb = (JTextField) e.getComponent();
            settings.setCommandCustom(tb.getText());
        }
    };

//#region Windows Form Designer generated code

    private void InitializeComponent() {
//        System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(TempoPanel));
        this.textBox1 = new JTextField();
        this.label2 = new JLabel();
        this.checkBox1 = new JCheckBox();
        this.panel3 = new JPanel();
        this.panel2 = new JPanel();
//        this.panel1.SuspendLayout();
//        this.panel3.SuspendLayout();
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
        this.panel1.add(this.panel2);
        this.panel1.add(this.checkBox1);
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
        // checkBox1
        //
//        resources.ApplyResources(this.checkBox1, "checkBox1");
        this.checkBox1.setSelected(true);
//        this.checkBox1.setSelected(JCheckState.isSelected());
        this.checkBox1.setName("checkBox1");
//        this.checkBox1.UseVisualStyleBackColor = true;
        this.checkBox1.addChangeListener(this::checkBox1_CheckedChanged);
        //
        // panel3
        //
//        resources.ApplyResources(this.panel3, "panel3");
        this.panel3.add(this.label2);
        this.panel3.add(this.textBox1);
        this.panel3.setName("panel3");
        //
        // panel2
        //
//        resources.ApplyResources(this.panel2, "panel2");
        this.panel2.add(this.panel3);
        this.panel2.setName("panel2");
        //
        // TempoPanel
        //
//        resources.ApplyResources(this, "$this");
//        this.AutoScaleMode = JAutoScaleMode.Font;
        this.setName("TempoPanel");
//        this.panel1.ResumeLayout(false);
//        this.panel1.PerformLayout();
//        this.panel3.ResumeLayout(false);
//        this.panel3.PerformLayout();
//        this.panel2.ResumeLayout(false);
//        this.ResumeLayout(false);
    }

//#endregion

    private JTextField textBox1;
    private JLabel label2;
    private JCheckBox checkBox1;
    private JPanel panel2;
    private JPanel panel3;
}
