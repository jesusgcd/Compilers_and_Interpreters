import java.io.FileReader;
import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        try {
            // Crea una instancia del lexer y le pasa un FileReader
            Lexer lexer = new Lexer(new FileReader("input.txt"));
            String token;

            // Itera a través de los tokens generados por el lexer
            while ((token = lexer.yylex()) != null) {
                System.out.println(token);
            }
        } catch (IOException e) {
            System.err.println("Error al abrir el archivo: " + e.getMessage());
        }
    }
}
