package highlighting.presets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import highlighting.core.HighlightRegion;
import highlighting.regex.Token;
import java.util.List;
import java.util.regex.Pattern;
import org.junit.jupiter.api.Test;

class MiniJavaTokensTest {

  @Test
  void stringLiteralShouldMatchSimpleString() {
    Token stringToken =
        Token.of(Pattern.compile("\"([^\"\\\\]|\\\\.)*\""), MiniJavaColours.STRING_LITERAL_COLOUR);
    String text = "String city = \"Oslo\";";
    List<HighlightRegion> regions = stringToken.test(text);
    assertEquals(1, regions.size());
    assertEquals(
        new HighlightRegion(14, 20, MiniJavaColours.STRING_LITERAL_COLOUR), regions.getFirst());
  }

  @Test
  void stringLiteralShouldMatchEmptyString() {
    Token stringToken =
        Token.of(Pattern.compile("\"([^\"\\\\]|\\\\.)*\""), MiniJavaColours.STRING_LITERAL_COLOUR);
    String text = "String blank = \"\";";
    List<HighlightRegion> regions = stringToken.test(text);
    assertEquals(1, regions.size());
    assertEquals(
        new HighlightRegion(15, 17, MiniJavaColours.STRING_LITERAL_COLOUR), regions.getFirst());
  }

  @Test
  void stringLiteralShouldMatchEscapedQuotes() {
    Token stringToken =
        Token.of(Pattern.compile("\"([^\"\\\\]|\\\\.)*\""), MiniJavaColours.STRING_LITERAL_COLOUR);
    String text = "String message = \"Moin \\\"Eva\\\"\";";
    List<HighlightRegion> regions = stringToken.test(text);
    assertEquals(1, regions.size());
    assertEquals(
        new HighlightRegion(17, 31, MiniJavaColours.STRING_LITERAL_COLOUR), regions.getFirst());
  }

  @Test
  void stringLiteralShouldNotMatchUnclosedString() {
    Token stringToken =
        Token.of(Pattern.compile("\"([^\"\\\\]|\\\\.)*\""), MiniJavaColours.STRING_LITERAL_COLOUR);
    String text = "String pending = \"Moin;";
    List<HighlightRegion> regions = stringToken.test(text);
    assertTrue(regions.isEmpty());
  }

  @Test
  void charLiteralShouldMatchSimpleCharacter() {
    Token charToken =
        Token.of(Pattern.compile("'([^'\\\\]|\\\\.)'"), MiniJavaColours.CHAR_LITERAL_COLOUR);
    String text = "char grade = 'q';";
    List<HighlightRegion> regions = charToken.test(text);
    assertEquals(1, regions.size());
    assertEquals(
        new HighlightRegion(13, 16, MiniJavaColours.CHAR_LITERAL_COLOUR), regions.getFirst());
  }

  @Test
  void charLiteralShouldMatchEscapedCharacter() {
    Token charToken =
        Token.of(Pattern.compile("'([^'\\\\]|\\\\.)'"), MiniJavaColours.CHAR_LITERAL_COLOUR);
    String text = "char tabulator = '\\t';";
    List<HighlightRegion> regions = charToken.test(text);
    assertEquals(1, regions.size());
    assertEquals(
        new HighlightRegion(17, 21, MiniJavaColours.CHAR_LITERAL_COLOUR), regions.getFirst());
  }

  @Test
  void lineCommentShouldMatchUntilLineEnd() {
    Token lineCommentToken = Token.of(Pattern.compile("//.*"), MiniJavaColours.LINE_COMMENT_COLOUR);
    String text = "int score; // retry later";
    List<HighlightRegion> regions = lineCommentToken.test(text);
    assertEquals(1, regions.size());
    assertEquals(
        new HighlightRegion(11, 25, MiniJavaColours.LINE_COMMENT_COLOUR), regions.getFirst());
  }

  @Test
  void javadocCommentShouldMatchAcrossMultipleLines() {
    Token javadocToken =
        Token.of(Pattern.compile("/\\*\\*[\\s\\S]*?\\*/"), MiniJavaColours.JAVADOC_COMMENT_COLOUR);
    String text = "/**\n * guide\n */";
    List<HighlightRegion> regions = javadocToken.test(text);
    assertEquals(1, regions.size());
    assertEquals(
        new HighlightRegion(0, 16, MiniJavaColours.JAVADOC_COMMENT_COLOUR), regions.getFirst());
  }

  @Test
  void blockCommentShouldMatchAcrossMultipleLines() {
    Token blockCommentToken =
        Token.of(Pattern.compile("/\\*[\\s\\S]*?\\*/"), MiniJavaColours.BLOCK_COMMENT_COLOUR);
    String text = "z /* alpha\nbeta */ y";
    List<HighlightRegion> regions = blockCommentToken.test(text);
    assertEquals(1, regions.size());
    assertEquals(
        new HighlightRegion(2, 18, MiniJavaColours.BLOCK_COMMENT_COLOUR), regions.getFirst());
  }

  @Test
  void keywordShouldMatchWholeWordsOnly() {
    Token keywordToken =
        Token.of(
            Pattern.compile("\\b(package|import|class|public|private|final|return|null|new)\\b"),
            MiniJavaColours.KEYWORD_COLOUR);
    String text = "private import imported";
    List<HighlightRegion> regions = keywordToken.test(text);
    assertEquals(2, regions.size());
    assertEquals(new HighlightRegion(0, 7, MiniJavaColours.KEYWORD_COLOUR), regions.getFirst());
    assertEquals(new HighlightRegion(8, 14, MiniJavaColours.KEYWORD_COLOUR), regions.get(1));
  }

  @Test
  void annotationShouldMatchAtSymbolWithLetters() {
    Token annotationToken =
        Token.of(Pattern.compile("@[A-Za-z-]+"), MiniJavaColours.ANNOTATION_COLOUR);
    String text = "@Service\n  @Entity";
    List<HighlightRegion> regions = annotationToken.test(text);
    assertEquals(2, regions.size());
    assertEquals(new HighlightRegion(0, 8, MiniJavaColours.ANNOTATION_COLOUR), regions.getFirst());
    assertEquals(new HighlightRegion(11, 18, MiniJavaColours.ANNOTATION_COLOUR), regions.get(1));
  }

  @Test
  void keywordShouldMatchAtStartMiddleAndEnd() {
    Token keywordToken =
        Token.of(
            Pattern.compile("\\b(package|import|class|public|private|final|return|null|new)\\b"),
            MiniJavaColours.KEYWORD_COLOUR);
    String text = "final Meter null";
    List<HighlightRegion> regions = keywordToken.test(text);
    assertEquals(2, regions.size());
    assertEquals(new HighlightRegion(0, 5, MiniJavaColours.KEYWORD_COLOUR), regions.getFirst());
    assertEquals(new HighlightRegion(12, 16, MiniJavaColours.KEYWORD_COLOUR), regions.get(1));
  }

  @Test
  void stringLiteralShouldMatchTextContainingLineCommentSymbols() {
    Token stringToken =
        Token.of(Pattern.compile("\"([^\"\\\\]|\\\\.)*\""), MiniJavaColours.STRING_LITERAL_COLOUR);

    String text = "String endpoint = \"file://server\";";
    List<HighlightRegion> regions = stringToken.test(text);
    assertEquals(1, regions.size());
    assertEquals(
        new HighlightRegion(18, 33, MiniJavaColours.STRING_LITERAL_COLOUR), regions.getFirst());
  }

  @Test
  void stringLiteralShouldMatchTextContainingBlockCommentSymbols() {
    Token stringToken =
        Token.of(Pattern.compile("\"([^\"\\\\]|\\\\.)*\""), MiniJavaColours.STRING_LITERAL_COLOUR);
    String text = "String pattern = \"keep /* marker */\";";
    List<HighlightRegion> regions = stringToken.test(text);
    assertEquals(1, regions.size());
    assertEquals(
        new HighlightRegion(17, 36, MiniJavaColours.STRING_LITERAL_COLOUR), regions.getFirst());
  }
}
