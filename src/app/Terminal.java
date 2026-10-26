package app;
import java.util.*;

import modelo.Servicio;
public class Terminal {
    private Map<String, Servicio> servicios;

    public Terminal(){
        this.servicios = HashMap<>();
    }

    public void agregarServicio( Servicio servicio){
        servicios.put(servicio.getCodigo())
    }
}
