/*
 * Copyright © 2017-2019 Rerrah
 *
 * MIDI to MML Converter
 */

package convfmml.form;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.LinearGradientPaint;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.SystemColor;
import java.awt.event.FocusEvent;
import java.awt.image.BufferedImage;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import convfmml.Common.MMLStyle;
import convfmml.data.intermediate.NotesStatus;


public class BasePanel extends JPanel {

    public BasePanel() {
        InitializeComponent();
    }

    protected void OnLoad(FocusEvent e) {

        BufferedImage canvas = new BufferedImage(titleLabel.getWidth(), titleLabel.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = canvas.createGraphics();
        try {
            Rectangle bounds = new Rectangle(0, 0, canvas.getWidth(), canvas.getHeight());
            LinearGradientPaint gradient = new LinearGradientPaint(
                    0, 0, canvas.getWidth(), 0, // Horizontal gradient
                    new float[]{0f, 1f},
                    new Color[]{Color.ORANGE, SystemColor.control}
            );
            g2d.setPaint(gradient);
            g2d.fill(bounds);
        } finally {
            g2d.dispose();
        }
        titleLabel.setIcon(new ImageIcon(canvas));
    }

    public void updateSelections(MMLStyle mmlStyle) {}

    public void loadMusicData(List<NotesStatus> statusList) {}

//#region Windows Form Designer generated code

    private void InitializeComponent() {
        this.titleLabel = new JLabel();
        this.panel1 = new JPanel();
//        this.SuspendLayout();
        //
        // titleLabel
        //
//        this.titleLabel.BackColor = SystemColors.Control;
//        this.titleLabel.Location = new Point(0, 0);
        this.titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        this.titleLabel.setName("titleLabel");
//        this.titleLabel.Padding = new JPadding(20, 0, 0, 0);
//        this.titleLabel.Size = new Size(518, 23);
//        this.titleLabel.TabIndex = 0;
        this.titleLabel.setText("title");
        this.titleLabel.setHorizontalTextPosition(SwingConstants.LEFT);
        //
        // panel1
        //
        this.panel1.setLocation(new Point(16, 33));
        this.panel1.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 0));
        this.panel1.setName("panel1");
        this.panel1.setPreferredSize(new Dimension(500, 208));
//        this.panel1.TabIndex = 1;
        //
        // BasePanel
        //
//        this.AutoScaleDimensions = new SizeF(6F, 12F);
//        this.AutoScaleMode = JAutoScaleMode.Font;
        this.add(this.panel1);
        this.add(this.titleLabel);
//        this.Margin = new JPadding(0);
        this.setName("BasePanel");
        this.setPreferredSize(new Dimension(528, 241));
//        this.ResumeLayout(false);
    }

//#endregion

    protected JLabel titleLabel;
    protected JPanel panel1;
}
