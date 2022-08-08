import java.io.IOException;
import java.io.StringReader;

/**
 * String translation.
 *
 * @author
 */
public class Translate {
  static String translate(String S, String from, String to) {
    char[] buffer = new char[S.length()];
    try {
      StringReader temp = new StringReader(S);
      TrReader result = new TrReader(temp, from, to);
      result.read(buffer);
      return new String(buffer);
    } catch (IOException e) {
      return null;
    }
  }
}
