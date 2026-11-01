package app;

import java.util.List;
import java.util.Optional;
import java.util.Scanner;

import modelo.*;

public class Main {

    private static final int ANCHO = 64;
    private static int proximoReclamo = 1;

    // menú

    private static void mostrarMenu() {
        System.out.println();
        System.out.println(linea('='));
        System.out.println(centrar("TERMINAL DE OMNIBUS DE NECOCHEA"));
        System.out.println(linea('='));
        System.out.println("  SERVICIOS");
        System.out.println("   1) Agregar un servicio");
        System.out.println("   2) Cargar servicios");
        System.out.println("   3) Mostrar servicios");
        System.out.println("  VENTAS");
        System.out.println("   4) Procesar archivo de ventas");
        System.out.println("   5) Vender un pasaje");
        System.out.println("   6) Devolver un pasaje");
        System.out.println("   7) Buscar un pasaje");
        System.out.println("  INFORMES");
        System.out.println("   8) Pasajeros y espera de un servicio");
        System.out.println("   9) Recaudación por empresa");
        System.out.println("  RECLAMOS");
        System.out.println("  10) Registrar un reclamo");
        System.out.println("  11) Atender el siguiente reclamo");
        System.out.println("  OTROS");
        System.out.println("  12) Demostración completa del TP");
        System.out.println("   0) Salir");
        System.out.println(linea('-'));
        System.out.print("Opción: ");
    }

    // servicios

    private static void agregarServicio(Terminal terminal, Scanner teclado) {
        titulo("AGREGAR SERVICIO");
        String codigo = leerTextoNoVacio(teclado, "Código (ej. NEC-MDP-0700): ");
        String empresa = leerTextoNoVacio(teclado, "Empresa: ");
        String destino = leerTextoNoVacio(teclado, "Destino: ");
        String hora = leerTextoNoVacio(teclado, "Hora de salida (ej. 07:00): ");
        int asientos = leerEntero(teclado, "Cantidad de asientos: ", 1);
        double precio = leerPrecio(teclado, "Precio: ");

        cargarServicio(terminal, new Servicio(codigo, empresa, destino, hora, asientos, precio));
    }
    
    private static void cargarServiciosDelTP(Terminal terminal) {
        titulo("CARGA DE LOS SERVICIOS DEL TP");
        cargarServicio(terminal, new Servicio("NEC-MDP-0700", "Costera", "Mar del Plata", "07:00", 4, 12000));
        cargarServicio(terminal, new Servicio("NEC-BA-2200", "Atlántica", "Buenos Aires", "22:00", 6, 38000));
        cargarServicio(terminal, new Servicio("NEC-TAN-0930", "Costera", "Tandil", "09:30", 3, 9500));
        cargarServicio(terminal, new Servicio("NEC-TRA-1400", "Atlántica", "Tres Arroyos", "14:00", 5, 21000));
    }

    private static void cargarServicio(Terminal terminal, Servicio servicio) {
        try {
            terminal.agregarServicio(servicio);
            System.out.println("[OK] Servicio " + servicio.getCodigo() + " agregado.");
        } catch (IllegalArgumentException e) {
            System.out.println("[X]  " + e.getMessage());
        }
    }

    private static void mostrarServicios(Terminal terminal) {
        titulo("SERVICIOS DE LA TERMINAL");
        if (terminal.getCodigosDeServicios().isEmpty()) {
            System.out.println("No hay servicios cargados.");
            return;
        }
        terminal.imprimirServicios();
    }

    // ventas

    private static void procesarArchivo(Terminal terminal, Scanner teclado) {
        titulo("PROCESAR ARCHIVO DE VENTAS");
        String ruta = leerTextoNoVacio(teclado, "Ruta del archivo (ej. documentos\\ventas.txt): ");
        System.out.println(linea('-'));
        terminal.procesarArchivo(ruta);
    }

    private static void venderPasaje(Terminal terminal, Scanner teclado) {
        titulo("VENDER UN PASAJE");
        String codigo = leerTextoNoVacio(teclado, "COdigo del servicio: ");
        String dni = leerTextoNoVacio(teclado, "DNI del pasajero: ");
        String nombre = leerTextoNoVacio(teclado, "Nombre del pasajero: ");
        String asiento = leerTextoNoVacio(teclado, "Asiento (número o * para cualquiera): ");
        try {
            Terminal.ResultadoVenta resultado =
                    terminal.venderPasaje(codigo.trim(), new Pasajero(dni, nombre), asiento);
            if (resultado == Terminal.ResultadoVenta.VENDIDO) {
                System.out.println("[OK] Pasaje vendido a " + nombre.trim() + ".");
            } else {
                System.out.println("[..] El servicio esta lleno: " + nombre.trim()
                        + " quedo en lista de espera");
            }
        } catch (ServicioInexistenteException | VentaRechazadaException e) {
            System.out.println("[X]  Venta rechazada: " + e.getMessage());
        }
    }

    private static void devolverPasaje(Terminal terminal, Scanner teclado) {
        titulo("DEVOLVER UN PASAJE");
        String codigo = leerTextoNoVacio(teclado, "Código del servicio: ").trim();
        String dni = leerTextoNoVacio(teclado, "DNI del pasajero que devuelve: ").trim();

        Optional<Pasaje> pasaje = terminal.buscarPasaje(dni, codigo);
        if (pasaje.isEmpty()) {
            System.out.println("[X]  El DNI " + dni + " no tiene pasaje en " + codigo + ".");
            return;
        }
        int asiento = pasaje.get().getNroAsiento();
        try {
            Optional<Pasajero> nuevoDueno = terminal.devolverPasaje(codigo, asiento);
            System.out.println("[OK] " + pasaje.get().getPasajero().getNombre()
                    + " devolvió el asiento " + asiento + ".");
            if (nuevoDueno.isPresent()) {
                System.out.println("     Se queda con el asiento: " + nuevoDueno.get());
            } else {
                System.out.println("     Nadie estaba en espera: el asiento queda libre.");
            }
        } catch (ServicioInexistenteException | IllegalArgumentException e) {
            System.out.println("[X]  " + e.getMessage());
        }
    }

    private static void buscarPasaje(Terminal terminal, Scanner teclado) {
        titulo("BUSCAR UN PASAJE");
        String codigo = leerTextoNoVacio(teclado, "Código del servicio: ").trim();
        String dni = leerTextoNoVacio(teclado, "DNI del pasajero: ").trim();
        mostrarBusqueda(terminal, dni, codigo);
    }

    private static void mostrarBusqueda(Terminal terminal, String dni, String codigo) {
        Optional<Pasaje> pasaje = terminal.buscarPasaje(dni, codigo);
        if (pasaje.isPresent()) {
            System.out.println("[OK] DNI " + dni + " en " + codigo + " -> " + pasaje.get());
        } else {
            System.out.println("[X]  DNI " + dni + " no tiene pasaje en " + codigo + ".");
        }
    }

//informes

    private static void informePasajeros(Terminal terminal, Scanner teclado) {
        titulo("PASAJEROS Y ESPERA DE UN SERVICIO");
        String codigo = leerTextoNoVacio(teclado, "Código del servicio: ").trim();
        System.out.println(linea('-'));
        try {
            terminal.imprimirInformePasajeros(codigo);
        } catch (ServicioInexistenteException e) {
            System.out.println("[X]  " + e.getMessage());
        }
    }

    private static void informeRecaudacion(Terminal terminal) {
        titulo("RECAUDACIÓN POR EMPRESA");
        terminal.imprimirRecaudacionPorEmpresa();
    }

    private static void informeDeTodosLosServicios(Terminal terminal) throws ServicioInexistenteException {
        List<String> codigos = terminal.getCodigosDeServicios();
        for (String codigo : codigos) {
            terminal.imprimirInformePasajeros(codigo);
            System.out.println(linea('-'));
        }
    }

    // reclamos

    private static void registrarReclamo(Terminal terminal, Scanner teclado) {
        titulo("REGISTRAR UN RECLAMO");
        String dni = leerTextoNoVacio(teclado, "DNI del pasajero: ");
        String nombre = leerTextoNoVacio(teclado, "Nombre del pasajero: ");
        String motivo = leerTextoNoVacio(teclado, "Motivo: ");
        Reclamo reclamo = new Reclamo("R-" + proximoReclamo, new Pasajero(dni, nombre), motivo.trim());
        if (terminal.registrarReclamo(reclamo)) {
            proximoReclamo++;
            System.out.println("[OK] Registrado: " + reclamo);
        } else {
            System.out.println("[X]  La lista de reclamos está llena. Atienda alguno antes de registrar más.");
        }
    }

    private static void atenderReclamo(Terminal terminal) {
        titulo("ATENDER EL SIGUIENTE RECLAMO");
        Optional<Reclamo> reclamo = terminal.atenderSiguienteReclamo();
        if (reclamo.isPresent()) {
            System.out.println("[OK] Atendiendo: " + reclamo.get());
        } else {
            System.out.println("No hay reclamos pendientes.");
        }
    }

    //  demostración

    private static void demostracion(Terminal terminal) {
        cargarServiciosDelTP(terminal);

        titulo("1. PROCESANDO ventas.txt");
        terminal.procesarArchivo("ventas.txt");

        titulo("2. ARCHIVO INEXISTENTE");
        terminal.procesarArchivo("no_existe.txt");

        try {
            titulo("3. BRUNO ORTIZ DEVUELVE SU PASAJE (NEC-MDP-0700)");
            Optional<Pasaje> bruno = terminal.buscarPasaje("28999111", "NEC-MDP-0700");
            if (bruno.isPresent()) {
                int asiento = bruno.get().getNroAsiento();
                Optional<Pasajero> nuevoDueno = terminal.devolverPasaje("NEC-MDP-0700", asiento);
                System.out.println("Bruno Ortiz devolvió el asiento " + asiento + ".");
                System.out.println(nuevoDueno.isPresent()
                        ? "Se queda con el asiento: " + nuevoDueno.get()
                        : "Nadie estaba en espera: el asiento queda libre.");
            } else {
                System.out.println("Bruno Ortiz no tiene pasaje en NEC-MDP-0700.");
            }

            titulo("4. INFORMES");
            informeDeTodosLosServicios(terminal);
            terminal.imprimirRecaudacionPorEmpresa();
        } catch (ServicioInexistenteException e) {
            System.out.println("[X]  " + e.getMessage());
        }

        titulo("5. BÚSQUEDA DE PASAJES");
        mostrarBusqueda(terminal, "30111222", "NEC-TRA-1400");
        mostrarBusqueda(terminal, "99999999", "NEC-TRA-1400");
    }

    // entrada de datos

    private static String leerTextoNoVacio(Scanner teclado, String pregunta) {
        while (true) {
            System.out.print(pregunta);
            String texto = teclado.nextLine().trim();
            if (!texto.isEmpty()) {
                return texto;
            }
            System.out.println("  El dato no puede estar vacío.");
        }
    }

    private static int leerEntero(Scanner teclado, String pregunta, int minimo) {
        while (true) {
            System.out.print(pregunta);
            String texto = teclado.nextLine().trim();
            try {
                int valor = Integer.parseInt(texto);
                if (valor >= minimo) {
                    return valor;
                }
                System.out.println("  Debe ser un número mayor o igual a " + minimo + ".");
            } catch (NumberFormatException e) {
                System.out.println("  '" + texto + "' no es un número entero válido.");
            }
        }
    }

    private static double leerPrecio(Scanner teclado, String pregunta) {
        while (true) {
            System.out.print(pregunta);
            String texto = teclado.nextLine().trim().replace(',', '.');
            try {
                double valor = Double.parseDouble(texto);
                if (valor >= 0) {
                    return valor;
                }
                System.out.println("  El precio no puede ser negativo.");
            } catch (NumberFormatException e) {
                System.out.println("  '" + texto + "' no es un precio válido.");
            }
        }
    }

    //formato

    private static void titulo(String texto) {
        System.out.println(linea('='));
        System.out.println(centrar(texto));
        System.out.println(linea('='));
    }

    private static String linea(char c) {
        return String.valueOf(c).repeat(ANCHO);
    }

    private static String centrar(String texto) {
        int espacios = Math.max(0, (ANCHO - texto.length()) / 2);
        return " ".repeat(espacios) + texto;
    }

    public static void main(String[] args) {
        Terminal terminal = new Terminal();

        // Carga de los servicios del dia
        cargarServicio(terminal, new Servicio("NEC-MDP-0700", "Costera", "Mar del Plata", "07:00", 4, 12000));
        cargarServicio(terminal, new Servicio("NEC-BA-2200", "Atlántica", "Buenos Aires", "22:00", 6, 38000));
        cargarServicio(terminal, new Servicio("NEC-TAN-0930", "Costera", "Tandil", "09:30", 3, 9500));
        cargarServicio(terminal, new Servicio("NEC-TRA-1400", "Atlántica", "Tres Arroyos", "14:00", 5, 21000));

        try (Scanner teclado = new Scanner(System.in)) {
            String opcion = "";
            while (!opcion.equals("0")) {
                mostrarMenu();
                if (!teclado.hasNextLine()) {
                    break;
                }
                opcion = teclado.nextLine().trim();
                System.out.println();
                switch (opcion) {
                    case "1":
                        agregarServicio(terminal, teclado);
                        break;
                    case "2":
                        cargarServiciosDelTP(terminal);
                        break;
                    case "3":
                        mostrarServicios(terminal);
                        break;
                    case "4":
                        procesarArchivo(terminal, teclado);
                        break;
                    case "5":
                        venderPasaje(terminal, teclado);
                        break;
                    case "6":
                        devolverPasaje(terminal, teclado);
                        break;
                    case "7":
                        buscarPasaje(terminal, teclado);
                        break;
                    case "8":
                        informePasajeros(terminal, teclado);
                        break;
                    case "9":
                        informeRecaudacion(terminal);
                        break;
                    case "10":
                        registrarReclamo(terminal, teclado);
                        break;
                    case "11":
                        atenderReclamo(terminal);
                        break;
                    case "12":
                        demostracion(terminal);
                        break;
                    case "0":
                        break;
                    default: System.out.println("Opcion invalida: '" + opcion + "'");
                }
            }
        }
        System.out.println();
        System.out.println("Programa finalizado.");
    }
}

