package weather.UnitTesting.DataBase;

import weather.common.data.WebLink;
import weather.common.data.WebLink;
import weather.common.data.WebLinkType;
import weather.common.dbms.mysql.MySQLImpl;
import weather.common.utilities.Debug;

/**
 * This class is used exclusively to test webLink sql functionality.
 * @author jjh35893
 */
public class WebLinkSQLTest {
public static void main(String [] args){
        boolean isSuccessfull = false;
        try {
            MySQLImpl dbms =  MySQLImpl.getMySQLDMBSSystem();

           // WebLinkCategories wl = new WebLinkCategories("Joe's Test Cat");
           // isSuccessfull = dbms.getWebLinkManager().addWebLinkCategory(wl);

          //  isSuccessfull = dbms.getWebLinkManager().addLinkForCategory("test",
          //          "www.google.com", "Education");

            WebLink webLink = new WebLink("test updated!", "http://www.google.com",
                    WebLinkType.LINK,12);
            webLink.setLinkNumber(276);

            isSuccessfull = dbms.getWebLinkManager().updateWebLink(webLink);

            if(isSuccessfull)
            {
                System.out.println("Update Successfull");
               // System.out.println("Inserted: " +
                       // wl.getLinkCategoryNumber() + " " + wl.getLinkCategory() );
            }
        } catch (Exception e) {
            // Terrible error catching, only used in unit testing.
            Debug.println("Error in unit test.");
            Debug.println(e.toString());
        } finally {
            // all conections should be closed
        }

    } // end of main
}
