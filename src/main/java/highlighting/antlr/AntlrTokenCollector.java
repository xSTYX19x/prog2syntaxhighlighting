package highlighting.antlr;

import highlighting.core.HighlightRegion;
import highlighting.core.SyntaxHighlighter;
import highlighting.presets.MiniJavaColours;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import org.antlr.v4.runtime.*;

public class AntlrTokenCollector extends SyntaxHighlighter {

  @Override
  public List<HighlightRegion> collectMatches(String text) {
    CharStream input = CharStreams.fromString(text);
    MiniJavaLexer lexer = new MiniJavaLexer(input);

    // Pull all Tokens - incl. comments
    CommonTokenStream tokenStream = new CommonTokenStream(lexer);
    tokenStream.fill();

    List<HighlightRegion> regions = new ArrayList<>();
    List<Token> tokens = tokenStream.getTokens();

    for (int i = 0; i < tokens.size(); i++) {
        Token t = tokens.get(i);

        // Skip EOF
        if (t.getType() == Token.EOF) continue;
        int start = t.getStartIndex();
        int end = t.getStopIndex() + 1;

        switch (t.getType()) {
            // Keywords
            case MiniJavaLexer.PACKAGE,
                 MiniJavaLexer.IMPORT,
                 MiniJavaLexer.CLASS,
                 MiniJavaLexer.PUBLIC,
                 MiniJavaLexer.PRIVATE,
                 MiniJavaLexer.FINAL,
                 MiniJavaLexer.RETURN,
                 MiniJavaLexer.NULL,
                 MiniJavaLexer.NEW,
                 MiniJavaLexer.IF,
                 MiniJavaLexer.ELSE,
                 MiniJavaLexer.WHILE,
                 MiniJavaLexer.EXTENDS,
                 MiniJavaLexer.IMPLEMENTS ->
                regions.add(new HighlightRegion(start, end, MiniJavaColours.KEYWORD_COLOUR));

            // Literals
            case MiniJavaLexer.STRING_LITERAL ->
                regions.add(new HighlightRegion(start, end, MiniJavaColours.STRING_LITERAL_COLOUR));
            case MiniJavaLexer.CHAR_LITERAL ->
                regions.add(new HighlightRegion(start, end, MiniJavaColours.CHAR_LITERAL_COLOUR));

            // Comments
            case MiniJavaLexer.LINE_COMMENT ->
                regions.add(new HighlightRegion(start, end, MiniJavaColours.LINE_COMMENT_COLOUR));
            case MiniJavaLexer.JAVADOC_COMMENT ->
                regions.add(new HighlightRegion(start, end, MiniJavaColours.JAVADOC_COMMENT_COLOUR));
            case MiniJavaLexer.BLOCK_COMMENT ->
                regions.add(new HighlightRegion(start, end, MiniJavaColours.BLOCK_COMMENT_COLOUR));

            // Annotations: '@' followed immediately by an IDENTIFIER token
            case MiniJavaLexer.AT -> {
                // Look ahead for the identifier that belongs to this annotation
                if (i + 1 < tokens.size()
                    && tokens.get(i + 1).getType() == MiniJavaLexer.IDENTIFIER) {
                    Token identifier = tokens.get(i + 1);
                    int annotationEnd = identifier.getStopIndex() + 1;
                    regions.add(
                        new HighlightRegion(start, annotationEnd, MiniJavaColours.ANNOTATION_COLOUR));
                    i++; // skip the consumed IDENTIFIER
                }
            }

            default -> {
                // All other tokens are not highlighted
            }
        }
    }
    return regions;
  }
}
