/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package BZTempTestStuff;

/**
 * Tests methods to obtain and print values of enumerated types.
 * @author Brian Zaiser
 */
public class TestEnum {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        ManagerTypes managerType;// = new ManagerTypes(1);
        managerType = ManagerTypes.ManagerType1;
        System.out.println("managerType = " + managerType);
        
        managerType = ManagerTypes.ManagerType3;
        System.out.println("managerType = " + managerType);
        
        for (ManagerTypes mt : ManagerTypes.values()) {
            System.out.println("type = " + mt);
        }
        System.out.println("valueOf(ManagerType1) =  " + ManagerTypes.valueOf("ManagerType1"));
        System.out.println("valueOf(ManagerType2) =  " + ManagerTypes.valueOf("ManagerType2"));
        System.out.println("valueOf(ManagerType3) =  " + ManagerTypes.valueOf("ManagerType3"));
        ManagerTypes typeMgr = ManagerTypes.valueOf("ManagerType2");
        int typeManager = Integer.parseInt(typeMgr.toString()) * 100 + 5;
        System.out.println("typeManager = " + typeManager);
    }
    
    public enum ManagerTypes {
        ManagerType1(1),
        ManagerType2(2),
        ManagerType3(3);
        
        private int type;
        
        private ManagerTypes(int type) {
            this.type = type;
        }
        
        @Override
        public String toString() {
            return ""+type;
        }
    }
}
