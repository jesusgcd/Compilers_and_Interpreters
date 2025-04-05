public class NodoNumero extends NodoAST {
    int valor;

    public NodoNumero(int valor) {
        this.valor = valor;
    }

    @Override
    public void aceptar(VisitanteAST visitante) {
        visitante.visitarNodoNumero(this);
    }
}
