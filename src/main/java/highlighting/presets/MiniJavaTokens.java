package highlighting.presets;

import highlighting.regex.Token;
import java.util.List;
import java.util.regex.Pattern;

public final class MiniJavaTokens {
  public static List<Token> defaultTokens() {
    return List.of(
        // Example: string literals (students should define further tokens below)
        Token.of(Pattern.compile("\"([^\"\\\\]|\\\\.)*\""), MiniJavaColours.STRING_LITERAL_COLOUR),
        Token.of(Pattern.compile("'([^\"\\\\]|\\\\.)'"), MiniJavaColours.CHAR_LITERAL_COLOUR),
        Token.of(Pattern.compile("(//.*)"), MiniJavaColours.LINE_COMMENT_COLOUR),
        Token.of(Pattern.compile("/\\*\\*[\\s\\S]*?\\*/"), MiniJavaColours.JAVADOC_COMMENT_COLOUR),
        Token.of(Pattern.compile("/\\*[\\s\\S]*?\\*/"), MiniJavaColours.BLOCK_COMMENT_COLOUR),
        Token.of(
            Pattern.compile(
                "\\b(abstract|assert|boolean|break|byte|case|catch|char|class|const|continue"
                    + "|default|do|double|else|enum|extends|final|finally|float|for|goto|if"
                    + "|implements|import|instanceof|int|interface|long|native|new|package"
                    + "|private|protected|public|return|short|static|strictfp|super|switch"
                    + "|synchronized|this|throw|throws|transient|try|void|volatile|while"
                    + "|true|false|null|var|record|sealed|yield)\\b"),
            MiniJavaColours.KEYWORD_COLOUR),
        Token.of(Pattern.compile("(@[a-zA-Z][a-zA-Z-]*)"), MiniJavaColours.ANNOTATION_COLOUR));
  }
}
