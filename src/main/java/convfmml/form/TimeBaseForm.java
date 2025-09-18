/*
 * Copyright © 2017-2019 Rerrah
 *
 * MIDI to MML Converter
 */

package convfmml.form;

import java.awt.event.ActionEvent;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;


public class TimeBaseForm extends JDialog {

    int dialogResult;

    public int getTimebase() {
        if (numericUpDown1.getValue() instanceof Number number)
            return number.intValue();
        else
            throw new AssertionError();
    }

    public TimeBaseForm(int timebase) {
        InitializeComponent();

        numericUpDown1.setValue(timebase);
    }

    private void settingButton_Click(ActionEvent e) {
        if (e.getSource() == settingButton)
            dialogResult = JOptionPane.OK_OPTION;
        else
            dialogResult = JOptionPane.CANCEL_OPTION;

        setVisible(false);
    }

//#region Windows Form Designer generated code

    private void InitializeComponent() {
//        System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(TimeBaseForm));
        this.numericUpDown1 = new JSpinner();
        this.label1 = new JLabel();
        this.settingButton = new JButton();
        this.cancelButton = new JButton();
//        ((System.ComponentModel.ISupportInitialize) (this.numericUpDown1)).BeginInit();
//        this.SuspendLayout();
        //
        // numericUpDown1
        //
//        resources.ApplyResources(this.numericUpDown1, "numericUpDown1");
        SpinnerNumberModel snm = new SpinnerNumberModel(192, 0, 7680, 1);
        this.numericUpDown1.setName("numericUpDown1");
        this.numericUpDown1.setModel(snm);
        //
        // label1
        //
//        resources.ApplyResources(this.label1, "label1");
        this.label1.setName("label1");
        //
        // settingButton
        //
//        resources.ApplyResources(this.settingButton, "settingButton");
//        this.settingButton.DialogResult = JDialogResult.OK;
        this.settingButton.setName("settingButton");
//        this.settingButton.UseVisualStyleBackColor = true;
        this.settingButton.addActionListener(this::settingButton_Click);
        //
        // cancelButton
        //
//        resources.ApplyResources(this.cancelButton, "cancelButton");
//        this.cancelButton.DialogResult = JDialogResult.Cancel;
        this.cancelButton.setName("cancelButton");
//        this.cancelButton.UseVisualStyleBackColor = true;
        this.cancelButton.addActionListener(this::settingButton_Click);
        //
        // TimeBaseForm
        //
//        this.AcceptButton = this.settingButton;
//        resources.ApplyResources(this, "$this");
//        this.AutoScaleMode = JAutoScaleMode.Font;
//        this.CancelButton = this.cancelButton;
        this.getContentPane().add(this.cancelButton);
        this.getContentPane().add(this.settingButton);
        this.getContentPane().add(this.numericUpDown1);
        this.getContentPane().add(this.label1);
//        this.FormBorderStyle = JFormBorderStyle.FixedDialog;
//        this.MaximizeBox = false;
//        this.MinimizeBox = false;
        this.setName("TimeBaseForm");
//        this.ShowInTaskbar = false;
//        ((System.ComponentModel.ISupportInitialize) (this.numericUpDown1)).EndInit();
//        this.ResumeLayout(false);
    }

//#endregion

    private JSpinner numericUpDown1;
    private JLabel label1;
    private JButton settingButton;
    private JButton cancelButton;
}

