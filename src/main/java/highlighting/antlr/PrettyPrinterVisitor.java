package highlighting.antlr;

import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.TerminalNode;

/// MiniJava Pretty Printer (minimal, stateful)
///
/// Requirements:
/// - Reproduce the whole program (comments and whitespaces are gone).
/// - Ignore whitespace from the input; instead, generate:
///     - indentation for class bodies and blocks,
///     - exactly one line per statement (lines ending in ';').
///
/// Simplification:
/// Everything that is not indentation or line breaks is printed as raw tokens (with a very simple
/// space heuristic). Expression and signature formatting is therefore not "nice", which is
/// acceptable for this exercise.
public final class PrettyPrinterVisitor extends MiniJavaBaseVisitor<Void> {

  private final StringBuilder out = new StringBuilder();
  private final int indentWidth;
  private int currentIndent = 0;
  private boolean atLineStart = true;

  private Token lastToken = null;

  public PrettyPrinterVisitor(int indentWidth) {
    this.indentWidth = Math.max(0, indentWidth);
  }

  public String result() {
    return out.toString();
  }

  // ----------------------------------------------------
  // Structural methods – these enforce indentation and "one statement per line"
  // ----------------------------------------------------

    @Override
    public Void visitCompilationUnit(MiniJavaParser.CompilationUnitContext ctx) {
        // package declaration (if present), followed by a blank line
        if (ctx.packageDecl() != null) {
            visit(ctx.packageDecl());
            nl();
        }

        // import declarations – one per line, then a blank line
        if (!ctx.importDecl().isEmpty()) {
            for (MiniJavaParser.ImportDeclContext imp : ctx.importDecl()) {
                visit(imp);
            }
            nl();
        }

        // type declarations separated by blank lines
        for (int i = 0; i < ctx.typeDecl().size(); i++) {
            visit(ctx.typeDecl(i));
            if (i < ctx.typeDecl().size() - 1) nl();
        }

        return null;
    }

    @Override
    public Void visitClassBody(MiniJavaParser.ClassBodyContext ctx) {
        // Opening brace on the same line as the class header
        write(" {");
        nl();

        currentIndent++;
        for (MiniJavaParser.ClassBodyDeclarationContext decl : ctx.classBodyDeclaration()) {
            visit(decl);
        }
        currentIndent--;

        writeln("}");
        return null;
    }

    @Override
    public Void visitBlock(MiniJavaParser.BlockContext ctx) {
        // Opening brace, then indented content, then closing brace
        write("{");
        nl();

        currentIndent++;
        for (MiniJavaParser.BlockStatementContext stmt : ctx.blockStatement()) {
            visit(stmt);
        }
        currentIndent--;

        writeln("}");
        return null;
    }

    @Override
    public Void visitStatement(MiniJavaParser.StatementContext ctx) {
        // Statements that are themselves blocks (if/else/while bodies) handle their own
        // braces via visitBlock; for all others we ensure one-line output with indentation.
        if (ctx.block() != null && ctx.getStart().getText().equals("{")) {
            // Naked block statement
            visit(ctx.block());
            return null;
        }

        // if/else
        if (ctx.IF() != null) {
            write("if");
            write("(");
            visit(ctx.expression());
            write(")");
            write(" ");
            visit(ctx.statement(0));
            if (ctx.ELSE() != null) {
                joinLine();
                write(" else ");
                visit(ctx.statement(1));
            }
            return null;
        }

        // while
        if (ctx.WHILE() != null) {
            write("while");
            write("(");
            visit(ctx.expression());
            write(")");
            write(" ");
            visit(ctx.statement(0));
            return null;
        }

        // return  /  expression-statement  → one statement per line, ending with newline
        visitChildren(ctx);
        nl();
        return null;
    }

    @Override
    public Void visitLocalVarDecl(MiniJavaParser.LocalVarDeclContext ctx) {
        visitChildren(ctx);
        nl();
        return null;
    }

    @Override
    public Void visitFieldDecl(MiniJavaParser.FieldDeclContext ctx) {
        visitChildren(ctx);
        nl();
        return null;
    }

  // ---------------- helper methods ----------------

  private void indent() {
    if (atLineStart) {
      out.repeat(" ", Math.max(0, indentWidth * currentIndent));
      atLineStart = false;
    }
  }

  private void write(String s) {
    if (s == null || s.isEmpty()) return;
    indent();
    out.append(s);
  }

  private void nl() {
    out.append('\n');
    atLineStart = true;
    lastToken = null; // Reset spacing context at the beginning of a line
  }

  private void writeln(String s) {
    write(s);
    nl();
  }

  // Remove the trailing newline so the next write() continues on the same line.
  // Used to place "else" on the same line as the closing "}" of an if-body block.
  private void joinLine() {
    if (atLineStart && out.length() > 0 && out.charAt(out.length() - 1) == '\n') {
      out.deleteCharAt(out.length() - 1);
      atLineStart = false;
    }
  }

  // --------------- token output + basic spacing ---------------

  @Override
  public Void visitTerminal(TerminalNode node) {
    Token t = node.getSymbol();
    String text = t.getText();

    if (lastToken != null) {
      int prevType = lastToken.getType();
      int curType = t.getType();

      // Simple heuristic: insert a space between "word-like" tokens
      if (needsSpaceBetween(prevType, curType)) write(" ");
    }

    write(text);
    lastToken = t;
    return null;
  }

  private boolean needsSpaceBetween(int prevType, int curType) {
    return isWordLike(prevType) && isWordLike(curType);
  }

  private boolean isWordLike(int type) {
    return type == MiniJavaLexer.IDENTIFIER
        || type == MiniJavaLexer.STRING_LITERAL
        || type == MiniJavaLexer.CHAR_LITERAL
        || type == MiniJavaLexer.NULL
        || type == MiniJavaLexer.PACKAGE
        || type == MiniJavaLexer.IMPORT
        || type == MiniJavaLexer.CLASS
        || type == MiniJavaLexer.PUBLIC
        || type == MiniJavaLexer.PRIVATE
        || type == MiniJavaLexer.FINAL
        || type == MiniJavaLexer.RETURN
        || type == MiniJavaLexer.NEW
        || type == MiniJavaLexer.IF
        || type == MiniJavaLexer.ELSE
        || type == MiniJavaLexer.WHILE
        || type == MiniJavaLexer.EXTENDS
        || type == MiniJavaLexer.IMPLEMENTS;
  }
}
