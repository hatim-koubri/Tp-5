package com.demo.repository;

import com.demo.entites.Reservation;
import com.demo.entites.Salle;
import com.demo.util.PaginationResult;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class SalleRepositoryImpl implements SalleRepository {
    private final EntityManager entityManager;

    public SalleRepositoryImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public List<Salle> findAvailableRooms(LocalDateTime debut, LocalDateTime fin) {
        validerCreneau(debut, fin);
        String jpql = "SELECT s FROM Salle s WHERE NOT EXISTS (" +
                "SELECT r.id FROM Reservation r WHERE r.salle = s " +
                "AND :debut < r.fin AND :fin > r.debut) ORDER BY s.nom";

        return entityManager.createQuery(jpql, Salle.class)
                .setParameter("debut", debut)
                .setParameter("fin", fin)
                .getResultList();
    }

    @Override
    public PaginationResult<Salle> searchRooms(String nom, Integer capaciteMin,
                                               LocalDateTime debut, LocalDateTime fin,
                                               int page, int size) {
        verifierPage(page, size);
        validerCreneauOptionnel(debut, fin);

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Salle> dataQuery = cb.createQuery(Salle.class);
        Root<Salle> salle = dataQuery.from(Salle.class);
        List<Predicate> predicates = construireFiltres(cb, dataQuery, salle, nom, capaciteMin, debut, fin);
        dataQuery.select(salle).where(predicates.toArray(new Predicate[0]));
        dataQuery.orderBy(cb.asc(salle.get("nom")), cb.asc(salle.get("id")));

        TypedQuery<Salle> query = entityManager.createQuery(dataQuery);
        query.setFirstResult((page - 1) * size);
        query.setMaxResults(size);

        long total = countRooms(nom, capaciteMin, debut, fin);
        return new PaginationResult<>(query.getResultList(), page, size, total);
    }

    @Override
    public long countRooms(String nom, Integer capaciteMin,
                           LocalDateTime debut, LocalDateTime fin) {
        validerCreneauOptionnel(debut, fin);
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<Salle> salle = countQuery.from(Salle.class);
        List<Predicate> predicates = construireFiltres(cb, countQuery, salle, nom, capaciteMin, debut, fin);
        countQuery.select(cb.count(salle)).where(predicates.toArray(new Predicate[0]));
        return entityManager.createQuery(countQuery).getSingleResult();
    }

    private List<Predicate> construireFiltres(CriteriaBuilder cb, CriteriaQuery<?> query,
                                               Root<Salle> salle, String nom, Integer capaciteMin,
                                               LocalDateTime debut, LocalDateTime fin) {
        List<Predicate> predicates = new ArrayList<>();
        if (nom != null && !nom.trim().isEmpty()) {
            String motif = "%" + nom.trim().toLowerCase(Locale.ROOT) + "%";
            predicates.add(cb.like(cb.lower(salle.<String>get("nom")), motif));
        }
        if (capaciteMin != null) {
            predicates.add(cb.ge(salle.<Integer>get("capacite"), capaciteMin));
        }
        if (debut != null) {
            Subquery<Long> conflit = query.subquery(Long.class);
            Root<Reservation> reservation = conflit.from(Reservation.class);
            conflit.select(reservation.<Long>get("id"));
            conflit.where(cb.and(
                    cb.equal(reservation.get("salle"), salle),
                    cb.lessThan(reservation.<LocalDateTime>get("debut"), fin),
                    cb.greaterThan(reservation.<LocalDateTime>get("fin"), debut)
            ));
            predicates.add(cb.not(cb.exists(conflit)));
        }
        return predicates;
    }

    private void verifierPage(int page, int size) {
        if (page < 1 || size < 1) {
            throw new IllegalArgumentException("La page et la taille doivent etre superieures a zero.");
        }
    }

    private void validerCreneauOptionnel(LocalDateTime debut, LocalDateTime fin) {
        if ((debut == null) != (fin == null)) {
            throw new IllegalArgumentException("Il faut fournir les deux dates du creneau.");
        }
        if (debut != null) validerCreneau(debut, fin);
    }

    private void validerCreneau(LocalDateTime debut, LocalDateTime fin) {
        if (debut == null || fin == null || !debut.isBefore(fin)) {
            throw new IllegalArgumentException("Le debut doit preceder la fin du creneau.");
        }
    }
}