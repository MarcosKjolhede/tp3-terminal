package modelo;
import java.util.*;

public class Servicio {
    private String codigo;
    private String empresa;
    private String destino;
    private String horaDeSalida;
    private int cantidadDeAsientos;
    private double precio;

    private Map<Integer, Pasaje> pasajeVendido;
    private ListaDeEspera<Pasajero> listaEspera;

    public Servicio(String codigo, String empresa, String destino, String horaDeSalida,int cantidadDeAsientos, double precio){
        this.codigo = codigo;
        this.empresa = empresa;
        this.destino = destino;
        this.horaDeSalida = horaDeSalida;
        this.cantidadDeAsientos = cantidadDeAsientos;
        this.precio = precio;
        this.pasajeVendido = new HashMap<>();
        this.listaEspera = new ListaDeEspera<>(5);//Maximo 5

    }
    public String getCodigo(){
        return codigo;
    }
    public String getEmpresa(){
        return empresa;
    }
    public double getPrecio(){
        return precio;
    }
    public int getCantifadDeAsientos(){
        return cantidadDeAsientos;
    }

    public boolean estaLleno(){
        return pasajeVendido.size() >= cantidadDeAsientos;
    }

    public boolean estaOcupado(int numeroAsiento){
        return pasajeVendido.containsKey(numeroAsiento);
    }
    
    public int obtenerAsientoLibreMasBajo() {
        for (int i = 1; i <= cantidadDeAsientos; i++) {
            if (!pasajeVendido.containsKey(i)) {
                return i;
            }
        }
        return -1; // Lleno
    }

    public boolean venderAsiento(Pasajero pasajero, int numeroAsiento){
        if(pasajeVendido.containsKey(numeroAsiento)){
            System.out.println("Rechazado: El asiento " + numeroAsiento + " ya se encuentra ocupao");
            return false;
        }
        Pasaje nuevoPasaje = new Pasaje(pasajero, this, numeroAsiento);
        pasajeVendido.put(numeroAsiento, nuevoPasaje);
        return true;
    }
    
    public boolean agregarAEnEspera(Pasajero pasajero) {
        return listaEspera.agregar(pasajero);
    }

    public Pasajero devolverPasaje(int numeroAsiento) {
        Pasaje devuelto = pasajeVendido.remove(numeroAsiento);
        if (devuelto == null) return null;

        if (!listaEspera.estaVacia()) {
            Pasajero enEspera = listaEspera.siguiente();
            pasajeVendido.put(numeroAsiento, new Pasaje(enEspera, this, numeroAsiento));
            return enEspera;
        }
        return null;
    }

    public Optional<Pasaje> buscarPasajePorDni(String dni) {
        for (Pasaje p : pasajeVendido.values()) {
            if (p.getPasajero().getDni().equals(dni)) {
                return Optional.of(p);
            }
        }
        return Optional.empty();
    }

    public void imprimirPasajerosYEspera() {
        System.out.println("Servicio " + codigo + " (" + empresa + " -> " + destino + ")");
        System.out.println(" Pasajes vendidos:");
        for (int i = 1; i <= cantidadDeAsientos; i++) {
            if (pasajeVendido.containsKey(i)) {
                System.out.println("  " + pasajeVendido.get(i));
            } else {
                System.out.println("  Asiento " + i + ": [LIBRE]");
            }
        }
        System.out.println(" En lista de espera (" + listaEspera.cantidad() + "):");
        listaEspera.imprimirLista();
    }
}
    
    
