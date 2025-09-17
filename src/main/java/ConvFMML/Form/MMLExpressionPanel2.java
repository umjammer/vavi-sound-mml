/*
 * Copyright © 2017-2019 Rerrah
 *
 * MIDI to MML Converter
 */

package ConvFMML.Form;


    public partial class MMLExpressionPanel2 extends BasePanel
    {
        private Settings.MMLExpression settings;

        public MMLExpressionPanel2(Settings.MMLExpression settings)
        {
            InitializeComponent();

            this.settings = settings;
        }

        @Override protected  void OnLoad(EventArgs e)
        {
            super.OnLoad(e);

            comboBox1.SelectedIndex = settings.TitleEnable;
            UpdatePanel2(settings.MMLStyle);
            checkBox1.Checked = settings.UseTabAfterPartName;
        }

        @Override public  void UpdateSelections(MMLStyle mmlStyle)
        {
            UpdatePanel2(mmlStyle);
        }

        private void UpdatePanel2(MMLStyle mmlStyle)
        {
            switch (mmlStyle)
            {
                case MMLStyle.Custom:
                case MMLStyle.FMP:
                    panel2.Enabled = false;
                    break;
                default:
                    panel2.Enabled = true;
                    break;
            }
        }

        private void comboBox1_SelectionChangeCommitted(Object sender, EventArgs e)
        {
            var cb = (ComboBox)sender;
            settings.TitleEnable = cb.SelectedIndex;
        }

        private void CheckBox1_CheckedChanged(Object sender, EventArgs e)
        {
            var cb = (CheckBox)sender;
            settings.UseTabAfterPartName = cb.Checked;
        }
    }
}
