package weather.common.utilities;

import java.io.Serializable;
import java.sql.Date;
import java.util.regex.*;

/**
 * Provides validation for input data as well as parsing of this data.
 *
 * @author Bloomsburg University Software Engineering
 * @author Brian Reich (2006)
 * @author Tom Hand (2007)
 * @version Spring 2008
 */
public class DataFilter implements Serializable
{
	//	 Regex description:
	// ^ : Must match to the beginning of the string
	// [\\w_-]+ : Must find one or more legal mail account characters
	// @ : Must find the @ separator
	// (?:[\\w_-]+\\.) : Must find one or more parts of a domain name.
	// [a-zA-Z]{2,4} : Must find the end portion of the domain name.
	// $ : Must match to the end of the string
	private static Pattern email = 
		Pattern.compile("^[\\w_-]+@(?:[\\w_-]+\\.)+[a-zA-Z]{2,4}$");
	
	// private static Pattern date = 
	//	Pattern.compile("^(\\d{1,2})-(\\d{1,2})-(\\d{1,4})$");
	
	private static Pattern zip = Pattern.compile("^\\d{5}(-\\d{4})?$");
        
        /**
        * Determines if a de-serialized file is compatible with this class.
        *
        * Maintainers must change this value if and only if the new version
        * of this class is not compatible with old versions. See Sun docs
        * for <a href=http://java.sun.com/products/jdk/1.1/docs/guide
        * /serialization/spec/version.doc.html> details. </a>
        *
        * Not necessary to include in first version of the class, but
        * included here as a reminder of its importance.
        * @serial
        */
        private static final long serialVersionUID = 1L;
	
	/**
	 * Returns true if the specified string value contains a valid email
	 * address.  If the string is not a valid email address, it will
	 * return false.
	 * @param value The String value to check for an email address.
	 * @return Returns true if <em>value</em> is an email address.
	 */
	public static boolean isEmail(String value)
	{
		return (email.matcher(value).matches());
	}
	
	/**
	 * Returns true if the specified string contains a value date value.
	 * This is determines by attempting to construct a Calendar Date
	 * from the specified String.
	 * @param value the value to check for a valid date.
	 * @return true if the <em>value</em> contains a date.
	 */
	public static boolean isDate(String value)
	{
		try
		{
			return DataFilter.getDate(value) instanceof java.sql.Date;
		} catch(Exception e)
		{
			// Debug.println(e.getCause().getMessage());
			return false;
		}
	}
	
	/**
	 * Returns a java.sql.Date instance containing the date value represented
	 * by the String value specified. If the String does not contain a valid
	 * date, an exception will be thrown.
	 * @param value the value from which to parse a date.
	 * @return a java.sql.Date with the value in the specified String.
	 * @throws Exception If <em>value</em> does not contain a valid date.
	 */
	public static java.sql.Date getDate(String value) throws Exception
	{
		return Date.valueOf(value);
	}
	
	/**
	 * Returns true if the specified value is blank, or null.
	 * @param value the value to test for a blank or null value.
	 * @return true if <em>value</em> is blank, or null.
	 */
	public static boolean isBlank(String value)
	{
		return ((value == null) || value.equals(""));
	}
	
	/**
	 * Returns true if the specified value is a value ZIP Code, either
	 * five-digit or nine-digit.
	 * @param value The value to test for a valid ZIP Code.
	 * @return true if <em>value</em> is a valid ZIP Code.
	 */
	public static boolean isZipCode(String value)
	{
		return zip.matcher(value).matches();
	}
	

	/**
	 * Returns a String containing only the digits from the specified String.
	 * @param value the value to apply a digit filter to.
	 * @return <em>value</em> with all non-digits characters removed.
	 */
	public static String getDigits(String value)
	{
		// Return string starts empty.
		String result = "";
		
		// Iterate value's characters
		for(int i = 0; i < value.length(); i++)
		{
			// Is current char a digit?
			if(Character.isDigit(value.charAt(i)))
			{
				// Yes - append it.
				result += "" + value.charAt(i);
			}
		}
		return result;
	}
	
	/**
	 * Returns only the letters in a specified String.
	 * @param value the value to apply an alphabetic filter to.
	 * @return the filtered string.
	 */
	public static String getLetters(String value)
	{
		String result = "";
		
		for(int i = 0; i < value.length(); i++)
		{
			if(Character.isLetter(value.charAt(i)))
			{
				result += value.charAt(i);
			}
		}
		return result;
	}
	
	/**
	 * Main program that tests the validity of the functions in the
	 * DataFilter class.
	 * @param args main program arguments.
	 */
	public static void main(String[] args)
	{
		
		// Test isEmail
		String e1 = "breich@sun-tech.org";
		String e2 = "breich@fdsa";
		String e3 = "@fdsa";
		String e4 = "@fdsa.net";
		
		Debug.println(e1 + " is email: " + DataFilter.isEmail(e1));
		Debug.println(e2 + " is email: " + DataFilter.isEmail(e2));
		Debug.println(e3 + " is email: " + DataFilter.isEmail(e3));
		Debug.println(e4 + " is email: " + DataFilter.isEmail(e4));
		
		// Test isDate/getDate()
		String d1 = "1-2-2003";
		String d2 = "I like mashed potatoes";
		String d3 = "2-2005";
		
		Debug.println(d1 + " is a date: " + DataFilter.isDate(d1));
		Debug.println(d2 + " is a date: " + DataFilter.isDate(d2));
		Debug.println(d3 + " is a date: " + DataFilter.isDate(d3));
	}
}
