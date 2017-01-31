package BBTest;

import java.sql.Date;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import weather.common.data.forecasterlesson.ForecasterLesson;
import weather.common.data.forecasterlesson.InstructorResponse;
import weather.common.data.forecasterlesson.Question;
import weather.common.dbms.DBMSForecasterLessonManager;
import weather.common.dbms.DBMSForecasterQuestionManager;
import weather.common.dbms.DBMSInstructorResponseManager;
import weather.common.dbms.DBMSSystemManager;
import weather.common.dbms.mysql.MySQLImpl;
import weather.common.utilities.CalendarFormatter;
import weather.common.utilities.Debug;

public class TestInstructorResponseManager {

    public static void main(String[] args) {
         //Make Date objects, tested several.
        GregorianCalendar cal = new GregorianCalendar();
        cal.set(2015, GregorianCalendar.MAY, 27, 0, 0, 0);
        cal.set(GregorianCalendar.MILLISECOND, 0);
        Date date = new Date(cal.getTimeInMillis());
        GregorianCalendar cal2 = new GregorianCalendar();
        cal2.set(2015, GregorianCalendar.MAY, 27, 0, 0, 0);
        cal2.set(GregorianCalendar.MILLISECOND, 0);
        Date date2 = new Date(cal2.getTimeInMillis());
        
        //Store station code, tested several.
        String code = "KAVP";
        
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
        
        //Get guestion from the database.
        DBMSForecasterQuestionManager qm = dbms.getForecasterQuestionManager();
        DBMSForecasterLessonManager lm = dbms.getForecasterLessonManager();
        //Assume the only lesson is the testing lesson.
        ForecasterLesson lesson = lm.getAllForecasterLessons().get(0);
        //Tested several questions.
        Question question = qm.getQuestions(lesson).get(1);
        
        //Insert new instructor response.
        InstructorResponse newResponse = new InstructorResponse(null,
                question.getQuestionID(), "SW", date, code);
        Debug.println("New object before insert:\n" + newResponse);
        DBMSInstructorResponseManager irm = dbms.getInstructorResponseManager();
        newResponse = irm.insertResponse(newResponse);
        Debug.println("New object after insert:\n" + newResponse);
        
        //Get test data from database.
        ArrayList<InstructorResponse> responses = irm
                .getResponsesByQuestionAndDateAndStation(question, date, code);
        
        //Show Results.
        Debug.println("Query Database for " + CalendarFormatter.format(cal) + ":");
        Debug.println("Array Size: " + responses.size());
        for (InstructorResponse response : responses) {
            Debug.println(response);
        }
        
        //Show another date.
        responses = irm
                .getResponsesByQuestionAndDateAndStation(question, date2, code);
        
        //Show Results.
        Debug.println("Query Database for " + CalendarFormatter.format(cal2) + ":");
        Debug.println("Array Size: " + responses.size());
        for (InstructorResponse response : responses) {
            Debug.println(response);
        }
        
        //Delete instructor responses.
        int counter = 0;
        for (InstructorResponse response : responses) {
            Debug.println("Object before delete:\n" + response);
            response = irm.deleteResponse(response);
            Debug.println("Object after delete:\n" + response);
            Debug.println("List after delete #" + ++counter + ":");
            ArrayList<InstructorResponse> responses2 = irm
                    .getResponsesByQuestionAndDateAndStation(question, date2,
                    code);
            Debug.println("Query Database for " + CalendarFormatter.format(cal2) + ":");
            Debug.println("Array Size: " + responses2.size());
            for (InstructorResponse aResponse : responses2) {
                Debug.println(aResponse);
            }
        }
    }    
}
