package exportmodeltodoc.action;

import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.InstanceSpecification;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.NamedElement;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Package;

import java.util.Collection;
import java.util.regex.Pattern;

public class ExportDocUtil {
    /**
     * ȥ�������е�������С��.
     *
     * @param name
     * @return
     */
    public static String wipeNum(String name) {
        String wipeName = Pattern.compile("[\\d]").matcher(name).replaceAll("");
        wipeName = wipeName.replace(".", "");
        return wipeName;
    }


    /**
     * �ж��Ƿ���magicdraw���ÿ�
     *
     * @param ownedMember
     * @return
     */
    public static boolean judgeIsInsidePackage(NamedElement ownedMember) {
        if (ownedMember == null) {
            return true;
        }
        /* ����magicdraw���ÿ� */
        if (ownedMember.getName().startsWith("Basic ") || ownedMember.getName().startsWith("MD Customization")
                || ownedMember.getName().startsWith("QUDV")
                || ownedMember.getName().equals("UseCase Description Profile") || ownedMember.getName().equals("SysML")
                || ownedMember.getName().equals("ReqIF Profile") || ownedMember.getName().equals("Unit Imports")
                || ownedMember.getName().equals("UML Testing Profile")) {
            return true;
        }
        if (ownedMember.getID().startsWith("cad") || ownedMember.getID().startsWith("_18_0beta")
                || ownedMember.getID().startsWith("_19_0beta") || ownedMember.getID().startsWith("magicdraw")
                || ownedMember.getID().startsWith("_9_0_be")) {
            return true;
        }
        return false;
    }

    /**
     * �жϰ����Ƿ����ʵ����
     *
     * @param namedElement
     * @return
     */
    public static boolean judgeHasInstance(NamedElement namedElement) {
        if (namedElement instanceof Package) {
            Package p = (Package) namedElement;
            Collection<NamedElement> memberList = p.getMember();
            if (memberList != null && memberList.size() > 0) {
                for (NamedElement subChild : memberList) {
                    if (subChild instanceof InstanceSpecification) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
