package highlighting.regex;

import highlighting.core.HighlightRegion;
import highlighting.core.SyntaxHighlighter;
import highlighting.presets.MiniJavaTokens;
import java.util.*;
import java.util.regex.Matcher;

public class ScanningHighlighter extends SyntaxHighlighter {
  private final List<Token> tokens;

  public ScanningHighlighter() {
    this(MiniJavaTokens.defaultTokens());
  }

  ScanningHighlighter(List<Token> tokens) {
    this.tokens = tokens;
  }

  @Override
  public List<HighlightRegion> collectMatches(String text) {
    List<HighlightRegion> result = new ArrayList<>();
    int i = 0;

    while (i < text.length()) {
      HighlightRegion best = null;

      for (Token token : tokens) {
        Matcher matcher = token.pattern().matcher(text);
        if (matcher.find(i) && matcher.start() == i) {
          HighlightRegion candidate = new HighlightRegion(i, matcher.end(), token.colour());
          if (best == null || (matcher.end() - i) > (best.end() - best.start())) {
            best = candidate;
          }
        }
      }

      if (best != null) {
        result.add(best);
        i = best.end();
      } else {
        i++;
      }
    }

    return result;
  }

  @Override
  public List<HighlightRegion> normalize(List<HighlightRegion> candidates) {
    return candidates;
  }
}
