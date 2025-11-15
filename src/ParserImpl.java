public class ParserImpl extends Parser {

    /*
     * Implements a recursive-descent parser for the following CFG:
     * * T -> F AddOp T              { if ($2.type == TokenType.PLUS) { $$ = new PlusExpr($1,$3); } else { $$ = new MinusExpr($1, $3); } }
     * T -> F                      { $$ = $1; }
     * F -> Lit MulOp F            { if ($2.type == TokenType.Times) { $$ = new TimesExpr($1,$3); } else { $$ = new DivExpr($1, $3); } }
     * F -> Lit                    { $$ = $1; }
     * Lit -> NUM                  { $$ = new FloatExpr(Float.parseFloat($1.lexeme)); }
     * Lit -> LPAREN T RPAREN      { $$ = $2; }
     * AddOp -> PLUS               { $$ = $1; }
     * AddOp -> MINUS              { $$ = $1; }
     * MulOp -> TIMES              { $$ = $1; }
     * MulOp -> DIV                { $$ = $1; }
     */

    @Override
    public Expr do_parse() throws Exception {
        // The start symbol of the grammar is T.
        // We also need to make sure there are no leftover tokens.
        Expr result = parse_T();
        
        // After parsing, we should be at the end of the token list.
        if (tokens != null) {
            throw new Exception("Parsing error: Unexpected tokens at end of input: " + tokens.elem.lexeme);
        }
        
        return result;
    }

    /*
     * Implements the rules for T:
     * T -> F AddOp T
     * T -> F
     * * We can rewrite this as: T -> F (AddOp T)?
     */
    private Expr parse_T() throws Exception {
        // All T rules start with F
        Expr f = parse_F();

        // Check if an AddOp (PLUS or MINUS) follows
        if (tokens != null && tokens.elem.ty == TokenType.PLUS) {
            // We are in the T -> F + T rule
            consume(TokenType.PLUS);
            Expr t = parse_T(); // Recursive call
            return new PlusExpr(f, t);
        } else if (tokens != null && tokens.elem.ty == TokenType.MINUS) {
            // We are in the T -> F - T rule
            consume(TokenType.MINUS);
            Expr t = parse_T(); // Recursive call
            return new MinusExpr(f, t);
        } else {
            // We are in the T -> F rule
            return f;
        }
    }

    /*
     * Implements the rules for F:
     * F -> Lit MulOp F
     * F -> Lit
     * * We can rewrite this as: F -> Lit (MulOp F)?
     */
    private Expr parse_F() throws Exception {
        // All F rules start with Lit
        Expr lit = parse_Lit();

        // Check if a MulOp (TIMES or DIV) follows
        if (tokens != null && tokens.elem.ty == TokenType.TIMES) {
            // We are in the F -> Lit * F rule
            consume(TokenType.TIMES);
            Expr f = parse_F(); // Recursive call
            return new TimesExpr(lit, f);
        } else if (tokens != null && tokens.elem.ty == TokenType.DIV) {
            // We are in the F -> Lit / F rule
            consume(TokenType.DIV);
            Expr f = parse_F(); // Recursive call
            return new DivExpr(lit, f);
        } else {
            // We are in the F -> Lit rule
            return lit;
        }
    }

    /*
     * Implements the rules for Lit:
     * Lit -> NUM
     * Lit -> LPAREN T RPAREN
     */
    private Expr parse_Lit() throws Exception {
        if (tokens == null) {
            throw new Exception("Parsing error: Unexpected end of input, expected NUM or (");
        }
        
        if (tokens.elem.ty == TokenType.NUM) {
            // We are in the Lit -> NUM rule
            Token num = consume(TokenType.NUM);
            return new FloatExpr(Float.parseFloat(num.lexeme));
        } else if (tokens.elem.ty == TokenType.LPAREN) {
            // We are in the Lit -> LPAREN T RPAREN rule
            consume(TokenType.LPAREN);
            Expr t = parse_T();
            consume(TokenType.RPAREN); // This will throw an error if ')' is missing
            return t;
        } else {
            // No rule matches
            throw new Exception("Parsing error: Expected NUM or '(', but found " + tokens.elem.ty);
        }
    }
}