# TP4 — Hibernate & MySQL : Persistance des données en Java

Ce TP consiste à construire une application Java avec **Hibernate** pour gérer la persistance des données dans une base **MySQL**. Voici le parcours complet de ce que j'ai réalisé, étape par étape.

---

## 📁 Structure du projet

```
TP4-Spring/
├── src/main/java/
│   ├── dao/
│   │   └── IDao.java
│   ├── entities/
│   │   ├── Salle.java
│   │   └── Machine.java
│   ├── services/
│   │   ├── SalleService.java
│   │   └── MachineService.java
│   ├── util/
│   │   └── HibernateUtil.java
│   └── test/
│       ├── Test.java
│       ├── SalleServiceTest.java
│       └── MachineServiceTest.java
└── src/main/resources/
    └── hibernate.cfg.xml
```

---

## Étape 1 — Configuration de Maven (`pom.xml`)

La première chose que j'ai faite c'est configurer les dépendances Maven nécessaires au projet :

- `hibernate-core` — le moteur ORM
- `mysql-connector-java` — le driver JDBC pour MySQL 8
- `javax.persistence-api` — les annotations JPA standard
- `junit` — pour les tests unitaires

> ⚠️ **Problème rencontré :** JUnit était configuré avec `<scope>test</scope>`, ce qui le rendait inaccessible depuis `src/main/java` où se trouvent mes fichiers de test. J'ai retiré cette ligne pour résoudre le problème.

---

## Étape 2 — Interface DAO générique

J'ai créé une interface générique `IDao<T>` qui définit les 5 opérations CRUD communes à toutes les entités :

```java
public interface IDao<T> {
    boolean create(T o);
    boolean delete(T o);
    boolean update(T o);
    T findById(int id);
    List<T> findAll();
}
```

Cela permet d'avoir un contrat commun que chaque service devra implémenter.

---

## Étape 3 — Création des entités JPA

J'ai modélisé deux entités avec une relation **OneToMany / ManyToOne** :

### `Salle`
- Mappée sur la table `salles` via `@Table(name = "salles")`
- Contient une liste de machines chargée en mode `EAGER`

### `Machine`
- Reliée à une salle via `@ManyToOne`
- Possède une date d'achat avec `@Temporal(TemporalType.DATE)`
- Déclare deux requêtes nommées pour rechercher par intervalle de dates :
  - `@NamedQuery` — en HQL
  - `@NamedNativeQuery` — en SQL natif

**Relation :**
```
Salle (1) ───────── (*) Machine
```

---

## Étape 4 — Configuration de Hibernate (`hibernate.cfg.xml`)

J'ai configuré la connexion à MySQL dans le fichier `hibernate.cfg.xml` :

```xml
<property name="hibernate.dialect">org.hibernate.dialect.MySQL8Dialect</property>
<property name="hibernate.connection.driver_class">com.mysql.cj.jdbc.Driver</property>
<property name="hibernate.connection.url">
    jdbc:mysql://localhost:3306/tp4-spring-hibernate?zeroDateTimeBehavior=CONVERT_TO_NULL&amp;useSSL=false&amp;serverTimezone=UTC
</property>
<property name="hibernate.hbm2ddl.auto">update</property>
<property name="hibernate.show_sql">true</property>
```

> ⚠️ **Problème rencontré :** les paramètres de l'URL étaient collés sans séparateur, ce qui causait l'erreur :
> `The value 'convertToNulluseSSL=falseserverTimezone=UTC' is not acceptable`
>
> **Fix :** séparer chaque paramètre avec `&amp;` et passer `zeroDateTimeBehavior` en majuscules : `CONVERT_TO_NULL`.

> ⚠️ **Deuxième problème :** `Access denied for user 'root'@'localhost'` — le mot de passe dans le fichier ne correspondait pas à celui de MySQL. Corrigé en mettant le bon mot de passe.

---

## 🗄️ Base de données & Tables générées

La base de données utilisée est `tp4-spring-hibernate`. Hibernate génère automatiquement les tables grâce à `hbm2ddl.auto=update`.

### Tables créées

---

#### Relation entre les tables

La colonne `salle_id` dans `Machine` est une clé étrangère qui référence `salles(id)`, ce qui représente la relation **OneToMany** définie dans les entités JPA.

<img width="812" height="444" alt="image" src="https://github.com/user-attachments/assets/bae532ec-038b-4732-b9c3-b39929f5835a" />

---

#### Données insérées

![Données dans salles](images/data_salles.png)

![Données dans Machine](images/data_machine.png)

---

## Étape 5 — Classe utilitaire `HibernateUtil`

J'ai créé une classe utilitaire qui gère la `SessionFactory` en singleton via un bloc `static` :

```java
static {
    sessionFactory = new Configuration().configure().buildSessionFactory();
}
```

Cette classe est utilisée par tous les services pour ouvrir des sessions vers la base de données.

---

## Étape 6 — Couche Service

J'ai implémenté `SalleService` et `MachineService` qui implémentent tous les deux `IDao<T>`.

Chaque méthode suit le même patron :

```
Ouvrir session → Démarrer transaction → Opération → Commit → [Rollback si erreur] → Fermer session
```

`MachineService` ajoute en plus la méthode `findBetweenDate(Date d1, Date d2)` qui utilise la `@NamedQuery` définie dans l'entité `Machine`.

---

## Étape 7 — Test de la persistance (`Test.java`)

J'ai écrit une classe `Test` avec une méthode `main` pour vérifier que tout fonctionne :

1. Création de deux salles `S1` et `S2`
2. Création de deux machines associées à ces salles
3. Affichage de toutes les salles avec leurs machines
4. Recherche des machines achetées entre deux dates

**Résultat en console :**

```
Salle: S1
  Machine: M1
Salle: S2
  Machine: M2
Machines achetées entre ... et ...:
M1 achetée le 2026-03-11
M2 achetée le 2026-03-11

Process finished with exit code 0
```

Les tables `salles` et `Machine` ont été créées automatiquement par Hibernate grâce à `hbm2ddl.auto=update`.

---

## Étape 8 — Tests unitaires JUnit

J'ai écrit des tests unitaires pour valider chaque opération CRUD sur `Salle` et `Machine`.

Chaque classe de test utilise :
- `@Before` pour créer les données avant chaque test
- `@After` pour nettoyer la base après chaque test

| Test | Ce qui est vérifié |
|---|---|
| `testCreate()` | L'objet est persisté et possède un ID |
| `testFindById()` | L'objet est retrouvé par son ID |
| `testUpdate()` | La modification est bien sauvegardée |
| `testDelete()` | L'objet n'existe plus après suppression |
| `testFindAll()` | La liste retournée n'est pas vide |
| `testFindBetweenDate()` | La requête par dates retourne les bons résultats |

---

## ▶️ Comment lancer le projet

### Prérequis
- Java 8+
- MySQL 8 actif sur `localhost:3306`
- Maven installé

### Étapes

```bash
# 1. Créer la base de données
mysql -u root -p -e "CREATE DATABASE \`tp4-spring-hibernate\`;"

# 2. Compiler
mvn clean install

# 3. Lancer
# Exécuter la classe test.Test depuis IntelliJ
```

> La base de données et les tables sont créées automatiquement par Hibernate au premier lancement.
