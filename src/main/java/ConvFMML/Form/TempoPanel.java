/*
 * Copyright © 2017-2019 Rerrah
 *
 * MIDI to MML Converter
 */

package ConvFMML.Form;


    public partial class TempoPanel extends BasePanel
    {
        private Settings.ControlCommand.Tempo settings;

        public TempoPanel(Settings.ControlCommand.Tempo settings)
        {
            InitializeComponent();

            this.settings = settings;
        }

        @Override protected  void OnLoad(EventArgs e)
        {
            super.OnLoad(e);

            checkBox1.Checked = settings.Enable;
            textBox1.Text = settings.CommandCustom;
        }

        @Override public  void UpdateSelections(MMLStyle mmlStyle)
        {
            panel2.Enabled = checkBox1.Checked;
            panel3.Enabled = (mmlStyle == MMLStyle.Custom);
        }

        private void checkBox1_CheckedChanged(Object sender, EventArgs e)
        {
            var cb = (CheckBox)sender;
            settings.Enable = cb.Checked;
            panel2.Enabled = cb.Checked;
        }

        private void textBox1_Leave(Object sender, EventArgs e)
        {
            var tb = (TextBox)sender;
            settings.CommandCustom = tb.Text;
        }
    }
}
