import java.util.ArrayList;
import java.util.List;

public class AnalizadorLexico {

    private static final String DIGITOS = "0123456789";
    private static final String LETRAS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String OPERADORES = "+-*/";

    public static void main(String[] args) {
        String entrada = "abc@123+def#456";
        AnalizadorLexico analizador = new AnalizadorLexico();
        List<String> tokens = analizador.procesar(entrada);
        
        System.out.println("Tokens generados: " + tokens);
    }

    // Método para procesar la entrada 
    public List<String> procesar(String entrada) {
        List<String> listaTokens = new ArrayList<>();
        StringBuilder tokenActual = new StringBuilder();

        for (char caracter : entrada.toCharArray()) {
            if (LETRAS.indexOf(caracter) >= 0) {
                // Identificadores (inician con letra)
                tokenActual.append(caracter);
            } else if (DIGITOS.indexOf(caracter) >= 0) {
                // Continuar formando identificador o número
                tokenActual.append(caracter);
            } else if (OPERADORES.indexOf(caracter) >= 0) {
                // Guarda el token actual antes de añadir el operador
                if (tokenActual.length() > 0) {
                    listaTokens.add(tokenActual.toString());
                    tokenActual.setLength(0); // Reiniciamos el token actual
                }
                listaTokens.add(String.valueOf(caracter)); // Añadir el operador como token
            } else {
                // Ignorar caracteres no válidos
                System.out.println("Advertencia: Carácter no válido ignorado - '" + caracter + "'");
            }
        }
        
        // Añadir el último token si hay alguno
        if (tokenActual.length() > 0) {
            listaTokens.add(tokenActual.toString());
        }

        return listaTokens;
    }
}
