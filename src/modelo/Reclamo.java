package modelo;

public class Reclamo {
    private final String idReclamo;
    private final Pasajero pasajero;
    private final String motivo;

    public Reclamo(String idReclamo, Pasajero pasajero, String motivo){
        this.idReclamo = idReclamo;
        this.pasajero = pasajero;
        this.motivo = motivo;
    }

    public String getIdReclamo(){
        return idReclamo;
    }

    public Pasajero getPasajero() {
        return pasajero;
    }

    public String getMotivo() {
        return motivo;
    }

    @Override
    public String toString(){
        return "Reclamo " + idReclamo + " [" + pasajero.getNombre() + "]" + motivo;
    }
}
