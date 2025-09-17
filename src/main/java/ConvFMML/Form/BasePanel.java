/*
 * Copyright © 2017-2019 Rerrah
 *
 * MIDI to MML Converter
 */

package ConvFMML.Form;


import java.awt.Color;
import java.awt.Graphics;
import java.util.List;

import ConvFMML.Common.MMLStyle;
import ConvFMML.Data.Intermediate.NotesStatus;


public  class BasePanel extends UserControl
    {
        public BasePanel()
        {
            InitializeComponent();
        }

        @Override protected  void OnLoad(EventArgs e)
        {
            super.OnLoad(e);

            var canvas = new Bitmap(titleLabel.Width, titleLabel.Height);
            try (Graphics g = Graphics.FromImage(canvas))
            try (LinearGradientBrush lgb = new LinearGradientBrush(g.VisibleClipBounds, Color.Orange, SystemColors.Control, LinearGradientMode.Horizontal))
            {
                g.FillRectangle(lgb, g.VisibleClipBounds);
            }
            titleLabel.Image = canvas;
        }

        public abstract void UpdateSelections(MMLStyle mmlStyle) { }

        public abstract void LoadMusicData(List< NotesStatus> statusList) { }
    }
}
