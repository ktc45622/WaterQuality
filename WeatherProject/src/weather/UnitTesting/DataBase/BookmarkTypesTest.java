package weather.UnitTesting.DataBase;

import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import weather.common.data.bookmark.BookmarkType;
import weather.common.data.bookmark.CategoryViewRights;
import weather.common.dbms.mysql.MySQLImpl;
import weather.common.utilities.Debug;

/**
 * @author Joseph Horro - jjh35893
 * creation 2011-3-4
 *
 * 2011-3-4     - Tested MySQL implementation.
 *
 */
public class BookmarkTypesTest {

    public static void main(String[] args) {
        int rowsAffected = 0;
        boolean isSuccessful = false;
        Vector<BookmarkType> list;

//        BookmarkType(int categoryNumber, String name, int createdBy,
//            CategoryViewRights viewRights, String notes)
        BookmarkType bmt = new BookmarkType(
                0, "joe's test type :) 3", 479,
                CategoryViewRights.instructor_only, "this is a test :)");
        BookmarkType bmtTest;


        try {
            MySQLImpl dbms = MySQLImpl.getMySQLDMBSSystem();

            // tests

            // add
            // isSuccessful = dbms.getBookmarkTypesManager().add(bmt);

            // obtain all
//            list = dbms.getBookmarkTypesManager().obtainAll();
//            if(list.size() > 0) isSuccessful = true;
//            rowsAffected = list.size();
//            for(BookmarkType b : list)
//                Debug.println("name: " + b.getName());

            // obtain all with view rights
//            list = dbms.getBookmarkTypesManager().obtainAll(CategoryViewRights.instructor_only);
//            if (list.size() > 0) {
//                isSuccessful = true;
//            }
//            rowsAffected = list.size();
//            for (BookmarkType b : list) {
//                Debug.println("name: " + b.getName());
//            }

            // obtain all with category number
//            list = dbms.getBookmarkTypesManager().obtainAll(0);
//            if (list.size() > 0) {
//                isSuccessful = true;
//            }
//            rowsAffected = list.size();
//            for (BookmarkType b : list) {
//                Debug.println("name: " + b.getName());
//            }

            // obtain all with userID
//            list = dbms.getBookmarkTypesManager().obtainAllbyUserID(479);
//            if (list.size() > 0) {
//                isSuccessful = true;
//            }
//            rowsAffected = list.size();
//            for (BookmarkType b : list) {
//                Debug.println("name: " + b.getName());
//            }

            // search
            // by pk
//            bmtTest = dbms.getBookmarkTypesManager().searchByBookmarkTypeNumber(1);
//            if(bmtTest != null) {
//                Debug.println(bmtTest.getName());
//                isSuccessful = true;
//            }

            // by name
//            bmtTest = dbms.getBookmarkTypesManager().searchByName("joe's test type :)");
//            if(bmtTest != null) {
//                Debug.println(bmtTest.getName());
//                isSuccessful = true;
//            }

            // update
//            bmt.setInstanceTypeNumber(1);
//            bmt.setName("Rename! POW!");
//            isSuccessful = dbms.getBookmarkTypesManager().update(bmt);

            // deletion
            // single by direct type
//            bmt.setInstanceTypeNumber(1);
//            isSuccessful = dbms.getBookmarkTypesManager().removeOne(bmt);

            // single by name
//            bmt.setInstanceTypeNumber(3);
//            isSuccessful = dbms.getBookmarkTypesManager().removeOne(bmt);

            // many
//            rowsAffected = dbms.getBookmarkTypesManager().removeMany(0);



            if (rowsAffected > 0) {
                isSuccessful = true;
            }

            if (isSuccessful) {
                Debug.println("successful");
                if (rowsAffected > 0) {
                    Debug.println("Rows effected: " + rowsAffected);
                }
            } else {
                Debug.println("not successful");
            }
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(BookmarkCategoryTest.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            Logger.getLogger(BookmarkCategoryTest.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(BookmarkCategoryTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
