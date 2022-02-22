package checkbox.tree.ui;

import diagram.entity.ExportType;
import exportmodeltodoc.entity.WordElement;
import exportmodeltodoc.ui.ExportWordUI;

import javax.swing.*;
import javax.swing.tree.DefaultTreeModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

public class SelectTree extends JFrame {
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public static boolean bClosed = false;
    public static CheckBoxTreeNode rootNode;
    JButton nextButton = new JButton("��һ��");

    public SelectTree(WordElement rootElement) {
//		addWindowListener(new WindowAdapter() {
//			public void windowClosing(WindowEvent e) {
//				bClosed = true;
//			}
//		});

        JFrame frame = new JFrame("word�ĵ�����");
        frame.setVisible(true);
        frame.setBounds(200, 200, 800, 800);
        JTree tree = new JTree();

        if (rootElement != null) {
            rootNode = new CheckBoxTreeNode(rootElement);
            geneSelectTree(rootNode, rootElement);

            Container container = frame.getContentPane();
            JPanel textPanel = new JPanel();
            JLabel textLable = new JLabel("��ӭʹ���ĵ��������ܣ������·���ѡҪ�����Ľڵ�");
            textLable.setFont(new Font("����", Font.PLAIN, 25));
            textLable.setForeground(Color.RED);
            textPanel.add(textLable);
            JPanel treePanel = new JPanel();
            JPanel nextButtonPanel = new JPanel();
            /* ���ÿ�Ƭ���� */
            final CardLayout cardLayout = new CardLayout();
            container.setLayout(new BorderLayout());
            container.add(textPanel, BorderLayout.PAGE_START);
            container.add(treePanel, BorderLayout.CENTER);
            container.add(nextButtonPanel, BorderLayout.PAGE_END);
            nextButtonPanel.setLayout(new FlowLayout());
            frame.setBounds(200, 200, 800, 800);
            /* �����״�ṹ�Ĳ��� */
            JScrollPane jsp = new JScrollPane(tree);
            treePanel.add(jsp);
            treePanel.setLayout(cardLayout);
            textPanel.setLayout(new FlowLayout());
            /* ��Ӱ�ť���� */
            nextButtonPanel.add(nextButton);

            DefaultTreeModel model = new DefaultTreeModel(rootNode);
            tree.addMouseListener(new CheckBoxTreeNodeSelectionListener());
            tree.setModel(model);
            tree.setCellRenderer(new CheckBoxTreeCellRenderer());
            tree.setFont(new java.awt.Font("����", Font.BOLD, 30));

            /* ��nextButton�¼� */
            bindNextButton(rootElement, frame);
        }

    }

    private void bindNextButton(WordElement rootElement, JFrame frame) {
        nextButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                int selectNodeCount = 0;
                if (rootNode == null) {
                    JOptionPane.showMessageDialog(null, "���ڵ�Ϊ��", "����", 0);
                    return;
                }
                Enumeration enumeration = rootNode.breadthFirstEnumeration();
                while (enumeration.hasMoreElements()) {
                    CheckBoxTreeNode node = (CheckBoxTreeNode) enumeration.nextElement();
                    Object userObject = node.getUserObject();
                    if (userObject instanceof WordElement) {
                        if (node.isSelected()) {
                            ((WordElement) userObject).setSelect(true);
                            selectNodeCount++;
                        } else {
                            ((WordElement) userObject).setSelect(false);
                        }
                    }

                }
                if (selectNodeCount == 0) {
                    JOptionPane.showMessageDialog(null, "δѡ���κνڵ�", "����", 0);
                    return;
                }
                frame.setVisible(false);
                /* ����ѡ����Ϣ������ӹ�ѡ�ˣ���Ĭ�ϸ�Ҳ��ѡ�� */
                setSelectInfo(rootElement);
                // List<WordElement> wordElementList = wordElement.getWordElementList();
                ExportWordUI ui = new ExportWordUI();
                ui.setRootElement(rootElement);
                ui.setTreeFrame(frame);
//				if (ui.bClosed) {
//					ui.main(null);
//				}

            }

        });

    }

    private void setSelectInfo(WordElement rootElement) {
        if (rootElement != null) {

            List<WordElement> wordElementList = rootElement.getWordElementList();
            if (wordElementList != null && wordElementList.size() > 0) {
                for (WordElement wordElement : wordElementList) {
                    if (!wordElement.isSelect() && wordElement.getWordElementList() != null
                            && wordElement.getWordElementList().size() > 0) {

                        boolean isSelect = false;
                        isSelect = judgeIsChildIsSelect(wordElement, isSelect);
                        if (isSelect) {
                            wordElement.setSelect(isSelect);
                        }
                        for (WordElement subElement : wordElement.getWordElementList()) {
                            if (subElement != null && !subElement.isSelect()) {
                                isSelect = false;
                                isSelect = judgeIsChildIsSelect(subElement, isSelect);
                                if (isSelect) {
                                    subElement.setSelect(isSelect);
                                }
                                setSelectInfo(subElement);
                            }
                        }
                    }
                }
            }
        }

    }

    private boolean judgeIsChildIsSelect(WordElement wordElement, boolean isSelect) {
        List<WordElement> subWordElementList = wordElement.getWordElementList();
        if (wordElement.getWordElementList() != null && wordElement.getWordElementList().size() > 0) {
            for (WordElement subElement : subWordElementList) {
                if (subElement.isSelect()) {
                    return true;
                } else {
                    isSelect = judgeIsChildIsSelect(subElement, isSelect);
                    if (isSelect) {
                        return true;
                    }
                }

            }
        }
        return isSelect;

    }

    private void geneSelectTree(CheckBoxTreeNode rootNode, WordElement wordElement) {
        List<WordElement> wordElementList = wordElement.getWordElementList();
        if (wordElementList != null && wordElementList.size() > 0) {
            for (WordElement subElement : wordElementList) {
                CheckBoxTreeNode subNode = new CheckBoxTreeNode(subElement);
                rootNode.add(subNode);
                geneSelectTree(subNode, subElement);
            }
        }
    }

}
