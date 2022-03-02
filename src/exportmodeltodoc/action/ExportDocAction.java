package exportmodeltodoc.action;

import checkbox.tree.ui.SelectTree;
import com.nomagic.magicdraw.core.Application;
import com.nomagic.magicdraw.core.Project;
import com.nomagic.magicdraw.openapi.uml.SessionManager;
import com.nomagic.magicdraw.ui.browser.Tree;
import com.nomagic.magicdraw.ui.browser.actions.DefaultBrowserAction;
import com.nomagic.uml2.ext.magicdraw.activities.mdfundamentalactivities.Activity;
import com.nomagic.uml2.ext.magicdraw.auxiliaryconstructs.mdmodels.Model;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Class;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Package;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.*;
import com.nomagic.uml2.ext.magicdraw.compositestructures.mdcollaborations.Collaboration;
import com.nomagic.uml2.ext.magicdraw.compositestructures.mdports.Port;
import com.nomagic.uml2.ext.magicdraw.mdusecases.UseCase;
import exportmodeltodoc.entity.ElementType;
import exportmodeltodoc.entity.WordElement;
import org.apache.commons.lang.StringUtils;

import java.awt.event.ActionEvent;
import java.util.*;

import static exportmodeltodoc.action.ExportDocUtil.judgeIsInsidePackage;
import static exportmodeltodoc.action.ExportDocUtil.wipeNum;

public class ExportDocAction extends DefaultBrowserAction {

    public ExportDocAction() {
        super("", "导出word文档", null, null);
    }

    public static List<java.util.Map<String, String>> instanceMapList = new ArrayList<>();
    public static Map<String, String> commentMap = new HashMap<>();
    List<String> parserElementList = new ArrayList<>();

    public void actionPerformed(ActionEvent e) {

        Project project = Application.getInstance().getProject();
        instanceMapList = new ArrayList<>();
        parserElementList.clear();
        commentMap.clear();
        Tree tree = getTree();

        com.nomagic.magicdraw.ui.browser.Node nodeSelect = tree.getSelectedNode();
        Object userObject = nodeSelect.getUserObject();
        SessionManager.getInstance().createSession(project, "Export");

        /* 在根节点上右键，即在model上右键 */
        if (userObject instanceof com.nomagic.uml2.ext.magicdraw.auxiliaryconstructs.mdmodels.Model) {
            com.nomagic.uml2.ext.magicdraw.auxiliaryconstructs.mdmodels.Model model = (Model) userObject;
            WordElement rootElement = createRootElement(model);
            /* 获取模型下所有的子节点 */
            Collection<NamedElement> ownedMemberList = model.getOwnedMember();
            /* 添加子节点的孩子信息 */
            addRootChildInfo(rootElement, ownedMemberList);
            SelectTree selectTree = new SelectTree(rootElement);
        }
        /* 在包上进行右键 */
        else if (userObject instanceof Package) {
            Package packageContent = (Package) userObject;
            WordElement rootElement = createRootElement(packageContent);
            addNodeInfo(packageContent, rootElement);
            SelectTree selectTree = new SelectTree(rootElement);
        }
        /* 在block上进行右键 */
        else if (userObject instanceof Class) {
            Class ownClass = (Class) userObject;
            WordElement rootElement = createRootElement(ownClass);
            addNodeInfo(ownClass, rootElement);
            SelectTree selectTree = new SelectTree(rootElement);
        }
        /* 在图上右键 */
        else if (userObject instanceof Diagram) {
            com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Diagram diagram = (com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Diagram) userObject;
            WordElement rootElement = createRootElement(diagram);
            addNodeInfo(diagram, rootElement);
            SelectTree selectTree = new SelectTree(rootElement);
        }
        /* 可以理解成在元素上右键 */
        else if (userObject instanceof NamedElement) {
            WordElement rootElement = createRootElement((NamedElement) userObject);
            if (((NamedElement) userObject).getHumanType().equals("Part Property")) {
                Type ownType = ((Property) userObject).getType();
                if (ownType instanceof Class) {
                    Class ownClass = (Class) ownType;
                    addNodeInfo(ownClass, rootElement);
                }
            }
            SelectTree selectTree = new SelectTree(rootElement);
        }
        SessionManager.getInstance().closeSession(project);

    }

    /**
     * 添加子节点的孩子信息
     *
     * @param rootElement
     * @param ownedMemberList
     */
    private void addRootChildInfo(WordElement rootElement, Collection<NamedElement> ownedMemberList) {
        if (ownedMemberList != null && ownedMemberList.size() > 0) {
            for (NamedElement namedElement : ownedMemberList) {
                String nameStr = namedElement.getName();
                boolean isInsidePackage = judgeIsInsidePackage(namedElement);
                if (StringUtils.isNotBlank(nameStr) && (!isInsidePackage)) {
                    WordElement subWordElement = new WordElement();
                    addElementName(subWordElement, namedElement, nameStr);
                    subWordElement.setElementType(addElementType(namedElement));
                    subWordElement.setWide(rootElement.getWide() + 1);
                    subWordElement.setQualifiedName(namedElement.getQualifiedName());
                    boolean hasInstance = ExportDocUtil.judgeHasInstance(namedElement);
                    subWordElement.setHasInstance(hasInstance);
                    if (hasInstance) {
                        subWordElement.setWide(0);
                    }
                    rootElement.addWordElement(subWordElement);
                    addNodeInfo(namedElement, subWordElement);
                }
            }
        }
    }

    /**
     * 添加节点信息
     *
     * @param ownedMember
     * @param packWordElement
     */
    private void addNodeInfo(NamedElement ownedMember, WordElement packWordElement) {
        if (ownedMember instanceof Activity || ownedMember instanceof Diagram) {
            ParserModelInfo.parserModelInfo(ownedMember, packWordElement);
        } else {
            /* 创建子包 */
            if (ownedMember instanceof Package) {
                Package pak = (Package) ownedMember;
                Collection<NamedElement> subMemberList = pak.getMember();
                boolean hasInstance = ExportDocUtil.judgeHasInstance(pak);
                packWordElement.setHasInstance(hasInstance);
                if (hasInstance) {
                    packWordElement.setWide(0);
                }
                addElementInfo(subMemberList, packWordElement, ownedMember);
            } else if (ownedMember instanceof Class) {
                Class ownClass = (Class) ownedMember;
                Collection<NamedElement> subClassMemberList = ownClass.getMember();
                addElementInfo(subClassMemberList, packWordElement, ownedMember);
            } else if (ownedMember instanceof Collaboration) {
                Collaboration collaboration = (Collaboration) ownedMember;
                Collection<NamedElement> collaborationList = collaboration.getMember();
                addElementInfo(collaborationList, packWordElement, ownedMember);
            } else if (ownedMember instanceof UseCase) {
                UseCase ownUseCase = (UseCase) ownedMember;
                Collection<NamedElement> useCaseList = ownUseCase.getMember();
                addElementInfo(useCaseList, packWordElement, ownedMember);
            }
        }

    }

    /**
     * 添加元素信息
     *
     * @param subMemberList
     * @param packWordElement
     * @param ownedMember
     */
    private void addElementInfo(Collection<NamedElement> subMemberList, WordElement packWordElement,
                                NamedElement ownedMember) {
        for (NamedElement subMember : subMemberList) {
            WordElement subWordElement = new WordElement();
            String parentId = subMember.getObjectParent().getID();
            /* 判断是否是本层级的节点 */
            if (!ownedMember.getID().equals(parentId)) {
                return;
            }
            if (StringUtils.isNotBlank(subMember.getName())) {
                if (parserElementList.contains(subMember.getID())) {
                    continue;
                } else {
                    parserElementList.add(subMember.getID());
                    if (subMember instanceof InstanceSpecification) {
                        Classifier subMemberClass = ((InstanceSpecification) subMember).getClassifier().get(0);
                        if (subMemberClass != null) {
                            if (Objects.equals(subMemberClass.getObjectParent().getHumanName(),
                                    ownedMember.getHumanName())) {
                                /* 存储实例化的值 */
                                saveInstanceValue((InstanceSpecification) subMember);
                            }
                        }
                        continue;
                    }
                    if (subMember instanceof Package) {
                        subWordElement.setElementName(wipeNum(subMember.getName()));
                    } else {
                        subWordElement.setElementName(subMember.getName());
                    }
                    subWordElement.setWide(packWordElement.getWide() + 1);
                    subWordElement.setQualifiedName(subMember.getQualifiedName());
                    subWordElement.setElementType(addElementType(subMember));
                    /* 处理Property信息 */
                    if (subMember instanceof Property) {
                        if ((subMember).getHumanType().equals("Part Property")) {
                            Type ownType = ((Property) subMember).getType();
                            if (ownType instanceof Class) {
                                Class ownClass = (Class) ownType;
                                Collection<NamedElement> subClassMemberList = ownClass.getMember();
                                addElementInfo(subClassMemberList, subWordElement, ownClass);
                            }
                        }
                    }
                    addNodeInfo(subMember, subWordElement);
                    packWordElement.addWordElement(subWordElement);
                }
            }
        }
    }

    private void saveInstanceValue(InstanceSpecification iSpecification) {
        java.util.Map<String, String> map = new HashMap<>();
        getSlotInfo(iSpecification, null, map);
        instanceMapList.add(map);
    }

    /**
     * 获取instance的子节点信息
     *
     * @param iSpecification
     * @param map
     */
    private void getSlotInfo(InstanceSpecification iSpecification, InstanceValue instanceValue,
                             java.util.Map<String, String> map) {
        Collection<Slot> slotList = iSpecification.getSlot();

        for (Slot slot : slotList) {
            List<ValueSpecification> valueList = slot.getValue();

            for (ValueSpecification vSpecification : valueList) {
                if (vSpecification instanceof InstanceValue) {
                    getSlotInfo(((InstanceValue) vSpecification).getInstance(), (InstanceValue) vSpecification, map);
                } else if (vSpecification instanceof LiteralSpecification) {
                    if (instanceValue != null) {
                        String nameString = ((InstanceValue) instanceValue).getOwningSlot().getDefiningFeature()
                                .getQualifiedName();

                        String valueString = getLiteralValue(vSpecification);
                        if (StringUtils.isNotBlank(nameString) && StringUtils.isNotBlank(valueString)) {
                            map.put(nameString, valueString);
                        }
                    }
                }
            }
        }
    }

    /**
     * 获取prop的值
     *
     * @param va
     * @return
     */
    public static String getLiteralValue(ValueSpecification va) {
        String value = " ";
        if (va instanceof LiteralReal) {
            value = String.valueOf(((LiteralReal) va).getValue());
            Element owner = va.getOwner();
            if (owner != null && owner instanceof Slot) {
                String dataName = getDataName(owner);
                if (StringUtils.isNotBlank(dataName)) {
                    value += " " + dataName;
                }
            }

        } else if (va instanceof LiteralBoolean) {
            if (((LiteralBoolean) va).isValue()) {
                value = "true";
            } else {
                value = "false";
            }
        } else if (va instanceof LiteralInteger) {
            value = String.valueOf(((LiteralInteger) va).getValue());
        } else if (va instanceof LiteralString) {
            value = ((LiteralString) va).getValue();
        }
        return value;
    }

    /**
     * 获取单位
     *
     * @param owner
     * @return
     */
    private static String getDataName(Element owner) {
        String value = "";
        Slot ownerSlot = (Slot) owner;
        StructuralFeature definingFeature = ownerSlot.getDefiningFeature();
        if (definingFeature != null && definingFeature instanceof Property) {
            Property property = (Property) definingFeature;
            if (property != null && property.getType() instanceof DataType) {
                DataType dataType = (DataType) property.getType();
                if (dataType != null) {
                    String dataName = dataType.getName();
                    if (StringUtils.isNotBlank(dataName) && dataName.contains("[") && dataName.contains("]")) {
                        dataName = dataName.substring(dataName.indexOf("[") + 1, dataName.indexOf("]"));
                        if (StringUtils.isNotBlank(dataName)) {
                            value = dataName;
                        }
                    }
                }

            }

        }
        return value;
    }

    /**
     * 如果是包或者顶层模型节点，去除名称中的小数点与数字
     *
     * @param wordElement
     * @param namedElement
     * @param nameStr
     */
    private void addElementName(WordElement wordElement, NamedElement namedElement, String nameStr) {
        if (namedElement instanceof Package) {
            wordElement.setElementName(wipeNum(nameStr));
        } else {
            wordElement.setElementName(nameStr);
        }
    }

    /**
     * 生成顶层节点，用于生成文档的标题
     *
     * @param namedElement
     * @return
     */
    private WordElement createRootElement(NamedElement namedElement) {
        String nameStr = namedElement.getName();
        if (StringUtils.isNotBlank(nameStr)) {
            WordElement rootElement = new WordElement();
            /* 如果是包或者顶层模型节点，去除名称中的小数点与数字 */
            addElementName(rootElement, namedElement, nameStr);
            rootElement.setWide(0);
            rootElement.setElementType(addElementType(namedElement));
            return rootElement;
        }
        return null;
    }

    /**
     * 添加元素的类型
     *
     * @param namedElement
     * @return
     */
    private int addElementType(NamedElement namedElement) {
        if (namedElement instanceof Model) {
            return ElementType.MODEL_TYPE;
        } else if (namedElement instanceof Package) {
            return ElementType.PACKAGE_TYPE;
        } else if (namedElement instanceof Diagram || namedElement instanceof Activity) {
            return ElementType.DIAGRAM_TYPE;
        } else if (namedElement instanceof Class || namedElement instanceof UseCase
                || namedElement instanceof Collaboration) {
            return ElementType.CLASS_TYPE;
        } else if (namedElement instanceof Property) {
            if (namedElement.getHumanType().equals("Part Property")) {
                return ElementType.PART_PROPERTY_TYPE;
            } else if (namedElement.getHumanType().equals("Value Property")) {
                return ElementType.VALUE_PROPERTY_TYPE;
            } else if (namedElement instanceof Port) {
                return ElementType.PORT_TYPE;
            } else {
                return ElementType.OTHER_TYPE;
            }
        } else {
            return ElementType.OTHER_TYPE;
        }
    }

}
