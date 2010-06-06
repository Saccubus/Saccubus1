grammar Niwango;

options {
	language=C;
	backtrack=true;
	output=AST;
}

@parser::header {
#include "../node/node.h"
}
@lexer::header {
#include "../node/node.h"
}

script returns [Node* node]
@init { $node = TopLevelNode_new(); }
	:(a=stmt {TopLevelNode_add($node,$a.node);}
		((',' | ';') b=stmt {TopLevelNode_add($node,$b.node);} )*
	)?
	EOF!;

stmt returns [Node* node]
	: e=expr {$node = $e.node;} ;

expr returns [Node* node]
	: t=term	( (':=' | '=') e=expr {$node = AssignNode_new($t.node, $e.node); }
			| op=opassign_op e=expr {$node = OpAssignNode_new($t.node,$e.node,$op.op); }
			)
	| ex=expr5 {$node = $ex.node; }
	;

expr5 returns [Node* node]
	: l=expr4  { $node = $l.node; }
		('||' r=expr4 { $node=BinaryOpNode_new("||",$node,$r.node); })*;

expr4 returns [Node* node]
	: l=expr3 { $node = $l.node; }
		('&&' r=expr3 { $node=BinaryOpNode_new("&&",$node,$r.node); })*;

expr3 returns [Node* node]
	: l=expr2 { $node = $l.node; }
				( '>' r=expr2 { $node=BinaryOpNode_new(">",$node,$r.node); }
				| '<' r=expr2 { $node=BinaryOpNode_new("<",$node,$r.node); }
				| '>=' r=expr2 { $node=BinaryOpNode_new(">=",$node,$r.node); }
				| '<=' r=expr2 { $node=BinaryOpNode_new("<=",$node,$r.node); }
				| '==' r=expr2 { $node=BinaryOpNode_new("==",$node,$r.node); }
				| '!=' r=expr2 { $node=BinaryOpNode_new("!=",$node,$r.node); }
				)*;

expr2 returns [Node* node]
	: l=expr1 { $node = $l.node; }
				( '+' r=expr1 { $node=BinaryOpNode_new("+",$node,$r.node); }
				| '-' r=expr1 { $node=BinaryOpNode_new("-",$node,$r.node); }
				)*;

expr1 returns [Node* node]
	: l=term { $node = $l.node; }
			( '*' r=term { $node=BinaryOpNode_new("*",$node,$r.node); }
			| '/' r=term { $node=BinaryOpNode_new("/",$node,$r.node); }
			| '\%' r=term { $node=BinaryOpNode_new("\%",$node,$r.node); }
			)*
	;

term returns [Node* node]
		: '++' term { $node = PrefixOpNode_new("++", $node); }
		| '--' term { $node = PrefixOpNode_new("--", $node); }
		| '+' term { $node = PrefixOpNode_new("+", $node); }
		| '-' term { $node = PrefixOpNode_new("-", $node); }
		| '!' term { $node = PrefixOpNode_new("!", $node); }
		| postfix
		;

postfix returns [Node* node]
		: prim = primary { $node = $prim.node; }
		( '++' { $node = SuffixOpNode_new("++", $node); }
		| '--' { $node = SuffixOpNode_new("--", $node); }
		| '.' ID { $node = AccessorNode_new($node, $ID.text->chars); }
		| '[' e=expr ']' { $node = ArrayAccessNode_new($node,$e.node); }
		| '(' a=args ')' { $node = CallingNode_new($node,$a.node); }
		)*;

primary returns [Node* node]
	: INT { $node=IntegerNode_new($INT.text->chars); }
	| FLOAT { $node=FloatNode_new($FLOAT.text->chars); }
	| STRING_DOUBLE { $node=StringNode_new_Double($STRING_DOUBLE.text->chars); }
	| STRING_SINGLE { $node=StringNode_new_Single($STRING_SINGLE.text->chars); }
	| v=array { $node = $v.node; }
	| ID { $node = LabelNode_new($ID.text->chars); }
	| '(' val=expr ')' { $node = $val.node; }
	;

array returns [Node* node]
@init {$node = ArrayNode_new();}
	:
		'['
		e=expr { ArrayNode_add($node,$e.node); } (','! v=expr { ArrayNode_add($node,$v.node); } )*
		']';

opassign_op returns [const gchar* op]
		: '+=' {$op = "+";}
		| '-=' {$op = "-";}
		| '*=' {$op = "*";}
		| '/=' {$op = "/";}
		| '\%=' {$op = "\%";}
		;

args returns [Node* node]
@init {$node = ArgsNode_new();}
	:	(a=arg {ArgsNode_add($node,$a.node);} (',' b=arg {ArgsNode_add($node,$b.node);} )*)?;

arg returns [Node* node]
	:	(ID ':')? e=expr {$node = ArgNode_new($ID.text->chars,$e.node);} ;

/////////////////////////////////////////////////////////////////////////////////////////////////

ID  :	('a'..'z'|'A'..'Z'|'_') ('a'..'z'|'A'..'Z'|'0'..'9'|'_')*
    ;

WS  :   ( ' '
        | '\t'
        | '\r'
        | '\n'
        ) {$channel=HIDDEN;}
    ;

//literal

INT :	'0'..'9'+
    ;

FLOAT
    :   ('0'..'9')+ '.' ('0'..'9')* EXPONENT?
    |   '.' ('0'..'9')+ EXPONENT?
    |   ('0'..'9')+ EXPONENT
    ;

STRING_DOUBLE returns [Node* node]
    :  '\"' str=( ESC_SEQ | ~('\\' | '"') )* '\"'
    ;

STRING_SINGLE returns [Node* node]
    :  '\'' str=( ESC_SEQ | ~('\\'|'\'') )* '\''
    ;

/* *****************
	Fragment Rules
   ****************** */

fragment
EXPONENT : ('e'|'E') ('+'|'-')? ('0'..'9')+ ;

fragment
HEX_DIGIT : ('0'..'9'|'a'..'f'|'A'..'F') ;

fragment
ESC_SEQ
    :   '\\' ('b'|'t'|'n'|'f'|'r'|'\"'|'\''|'\\')
    |   UNICODE_ESC
    |   OCTAL_ESC
    ;

fragment
OCTAL_ESC
    :   '\\' ('0'..'3') ('0'..'7') ('0'..'7')
    |   '\\' ('0'..'7') ('0'..'7')
    |   '\\' ('0'..'7')
    ;

fragment
UNICODE_ESC
    :   '\\' 'u' HEX_DIGIT HEX_DIGIT HEX_DIGIT HEX_DIGIT
    ;
