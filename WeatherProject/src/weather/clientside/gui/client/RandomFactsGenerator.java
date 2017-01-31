package weather.clientside.gui.client;

import java.util.Random;

/**
 * This class is designed to generate a random weather fact for the windows
 * that have not been currently implemented. It is hard-coded on purpose.
 *
 * @author Alex Funk
 * @version 2011
 */
public class RandomFactsGenerator {
    
    /**
     * Creates an array of random weather facts.
     */
    public RandomFactsGenerator(){
    }

    /**
     * Gets a random generated weather facts.
     * @return A String of a random weather facts.
     */
    public static String getFact(){
        Random r = new Random();
        int next = r.nextInt(28);
        return array[next];
    }

    //TODO: These need to be stored in a different more readable format
    private static String[] array = {"You can use pine cones to forecast the<br>"
            + "weather: The scales will close when<br>rain is on the way.",
    "Listening to the chirps of crickets can<br>give you a rough estimate of "
            + "what the<br>temperature outdoors is on the Fahrenheit<br>temperature "
            + "scale. Count the amount of chirps<br>you hear in fifteen seconds "
            + "and add 37!", "Every winter around one septillion snowflakes<br>"
            + "fall from the sky! That's a one<br>with 24 zeros following it!"
            , "The largest hail stone ever discovered<br>was found in Nebraska"
            + " and its circumference<br>was that of a soccer ball!","A light"
            + " wind is called a \"zephyr.\"A lightning bolt travels up to<br>"
            + "60,000 miles per second and can reach<br>temperatures as "
            + "high as 50,000 degrees<br>Fahrenheit. ","Texas gets about 110"
            + " tornadoes<br>each year, the most of any U.S.<br>state. ","Did you"
            + " know that hurricanes have<br>an eye in the center of its spiral"
            + "<br>that is calm and even sunny?<br>However, the arms of the "
            + "hurricane have a<br>destructive force that can decimate entire<br>"
            + "coastlines and towns. ","Hurricanes are called hurricanes<br>"
            + "if they develop in the Atlantic Ocean.<br>If they develop in the"
            + " Pacific Ocean,<br>they are called typhoons. ","The average"
            + " lifespan of a tornado<br>is less than 15 minutes.","Sir Isaac"
            + " Newton discovered the seven distinct<br>colors of the "
            + "visible spectrum.","In ten minutes, a hurricane releases more"
            + "energy than<br>all the world's nuclear weapons combined!","The"
            + " speed of a typical raindrop is 17<br>miles per hour!","Rain "
            + "contains vitamin B12.","Men are 6 times more likely to be"
            + "<br>struck by lightning than women.","It is possible to see a"
            + " rainbow at night!","It snows more in the Grand Canyon than<br>"
            + "it does in Minneapolis, Minnesota.","A rainbow can only be"
            + " seen in the morning or<br>late afternoon.","Sometimes two rainbows"
            + " will form at<br>the same time. When this"
            + " happens, there will be a normal<br>rainbow and outside it"
            + " will be a larger, more faint rainbow.<br>The second, bigger"
            + " rainbow, will also have its<br>colours in reverse.","Dry air is"
            + " heavier than wet air.","Amongst all thunderstorm types,<br>"
            + "Supercells are the most hazardous.","A fire rainbow is an "
            + "extremely rare phenomenon<br>that occurs only when the sun "
            + "is high allowing<br>its light to pass through high-altitude "
            + "cirrus<br>clouds with a high content of ice crystals","If the "
            + "total wind energy of an average<br>hurricane could be harnessed "
            + "and converted toelectricity,<br>it would supply the US for as"
            + " much as<br>three years.","A molecule of water will stay in "
            + "Earth's atmosphere<br>for an average duration of 10-12 days.",
            "Snowflakes are actually aggregates of smaller<br>snow crystals, "
            + "often containing hundreds of<br>individual crystals.","The "
            + "lifetime of a typical small<br>cumulus cloud is 10 to 15 minutes!",
            "The biggest clouds are cumulonimbus,<br>climbing up to 6 miles"
            + " high and holding up to half a<br>million tons of water!",
            "Thunderstorms can generate gusts of windthat<br>can develop"
            + " additional thunderstorms 100 miles away!","You can use pine "
            + "cones to forecast the weather:<br>The scales will close when "
            + "rain is on the<br>way."};
}
