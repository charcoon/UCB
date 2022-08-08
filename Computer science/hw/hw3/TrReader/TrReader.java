import java.io.IOException;
import java.io.Reader;

/**
 * Translating Reader: a stream that is a translation of an existing reader.
 *
 * @author
 */
public class TrReader extends Reader {
  private Reader source;
  private String from;
  private String to;


  public TrReader(Reader str, String from, String to) {
    assert from != null;
    assert to != null;
    assert from.length() == to.length();
    this.source = str;
    this.from = from;
    this.to = to;
  }

  @Override
  public void close() throws IOException {
    this.source.close();

  }

  @Override
  public int read(char[] cbuf, int off, int len) throws IOException {
    int readStatus = this.source.read(cbuf, off, len);
    for (int i = off; i < off+len; i++) {
      cbuf[i] = this.map(cbuf[i]);
    }
    return Math.min(len, readStatus);
  }

  /**
   * Translates a given character.
   * 
   * @param origin
   * @return
   */
  private char map(char origin) {
    int fromIndex = this.from.indexOf(origin);
    if (fromIndex == -1) {
      return origin;
    } else {
      return this.to.charAt(fromIndex);
    }

  }
}