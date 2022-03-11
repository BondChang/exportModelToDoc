package exportmodeltodoc.service;

import com.nomagic.magicdraw.core.Application;
import com.nomagic.magicdraw.export.image.ImageExporter;
import com.nomagic.magicdraw.uml.symbols.DiagramPresentationElement;
import com.nomagic.uml2.ext.magicdraw.activities.mdfundamentalactivities.Activity;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Diagram;
import com.spire.doc.*;
import com.spire.doc.collections.ListLevelCollection;
import com.spire.doc.documents.*;
import com.spire.doc.fields.DocPicture;
import com.spire.doc.fields.TableOfContent;
import com.spire.doc.fields.TextRange;
import diagram.entity.*;
import exportmodeltodoc.action.ExportDocAction;
import exportmodeltodoc.entity.*;
import exportmodeltodoc.ui.ExportWordUI;
import org.apache.commons.lang.StringUtils;

import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

public class ExportToDocService {
    private static String deleteFileName = null;

    /**
     * ����word�ĵ������
     *
     * @param rootElement
     * @param filePathName
     */
    public void exportWord(WordElement rootElement, String filePathName, boolean isInstance) {
        if (isInstance == false && rootElement.isHasInstance()) {
            deleteFileName = filePathName;
        }
        List<WordElement> wordElementList = rootElement.getWordElementList();
        if (wordElementList == null || wordElementList.size() == 0) {
            /* ���ɽ����б����word�ĵ� */
            geneTitleWord(rootElement, filePathName);
            return;
        }

        Document document = addStyleToDoc();

        /* ÿ��Section��ʾһ���ڣ��˳����зֳ�3�ֽڡ�����Ŀ��Ŀ¼�����ģ� */
        Section titleSection = document.addSection();

        Section catalogueSection = document.addSection();
        /* ������ĵ�Session */
        Section contentSession = document.addSection();
        /* ���Ŀ¼ */
        insertCatalogue(document, catalogueSection);
        /* �ж��Ƿ���ʵ���� */
        if (isInstance) {
            /* ����������� */
            addContentInfo(wordElementList, contentSession, filePathName, document);
        } else {
            /* ��Ӹ������� */
            addHeadTitle(rootElement, contentSession, filePathName, document);
        }
        /* ����Ŀ¼�� */
        document.updateTableOfContents();

        /* �����ҳ���� */
        addDocTitle(titleSection, rootElement);

        /* ��ҳ�����ҳ�� */
        addFootPageNum(document);

        /* �����ĵ� */
        document.saveToFile(filePathName, FileFormat.Docx);
        /* ɾ�����ɵĶ����ļ� */
        delFile();
    }

    /**
     * ɾ�����ɵĶ����ļ�
     */
    private void delFile() {
        if (StringUtils.isNotBlank(deleteFileName)) {
            File delFile = new File(deleteFileName);
            if (delFile.exists()) {
                delFile.delete();
            }
        }
    }

    /**
     * ���Document�ĸ�ʽ
     *
     * @return
     */
    private Document addStyleToDoc() {
        Document document = new Document();
        /* ��Ӷ����Style����ͬid�����ظ���� */
        addParaStyle(document);
        /* ��ӱ����Style */
        addListStyle(document);
        return document;
    }

    /**
     * ���ɽ��������word�ĵ�
     *
     * @param rootElement
     * @param filePathName
     */
    private void geneTitleWord(WordElement rootElement, String filePathName) {
        Document document = new Document();
        Section titleSection = document.addSection();
        /* �����ҳ���� */
        addDocTitle(titleSection, rootElement);
        document.saveToFile(filePathName, FileFormat.Docx);
    }

    /**
     * ��Ӷ���style
     *
     * @param document
     */
    private void addParaStyle(Document document) {
        ParagraphStyle style = new ParagraphStyle(document);
        style.setName("paraStyle");
        style.getCharacterFormat().setFontName("����");
        style.getCharacterFormat().setFontSize(11f);
        document.getStyles().add(style);

    }

    /**
     * ����ĵ��ı���
     *
     * @param headSection
     * @param rootElement
     */
    private void addDocTitle(Section headSection, WordElement rootElement) {
        Paragraph para = headSection.addParagraph();
        para.appendText(rootElement.getElementName());
        // ���õ�һ������Ķ��뷽ʽ
        para.getFormat().setHorizontalAlignment(HorizontalAlignment.Center);
        para.applyStyle(BuiltinStyle.Title);
        // para1.applyStyle("titleStyle");
        para.getListFormat().setListLevelNumber(rootElement.getWide());

    }

    /**
     * ����ĵ���Ŀ¼�ṹ
     *
     * @param doc
     * @param section
     */
    private void insertCatalogue(Document doc, Section section) {
        Paragraph para = section.addParagraph();
        TextRange tr = para.appendText("Ŀ ¼");
        tr.getCharacterFormat().setBold(true);
        tr.getCharacterFormat().setTextColor(Color.gray);
        doc.getSections().get(1).getParagraphs().insert(0, para);
        para.getFormat().setHorizontalAlignment(HorizontalAlignment.Center);
        // ���öκ���
        para.getFormat().setAfterSpacing(10);
        TableOfContent toc = new TableOfContent(doc, "{\\o \"1-" + ExportWordUI.contentDisplayWide + "\" \\h \\z \\u}");
        para = section.addParagraph();
        para.getItems().add(toc);
        para.appendFieldMark(FieldMarkType.Field_Separator);
        para.appendText("TOC");
        para.appendFieldMark(FieldMarkType.Field_End);

        doc.setTOC(toc);

    }

    /**
     * ��ҳ�����ҳ��
     *
     * @param document
     */
    private void addFootPageNum(Document document) {
        // ��ȡ��һ�����е�ҳ��
        HeaderFooter footer = document.getSections().get(2).getHeadersFooters().getFooter();

        // ��Ӷ��䵽ҳ��
        Paragraph footerParagraph = footer.addParagraph();

        // ������֡�ҳ�������ҳ���򵽶���
        footerParagraph.appendText("��");
        footerParagraph.appendField("page number", FieldType.Field_Page);
        footerParagraph.appendText("ҳ ��");
        footerParagraph.appendField("number of pages", FieldType.Field_Num_Pages);
        footerParagraph.appendText("ҳ");

        // ���������
        footerParagraph.getFormat().setHorizontalAlignment(HorizontalAlignment.Center);

    }

    /**
     * ����part����ӱ���ͱ��
     *
     * @param rootElement
     * @param section
     * @param filePathName
     */
    private void addHeadTitle(WordElement rootElement, Section section, String filePathName, Document document) {
        if (rootElement.isHasInstance()) {
            /* ���ɶ�����word�ĵ� */
            geneInstanceWord(rootElement, filePathName);
            return;
        }
        List<WordElement> wordElementList = rootElement.getWordElementList();
        addContentInfo(wordElementList, section, filePathName, document);

//        if (wordElementList != null && wordElementList.size() > 0) {
//            List<WordElement> leafNodeList = new ArrayList<WordElement>();
//            List<WordElement> notLeafNodeList = new ArrayList<WordElement>();
//            distinguishNode(leafNodeList, notLeafNodeList, wordElementList);
//            /* Ҷ�ӽڵ����ɱ�� */
//            // if (!leafNodeList.isEmpty() && leafNodeList.get(0).getWide() > 1) {
//            if (!leafNodeList.isEmpty()) {
//                for (WordElement wordElement : leafNodeList) {
//                    geneDiagramImage(wordElement, section, filePathName);
//                }
//                if (ExportWordUI.exportType == ExportType.STRUCTEXPORT) {
//                    String[] header = {"���", "����", "ֵ", "ʵ��ֵ"};
//                    String[][] data = new String[leafNodeList.size()][header.length];
//                    int index = 1;
//                    for (int i = 0; i < leafNodeList.size(); i++) {
//                        data[i][0] = String.valueOf(index++);
//                        data[i][1] = leafNodeList.get(i).getElementName();
//                        data[i][2] = leafNodeList.get(i).getElementValue();
//                        data[i][3] = "";
//                    }
//                    /* ��ӱ�� */
//                    addTable(section, header, data);
//                }
//            }
//            if (!notLeafNodeList.isEmpty()) {
//                /* ��Ҷ�ӽ�����ɱ��� */
//                for (WordElement wordElement : notLeafNodeList) {
//                    Paragraph para = section.addParagraph();
//                    para.appendText(wordElement.getElementName());
//                    para.applyStyle(getParaStyle(wordElement.getWide()));
//                    para.getListFormat().setListLevelNumber(wordElement.getWide() - 1);
//                    para.getListFormat().applyStyle("CustomStyle");
//                    geneDiagramImage(wordElement, section, filePathName);
//                    /* ���ע����Ϣ */
//                    String elementName = wordElement.getElementName();
//
//                    String commentStr = ExportDocAction.commentMap.getOrDefault(elementName, null);
//                    if (commentStr != null) {
//                        Paragraph commentPara = section.addParagraph();
//                        commentPara.appendText(commentStr);
//
//                        commentPara.getFormat().setFirstLineIndent(25f);
//                        commentPara.getFormat().setAfterSpacing(10f);
//                    }
//                    addHeadTitle(wordElement, section, filePathName);
//                }
//            }
//
//        }
    }

    /**
     * ���word����������
     *
     * @param wordElementList
     * @param section
     * @param filePathName
     */
    private void addContentInfo(List<WordElement> wordElementList, Section section, String filePathName, Document document) {
        /* ������е�package��class��part property */
        List<WordElement> contentNodeList = new ArrayList<>();
        /* ������е�instance package */
        List<WordElement> instancePackageNodeList = new ArrayList<>();
        /* ������е�port */
        List<WordElement> portNodeList = new ArrayList<>();
        /* ������е� value Property */
        List<WordElement> valueNodeList = new ArrayList<>();
        /* ������е�diagram */
        List<WordElement> diagramNodeList = new ArrayList<>();
        /* ������еĸ�����Ϣ */
        List<WordElement> attachedFileList = new ArrayList<>();
        /* С�������� */
        int subTitleIndex = 0;
        //geneDiagramImage(rootElement, section, filePathName);
        if (wordElementList != null && wordElementList.size() > 0) {
            distinguishNode(contentNodeList, instancePackageNodeList, portNodeList, valueNodeList, diagramNodeList, attachedFileList, wordElementList);
            /* ���ɱ��㼶��port��� */
            if (portNodeList != null && portNodeList.size() > 0) {
                subTitleIndex++;
                /* ��Ӷ˿ڵı����Ϣ */
                genePortTable(portNodeList, section, subTitleIndex);
            }
            if (valueNodeList != null && valueNodeList.size() > 0) {
                subTitleIndex++;
                geneValueTable(valueNodeList, section, subTitleIndex);
            }
            if (diagramNodeList != null && diagramNodeList.size() > 0) {
                geneDiagramImage(diagramNodeList, section, filePathName, subTitleIndex);
            }
            if (attachedFileList != null && attachedFileList.size() > 0) {
                geneAttachedFile(attachedFileList, section, filePathName, subTitleIndex, document);
            }
            if (contentNodeList != null && contentNodeList.size() > 0) {
                for (WordElement contentWord : contentNodeList) {
                    if (contentWord.getElementName().contains("�Զ�Ѳ��")) {
                        System.out.println("123");
                    }
                    if (ExportWordUI.geneBlankContent == false) {
                        boolean isBlankContent = true;
                        isBlankContent = judgeIsBlankContent(contentWord);
                        if (isBlankContent) {
                            continue;
                        }
                    }
                    Paragraph para = section.addParagraph();
                    para.appendText(contentWord.getElementName());
//                    if (contentWord.getDiagramInfoList() != null && contentWord.getDiagramInfoList().size() > 0) {
//                        addDiagramImage(contentWord.getDiagramInfoList(), subTitleIndex, filePathName, section);
//                    }
//                    if (contentWord.getElementName().equals("��е��ȡ��")) {
//                        System.out.println();
//                    }
                    para.applyStyle(getParaStyle(contentWord.getWide()));
                    if (contentWord.getWide() >= 8) {
                        para.getListFormat().setListLevelNumber(8);
                    } else {
                        para.getListFormat().setListLevelNumber(contentWord.getWide() - 1);
                    }
                    para.getListFormat().applyStyle("CustomStyle");
                    addHeadTitle(contentWord, section, filePathName, document);
                }
            }
            if (instancePackageNodeList != null && instancePackageNodeList.size() > 0) {
                for (WordElement instance : instancePackageNodeList) {
                    geneInstanceWord(instance, filePathName);
                }
            }
        }
    }

    private void geneAttachedFile(List<WordElement> attachedFileList, Section section, String filePathName, int subTitleIndex, Document document) {
        for (WordElement wordElement : attachedFileList) {
            String elementAttachedName = wordElement.getElementAttachedName();
            File attachedFile = new File(new File(filePathName).getParentFile().getParentFile().getAbsolutePath() + "/" + elementAttachedName);
            if (attachedFile.exists()) {
                String imagePath = System.getProperty("user.dir") + "/image";
                DocPicture docPicture = new DocPicture(document);
                /* ˵����excel */
                if (elementAttachedName.endsWith(".xlsx") || elementAttachedName.endsWith(".xls")) {
                    subTitleIndex++;
                    String excelImagePath = imagePath + "/excel.png";
                    addAttachedFile(section, subTitleIndex, excelImagePath, OleObjectType.Excel_Worksheet, wordElement, docPicture, attachedFile);
                    //addAttachedFile(section, subTitleIndex, excelImagePath, OleObjectType.Excel_Worksheet, wordElement, docPicture,attachedFile);
                }
                /* ˵����word */
                else if (elementAttachedName.equals(".docx") || elementAttachedName.equals("doc")) {
                    subTitleIndex++;
                    String wordImagePath = imagePath + "/word.jpg";
                    addAttachedFile(section, subTitleIndex, wordImagePath, OleObjectType.Word_Document, wordElement, docPicture, attachedFile);
                }
            }
        }
    }

    private void addAttachedFile(Section section, int subTitleIndex, String excelImagePath, OleObjectType oleObjectType, WordElement wordElement, DocPicture docPicture, File attachedFile) {
        if (subTitleIndex == 1) {
            excelImagePath = "C:\\Users\\methon\\eclipse2019-workspace\\exportModelToDoc\\image\\excel.png";
        } else {
            excelImagePath = "C:\\Users\\methon\\eclipse2019-workspace\\exportModelToDoc\\image\\word.jpg";
        }
        Paragraph paragraphTitle = section.addParagraph();
        paragraphTitle.appendText("(" + subTitleIndex + ")\t" + wordElement.getElementName());
        Paragraph paragraphContent = section.addParagraph();
        //DocPicture docPicture1 = paragraphContent.appendPicture(excelImagePath);
        //paragraphContent.appendOleObject(attachedFile.getAbsolutePath(), docPicture1,oleObjectType.)
        //docPicture.loadImage(excelImagePath);
        paragraphContent.appendHyperlink(attachedFile.getAbsolutePath(), wordElement.getElementAttachedName(), HyperlinkType.File_Link);
        //paragraphContent.appendOleObject(attachedFile.getAbsolutePath(), docPicture, oleObjectType);
        paragraphContent.getFormat().setLeftIndent(30f);
        //paragraphContent.getFormat().setHorizontalAlignment(HorizontalAlignment.Center);
    }

    private boolean judgeIsBlankContent(WordElement contentWord) {
        List<WordElement> wordElementList = contentWord.getWordElementList();
        if (wordElementList == null || wordElementList.size() == 0) {
            return true;
        }
        for (WordElement wordElement : wordElementList) {
            if ((wordElement.getElementType() != (ElementType.PACKAGE_TYPE)) && ((wordElement.getElementType() != (ElementType.OTHER_TYPE)))) {
                return false;
            }
            boolean isBlankContent = judgeIsBlankContent(wordElement);
            if (!isBlankContent) {
                return false;
            }
        }
        return true;
    }

    private void geneInstanceWord(WordElement rootElement, String filePathName) {
        int instanceSize = ExportDocAction.instanceListList.size();
        if (instanceSize <= 0) {
            return;
        }
        /* ����ʵ�����������������ݽṹ�������������Word�ĵ� */
        for (int i = 0; i < instanceSize; i++) {
            List<InstanceContent> instanceContentList = ExportDocAction.instanceListList.get(i);
            //Map<String, String> name2ValueMap = ExportDocAction.instanceMapList.get(i);
            WordElement wordElement = setValue2rootElement(rootElement, instanceContentList);
            File file = new File(filePathName);
            String fileName = file.getName().substring(0, file.getName().indexOf("."));
            String filePath = file.getParent();
            exportWord(wordElement, filePath + "/" + fileName + i + ".docx", true);
        }
    }

    /**
     * ����Instance��ֵ
     *
     * @param rootElement
     * @param instanceContentList
     * @return
     */
    public static WordElement setValue2rootElement(WordElement rootElement, List<InstanceContent> instanceContentList) {
        List<DiagramInfo> diagramInfoList = new ArrayList<>();
        rootElement.setDiagramInfoList(diagramInfoList);
        WordElement wordElement = deepCopy(rootElement);
        //wordElement.setWordElementList(deepCopy(rootElement.getWordElementList()));
        setValue(wordElement, instanceContentList);
        return wordElement;
    }

    /**
     * ִ�����
     *
     * @param obj
     * @param <T>
     * @return
     */
    public static <T extends Serializable> T deepCopy(T obj) {
        T cloneObj = null;
        try {
            //д���ֽ���
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            ObjectOutputStream obs = new ObjectOutputStream(out);
            obs.writeObject(obj);
            obs.close();

            //�����ڴ棬д��ԭʼ���������¶���
            ByteArrayInputStream ios = new ByteArrayInputStream(out.toByteArray());
            ObjectInputStream ois = new ObjectInputStream(ios);
            //�������ɵ��¶���
            cloneObj = (T) ois.readObject();
            ois.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return cloneObj;
    }

//    /**
//     * ִ�����
//     *
//     * @param src
//     * @param <T>
//     * @return
//     * @throws IOException
//     * @throws ClassNotFoundException
//     */
//    public static <T> List<T> deepCopy(List<T> src) throws IOException, ClassNotFoundException {
//        ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
//        ObjectOutputStream out = new ObjectOutputStream(byteOut);
//        out.writeObject(src);
//        ByteArrayInputStream byteIn = new ByteArrayInputStream(byteOut.toByteArray());
//        ObjectInputStream in = new ObjectInputStream(byteIn);
//        return (List<T>) in.readObject();
//    }

    /**
     * ����ֵ
     *
     * @param wordElement
     * @param instanceContentList
     */
    private static void setValue(WordElement wordElement, List<InstanceContent> instanceContentList) {
        if (wordElement != null) {
            String elementQualifiedName = wordElement.getQualifiedName();
            String elementReqValue = getInstanceReqValue(elementQualifiedName, instanceContentList);
            String elementRealValue = getInstanceRealValue(elementQualifiedName, instanceContentList);
            String elementAttachedName = getInstanceAttachedName(elementQualifiedName, instanceContentList);
            //String elementValue = indexOfMap.getOrDefault(elementQualifiedName, null);
            setInstanceValue(wordElement, elementReqValue, elementRealValue, elementAttachedName);
            List<WordElement> wordElementList = wordElement.getWordElementList();
            if (wordElementList != null && wordElementList.size() > 0) {
                for (WordElement subWordElement : wordElementList) {
                    if (Objects.equals(subWordElement.getElementName(), wordElement.getElementName())) {
                        setInstanceValue(subWordElement, elementReqValue, elementRealValue, elementAttachedName);
                    }
                }
            }
            if (wordElementList != null && wordElementList.size() > 0) {
                for (WordElement subElement : wordElementList) {
                    setValue(subElement, instanceContentList);
                }
            }
        }

    }

    /**
     * ����ʵ����ֵ
     *
     * @param wordElement
     * @param elementReqValue
     * @param elementRealValue
     * @param elementAttachedName
     */
    private static void setInstanceValue(WordElement wordElement, String elementReqValue, String elementRealValue, String elementAttachedName) {
        if (StringUtils.isNotBlank(elementReqValue)) {
            wordElement.setElementRequireValue(elementReqValue);
        }
        if (StringUtils.isNotBlank(elementRealValue)) {
            wordElement.setElementRealValue(elementRealValue);
        }
        if (StringUtils.isNotBlank(elementAttachedName)) {
            wordElement.setElementAttachedName(elementAttachedName);
        }
    }

    /**
     * ��ȡ����������
     *
     * @param elementQualifiedName
     * @param instanceContentList
     * @return
     */
    private static String getInstanceAttachedName(String elementQualifiedName, List<InstanceContent> instanceContentList) {
        if (instanceContentList == null || instanceContentList.size() <= 0) {
            return null;
        }
        for (InstanceContent instanceContent : instanceContentList) {
            if (instanceContent.getName().equals(elementQualifiedName)) {
                return instanceContent.getAttachedName();
            }
        }
        return null;
    }

    /**
     * ��ȡʵ��ֵ
     *
     * @param elementQualifiedName
     * @param instanceContentList
     * @return
     */
    private static String getInstanceRealValue(String elementQualifiedName, List<InstanceContent> instanceContentList) {
        if (instanceContentList == null || instanceContentList.size() <= 0) {
            return null;
        }
        for (InstanceContent instanceContent : instanceContentList) {
            if (instanceContent.getName().equals(elementQualifiedName)) {
                return instanceContent.getRealValue();
            }
        }
        return null;
    }

    /**
     * ��ȡҪ��ֵ
     *
     * @param elementQualifiedName
     * @param instanceContentList
     * @return
     */
    private static String getInstanceReqValue(String elementQualifiedName, List<InstanceContent> instanceContentList) {
        if (instanceContentList == null || instanceContentList.size() <= 0) {
            return null;
        }
        for (InstanceContent instanceContent : instanceContentList) {
            if (instanceContent.getName().equals(elementQualifiedName)) {
                return instanceContent.getRequireValue();
            }
        }
        return null;
    }

    /**
     * ����value�ı��
     *
     * @param valueNodeList
     * @param section
     * @param subTitleIndex
     */
    private void geneValueTable(List<WordElement> valueNodeList, Section section, int subTitleIndex) {
        Paragraph paragraph = section.addParagraph();
        paragraph.appendText("(" + subTitleIndex + ")\tValue Property");
        /* ���С���� */
        String[] header = {"���", "����", "ֵ"};
        String[][] data = new String[valueNodeList.size()][header.length];
        int index = 1;
        for (int i = 0; i < valueNodeList.size(); i++) {
            data[i][0] = String.valueOf(index++);
            data[i][1] = valueNodeList.get(i).getElementName();
            if (data[i][1].equals("����ӿ�˵��")) {
                System.out.println();
            }
            data[i][2] = valueNodeList.get(i).getElementRequireValue();
        }
        /* ��ӱ�� */
        addTable(section, header, data);
    }

    /**
     * ��ӱ��Ķ˿���Ϣ
     *
     * @param portNodeList
     * @param section
     * @param subTitleIndex
     */
    private void genePortTable(List<WordElement> portNodeList, Section section, int subTitleIndex) {
        Paragraph paragraph = section.addParagraph();
        paragraph.appendText("(" + subTitleIndex + ")\t�˿�");
        /* ���С���� */
        String[] header = {"���", "����", "ֵ"};
        String[][] data = new String[portNodeList.size()][header.length];
        int index = 1;
        for (int i = 0; i < portNodeList.size(); i++) {
            data[i][0] = String.valueOf(index++);
            data[i][1] = portNodeList.get(i).getElementName();
            data[i][2] = portNodeList.get(i).getElementRequireValue();
        }
        /* ��ӱ�� */
        addTable(section, header, data);
    }

    /**
     * ���ֲ�ͬ��Node����
     *
     * @param contentNodeList
     * @param instancePackageNodeList
     * @param portNodeList
     * @param valueNodeList
     * @param diagramNodeList
     * @param wordElementList
     */
    private void distinguishNode(List<WordElement> contentNodeList, List<WordElement> instancePackageNodeList, List<WordElement> portNodeList, List<WordElement> valueNodeList, List<WordElement> diagramNodeList, List<WordElement> attachedFileList, List<WordElement> wordElementList) {
        for (WordElement wordElement : wordElementList) {
            if (wordElement.isSelect()) {
                int elementType = wordElement.getElementType();
                String elementAttachedName = wordElement.getElementAttachedName();
                if (StringUtils.isNotBlank(elementAttachedName)) {
                    attachedFileList.add(wordElement);
                } else if (elementType == ElementType.MODEL_TYPE || elementType == ElementType.CLASS_TYPE || elementType == ElementType.PART_PROPERTY_TYPE) {
                    contentNodeList.add(wordElement);
                } else if (elementType == ElementType.PACKAGE_TYPE) {
                    if (wordElement.isHasInstance()) {
                        instancePackageNodeList.add(wordElement);
                    } else {
                        contentNodeList.add(wordElement);
                    }
                } else if (elementType == ElementType.PORT_TYPE) {
                    portNodeList.add(wordElement);
                } else if (elementType == ElementType.VALUE_PROPERTY_TYPE) {
                    valueNodeList.add(wordElement);
                } else if (elementType == ElementType.DIAGRAM_TYPE) {
                    diagramNodeList.add(wordElement);
                }
            }
        }
    }

    private void geneDiagramImage(List<WordElement> diagramWordList, Section section, String filePathName, int subTitleIndex) {
        for (WordElement diagramElement : diagramWordList) {
            if (diagramElement.getDiagramInfoList() != null && diagramElement.getDiagramInfoList().size() > 0) {
                addDiagramImage(diagramElement.getDiagramInfoList(), subTitleIndex, filePathName, section);
            }
//            if (diagramElement.getWordElementList() != null && diagramElement.getWordElementList().size() > 0) {
//                for (WordElement wordElement : diagramWordList) {
//                    if (wordElement.getDiagramInfoList() != null && wordElement.getDiagramInfoList().size() > 0) {
//                        addDiagramImage(wordElement.getDiagramInfoList(), subTitleIndex, filePathName, section);
//                    }
//                }
//            }
        }


    }

    /**
     * ��ӾŴ�ͼ����Ϣ
     *
     * @param diagramInfoList
     * @param subTitleIndex
     * @param filePathName
     * @param section
     */
    private void addDiagramImage(List<DiagramInfo> diagramInfoList, int subTitleIndex, String filePathName, Section section) {
        for (DiagramInfo diagramInfo : diagramInfoList) {
            subTitleIndex++;
            Paragraph paragraph = section.addParagraph();
            paragraph.appendText("(" + subTitleIndex + ")\t" + diagramInfo.getDiagramName());
            geneContentInfo(diagramInfo, section, filePathName);
        }
    }

    public ListStyle addListStyle(Document document) {
        ListStyle listStyle = new ListStyle(document, ListType.Numbered);
        listStyle.setName("CustomStyle");
        float level0TextPosition = listStyle.getLevels().get(0).getTextPosition();
        float level0NumPosition = listStyle.getLevels().get(0).getNumberPosition();
        // Set the list pattern type and number prefix of each level
        BuiltinStyle defaultStyleType = listStyle.getDefaultStyleType();
        //listStyle.applyBaseStyle(BuiltinStyle.Title);
        ListLevelCollection levels = listStyle.getLevels();
        listStyle.getLevels().get(0).setPatternType(ListPatternType.Arabic);
        listStyle.getLevels().get(1).setNumberPrefix("\u0000.");
        listStyle.getLevels().get(1).setPatternType(ListPatternType.Arabic);
        listStyle.getLevels().get(2).setNumberPrefix("\u0000.\u0001.");

        listStyle.getLevels().get(2).setPatternType(ListPatternType.Arabic);
        listStyle.getLevels().get(3).setNumberPrefix("\u0000.\u0001.\u0002.");

        listStyle.getLevels().get(3).setPatternType(ListPatternType.Arabic);
        listStyle.getLevels().get(4).setNumberPrefix("\u0000.\u0001.\u0002.\u0003.");

        listStyle.getLevels().get(4).setPatternType(ListPatternType.Arabic);
        listStyle.getLevels().get(5).setNumberPrefix("\u0000.\u0001.\u0002.\u0003.\u0004.");

        listStyle.getLevels().get(5).setPatternType(ListPatternType.Arabic);
        listStyle.getLevels().get(6).setNumberPrefix("\u0000.\u0001.\u0002.\u0003.\u0004.\u0005.");

        listStyle.getLevels().get(6).setPatternType(ListPatternType.Arabic);
        listStyle.getLevels().get(7).setNumberPrefix("\u0000.\u0001.\u0002.\u0003.\u0004.\u0005.\u0006.");

        listStyle.getLevels().get(7).setPatternType(ListPatternType.Arabic);

        listStyle.getLevels().get(8).setNumberPrefix("\u0000.\u0001.\u0002.\u0003.\u0004.\u0005.\u0006.\u0007.");
        listStyle.getLevels().get(8).setPatternType(ListPatternType.Arabic);

        for (int i = 0; i < levels.getCount(); i++) { // 9
            setListLevelTextPosition(listStyle.getLevels().get(i), level0TextPosition);// ��������
            setListLevelNumPosition(listStyle.getLevels().get(i), -50);// ��������
            //setBold(listStyle.getLevels().get(i), true);// �Ƿ��
            //setItalic(listStyle.getLevels().get(i), false);// �Ƿ�б
        }
        setListLevelTextPosition(listStyle.getLevels().get(1), 54);// ��������
        setListLevelNumPosition(listStyle.getLevels().get(1), 5 - 32);// ��������
        setListLevelTextPosition(listStyle.getLevels().get(4), 54);// ��������
        setListLevelNumPosition(listStyle.getLevels().get(4), 30 - 32);// ��������
        document.getListStyles().add(listStyle);
        return listStyle;
    }

    // ��������
    private void setListLevelTextPosition(ListLevel level, float level0TextPosition) {
        if (level != null) {
            level.setTextPosition(level0TextPosition);
        }
    }

    // ��������
    private void setListLevelNumPosition(ListLevel level, float level0NumPosition) {
        if (level != null) {
            level.setNumberPosition(level0NumPosition);
        }
    }

    // �Ƿ��
    private void setBold(ListLevel level, boolean bold) {
        if (level != null && level.getCharacterFormat() != null) {
            level.getCharacterFormat().setBold(bold);
        }
    }

    // �Ƿ�б
    private void setItalic(ListLevel level, boolean italic) {
        if (level != null && level.getCharacterFormat() != null) {
            level.getCharacterFormat().setItalic(italic);
        }
    }


    /**
     * ��ӱ��
     *
     * @param section
     * @param header
     * @param data
     */
    private void addTable(Section section, String[] header, String[][] data) {
        // ��ӱ��
        Table table = section.addTable(true);
        // ���ñ�������������
        table.resetCells(data.length + 1, header.length);
        //table.autoFit(AutoFitBehaviorType.Auto_Fit_To_Window);
        /* ���������п�ʹ������������С��������ƽ������ */
        setTableColumnWidth(table, header.length);
        //CaptionNumberingFormat format = CaptionNumberingFormat.Number;
        //IParagraph addCaption = table.addCaption("������", format, CaptionPosition.Below_Item);
        //addCaption.applyStyle("paraStyle");
        //addCaption.getListFormat().applyStyle("paraStyle");
        // ���õ�һ����Ϊ���ı�ͷ���������
        TableRow row = table.getRows().get(0);
        row.isHeader(true);
        row.setHeight(20);
        row.setHeightType(TableRowHeightType.Exactly);
        row.getRowFormat().setBackColor(Color.LIGHT_GRAY);
        for (int i = 0; i < header.length; i++) {
            row.getCells().get(i).getCellFormat().setVerticalAlignment(VerticalAlignment.Middle);
            Paragraph p = row.getCells().get(i).addParagraph();
            p.getFormat().setHorizontalAlignment(HorizontalAlignment.Center);
            TextRange range1 = p.appendText(header[i]);
            range1.getCharacterFormat().setFontName("Arial");
            range1.getCharacterFormat().setFontSize(12f);
            range1.getCharacterFormat().setBold(true);
        }

        // ������ݵ�ʣ����
        for (int r = 0; r < data.length; r++) {
            TableRow dataRow = table.getRows().get(r + 1);
            //dataRow.setHeightType();
            //dataRow.setHeight(25);
            dataRow.setHeightType(TableRowHeightType.Auto);
            dataRow.getRowFormat().setBackColor(Color.white);
            for (int c = 0; c < data[r].length; c++) {
                dataRow.getCells().get(c).getCellFormat().setVerticalAlignment(VerticalAlignment.Middle);
                TextRange range2 = dataRow.getCells().get(c).addParagraph().appendText(data[r][c]);
                range2.getCharacterFormat().setFontName("Arial");
                range2.getCharacterFormat().setFontSize(10f);
            }
        }
        /* ��ӿ��� */
        Paragraph blankPara = section.addParagraph();
        blankPara.appendText("\n");
    }

    /**
     * ���������п�ʹ������������С��������ƽ������
     *
     * @param table
     * @param columnCount
     */
    private void setTableColumnWidth(Table table, int columnCount) {
        float sumPercent = 100f;
        /* ����п�Ƚ�С */
        float indexColumn = 10f;
        /* �����п�ƽ������ */
        float contentPercent = (sumPercent - indexColumn) / (columnCount - 1);
        sumPercent -= indexColumn;
        /* �����ռ�ٷ�֮10��������ƽ���� */
        table.getRows().get(0).getCells().get(0).setCellWidth(indexColumn, CellWidthType.Percentage);
        for (int i = 1; i < columnCount - 1; i++) {
            table.getRows().get(0).getCells().get(i).setCellWidth(contentPercent, CellWidthType.Percentage);
            sumPercent -= contentPercent;
        }
        table.getRows().get(0).getCells().get(columnCount - 1).setCellWidth(sumPercent, CellWidthType.Percentage);
    }

    /**
     * ����Ҷ�ӽ��ͷ�Ҷ�ӽ��
     *
     * @param leafNodeList
     * @param notLeafNodeList
     * @param wordElementList
     */
    private void distinguishNode(List<WordElement> leafNodeList, List<WordElement> notLeafNodeList, List<WordElement> wordElementList) {
        for (WordElement wordElement : wordElementList) {
            if (wordElement.isSelect()) {
                if (wordElement.getWordElementList() == null || wordElement.getWordElementList().isEmpty()) {
                    leafNodeList.add(wordElement);
                } else {
                    notLeafNodeList.add(wordElement);
                }
            }
        }

    }

    private BuiltinStyle getParaStyle(int wide) {
        switch (wide) {
            case 1:
                return BuiltinStyle.Heading_1;
            case 2:
                return BuiltinStyle.Heading_2;
            case 3:
                return BuiltinStyle.Heading_3;
            case 4:
                return BuiltinStyle.Heading_4;
            case 5:
                return BuiltinStyle.Heading_5;
            case 6:
                return BuiltinStyle.Heading_6;
            case 7:
                return BuiltinStyle.Heading_7;
            case 8:
                return BuiltinStyle.Heading_8;
            default:
                return BuiltinStyle.Heading_9;
        }
    }

    /**
     * ����ͼ����Ϣ
     *
     * @param contentDiagramInfo
     * @param section
     * @param filePathName
     */
    private void geneContentInfo(DiagramInfo contentDiagramInfo, Section section, String filePathName) {
        int diagramType = contentDiagramInfo.getDiagramType();
        String contInfoStr = "";
        String diagramName = contentDiagramInfo.getDiagramName();
        if (diagramType == DiagramType.ACTIVITY) {
            contInfoStr = "��ͼ��ϸ������ִ��" + diagramName + "�Ļ���̡�";
        } else if (diagramType == DiagramType.USECASE) {
            contInfoStr = "����ͼ���ڱ��ϵͳִ�е��������Լ�������������Ϊ�ߺ����еĲ����ߡ�";
        } else if (diagramType == DiagramType.STATE_MACHINE) {
            contInfoStr = "��ͼ����" + diagramName + "ģ���һϵ��״̬���Լ���Ӧ�¼�ʱ��״̬֮��Ŀ���ת����";
        } else if (diagramType == DiagramType.PACKAGE_DIAGRAM) {
            contInfoStr = "��ͼ��ϸ����" + diagramName + "����ɲ����Լ���ɲ���֮ǰ�Ĺ�����ϵ��";
        } else if (diagramType == DiagramType.IBD) {
            contInfoStr = "��ͼ��ϸ��ʾ��" + diagramName + "ģ����ڲ��ṹ������ɲ���֮������ӹ�ϵ��";
        } else if (diagramType == DiagramType.BDD) {
            if (contentDiagramInfo.getBddDiagram() != null) {
                BDDDiagram bddDiagram = contentDiagramInfo.getBddDiagram();
                contInfoStr = bddDiagram.getRootName() + "ϵͳ��" + bddDiagram.getChildList().size() + "��ģ����ɣ��ֱ�Ϊ:";
            }

        } else if (diagramType == DiagramType.REQUIREMENT) {
            List<ReqDiagram> reqDiagram = contentDiagramInfo.getReqDiagram();
            if (reqDiagram != null && reqDiagram.size() > 0) {
                contInfoStr = contentDiagramInfo.getDiagramName() + "ϵͳ����" + reqDiagram.size() + "������������ϸ��������Ϣ���±���ʾ��";
            }
        } else if (diagramType == DiagramType.SEQUENCE) {
            contInfoStr = "��ͼ������ʱ��ͼ" + diagramName + "�Ĳ������ù�ϵ��";
        } else if (diagramType == DiagramType.PARAMETRIC) {
            List<ParaDiagram> paraDiagramList = contentDiagramInfo.getParaDiagramList();
            if (paraDiagramList != null && paraDiagramList.size() > 0) {
                contInfoStr = "���²���ͼ������" + diagramName + "��" + paraDiagramList.size() + "��Լ����ϵ���ֱ�Ϊ:";
            }

        }
        if (!StringUtils.isBlank(contInfoStr)) {
            Paragraph commentPara = section.addParagraph();
            commentPara.appendText(contInfoStr);

            commentPara.getFormat().setFirstLineIndent(25f);
            commentPara.getFormat().setAfterSpacing(10f);
            if (diagramType == DiagramType.BDD) {
                if (contentDiagramInfo.getBddDiagram() != null) {
                    BDDDiagram bddDiagram = contentDiagramInfo.getBddDiagram();
                    List<String> childList = bddDiagram.getChildList();
                    for (String bddCompose : childList) {
                        Paragraph bddComposeParagraph = section.addParagraph();
                        bddComposeParagraph.appendText(bddCompose);
                        bddComposeParagraph.getListFormat().applyBulletStyle();
                        bddComposeParagraph.getListFormat().getCurrentListLevel().setNumberPosition(-10);
                    }
                }
            } else if (diagramType == DiagramType.STATE_MACHINE) {
                StateFlow stateFlow = contentDiagramInfo.getStateFlow();
                addStateInfo(stateFlow, section);
            } else if (diagramType == DiagramType.REQUIREMENT) {
                List<ReqDiagram> reqDiagram = contentDiagramInfo.getReqDiagram();
                if (reqDiagram != null && reqDiagram.size() > 0) {
                    /* ��ӱ�� */
                    String[] header = {"���", "��������", "��������"};
                    String[][] data = new String[reqDiagram.size()][header.length];
                    int index = 1;
                    for (int i = 0; i < reqDiagram.size(); i++) {
                        data[i][0] = String.valueOf(index++);
                        data[i][1] = reqDiagram.get(i).getName();
                        data[i][2] = reqDiagram.get(i).getType();
                    }
                    /* ��ӱ�� */
                    addTable(section, header, data);
                }
            } else if (diagramType == DiagramType.PARAMETRIC) {
                List<ParaDiagram> paraDiagramList = contentDiagramInfo.getParaDiagramList();
                if (paraDiagramList != null && paraDiagramList.size() > 0) {
                    for (ParaDiagram paraDiagram : paraDiagramList) {
                        Paragraph bddComposeParagraph = section.addParagraph();
                        bddComposeParagraph.appendText(paraDiagram.getName());
                        bddComposeParagraph.appendText("\t{");
                        bddComposeParagraph.appendText(paraDiagram.getCondition());
                        bddComposeParagraph.appendText("}");
                        bddComposeParagraph.getListFormat().applyBulletStyle();
                        bddComposeParagraph.getListFormat().getCurrentListLevel().setNumberPosition(-10);
                    }
                }
            } else if (diagramType == DiagramType.ACTIVITY) {
                StateFlow stateFlow = contentDiagramInfo.getStateFlow();
                List<Lane> activityLaneList = contentDiagramInfo.getActivityLaneList();
                addActivityInfo(stateFlow, section);
                addActivityLane(activityLaneList, section);
            }
            Object diagramEntity = contentDiagramInfo.getDiagramEntity();
            if (diagramEntity != null) {
                if (diagramEntity instanceof Activity) {
                    Activity activity = (Activity) diagramEntity;
                    Collection<Diagram> ownedDiagramList = activity.getOwnedDiagram();
                    if (ownedDiagramList != null && ownedDiagramList.size() > 0) {
                        for (Diagram activityDiagram : ownedDiagramList) {
                            geneImage(activityDiagram, filePathName, section);
                        }
                    }
                } else if (diagramEntity instanceof Diagram) {
                    geneImage((Diagram) diagramEntity, filePathName, section);
                }

            }
        }

    }

    /**
     * ���Ӿ����Ϣ
     *
     * @param activityLaneList
     * @param section
     */
    private void addActivityLane(List<Lane> activityLaneList, Section section) {
        if (activityLaneList == null || activityLaneList.size() == 0) {
            return;
        }
        Paragraph paragraph = section.addParagraph();
        paragraph.appendText("\t��ͼ��" + activityLaneList.size() + "��Ӿ�����,��ϸ��ɹ�ϵ����:");
        for (Lane lane : activityLaneList) {
            //section.getPageSetup().setPageStartingNumber(1);
            Paragraph subParagraph = section.addParagraph();
            String laneName = lane.getLaneName();
            subParagraph.appendText("\t" + laneName);
            subParagraph.getListFormat().applyBulletStyle();
            List<String> nodeList = lane.getNodeList();
            for (String nodeName : nodeList) {
                if (StringUtils.isNotBlank(nodeName)) {
                    Paragraph nodeParagraph = section.addParagraph();
                    nodeParagraph.appendText("\t\t\t" + nodeName);
//                    nodeParagraph.getListFormat().getCurrentListLevel().setNumberPosition(-30);
                }
            }
        }
    }

    /**
     * ��ӻͼ�Ķ���
     *
     * @param stateFlow
     * @param section
     */
    private void addActivityInfo(StateFlow stateFlow, Section section) {
        if (stateFlow == null) {
            return;
        }
        StringBuffer sb = new StringBuffer();
        sb.append("\tstart\n");
        String stateItemListInfo = addStateItemInfo(stateFlow, null);
        sb.append(stateItemListInfo);
        sb.append("\tend\n");
        Paragraph paragraph = section.addParagraph();
        paragraph.appendText(sb.toString());

    }

    /**
     * ���State���
     *
     * @param stateFlow
     * @param wideBranch
     * @return
     */
    private String addStateItemInfo(StateFlow stateFlow, Branch wideBranch) {
        String blankContent = getBlankContent(wideBranch);
        StringBuffer sb = new StringBuffer();
        List<StateItem> stateItemList = stateFlow.getStateItemList();
        for (StateItem stateItem : stateItemList) {
            sb.append(blankContent + stateItem.getName());
            appendStateType(stateItem, sb);
            if (stateItem.getBranchList() != null && stateItem.getBranchList().size() > 0) {
                sb.append(addActiveBranchExpr(stateItem));
            }
        }
        return sb.toString();
    }

    /**
     * ���ɻͼ�ķ�֧��Ϣ
     *
     * @param stateItem
     * @return
     */
    private String addActiveBranchExpr(StateItem stateItem) {
        StringBuffer sb = new StringBuffer();
        List<Branch> branchList = stateItem.getBranchList();
        if (branchList != null && branchList.size() >= 1) {
            for (int i = 0; i < branchList.size(); i++) {
                Branch branch = branchList.get(i);
                String blankContent = getBlankContent(branch);
                String guardStr = branch.getGuard();
                StateFlow subStateFlow = branch.getStateFlow();
                if (subStateFlow != null) {
                    List<StateItem> subStateItemList = subStateFlow.getStateItemList();
                    /* if��֧ */
                    if (i == 0) {
                        sb.append(blankContent + "if(" + guardStr + "){\n");
                    }
                    /* else-if ��֧ */
                    else {
                        sb.append(blankContent + "else if(" + guardStr + "){\n");
                    }
                    for (StateItem subStateItem : subStateItemList) {
                        sb.append(blankContent + "\t" + subStateItem.getName());
                        appendStateType(subStateItem, sb);
                        if (subStateItem.getBranchList() != null && subStateItem.getBranchList().size() > 0) {
                            sb.append(addActiveBranchExpr(subStateItem));
                        }
                    }
                    sb.append(blankContent + "}\n");
                }

            }
        }
        return sb.toString();
    }

    /**
     * ��ÿհ׵ĳ���
     *
     * @param branch
     * @return
     */
    private String getBlankContent(Branch branch) {
        StringBuffer sb = new StringBuffer();
        sb.append("\t");
        if (branch == null) {
            return sb.toString();
        } else {
            for (int i = 0; i < branch.getWide() - 1; i++) {
                sb.append("\t");
            }
        }
        return sb.toString();
    }

    /**
     * ���״̬���Ķ���
     *
     * @param stateFlow
     * @param section
     */
    private void addStateInfo(StateFlow stateFlow, Section section) {
        if (stateFlow == null) {
            return;
        }
        StringBuffer sb = new StringBuffer();
        sb.append("\tstart\n");
        List<StateItem> stateItemList = stateFlow.getStateItemList();
        List<String> existList = new ArrayList<>();
        for (StateItem stateItem : stateItemList) {
            if (stateItem.getBranchList() == null || stateItem.getBranchList().size() == 0) {
                if (!existList.contains(stateItem.getName())) {
                    existList.add(stateItem.getName());
                    sb.append("\tִ�� " + stateItem.getName());
                    appendStateType(stateItem, sb);
                }
            } else {
                if (!existList.contains(stateItem.getName())) {
                    existList.add(stateItem.getName());
                    sb.append("\tִ�� " + stateItem.getName());
                    appendStateType(stateItem, sb);
                }
                List<Branch> branchList = stateItem.getBranchList();
                if (branchList != null && branchList.size() >= 1) {
                    for (int i = 0; i < branchList.size(); i++) {
                        Branch branch = branchList.get(i);
                        String guardStr = branch.getGuard();
                        String targetName = branch.getTargetName();
                        /* if��֧ */
                        if (i == 0) {
                            if (existList.contains(targetName)) {
                                addBranchExpr("if", guardStr, targetName, "��ת��", sb);
                            } else {
                                existList.add(targetName);
                                addBranchExpr("if", guardStr, targetName, "ִ��", sb);
                            }
                        }
                        /* else-if ��֧ */
                        else if (i != branchList.size() - 1) {
                            if (existList.contains(targetName)) {
                                addBranchExpr("else if", guardStr, targetName, "��ת��", sb);
                            } else {
                                existList.add(targetName);
                                addBranchExpr("else if", guardStr, targetName, "ִ��", sb);
                            }
                        }
                        /* else ��֧ */
                        else {
                            if (existList.contains(targetName)) {
                                addBranchExpr("else", guardStr, targetName, "��ת��", sb);
                            } else {
                                existList.add(targetName);
                                addBranchExpr("else", guardStr, targetName, "ִ��", sb);
                            }
                        }
                    }
                }
            }
        }
        sb.append("\tend\n");
        Paragraph paragraph = section.addParagraph();
        paragraph.appendText(sb.toString());

    }

    private void appendStateType(StateItem stateItem, StringBuffer sb) {
        sb.append(" ");
        if (StringUtils.isNotBlank(stateItem.getType())) {
            sb.append(stateItem.getType());
        }
        if (stateItem.isEdit()) {
            sb.append("(E)");
            sb.append("(" + stateItem.getTypeName() + ")");
        }
        sb.append("\n");
    }

    /**
     * ��ӷ�֧���
     *
     * @param condition
     * @param guardStr
     * @param targetName
     * @param linkWord
     * @param sb
     */
    private void addBranchExpr(String condition, String guardStr, String targetName, String linkWord, StringBuffer sb) {
        sb.append("\t" + condition + "(" + guardStr + "){\n");
        sb.append("\t\t" + linkWord + " " + targetName);
        sb.append(" ");
        sb.append("(S)");
        sb.append("\n");
        sb.append("\t}\n");
    }

    /**
     * ����ͼƬ
     *
     * @param diagramEntity
     * @param filePathName
     * @param section
     */
    private void geneImage(Diagram diagramEntity, String filePathName, Section section) {
        File wordFile = new File(filePathName);
        File imageDir = new File(wordFile.getParent() + '/' + "image");
        if (!imageDir.exists()) {
            imageDir.mkdirs();
        }
        final File diagramFile = new File(imageDir.getAbsolutePath(), diagramEntity.getHumanName() + diagramEntity.getID() + ".png");
        try {
            DiagramPresentationElement diagramPresentationElement = Application.getInstance().getProject().getDiagram(diagramEntity);
            ImageExporter.export(diagramPresentationElement, ImageExporter.PNG, diagramFile);
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        Paragraph paragraph2 = section.addParagraph();
        // ���ͼƬ������
        DocPicture picture = paragraph2.appendPicture(diagramFile.getAbsolutePath());
        // ����ͼƬ���
        picture.setWidth(300f);
        // ����ͼƬ�߶�
        picture.setHeight(250f);
        picture.setVerticalAlignment(ShapeVerticalAlignment.Center);
        picture.setHorizontalAlignment(ShapeHorizontalAlignment.Center);
        // ����ͼƬ�������
        paragraph2.getFormat().setHorizontalAlignment(HorizontalAlignment.Center);

    }
}
