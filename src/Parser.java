import java.util.*;

/*
Implement the class Parser, the syntax analyzer (parser).
This should be a top-down recursive-descent parser for the grammar G above,
i.e. a program which recognizes valid infix expressions.
The output of the parser is the stack-based intermediate code S,
corresponding to the given expression, written to standard output (stdout).
If the expression is not in the language (or if an ERROR token is returned by the Lexer)
then the parser should output the error message "Syntax error!"
(at the point when the error is recognized) and immediately quit.
*/
public class Parser {
    private Lexer lexer;
    private Token token;
    Stack<Token> operators;
    Stack<Token> nums;
    int currentFactor;
    int lastFactor;
    Node root;
    Node current;
    private class Node {
        Node parent;
        public ArrayList<Token> operators;
        public Stack<Token> nums;
        public List<Node> children;
        public Node(Node parent)
        {
            operators = new ArrayList<Token>();
            nums = new Stack<Token>();
            children = new ArrayList<Node>();
            this.parent=parent;
        }
    }
    public Parser(Lexer lexer )
    {
        currentFactor = 0;
        lastFactor = 0;
        this.lexer=lexer;
        token = this.lexer.nextToken();
        operators= new Stack<Token>();
        nums = new Stack<Token>();
        root = new Node(null);
        current = root;
    }

    public void expr() {
        term();
        while ( token.gettCode() == TokenCode.PLUS ) {
            current.operators.add(token);
            token = lexer.nextToken();
            term();

        }
    }

    public void term() {
        factor(false); /* parses the first factor */
        while ( token.gettCode() == TokenCode.MULT) {
            current.operators.add(token);
            token = lexer.nextToken();
            factor(true);
            nums = new Stack<Token>();
        }
    }

    public void factor(boolean mult) {
        /* Decide what rule to use */
        if (token.gettCode() == TokenCode.INT)
        {
            Node newNode = new Node(current);
            newNode.nums.push(token);
            current.children.add(newNode);
            token = lexer.nextToken(); /* get the next token */
        }
        else if (token.gettCode() == TokenCode.LPAREN)
        {
            Node newNode = new Node(current);
            current.children.add(newNode);
            current = newNode;
            token = lexer.nextToken();
            expr();
            if (token.gettCode() == TokenCode.RPAREN) {
                current = newNode.parent;
                token = lexer.nextToken();
            }
        }


        else error(); /* neither and id nor a left parenthesis */
    }

    public void error() {
        System.out.println("Syntax error!");
        System.exit(1);
    }

    public void end() {
        System.out.println("End!");

    }

    public void print(Node node) {
        if(node == null) return;
        //System.out.println("----------BEGIN PRINT");

        for(Token it : node.nums)
        {
            System.out.println("PUSH " + it.getLexeme());

        }
        for(Node ch : node.children)
        {
            print(ch);
        }
        Collections.reverse(node.operators);
        for(Token it : node.operators )
        {
            if(it.gettCode() == TokenCode.MULT)
                System.out.println("MULT");
            else if(it.gettCode()==TokenCode.PLUS)
                System.out.println("ADD");
        }

        //System.out.println("------------END PRINT");
    }

    public void parse() {
        expr();
       // end();
        print(root);
        System.out.println("PRINT");
    }
}
