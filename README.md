## Captures des résultats

### 1. Création des tables et des relations

Hibernate crée les tables `salles` et `reservations`, puis ajoute la contrainte d’unicité du nom et la clé étrangère reliant chaque réservation à sa salle.

<img width="1797" height="1022" alt="1" src="https://github.com/user-attachments/assets/a706dfb7-b76a-45e4-b7b1-2de940ef4086" />

### 2. Insertion des salles et des réservations

Le programme ajoute plusieurs salles et quelques réservations afin de comparer les créneaux disponibles avec les créneaux déjà occupés.

<img width="1797" height="1030" alt="2" src="https://github.com/user-attachments/assets/e54ef97d-7c53-44f7-b1fd-e4891e5e99a4" />

### 3. Recherche des salles disponibles avec JPQL

La requête JPQL exclut les salles dont une réservation chevauche le créneau demandé, de 09 h à 11 h.

<img width="1767" height="992" alt="3" src="https://github.com/user-attachments/assets/79146255-c5ef-4c32-a13a-4737e9f21744" />

### 4. Recherche multi-critères avec Criteria API

La requête applique les critères sur le nom, la capacité minimale et la disponibilité. Hibernate affiche également la requête de comptage utilisée pour la pagination.

<img width="1622" height="1017" alt="4" src="https://github.com/user-attachments/assets/626ec964-3448-48e8-bc0a-67cb61e33f59" />

### 5. Pagination des résultats

Les résultats sont répartis sur deux pages. La sortie affiche la deuxième page et le nombre total de salles correspondant aux critères. L’exécution du programme se termine sans erreur.

<img width="1727" height="990" alt="5" src="https://github.com/user-attachments/assets/066a3259-d537-497a-b5d9-7c0493bca511" />
