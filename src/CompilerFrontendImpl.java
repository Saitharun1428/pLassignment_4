public class CompilerFrontendImpl extends CompilerFrontend {
    public CompilerFrontendImpl() {
        super();
    }

    public CompilerFrontendImpl(boolean debug_) {
        super(debug_);
    }

    /*
     * Initializes the local field "lex" to be equal to the desired lexer.
     * The desired lexer has the following specification:
     * 
     * NUM: [0-9]*\.[0-9]+
     * PLUS: \+
     * MINUS: -
     * TIMES: \*
     * DIV: /
     * WHITE_SPACE (' '|\n|\r|\t)*
     */
    @Override
protected void init_lexer() {
    // 1. Create the lexer
    this.lex = new LexerImpl();

    // --- 2. Build Automaton for NUM: [0-9]*\.[0-9]+ ---
    // This is the most complex one. Let's use:
    // State 0: Start state
    // State 1: Integer part (accepts [0-9])
    // State 2: Seen decimal point (accepts .)
    // State 3: Fractional part (accepts [0-9], is an accept state)
    Automaton num_automaton = new AutomatonImpl();
    num_automaton.addState(0, true, false);
    num_automaton.addState(1, false, false);
    num_automaton.addState(2, false, false);
    num_automaton.addState(3, false, true); // Final state is the only accept state

    // For the [0-9]* part (loop on state 0 and 1)
    for (char c = '0'; c <= '9'; c++) {
        num_automaton.addTransition(0, c, 1); // From start to integer part
        num_automaton.addTransition(1, c, 1); // Loop for more digits
    }

    // For the \. part
    num_automaton.addTransition(0, '.', 2); // e.g., .5
    num_automaton.addTransition(1, '.', 2); // e.g., 5.5

    // For the [0-9]+ part
    for (char c = '0'; c <= '9'; c++) {
        num_automaton.addTransition(2, c, 3); // Must have at least one digit after .
        num_automaton.addTransition(3, c, 3); // Loop for more digits
    }
    lex.add_automaton(TokenType.NUM, num_automaton);


    // --- 3. Build Automaton for PLUS: \+ ---
    // State 0: Start
    // State 1: Accept
    Automaton plus_automaton = new AutomatonImpl();
    plus_automaton.addState(0, true, false);
    plus_automaton.addState(1, false, true);
    plus_automaton.addTransition(0, '+', 1);
    lex.add_automaton(TokenType.PLUS, plus_automaton);


    // --- 4. Build Automaton for MINUS: - ---
    Automaton minus_automaton = new AutomatonImpl();
    minus_automaton.addState(0, true, false);
    minus_automaton.addState(1, false, true);
    minus_automaton.addTransition(0, '-', 1);
    lex.add_automaton(TokenType.MINUS, minus_automaton);


    // --- 5. Build Automaton for TIMES: \* ---
    Automaton times_automaton = new AutomatonImpl();
    times_automaton.addState(0, true, false);
    times_automaton.addState(1, false, true);
    times_automaton.addTransition(0, '*', 1);
    lex.add_automaton(TokenType.TIMES, times_automaton);


    // --- 6. Build Automaton for DIV: / ---
    Automaton div_automaton = new AutomatonImpl();
    div_automaton.addState(0, true, false);
    div_automaton.addState(1, false, true);
    div_automaton.addTransition(0, '/', 1);
    lex.add_automaton(TokenType.DIV, div_automaton);


    // --- 7. Build Automaton for LPAREN: \( ---
    Automaton lparen_automaton = new AutomatonImpl();
    lparen_automaton.addState(0, true, false);
    lparen_automaton.addState(1, false, true);
    lparen_automaton.addTransition(0, '(', 1);
    lex.add_automaton(TokenType.LPAREN, lparen_automaton);


    // --- 8. Build Automaton for RPAREN: \) ---
    Automaton rparen_automaton = new AutomatonImpl();
    rparen_automaton.addState(0, true, false);
    rparen_automaton.addState(1, false, true);
    rparen_automaton.addTransition(0, ')', 1);
    lex.add_automaton(TokenType.RPAREN, rparen_automaton);


    // --- 9. Build Automaton for WHITE_SPACE: (' '|\n|\r|\t)* ---
    // State 0: Start AND Accept (for empty string)
    Automaton ws_automaton = new AutomatonImpl();
    ws_automaton.addState(0, true, true); // Accepts zero or more
    ws_automaton.addTransition(0, ' ', 0);
    ws_automaton.addTransition(0, '\n', 0);
    ws_automaton.addTransition(0, '\r', 0);
    ws_automaton.addTransition(0, '\t', 0);
    lex.add_automaton(TokenType.WHITE_SPACE, ws_automaton);
}

}
