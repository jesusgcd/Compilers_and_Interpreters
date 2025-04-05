%%
%class Lexer
%type String
L=[a-zA-Z_]+
D=[0-9]+
espacio=[ ,\t,\r,\n]+
%{
public String lexeme;
%}
%%
int {lexeme=yytext(); return "Reservadas: " + lexeme;} 
if {lexeme=yytext(); return "Reservadas: " + lexeme;} 
else {lexeme=yytext(); return "Reservadas: " + lexeme;} 
while {lexeme=yytext(); return "Reservadas: " + lexeme;} 
{espacio} {/*Ignore*/}
"//".* {/*Ignore*/}
"=" {lexeme=yytext(); return "Igual: " + lexeme;} 
"+" {lexeme=yytext(); return "Suma: " + lexeme;} 
"-" {lexeme=yytext(); return "Resta: " + lexeme;} 
"*" {lexeme=yytext(); return "Multiplicacion: " + lexeme;} 
"/" {lexeme=yytext(); return "Division: " + lexeme;} 
"(" {lexeme=yytext(); return "ParIzq: " + lexeme;} 
")" {lexeme=yytext(); return "ParDer: " + lexeme;} 
"{" {lexeme=yytext(); return "LlaveIzq: " + lexeme;} 
"}" {lexeme=yytext(); return "LlaveDer: " + lexeme;} 
";" {lexeme=yytext(); return "PuntoYComa: " + lexeme;} 
{L} ({L} | {D})* {lexeme=yytext(); return "Identificador: " + lexeme;} 
("(-"{D}+")") | {D}+ {lexeme=yytext(); return "Numero: " + lexeme;} 
 . {lexeme=yytext(); return "Error: " + lexeme;}
