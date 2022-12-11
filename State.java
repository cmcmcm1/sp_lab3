enum State{
    None,
    Integer,
    WithFloatingPoint,
    Operator,
    StringLiteral,
    CharLiteral,
    Identifier,
    LineComment,
    Comment,
    Punctuation,
    Error,
    Final,
}