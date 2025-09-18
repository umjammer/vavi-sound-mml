/*
 * Copyright © 2017-2019 Rerrah
 *
 * MIDI to MML Converter
 */

package convfmml.form;

import java.awt.event.ActionEvent;
import java.awt.event.WindowListener;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import convfmml.Common;


public class VersionInfoForm extends JFrame {

    ImageIcon image;

    public VersionInfoForm() {
        InitializeComponent();
    }

    protected void OnLoad(WindowListener e) {

        descriptionLabel.setText(Common.getAssemblyDescription());
        titleLabel.setText(Common.getAssemblyTitle());
        versionLabel.setText("Version " + Common.getAssemblyFileVersion());
        copyrightLabel.setText(Common.getAssemblyCopyright());
        image = Common.getIcon();
    }

    private void okButton_Click(ActionEvent e) {
        setVisible(false);
    }

//#region Windows Form Designer generated code

    private void InitializeComponent() {
//        System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(VersionInfoForm));
        this.descriptionLabel = new JLabel();
        this.titleLabel = new JLabel();
        this.versionLabel = new JLabel();
        this.copyrightLabel = new JLabel();
        this.okButton = new JButton();
        this.iconPictureBox = new JPanel();
//        ((System.ComponentModel.ISupportInitialize) (this.iconPictureBox)).BeginInit();
//        this.SuspendLayout();
        //
        // descriptionLabel
        //
//        resources.ApplyResources(this.descriptionLabel, "descriptionLabel");
        this.descriptionLabel.setName("descriptionLabel");
        //
        // titleLabel
        //
//        resources.ApplyResources(this.titleLabel, "titleLabel");
        this.titleLabel.setName("titleLabel");
        //
        // versionLabel
        //
//        resources.ApplyResources(this.versionLabel, "versionLabel");
        this.versionLabel.setName("versionLabel");
        //
        // copyrightLabel
        //
//        resources.ApplyResources(this.copyrightLabel, "copyrightLabel");
        this.copyrightLabel.setName("copyrightLabel");
        //
        // okButton
        //
//        resources.ApplyResources(this.okButton, "okButton");
//        this.okButton.DialogResult = JDialogResult.OK;
        this.okButton.setName("okButton");
//        this.okButton.UseVisualStyleBackColor = true;
        this.okButton.addActionListener(this::okButton_Click);
        //
        // iconPictureBox
        //
//        resources.ApplyResources(this.iconPictureBox, "iconPictureBox");
        this.iconPictureBox.setName("iconPictureBox");
//        this.iconPictureBox.TabStop = false;
        //
        // VersionInfoForm
        //
//        this.AcceptButton = this.okButton;
//        resources.ApplyResources(this, "$this");
//        this.AutoScaleMode = JAutoScaleMode.Font;
        this.getContentPane().add(this.iconPictureBox);
        this.getContentPane().add(this.okButton);
        this.getContentPane().add(this.copyrightLabel);
        this.getContentPane().add(this.versionLabel);
        this.getContentPane().add(this.titleLabel);
        this.getContentPane().add(this.descriptionLabel);
//        this.FormBorderStyle = JFormBorderStyle.FixedDialog;
//        this.MaximizeBox = false;
//        this.MinimizeBox = false;
        this.setName("VersionInfoForm");
//        this.ShowInTaskbar = false;
//        ((System.ComponentModel.ISupportInitialize) (this.iconPictureBox)).EndInit();
//        this.ResumeLayout(false);
    }

//#endregion

    private JLabel descriptionLabel;
    private JLabel titleLabel;
    private JLabel versionLabel;
    private JLabel copyrightLabel;
    private JButton okButton;
    private JPanel iconPictureBox;
}
