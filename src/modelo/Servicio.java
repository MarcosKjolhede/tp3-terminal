package modelo;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.TreeMap;;

public class Servicio {
    private static final int MAX_ESPERA = 5;

    private final String codigo;
    private final String empresa;
    private final String destino;
    private final String horaDeSalida;
    private final int cantidadDeAsientos;
    private final double precio;

    //Busqueda por asiento y recorrido ya ordenado por asiento (TreeMap)
    private final Map<Integer, Pasaje> pasajesVendidos = new TreeMap<>();
    private final ListaDeEspera<Pasajero> espera = new ListaDeEspera<>(MAX_ESPERA);

    public Servicio(String codigo, String empresa, String destino, String horaDeSalida,int cantidadDeAsientos, double precio){
        this.codigo = codigo;
        this.empresa = empresa;
        this.destino = destino;
        this.horaDeSalida = horaDeSalida;
        this.cantidadDeAsientos = cantidadDeAsientos;
        this.precio = precio;
    }
    public String getCodigo(){
        return codigo;
    }
    public String getEmpresa(){
        return empresa;
    }
    public String getDestino() {
        return destino;
    }
    public String getHoraDeSalida() {
        return horaDeSalida;
    }
    public double getPrecio(){
        return precio;
    }
    public int getCantidadDeAsientos(){
        return cantidadDeAsientos;
    }
    public int getCantidadDeAsientosVendidos(){
        return pasajesVendidos.size();
    }
    public int getCantidadDeAsientosLibres(){
        return cantidadDeAsientos - pasajesVendidos.size();
    }
    public double getRecaudacion(){
        return precio * pasajesVendidos.size();
    }

    public boolean estaLleno(){
        return pasajesVendidos.size() >= cantidadDeAsientos;
    }

    public boolean estaOcupado(int numeroAsiento){
        return pasajesVendidos.containsKey(numeroAsiento);
    }
    
    public OptionalInt obtenerAsientoLibreMasBajo() {
        for (int i = 1; i <= cantidadDeAsientos; i++) {
            if (!pasajesVendidos.containsKey(i)) {
                return OptionalInt.of(i);
            }
        }
        return OptionalInt.empty(); 
    }

    //Vende un asiento, si no se puede, saca una VentaRechazadaException
    public void venderAsiento(Pasajero pasajero, int numeroAsiento){
        if(numeroAsiento < 1 || numeroAsiento > cantidadDeAsientos){
            throw new VentaRechazadaException("El asiento " + numeroAsiento + " esta fuera de rango (1 a " + cantidadDeAsientos + ")");
        }
        if (estaOcupado(numeroAsiento)){
            throw new VentaRechazadaException("El asiento " + numeroAsiento + " ya esta vendido.");
        }
        pasajesVendidos.put(numeroAsiento, new Pasaje(pasajero, this, numeroAsiento));
    }
    
    public boolean agregarAEnEspera(Pasajero pasajero) {
        return espera.agregar(pasajero);
    }

    public Pasajero devolverPasaje(int numeroAsiento) {
        Pasaje devuelto = pasajesVendidos.remove(numeroAsiento);
        if (devuelto == null) return null;

        if (!espera.estaVacia()) {
            Pasajero enEspera = espera.siguiente();
            pasajesVendidos.put(numeroAsiento, new Pasaje(enEspera, this, numeroAsiento));
            return enEspera;
        }
        return null;
    }

    public Optional<Pasaje> buscarPasajePorDni(String dni) {
        for (Pasaje p : pasajesVendidos.values()) {
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
            if (pasajesVendidos.containsKey(i)) {
                System.out.println("  " + pasajesVendidos.get(i));
            } else {
                System.out.println("  Asiento " + i + ": [LIBRE]");
            }
        }
        System.out.println(" En lista de espera (" + espera.cantidad() + "):");
        espera.imprimirLista();
    }
}
    
    
