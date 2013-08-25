/*
 * Copyright [2013] [Nazmul Idris]
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package zen.utlis;


/**
 * Frequently used utils for the framework.
 *
 * @author Nazmul Idris
 * @version 1.0
 * @since Apr 22, 2010, 6:53:22 PM
 */
public class SharedUtils {

//XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
// array utils
//XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX

public static boolean arrayContainsSomething(Object ray[]) {
  if (ray == null) { return false; }
  if (ray.length == 0) { return false; }
  return true;
}

/** @return 0 means null or empty; otherwise length is returned */
public static int arrayLength(Object ray[]) {
  if (ray == null) { return 0; }
  else { return ray.length; }
}

//XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
// conversions
//XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX

/** converts milliseconds to sec */
public static float msToSec(long ms) {
  int ms_in_sec = 1000;
  return (float) ms / (float) ms_in_sec;
}

/** converts milliseconds to minutes */
public static float msToMin(long ms) {
  int ms_in_min = 1 * 60 * 1000;
  return (float) ms / (float) ms_in_min;
}

/** converts milliseconds to hours */
public static float msToHr(long ms) {
  float mins = msToMin(ms);
  return mins / 60f;
}

// distance mile -> km (everything is done in float, then converted to whatever)

public static float miToKm(float mi) {
  return mi * 1.609344f;
}

public static double miToKm(String mi) {
  return Double.parseDouble(mi) * 1.609344d;
}

public static double miToKm(double mi) {
  return mi * 1.609344d;
}

// distance km -> mile (everything is done in float, then converted to whatever)

public static float kmToMi(float km) {
  return km * 0.621371192f;
}

public static double kmToMi(double km) {
  return km * 0.621371192d;
}

public static double kmToMi(String km) {
  return Double.parseDouble(km) * 0.621371192d;
}

// underlying distance converters

/** converts meters to miles */
public static float meterToMile(float meters) {
  return 0.000621371192f * meters;
}

/** converts miles to meters */
public static float mileToMeter(float mile) {
  return mile * 1609.344f;
}

/** converts meters to feet */
public static float meterToFoot(float meters) {
  return 3.2808399f * meters;
}

/** converts feet to meters */
public static float footToMeter(float foot) {
  return 0.3048f * foot;
}

public static long hrToMs(long hr) {
  return hr * 60 * 60 * 1000;
}

public static long minToMs(long min) {
  return min * 60 * 1000;
}

public static long secToMs(long sec) {
  return sec * 1000;
}


/** searches for element in the ray. if element is not found, returns false */
public static boolean contains(String element, String[] ray) {

  if (SharedUtils.isNullOrEmpty(element) || SharedUtils.isNull(ray)) { return false; }

  for (int i = 0; i < ray.length; i++) {
    if (element.equals(ray[i])) { return true; }
  }

  return false;

}

/** searches for element in the ray. if element is not found, returns false */
public static int containsCount(String element, String[] ray) {

  if (SharedUtils.isNullOrEmpty(element) || SharedUtils.isNull(ray)) { return 0; }

  int count = 0;

  for (int i = 0; i < ray.length; i++) {
    if (element.equals(ray[i])) { count++; }
  }

  return count;

}


/*
public static void main(String[] args) {
  System.out.println(prettyPrintDouble(15.344d));
  System.out.println(prettyPrintDouble(12d));
  System.out.println(prettyPrintDouble(12.2d));
  System.out.println(prettyPrintDouble(12.24d));
  System.out.println(prettyPrintDouble(12.24028d));
}
*/

public static String prettyPrintDouble(double input) {

  int beforeDecimal;
  int afterDecimal;
  StringBuffer output;
  StringBuffer outputAfterDecimal;

  output = new StringBuffer();
  beforeDecimal = (int) input;
  output.append(beforeDecimal);

  afterDecimal = (int) ((input - (beforeDecimal * 1d)) * 100d);
  if (afterDecimal == 0) { return output.toString(); }

  output.append('.');
  outputAfterDecimal = new StringBuffer(String.valueOf(afterDecimal));
  while (outputAfterDecimal.length() < 2) {
    outputAfterDecimal.append('0');
  }
  output.append(outputAfterDecimal);
  return output.toString();

}


//XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
// validation/assertion methods
//XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX

public static void assertNotTrue(boolean obj, String errMsg) {
  if (obj == true) {
    throw new IllegalArgumentException(errMsg);
  }
}

public static void assertNotFalse(boolean obj, String errMsg) {
  if (obj == false) {
    throw new IllegalArgumentException(errMsg);
  }
}

public static void assertNotNull(Object obj, String errMsg) throws IllegalArgumentException {
  if (obj == null) {
    throw new IllegalArgumentException(errMsg);
  }
}

public static boolean notNull(Object obj) {
  if (obj != null) { return true; }
  else { return false; }
}

public static boolean isNull(Object obj) {
  return !notNull(obj);
}

public static void assertNotNullOrEmpty(String str, String errMsg) throws IllegalArgumentException {
  if (str == null) {
    throw new IllegalArgumentException(errMsg);
  }
  if (str.equals("")) {
    throw new IllegalArgumentException(errMsg);
  }
}

public static boolean isNullOrEmpty(String obj) {
  if (obj == null) { return true; }
  if (obj.equals("")) { return true; }
  return false;
}

public static boolean isNullOrEmptyAfterTrim(String obj) {
  if (obj == null) { return true; }
  if (obj.trim().equals("")) { return true; }
  return false;
}

public static boolean notNullOrEmpty(String obj) {
  if (obj == null) { return false; }

  if (obj.equals("")) { return false; }
  else { return true; }
}

/** converts the String[] to a string, where each element of the String[] is surrounded with quotes */
public static String arrayToString(Object[] keywords, String delim) {

  if (keywords == null) { return ""; }

  StringBuffer buf = new StringBuffer();

  boolean isLastElement;
  int size = keywords.length;

  for (int i = 0; i < size; i++) {
    buf.append("\"").append(keywords[i]).append("\"");
    isLastElement = (i == (size - 1));
    if (!isLastElement) { buf.append(delim); }
  }

  return buf.toString();
}

/** converts the String[] to a string, where each element of the String[] is NOT surrounded with quotes */
public static String arrayToStringWithoutQuotes(Object[] keywords, String delim) {

  StringBuffer buf = new StringBuffer();

  boolean isLastElement;
  int size = keywords.length;

  for (int i = 0; i < size; i++) {
    buf.append(keywords[i]);
    isLastElement = (i == (size - 1));
    if (!isLastElement) { buf.append(delim); }
  }

  return buf.toString();
}


public static boolean stringContainsSomething(String obj) {
  if (obj == null) { return false; }

  if (obj.equals("")) { return false; }
  else { return true; }
}

/**
 * Identifies the substrings in a given string that are delimited
 * by one or more characters specified in an array, and then
 * places the substrings into a String array.
 */
public static String[] split(String strString, String strDelimiter) {
  String[] strArray;
  int iOccurrences = 0;
  int iIndexOfInnerString = 0;
  int iIndexOfDelimiter = 0;
  int iCounter = 0;

  //Check for null input strings.
  if (strString == null) {
    throw new IllegalArgumentException("Input string cannot be null.");
  }
  //Check for null or empty delimiter strings.
  if (strDelimiter.length() <= 0 || strDelimiter == null) {
    throw new IllegalArgumentException("Delimeter cannot be null or empty.");
  }

  //strString must be in this format: (without {} )
  //"{str[0]}{delimiter}str[1]}{delimiter} ...
  // {str[n-1]}{delimiter}{str[n]}{delimiter}"

  //If strString begins with delimiter then remove it in order
  //to comply with the desired format.

  if (strString.startsWith(strDelimiter)) {
    strString = strString.substring(strDelimiter.length());
  }

  //If strString does not end with the delimiter then add it
  //to the string in order to comply with the desired format.
  if (!strString.endsWith(strDelimiter)) {
    strString += strDelimiter;
  }

  //Count occurrences of the delimiter in the string.
  //Occurrences should be the same amount of inner strings.
  while ((iIndexOfDelimiter = strString.indexOf(strDelimiter,
                                                iIndexOfInnerString)) != -1)
  {
    iOccurrences += 1;
    iIndexOfInnerString = iIndexOfDelimiter +
                          strDelimiter.length();
  }

  //Declare the array with the correct size.
  strArray = new String[iOccurrences];

  //Reset the indices.
  iIndexOfInnerString = 0;
  iIndexOfDelimiter = 0;

  //Walk across the string again and this time add the
  //strings to the array.
  while ((iIndexOfDelimiter = strString.indexOf(strDelimiter,
                                                iIndexOfInnerString)) != -1)
  {

    //Add string to array.
    strArray[iCounter] = strString.substring(iIndexOfInnerString, iIndexOfDelimiter);

    //Increment the index to the next character after
    //the next delimiter.
    iIndexOfInnerString = iIndexOfDelimiter +
                          strDelimiter.length();

    //Inc the counter.
    iCounter += 1;
  }

  return strArray;
}

}//end class SharedUtils
