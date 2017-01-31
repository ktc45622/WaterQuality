package BBTest;

import java.sql.Date;
import java.util.GregorianCalendar;
import weather.common.data.forecasterlesson.ForecasterLesson;
import weather.common.data.forecasterlesson.MissingWebGradingDataRecord;
import weather.common.dbms.DBMSForecasterLessonManager;
import weather.common.dbms.DBMSMissingDataRecordManager;
import weather.common.dbms.DBMSSystemManager;
import weather.common.dbms.mysql.MySQLImpl;
import weather.common.utilities.Debug;

public class TestMissingRowManager {

    public static void main(String[] args) {
         //Make Date objects.
        GregorianCalendar cal1 = new GregorianCalendar();
        cal1.set(2015, GregorianCalendar.JULY, 5, 0, 0, 0);
        cal1.set(GregorianCalendar.MILLISECOND, 0);
        Date date1 = new Date(cal1.getTimeInMillis());
        GregorianCalendar cal2 = new GregorianCalendar();
        cal2.set(2015, GregorianCalendar.JULY, 6, 0, 0, 0);
        cal2.set(GregorianCalendar.MILLISECOND, 0);
        Date date2 = new Date(cal2.getTimeInMillis());
        
        //Store station codes for testing.
        String code1 = "KAVP";
        String code2 = "KCNO";
        
        //Setup dbms.
        DBMSSystemManager dbms = null;

        try {
            dbms = MySQLImpl.getMySQLDMBSSystem();
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
            Debug.println("Could not get a database instance");
            e.toString();
            Debug.println("The program will now terminate");
            System.exit(1);
        }
        
        //Check dbms.
        if (dbms == null) {
            Debug.println("NO DBMS!");
        } else {
            Debug.println("Got DBMS.");
        }
        
        Debug.println();
        
        //Get lessons from the database (two required for this testing.)
        DBMSForecasterLessonManager lm = dbms.getForecasterLessonManager();
        ForecasterLesson lesson1 = lm.getAllForecasterLessons().get(0);
        ForecasterLesson lesson2 = lm.getAllForecasterLessons().get(1);
        
        //Get missing record manager form database.
        DBMSMissingDataRecordManager mdrm = dbms.getMissingDataRecordManager();
        
        //Counter
        int i;
        
        //Record 1
        //Make record not in database on first run.
        MissingWebGradingDataRecord record1 = mdrm.
                getRecordByLessonAndDateAndStation(lesson1, date1, code1);
        Debug.println("Record 1:");
        Debug.println(record1);
        for (i = 0; i < 2; i++) {
            if (record1.hasDatabaseId()) {
                Debug.println("Recond 1 in Database.");
            } else {
                Debug.println("Record 1 NOT in Database. Inserting...");
                record1 = mdrm.insertRecord(record1);
                Debug.println(record1);
            }
        }
        
        Debug.println();
        
        //Record 2
        //Make record not in database on first run.
        MissingWebGradingDataRecord record2 = mdrm.
                getRecordByLessonAndDateAndStation(lesson1, date1, code2);
        Debug.println("Record 2:");
        Debug.println(record2);
        for (i = 0; i < 2; i++) {
            if (record2.hasDatabaseId()) {
                Debug.println("Recond 2 in Database.");
            } else {
                Debug.println("Record 2 NOT in Database. Inserting...");
                record2 = mdrm.insertRecord(record2);
                Debug.println(record2);
            }
        }
        
        Debug.println();
        
        //Record 3
        //Make record not in database on first run.
        MissingWebGradingDataRecord record3 = mdrm.
                getRecordByLessonAndDateAndStation(lesson1, date2, code1);
        Debug.println("Record 3:");
        Debug.println(record3);
        for (i = 0; i < 2; i++) {
            if (record3.hasDatabaseId()) {
                Debug.println("Recond 3 in Database.");
            } else {
                Debug.println("Record 3 NOT in Database. Inserting...");
                record3 = mdrm.insertRecord(record3);
                Debug.println(record3);
            }
        }
        
        Debug.println();
        
        //Record 4
        //Make record not in database on first run.
        MissingWebGradingDataRecord record4 = mdrm.
                getRecordByLessonAndDateAndStation(lesson1, date2, code2);
        Debug.println("Record 4:");
        Debug.println(record4);
        for (i = 0; i < 2; i++) {
            if (record4.hasDatabaseId()) {
                Debug.println("Recond 4 in Database.");
            } else {
                Debug.println("Record 4 NOT in Database. Inserting...");
                record4 = mdrm.insertRecord(record4);
                Debug.println(record4);
            }
        }
        
        Debug.println();
        
        //Record 5
        //Make record not in database on first run.
        MissingWebGradingDataRecord record5 = mdrm.
                getRecordByLessonAndDateAndStation(lesson2, date1, code1);
        Debug.println("Record 5:");
        Debug.println(record5);
        for (i = 0; i < 2; i++) {
            if (record5.hasDatabaseId()) {
                Debug.println("Recond 5 in Database.");
            } else {
                Debug.println("Record 5 NOT in Database. Inserting...");
                record5 = mdrm.insertRecord(record5);
                Debug.println(record5);
            }
        }
        
        Debug.println();
        
        //Record 6
        //Make record not in database on first run.
        MissingWebGradingDataRecord record6 = mdrm.
                getRecordByLessonAndDateAndStation(lesson2, date1, code2);
        Debug.println("Record 6:");
        Debug.println(record6);
        for (i = 0; i < 2; i++) {
            if (record6.hasDatabaseId()) {
                Debug.println("Recond 6 in Database.");
            } else {
                Debug.println("Record 6 NOT in Database. Inserting...");
                record6 = mdrm.insertRecord(record6);
                Debug.println(record6);
            }
        }
        
        Debug.println();
        
        //Record 7
        //Make record not in database on first run.
        MissingWebGradingDataRecord record7 = mdrm.
                getRecordByLessonAndDateAndStation(lesson2, date2, code1);
        Debug.println("Record 7:");
        Debug.println(record7);
        for (i = 0; i < 2; i++) {
            if (record7.hasDatabaseId()) {
                Debug.println("Recond 7 in Database.");
            } else {
                Debug.println("Record 7 NOT in Database. Inserting...");
                record7 = mdrm.insertRecord(record7);
                Debug.println(record7);
            }
        }
        
        Debug.println();
        
        //Record 8
        //Make record not in database on first run.
        MissingWebGradingDataRecord record8 = mdrm.
                getRecordByLessonAndDateAndStation(lesson2, date2, code2);
        Debug.println("Record 8:");
        Debug.println(record8);
        for (i = 0; i < 2; i++) {
            if (record8.hasDatabaseId()) {
                Debug.println("Recond 8 in Database.");
            } else {
                Debug.println("Record 8 NOT in Database. Inserting...");
                record8 = mdrm.insertRecord(record8);
                Debug.println(record8);
            }
        }
        
        Debug.println();
        
        //Update some records.
        Debug.println("Updating records..");
        record1.setIsInstructorDataSet(true);
        record1.setWasEmailSent(true);
        mdrm.updateRecord(record1);
        record2.setIsInstructorDataSet(true);
        record2.setWasEmailSent(true);
        mdrm.updateRecord(record2);
        record3.setIsInstructorDataSet(true);
        mdrm.updateRecord(record3);
        record4.setWasEmailSent(true);
        mdrm.updateRecord(record4);
        Debug.println("Updating dome.");
        
        Debug.println();
        
         //Show records by lesson.
        Debug.println("Records for lesson 1:");
        for(MissingWebGradingDataRecord record : mdrm.
                getAllRecordsForLesson(lesson1)) {
            Debug.println(record);
        }
        
        Debug.println();
        
        Debug.println("Records for lesson 2:");
        for(MissingWebGradingDataRecord record : mdrm.
                getAllRecordsForLesson(lesson2)) {
            Debug.println(record);
        }
    }
}
