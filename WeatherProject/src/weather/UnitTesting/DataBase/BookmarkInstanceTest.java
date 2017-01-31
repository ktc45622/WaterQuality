package weather.UnitTesting.DataBase;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Vector;
import weather.common.data.bookmark.Bookmark;
import weather.common.dbms.mysql.MySQLImpl;
import weather.common.utilities.Debug;

/**
 * @author Joseph Horro - jjh35893
 * creation 2-10-2011
 *
 * Test: 2-10-2011  - failed - throwing an SQL null exception
 * Test: 2-11-2011  - repaired, wasn't passing prepared statement the SQL string.
 *                  - at home, can't see results until I go to the lab later.
 * Test: 3-1-2011   - have working after hours of playing with making images into
 *                    byte[]'s
 * Test: 3-2-2011   - Rewrote bookmark logic to use ImageInstance, a bit easier.
 * Test: 3-3-2011   - Renamed several files/classes.
 *
 * This file is used exclusively to test the bookmarks database functionality.
 */
public class BookmarkInstanceTest {

    public static void main(String[] args) throws IOException {
        // testing adding bookmarks
//        public Bookmark(int categoryNumber, int typeNumber, String name,
//            int createdBy, AccessRights accessRights, Timestamp startTime,
//            int movieTimeLink, BookmarkRank ranking)

        Calendar calendar = Calendar.getInstance();
        Timestamp currentDateTime = new Timestamp(calendar.getTime().getTime());
        int rowsAffected = 0;
        Vector<Bookmark> list;

        /*Bookmark bm = new Bookmark(
                0, 0, "joe's test", 0,
                AccessRights.Everyone, currentDateTime, 0,
                BookmarkRank.AVERAGE);*/

//        System.out.println("rank: " + BookmarkRank.AVERAGE.toString());
//        System.out.println("rank: " + BookmarkRank.getEnum("Average"));

        // so many wasted hours
//        BufferedImage bf= ImageIO.read(new File("Icons\\BU.JPG"));
//        ByteArrayOutputStream baos = new ByteArrayOutputStream();
//        ImageIO.write(bf, "jpg", baos);
//        baos.flush();
//        byte[] imageInByte = baos.toByteArray();
//        baos.close();

        /*bm.setWeatherCameraPicture(new ImageInstance(new File("Icons\\BU.JPG")));
        bm.setWeatherMapPicture(new ImageInstance(new File("Icons\\BU.JPG")));
        bm.setWeatherStationPicture(new ImageInstance(new File("Icons\\BU.JPG")));*/

        boolean isSuccessful = false;

        try {
            MySQLImpl dbms = MySQLImpl.getMySQLDMBSSystem();

            // attempt insertion (multiple)
//            for (int i = 1; i < 10; i++) {
//                if (dbms.getBookmarkManager().add(bm)) {
//                    isSuccessful = true;
//                }
//                bm.setBookmarkNumber(-1);   // needed to insert duplicate
//                bm.setCreatedBy(i);
//            }


            // attempt deletion of one bookmark
//            bm.setBookmarkNumber(36);
//            if(dbms.getBookmarkManager().removeOne(bm))
//                isSuccessful = true;

            // attempt bookmark deletion by category number
            //rowsAffected = dbms.getBookmarkManager().removeManyByCategoryNumber(0);
            //rowsAffected = dbms.getBookmarkManager().removeManyByTypeNumber(0);
            //rowsAffected = dbms.getBookmarkManager().removeManyByUserNumber(0);
            //rowsAffected = dbms.getBookmarkManager().removeManyByRanking(BookmarkRank.AVERAGE);

            // search by created by
//            list = dbms.getBookmarkManager().searchByCreatedBy(5);
//            if(list.size() > 0) isSuccessful = true;
//            for(Bookmark b : list)
//                Debug.println("bookmarkNumber: " + b.toString());

            // search by access rights
//            list = dbms.getBookmarkManager().searchByAccessRights(AccessRights.Everyone);
//            if(list.size() > 0) isSuccessful = true;
//            for(Bookmark b : list)
//                Debug.println("bookmarkNumber: " + b.toString());

            // search by rank
//            list = dbms.getBookmarkManager().searchByRank(BookmarkRank.AVERAGE);
//            if(list.size() > 0) isSuccessful = true;
//            for(Bookmark b : list)
//                Debug.println("bookmarkNumber: " + b.toString());

            // search by categoryNumber
//            list = dbms.getBookmarkManager().searchByCategoryNumber(0);
//            if(list.size() > 0) isSuccessful = true;
//            for(Bookmark b : list)
//                Debug.println("bookmarkNumber: " + b.toString());

            // search by type number
//            list = dbms.getBookmarkManager().searchByTypeNumber(0);
//            if(list.size() > 0) isSuccessful = true;
//            for(Bookmark b : list)
//                Debug.println("bookmarkNumber: " + b.toString());

            // update
//            bm.setBookmarkNumber(31);
//            bm.setCategoryNumber(9001);
//            isSuccessful = dbms.getBookmarkManager().update(bm);

            // retrieve one by pk
//            Bookmark bm2 = dbms.getBookmarkManager().searchByBookmarkNumber(31);
//            Debug.println("bm pk-> " + bm2.getBookmarkNumber());
//            if(bm2 != null) isSuccessful = true;

            // get a list between some time range -- sill time range just for testing
//            Calendar currenttime = Calendar.getInstance();
//            list = dbms.getBookmarkManager()
//                    .getBookmarksInTimeRange(new Date(0),
//                    new Date((currenttime.getTime()).getTime()));
//            if(list.size() > 0) isSuccessful = true;
//            for(Bookmark b : list)
//                Debug.println("bookmarkNumber: " + b.toString());


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
        } catch (Exception e) {
            // Terrible error catching, only used in unit testing.
            Debug.println("Error in Bookmark unit test.");
            Debug.println(e.toString());
        } finally {
            // all conections should be closed
        }

    } // end of main
} // end of class

