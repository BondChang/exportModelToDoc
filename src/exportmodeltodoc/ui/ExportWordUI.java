package exportmodeltodoc.ui;

import com.nomagic.magicdraw.core.Application;
import exportmodeltodoc.entity.WordElement;
import exportmodeltodoc.service.ExportToDocService;
import org.apache.commons.lang.StringUtils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.*;
import java.io.File;

public class ExportWordUI extends JFrame {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    public static boolean bClosed = true;

    private static JFrame treeFrame = null;
    private static WordElement rootElement = null;
    public static int exportType = -1;
    public static String contentDisplayWide = "3";
    public static boolean geneBlankContent = false;

    /**
     * Create the frame.
     */
    public ExportWordUI() {
        final JTextField textField_1 = new JTextField();
        JFrame frame = new JFrame();
        JPanel contentPane = new JPanel();
        JButton button_2;
        JButton buttonReturn;
        frame.setVisible(true);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                bClosed = true;
            }
        });

        frame.setTitle("导出doc文档");
        // setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setBounds(200, 200, 800, 800);
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        contentPane.setLayout(null);
        /* 添加目录深度的下拉选择框 */
        contentPane.add(getJComboBox());
        /* 添加是否过滤空目录的复选框 */
        contentPane.add(getJCheckBox());
        JButton button_1 = new JButton("选择导出路径");
        button_1.setFont(new Font("宋体", Font.PLAIN, 20));
        button_1.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
            }
        });
        button_1.setBounds(500, 103, 170, 62);
        contentPane.add(button_1);
        textField_1.setFont(new Font("宋体", Font.PLAIN, 20));
        textField_1.setBounds(100, 103, 380, 62);
        String defaultWordPath = geneDefaultWordPath();
        if (defaultWordPath != null) {
            textField_1.setText(defaultWordPath);
        }
        contentPane.add(textField_1);
        textField_1.setColumns(10);

        buttonReturn = new JButton("上一步");
        buttonReturn.setFont(new Font("宋体", Font.PLAIN, 20));
        buttonReturn.setBounds(50, 550, 200, 62);
        contentPane.add(buttonReturn);

        button_2 = new JButton("开始导出");
        button_2.setFont(new Font("宋体", Font.PLAIN, 20));
        button_2.setBounds(500, 550, 200, 62);
        contentPane.add(button_2);
        ((JPanel) contentPane).setOpaque(false);
        frame.add(contentPane);
        /* 点击设置导出路径按钮，选择导出文件路径并设置导出文件名称 */
        button_1.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent arg0) {
                JFileChooser chooser = new JFileChooser();
                chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

                FileNameExtensionFilter filter = new FileNameExtensionFilter("*.docx", "docx");
                chooser.setFileFilter(filter);
                chooser.showDialog(new JLabel(), "确定");

                File file = chooser.getSelectedFile();
                if (file == null) {
                    return;
                }

                String fname = chooser.getName(file); // 从文件名输入框中获取文件名
                if ((!fname.endsWith(".doc")) && (!fname.endsWith(".docx"))) {
                    file = new File(chooser.getCurrentDirectory(), fname + ".docx");
                }
                String path = file.getAbsolutePath();
                textField_1.setText(path);
            }
        });

        buttonReturn.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (treeFrame != null) {
                    treeFrame.setVisible(true);
                    frame.setVisible(false);
                }

            }

        });

        /* 点击开始导出按钮，将选择的内容导出到设置的文件中 */
        button_2.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent arg0) {
                String filePathName = textField_1.getText();
                if (StringUtils.isBlank(filePathName)) {
                    JOptionPane.showMessageDialog(null, "请选择导出文件路径", "错误", 0);
                    return;
                }
                if (StringUtils.isNotBlank(filePathName)) {

                    File file = new File(filePathName);
                    if (file.exists()) {
                        file.delete();
                    }
                    ExportToDocService service = new ExportToDocService();
                    try {
                        service.exportWord(rootElement, file.getAbsolutePath(), false);
                        frame.setVisible(false);
                        bClosed = true;
                    } catch (Exception e1) {
                        // TODO Auto-generated catch block
                        e1.printStackTrace();
                    }
                    // }

                }

            }
        });

    }

    private JPanel getJCheckBox() {
        JPanel jp = new JPanel();    //创建面板
        JLabel label1 = new JLabel("请选择是否过滤空白标题:         ");    //创建标签
        label1.setFont(new Font("宋体", Font.PLAIN, 30));
        //label1.setBounds();
        //label1.setBounds(50, 400, 100, 100);
        JCheckBox checkBox = new JCheckBox("");
        checkBox.setSelected(true);
        checkBox.setBounds(60, 420, 200, 62);
        checkBox.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                JCheckBox jCheckBox = (JCheckBox) e.getSource();
                if (jCheckBox.isSelected()) {
                    geneBlankContent = false;
                } else {
                    geneBlankContent = true;
                }
            }
        });
        //checkBox.setFont(new Font("宋体", Font.PLAIN, 30));
        jp.add(label1);
        jp.add(checkBox);
        jp.setVisible(true);
        jp.setBounds(50, 420, 600, 100);
        return jp;
    }

    /**
     * 新建JComboBox
     *
     * @return
     */
    private JPanel getJComboBox() {
        JPanel jp = new JPanel();    //创建面板
        JLabel label1 = new JLabel("请选择目录的展示深度:         ");    //创建标签
        label1.setFont(new Font("宋体", Font.PLAIN, 30));
        //label1.setBounds(50, 300, 200, 62);
        JComboBox cmb = new JComboBox();    //创建JComboBox//向下拉列表中添加一项
        cmb.addItem("1");
        cmb.addItem("2");
        cmb.addItem("3");
        cmb.addItem("4");
        cmb.addItem("5");
        cmb.addItem("6");
        cmb.addItem("7");
        cmb.addItem("8");
        cmb.setFont(new Font("宋体", Font.PLAIN, 30));
        cmb.setBounds(60, 300, 200, 62);
        cmb.setSelectedIndex(2);
        jp.add(label1);
        jp.add(cmb);
        jp.setVisible(true);
        jp.setBounds(50, 300, 600, 100);
        cmb.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                contentDisplayWide = (String) cmb.getSelectedItem();
            }
        });
        return jp;
    }

    /**
     * 生成word文档的默认路径
     *
     * @return
     */
    private String geneDefaultWordPath() {
        String absolutePath = Application.getInstance().getProject().getFile().getAbsolutePath();
        if (absolutePath != null) {
            File file = new File(absolutePath);
            String fileName = file.getName().substring(0, file.getName().indexOf("."));
            String parentFile = file.getParent();
            String docPath = parentFile + "/document/" + fileName + ".docx";
            File docFile = new File(docPath);
            if (!docFile.exists()) {
                docFile.mkdirs();
            }
            return docFile.getAbsolutePath();
        }
        return null;
    }

    public static WordElement getRootElement() {
        return rootElement;
    }

    public static void setRootElement(WordElement rootElement) {
        ExportWordUI.rootElement = rootElement;
    }

    public static JFrame getTreeFrame() {
        return treeFrame;
    }

    public static void setTreeFrame(JFrame treeFrame) {
        ExportWordUI.treeFrame = treeFrame;
    }

}
