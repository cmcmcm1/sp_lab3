public class Automate {
    String lexeme="";
    State state=State.None;
    String code;
    boolean exponent=false;
    public Automate(String code){
        this.code=code;
    }
    public void Process(){
        //System.out.println("process");
        int i = 0;
        while (state != State.Final) {
            if (i >= code.length()) {
                if (state == State.None) {
                    state = State.Final;
                    //System.out.println("final");
                } else {
                    state = State.Error;
                    //System.out.println("error");
                }
                break;
            }

            char symbol = code.charAt(i);
            var res = AnalyzeState(symbol , code, i);
            if (res != null){
                i = res.position;
                //System.out.println(i);
            }

        }
    }
    private void Error(){
        lexeme="";
        state=State.None;
    }
    private Result AnalyzeState(char symbol, String text, int position){
        //System.out.println(symbol);
        switch (state) {
            case None:
                lexeme="";
                position=StateNone(symbol,text,position)+1;
                break;
            case LineComment:
                position=LineComment(symbol, position);
                break;
            case Comment:
                position=Comment(symbol,position);
                break;
            case Integer:
                position=Number(symbol,position);
                break;
            case WithFloatingPoint:
                position=WithFloatingPoint(symbol, position);
                break;
            case Identifier:
                position=Identifier(symbol, position);
                break;
            case StringLiteral:
                position=StringLiteral(symbol, position);
                break;
            case CharLiteral:
                position=CharLiteral(symbol, position);
                break;
            case Operator:
                position=Operator(symbol, position);
                break;
            case Punctuation:
                position=Punctuation(symbol,position);
                break;
            case Error:
                printLexeme(lexeme, "error");
                Error();
                return null;
            case Final:
                return null;
        }
        return new Result(position, lexeme);
    }
    private int StateNone(char symbol, String text, int position){
        //System.out.println("none");
        if(Character.isWhitespace(symbol)){
            //System.out.println("whitespace");
        }
        else if (symbol == '/' && text.length() > position +1 && text.charAt(position +1) == '/') {
            state = State.LineComment;
            lexeme = "//";
            position++;
        }
        else if (symbol == '/' && text.length() > position +1 && text.charAt(position +1) == '*') {
            state=State.Comment;
            lexeme ="/*";
            position++;
        }
        else if (isLetter(symbol)||symbol=='$'||symbol=='_') {
            lexeme+=symbol;
            state=State.Identifier;
        }
        else if(symbol=='"'){
            lexeme+=symbol;
            state=State.StringLiteral;
        }
        else if (symbol=='\'') {
            lexeme+=symbol;
            state=State.CharLiteral;
        }
        else if(isDigit(symbol)){
            lexeme+=symbol;
            state=State.Integer;
        }
        else if (contains(constants.operators,symbol+"")){
            lexeme+=symbol;
            state=State.Operator;
        }
        else if(contains(constants.punctuation,symbol+"")){
            lexeme+=symbol;
            state=State.Punctuation;
        }

        return position;
    }
    private int Punctuation(char symbol, int position){
        if(contains(constants.punctuation,lexeme+symbol)) {
            lexeme+=symbol;
            position++;
        }
        else {
            printLexeme(lexeme,"punctuation");
            state=State.None;
        }
        return position;
    }
    private int WithFloatingPoint(char symbol, int position){
        //System.out.println("float");
        if(isDigit(symbol)){
            lexeme+=symbol;
            position++;
        }
        else if(symbol=='.'){
            state=State.Error;
        }
        else if((symbol=='e'||symbol=='E')&&exponent){
            state=State.Error;
        }
        else if(symbol=='e'||symbol=='E'){
            lexeme+=symbol;
            position++;
            exponent=true;
        }
        else {
            state=State.None;
            printLexeme(lexeme, "float");
        }
        return position;
    }
    private int Operator(char symbol, int position){
        //System.out.println("operator" + lexeme+symbol);
        if(contains(constants.operators,lexeme+symbol)){
            lexeme+=symbol;
            position++;
        }
        else
        {
            state=State.None;
            printLexeme(lexeme,"operator");
        }
        return position;
    }

    private int LineComment(char symbol, int position){ //for comments in this style
        //System.out.println("comment");
        if(symbol!='\n'){
            lexeme=lexeme+symbol;
            position++;
        }
        else state=State.None;
        return position;
    }
    private int Comment(char symbol, int position){ /* for comments in this style */
        //System.out.println("comment");
        if((lexeme.charAt(lexeme.length()-1)=='*')&&symbol=='/'){
            state=State.None;
            position++;
        }
        else {
            lexeme+=symbol;
            position++;
        }

        return position;
    }
    private int Number(char symbol, int position){
        //System.out.println("number");
        if(isDigit(symbol)){
            lexeme+=symbol;
            position++;
        }
        else if (symbol=='.'){
            state=State.WithFloatingPoint;
            lexeme+=symbol;
            position++;
        }
        else if((symbol=='e'||symbol=='E')&&exponent){
            state=State.Error;
        }
        else if(symbol=='e'||symbol=='E'){
            lexeme+=symbol;
            position++;
            exponent=true;
        }
        else {
            state=State.None;
            printLexeme(lexeme, "integer");
        }
        return position;
    }
    private int StringLiteral(char symbol, int position){
        //System.out.println("string");
        lexeme+=symbol;
        if(symbol=='"'){
            printLexeme(lexeme, "string");
            state=State.None;
        }
        position++;
        return position;
    }

    private int CharLiteral(char symbol, int position){
        lexeme+=symbol;
        position++;
        if(symbol=='\''){
            if(lexeme.length()<=3){
                printLexeme(lexeme,"char");
                state=State.None;
            }
            else{
                printLexeme(lexeme,"error");
                state=State.Error;
            }
        }

        return position;
    }
    private int Identifier(char symbol, int position){
        //System.out.println("id");
        if(isLetter(symbol)||symbol=='_'||symbol=='$'||isDigit(symbol)){
            lexeme+=symbol;
            position++;
        }
        else{
            analyzeIdentifierOrKeyword();
            state=State.None;
        }
        return position;
    }
    private void analyzeIdentifierOrKeyword(){
        if(contains(constants.keywords,lexeme))printLexeme(lexeme, "keyword");
        else printLexeme(lexeme, "identifier");
    }
    private boolean isLetter(char symbol){
        if((symbol>='A'&&symbol<='Z')||(symbol>='a'&&symbol<='z'))return true;
        else return false;
    }
    private void printLexeme(String lexeme, String type){
        System.out.println(lexeme+" - "+type);
    }
    private boolean isDigit(char symbol){
        if(symbol>='0'&&symbol<='9') return true;
        else return false;
    }
    private boolean contains(String []strings, String string){
        for (String s: strings) {
                if (s.equals(string)) return true;
        }
        return false;
    }
}
