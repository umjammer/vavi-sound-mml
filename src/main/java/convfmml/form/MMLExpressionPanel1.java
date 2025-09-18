/*
 * Copyright © 2017-2019 Rerrah
 *
 * MIDI to MML Converter
 */

package convfmml.form;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.math.BigDecimal;
import java.util.List;
import java.util.ResourceBundle;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;

import convfmml.Common.MMLStyle;
import convfmml.Settings;
import convfmml.data.intermediate.NotesStatus;


public class MMLExpressionPanel1 extends BasePanel {

    static final ResourceBundle rb = ResourceBundle.getBundle("messages");

    private Settings.MMLExpression settings;
    public ActionListener TimeBaseFormButtonClick;
    public ItemListener MMLStyleChanged;

    public MMLExpressionPanel1(Settings.MMLExpression settings) {
        InitializeComponent();

        this.settings = settings;
    }

    protected void OnLoad(ComponentEvent e) {

        comboBox1.setSelectedIndex(settings.getMmlStyle().ordinal());
        UpdateComboBox2();

        textBox1.setText(settings.getTimeBase().toString());
        comboBox3.setSelectedIndex(settings.getNewBlockByBar());
        numericUpDown2.setValue(settings.getNewLineBarCount());
        comboBox4.setSelectedIndex(settings.getNewLineByTimeSignature());
    }

    @Override
    public void updateSelections(MMLStyle mmlStyle) {
    }

    @Override
    public void loadMusicData(List<NotesStatus> statusList) {
        textBox1.setText(settings.getTimeBase().toString());
    }

    private void UpdateComboBox2() {
        comboBox2.removeAllItems();
        if (settings.getMmlStyle() == MMLStyle.PMD) {
            cbm2.addAll(List.of(rb.getString("Disabled"), rb.getString("TimeBasePMDZenlen"), rb.getString("TimeBasePMDC")));
            comboBox2.setSelectedIndex(settings.getPrintTimeBasePMD());
        } else {
            cbm2.addAll(List.of(rb.getString("Disabled"), rb.getString("Enabled")));
            comboBox2.setSelectedIndex(settings.getPrintTimeBase());
        }
    }

    private void comboBox1_SelectionChangeCommitted(ItemEvent e) {
        var cb = (JComboBox) e.getSource();
        settings.setMmlStyle(MMLStyle.values()[cb.getSelectedIndex()]);
        UpdateComboBox2();
        MMLStyleChanged.itemStateChanged(e);
    }

    private void button1_Click(ActionEvent e) {
        TimeBaseFormButtonClick.actionPerformed(e);
    }

    private void comboBox2_SelectionChangeCommitted(ItemEvent e) {
        var cb = (JComboBox) e.getSource();
        if (settings.getMmlStyle() == MMLStyle.PMD) {
            settings.setPrintTimeBasePMD(comboBox2.getSelectedIndex());
        } else {
            settings.setPrintTimeBase(comboBox2.getSelectedIndex());
        }
    }

    private void comboBox3_SelectionChangeCommitted(ItemEvent e) {
        var cb = (JComboBox) e.getSource();
        settings.setNewBlockByBar(cb.getSelectedIndex());
    }

    private final FocusListener numericUpDown2_Leave = new FocusAdapter() {
        @Override
        public void focusLost(FocusEvent e) {
            var nud = (JSpinner) e.getSource();
            settings.setNewLineBarCount(new BigDecimal((int) nud.getValue()));
        }
    };

    private void comboBox4_SelectionChangeCommitted(ItemEvent e) {
        var cb = (JComboBox) e.getSource();
        settings.setNewLineByTimeSignature(cb.getSelectedIndex());
    }

//#region Windows Form Designer generated code

    private void InitializeComponent() {
//        System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(MMLExpressionPanel1));
        this.label1 = new JLabel();
        this.comboBox1 = new JComboBox();
        this.label3 = new JLabel();
        this.comboBox2 = new JComboBox();
        this.groupBox1 = new JPanel();
        this.label6 = new JLabel();
        this.numericUpDown2 = new JSpinner();
        this.label5 = new JLabel();
        this.label8 = new JLabel();
        this.comboBox4 = new JComboBox();
        this.label7 = new JLabel();
        this.comboBox3 = new JComboBox();
        this.label4 = new JLabel();
        this.label2 = new JLabel();
        this.textBox1 = new JTextField();
        this.button1 = new JButton();
//        this.panel1.SuspendLayout();
//        this.groupBox1.SuspendLayout();
//        ((System.ComponentModel.ISupportInitialize) (this.numericUpDown2)).BeginInit();
//        this.SuspendLayout();
        // 
        // titleLabel
        // 
//        resources.ApplyResources(this.titleLabel, "titleLabel");
        // 
        // panel1
        // 
        this.panel1.add(this.button1);
        this.panel1.add(this.textBox1);
        this.panel1.add(this.label2);
        this.panel1.add(this.groupBox1);
        this.panel1.add(this.comboBox2);
        this.panel1.add(this.label3);
        this.panel1.add(this.comboBox1);
        this.panel1.add(this.label1);
        // 
        // label1
        // 
//        resources.ApplyResources(this.label1, "label1");
        this.label1.setName("label1");
        // 
        // comboBox1
        // 
//        this.comboBox1.DropDownStyle = JComboBoxStyle.DropDownList;
//        this.comboBox1.FormattingEnabled = true;
        DefaultComboBoxModel<String> cbm1 = new DefaultComboBoxModel<>();
        cbm1.addAll(List.of(
                "Custom",
                "FMP7",
                "FMP",
                "PMD",
                "MXDRV",
                "NRTDRV",
                "MUCOM88",
                "mml2vgm"));
        this.comboBox1.setModel(cbm1);
//        resources.ApplyResources(this.comboBox1, "comboBox1");
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
//        this.comboBox2.DropDownStyle = JComboBoxStyle.DropDownList;
//        this.comboBox2.FormattingEnabled = true;
//        resources.ApplyResources(this.comboBox2, "comboBox2");
        this.comboBox2.setName("comboBox2");
        this.comboBox2.addItemListener(this::comboBox2_SelectionChangeCommitted);
        // 
        // groupBox1
        // 
        this.groupBox1.add(this.label6);
        this.groupBox1.add(this.numericUpDown2);
        this.groupBox1.add(this.label5);
        this.groupBox1.add(this.label8);
        this.groupBox1.add(this.comboBox4);
        this.groupBox1.add(this.label7);
        this.groupBox1.add(this.comboBox3);
        this.groupBox1.add(this.label4);
//        resources.ApplyResources(this.groupBox1, "groupBox1");
        this.groupBox1.setName("groupBox1");
//        this.groupBox1.TabStop = false;
        // 
        // label6
        // 
//        resources.ApplyResources(this.label6, "label6");
        this.label6.setName("label6");
        // 
        // numericUpDown2
        // 
//        resources.ApplyResources(this.numericUpDown2, "numericUpDown2");
        cbm2 = new DefaultComboBoxModel<>();
        this.comboBox2.setModel(cbm2);
        SpinnerNumberModel snm2 = new SpinnerNumberModel(2, 0, 100, 1);
        this.numericUpDown2.setModel(snm2);
        this.numericUpDown2.setName("numericUpDown2");
//        this.numericUpDown2.setValue(new decimal(new int[] {
//                2,
//                0,
//                0,
//                0});
        this.numericUpDown2.addFocusListener(this.numericUpDown2_Leave);
        // 
        // label5
        // 
//        resources.ApplyResources(this.label5, "label5");
        this.label5.setName("label5");
        // 
        // label8
        // 
//        resources.ApplyResources(this.label8, "label8");
        this.label8.setName("label8");
        // 
        // comboBox4
        // 
//        this.comboBox4.DropDownStyle = JComboBoxStyle.DropDownList;
//        this.comboBox4.FormattingEnabled = true;
        DefaultComboBoxModel<String> cbm4 = new DefaultComboBoxModel<>();
        cbm4.addAll(List.of(
                "Disabled",
                "Enabled"));
        this.comboBox4.setModel(cbm4);
//        resources.ApplyResources(this.comboBox4, "comboBox4");
        this.comboBox4.setName("comboBox4");
        this.comboBox4.addItemListener(this::comboBox4_SelectionChangeCommitted);
        // 
        // label7
        // 
//        resources.ApplyResources(this.label7, "label7");
        this.label7.setName("label7");
        // 
        // comboBox3
        // 
//        this.comboBox3.DropDownStyle = JComboBoxStyle.DropDownList;
//        this.comboBox3.FormattingEnabled = true;
        DefaultComboBoxModel<String> cbm3 = new DefaultComboBoxModel<>();
        cbm3.addAll(List.of(
                "None",
                "Space",
                "Tab"));
        this.comboBox3.setModel(cbm3);
//        resources.ApplyResources(this.comboBox3, "comboBox3");
        this.comboBox3.setName("comboBox3");
        this.comboBox3.addItemListener(this::comboBox3_SelectionChangeCommitted);
        // 
        // label4
        // 
//        resources.ApplyResources(this.label4, "label4");
        this.label4.setName("label4");
        // 
        // label2
        // 
//        resources.ApplyResources(this.label2, "label2");
        this.label2.setName("label2");
        // 
        // textBox1
        // 
//        resources.ApplyResources(this.textBox1, "textBox1");
        this.textBox1.setName("textBox1");
//        this.textBox1.ReadOnly = true;
//        this.textBox1.TabStop = false;
        // 
        // button1
        // 
//        resources.ApplyResources(this.button1, "button1");
        this.button1.setName("button1");
//        this.button1.UseVisualStyleBackColor = true;
        this.button1.addActionListener(this::button1_Click);
        // 
        // MMLExpressionPanel1
        // 
//        resources.ApplyResources(this, "$this");
//        this.AutoScaleMode = JAutoScaleMode.Font;
        this.setName("MMLExpressionPanel1");
//        this.panel1.ResumeLayout(false);
//        this.panel1.PerformLayout();
//        this.groupBox1.ResumeLayout(false);
//        this.groupBox1.PerformLayout();
//        ((System.ComponentModel.ISupportInitialize) (this.numericUpDown2)).EndInit();
//        this.ResumeLayout(false);
    }

    DefaultComboBoxModel<String> cbm2;

//#endregion

    private JComboBox<String> comboBox1;
    private JLabel label1;
    private JComboBox<String> comboBox2;
    private JLabel label3;
    private JPanel groupBox1;
    private JLabel label8;
    private JComboBox<String> comboBox4;
    private JLabel label7;
    private JComboBox<String> comboBox3;
    private JLabel label4;
    private JLabel label2;
    private JTextField textBox1;
    private JButton button1;
    private JLabel label6;
    private JSpinner numericUpDown2;
    private JLabel label5;
}
