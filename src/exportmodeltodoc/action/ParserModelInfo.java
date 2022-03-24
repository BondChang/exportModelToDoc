package exportmodeltodoc.action;

import com.nomagic.magicdraw.foundation.diagram.AbstractDiagramRepresentationObject;
import com.nomagic.uml2.ext.magicdraw.actions.mdbasicactions.CallBehaviorAction;
import com.nomagic.uml2.ext.magicdraw.activities.mdbasicactivities.ActivityEdge;
import com.nomagic.uml2.ext.magicdraw.activities.mdbasicactivities.ActivityFinalNode;
import com.nomagic.uml2.ext.magicdraw.activities.mdbasicactivities.InitialNode;
import com.nomagic.uml2.ext.magicdraw.activities.mdfundamentalactivities.Activity;
import com.nomagic.uml2.ext.magicdraw.activities.mdfundamentalactivities.ActivityNode;
import com.nomagic.uml2.ext.magicdraw.activities.mdintermediateactivities.ActivityPartition;
import com.nomagic.uml2.ext.magicdraw.activities.mdintermediateactivities.DecisionNode;
import com.nomagic.uml2.ext.magicdraw.activities.mdintermediateactivities.MergeNode;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Class;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Package;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.*;
import com.nomagic.uml2.ext.magicdraw.commonbehaviors.mdbasicbehaviors.Behavior;
import com.nomagic.uml2.ext.magicdraw.compositestructures.mdcollaborations.Collaboration;
import com.nomagic.uml2.ext.magicdraw.interactions.mdbasicinteractions.Interaction;
import com.nomagic.uml2.ext.magicdraw.interactions.mdbasicinteractions.Lifeline;
import com.nomagic.uml2.ext.magicdraw.interactions.mdbasicinteractions.Message;
import com.nomagic.uml2.ext.magicdraw.interactions.mdbasicinteractions.MessageOccurrenceSpecification;
import com.nomagic.uml2.ext.magicdraw.statemachines.mdbehaviorstatemachines.*;
import diagram.entity.*;
import exportmodeltodoc.entity.Branch;
import exportmodeltodoc.entity.StateFlow;
import exportmodeltodoc.entity.StateItem;
import exportmodeltodoc.entity.WordElement;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static exportmodeltodoc.action.ExportDocUtil.wipeNum;

public class ParserModelInfo {

    private static final String BDD_DIAGRAM = "Block Definition Diagram";

    private static final String IBD_DIAGRAM = "Internal Block Diagram";

    private static final String PARAMETRIC_DIAGRAM = "Parametric Diagram";

    private static final String PACKAGE_DIAGRAM = "Package Diagram";

    private static final String REQUIREMENT_DIAGRAM = "Requirement Diagram";

    private static final String ACTIVITY_DIAGRAM = "Activity Diagram";

    private static final String SEQUENCE_DIAGRAM = "Sequence Diagram";

    private static final String STATE_MACHINE_DIAGRAM = "State Machine Diagram";

    private static final String USECASE_DIAGRAM = "Use Case Diagram";

    /**
     * 解析图形信息
     *
     * @param ownedMember
     * @param rootElement
     */
    public static void parserModelInfo(NamedElement ownedMember, WordElement rootElement) {
        boolean isInsidePackage = ExportDocUtil.judgeIsInsidePackage(ownedMember);
        if (!isInsidePackage) {
            /* 如果在子包里 */
            if (ownedMember instanceof Package) {
                Package pak = (Package) ownedMember;
                Collection<NamedElement> subMemberList = pak.getMember();
                for (NamedElement subMember : subMemberList) {
                    parserModelInfo(subMember, rootElement);
                }
            } else {
                /* 如果是活动图 */
                if (ownedMember instanceof Activity) {
                    DiagramInfo diagramInfo = new DiagramInfo();
                    parserActivityInfo((Activity) ownedMember, diagramInfo);
                    parserActivityLane((Activity) ownedMember, diagramInfo);
                    diagramInfo.setWide(rootElement.getWide() + 1);
                    if (diagramInfo != null && diagramInfo.getDiagramName() != null) {
                        rootElement.addDiagramInfo(diagramInfo);
                    }
                    Collection<NamedElement> subMemberList = ((Activity) ownedMember).getMember();
                    for (NamedElement subMember : subMemberList) {
                        parserModelInfo(subMember, rootElement);
                    }
                } else if (ownedMember instanceof Diagram) {
                    DiagramInfo diagramInfo = new DiagramInfo();
                    diagramInfo.setWide(rootElement.getWide() + 1);
                    com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Diagram diagram = (com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Diagram) ownedMember;
                    AbstractDiagramRepresentationObject representation = diagram.get_representation();
                    String diagramType = representation.getType();
                    parserDiagramInfo(diagramType, diagram, diagramInfo);
                    if (diagramInfo != null && diagramInfo.getDiagramName() != null) {
                        rootElement.addDiagramInfo(diagramInfo);
                    }
                } else if (ownedMember instanceof Class) {
                    Class ownClass = (Class) ownedMember;
                    Collection<NamedElement> subClassMemberList = ownClass.getMember();
                    for (NamedElement classMember : subClassMemberList) {
                        parserModelInfo(classMember, rootElement);
                    }
                }
                /* 时序图中的对象 */
                else if (ownedMember instanceof Collaboration) {
                    Collaboration collaboration = (Collaboration) ownedMember;
                    Collection<NamedElement> memberList = collaboration.getMember();
                    for (NamedElement classMember : memberList) {
                        parserModelInfo(classMember, rootElement);
                    }
                }
            }
        }
    }

    /**
     * 解析活动图的泳道信息
     *
     * @param ownedMember
     * @param diagramInfo
     */
    private static void parserActivityLane(Activity ownedMember, DiagramInfo diagramInfo) {
        List<Lane> laneList = new ArrayList<>();
        diagramInfo.setActivityLaneList(laneList);
        Collection<ActivityNode> nodeList = ownedMember.getNode();
        for (ActivityNode node : nodeList) {
            Collection<ActivityPartition> partitionList = node.getInPartition();
            if (partitionList != null && partitionList.size() > 0) {
                for (ActivityPartition partition : partitionList) {
                    Element represents = partition.getRepresents();
                    if (represents != null) {
                        if (represents instanceof Property) {
                            Property property = (Property) represents;
                            String laneName = property.getName();
                            Lane lane = getLane(laneList, laneName);
                            lane.getNodeList().add(node.getName());
                        }
                    }
                }
            }
        }
    }

    private static Lane getLane(List<Lane> laneList, String laneName) {
        for (Lane subLane : laneList) {
            if (subLane.getLaneName().equals(laneName)) {
                return subLane;
            }
        }
        Lane lane = new Lane(laneName);
        laneList.add(lane);
        return lane;
    }

    /**
     * 解析diagram图信息
     *
     * @param diagramType
     * @param diagram
     * @param diagramInfo
     */
    private static void parserDiagramInfo(String diagramType, Diagram diagram, DiagramInfo diagramInfo) {
        /* 用例图 */
        if (diagramType.contains(USECASE_DIAGRAM)) {
            setDiagramBasicInfo(diagram, diagramInfo, diagramType);
            // parserUseCaseInfo(diagram, diagramInfo);
        }
        /* 状态机图 */
        else if (diagramType.contains(STATE_MACHINE_DIAGRAM)) {
            setDiagramBasicInfo(diagram, diagramInfo, diagramType);
            parserStmInfo(diagram, diagramInfo);
        }
        /* 时序图 */
        else if (diagramType.contains(SEQUENCE_DIAGRAM)) {
            setDiagramBasicInfo(diagram, diagramInfo, diagramType);
            parserSequenceInfo(diagram, diagramInfo);
        }
        /* 包图 */
        else if (diagramType.contains(PACKAGE_DIAGRAM)) {
            setDiagramBasicInfo(diagram, diagramInfo, diagramType);
        }
        /* ibd图 */
        else if (diagramType.contains(IBD_DIAGRAM)) {
            setDiagramBasicInfo(diagram, diagramInfo, diagramType);
            parserIBDInfo(diagram, diagramInfo);
        }
        /* bdd图 */
        else if (diagramType.contains(BDD_DIAGRAM)) {
            setDiagramBasicInfo(diagram, diagramInfo, diagramType);
            parserBDDInfo(diagram, diagramInfo);
        }
        /* 需求图 */
        else if (diagramType.contains(REQUIREMENT_DIAGRAM)) {
            setDiagramBasicInfo(diagram, diagramInfo, diagramType);
            parserRequirementInfo(diagram, diagramInfo);
            diagramInfo.getSubDiagramList().add(diagramInfo);
            diagramInfo.setDiagramName(diagram.getName());
        }
        /* 参数图 */
        else if (diagramType.contains(PARAMETRIC_DIAGRAM)) {
            setDiagramBasicInfo(diagram, diagramInfo, diagramType);
            parserParaInfo(diagram, diagramInfo);
        } else {
            //TODO
            System.out.println(diagramType + "------------------------------------------");
        }

    }

    private static void parserIBDInfo(Diagram diagram, DiagramInfo diagramInfo) {
        Namespace namespace = diagram.getNamespace();
        if (namespace instanceof Class) {
            Class ibdClass = (Class) namespace;
            addIBDInfo(ibdClass, diagramInfo);
        }
    }

    /**
     * 添加ibd的图的信息
     *
     * @param ibdClass
     * @param diagramInfo
     */
    private static void addIBDInfo(Class ibdClass, DiagramInfo diagramInfo) {
        Collection<NamedElement> memberList = ibdClass.getMember();
        List<BaseChild> filterSubMemberList = filterIBDMember(memberList);
        if (filterSubMemberList != null && filterSubMemberList.size() > 0) {
            IBDDiagram bddDiagram = new IBDDiagram(wipeNum(ibdClass.getName()), filterSubMemberList);
            diagramInfo.setBddDiagram(bddDiagram);
        }
    }

    /**
     * 过滤ibd图的元素
     *
     * @param memberList
     * @return
     */
    private static List<BaseChild> filterIBDMember(Collection<NamedElement> memberList) {
        List<BaseChild> propList = new ArrayList<BaseChild>();
        for (NamedElement subElement : memberList) {
            if (subElement instanceof Property) {
                Property idbProperty = (Property) subElement;
                if (idbProperty.getHumanType().equals("Part Property")) {
                    String childDiagramName = getIBDChildDiagram(idbProperty);
                    BaseChild bddChild = new BaseChild(idbProperty.getName(), childDiagramName);
                    bddChild.setType(setStateType(subElement));
                    propList.add(bddChild);
                }
            }
        }
        return propList;
    }

    private static String getIBDChildDiagram(Property ibdProperty) {
        Type ibdPropertyType = ibdProperty.getType();
        if (ibdPropertyType instanceof Class) {
            Class ibdClass = (Class) ibdPropertyType;
            Collection<NamedElement> memberList = ibdClass.getMember();
            for (NamedElement element : memberList) {
                if (element instanceof Diagram) {
                    return ((Diagram) element).getName();
                }
            }
        }
//        System.out.println();
//        Collection<Element> ownedElementList = ibdProperty.getOwnedElement();
//        for (Element element : ownedElementList) {
//            if (element instanceof Diagram) {
//                return ((Diagram) element).getName();
//            } else if (element instanceof InstanceSpecification) {
//                InstanceSpecification instanceSpecification= (InstanceSpecification) element;
//                List<Classifier> classifier1 = instanceSpecification.getClassifier();
//                Classifier classifier = instanceSpecification.getClassifier().get(0);
//                Collection<TypedElement> typedElementOfTypeList = classifier.get_typedElementOfType();
//                System.out.println();
//            }
//        }
        return "";
    }

    /**
     * 解析状态机图信息
     *
     * @param diagram
     * @param diagramInfo
     */
    private static void parserStmInfo(Diagram diagram, DiagramInfo diagramInfo) {
        Namespace stmtNamespace = diagram.getNamespace();
        if (stmtNamespace instanceof StateMachine) {
            StateMachine stateMachine = (StateMachine) stmtNamespace;
            Collection<Region> stateMachineRegionList = stateMachine.getRegion();
            for (Region region : stateMachineRegionList) {
                Collection<NamedElement> stmtMemberList = region.getMember();
                StateFlow stateFlow = addStmtInfo(stmtMemberList);
                diagramInfo.setStateFlow(stateFlow);
            }
        }
    }

    public static StateFlow addStmtInfo(Collection<NamedElement> stmtMemberList) {
        StateFlow stateFlow = new StateFlow();
        /* 创建所有的stateItem */
        for (NamedElement namedElement : stmtMemberList) {
            if (namedElement instanceof State) {
                String stateName = namedElement.getName();
                if (StringUtils.isNotBlank(stateName)) {
                    StateItem stateItem = new StateItem(stateName);
                    stateItem.setType(setStateType(namedElement));
                    stateItem.setEdit(false);
                    stateFlow.getStateItemList().add(stateItem);
                }
            }
        }
        for (NamedElement namedElement : stmtMemberList) {
            /* 获取所有的连线 */
            if (namedElement instanceof Transition) {
                String sourceName = ((Transition) namedElement).getSource().getName();
                String targetName = ((Transition) namedElement).getTarget().getName();
                /* 说明是分支 */
                if (StringUtils.isNotBlank(sourceName) && StringUtils.isBlank(targetName)) {
                    Vertex target = ((Transition) namedElement).getTarget();
                    if (target instanceof Pseudostate) {
                        Pseudostate pseudostate = (Pseudostate) target;
                        // 获取节点类型
                        PseudostateKind kind = pseudostate.getKind();
                        if (kind.toString().equals("choice")) {
                            StateItem stateItem = getStateItem(sourceName, stateFlow.getStateItemList());
                            if (stateItem != null) {
                                stateItem.setStateId(target.getID());
                            }
                        }
                    }
                }
                /* 设置guard信息 */
                else if (StringUtils.isBlank(sourceName) && StringUtils.isNotBlank(targetName)) {
                    Constraint guard = ((Transition) namedElement).getGuard();
                    if (guard != null) {
                        String targetId = ((Transition) namedElement).getSource().getID();
                        StateItem stateItem = useIdGetStateItem(targetId, stateFlow.getStateItemList());
                        if (stateItem != null) {
                            /* 获取guard条件 */
                            ValueSpecification specification = guard.getSpecification();
                            if (specification instanceof OpaqueExpression) {
                                OpaqueExpression opaqueExpression = (OpaqueExpression) specification;
                                List<String> bodyList = opaqueExpression.getBody();
                                if (bodyList != null && bodyList.size() > 0) {
                                    String guardStr = bodyList.get(0);
                                    if (StringUtils.isNotBlank(guardStr) && StringUtils.isNotBlank(targetName)) {
                                        Branch branch = new Branch(guardStr, targetName);
                                        stateItem.getBranchList().add(branch);
                                    }
                                }
                            }
                        }
                    }
                }
            }

        }
        return stateFlow;
    }

    /**
     * 获取stateItem
     *
     * @param stateID
     * @param stateItemList
     * @return
     */
    private static StateItem useIdGetStateItem(String stateID, List<StateItem> stateItemList) {
        if (stateItemList == null || stateItemList.size() == 0) {
            return null;
        }
        for (StateItem stateItem : stateItemList) {
            if (StringUtils.isNotBlank(stateItem.getStateId())) {
                if (stateItem.getStateId().equals(stateID)) {
                    return stateItem;
                }
            }
        }
        return null;
    }


    /**
     * 获取stateItem
     *
     * @param stateName
     * @param stateItemList
     * @return
     */
    private static StateItem getStateItem(String stateName, List<StateItem> stateItemList) {
        if (stateItemList == null || stateItemList.size() == 0) {
            return null;
        }
        for (StateItem stateItem : stateItemList) {
            if (stateItem.getName().equals(stateName)) {
                return stateItem;
            }
        }
        return null;
    }

    /**
     * 解析参数图信息
     *
     * @param diagram
     * @param diagramInfo
     */
    private static void parserParaInfo(Diagram diagram, DiagramInfo diagramInfo) {
        Namespace namespace = diagram.getNamespace();
        Collection<NamedElement> memberList = namespace.getMember();
        for (NamedElement namedElement : memberList) {
            if (namedElement instanceof Property) {
                if (namedElement.getHumanType().equals("Constraint Property")) {
                    Property property = (Property) namedElement;

                    Type properType = property.getType();
                    if (properType instanceof Class) {
                        String name = properType.getName();
                        Collection<Constraint> constraintList = properType.get_constraintOfConstrainedElement();
                        if (StringUtils.isNotBlank(name) && constraintList != null && constraintList.size() > 0) {
                            for (Constraint constraint : constraintList) {
                                ValueSpecification specification = constraint.getSpecification();
                                if (specification instanceof OpaqueExpression) {
                                    OpaqueExpression opaqueExpression = (OpaqueExpression) specification;
                                    List<String> bodyList = opaqueExpression.getBody();
                                    String conString = "";
                                    for (String con : bodyList) {
                                        conString += con + " ";
                                    }
                                    if (StringUtils.isNotBlank(conString)) {
                                        ParaDiagram paraDiagram = new ParaDiagram(name, conString);
                                        diagramInfo.getParaDiagramList().add(paraDiagram);
                                    }
                                }
//								List<Element> constrainedElement = constraint.getConstrainedElement();
//								System.out.println(constrainedElement.size());
//								for (Element element : constrainedElement) {
//									System.out.println(element.getClass());
//									System.out.println(element.getHumanName());
//									System.out.println();
//								}
                            }
                        }

                    }
                }
            }

        }

    }

    /**
     * 解析bdd图
     *
     * @param diagram
     * @param diagramInfo
     */
    private static void parserBDDInfo(Diagram diagram, DiagramInfo diagramInfo) {
        Namespace namespace = diagram.getNamespace();
        if (namespace instanceof Package) {
            Package bddPackage = (Package) namespace;
            Collection<PackageableElement> packagedElement = bddPackage.getPackagedElement();
            List<BaseChild> bddClassList = new ArrayList<>();
            for (PackageableElement ele : packagedElement) {
                if (ele instanceof Class) {
                    Class bddClass = (Class) ele;
                    String childDiagramName = getChildDiagram(bddClass);
                    BaseChild bddChild = new BaseChild(bddClass.getName(), childDiagramName);
                    bddChild.setType(setStateType(bddClass));
                    bddClassList.add(bddChild);
                }
            }
            BDDDiagram bddDiagram = new BDDDiagram(wipeNum(namespace.getName()), bddClassList);
            diagramInfo.setBddDiagram(bddDiagram);
//            for (Class bddClass : bddClassList) {
//                addBDDInfo(bddClass, diagramInfo);
//            }
        } else if (namespace instanceof Class) {
            Class bddClass = (Class) namespace;
            addBDDInfo(bddClass, diagramInfo);
        }
    }

    private static void addBDDInfo(Class bddClass, DiagramInfo diagramInfo) {
        Collection<NamedElement> memberList = bddClass.getMember();
        /* 筛选子元素，只留下Part property */
        List<BaseChild> filterSubMemberList = filterSubMember(memberList);
        if (filterSubMemberList != null && filterSubMemberList.size() > 0) {
            BDDDiagram bddDiagram = new BDDDiagram(bddClass.getName(), filterSubMemberList);
            diagramInfo.setBddDiagram(bddDiagram);
        }

    }

    private static List<BaseChild> filterSubMember(Collection<NamedElement> memberList) {
        List<BaseChild> propList = new ArrayList<BaseChild>();
        if (memberList != null && memberList.size() > 0) {
            for (NamedElement subElement : memberList) {
                if (subElement instanceof Class) {
                    Class bddClass = (Class) subElement;
                    String childDiagramName = getChildDiagram(bddClass);
                    BaseChild bddChild = new BaseChild(subElement.getName(), childDiagramName);
                    bddChild.setType(setStateType(subElement));
                    propList.add(bddChild);
                }
            }
        }
        return propList;
    }

    private static String getChildDiagram(Class bddClass) {
        Collection<NamedElement> memberList = bddClass.getMember();
        for (NamedElement element : memberList) {
            if (element instanceof Diagram) {
                return ((Diagram) element).getName();
            }
        }
        return "";
    }

    /**
     * 解析需求图
     *
     * @param diagram
     * @param diagramInfo
     */
    private static void parserRequirementInfo(Diagram diagram, DiagramInfo diagramInfo) {

        Namespace namespace = diagram.getNamespace();
        if (namespace instanceof Class) {
            parserClassRequirementInfo((Class) namespace, diagramInfo);
        } else if (namespace instanceof Package) {
            Package reqPackage = (Package) namespace;
            Collection<PackageableElement> packagedElementList = reqPackage.getPackagedElement();
            for (PackageableElement packageableElement : packagedElementList) {
                if (packageableElement instanceof Class) {
                    parserClassRequirementInfo((Class) packageableElement, diagramInfo);
                }
            }

//			for(NamedElement namedElement:subMemberList){
//				if(namedElement instanceof Diagram){
//					parserRequirementInfo((Diagram) namedElement,diagramInfo);
//				}
//			}

        }
    }

    private static void parserClassRequirementInfo(Class packageableElement, DiagramInfo diagramInfo) {
        Class reqClass = (Class) packageableElement;
        List<Property> attributeList = reqClass.getAttribute();
        List<ReqDiagram> reqDiagramList = new ArrayList<ReqDiagram>();
        ReqDiagram reqDiagram = new ReqDiagram(reqClass.getName(), reqClass.getHumanType());
        reqDiagramList.add(reqDiagram);
        if (reqClass.hasNestedClassifier()) {
            for (Classifier classifiers : reqClass.getNestedClassifier()) {
                // 遍历子需求
                ReqDiagram subReqDiagram = new ReqDiagram(classifiers.getName(), classifiers.getHumanType());
                reqDiagramList.add(subReqDiagram);
            }
        }
        diagramInfo.setReqDiagram(reqDiagramList);
    }

    /**
     * 解析时序图的基本信息
     *
     * @param diagram
     * @param diagramInfo
     */
    private static void parserSequenceInfo(Diagram diagram, DiagramInfo diagramInfo) {
        Collection<Namespace> ownedElementList = diagram.get_namespaceOfMember();
        List<String> lifeList = new ArrayList<>();
        List<String> messageList = new ArrayList<>();
        //System.out.println(diagram.getName());
        for (Namespace ownedElement : ownedElementList) {
            if (ownedElement instanceof Interaction) {
                Interaction interaction = (Interaction) ownedElement;
                Collection<NamedElement> iNamedElementList = interaction.getMember();
                Collection<Message> message1List = interaction.getMessage();
                for (Message message : message1List) {
                    System.out.println(message.getName());
                }
                for (NamedElement namedElement : iNamedElementList) {
                    if (namedElement instanceof Lifeline) {
                        Lifeline lifeline = (Lifeline) namedElement;
                        lifeList.add(lifeline.getRepresents().getName());
                    } else if (namedElement instanceof Message) {
//                        Message message = (Message) namedElement;
//                        Constraint guard = message.getGuard();
//                        String messageName = namedElement.getName();
//                        if (guard != null) {
//                            ValueSpecification specification = guard.getSpecification();
//                            if (specification != null) {
//                                if (specification != null && specification instanceof OpaqueExpression) {
//                                    OpaqueExpression opaqueExpression = (OpaqueExpression) specification;
//                                    List<String> bodyList = opaqueExpression.getBody();
//                                    if (bodyList != null && bodyList.size() > 0) {
//                                        for (String bodyStr : bodyList) {
//                                            if (StringUtils.isNotBlank(bodyStr)) {
//                                                messageName = bodyStr + " " + messageName;
//                                            }
//                                            messageList.add(messageName);
//                                        }
//
//                                    }
//                                }
//                            }
//                        } else {
//                            if (StringUtils.isNotBlank(messageName)) {
//                                messageList.add(messageName);
//                            }
//                        }
                        //String guardName = Optional.ofNullable(message).map(m -> m.getGuard()).map(m -> m.getBodyContext()).map(m -> m.getName()).orElse("")
                    } else if (namedElement instanceof MessageOccurrenceSpecification) {
                        Message message = ((MessageOccurrenceSpecification) namedElement).getMessage();
                        if (message != null) {
                            addSequenceMessage(message, messageList);
                        }
                    }
                }
            }

        }
        SequenceDiagram sequenceDiagram = new SequenceDiagram();
        sequenceDiagram.setLifeLineList(lifeList);
        sequenceDiagram.setMessageList(messageList);
        diagramInfo.setSequenceDiagram(sequenceDiagram);
    }

    /**
     * 添加时序图的消息信息
     *
     * @param message
     * @param messageList
     */
    private static void addSequenceMessage(Message message, List<String> messageList) {
        Constraint guard = message.getGuard();
        //Collection<InformationFlow> informationFlowOfInformationSource = message.get_informationFlowOfInformationSource();
        //Collection<Namespace> namespaceOfMemberList = message.get_namespaceOfMember();
//        for(Namespace namespace:namespaceOfMemberList){
//            if(namespace instanceof Interaction){
//                Interaction interaction = (Interaction) namespace;
//                Collection<NamedElement> iNamedElementList = interaction.getMember();
//                for (NamedElement namedElement : iNamedElementList) {
//                    if (namedElement instanceof Lifeline) {
//                        Lifeline lifeline = (Lifeline) namedElement;
//                        System.out.println(lifeline.getRepresents().getName()+"88888888888888888888888");
//                    }
//                }
//                        //lifeList.add(lifeline.getRepresents().getName());
//            }
//        }
        String messageName = message.getName();
        if (guard != null) {
            ValueSpecification specification = guard.getSpecification();
            if (specification != null) {
                if (specification != null && specification instanceof OpaqueExpression) {
                    OpaqueExpression opaqueExpression = (OpaqueExpression) specification;
                    List<String> bodyList = opaqueExpression.getBody();
                    if (bodyList != null && bodyList.size() > 0) {
                        for (String bodyStr : bodyList) {
                            if (StringUtils.isNotBlank(bodyStr)) {
                                messageName = bodyStr + " " + messageName;
                            }
                            if (StringUtils.isNotBlank(messageName) && (!messageList.contains(messageName))) {
                                messageList.add(messageName);
                            }
                        }

                    }
                }
            }
        } else {
            if (StringUtils.isNotBlank(messageName) && (!messageList.contains(messageName))) {
                messageList.add(messageName);
            }
        }
    }

    /**
     * 设置图的基本信息
     *
     * @param diagram
     * @param diagramInfo
     * @param diagramType
     */
    private static void setDiagramBasicInfo(Diagram diagram, DiagramInfo diagramInfo, String diagramType) {
        diagramInfo.setDiagramName(diagram.getName());
        diagramInfo.setDiagramEntity(diagram);
        int diagramFlag = getDiagramFlag(diagramType);
        if (diagramFlag != -1) {
            diagramInfo.setDiagramType(diagramFlag);
        }

    }

    /**
     * 解析用例图信息
     *
     * @param diagram
     * @param diagramInfo
     */
    private static void parserUseCaseInfo(Diagram diagram, DiagramInfo diagramInfo) {

    }

    /**
     * 解析活动图信息
     *
     * @param ownedMember
     * @param diagramInfo
     */
    private static void parserActivityInfo(Activity ownedMember, DiagramInfo diagramInfo) {
        String activityName = ownedMember.getName();
        diagramInfo.setDiagramName(activityName);
        diagramInfo.setDiagramType(DiagramType.ACTIVITY);
        diagramInfo.setDiagramEntity(ownedMember);
        StateFlow stateFlow = parserActivityStateInfo(ownedMember);
        diagramInfo.setStateFlow(stateFlow);
    }

    private static StateFlow parserActivityStateInfo(Activity activity) {
        StateFlow stateFlow = new StateFlow();
        List<String> existList = new ArrayList<>();
        Collection<ActivityEdge> edgeList = activity.getEdge();
        ActivityEdge rootEdge = getRootEdge(edgeList);
        if (rootEdge != null) {
            ActivityNode target = rootEdge.getTarget();
            if (target != null) {
                String rootName = target.getName();
                StateItem stateItem = new StateItem(rootName);
                stateItem.setType(setStateType(target));
                setCanEdit(target, stateItem);
                addSubStmt(target, stateItem, edgeList, stateFlow, existList, null);
            }
        }
        return stateFlow;
    }

    private static void setCanEdit(ActivityNode target, StateItem stateItem) {
        if (target != null) {
            if (target instanceof CallBehaviorAction) {
                CallBehaviorAction behaviorAction = (CallBehaviorAction) target;
                Behavior behavior = behaviorAction.getBehavior();
                if (behavior != null) {
                    stateItem.setEdit(true);
                    stateItem.setTypeName(behavior.getName());
                    return;
                }
            }
        }
        stateItem.setEdit(false);
    }

    private static String setStateType(NamedElement target) {
        if (target instanceof CallBehaviorAction || target instanceof DecisionNode) {
            return "(A)";
            //stateItem.setType("(A)");
        } else if (target instanceof State) {
            return "(S)";
            //stateItem.setType("(S)");
        } else if (target instanceof Property) {
            return ("(P)");
        } else if (target instanceof Class) {
            return ("(B)");
        } else {
            return ("(N)");
            //stateItem.setType("(N)");
        }

    }

    private static void addSubStmt(ActivityNode rootNode, StateItem stateItem, Collection<ActivityEdge> edgeList, StateFlow stateFlow, List<String> existList, Branch wideBranch) {
        existList.add(stateItem.getName());
        List<ActivityEdge> targetEdgeList = getTargetEdge(rootNode.getID(), edgeList);
        if (targetEdgeList != null && targetEdgeList.size() > 0) {
            ActivityNode sourceNode = targetEdgeList.get(0).getSource();
            ActivityNode targetNode = targetEdgeList.get(0).getTarget();
            //System.out.println(targetNode.getClass());
            if (sourceNode != null && targetNode != null) {
                /* 说明是Node连接Node */
                if (sourceNode instanceof CallBehaviorAction && targetNode instanceof CallBehaviorAction) {
                    stateFlow.getStateItemList().add(stateItem);
                    StateItem subStateItem = new StateItem(targetNode.getName());
                    subStateItem.setType(setStateType(targetNode));
                    setCanEdit(targetNode, subStateItem);
                    if (existList.contains(targetNode.getName())) {
                        stateFlow.getStateItemList().add(subStateItem);
                    } else {
                        addSubStmt(targetNode, subStateItem, edgeList, stateFlow, existList, wideBranch);
                    }
                }
                /* 说明Node连接Merge */
                else if (sourceNode instanceof CallBehaviorAction && (targetNode instanceof MergeNode || targetNode instanceof ActivityFinalNode)) {
                    stateFlow.getStateItemList().add(stateItem);
                }
                /* 说明是Node连接判断模块 */
                else if (sourceNode instanceof CallBehaviorAction && targetNode instanceof DecisionNode) {
                    List<ActivityEdge> decisionEdgeList = getTargetEdge(targetNode.getID(), edgeList);
                    stateFlow.getStateItemList().add(stateItem);
                    for (ActivityEdge decisionEdge : decisionEdgeList) {
                        ActivityNode target = decisionEdge.getTarget();
                        ValueSpecification guard = decisionEdge.getGuard();
                        if (guard != null) {
                            if (guard != null && guard instanceof OpaqueExpression) {
                                OpaqueExpression opaqueExpression = (OpaqueExpression) guard;
                                List<String> bodyList = opaqueExpression.getBody();
                                if (bodyList != null && bodyList.size() > 0) {
                                    String guardStr = bodyList.get(0);
                                    Branch branch = new Branch(guardStr, target.getName());
                                    stateItem.getBranchList().add(branch);
                                    StateItem subStateItem = new StateItem(target.getName());
                                    subStateItem.setType(setStateType(targetNode));
                                    setCanEdit(targetNode, subStateItem);
                                    StateFlow subStateFlow = new StateFlow();
                                    branch.setStateFlow(subStateFlow);
                                    if (wideBranch == null) {
                                        branch.setWide(1);
                                    } else {
                                        branch.setWide(wideBranch.getWide() + 1);
                                    }
                                    if (existList.contains(target.getName())) {
                                        subStateFlow.getStateItemList().add(subStateItem);
                                    } else {
                                        addSubStmt(target, subStateItem, edgeList, subStateFlow, existList, branch);
                                    }
                                }
                            }
                        }

                    }

                }
            }
        }
        //System.out.println();
    }

    private static List<ActivityEdge> getTargetEdge(String id, Collection<ActivityEdge> edgeList) {
        List<ActivityEdge> activityEdgeList = new ArrayList<>();
        for (ActivityEdge activityEdge : edgeList) {
            ActivityNode source = activityEdge.getSource();
            if (source != null && source.getID().equals(id)) {
                activityEdgeList.add(activityEdge);
            }
        }
        return activityEdgeList;
    }

    private static ActivityEdge getRootEdge(Collection<ActivityEdge> edgeList) {
        for (ActivityEdge activityEdge : edgeList) {
            ActivityNode source = activityEdge.getSource();
            if (source instanceof InitialNode) {
                return activityEdge;
            }
        }
        return null;
    }

    private static int getDiagramFlag(String diagramType) {
        if (diagramType.contains(BDD_DIAGRAM)) {
            return DiagramType.BDD;
        } else if (diagramType.contains(IBD_DIAGRAM)) {
            return DiagramType.IBD;
        } else if (diagramType.contains(PARAMETRIC_DIAGRAM)) {
            return DiagramType.PARAMETRIC;
        } else if (diagramType.contains(PACKAGE_DIAGRAM)) {
            return DiagramType.PACKAGE_DIAGRAM;
        } else if (diagramType.contains(REQUIREMENT_DIAGRAM)) {
            return DiagramType.REQUIREMENT;
        } else if (diagramType.contains(ACTIVITY_DIAGRAM)) {
            return DiagramType.ACTIVITY;
        } else if (diagramType.contains(SEQUENCE_DIAGRAM)) {
            return DiagramType.SEQUENCE;
        } else if (diagramType.contains(STATE_MACHINE_DIAGRAM)) {
            return DiagramType.STATE_MACHINE;
        } else if (diagramType.contains(USECASE_DIAGRAM)) {
            return DiagramType.USECASE;
        }
        return -1;
    }
}
