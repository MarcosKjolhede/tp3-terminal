package modelo;

/*
 Un renglon mal formado es un error esperable del archivo cargado
 a mano, y hay q tiene manejarlo para seguir con el siguiente
 */
public class RenglonInvalidoException extends Exception {
    public RenglonInvalidoException(String renglon, int cantidadDeCampos) {
        super("El renglon debe tener 4 campos y tiene " + cantidadDeCampos + ": '" + renglon + "'.");
    }
}