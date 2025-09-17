/*
 * Copyright © 2017-2019 Rerrah
 *
 * MIDI to MML Converter
 */

package ConvFMML.Form;


    public partial class TimeBaseForm extends System.Windows.Forms.Form
    {
        public decimal Timebase
        {
            get
            {
                return numericUpDown1.Value;
            }
        }

        public TimeBaseForm(decimal timebase)
        {
            InitializeComponent();

            numericUpDown1.Value = timebase;
        }

        private void settingButton_Click(Object sender, EventArgs e)
        {
            Close();
        }
    }
}
