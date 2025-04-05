public class NodoOperacionBinaria extends NodoAST {
    NodoAST izquierda;
    NodoAST derecha;
    String operador;

    public NodoOperacionBinaria(NodoAST izquierda, NodoAST derecha, String operador) {
        this.izquierda = izquierda;
        this.derecha = derecha;
        this.operador = operador;
    }

    @Override
    public void aceptar(VisitanteAST visitante) {
        visitante.visitarNodoOperacionBinaria(this);
    }
}
