package highlighting.presets;

import static org.junit.jupiter.api.Assertions.*;

import highlighting.regex.Token;
import java.util.List;
import org.junit.jupiter.api.Test;

class MiniJavaTokensTest {

  private List<Token> tokens() {
    return MiniJavaTokens.defaultTokens();
  }

  private Token stringToken() {
    return tokens().get(0);
  }

  private Token charToken() {
    return tokens().get(1);
  }

  private Token zeilenKommentarToken() {
    return tokens().get(2);
  }

  private Token javadocKommentarToken() {
    return tokens().get(3);
  }

  private Token blockKommentarToken() {
    return tokens().get(4);
  }

  private Token keywordToken() {
    return tokens().get(5);
  }

  private Token annotationToken() {
    return tokens().get(6);
  }

  @Test
  void stringTokenFindetStringsAmAnfangInDerMitteUndAmEnde() {
    assertEquals(1, stringToken().test("\"Anfang\" und Text").size());
    assertEquals(1, stringToken().test("Text \"Mitte\" Text").size());
    assertEquals(1, stringToken().test("Text und \"Ende\"").size());
  }

  @Test
  void stringTokenFindetMehrereStringsUndStringsMitKommentarenImInhalt() {
    List<?> funde = stringToken().test("\"//\" \"/* kein Kommentar */\"");
    assertEquals(2, funde.size());
  }

  @Test
  void stringTokenFindetKeinenUnfertigenString() {
    List<?> funde = stringToken().test("\"nicht fertig");
    assertTrue(funde.isEmpty());
  }

  @Test
  void charTokenFindetMehrereCharsUndKeinenZuLangenChar() {
    assertEquals(2, charToken().test("'a' 'b'").size());
    assertTrue(charToken().test("'ab'").isEmpty());
  }

  @Test
  void zeilenKommentarTokenFindetKommentarMitKeywordUndKeinenNormalenText() {
    List<?> funde = zeilenKommentarToken().test("// return ist hier nur Kommentar");
    assertEquals(1, funde.size());
    assertTrue(zeilenKommentarToken().test("return ohne Kommentar").isEmpty());
  }

  @Test
  void javadocTokenFindetKommentarMitKeywordUndKeinenUnfertigenKommentar() {
    List<?> funde = javadocKommentarToken().test("/** @return etwas */");
    assertEquals(1, funde.size());
    assertTrue(javadocKommentarToken().test("/** nicht fertig").isEmpty());
  }

  @Test
  void blockKommentarTokenFindetMehrereKommentareUndKeinenUnfertigenKommentar() {
    List<?> funde = blockKommentarToken().test("/* a */ Text /* b */");
    assertEquals(2, funde.size());
    assertTrue(blockKommentarToken().test("/* nicht fertig").isEmpty());
  }

  @Test
  void keywordTokenFindetMehrereKeywordsUndKeinKeywordInBezeichner() {
    assertEquals(2, keywordToken().test("public class Test").size());
    assertTrue(keywordToken().test("returnable").isEmpty());
  }

  @Test
  void annotationTokenFindetAnnotationAmAnfangUndMitLeerzeichen() {
    assertEquals(1, annotationToken().test("@Override").size());
    assertEquals(1, annotationToken().test(" @Override").size());
    assertTrue(annotationToken().test("Override").isEmpty());
  }
}
