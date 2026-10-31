package app;
import java.util.*;
import modelo.*;

public class Terminal {

    public boolean venderPasaje(Map<String, Servicio> servicios, String codigoServicio, Pasajero pasajero, String asiento) throws ServicioInexistenteException, VentaRechazadaException {
        Servicio servicio = servicios.get(codigoServicio);
        if(servicio == null) {
            throw new ServicioInexistenteException(codigoServicio);
        }
//            System.out.println("Rechazado: El servicio " + codigoServicio + " no existe");
//            return false;
//        }

        //Manejar el asiento "*" o con numero
        if(asiento.equals("*")){
            if(servicio.estaLleno()){
                servicio.agregarAEnEspera(pasajero);
                return false;
            } else {
                int asientoLibre = servicio.obtenerAsientoLibreMasBajo();
                servicio.venderAsiento(pasajero, asientoLibre);
                return true;
            }
        } else {
            //si no es "*", intentamos pasarlo a numero
            //si el usuario mete una palabra Integer.parseInt manda error numberformatexeption
            try{
                int numeroAsiento = Integer.parseInt(asiento);
                //vamos a validar si el asiento esta fuera de rango
                if(numeroAsiento < 1 || numeroAsiento >servicio.getCantidadDeAsientos() ){
                    throw new VentaRechazadaException("El asiento" + numeroAsiento + "esta fuera de rango.");
                }
//                    System.out.println("Rechazado: Numero de asiento fuera de rango" + numeroAsiento);
//                    return false;
//                }
                //Validar si el asiento esta ocupado
                if(servicio.estaOcupado(numeroAsiento)){
                    throw new VentaRechazadaException("El asiento "+ numeroAsiento + "ya esta ocupado.");
                }
//                    System.out.println("Rechazado: El asiento " + numeroAsiento + " ya esta vendido");
//                    return false;
//                }
                servicio.venderAsiento(pasajero, numeroAsiento);
                return true;
            }catch (NumberFormatException e){
                throw new VentaRechazadaException("El asiento" + asiento + "no es un numero valido ni '*'.");
            }
//                System.out.println("Rechazado: El asiento '" + asiento + "' no es un número válido ni '*'");
//                return false;
//            }
        }
    }
    public Optional<Pasaje> buscarPasaje(Map<String, Servicio> servicios, String dni, String codigoServicio) {
        Servicio servicio = servicios.get(codigoServicio);
        if (servicio == null) {
            return Optional.empty();
        }
        return servicio.buscarPasajePorDni(dni);
    }
    //recaudacion por empresa
    public Map<String, Double> calcularRecaudacionPorEmpresa(Map<String, Servicio> servicios){
        Map<String, Double> recaudacion = new HashMap<>();
        for(Servicio s : servicios.values()){
            double totalServicio = s.getPrecio() * s.getCantidadDeAsientos();
            recaudacion.put(s.getEmpresa(), recaudacion.getOrDefault(s.getEmpresa(), 0.0) + totalServicio);
        }
        return recaudacion;
    }

    public boolean registrarReclamo(ListaDeEspera<Reclamo> listaDeEsperaReclamos, Reclamo reclamo){
        return listaDeEsperaReclamos.agregar(reclamo);
    }

    public Optional<Reclamo> atenderSiguienteReclamo(ListaDeEspera<Reclamo> listaDeEsperaReclamos){
        return Optional.ofNullable(listaDeEsperaReclamos.siguiente());
    }

    public static void mostrarServicios(Map<String, Servicio> servicios){
        System.out.println("------------------------------------------------------------");
        for(Servicio servicio: servicios.values()){
            System.out.println("Servicio codigo " + servicio.getCodigo() + ". " + servicio.getEmpresa() + ". Destino: " + servicio.getDestino() + ". Horario: " + servicio.getHoraDeSalida() + ". Asientos disponibles: " + servicio.getCantidadDeAsientosLibres() + ". Precio: " + servicio.getPrecio());
            System.out.println("------------------------------------------------------------");
        }
    }

    public static void agregarServicio(Map<String, Servicio> servicios, Scanner teclado){
        System.out.println("Ingrese el codigo del servicio: ");
        String codigo = teclado.nextLine();
        System.out.println("Ingrese la empresa que ofrece el servicio: ");
        String empresa = teclado.nextLine();
        System.out.println("Ingrese el destino del servicio: ");
        String destino = teclado.nextLine();
        System.out.println("Ingrese la hora de salida del servicio: ");
        String horaDeSalida = teclado.nextLine();
        System.out.println("Ingrese la cantidad de asientos del servicio");
        int cantidadDeAsientos = teclado.nextInt();
        teclado.nextLine();
        System.out.println("Ingrese el costo del servicio: ");
        double precio = teclado.nextDouble();
        teclado.nextLine();
        Servicio servicio = new Servicio(codigo, empresa, destino, horaDeSalida, cantidadDeAsientos, precio);
        servicios.put(servicio.getCodigo(), servicio);
    }

    public static void interfazPrincipal(Map<String, Servicio> servicios, Scanner teclado, ListaDeEspera<Reclamo> listaDeEsperaReclamos){
        String accion = "";
        while (!accion.equalsIgnoreCase("!")){
            System.out.println("------------------------------------------------------------");
            System.out.println("Seleccione la accion que desea realizar: ");
            System.out.println("(Escriba el simbolo que se encuentra delante sin las comillas)");
            System.out.println("'@' Para agregar un servicio");
            System.out.println("'#' Para mostrar todos los servicios");
            System.out.println("'!' Para detener la ejecucion del programa");
            System.out.println("------------------------------------------------------------");
            accion = teclado.nextLine();
            switch(accion){
                case "@":
                    agregarServicio(servicios, teclado);
                    break;
                case "#":
                    mostrarServicios(servicios);
                    break;
            }
        }
    }

    public static void main(String[] args){
        Map<String, Servicio> servicios = new HashMap<>();
        ListaDeEspera<Reclamo> listaDeEsperaReclamos = new ListaDeEspera<>(10);

        Scanner teclado = new Scanner(System.in);

        interfazPrincipal(servicios, teclado, listaDeEsperaReclamos);
    }
}

