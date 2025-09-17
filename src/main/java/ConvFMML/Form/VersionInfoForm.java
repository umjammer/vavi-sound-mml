/*
 * Copyright © 2017-2019 Rerrah
 *
 * MIDI to MML Converter
 */

package ConvFMML.Form;


    public partial class VersionInfoForm extends System.Windows.Forms.Form
    {
        public VersionInfoForm()
        {
            InitializeComponent();
        }

        @Override protected  void OnLoad(EventArgs e)
        {
            super.OnLoad(e);

            descriptionLabel.Text = Common.AssemblyDescription;
            titleLabel.Text = Common.AssemblyTitle;
            versionLabel.Text = "Version " + Common.AssemblyFileVersion;
            copyrightLabel.Text = Common.AssemblyCopyright;
            iconPictureBox.Image = Common.Icon.ToBitmap();
        }

        private void okButton_Click(Object sender, EventArgs e)
        {
            Close();
        }
    }
}
