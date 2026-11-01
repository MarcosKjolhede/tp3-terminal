package modelo;

public class Pasaje {
    private final Pasajero pasajero;
    private final Servicio servicio;
    private final int numeroAsiento;

    public Pasaje(Pasajero pasajero, Servicio servicio, int numeroAsiento){
        this.pasajero = pasajero;
        this.servicio = servicio;
        this.numeroAsiento = numeroAsiento;
    }

    public Pasajero getPasajero(){
        return pasajero;
    }
    public Servicio getServicio(){
        return servicio;
    }
    public int getNroAsiento(){
        return numeroAsiento;
    }

    @Override 
    public String toString(){
        return "Asiento " + numeroAsiento + ": " + pasajero;
    }

}
