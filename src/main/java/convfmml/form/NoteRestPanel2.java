/*
 * Copyright © 2017-2019 Rerrah
 *
 * MIDI to MML Converter
 */

package convfmml.form;

import java.awt.event.ComponentEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.ItemEvent;
import java.util.List;
import java.util.ResourceBundle;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;

import convfmml.Common.MMLStyle;
import convfmml.Settings;


public class NoteRestPanel2 extends BasePanel {

    static final ResourceBundle rb = ResourceBundle.getBundle("messages");

    private Settings.NoteRest settings;
    private MMLStyle mmlStyle;

    public NoteRestPanel2(Settings.NoteRest settings, MMLStyle mmlStyle) {
        InitializeComponent();

        this.settings = settings;
        this.mmlStyle = mmlStyle;
    }

    protected void OnLoad(ComponentEvent e) {

        // Load CutByMeasure in UpdateSelections
        checkBox2.setSelected(settings.isNewBlockInCutted());
        // Load TieStyle in UpdateSelections
        checkBox3.setSelected(settings.isUnuseTiedRest());
    }

    @Override
    public void updateSelections(MMLStyle mmlStyle) {
        this.mmlStyle = mmlStyle;

        comboBox2.removeAllItems();
        switch (this.mmlStyle) {
            case MMLStyle.MXDRV:
            case MMLStyle.NRTDRV:
            case MMLStyle.MUCOM88:
                cbm2.addAll(List.of(rb.getString("TieLengthStyleC4AndC16"), rb.getString("TieLengthStyleC4Hat16")));
                break;
            default:
                cbm2.addAll(List.of(rb.getString("TieLengthStyleC4AndC16"), rb.getString("TieLengthStyleC4And16")));
                break;
        }
        comboBox2.setSelectedIndex(settings.getTieStyle());

        if (mmlStyle == MMLStyle.Custom) {
            label4.setEnabled(true);
            textBox1.setEnabled(true);
        } else {
            label4.setEnabled(false);
            textBox1.setEnabled(false);
        }

        switch (mmlStyle) {
            case MMLStyle.FMP:
            case MMLStyle.FMP7:
            case MMLStyle.Mml2vgm:
                checkBox3.setEnabled(false);
                break;
            case MMLStyle.MUCOM88:
                checkBox3.setEnabled((comboBox2.getSelectedIndex() != 0));
                break;
            default:
                checkBox3.setEnabled(true);
                break;
        }

        updateComboBox1(comboBox2);
        UpdateCheckBox2();
    }

    private void UpdateCheckBox2() {
        checkBox2.setEnabled((comboBox1.getSelectedIndex() == 1 && comboBox2.getSelectedIndex() == 0));
    }

    private void updateComboBox1(JComboBox<String> comboBox2) {
        comboBox1.removeAllItems();

        List<String> items;
        if (comboBox2.getSelectedIndex() == 0) {
            items = List.of(rb.getString("OverMeasureC4"), rb.getString("OverMeasureC8AndC8"));
        } else {
            switch (mmlStyle) {
                case MMLStyle.MXDRV:
                case MMLStyle.NRTDRV:
                    items = List.of(rb.getString("OverMeasureC4"), rb.getString("OverMeasureC8Hat8"));
                    break;
                default:
                    items = List.of(rb.getString("OverMeasureC4"), rb.getString("OverMeasureC8And8"));
                    break;
            }
        }
        cbm1.addAll(items);

        comboBox1.setSelectedIndex(settings.getCutByBar());
        UpdateCheckBox2();
    }

    private void comboBox1_SelectionChangeCommitted(ItemEvent e) {
        var cb = (JComboBox<?>) e.getSource();
        settings.setCutByBar(cb.getSelectedIndex());
        UpdateCheckBox2();
    }

    private void checkBox2_CheckedChanged(ItemEvent e) {
        var cb = (JCheckBox) e.getSource();
        settings.setNewBlockInCutted(cb.isSelected());
    }

    private final FocusListener textBox1_Leave = new FocusAdapter() {
        @Override
        public void focusLost(FocusEvent e) {
            var tb = (JTextField) e.getComponent();
            settings.setTieCommandCustom(tb.getText());
        }
    };

    private void comboBox2_SelectionChangeCommitted(ItemEvent e) {
        var cb = (JComboBox<String>) e.getSource();
        settings.setTieStyle(cb.getSelectedIndex());
        updateComboBox1(cb);

        if (mmlStyle == MMLStyle.MUCOM88) {
            checkBox3.setEnabled((cb.getSelectedIndex() != 0));
        }
    }

    private void checkBox3_CheckedChanged(ChangeEvent e) {
        var cb = (JCheckBox) e.getSource();
        settings.setUnuseTiedRest(cb.isSelected());
    }

//#region Windows Form Designer generated code

    private void InitializeComponent() {
//        System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(NoteRestPanel2));
        this.textBox1 = new JTextField();
        this.label4 = new JLabel();
        this.checkBox3 = new JCheckBox();
        this.comboBox1 = new JComboBox<>();
        this.label3 = new JLabel();
        this.comboBox2 = new JComboBox<>();
        this.label5 = new JLabel();
        this.checkBox2 = new JCheckBox();
//        this.panel1.SuspendLayout();
//        this.SuspendLayout();
        //
        // titleLabel
        //
//        resources.ApplyResources(this.titleLabel, "titleLabel");
        //
        // panel1
        //
//        resources.ApplyResources(this.panel1, "panel1");
        this.panel1.add(this.checkBox2);
        this.panel1.add(this.comboBox2);
        this.panel1.add(this.label5);
        this.panel1.add(this.comboBox1);
        this.panel1.add(this.label3);
        this.panel1.add(this.checkBox3);
        this.panel1.add(this.textBox1);
        this.panel1.add(this.label4);
        //
        // textBox1
        //
//        resources.ApplyResources(this.textBox1, "textBox1");
        this.textBox1.setName("textBox1");
        this.textBox1.addFocusListener(this.textBox1_Leave);
        //
        // label4
        //
//        resources.ApplyResources(this.label4, "label4");
        this.label4.setName("label4");
        //
        // checkBox3
        //
//        resources.ApplyResources(this.checkBox3, "checkBox3");
        this.checkBox3.setName("checkBox3");
//        this.checkBox3.UseVisualStyleBackColor = true;
        this.checkBox3.addChangeListener(this::checkBox3_CheckedChanged);
        //
        // comboBox1
        //
//        resources.ApplyResources(this.comboBox1, "comboBox1");
//        this.comboBox1.DropDownStyle = JComboBoxStyle.DropDownList;
//        this.comboBox1.FormattingEnabled = true;
        cbm1 = new DefaultComboBoxModel<>();
        this.comboBox1.setModel(cbm1);
        this.comboBox1.setName("comboBox1");
        this.comboBox1.addItemListener(this::comboBox1_SelectionChangeCommitted);
        //
        // label3
        //
//        resources.ApplyResources(this.label3, "label3");
        this.label3.setName("label3");
        //
        // comboBox2
        //
//        resources.ApplyResources(this.comboBox2, "comboBox2");
//        this.comboBox2.DropDownStyle = JComboBoxStyle.DropDownList;
//        this.comboBox2.FormattingEnabled = true;
        cbm2 = new DefaultComboBoxModel<>();
        this.comboBox2.setModel(cbm2);
        this.comboBox2.setName("comboBox2");
        this.comboBox2.addItemListener(this::comboBox2_SelectionChangeCommitted);
        //
        // label5
        //
//        resources.ApplyResources(this.label5, "label5");
        this.label5.setName("label5");
        //
        // checkBox2
        //
//        resources.ApplyResources(this.checkBox2, "checkBox2");
        this.checkBox2.setName("checkBox2");
//        this.checkBox2.UseVisualStyleBackColor = true;
        this.checkBox2.addItemListener(this::checkBox2_CheckedChanged);
        //
        // NoteRestPanel2
        //
//        resources.ApplyResources(this, "$this");
//        this.AutoScaleMode = JAutoScaleMode.Font;
        this.setName("NoteRestPanel2");
//        this.panel1.ResumeLayout(false);
//        this.panel1.PerformLayout();
//        this.ResumeLayout(false);
    }

    DefaultComboBoxModel<String> cbm1;
    DefaultComboBoxModel<String> cbm2;

//#endregion

    private JTextField textBox1;
    private JLabel label4;
    private JCheckBox checkBox3;
    private JComboBox<String> comboBox1;
    private JLabel label3;
    private JCheckBox checkBox2;
    private JComboBox<String> comboBox2;
    private JLabel label5;
}
