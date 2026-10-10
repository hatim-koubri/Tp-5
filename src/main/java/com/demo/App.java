package com.demo;

import com.demo.entites.Reservation;
import com.demo.entites.Salle;
import com.demo.service.SalleService;
import com.demo.service.SalleServiceImpl;
import com.demo.util.PaginationResult;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.time.LocalDateTime;
import java.util.List;

public class App {
    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("SallePU");
        EntityManager em = emf.createEntityManager();
        try {
            insererExemples(em);
            SalleService service = new SalleServiceImpl(em);

            LocalDateTime debut = LocalDateTime.of(2026, 10, 15, 9, 0);
            LocalDateTime fin = LocalDateTime.of(2026, 10, 15, 11, 0);

            System.out.println("\n--- Salles disponibles de 09:00 a 11:00 (JPQL) ---");
            List<Salle> disponibles = service.getAvailableRooms(debut, fin);
            afficher(disponibles);

            System.out.println("\n--- Recherche Criteria : nom contenant 'a', capacite >= 8 ---");
            PaginationResult<Salle> page = service.searchRooms("a", 8, debut, fin, 1, 2);
            System.out.println("Page " + page.getCurrentPage() + "/" + page.getTotalPages()
                    + " | resultats : " + page.getTotalItems());
            afficher(page.getItems());

            PaginationResult<Salle> pageSuivante = service.searchRooms("a", 8, debut, fin, 2, 2);
            System.out.println("\n--- Page suivante ---");
            System.out.println("Page " + pageSuivante.getCurrentPage() + "/" + pageSuivante.getTotalPages());
            afficher(pageSuivante.getItems());

            long total = service.countRooms("a", 8, debut, fin);
            System.out.println("\nNombre total correspondant aux filtres : " + total);
        } finally {
            em.close();
            emf.close();
        }
    }

    private static void insererExemples(EntityManager em) {
        LocalDateTime jour = LocalDateTime.of(2026, 10, 15, 0, 0);
        Salle noria = new Salle("Atelier Noria", 12);
        noria.ajouterReservation(new Reservation(jour.withHour(8).withMinute(30), jour.withHour(9).withMinute(30)));

        Salle atlas = new Salle("Studio Atlas", 8);
        // Cette reservation finit a 11h : elle ne chevauche pas le creneau demande.
        atlas.ajouterReservation(new Reservation(jour.withHour(11), jour.withHour(12).withMinute(30)));

        Salle jasmin = new Salle("Salle Jasmin", 20);
        jasmin.ajouterReservation(new Reservation(jour.withHour(10), jour.withHour(11)));

        Salle zellige = new Salle("Espace Zellige", 10);
        Salle andalousie = new Salle("Salle Andalousie", 14);
        Salle tifawine = new Salle("Atelier Tifawine", 16);
        Salle bab = new Salle("Salle Amal", 5);

        em.getTransaction().begin();
        em.persist(noria);
        em.persist(atlas);
        em.persist(jasmin);
        em.persist(zellige);
        em.persist(andalousie);
        em.persist(tifawine);
        em.persist(bab);
        em.getTransaction().commit();
    }

    private static void afficher(List<Salle> salles) {
        for (Salle salle : salles) {
            System.out.println("- " + salle.getNom() + " (capacite : " + salle.getCapacite() + ")");
        }
    }
}