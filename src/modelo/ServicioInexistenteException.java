package modelo;

public class ServicioInexistenteException extends Exception {
    public ServicioInexistenteException(String codigo){
        super(" El servicio con codigo '" + codigo + "' no existe enla terminal.");
    }
}
