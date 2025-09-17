/*
 * Copyright © 2017-2019 Rerrah
 *
 * MIDI to MML Converter
 */

package ConvFMML.Form;


    public partial class ControlCommandGenericPanel extends BasePanel
    {
        private Settings.ControlCommand.Generic settings;

        public ControlCommandGenericPanel(Settings.ControlCommand.Generic settings)
        {
            InitializeComponent();

            this.settings = settings;
        }

        @Override protected  void OnLoad(EventArgs e)
        {
            super.OnLoad(e);

            comboBox1.SelectedIndex = settings.Invalid;
            comboBox2.SelectedIndex = settings.SamePosition;
            comboBox3.SelectedIndex = settings.Predeclared;
        }

        private void comboBox1_SelectionChangeCommitted(Object sender, EventArgs e)
        {
            var cb = (ComboBox)sender;
            settings.Invalid = cb.SelectedIndex;
        }

        private void comboBox2_SelectionChangeCommitted(Object sender, EventArgs e)
        {
            var cb = (ComboBox)sender;
            settings.SamePosition = cb.SelectedIndex;
        }

        private void comboBox3_SelectionChangeCommitted(Object sender, EventArgs e)
        {
            var cb = (ComboBox)sender;
            settings.Predeclared = cb.SelectedIndex;
        }
    }
}
