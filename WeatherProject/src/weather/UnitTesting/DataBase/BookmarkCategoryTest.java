package weather.UnitTesting.DataBase;

import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import weather.common.data.bookmark.BookmarkDuration;
import weather.common.data.bookmark.BookmarkCategory;
import weather.common.data.bookmark.CategoryViewRights;
import weather.common.dbms.mysql.MySQLImpl;
import weather.common.utilities.Debug;

/**
 * @author Joseph Horro - jjh35893
 * creation 2011-3-4
 *
 * 2011-3-4     - Tested MySQL implementation.
 *                All tested successful (after some tweaking) -Joe H.
 */
public class BookmarkCategoryTest {

    public static void main(String[] args) {
        int rowsAffected = 0;
        boolean isSuccessful = false;
        Vector<BookmarkCategory> list;
        // test bookmarkCategory
//        BookmarkCategory(String name, int createdBy,
//            CategoryViewRights viewRights,
//            BookmarkDuration alternative, String notes) 479 = jjh35893
        BookmarkCategory bmc =
                new BookmarkCategory("test bmc",
                479, CategoryViewRights.instructor_only,
                BookmarkDuration.instance,
                "This is a test note");
        BookmarkCategory bmcTest;

        try {
            MySQLImpl dbms =  MySQLImpl.getMySQLDMBSSystem();

            // test area

            //add
            //  isSuccessful = dbms.getBookmarkCategoriesManager().add(bmc);

            // obtain all
//            list = dbms.getBookmarkCategoriesManager().obtainAll();
//            if(list.size() > 0) isSuccessful = true;
//            rowsAffected = list.size();
//            for(BookmarkCategory b : list)
//                Debug.println("name: " + b.getName());

            // searches
            // search by bookmark number
//            bmcTest = dbms.getBookmarkCategoriesManager().searchByBookmarkCategoryNumber(34);
//            if(bmcTest != null) {
//                Debug.println(bmcTest.getName());
//                isSuccessful = true;
//            }

            // name
//            bmcTest = dbms.getBookmarkCategoriesManager().searchByName("test bmc");
//            if(bmcTest != null) {
//                Debug.println(bmcTest.getName());
//                isSuccessful = true;
//            }

            // deletion
            // remove all by user
            // rowsAffected = dbms.getBookmarkCategoriesManager().removeManyByUser(479);

            // remove one
//            bmc.setBookmarkCategoryNumber(38);
//            isSuccessful = dbms.getBookmarkCategoriesManager().removeOne(bmc);

             //isSuccessful = dbms.getBookmarkCategoriesManager().removeOne("test bmc");


            // update
//            bmc.setBookmarkCategoryNumber(40);
//            bmc.setName("renamed!");
//            isSuccessful = dbms.getBookmarkCategoriesManager().update(bmc);


            // results
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
