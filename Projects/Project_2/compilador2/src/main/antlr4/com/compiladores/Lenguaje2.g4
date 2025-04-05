grammar Lenguaje2;

@parser::header {
    import java.util.Map;
    import java.util.HashMap;
}

@parser::members {
    Map<String, Object> symbolTable = new HashMap<>();
}

programa 
    : declaraciones+;

declaraciones 
    : declaracion PYC;

declaracion 
    : expresion
    | control_flujo
    | impresion;

control_flujo 
    : if_stmt
    | while_stmt
    | for_stmt;

if_stmt 
    : IF PAR_IZQ expresion PAR_DER LLAV_IZQ programa LLAV_DER
    | IF PAR_IZQ expresion PAR_DER LLAV_IZQ programa LLAV_DER ELSE LLAV_IZQ programa LLAV_DER;

while_stmt 
    : WHILE PAR_IZQ expresion PAR_DER LLAV_IZQ programa LLAV_DER;

for_stmt 
    : FOR PAR_IZQ expresion PYC expresion PYC expresion PAR_DER LLAV_IZQ programa LLAV_DER;

impresion 
    : PRINT PAR_IZQ expresion PAR_DER /*{ System.out.println($expresion.value); }*/;

asignacion 
    : ID ASIG expresion { symbolTable.put($ID.text, $expresion.value); };

expresion returns [Object value]
    : asignacion
    | t1 = factor { $value = (int)$t1.value; }
      (
          MAS t2 = factor { $value = (int)$value + (int)$t2.value; }
        | MENOS t2 = factor { $value = (int)$value - (int)$t2.value; }
      )*
    | expresion OP_REL expresion;

factor returns [Object value]
    : MENOS t = term { $value = -(int)$t.value; }
      (
          MULT t2 = term { $value = (int)$value * (int)$t2.value; }
        | DIV t2 = term { $value = (int)$value / (int)$t2.value; }
      )*
    | t1 = term { $value = (int)$t1.value; }
      (
          MULT t2 = term { $value = (int)$value * (int)$t2.value; }
        | DIV t2 = term { $value = (int)$value / (int)$t2.value; }
      )*;

term returns [Object value] 
    : ID 
      {
        if (symbolTable.containsKey($ID.text)) {
            $value = symbolTable.get($ID.text);
        } else {
            System.err.println(
                "Error semántico en la línea " + $ID.getLine() + 
                ":" + $ID.getCharPositionInLine() + 
                ": El identificador '" + $ID.text + "' no está declarado."
            );
            $value = null; // Puedes manejar esto según sea necesario
        }
      }
    | NUM { $value = Integer.parseInt($NUM.text); }
    | PAR_IZQ expresion PAR_DER { $value = $expresion.value; };

// Tokens
IF      : 'if';
ELSE    : 'else';
WHILE   : 'while';
FOR     : 'for';
PRINT   : 'print';
MAS     : '+';
MENOS   : '-';
MULT    : '*';
DIV     : '/';
OP_REL  : '<' | '>' | '==' | '<>' | '<=' | '>=';
PAR_IZQ : '(';
PAR_DER : ')';
LLAV_IZQ: '{';
LLAV_DER: '}';
ASIG    : '=';
PYC     : ';';
NUM     : [0-9]+;
ID      : [a-z]+;
WS      : [ \t\r\n]+ -> skip;
