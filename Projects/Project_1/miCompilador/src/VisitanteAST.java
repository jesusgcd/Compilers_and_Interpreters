public interface VisitanteAST {
    void visitarNodoAsignacion(NodoAsignacion nodo);
    void visitarNodoOperacionBinaria(NodoOperacionBinaria nodo);
    void visitarNodoIdentificador(NodoIdentificador nodo);
    void visitarNodoNumero(NodoNumero nodo);
}
