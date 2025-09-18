/*
 * Copyright © 2017-2019 Rerrah
 *
 * MIDI to MML Converter
 */

package convfmml.form;

import java.awt.Container;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ContainerAdapter;
import java.awt.event.ContainerEvent;
import java.awt.event.ContainerListener;
import java.awt.event.ItemEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.ResourceBundle;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;

import convfmml.Common.MMLStyle;
import convfmml.MIDIReader;
import convfmml.MMLPrinter;
import convfmml.Settings;
import convfmml.converter.IntermediateToMMLConverter;
import convfmml.converter.MIDIToIntermediateConverter;
import convfmml.data.intermediate.Intermediate;
import convfmml.data.intermediate.NotesStatus;
import convfmml.data.mml.MML;
import convfmml.modifier.MusicDataModifier;


public class Main extends JFrame {

    static final ResourceBundle rb = ResourceBundle.getBundle("messages");

    static String[] files;

    private Settings settings;
    private Intermediate srcMusic;
    private Intermediate modTBMusic = null;

    public Main() {
        InitializeComponent();
    }

    private final WindowListener windowAdapter = new WindowAdapter() {
        @Override
        public void windowOpened(WindowEvent e) {
            try {
                settings = Settings.load();
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(null, ex.getMessage(), rb.getString("Error"), JOptionPane.ERROR_MESSAGE);
            }

            onLoad(e);
        }

        @Override
        public void windowClosing(WindowEvent e) {
            onFormClosed(e);
        }
    };

    protected void onLoad(WindowEvent e) {

        var treeNode1 = new DefaultMutableTreeNode(rb.getString("MainMenuMMLExpression1"));
        var mmlPanel = new MMLExpressionPanel1(settings.getMmlExpression());
        mmlPanel.TimeBaseFormButtonClick = this::mmlPanel_TimeBaseFormButtonClick;
        mmlPanel.MMLStyleChanged = this::mmlPanel_MMLStyleChanged;
        treeNode1.setUserObject(mmlPanel);
        var treeNode2 = new DefaultMutableTreeNode(rb.getString("MainMenuMMLExpression2"));
        treeNode2.setUserObject(new MMLExpressionPanel2(settings.getMmlExpression()));
        var treeNode3 = new DefaultMutableTreeNode(rb.getString("MainMenuNoteRest1"));
        treeNode3.setUserObject(new NoteRestPanel1(settings.getNoteRest()));
        var treeNode4 = new DefaultMutableTreeNode(rb.getString("MainMenuNoteRest2"));
        treeNode4.setUserObject(new NoteRestPanel2(settings.getNoteRest(), settings.getMmlExpression().getMmlStyle()));
        var treeNode5 = new DefaultMutableTreeNode(rb.getString("MainMenuGeneral"));
        treeNode5.setUserObject(new ControlCommandGenericPanel(settings.getControlCommand().getGeneric()));
        var treeNode6 = new DefaultMutableTreeNode(rb.getString("MainMenuVolume"));
        treeNode6.setUserObject(new VolumePanel(settings.getControlCommand().getVolume(), settings.getMmlExpression().getMmlStyle()));
        var treeNode7 = new DefaultMutableTreeNode(rb.getString("MainMenuPan"));
        treeNode7.setUserObject(new PanPanel(settings.getControlCommand().getPan(), settings.getMmlExpression().getMmlStyle()));
        var treeNode8 = new DefaultMutableTreeNode(rb.getString("MainMenuProgramChange"));
        treeNode8.setUserObject(new ProgramChangePanel(settings.getControlCommand().getProgramChange()));
        var treeNode9 = new DefaultMutableTreeNode(rb.getString("MainMenuTempo"));
        treeNode9.setUserObject(new TempoPanel(settings.getControlCommand().getTempo()));
        var treeNode10 = new DefaultMutableTreeNode(rb.getString("MainMenuControlCommands"));
        treeNode10.add(treeNode5);
        treeNode10.add(treeNode6);
        treeNode10.add(treeNode7);
        treeNode10.add(treeNode8);
        treeNode10.add(treeNode9);
        treeNode10.setUserObject(treeNode5.getUserObject());
        var treeNode11 = new DefaultMutableTreeNode(rb.getString("MainMenuPart"));
        treeNode11.setUserObject(new OutputPartPanel(settings.getOutputPart(), settings.getMmlExpression().getMmlStyle()));
        ((DefaultMutableTreeNode) dtm.getRoot()).add(treeNode1);
        ((DefaultMutableTreeNode) dtm.getRoot()).add(treeNode2);
        ((DefaultMutableTreeNode) dtm.getRoot()).add(treeNode3);
        ((DefaultMutableTreeNode) dtm.getRoot()).add(treeNode4);
        ((DefaultMutableTreeNode) dtm.getRoot()).add(treeNode10);
        ((DefaultMutableTreeNode) dtm.getRoot()).add(treeNode11);

        ((Container) splitContainer4.getRightComponent()).add((BasePanel) treeNode1.getUserObject());

        String[] fileNameArray = Main.files;
        if (fileNameArray.length == 2 && Files.exists(Path.of(fileNameArray[1]))) {
            String prevFileName = inputMIDIJTextField.getText();
            try {
                splitContainer2.setEnabled(false);
                LoadMIDI(fileNameArray[1]); // Get 1 file only
                toolStripStatusLabel1.setText(rb.getString("StatusBarReady"));
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, ex.getMessage(), rb.getString("Error"), JOptionPane.ERROR_MESSAGE);
                inputMIDIJTextField.setText(prevFileName);
                toolStripStatusLabel1.setText("");
            } finally {
                splitContainer2.setEnabled(true);
                splitContainer2.getRightComponent().setEnabled((modTBMusic != null));
            }
        }
    }

    protected void OnDragEnter(DropTargetDragEvent drgevent) {
        try {
            Transferable transferable = drgevent.getTransferable();
            if (transferable.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
                List<File> fileList = (List<File>) transferable.getTransferData(DataFlavor.javaFileListFlavor);
                if (fileList.size() == 1) {
                    File file = fileList.get(0);
                    if (file.getName().toLowerCase().endsWith(".mid")) {
                        drgevent.acceptDrag(DnDConstants.ACTION_COPY_OR_MOVE);
                        return;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        drgevent.rejectDrag();
    }

    protected void OnDragDrop(DropTargetDropEvent drgevent) {
        String prevFileName = inputMIDIJTextField.getText();
        try {
            drgevent.acceptDrop(DnDConstants.ACTION_COPY_OR_MOVE);
            Transferable transferable = drgevent.getTransferable();
            if (transferable.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
                List<File> fileList = (List<File>) transferable.getTransferData(DataFlavor.javaFileListFlavor);
                if (!fileList.isEmpty()) {
                    File file = fileList.getFirst();
                    splitContainer2.setEnabled(false);
                    LoadMIDI(file.getAbsolutePath()); // Load the .mid file
                    toolStripStatusLabel1.setText(rb.getString("StatusBarReady"));
                }
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage(), rb.getString("Error"), JOptionPane.ERROR_MESSAGE);
            inputMIDIJTextField.setText(prevFileName);
            toolStripStatusLabel1.setText((modTBMusic == null) ? "" : rb.getString("StatusBarReady"));
        } finally {
            splitContainer2.setEnabled(true);
            splitContainer2.getRightComponent().setEnabled(modTBMusic != null);
        }
    }

    protected void onFormClosed(WindowEvent e) {
        try {
            settings.save();
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage(), rb.getString("Error"), JOptionPane.ERROR_MESSAGE);
        }
    }

    private void exitToolStripMenuItem_Click(ActionEvent e) {
        setVisible(false);
    }

    private void versionInfoToolStripMenuItem_Click(ActionEvent e) {
        var form = new VersionInfoForm();
        form.setVisible(true);
    }

    private void InputMIDIButton_Click(ActionEvent e) {
        String prevFileName = inputMIDIJTextField.getText();
        try {
            var ofd = new JFileChooser();
            String title = rb.getString("InputMIDIDialogTitle");
            ofd.setFileFilter(createFileFilterFromDesc("MIDI File (*.mid)|*.mid|All File (*.*)|*.*"));
            if (ofd.showDialog(null, title) == JFileChooser.APPROVE_OPTION) {
                splitContainer2.setEnabled(false);
                LoadMIDI(ofd.getSelectedFile().getName());
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage(), rb.getString("Error"), JOptionPane.ERROR_MESSAGE);
            inputMIDIJTextField.setText(prevFileName);
        } finally {
            toolStripStatusLabel1.setText((modTBMusic == null) ? "" : rb.getString("StatusBarReady"));
            splitContainer2.setEnabled(true);
            splitContainer2.getRightComponent().setEnabled((modTBMusic != null));
        }
    }

    private void LoadMIDI(String fileName) {
        inputMIDIJTextField.setText(fileName);

        toolStripStatusLabel1.setText(String.format(rb.getString("StatusBarLoad"), fileName));
        statusStrip1.repaint();

        var reader = new MIDIReader();
        var converter = new MIDIToIntermediateConverter();
        srcMusic = converter.convert(reader.read(fileName, 1));

        updateMusicdata();

        autoGenerateOutputName(fileName);
    }

    private void autoGenerateOutputName(String inputName) {
        outputMMLJTextField.setText(dotnet4j.io.Path.combine(
                dotnet4j.io.Path.getDirectoryName(inputName),
                (dotnet4j.io.Path.getFileNameWithoutExtension(inputName) + settings.getMmlExpression().getExtension())));
    }

    private void updateMusicdata() {
        toolStripStatusLabel1.setText(rb.getString("StatusBarTimeBase"));
        statusStrip1.repaint();
        setModifiedMusicData();

        List<NotesStatus> list = modTBMusic.GetNotesStatusList();
        Enumeration<TreeNode> e = ((DefaultMutableTreeNode) dtm.getRoot()).breadthFirstEnumeration();
        while (e.hasMoreElements()) {
            DefaultMutableTreeNode tn = (DefaultMutableTreeNode) e.nextElement();
            if (tn.getUserObject() instanceof BasePanel)
                ((BasePanel) tn.getUserObject()).loadMusicData(list);
        }
    }

    private void setModifiedMusicData() {
        modTBMusic = srcMusic.clone();
        modTBMusic.setCountsPerWholeNote(settings.getMmlExpression().getTimeBase().intValue());
    }

    private void saveButton_Click(ActionEvent e) {
        try {
            var sfd = new JFileChooser();
            String Title = rb.getString("ExportAs");
            sfd.setSelectedFile(new File(dotnet4j.io.Path.getFileName(outputMMLJTextField.getText())));
            sfd.setCurrentDirectory(new File(dotnet4j.io.Path.getDirectoryName(outputMMLJTextField.getText())));
//            sfd.RestoreDirectory = true;

            String desc = "";
            switch (settings.getMmlExpression().getMmlStyle()) {
                case MMLStyle.Custom:
                    desc = "All File (*.*)|*.*";
                    break;
                case MMLStyle.FMP7:
                    desc = "MWI File (*.mwi)|*.mwi";
                    break;
                case MMLStyle.FMP:
                    desc = "MPI File (*.mpi)|*.mpi|MVI File (*.mvi)|*.mvi|MZI File (*.mzi)|*.mzi";
//                    extsIndex = settings.getMmlExpression().getExtensionFMP();
                    break;
                case MMLStyle.MXDRV:
                    desc = "MUS File (*.mus)|*.mus";
                    break;
                case MMLStyle.PMD:
                case MMLStyle.NRTDRV:
                    desc = "MML File (*.mml)|*.mml";
                    break;
                case MMLStyle.MUCOM88:
                    desc = "MUC File (*.muc)|*.muc";
                    break;
                case MMLStyle.Mml2vgm:
                    desc = "GWI File (*.gwi)|*.gwi";
                    break;
                default:
                    break;
            }
            sfd.setFileFilter(createFileFilterFromDesc(desc));

            if (sfd.showDialog(null, Title) == JFileChooser.APPROVE_OPTION) {
                outputMMLJTextField.setText(sfd.getSelectedFile().getName());

                if (settings.getMmlExpression().getMmlStyle() == MMLStyle.Custom) {
                    settings.getMmlExpression().setExtensionCustom(dotnet4j.io.Path.getExtension(sfd.getSelectedFile().getName()));
                } else if (settings.getMmlExpression().getMmlStyle() == MMLStyle.FMP) {
//                    settings.getMmlExpression().getExtensionFMP() = sfd.getFileFilter(). Index;
                }
            } else {
                return;
            }

            splitContainer2.setEnabled(false);

            toolStripStatusLabel1.setText(rb.getString("StatusBarArrange"));
            statusStrip1.repaint();
            var modifier = MusicDataModifier.factory(settings.getMmlExpression().getMmlStyle());
            List<NotesStatus> statusList = null;
            Enumeration<TreeNode> i = ((DefaultMutableTreeNode) dtm.getRoot()).breadthFirstEnumeration();
            while (i.hasMoreElements()) {
                DefaultMutableTreeNode tn = (DefaultMutableTreeNode) i.nextElement();
                if (tn.getUserObject() instanceof OutputPartPanel) {
                    statusList = ((OutputPartPanel) tn.getUserObject()).getOutputPartSettings();
                    break;
                }
            }
            Intermediate modMusic = modifier.modify(modTBMusic, settings, statusList);

            IntermediateToMMLConverter converter = IntermediateToMMLConverter.factory(settings.getMmlExpression().getMmlStyle());
            MML mml = converter.convert(modMusic, settings, statusList);

            toolStripStatusLabel1.setText(rb.getString("StatusBarExport"));
            statusStrip1.repaint();
            var printer = new MMLPrinter();
            printer.print(mml, outputMMLJTextField.getText(), settings);

            toolStripStatusLabel1.setText(rb.getString("StatusBarComplete"));
            statusStrip1.repaint();
            JOptionPane.showMessageDialog(null, rb.getString("SuccessText"), rb.getString("SuccessTitle"), JOptionPane.PLAIN_MESSAGE);
        } catch (Exception ex) {
            toolStripStatusLabel1.setText(rb.getString("StatusBarFailed"));
            statusStrip1.repaint();
            JOptionPane.showMessageDialog(null, ex.getMessage(), rb.getString("FailedTitle"), JOptionPane.PLAIN_MESSAGE);
        } finally {
            setModifiedMusicData();
            toolStripStatusLabel1.setText(rb.getString("StatusBarReady"));
            splitContainer2.setEnabled(true);
        }
    }

    private static FileNameExtensionFilter createFileFilterFromDesc(String desc) {
        String[] exts = Arrays.stream(desc.split("[|; ()]")).filter(s -> s.contains("*.")).map(s -> s.replace("*.", ""))
                .toArray(String[]::new);
        return new FileNameExtensionFilter(desc, exts);
    }

    private void treeView1_AfterSelect(TreeSelectionEvent e) {
        var panel = (BasePanel) ((DefaultMutableTreeNode) e.getPath().getLastPathComponent()).getUserObject();
        if (!((Container) splitContainer4.getRightComponent()).isAncestorOf(panel)) {
            ((Container) splitContainer4.getRightComponent()).removeAll();
            ((Container) splitContainer4.getRightComponent()).add(panel);
        }
    }

    private final ContainerListener splitContainer4_Panel2_ControlAdded = new ContainerAdapter() {
        @Override
        public void componentAdded(ContainerEvent e) {
            var panel = (BasePanel) e.getSource();
            panel.updateSelections(settings.getMmlExpression().getMmlStyle());
        }
    };

    private void mmlPanel_TimeBaseFormButtonClick(ActionEvent e) {
        try {
            var dialog = new TimeBaseForm(settings.getMmlExpression().getPrintTimeBase());
            dialog.setVisible(true);
            if (dialog.dialogResult == JOptionPane.OK_OPTION) {
                settings.getMmlExpression().setTimeBase(new BigDecimal(dialog.getTimebase()));
            } else {
                return;
            }

            splitContainer2.setEnabled(false);

            updateMusicdata();

            splitContainer2.setEnabled(true);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage(), rb.getString("Error"), JOptionPane.ERROR_MESSAGE);
        } finally {
            toolStripStatusLabel1.setText(rb.getString("StatusBarReady"));
        }
    }

    private void mmlPanel_MMLStyleChanged(ItemEvent e) {
        Enumeration<TreeNode> i = ((DefaultMutableTreeNode) dtm.getRoot()).breadthFirstEnumeration();
        while (i.hasMoreElements()) {
            DefaultMutableTreeNode tn = (DefaultMutableTreeNode) i.nextElement();
            if (tn.getUserObject() instanceof OutputPartPanel) {
                ((OutputPartPanel) tn.getUserObject()).changeOutputPartMMLSyle(settings.getMmlExpression().getMmlStyle());
            }
        }

        autoGenerateOutputName(inputMIDIJTextField.getText());
    }

//#region Windows Form Designer generated code

    private void InitializeComponent() {
//        System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(Main));
        this.splitContainer2 = new JSplitPane();
        this.inputMIDILabel = new JLabel();
        this.inputMIDIButton = new JButton();
        this.inputMIDIJTextField = new JTextField();
        this.splitContainer3 = new JSplitPane();
        this.configGroupBox = new JPanel();
        this.splitContainer4 = new JSplitPane();
        this.treeView1 = new JTree();
        this.saveButton = new JButton();
        this.outputMMLJTextField = new JTextField();
        this.outputMMLLabel = new JLabel();
        this.statusStrip1 = new JPanel();
        this.toolStripStatusLabel1 = new JLabel();
        this.menuStrip1 = new JMenuBar();
        this.fileToolStripMenuItem = new JMenu();
        this.exitToolStripMenuItem = new JMenu();
        this.helpToolStripMenuItem = new JMenu();
        this.versionInfoToolStripMenuItem = new JMenu();
//        ((System.ComponentModel.ISupportInitialize) (this.splitContainer2)).BeginInit();
//        this.splitContainer2.Panel1.SuspendLayout();
//        this.splitContainer2.Panel2.SuspendLayout();
//        this.splitContainer2.SuspendLayout();
//        ((System.ComponentModel.ISupportInitialize) (this.splitContainer3)).BeginInit();
//        this.splitContainer3.Panel1.SuspendLayout();
//        this.splitContainer3.Panel2.SuspendLayout();
//        this.splitContainer3.SuspendLayout();
//        this.configGroupBox.SuspendLayout();
//        ((System.ComponentModel.ISupportInitialize) (this.splitContainer4)).BeginInit();
//        this.splitContainer4.Panel1.SuspendLayout();
//        this.splitContainer4.SuspendLayout();
//        this.statusStrip1.SuspendLayout();
//        this.menuStrip1.SuspendLayout();
//        this.SuspendLayout();
        dtm = new DefaultTreeModel(new DefaultMutableTreeNode());
        treeView1.setModel(dtm);
        treeView1.setRootVisible(false);
        //
        // splitContainer2
        //
//        resources.ApplyResources(this.splitContainer2, "splitContainer2");
        this.splitContainer2.setName("splitContainer2");
        //
        // splitContainer2.Panel1
        //
//        resources.ApplyResources(this.splitContainer2.Panel1, "splitContainer2.Panel1");
        JPanel leftPanel2 = new JPanel();
        leftPanel2.add(this.inputMIDILabel);
        leftPanel2.add(this.inputMIDIButton);
        leftPanel2.add(this.inputMIDIJTextField);
        this.splitContainer2.setLeftComponent(leftPanel2);
        //
        // splitContainer2.Panel2
        //
//        resources.ApplyResources(this.splitContainer2.Panel2, "splitContainer2.Panel2");
        this.splitContainer2.setRightComponent(this.splitContainer3);
//        this.splitContainer2.TabStop = false;
        //
        // inputMIDILabel
        //
//        resources.ApplyResources(this.inputMIDILabel, "inputMIDILabel");
        this.inputMIDILabel.setName("inputMIDILabel");
        //
        // inputMIDIButton
        //
//        resources.ApplyResources(this.inputMIDIButton, "inputMIDIButton");
        this.inputMIDIButton.setName("inputMIDIButton");
//        this.inputMIDIButton.UseVisualStyleBackColor = true;
        this.inputMIDIButton.addActionListener(this::InputMIDIButton_Click);
        //
        // inputMIDIJTextField
        //
//        resources.ApplyResources(this.inputMIDIJTextField, "inputMIDIJTextField");
        this.inputMIDIJTextField.setName("inputMIDIJTextField");
        this.inputMIDIJTextField.setEditable(false);
//        this.inputMIDIJTextField.TabStop = false;
        //
        // splitContainer3
        //
//        resources.ApplyResources(this.splitContainer3, "splitContainer3");
        this.splitContainer3.setName("splitContainer3");
        //
        // splitContainer3.Panel1
        //
//        resources.ApplyResources(this.splitContainer3.Panel1, "splitContainer3.Panel1");
        this.splitContainer3.setLeftComponent(this.configGroupBox);
        //
        // splitContainer3.Panel2
        //
//        resources.ApplyResources(this.splitContainer3.getRightComponent(), "splitContainer3.Panel2");
        JPanel rightPanel3 = new JPanel();
        rightPanel3.add(this.saveButton);
        rightPanel3.add(this.outputMMLJTextField);
        rightPanel3.add(this.outputMMLLabel);
        this.splitContainer3.setRightComponent(rightPanel3);
//        this.splitContainer3.TabStop = false;
        //
        // configGroupBox
        //
//        resources.ApplyResources(this.configGroupBox, "configGroupBox");
        this.configGroupBox.add(this.splitContainer4);
//        this.configGroupBox.setName("configGroupBox");
//        this.configGroupBox.TabStop = false;
        //
        // splitContainer4
        //
//        resources.ApplyResources(this.splitContainer4, "splitContainer4");
//        this.splitContainer4.setName("splitContainer4");
        //
        // splitContainer4.Panel1
        //
//        resources.ApplyResources(this.splitContainer4.Panel1, "splitContainer4.Panel1");
        this.splitContainer4.setLeftComponent(this.treeView1);
        //
        // splitContainer4.Panel2
        //
//        resources.ApplyResources(this.splitContainer4.Panel2, "splitContainer4.Panel2");
        ((Container) this.splitContainer4.getRightComponent()).addContainerListener(this.splitContainer4_Panel2_ControlAdded);
//        this.splitContainer4.TabStop = false;
        //
        // treeView1
        //
//        resources.ApplyResources(this.treeView1, "treeView1");
        this.treeView1.setName("treeView1");
        this.treeView1.addTreeSelectionListener(this::treeView1_AfterSelect);
        //
        // saveButton
        //
//        resources.ApplyResources(this.saveButton, "saveButton");
        this.saveButton.setName("saveButton");
//        this.saveButton.UseVisualStyleBackColor = true;
        this.saveButton.addActionListener(this::saveButton_Click);
        //
        // outputMMLJTextField
        //
//        resources.ApplyResources(this.outputMMLJTextField, "outputMMLJTextField");
        this.outputMMLJTextField.setName("outputMMLJTextField");
        this.outputMMLJTextField.setEditable(false);
//        this.outputMMLJTextField.TabStop = false;
        //
        // outputMMLLabel
        //
//        resources.ApplyResources(this.outputMMLLabel, "outputMMLLabel");
        this.outputMMLLabel.setName("outputMMLLabel");
        //
        // statusStrip1
        //
//        resources.ApplyResources(this.statusStrip1, "statusStrip1");
        this.statusStrip1.add(this.toolStripStatusLabel1);
        this.statusStrip1.setName("statusStrip1");
        //
        // toolStripStatusLabel1
        //
//        resources.ApplyResources(this.toolStripStatusLabel1, "toolStripStatusLabel1");
        this.toolStripStatusLabel1.setName("toolStripStatusLabel1");
        //
        // menuStrip1
        //
//        resources.ApplyResources(this.menuStrip1, "menuStrip1");
        this.menuStrip1.add(this.fileToolStripMenuItem);
        this.menuStrip1.add(this.helpToolStripMenuItem);
        this.menuStrip1.setName("menuStrip1");
        //
        // fileToolStripMenuItem
        //
//        resources.ApplyResources(this.fileToolStripMenuItem, "fileToolStripMenuItem");
        this.fileToolStripMenuItem.add(this.exitToolStripMenuItem);
        this.fileToolStripMenuItem.setName("fileToolStripMenuItem");
        //
        // exitToolStripMenuItem
        //
//        resources.ApplyResources(this.exitToolStripMenuItem, "exitToolStripMenuItem");
        this.exitToolStripMenuItem.setName("exitToolStripMenuItem");
        this.exitToolStripMenuItem.addActionListener(this::exitToolStripMenuItem_Click);
        //
        // helpToolStripMenuItem
        //
//        resources.ApplyResources(this.helpToolStripMenuItem, "helpToolStripMenuItem");
        this.helpToolStripMenuItem.add(this.versionInfoToolStripMenuItem);
        this.helpToolStripMenuItem.setName("helpToolStripMenuItem");
        //
        // versionInfoToolStripMenuItem
        //
//        resources.ApplyResources(this.versionInfoToolStripMenuItem, "versionInfoToolStripMenuItem");
        this.versionInfoToolStripMenuItem.setName("versionInfoToolStripMenuItem");
        this.versionInfoToolStripMenuItem.addActionListener(this::versionInfoToolStripMenuItem_Click);
        //
        // Form1
        //
//        resources.ApplyResources(this, "$this");
//        this.AllowDrop = true;
//        this.AutoScaleMode = JAutoScaleMode.Font;
//        this.Controls.add(this.splitContainer2);
//        this.Controls.add(this.statusStrip1);
//        this.Controls.add(this.menuStrip1);
//        this.FormBorderStyle = JFormBorderStyle.FixedSingle;
        this.setJMenuBar(this.menuStrip1);
//        this.MaximizeBox = false;
//        this.setName("Form1");
//        this.splitContainer2.Panel1.ResumeLayout(false);
//        this.splitContainer2.Panel1.PerformLayout();
//        this.splitContainer2.Panel2.ResumeLayout(false);
//        ((System.ComponentModel.ISupportInitialize) (this.splitContainer2)).EndInit();
//        this.splitContainer2.ResumeLayout(false);
//        this.splitContainer3.Panel1.ResumeLayout(false);
//        this.splitContainer3.Panel2.ResumeLayout(false);
//        this.splitContainer3.Panel2.PerformLayout();
//        ((System.ComponentModel.ISupportInitialize) (this.splitContainer3)).EndInit();
//        this.splitContainer3.ResumeLayout(false);
//        this.configGroupBox.ResumeLayout(false);
//        this.splitContainer4.Panel1.ResumeLayout(false);
//        ((System.ComponentModel.ISupportInitialize) (this.splitContainer4)).EndInit();
//        this.splitContainer4.ResumeLayout(false);
//        this.statusStrip1.ResumeLayout(false);
//        this.statusStrip1.PerformLayout();
//        this.menuStrip1.ResumeLayout(false);
//        this.menuStrip1.PerformLayout();
//        this.ResumeLayout(false);
//        this.PerformLayout();
        addWindowListener(this.windowAdapter);
    }

    DefaultTreeModel dtm;

//#endregion

    private JPanel statusStrip1;
    private JLabel toolStripStatusLabel1;
    private JSplitPane splitContainer2;
    private JLabel inputMIDILabel;
    private JButton inputMIDIButton;
    private JTextField inputMIDIJTextField;
    private JSplitPane splitContainer3;
    private JPanel configGroupBox;
    private JSplitPane splitContainer4;
    private JTree treeView1;
    private JButton saveButton;
    private JTextField outputMMLJTextField;
    private JLabel outputMMLLabel;
    private JMenuBar menuStrip1;
    private JMenu fileToolStripMenuItem;
    private JMenu exitToolStripMenuItem;
    private JMenu helpToolStripMenuItem;
    private JMenu versionInfoToolStripMenuItem;
}

