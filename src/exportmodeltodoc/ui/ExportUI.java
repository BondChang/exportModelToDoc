package exportmodeltodoc.ui;

import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.UnsupportedEncodingException;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.apache.commons.lang.StringUtils;

import exportmodeltodoc.entity.WordTop;
import exportmodeltodoc.service.ExportToDocService;

public class ExportUI extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static boolean bClosed = true;
	private JPanel contentPane;

	private JTextField textField_1;
	private JButton button_2;
	private static ExportUI frame = null;

	private static WordTop wordTop = null;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					frame = new ExportUI();
					frame.setVisible(true);
					bClosed = false;
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public ExportUI() {

		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				bClosed = true;
			}
		});

		setTitle("导出doc文档");
		// setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 600, 314);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		JButton button_1 = new JButton("设置导出路径");
		button_1.setFont(new Font("宋体", Font.PLAIN, 20));
		button_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
			}
		});
		button_1.setBounds(15, 103, 170, 62);
		contentPane.add(button_1);

		textField_1 = new JTextField();
		textField_1.setFont(new Font("宋体", Font.PLAIN, 20));
		textField_1.setBounds(190, 103, 380, 62);
		contentPane.add(textField_1);
		textField_1.setColumns(10);

		button_2 = new JButton("开始导出");
		button_2.setFont(new Font("宋体", Font.PLAIN, 20));
		button_2.setBounds(132, 185, 289, 62);
		contentPane.add(button_2);

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

		/* 点击开始导出按钮，将选择的内容导出到设置的文件中 */
		button_2.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				String filePathName = textField_1.getText();
				if (StringUtils.isNotBlank(filePathName)) {
					String jarWholePath = ExportUI.class.getProtectionDomain().getCodeSource().getLocation().getFile();

					try {
						jarWholePath = java.net.URLDecoder.decode(jarWholePath, "UTF-8");
					} catch (UnsupportedEncodingException e) {
						System.out.println(e.toString());
					}
					String jarPath = new File(jarWholePath).getParentFile().getAbsolutePath();
					String templateFilePath = jarPath + "\\exp_sysml_template.docx";
//					String templateFilePath = "C:\\z-temp\\exp_sysml_template.docx";
					System.out.println(templateFilePath);

					File file = new File(filePathName);
					if (file.exists()) {
						file.delete();
					}

					File templateFile = new File(templateFilePath);
					if (templateFile.exists()) {
						ExportToDocService service = new ExportToDocService();
						try {

							service.exportDoc(templateFilePath, filePathName, wordTop);
							frame.setVisible(false);
							bClosed = true;
						} catch (Exception e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					} else {

					}

				}

			}
		});
	}

	public static WordTop getWordTop() {
		return wordTop;
	}

	public static void setWordTop(WordTop wordTop) {
		ExportUI.wordTop = wordTop;
	}

}
