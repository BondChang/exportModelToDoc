package exportmodeltodoc.service;

import com.aspose.words.License;
import com.aspose.words.ReportingEngine;
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
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ExportToDocService {

    public void exportDoc(String templateFilePath, String filePathName, WordTop wordTop) throws Exception {

        // loadLicense();
        Document doc = new Document(templateFilePath);
        System.out.println(doc.toString());

        buildReport(doc, wordTop, "wordTop", new Class[]{WordTop.class, ChapterElement.class, ContentElement.class,
                ContentElementTable.class, ContentElementRow.class});
//		doc.updateFields();
//		doc.save(filePathName);
    }

    private void buildReport(final Document document, final Object dataSource, final String dataSourceName,
                             final Class[] knownTypes) throws Exception {
        ReportingEngine engine = new ReportingEngine();

        for (Class knownType : knownTypes) {
            engine.getKnownTypes().add(knownType);
        }

        // engine.buildReport(document, dataSource, dataSourceName);
    }

    //
    private void loadLicense() {
        // 杩斿洖璇诲彇鎸囧畾璧勬簮鐨勮緭鍏ユ祦
        License license = new License();
        // InputStream is = null;
        ByteArrayInputStream inputStream = null;
        try {

            // is = new FileInputStream(new File("C:\\z-temp\\expDir\\license.xml"));
            String s = "<License>" + "  <Data>" + "    <Products>" + "      <Product>Aspose.Total for Java</Product>"
                    + "    </Products>" + "    <EditionType>Enterprise</EditionType>"
                    + "    <SubscriptionExpiry>29991231</SubscriptionExpiry>"
                    + "    <LicenseExpiry>29991231</LicenseExpiry>"
                    + "    <SerialNumber>8bfe198c-7f0c-4ef8-8ff0-acc3237bf0d7</SerialNumber>" + "  </Data>"
                    + "  <Signature>sNLLKGMUdF0r8O1kKilWAGdgfs2BvJb/2Xp8p5iuDVfZXmhppo+d0Ran1P9TKdjV4ABwAgKXxJ3jcQTqE/2IRfqwnPf8itN8aFZlV3TJPYeD3yWE7IT55Gz6EijUpC7aKeoohTb4w2fpox58wWoF3SNp6sK6jDfiAUGEHYJ9pjU=</Signature>"
                    + "</License>";
            inputStream = new ByteArrayInputStream(s.getBytes());

            license.setLicense(inputStream);
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException ex) {
                }

                inputStream = null;
            }
        }
    }

    /**
     * 导出word文档的入口
     *
     * @param rootElement
     * @param filePathName
     */
    public void exportWord(WordElement rootElement, String filePathName) {
        Document document = new Document();

        /* 每个Section表示一个节，此程序中分成3分节。（题目、目录、正文） */
        Section titleSection = document.addSection();
        /* 添加段落的Style，相同id不能重复添加 */
        addParaStyle(document);

        ListStyle listStyle = getListStyle(document);
        Section catalogue = document.addSection();
        /* 添加正文的Session */
        Section content = document.addSection();
        /* 添加目录 */
        catalogueInsert(document, catalogue);
        /* 添加各级标题 */
        addHeadTitle(rootElement, content, listStyle, filePathName);

        // 更新目录表
        document.updateTableOfContents();

        /* 添加首页标题 */
        addDocTitle(titleSection, rootElement);

        /* 在页脚添加页码 */
        addFootPageNum(document);
        document.saveToFile(filePathName, FileFormat.Docx);

    }

    /**
     * 添加段落style
     *
     * @param document
     */
    private void addParaStyle(Document document) {
        ParagraphStyle style = new ParagraphStyle(document);
        style.setName("paraStyle");
        style.getCharacterFormat().setFontName("宋体");
        style.getCharacterFormat().setFontSize(11f);
        document.getStyles().add(style);

    }

    /**
     * 添加文档的标题
     *
     * @param headSection
     * @param rootElement
     */
    private void addDocTitle(Section headSection, WordElement rootElement) {
        Paragraph para = headSection.addParagraph();
        para.appendText(rootElement.getElementName());
        // 设置第一个段落的对齐方式
        para.getFormat().setHorizontalAlignment(HorizontalAlignment.Center);
        para.applyStyle(BuiltinStyle.Title);
        // para1.applyStyle("titleStyle");
        para.getListFormat().setListLevelNumber(rootElement.getWide());

    }

    /**
     * 添加文档的目录结构
     *
     * @param doc
     * @param section
     */
    private void catalogueInsert(Document doc, Section section) {
        Paragraph para = section.addParagraph();
        TextRange tr = para.appendText("目 录");
        tr.getCharacterFormat().setBold(true);
        tr.getCharacterFormat().setTextColor(Color.gray);
        doc.getSections().get(1).getParagraphs().insert(0, para);
        para.getFormat().setHorizontalAlignment(HorizontalAlignment.Center);
        // 设置段后间距
        para.getFormat().setAfterSpacing(10);

        TableOfContent toc = new TableOfContent(doc, "{\\o \"1-9\" \\h \\z \\u}");
        para = section.addParagraph();
        para.getItems().add(toc);
        para.appendFieldMark(FieldMarkType.Field_Separator);
        para.appendText("TOC");
        para.appendFieldMark(FieldMarkType.Field_End);

        doc.setTOC(toc);

    }

    /**
     * 在页脚添加页码
     *
     * @param document
     */
    private void addFootPageNum(Document document) {
        // 获取第一个节中的页脚
        HeaderFooter footer = document.getSections().get(2).getHeadersFooters().getFooter();

        // 添加段落到页脚
        Paragraph footerParagraph = footer.addParagraph();

        // 添加文字、页码域和总页数域到段落
        footerParagraph.appendText("第");
        footerParagraph.appendField("page number", FieldType.Field_Page);
        footerParagraph.appendText("页 共");
        footerParagraph.appendField("number of pages", FieldType.Field_Num_Pages);
        footerParagraph.appendText("页");

        // 将段落居中
        footerParagraph.getFormat().setHorizontalAlignment(HorizontalAlignment.Center);

    }

    /**
     * 遍历part，添加标题和表格
     *
     * @param rootElement
     * @param section
     * @param filePathName
     */
    private void addHeadTitle(WordElement rootElement, Section section, ListStyle listStyle, String filePathName) {
        List<WordElement> wordElementList = rootElement.getWordElementList();
        geneDiagramImage(rootElement, section, filePathName);

        if (wordElementList != null && wordElementList.size() > 0) {
            List<WordElement> leafNodeList = new ArrayList<WordElement>();
            List<WordElement> notLeafNodeList = new ArrayList<WordElement>();
            distinguishNode(leafNodeList, notLeafNodeList, wordElementList);
            /* 叶子节点生成表格 */
            // if (!leafNodeList.isEmpty() && leafNodeList.get(0).getWide() > 1) {
            if (!leafNodeList.isEmpty()) {
                for (WordElement wordElement : leafNodeList) {
                    geneDiagramImage(wordElement, section, filePathName);
                }
                if (ExportWordUI.exportType == ExportType.STRUCTEXPORT) {
                    String[] header = {"序号", "名称", "值", "实做值"};
                    String[][] data = new String[leafNodeList.size()][header.length];
                    int index = 1;
                    for (int i = 0; i < leafNodeList.size(); i++) {
                        data[i][0] = String.valueOf(index++);
                        data[i][1] = leafNodeList.get(i).getElementName();
                        data[i][2] = leafNodeList.get(i).getElementValue();
                        data[i][3] = "";
                    }
                    /* 添加表格 */
                    addTable(section, header, data);
                }
            }
            if (!notLeafNodeList.isEmpty()) {
                /* 非叶子结点生成标题 */
                for (WordElement wordElement : notLeafNodeList) {
                    Paragraph para = section.addParagraph();
                    para.appendText(wordElement.getElementName());
                    para.applyStyle(getParaStyle(wordElement.getWide()));
                    para.getListFormat().setListLevelNumber(wordElement.getWide() - 1);
                    para.getListFormat().applyStyle(listStyle.getName());
                    geneDiagramImage(wordElement, section, filePathName);
                    /* 添加注释信息 */
                    String elementName = wordElement.getElementName();

                    String commentStr = ExportDocAction.commentMap.getOrDefault(elementName, null);
                    if (commentStr != null) {
                        Paragraph commentPara = section.addParagraph();
                        commentPara.appendText(commentStr);

                        commentPara.getFormat().setFirstLineIndent(25f);
                        commentPara.getFormat().setAfterSpacing(10f);
                    }
                    addHeadTitle(wordElement, section, listStyle, filePathName);
                }
            }

        }
    }

    private void geneDiagramImage(WordElement rootElement, Section section, String filePathName) {
        if (rootElement.getDiagramInfoList() != null && rootElement.getDiagramInfoList().size() > 0) {
            for (DiagramInfo diagramInfo : rootElement.getDiagramInfoList()) {
                geneContentInfo(diagramInfo, section, filePathName);
            }
        }

    }

    public ListStyle getListStyle(Document document) {
        ListStyle listStyle = new ListStyle(document, ListType.Numbered);
        listStyle.setName("CustomStyle");

        // Set the list pattern type and number prefix of each level
        BuiltinStyle defaultStyleType = listStyle.getDefaultStyleType();
        listStyle.applyBaseStyle(BuiltinStyle.Balloon_Text);
        ListLevelCollection levels = listStyle.getLevels();
//        System.out.println(levels.getCount());
//        System.out.println(defaultStyleType);
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
        document.getListStyles().add(listStyle);

        return listStyle;
    }

    /**
     * 添加表格
     *
     * @param section
     * @param header
     * @param data
     */
    private void addTable(Section section, String[] header, String[][] data) {
        // 添加表格
        Table table = section.addTable(true);
        // 设置表格的行数和列数
        table.resetCells(data.length + 1, header.length);

        // 设置第一行作为表格的表头并添加数据
        TableRow row = table.getRows().get(0);
        row.isHeader(true);
        row.setHeight(20);
        row.setHeightType(TableRowHeightType.Exactly);
        row.getRowFormat().setBackColor(Color.gray);
        for (int i = 0; i < header.length; i++) {
            row.getCells().get(i).getCellFormat().setVerticalAlignment(VerticalAlignment.Middle);
            Paragraph p = row.getCells().get(i).addParagraph();
            p.getFormat().setHorizontalAlignment(HorizontalAlignment.Center);
            TextRange range1 = p.appendText(header[i]);
            range1.getCharacterFormat().setFontName("Arial");
            range1.getCharacterFormat().setFontSize(12f);
            range1.getCharacterFormat().setBold(true);
        }

        // 添加数据到剩余行
        for (int r = 0; r < data.length; r++) {
            TableRow dataRow = table.getRows().get(r + 1);
            dataRow.setHeight(25);
            dataRow.setHeightType(TableRowHeightType.Exactly);
            dataRow.getRowFormat().setBackColor(Color.white);
            for (int c = 0; c < data[r].length; c++) {
                dataRow.getCells().get(c).getCellFormat().setVerticalAlignment(VerticalAlignment.Middle);
                TextRange range2 = dataRow.getCells().get(c).addParagraph().appendText(data[r][c]);
                range2.getCharacterFormat().setFontName("Arial");
                range2.getCharacterFormat().setFontSize(10f);
            }
        }

    }

    /**
     * 区分叶子结点和非叶子结点
     *
     * @param leafNodeList
     * @param notLeafNodeList
     * @param wordElementList
     */
    private void distinguishNode(List<WordElement> leafNodeList, List<WordElement> notLeafNodeList,
                                 List<WordElement> wordElementList) {
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
     * 生成图的信息
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
            contInfoStr = "下图详细描述了执行" + diagramName + "的活动过程。";
        } else if (diagramType == DiagramType.USECASE) {
            contInfoStr = "用例图用于表达系统执行的用例，以及引起用例的行为者和其中的参与者。";
        } else if (diagramType == DiagramType.STATE_MACHINE) {
            contInfoStr = "下图描述" + diagramName + "模块的一系列状态，以及响应事件时，状态之间的可能转换。";
        } else if (diagramType == DiagramType.PACKAGE_DIAGRAM) {
            contInfoStr = "下图详细描述" + diagramName + "的组成部分以及组成部分之前的关联关系。";
        } else if (diagramType == DiagramType.IBD) {
            contInfoStr = "下图详细显示了" + diagramName + "模块的内部结构及其组成部分之间的连接关系。";
        } else if (diagramType == DiagramType.BDD) {
            if (contentDiagramInfo.getBddDiagram() != null) {
                BDDDiagram bddDiagram = contentDiagramInfo.getBddDiagram();
                contInfoStr = bddDiagram.getRootName() + "系统由" + bddDiagram.getChildList().size() + "个模块组成，分别为:";
            }

        } else if (diagramType == DiagramType.REQUIREMENT) {
            List<ReqDiagram> reqDiagram = contentDiagramInfo.getReqDiagram();
            if (reqDiagram != null && reqDiagram.size() > 0) {
                contInfoStr = contentDiagramInfo.getDiagramName() + "系统包含" + reqDiagram.size() + "个功能需求，详细的需求信息如下表所示：";
            }
        } else if (diagramType == DiagramType.SEQUENCE) {
            contInfoStr = "下图描述了时序图" + diagramName + "的操作调用关系。";
        } else if (diagramType == DiagramType.PARAMETRIC) {
            List<ParaDiagram> paraDiagramList = contentDiagramInfo.getParaDiagramList();
            if (paraDiagramList != null && paraDiagramList.size() > 0) {
                contInfoStr = "以下参数图描述了" + diagramName + "的" + paraDiagramList.size() + "个约束关系，分别为:";
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
                    /* 添加表格 */
                    String[] header = {"序号", "需求名称", "需求类型"};
                    String[][] data = new String[reqDiagram.size()][header.length];
                    int index = 1;
                    for (int i = 0; i < reqDiagram.size(); i++) {
                        data[i][0] = String.valueOf(index++);
                        data[i][1] = reqDiagram.get(i).getName();
                        data[i][2] = reqDiagram.get(i).getType();
                    }
                    /* 添加表格 */
                    addTable(section, header, data);
                }
            } else if (diagramType == DiagramType.PARAMETRIC) {
                List<ParaDiagram> paraDiagramList = contentDiagramInfo.getParaDiagramList();
                if (paraDiagramList != null && paraDiagramList.size() > 0) {
                    for (ParaDiagram paraDiagram : paraDiagramList) {
                        Paragraph bddComposeParagraph = section.addParagraph();
                        bddComposeParagraph.appendText(paraDiagram.getName());
                        bddComposeParagraph.appendText("\t");
                        bddComposeParagraph.appendText(paraDiagram.getCondition());
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
     * 添加泳道信息
     *
     * @param activityLaneList
     * @param section
     */
    private void addActivityLane(List<Lane> activityLaneList, Section section) {
        if (activityLaneList == null || activityLaneList.size() == 0) {
            return;
        }
        Paragraph paragraph = section.addParagraph();
        paragraph.appendText("\t此图由" + activityLaneList.size() + "个泳道组成,详细组成关系如下:");
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
     * 添加活动图的段落
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
     * 添加State语句
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
            sb.append(blankContent + stateItem.getName() + "\n");
            if (stateItem.getBranchList() != null && stateItem.getBranchList().size() > 0) {
                sb.append(addActiveBranchExpr(stateItem));
            }
        }
        return sb.toString();
    }

    /**
     * 生成活动图的分支信息
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
                    /* if分支 */
                    if (i == 0) {
                        sb.append(blankContent + "if(" + guardStr + "){\n");
                    }
                    /* else-if 分支 */
                    else {
                        sb.append(blankContent + "else if(" + guardStr + "){\n");
                    }
                    for (StateItem subStateItem : subStateItemList) {
                        sb.append(blankContent + "\t" + subStateItem.getName() + "\n");
//                        if (subStateItem.getName().equals("生成路线图")) {
//                            System.out.println("123");
//                        }
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
     * 获得空白的长度
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
     * 添加状态机的段落
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
                    sb.append("\t执行 " + stateItem.getName() + "\n");
                }
            } else {
                if (!existList.contains(stateItem.getName())) {
                    existList.add(stateItem.getName());
                    sb.append("\t执行 " + stateItem.getName() + "\n");
                }
                List<Branch> branchList = stateItem.getBranchList();
                if (branchList != null && branchList.size() >= 1) {
                    for (int i = 0; i < branchList.size(); i++) {
                        Branch branch = branchList.get(i);
                        String guardStr = branch.getGuard();
                        String targetName = branch.getTargetName();
                        /* if分支 */
                        if (i == 0) {
                            if (existList.contains(targetName)) {
                                addBranchExpr("if", guardStr, targetName, "跳转到", sb);
                            } else {
                                existList.add(targetName);
                                addBranchExpr("if", guardStr, targetName, "执行", sb);
                            }
                        }
                        /* else-if 分支 */
                        else if (i != branchList.size() - 1) {
                            if (existList.contains(targetName)) {
                                addBranchExpr("else if", guardStr, targetName, "跳转到", sb);
                            } else {
                                existList.add(targetName);
                                addBranchExpr("else if", guardStr, targetName, "执行", sb);
                            }
                        }
                        /* else 分支 */
                        else {
                            if (existList.contains(targetName)) {
                                addBranchExpr("else", guardStr, targetName, "跳转到", sb);
                            } else {
                                existList.add(targetName);
                                addBranchExpr("else", guardStr, targetName, "执行", sb);
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

    /**
     * 添加分支语句
     *
     * @param condition
     * @param guardStr
     * @param targetName
     * @param linkWord
     * @param sb
     */
    private void addBranchExpr(String condition, String guardStr, String targetName, String linkWord, StringBuffer sb) {
        sb.append("\t" + condition + "(" + guardStr + "){\n");
        sb.append("\t\t" + linkWord + " " + targetName + "\n");
        sb.append("\t}\n");
    }

    /**
     * 插入图片
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
        final File diagramFile = new File(imageDir.getAbsolutePath(),
                diagramEntity.getHumanName() + diagramEntity.getID() + ".png");
        try {
            DiagramPresentationElement diagramPresentationElement = Application.getInstance().getProject()
                    .getDiagram(diagramEntity);
            ImageExporter.export(diagramPresentationElement, ImageExporter.PNG, diagramFile);
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        Paragraph paragraph2 = section.addParagraph();
        // 添加图片到段落
        DocPicture picture = paragraph2.appendPicture(diagramFile.getAbsolutePath());
        // 设置图片宽度
        picture.setWidth(300f);
        // 设置图片高度
        picture.setHeight(250f);
        picture.setVerticalAlignment(ShapeVerticalAlignment.Center);
        picture.setHorizontalAlignment(ShapeHorizontalAlignment.Center);
        // 设置图片段落居中
        paragraph2.getFormat().setHorizontalAlignment(HorizontalAlignment.Center);

    }
}
