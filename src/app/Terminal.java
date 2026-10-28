package app;
import java.util.*;

import modelo.*;

public class Terminal {
    private Map<String, Servicio> servicios;

    private ListaDeEspera<Reclamo> listaDeEsperaReclamos;

    public Terminal(){
        this.servicios = new HashMap<>();
        this.listaDeEsperaReclamos = new ListaDeEspera<>(10);
    }

    public void agregarServicio( Servicio servicio){
        servicios.put(servicio.getCodigo(), servicio);
    }

    public boolean venderPasaje(String codigoServicio, Pasajero pasajero, String asiento) throws ServicioInexistenteException, VentaRechazadaException {
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
                return  false;
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
                if(numeroAsiento < 1 || numeroAsiento >servicio.getCantifadDeAsientos() ){
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
    public Optional<Pasaje> buscarPasaje(String dni, String codigoServicio) {
        Servicio servicio = servicios.get(codigoServicio);
        if (servicio == null) {
            return Optional.empty();
        }
        return servicio.buscarPasajePorDni(dni);
    }
    //recaudacion por empresa
    public Map<String, Double> calcularRecaudacionPorEmpresa(){
        Map<String, Double> recaudacion = new HashMap<>();
        for(Servicio s : servicios.values()){
            double totalServicio = s.getPrecio() * s.getCantifadDeAsientos();
            recaudacion.put(s.getEmpresa(), recaudacion.getOrDefault(s.getEmpresa(), 0.0) + totalServicio);
        }
        return recaudacion;
    }

    public boolean registrarReclamo(Reclamo reclamo){
        return listaDeEsperaReclamos.agregar(reclamo);
    }

    public Optional<Reclamo> atenderSiguienteReclamo(){
        return listaDeEsperaReclamos.siguiente();
    }

    public Servicio getServicio(String codigo){
        return servicios.get(codigo);
    }
}

