public class NodoIdentificador extends NodoAST {
    String nombre;

    public NodoIdentificador(String nombre) {
        this.nombre = nombre;
    }

    @Override
    public void aceptar(VisitanteAST visitante) {
        visitante.visitarNodoIdentificador(this);
    }
}
