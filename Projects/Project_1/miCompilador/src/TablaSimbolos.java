
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

class InformacionSimbolo {

    String tipo;
    String ambito;
    int linea;

    public InformacionSimbolo(String tipo, String ambito, int linea) {
        this.tipo = tipo;
        this.ambito = ambito;
        this.linea = linea;
    }

    @Override
    public String toString() {
        return "Tipo: " + tipo + ", Ámbito: " + ambito + ", Línea: " + linea;
    }

    public int getLinea() {
        return this.linea;
    }
}

public class TablaSimbolos {

    private final Map<String, InformacionSimbolo> tabla;

    public TablaSimbolos() {
        tabla = new HashMap<>();
    }

    public void agregarSimbolo(String identificador, InformacionSimbolo info) {
        // Solo agrega el símbolo si no existe ya en la tabla
        if (!tabla.containsKey(identificador)) {
            tabla.put(identificador, info);
        }
    }

    public InformacionSimbolo obtenerSimbolo(String identificador) {
        return tabla.get(identificador);
    }

    public void eliminarSimbolosPorLinea(int linea) {
        Iterator<Map.Entry<String, InformacionSimbolo>> iterator = tabla.entrySet().iterator();

        while (iterator.hasNext()) {
            Map.Entry<String, InformacionSimbolo> entry = iterator.next();
            if (entry.getValue().getLinea() == linea) {
                iterator.remove();
                System.out.println("Simbolo con identificador '" + entry.getKey() + "' en la línea " + linea + " eliminado de la tabla de símbolos.");
            }
        }
    }

    public void mostrarSimbolos() {
        for (Map.Entry<String, InformacionSimbolo> entry : tabla.entrySet()) {
            System.out.println("Identificador: " + entry.getKey() + " - " + entry.getValue());
        }
    }

    public Map<String, InformacionSimbolo> obtenerTodosSimbolos() {
        return tabla;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Tabla de Símbolos:\n");
        for (Map.Entry<String, InformacionSimbolo> entry : tabla.entrySet()) {
            sb.append("Identificador: ").append(entry.getKey())
                    .append(" -> ").append(entry.getValue()).append("\n");
        }
        return sb.toString();
    }
}

