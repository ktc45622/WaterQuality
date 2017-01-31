package weather.common.data;

import java.io.Serializable;

/**
 * This class is an enumeration that defines each hour of the day.
 *
 * @author Chris Mertens
 */
public enum HourlyTimeType implements Serializable {

    NA("N/A"),
    MIDNIGHT("12 AM (Midnight)"),
    ONE_AM("01 AM"),
    TWO_AM("02 AM"),
    THREE_AM("03 AM"),
    FOUR_AM("04 AM"),
    FIVE_AM("05 AM"),
    SIX_AM("06 AM"),
    SEVEN_AM("07 AM"),
    EIGHT_AM("08 AM"),
    NINE_AM("09 AM"),
    TEN_AM("10 AM"),
    ELEVEN_AM("11 AM"),
    NOON("12 PM (Noon)"),
    ONE_PM("01 PM"),
    TWO_PM("02 PM"),
    THREE_PM("03 PM"),
    FOUR_PM("04 PM"),
    FIVE_PM("05 PM"),
    SIX_PM("06 PM"),
    SEVEN_PM("07 PM"),
    EIGHT_PM("08 PM"),
    NINE_PM("09 PM"),
    TEN_PM("10 PM"),
    ELEVEN_PM("11 PM");
    private static final long serialVersionUID = 1L;
    private String stringValue;

    private HourlyTimeType(String str) {
        this.stringValue = str;
    }

    /**
     * Gets a string representation of for displaying on screen.
     *
     * NOTE: To get a string for updating the database, use the toString method.
     *
     * @return A String representation.
     */
    public String displayString() {
        return (stringValue);
    }

    public static int getMilitaryHour(HourlyTimeType hour) {
        switch (hour) {
            case MIDNIGHT:
                return 0;
            case ONE_AM:
                return 1;
            case TWO_AM:
                return 2;
            case THREE_AM:
                return 3;
            case FOUR_AM:
                return 4;
            case FIVE_AM:
                return 5;
            case SIX_AM:
                return 6;
            case SEVEN_AM:
                return 7;
            case EIGHT_AM:
                return 8;
            case NINE_AM:
                return 9;
            case TEN_AM:
                return 10;
            case ELEVEN_AM:
                return 11;
            case NOON:
                return 12;
            case ONE_PM:
                return 13;
            case TWO_PM:
                return 14;
            case THREE_PM:
                return 15;
            case FOUR_PM:
                return 16;
            case FIVE_PM:
                return 17;
            case SIX_PM:
                return 18;
            case SEVEN_PM:
                return 19;
            case EIGHT_PM:
                return 20;
            case NINE_PM:
                return 21;
            case TEN_PM:
                return 22;
            case ELEVEN_PM:
                return 23;
            default:
                return -1;
        }
    }

    /**
     * Return the HourlyTimeType with the corresponding string value.
     *
     * @param value String value of the enumeration.
     *
     * @return An enumeration with that string value.
     */
    public static HourlyTimeType getEnum(String value) {
        HourlyTimeType type = null;

        for (HourlyTimeType t : HourlyTimeType.values()) {
            if (t.stringValue.equals(value)) {
                type = t;
                break;
            }
        }

        return type;
    }
}