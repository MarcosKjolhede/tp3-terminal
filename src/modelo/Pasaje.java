package modelo;

public class Pasaje {
    private Pasajero pasajero;
    private Servicio servicio;
    private int numeroAsiento;

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
