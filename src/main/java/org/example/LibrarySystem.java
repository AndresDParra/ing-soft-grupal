package org.example;

import java.util.HashMap;
import java.util.Map;

// ==========================================
// A) ENTITIES: Usuario, Ejemplar, Préstamo
// ==========================================

class Usuario {
    public final String id;
    public final String nombre;

    public Usuario(String id, String nombre) {
        this.id = id;
        this.nombre = nombre;
    }
}

class Ejemplar {
    public final String id;
    public final String titulo;
    public boolean disponible;

    public Ejemplar(String id, String titulo, boolean disponible) {
        this.id = id;
        this.titulo = titulo;
        this.disponible = disponible;
    }
}

class Prestamo {
    public final String id;
    public final Usuario usuario;
    public final Ejemplar ejemplar;
    public boolean activo;

    public Prestamo(String id, Usuario usuario, Ejemplar ejemplar) {
        this.id = id;
        this.usuario = usuario;
        this.ejemplar = ejemplar;
        this.activo = true;
    }
}

class Multa {
    public final Prestamo prestamo;
    public final double monto;
    public boolean pagada;

    public Multa(Prestamo prestamo, double monto) {
        this.prestamo = prestamo;
        this.monto = monto;
        this.pagada = false;
    }
}

// ==========================================
// CORE SYSTEM: Biblioteca Universitaria
// ==========================================
public class LibrarySystem {
    private Map<String, Usuario> usuarios = new HashMap<>();
    private Map<String, Ejemplar> catalogo = new HashMap<>();
    private Map<String, Prestamo> prestamos = new HashMap<>();
    
    private TransactionVerifier txVerifier;

    public LibrarySystem(TransactionVerifier txVerifier) {
        this.txVerifier = txVerifier;
    }

    public void registrarUsuario(Usuario u) { usuarios.put(u.id, u); }
    public void registrarEjemplar(Ejemplar e) { catalogo.put(e.id, e); }

    /**
     * A) Prestar Libro
     */
    public Prestamo prestarLibro(String prestamoId, String usuarioId, String ejemplarId) {
        System.out.println("\n--- Initiating Loan: " + prestamoId + " ---");
        Usuario usuario = usuarios.get(usuarioId);
        Ejemplar ejemplar = catalogo.get(ejemplarId);

        if (usuario == null || ejemplar == null || !ejemplar.disponible) {
            System.err.println("Loan Failed: User not found or Book Copy is unavailable.");
            return null;
        }

        Prestamo prestamo = new Prestamo(prestamoId, usuario, ejemplar);
        ejemplar.disponible = false; // Mark book as borrowed
        prestamos.put(prestamoId, prestamo);

        System.out.println("Success: Book '" + ejemplar.titulo + "' lent to " + usuario.nombre);
        return prestamo;
    }

    /**
     * B) Devolver y Calcular Multa (Pago)
     */
    public Multa devolverYCalcularMulta(String prestamoId, int diasRetraso, String transactionIdParaPago) {
        System.out.println("\n--- Initiating Return: " + prestamoId + " ---");
        Prestamo prestamo = prestamos.get(prestamoId);

        if (prestamo == null || !prestamo.activo) {
            System.err.println("Return Failed: Loan record not found or already closed.");
            return null;
        }

        // Process return
        prestamo.activo = false;
        prestamo.ejemplar.disponible = true;
        System.out.println("Book '" + prestamo.ejemplar.titulo + "' successfully returned by " + prestamo.usuario.nombre);

        // Calculate fine
        if (diasRetraso > 0) {
            double definemonto = diasRetraso * 2.50; // $2.50 per day
            Multa multa = new Multa(prestamo, definemonto);
            System.out.println("Fine calculated: $" + definemonto + " for " + diasRetraso + " days late.");

            if (transactionIdParaPago != null) {
                System.out.println("Verifying fine payment using Transaction ID: " + transactionIdParaPago + "...");
                
                // PAGO: Use Transaction Verifier to validate the fine payment
                boolean isPaymentValid = this.txVerifier.verifyFinality(transactionIdParaPago);
                
                if (isPaymentValid) {
                    multa.pagada = true;
                    System.out.println("Fine payment verified. Debt cleared for " + prestamo.usuario.nombre);
                } else {
                    System.err.println("Fine payment rejected. User still owes $" + definemonto);
                }
            } else {
                System.err.println("No payment provided. User owes $" + definemonto);
            }
            return multa;
        }

        System.out.println("Returned on time. No fines applied.");
        return null;
    }
}
