/*
 * Copyright © 2017-2019 Rerrah
 *
 * MIDI to MML Converter
 */

package ConvFMML.Form;


    public partial class MMLExpressionPanel1 extends BasePanel
    {
        private Settings.MMLExpression settings;
        public event EventHandler TimeBaseFormButtonClick;
        public event EventHandler MMLStyleChanged;

        public MMLExpressionPanel1(Settings.MMLExpression settings)
        {
            InitializeComponent();

            this.settings = settings;
        }

        @Override protected  void OnLoad(EventArgs e)
        {
            super.OnLoad(e);

            comboBox1.SelectedIndex = (int)settings.MMLStyle;
            UpdateComboBox2();

            textBox1.Text = settings.TimeBase.toString();
            comboBox3.SelectedIndex = settings.NewBlockByBar;
            numericUpDown2.Value = settings.NewLineBarCount;
            comboBox4.SelectedIndex = settings.NewLineByTimeSignature;
        }

        @Override public  void UpdateSelections(MMLStyle mmlStyle) { }

        @Override public  void LoadMusicData(List<NotesStatus> statusList)
        {
            textBox1.Text = settings.TimeBase.toString();
        }

        private void UpdateComboBox2()
        {
            comboBox2.Items.Clear();
            if (settings.MMLStyle == MMLStyle.PMD)
            {
                comboBox2.Items.addRange(new string[] { Resources.Disabled, Resources.TimeBasePMDZenlen, Resources.TimeBasePMDC });
                comboBox2.SelectedIndex = settings.PrintTimeBasePMD;
            }
            else
            {
                comboBox2.Items.addRange(new string[] { Resources.Disabled, Resources.Enabled });
                comboBox2.SelectedIndex = settings.PrintTimeBase;
            }
        }

        private void comboBox1_SelectionChangeCommitted(Object sender, EventArgs e)
        {
            var cb = (ComboBox)sender;
            settings.MMLStyle = (MMLStyle)cb.SelectedIndex;
            UpdateComboBox2();
            MMLStyleChanged.Invoke(this, EventArgs.Empty);
        }

        private void button1_Click(Object sender, EventArgs e)
        {
            TimeBaseFormButtonClick.Invoke(this, EventArgs.Empty);
        }

        private void comboBox2_SelectionChangeCommitted(Object sender, EventArgs e)
        {
            var cb = (ComboBox)sender;
            if (settings.MMLStyle == MMLStyle.PMD)
            {
                settings.PrintTimeBasePMD = comboBox2.SelectedIndex;
            }
            else
            {
                settings.PrintTimeBase = comboBox2.SelectedIndex;
            }
        }

        private void comboBox3_SelectionChangeCommitted(Object sender, EventArgs e)
        {
            var cb = (ComboBox)sender;
            settings.NewBlockByBar = cb.SelectedIndex;
        }

        private void numericUpDown2_Leave(Object sender, EventArgs e)
        {
            var nud = (NumericUpDown)sender;
            settings.NewLineBarCount = nud.Value;
        }

        private void comboBox4_SelectionChangeCommitted(Object sender, EventArgs e)
        {
            var cb = (ComboBox)sender;
            settings.NewLineByTimeSignature = cb.SelectedIndex;
        }
    }
}
