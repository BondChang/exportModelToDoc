package exportmodeltodoc.ui;

import com.nomagic.magicdraw.core.Application;
import exportmodeltodoc.entity.WordElement;
import exportmodeltodoc.service.ExportToDocService;
import org.apache.commons.lang.StringUtils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
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


    /**
     * Create the frame.
     */
    public ExportWordUI() {
        final JTextField textField_1 = new JTextField();
        JFrame frame = new JFrame();
        JPanel contentPane = new JPanel();
        JButton button_2 = new JButton();
        JButton buttonReturn = new JButton();
        frame.setVisible(true);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                bClosed = true;
            }
        });

        frame.setTitle("����doc�ĵ�");
        // setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setBounds(200, 200, 800, 800);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));

        contentPane.setLayout(null);

        JButton button_1 = new JButton("ѡ�񵼳�·��");
        button_1.setFont(new Font("����", Font.PLAIN, 20));
        button_1.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
            }
        });
        button_1.setBounds(500, 103, 170, 62);
        contentPane.add(button_1);
        textField_1.setFont(new Font("����", Font.PLAIN, 20));
        textField_1.setBounds(100, 103, 380, 62);
        String defaultWordPath = geneDefaultWordPath();
        if(defaultWordPath!=null){
            textField_1.setText(defaultWordPath);
        }
        contentPane.add(textField_1);
        textField_1.setColumns(10);

        buttonReturn = new JButton("��һ��");
        buttonReturn.setFont(new Font("����", Font.PLAIN, 20));
        buttonReturn.setBounds(50, 550, 200, 62);
        contentPane.add(buttonReturn);

        button_2 = new JButton("��ʼ����");
        button_2.setFont(new Font("����", Font.PLAIN, 20));
        button_2.setBounds(500, 550, 200, 62);
        contentPane.add(button_2);
        ((JPanel) contentPane).setOpaque(false);
        frame.add(contentPane);
        /* ������õ���·����ť��ѡ�񵼳��ļ�·�������õ����ļ����� */
        button_1.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent arg0) {
                JFileChooser chooser = new JFileChooser();
                chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

                FileNameExtensionFilter filter = new FileNameExtensionFilter("*.docx", "docx");
                chooser.setFileFilter(filter);
                chooser.showDialog(new JLabel(), "ȷ��");

                File file = chooser.getSelectedFile();
                if (file == null) {
                    return;
                }

                String fname = chooser.getName(file); // ���ļ���������л�ȡ�ļ���
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

        /* �����ʼ������ť����ѡ������ݵ��������õ��ļ��� */
        button_2.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent arg0) {
                String filePathName = textField_1.getText();
                if (StringUtils.isBlank(filePathName)) {
                    JOptionPane.showMessageDialog(null, "��ѡ�񵼳��ļ�·��", "����", 0);
                    return;
                }
                if (StringUtils.isNotBlank(filePathName)) {

                    File file = new File(filePathName);
                    if (file.exists()) {
                        file.delete();
                    }
                    ExportToDocService service = new ExportToDocService();
                    try {
//                        if (exportType == ExportType.STRUCTEXPORT) {
//                            for (int i = 0; i < wordElementList.size(); i++) {
//                                WordElement wordElement = wordElementList.get(i);
//                                String fileName = file.getName().substring(0, file.getName().indexOf("."));
//                                String filePath = file.getParent();
//                                service.exportWord(wordElement, filePath + "/" + fileName + i + ".docx");
//                            }
//                        } else {
//                            WordElement wordElement = wordElementList.get(0);
//                            String fileName = file.getName().substring(0, file.getName().indexOf("."));
//                            String filePath = file.getParent();
//                            service.exportWord(wordElement, filePath + "/" + fileName + ".docx");
//                        }
                        service.exportWord(rootElement, file.getName());
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

    /**
     * ����word�ĵ���Ĭ��·��
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
