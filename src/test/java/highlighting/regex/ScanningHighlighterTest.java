package highlighting.regex;

import static org.junit.jupiter.api.Assertions.*;

import highlighting.core.HighlightRegion;
import highlighting.presets.MiniJavaColours;
import java.awt.Color;
import java.util.List;
import java.util.regex.Pattern;
import org.junit.jupiter.api.Test;

class ScanningHighlighterTest {

  private final ScanningHighlighter highlighter = new ScanningHighlighter();

  @Test
  void laengstesMatchGewinntBeiGleicherStartposition() {
    Color kurz = Color.RED;
    Color lang = Color.BLUE;
    ScanningHighlighter eigenerHighlighter =
        new ScanningHighlighter(
            List.of(
                Token.of(Pattern.compile("ab"), kurz), Token.of(Pattern.compile("abcd"), lang)));

    List<HighlightRegion> result = eigenerHighlighter.collectMatches("abcd");

    assertEquals(List.of(new HighlightRegion(0, 4, lang)), result);
  }

  @Test
  void gleichstandWaehltFrueheresTokenAusMiniJavaTokens() {
    List<HighlightRegion> result = highlighter.collectMatches("/** doc */");

    assertEquals(
        List.of(new HighlightRegion(0, 10, MiniJavaColours.JAVADOC_COMMENT_COLOUR)), result);
  }

  @Test
  void stellenOhneMatchBlockierenScannerNicht() {
    Color farbe = Color.RED;
    ScanningHighlighter eigenerHighlighter =
        new ScanningHighlighter(List.of(Token.of(Pattern.compile("ab"), farbe)));

    List<HighlightRegion> result = eigenerHighlighter.collectMatches("###ab");

    assertEquals(List.of(new HighlightRegion(3, 5, farbe)), result);
  }

  @Test
  void kombinationAusTreffernUndNichtTreffernImSelbenText() {
    List<HighlightRegion> result = highlighter.collectMatches("public ### @Override ??? class");

    assertEquals(
        List.of(
            new HighlightRegion(0, 6, MiniJavaColours.KEYWORD_COLOUR),
            new HighlightRegion(11, 20, MiniJavaColours.ANNOTATION_COLOUR),
            new HighlightRegion(25, 30, MiniJavaColours.KEYWORD_COLOUR)),
        result);
  }

  @Test
  void leerstringSollLeereListeGeben() {
    List<HighlightRegion> result = highlighter.collectMatches("");

    assertTrue(result.isEmpty());
  }

  @Test
  void aufeinanderfolgendeMatchesSindBeideEnthalten() {
    List<HighlightRegion> result = highlighter.collectMatches("public class");

    assertEquals(
        List.of(
            new HighlightRegion(0, 6, MiniJavaColours.KEYWORD_COLOUR),
            new HighlightRegion(7, 12, MiniJavaColours.KEYWORD_COLOUR)),
        result);
  }
}
