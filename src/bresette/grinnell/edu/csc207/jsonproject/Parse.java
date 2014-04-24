package bresette.grinnell.edu.csc207.jsonproject;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;



// With help from Alex Greenberg
// Used http://stackoverflow.com/questions/2915453/how-to-get-hashtable-values-as-arraylist for unparsing hashtables
// Used Sam Rebelsky's Sample JSON Parser code for unparsing arraylists


/**
 * 
 * @author Nora Bresette Buccino and Helen Dougherty
 * 
 * Citations
 * Suggestion about creating a separate JSONInput object came from Alex Greenberg
 * Used http://stackoverflow.com/questions/2915453/how-to-get-hashtable-values-as-arraylist for unparsing hashtables
 *
 */
public class Parse
{
  /**
   * A method that parses a string of JSON input and returns an object representing that JSON string
   * @param str
   *    a String of JSON input
   * @return
   *    an Object representing the JSON input
   * @pre
   *    str must be valid JSON
   * @post
   *    returns a object representing JSON input from str
   */
  public static Object parse(String str)
  {
    //Create a new JSONInput object from the given string
    JSONInput input = new JSONInput(str);
    return parse(input);
  } // Parse(String)

  /**
   * A method that parses a JSONInput object and returns an object representing that JSONInput
   * @param json
   *    a JSONInput object with a string value and int index
   * @return
   *    an Object representing the JSON input
   * @pre
   *    JSONInput.value must be valid JSON
   * @post
   *    returns java objects representing the JSON input
   */
  public static Object parse(JSONInput json)
  {
    switch (json.value.charAt(json.index))
      {
        case '[':
          {
            json.index++;
            ArrayList<Object> list = new ArrayList<Object>();
            while (json.value.charAt(json.index) != ']'
                   && json.index < (json.value.length() - 1))
              {
                list.add(parse(json));
                if (json.value.charAt(json.index) == ',')
                  json.index++;
              } // while
            json.index++;
            return list;
          } //case [

        case '{':
          {
            json.index++;
            Hashtable<String, Object> hash = new Hashtable<String, Object>();
            while (json.value.charAt(json.index) != '}')
              {
                String key;
                Object val;

                key = (String) parse(json);
                json.index++;
                val = parse(json);
                hash.put(key, val);
                if (json.value.charAt(json.index) == ',')
                  json.index++;
              } // while
            return hash;
          }//case {

        case '"':
          {
            String myString;
            json.index++;
            int start = json.index;
            while (json.index < (json.value.length() - 1)
                   && (json.value.charAt(json.index) != '"' || json.value.charAt(json.index) == '\\'))
              {
                json.index++;
              } // while
            myString = json.value.substring(start, json.index);
            json.index++;
            return myString;
          }//case "

        case '0':
        case '1':
        case '2':
        case '3':
        case '4':
        case '5':
        case '6':
        case '7':
        case '8':
        case '9':
        case '-':
        case '.':
          {
            int start = json.index;
            while (json.index < json.value.length()
                   && (Character.isDigit(json.value.charAt(json.index))
                       || json.value.charAt(json.index) == '.'
                       || json.value.charAt(json.index) == '-'
                       || json.value.charAt(json.index) == 'e' || json.value.charAt(json.index) == 'E'))
              {
                json.index++;
              } // while
            String valStr = json.value.substring(start, json.index);
            BigDecimal digit = BigDecimal.valueOf(Double.valueOf(valStr));
            return digit;
          } // all numeric cases

        case 'n':
          {
            if (json.value.substring(json.index, json.index + 4)
                          .equalsIgnoreCase("null"))
              {
                json.index = json.index + 4;
                return null;
              } // if null
            else
              return "Incorrect JSON";
          } // case n

        case 't':
          {
            if (json.value.substring(json.index, json.index + 4)
                          .equalsIgnoreCase("true"))
              {
                json.index = json.index + 4;
                return true;
              } // if true
            else
              return "Incorrect JSON";
          } // case t

        case 'f':
          {
            if (json.value.substring(json.index, json.index + 5)
                          .equalsIgnoreCase("false"))
              {
                json.index = json.index + 5;
                return false;
              } // if false
            else
              return "Incorrect JSON";
          } // case f
        default:
          return "Incorrect JSON";
      }//switch
  } // Parse(Object)

  /**
   * A method to unparse a Java object and create a string of JSON
   * @param ob
   *    a java object, can be a String, a Hashtable, an ArrayList, a BigDecimal or the literals true, false or null
   * @return
   *    a string of JSON representing the object
   * @pre
   *    ob must be one of the allowed java objects
   * @post
   *    returns a string that when parsed will return the same object
   * @throws ClassNotFoundException
   *    if the object is not one of the specified classes of objects that are allowed
   */
  public static String unparse(Object ob)
    throws ClassNotFoundException
  {
    if (ob == null)
      {
        return "null";
      } // if null
    else if (ob.getClass() == Class.forName("java.lang.String"))
      {
        return "\"" + ob + "\"";
      } // if string
    else if (ob.getClass() == Class.forName("java.math.BigDecimal"))
      {
        return ob.toString();
      } // if number
    else if (ob.getClass() == Class.forName("java.util.ArrayList"))
      { 
        //Took code from Sam's sample JSON parser
        StringBuilder result = new StringBuilder();
        boolean first = true; // Hack!
        ArrayList<Object> a = (ArrayList<Object>) ob;
        result.append("[");
        for (Object obj : a)
          {
            if (!first)
              result.append(",");
            else
              first = false;
            result.append(unparse(obj));
          } //  for
        result.append("]");
        return result.toString();
        
      }//else if ArrayList
    else if (ob.getClass() == Class.forName("java.util.Hashtable"))
      {
        StringBuilder myString = new StringBuilder();
        myString.append('{');
        Hashtable hash = (Hashtable) ob;
        ArrayList<Object> keys = Collections.list(hash.keys());
        ArrayList<Object> vals = Collections.list(hash.elements());
        int i = 0;
        while (i < vals.size() - 1)
          {
            myString.append(unparse(keys.get(i)));
            myString.append(':');
            myString.append(unparse(vals.get(i)));
            myString.append(',');
            i++;
          }//while
        myString.append(unparse(keys.get(keys.size() - 1)));
        myString.append(':');
        myString.append(unparse(vals.get(vals.size() - 1)));
        myString.append('}');
        return myString.toString();
      }//else if Hashtable
    else if (ob.getClass() == Class.forName("java.lang.Boolean"))
      {
        return ob.toString();
      }//else if Boolean
    else
      return "Not a properly formatted Object";
  } //unparse(Object)
} // class ParseObject

