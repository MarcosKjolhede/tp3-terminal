package modelo;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.TreeMap;

public class Terminal {

    public enum ResultadoVenta { VENDIDO, EN_ESPERA }

    private static final int MAX_RECLAMOS = 10;

    // LinkedHashMap: busqueda por código en O(1) y mantiene el orden de carga
    private final Map<String, Servicio> servicios = new LinkedHashMap<>();
    // Segundo uso real de ListaDeEspera<T>: reclamos pendientes de atencion
    private final ListaDeEspera<Reclamo> reclamos = new ListaDeEspera<>(MAX_RECLAMOS);

    // servicios

    public void agregarServicio(Servicio servicio) {
        if (servicios.putIfAbsent(servicio.getCodigo(), servicio) != null) {
            throw new IllegalArgumentException("Ya existe un servicio con código " + servicio.getCodigo());
        }
    }

    private Servicio obtenerServicio(String codigo) throws ServicioInexistenteException {
        Servicio servicio = servicios.get(codigo);
        if (servicio == null) {
            throw new ServicioInexistenteException(codigo);
        }
        return servicio;
    }

    // ventas

    /*
     Vende un pasaje. 'asiento' es un número o "*" (cualquier asiento libre)
     Con "*" y servicio lleno, el pasajero pasa a la espera
     */
    public ResultadoVenta venderPasaje(String codigoServicio, Pasajero pasajero, String asiento)
            throws ServicioInexistenteException {
        Servicio servicio = obtenerServicio(codigoServicio);
        String pedido = asiento.trim();

        if (pedido.equals("*")) {
            OptionalInt libre = servicio.obtenerAsientoLibreMasBajo();
            if (libre.isPresent()) {
                servicio.venderAsiento(pasajero, libre.getAsInt());
                return ResultadoVenta.VENDIDO;
            }
            if (!servicio.agregarAEnEspera(pasajero)) {
                throw new VentaRechazadaException("El servicio " + codigoServicio + " está lleno y su lista de espera también.");
            }
            return ResultadoVenta.EN_ESPERA;
        }

        int numeroAsiento;
        try {
            numeroAsiento = Integer.parseInt(pedido);
        } catch (NumberFormatException e) {
            throw new VentaRechazadaException("El asiento '" + pedido + "' no es un número válido ni '*'.", e);
        }
        servicio.venderAsiento(pasajero, numeroAsiento);
        return ResultadoVenta.VENDIDO;
    }

    //Devuelve el pasaje de un asiento; Optional con quién de la espera se lo queda
    public Optional<Pasajero> devolverPasaje(String codigoServicio, int numeroAsiento)
            throws ServicioInexistenteException {
        return obtenerServicio(codigoServicio).devolverPasaje(numeroAsiento);
    }

    //Nunca devuelve null: si no hay pasaje (o no existe el servicio) devuelve Optional.empty()
    public Optional<Pasaje> buscarPasaje(String dni, String codigoServicio) {
        Servicio servicio = servicios.get(codigoServicio);
        if (servicio == null) {
            return Optional.empty();
        }
        return servicio.buscarPasajePorDni(dni.trim());
    }

    // archivo

    public void procesarArchivo(String ruta) {
        int leidos = 0;
        int ventas = 0;
        int enEspera = 0;
        int rechazados = 0;

        try (BufferedReader lector = new BufferedReader(new FileReader(ruta))) {
            String renglon;
            while ((renglon = lector.readLine()) != null) {
                leidos++;
                try {
                    ResultadoVenta resultado = procesarRenglon(renglon);
                    if (resultado == ResultadoVenta.VENDIDO) {
                        ventas++;
                    } else {
                        enEspera++;
                        System.out.println("Renglon " + leidos + ": quedó en lista de espera.");
                    }
                } catch (ServicioInexistenteException | RenglonInvalidoException | VentaRechazadaException e) {
                    rechazados++;
                    System.out.println("Renglón " + leidos + " rechazado: " + e.getMessage());
                }
            }
            System.out.println("Resumen de " + ruta + ": " + leidos + " renglones leidos, " + ventas + " ventas, " + enEspera + " en espera, " + rechazados + " rechazados.");
        } catch (FileNotFoundException e) {
            System.out.println("No se encontró el archivo '" + ruta + "'.");
        } catch (IOException e) {
            System.out.println("Error leyendo '" + ruta + "': " + e.getMessage());
        }
    }

    private ResultadoVenta procesarRenglon(String renglon)
            throws ServicioInexistenteException, RenglonInvalidoException {
        String[] campos = renglon.split(";", -1);
        if (campos.length != 4) {
            throw new RenglonInvalidoException(renglon, campos.length);
        }
        String codigo = campos[0].trim();
        Pasajero pasajero = new Pasajero(campos[1], campos[2]);
        String asiento = campos[3];
        return venderPasaje(codigo, pasajero, asiento);
    }

    // informes

    public void imprimirInformePasajeros(String codigoServicio) throws ServicioInexistenteException {
        Servicio s = obtenerServicio(codigoServicio);
        System.out.println("Servicio " + s.getCodigo() + " (" + s.getEmpresa() + " -> " + s.getDestino() + ")");
        System.out.println(" Pasajeros por asiento:");
        if (s.getPasajes().isEmpty()) {
            System.out.println("  (ninguno)");
        }
        for (Pasaje p : s.getPasajes()) {
            System.out.println("  " + p);
        }
        List<Pasajero> espera = s.getEnEspera();
        System.out.println(" En espera (" + espera.size() + "):");
        if (espera.isEmpty()) {
            System.out.println("  (ninguno)");
        }
        int pos = 1;
        for (Pasajero p : espera) {
            System.out.println("  " + pos++ + ". " + p);
        }
    }

    public Map<String, Double> calcularRecaudacionPorEmpresa(){
        // TreeMap: empresas ordenadas alfabeticamente en el informe.
        Map<String, Double> recaudacion = new TreeMap<>();
        for (Servicio s : servicios.values()) {
            recaudacion.merge(s.getEmpresa(), s.getRecaudacion(), Double::sum);
        }
        return recaudacion;
    }

    public void imprimirRecaudacionPorEmpresa(){
        System.out.println("Recaudación por empresa:");
        for (Map.Entry<String, Double> e : calcularRecaudacionPorEmpresa().entrySet()) {
            System.out.printf("  %s: $%,.0f%n", e.getKey(), e.getValue());
        }
    }

    public void imprimirServicios() {
        for (Servicio s : servicios.values()) {
            System.out.println("Servicio " + s.getCodigo() + ". " + s.getEmpresa() + ". Destino: " + s.getDestino() + ". Salida: " + s.getHoraDeSalida() + ". Asientos libres: " + s.getCantidadDeAsientosLibres() + ". Precio: " + s.getPrecio());
        }
    }

    public List<String> getCodigosDeServicios() {
        return List.copyOf(servicios.keySet());
    }

    //reclmos

    //Devuelve false si la lista de reclamos está llena
    public boolean registrarReclamo(Reclamo reclamo) {
        return reclamos.agregar(reclamo);
    }

    public Optional<Reclamo> atenderSiguienteReclamo() {
        return reclamos.siguiente();
    }
}
