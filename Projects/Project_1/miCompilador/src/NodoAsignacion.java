public class NodoAsignacion extends NodoAST {
    String identificador;
    NodoAST expresion;

    public NodoAsignacion(String identificador, NodoAST expresion) {
        this.identificador = identificador;
        this.expresion = expresion;
    }

    @Override
    public void aceptar(VisitanteAST visitante) {
        visitante.visitarNodoAsignacion(this);
    }
}
