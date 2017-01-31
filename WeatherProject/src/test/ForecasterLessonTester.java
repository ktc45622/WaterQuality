/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package test;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Scanner;
import weather.common.data.Course;
import weather.common.data.User;
import weather.common.data.forecasterlesson.Attempt;
import weather.common.data.forecasterlesson.ForecasterLesson;
import weather.common.data.forecasterlesson.Instructions;
import weather.common.dbms.mysql.MySQLImpl;
import weather.common.utilities.WeatherException;

/**
 *
 * @author jbenscoter
 */
public class ForecasterLessonTester {

    static MySQLImpl manager;
    static Scanner scanner;
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        boolean quit = false;
        scanner = new Scanner(System.in);
        try{
        manager = MySQLImpl.getMySQLDMBSSystem();
        String selection;
        while(!quit)
        {
            System.out.println("1) select all lessons");
            System.out.println("2) get lesson by id");
            System.out.println("3) add new lessson");
            System.out.println("4) delete a lesson");
            System.out.println("5) update a lesson");
            System.out.println("6) select lesson by course");
            System.out.println("7) select attempts by lesson and user");
            System.out.println("e) exit");
            selection = scanner.nextLine();
            
            switch(selection)
            {
                case "1":
                    getAllLessons();
                    break;
                case "2":
                    getLessonById();
                    break;
                case "3":
                    insertLesson();
                    break;
                case "4":
                    removeLesson();
                    break;
                case "5":
                    updateLesson();
                    break;
                case "6":
                    getAllLessonsByCourse();
                    break;
                case "7":
                    getAttemptsByLessonAndUser();
                    break;
                case "e":
                    quit = true;
                    break;
            }
                    
        }
        }catch(Exception ex)
        {
            System.out.println(ex.getMessage());
        }
    }
    
    public static void getAllLessons()
    {
        ArrayList<ForecasterLesson> lessons = manager
                    .getForecasterLessonManager().getAllForecasterLessons();
        System.out.println("Select all lessons: ");
        System.out.println("------");
        for(int i = 0; i < lessons.size(); i++)
        {
            System.out.println(lessons.get(i).toString());
            System.out.println(lessons.get(i).getCourse().toString());
            System.out.println(lessons.get(i).getCourse().getInstructor()
                    .toString());
            System.out.println("------");
        }
    }
    
    public static void getAllLessonsByCourse()
    {
        System.out.print("Enter the course id: ");
        int courseId = scanner.nextInt();
        System.out.println("\nSelect lesson with course id: " + courseId);
        ArrayList<ForecasterLesson> lessons = manager
                    .getForecasterLessonManager().getForecasterLessonsByCourse(courseId);
        System.out.println("Select lessons: ");
        System.out.println("------");
        
        for(int i = 0; i < lessons.size(); i++)
        {
            System.out.println(lessons.get(i).toString());
            System.out.println(lessons.get(i).getCourse().toString());
            System.out.println(lessons.get(i).getCourse().getInstructor()
                    .toString());
            System.out.println("------");
        }
    }
    
    public static void getLessonById()
    {
        System.out.print("Enter the lesson id: ");
        String lessonId = scanner.nextLine();
        System.out.println("\nSelect lesson with id: " + lessonId);
            
        ForecasterLesson fl = manager.getForecasterLessonManager()
                .getForecasterLesson(lessonId);
        System.out.println("------");
        System.out.println(fl.toString());
        System.out.println(fl.getCourse().toString());
        System.out.println(fl.getCourse().getInstructor()
                    .toString());
        System.out.println("------");
    }
    
    public static void insertLesson()
    {
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:SS");
        System.out.print("Enter lesson name: ");
        String lessonName = scanner.nextLine();
        System.out.print("\nEnter lesson start date: ");
        String startDate = scanner.nextLine();
        System.out.print("\nEnter lesson due date: ");
        String dueDate = scanner.nextLine();
        System.out.print("\nEnter maximum tries: ");
        int maxTries = Integer.parseInt(scanner.nextLine());
        System.out.print("\nEnter student edit type: ");
        String studentEditType = scanner.nextLine();
        boolean active = true;
        boolean useArchived = false;
        Instructions i = null;
        System.out.print("\nEnter the course number: ");
        int courseNumber = Integer.parseInt(scanner.nextLine());
        Course course = manager.getCourseManager().obtainCourse(courseNumber);
        try
        {
        ForecasterLesson fl = new ForecasterLesson("-1", lessonName, studentEditType, 
                    null, formatter.parse(startDate), 
                    formatter.parse(dueDate), maxTries,
                    useArchived, null, 
                    i, course, null,null);
            
            try
            {
                fl = manager.getForecasterLessonManager().insertForecasterLesson(fl);
                
                System.out.println("\nNew lesson inserted: ");
                System.out.println("------");
                System.out.println(fl.toString());
                System.out.println(fl.getCourse().toString());
                System.out.println(fl.getCourse().getInstructor()
                        .toString());
                System.out.println("------");
            }
            catch(WeatherException we)
            {
                
            }
        }
        catch(ParseException pe)
        {
            System.out.println("You screwed up typing in some stuff. Try again.");
        }
    }
    
    public static void removeLesson()
    {
        ArrayList<ForecasterLesson> lessons = manager
                    .getForecasterLessonManager().getAllForecasterLessons();
        System.out.println("Select a lesson to remove: ");
        System.out.println("------");
        for(int i = 0; i < lessons.size(); i++)
        {
            System.out.println((i + 1) +") " + lessons.get(i).getLessonName());
        }
        System.out.println("------");
        System.out.print("\nPlease enter the number of the lesson to delete: ");
      
        int index = Integer.parseInt(scanner.nextLine()) - 1;
        
        ForecasterLesson fl = lessons.get(index);
        System.out.println("\nRemoving lesson: " + fl.getLessonName());
            fl = manager.getForecasterLessonManager().removeForecasterLesson(fl);
            
            if(fl.getLessonID().equals("-1"))
            {
                System.out.println("Lesson " + fl.getLessonID() + " - " 
                        + fl.getLessonName() + " removed.");
            }
            else
            {
                System.out.println("Lesson " + fl.getLessonID() + " - " 
                        + fl.getLessonName() + " was not removed.");
            }
    }
    
    public static void updateLesson()
    {
        ArrayList<ForecasterLesson> lessons = manager
                    .getForecasterLessonManager().getAllForecasterLessons();
        System.out.println("Select a lesson to remove: ");
        System.out.println("------");
        for(int i = 0; i < lessons.size(); i++)
        {
            System.out.println((i + 1) +") " + lessons.get(i).getLessonName());
        }
        System.out.println("------");
        System.out.print("\nPlease enter the number of the lesson to update: ");

        int index = Integer.parseInt(scanner.nextLine()) - 1;
        
        ForecasterLesson fl = lessons.get(index);
        
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:SS");
        
        System.out.print("Enter lesson name (current " + fl.getLessonName() 
                + "): ");
        fl.setLessonName(scanner.nextLine());
        System.out.print("\nEnter lesson start date (current " 
                + fl.getLessonStartDate() + "): ");
        try{
        fl.setLessonStartDate(formatter.parse(scanner.nextLine()));
        System.out.print("\nEnter lesson due date (current " 
                + fl.getLessonEndDate() + "): ");
        fl.setLessonEndDate(formatter.parse(scanner.nextLine()));
        System.out.print("\nEnter maximum tries (current " 
                + fl.getMaximumTries() + "): ");
        fl.setMaximumTries(Integer.parseInt(scanner.nextLine()));
        System.out.print("\nEnter student edit type (current " 
                + fl.getStudentEditType() + "): ");
        fl.setStudentEditType(scanner.nextLine());
        boolean active = true;
        boolean useArchived = false;
        Instructions i = null;
        System.out.print("\nEnter the course number (current " + fl.getCourse().toString() + "): ");
        int courseNumber = Integer.parseInt(scanner.nextLine());
        fl.setCourse(manager.getCourseManager().obtainCourse(courseNumber));
        
        fl.setLessonName("Updated Test Lesson");

        manager.getForecasterLessonManager().updateForecasterLesson(fl);

        System.out.println("\nExisting lesson updated: ");
        System.out.println("------");
        System.out.println(fl.toString());
        System.out.println(fl.getCourse().toString());
        System.out.println(fl.getCourse().getInstructor()
                .toString());
        System.out.println("------");
        } catch(ParseException pe)
        {
            System.out.println("Ya dun goofed.");
        }
    }
    
    public static void getAttemptsByLessonAndUser()
    {
        System.out.print("Enter the lesson id: ");
        String lessonId = scanner.nextLine();
        System.out.println("\nSelect lesson with id: " + lessonId);
            
        ForecasterLesson fl = manager.getForecasterLessonManager()
                .getForecasterLesson(lessonId);
        
        System.out.print("Enter the user id: ");
        String userId = scanner.nextLine();
        System.out.println("\nSelect user with id: " + userId);
        
        User u = manager.getUserManager().obtainUser(userId);
        
        ArrayList<Attempt> attempts = manager.getForecasterAttemptManager()
                .getAttempts(fl, u);
        
        for(int i = 0; i < attempts.size();i++)
        {
            System.out.println(attempts.get(i).toString());
        }
    }
}
