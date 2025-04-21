import java.util.Scanner;

public class EXP2_S6_Keneth_Novoa {
    static final int FILAS = 3;
    static final int COLUMNAS = 5;
    static Cliente[][] asientos = new Cliente[FILAS][COLUMNAS];
    static Scanner scanner = new Scanner(System.in);

    // Variables estáticas para estadísticas globales
    static int totalEntradasVendidas = 0;
    static double totalIngresos = 0.0;
    static int totalEntradasReservadas = 0;

    public static void main(String[] args) throws Exception {
        int opcion;
        do {
            System.out.println("\n===== TEATRO MORO MENU PRINCIPAL =====");
            System.out.println("1. Reservar entrada");
            System.out.println("2. Comprar entrada");
            System.out.println("3. Modificar asiento comprado");
            System.out.println("4. Imprimir boleta");
            System.out.println("5. Ver plano del teatro");
            System.out.println("6. Salir");
            System.out.print("Seleccione una opcion: ");
            opcion = leerEntero();

            switch (opcion) {
                case 1:
                    reservarEntrada();// Breakpoint 1
                    break;
                case 2:
                    comprarEntrada();// Breakpoint 2
                    break;
                case 3:
                    modificarAsiento();
                    break;
                case 4:
                    verBoleta();// Breakpoint 3
                    break;
                case 5:
                    mostrarPlano();
                    break;
                case 6:
                    System.out.println("Gracias por usar el sistema.");
                    break;
                default:
                    System.out.println("Opcion no valida.");
            }
        } while (opcion != 6);
    }

    static void reservarEntrada() {
        System.out.print("Ingrese su nombre: ");
        String nombre = scanner.next();
        System.out.print("Ingrese su edad: ");
        int edad = leerEntero();
        mostrarPlano();
        System.out.print("Ingrese fila (1 a 3): ");
        int fila = leerEntero() - 1;
        System.out.print("Ingrese columna (1 a 5): ");
        int columna = leerEntero() - 1;

        if (esValido(fila, columna) && asientos[fila][columna] == null) { // Breakpoint 4
            Cliente cliente = new Cliente(nombre, edad, fila, columna, 0, "Reservado");
            asientos[fila][columna] = cliente;
            totalEntradasReservadas++; // Incrementamos el contador de entradas reservadas
            System.out.println("Asiento reservado con exito.\nTiene 10 segundos para confirmar la compra...");

            final int tiempoLimite = 10;
            System.out.println("Desea pagar? seleccione 'si' o 'no'");

            long inicio = System.currentTimeMillis();
            boolean finIngreso = false;
            String pagar = "no";

            while ((System.currentTimeMillis() - inicio) < tiempoLimite * 1000) {
                try {
                    if (System.in.available() > 0) {
                        pagar = scanner.next();// Breakpoint 5
                        finIngreso = true;
                        break;
                    }
                    Thread.sleep(100);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            if (!finIngreso) {
                System.out.println("Tiempo de espera agotado, se cancelo la compra.");
                asientos[fila][columna] = null;
                totalEntradasReservadas--; // Decrementamos las entradas reservadas si la compra es cancelada
                return;
            }

            if (pagar.equalsIgnoreCase("si")) {
                double precioBase = calcularPrecioBase(fila);
                double descuento = calcularDescuento(precioBase, edad);
                double iva = 0.19 * (precioBase - descuento); // Breakpoint 6
                double precioFinal = precioBase - descuento + iva;

                cliente.precio = precioFinal;
                cliente.estado = "Comprado";
                asientos[fila][columna] = cliente;
                totalEntradasVendidas++; // Incrementamos las entradas vendidas
                totalIngresos += precioFinal; // Sumamos el precio de la venta al total de ingresos
                System.out.println("Compra realizada con exito.");
            } else {
                asientos[fila][columna] = null;
                System.out.println("Compra cancelada por el usuario.");
            }
        } else {
            System.out.println("Asiento no disponible.");
        }
    }

    static void comprarEntrada() {
        System.out.print("Ingrese su nombre: ");
        String nombre = scanner.next();
        System.out.print("Ingrese su edad: ");
        int edad = leerEntero();
        mostrarPlano();
        System.out.print("Ingrese fila (1 a 3): ");
        int fila = leerEntero() - 1;
        System.out.print("Ingrese columna (1 a 5): ");
        int columna = leerEntero() - 1;

        if (esValido(fila, columna) && asientos[fila][columna] == null) {
            double precioBase = calcularPrecioBase(fila);
            double descuento = calcularDescuento(precioBase, edad);
            double iva = 0.19 * (precioBase - descuento);
            double precioFinal = precioBase - descuento + iva;

            Cliente cliente = new Cliente(nombre, edad, fila, columna, precioFinal, "Comprado");
            asientos[fila][columna] = cliente;
            totalEntradasVendidas++; // Incrementamos las entradas vendidas
            totalIngresos += precioFinal; // Sumamos el precio de la venta al total de ingresos
        } else {
            System.out.println("Asiento no disponible.");
        }
    }

    static void modificarAsiento() {
        System.out.print("Ingrese su nombre: ");
        String nombre = scanner.next();

        Cliente cliente = buscarClientePorNombre(nombre);
        if (cliente != null && cliente.estado.equals("Comprado")) {
            System.out.print("Ingrese nueva fila (1 a 3): ");
            int nuevaFila = leerEntero() - 1;
            System.out.print("Ingrese nueva columna (1 a 5): ");
            int nuevaColumna = leerEntero() - 1;

            if (esValido(nuevaFila, nuevaColumna) && asientos[nuevaFila][nuevaColumna] == null) {
                asientos[cliente.fila][cliente.columna] = null;
                cliente.fila = nuevaFila;
                cliente.columna = nuevaColumna;
                asientos[nuevaFila][nuevaColumna] = cliente;
            } else {
                System.out.println("Nuevo asiento no disponible.");
            }
        } else {
            System.out.println("Cliente no encontrado o no tiene una compra.");
        }
    }

    static void verBoleta() {
        System.out.print("Ingrese su nombre: ");
        String nombre = scanner.next();

        Cliente cliente = buscarClientePorNombre(nombre);
        if (cliente != null) {
            System.out.println("===== BOLETA =====");
            System.out.println("Nombre: " + cliente.nombre);
            System.out.println("Edad: " + cliente.edad);
            System.out.println("Ubicacion: Fila " + (cliente.fila + 1) + ", Columna " + (cliente.columna + 1));
            System.out.println("Estado: " + cliente.estado);
            System.out.printf("Precio total: $%.0f\n", cliente.precio);
        } else {
            System.out.println("Cliente no encontrado.");
        }
    }

    static void mostrarPlano() {
        System.out.println("Plano del Teatro (R = Reservado, C = Comprado, _ = Libre):");
        System.out.print("     ");
        for (int j = 0; j < COLUMNAS; j++) {
            System.out.print("   " + (j + 1) + "   ");
        }
        System.out.println();
        for (int i = 0; i < FILAS; i++) {
            System.out.print("Fila " + (i + 1) + ": ");
            for (int j = 0; j < COLUMNAS; j++) {
                if (asientos[i][j] == null) {
                    System.out.print("[ _ ] ");
                } else if (asientos[i][j].estado.equals("Reservado")) {
                    System.out.print("[ R ] ");
                } else {
                    System.out.print("[ C ] ");
                }
            }
            System.out.println();
        }
    }

    static int leerEntero() {
        while (!scanner.hasNextInt()) {
            System.out.print("Por favor ingrese un numero valido: ");
            scanner.next();
        }
        return scanner.nextInt();
    }

    static boolean esValido(int fila, int columna) {
        return fila >= 0 && fila < FILAS && columna >= 0 && columna < COLUMNAS;
    }

    static Cliente buscarClientePorNombre(String nombre) {
        for (int i = 0; i < FILAS; i++) {
            for (int j = 0; j < COLUMNAS; j++) { // Breakpoint 7
                if (asientos[i][j] != null && asientos[i][j].nombre.equalsIgnoreCase(nombre)) {
                    return asientos[i][j];
                }
            }
        }
        return null;
    }

    static double calcularPrecioBase(int fila) {
        if (fila == 0) return 20000;
        if (fila == 1) return 15000;
        return 10000;
    }

    static double calcularDescuento(double precio, int edad) {
        if (edad <= 18 || edad >= 60) {
            return precio * 0.20;
        }
        return 0;
    }
}

class Cliente {
    String nombre;
    int edad;
    int fila;
    int columna;
    double precio;
    String estado;

    public Cliente(String nombre, int edad, int fila, int columna, double precio, String estado) {
        this.nombre = nombre;
        this.edad = edad;
        this.fila = fila;
        this.columna = columna;
        this.precio = precio;
        this.estado = estado;
    }
}
