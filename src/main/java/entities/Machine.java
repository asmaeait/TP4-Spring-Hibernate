package entities;

import javax.persistence.*;
import java.util.Date;

@Entity
@NamedNativeQuery(name = "findBetweenDateNative", query = "select * from machine where dateAchat between :d1 and :d2", resultClass = Machine.class)
@NamedQuery(name = "findBetweenDate", query = "from Machine where dateAchat between :d1 and :d2")
public class Machine {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String Ref;

    @Temporal(TemporalType.DATE)
    private Date dateAchat;

    @ManyToOne
    private Salle salle;

    // Constructeurs, Getters & Setters

    // constructeur par defaut
    public Machine() {
    }

    // constructeur avec parametres
    public Machine(String ref, Date dateAchat, Salle salle) {
        this.Ref = ref;
        this.dateAchat = dateAchat;
        this.salle = salle;
    }

    // Getters and Setters

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getRef() {
        return Ref;
    }

    public void setRef(String ref) {
        Ref = ref;
    }

    public Date getDateAchat() {
        return dateAchat;
    }

    public void setDateAchat(Date dateAchat) {
        this.dateAchat = dateAchat;
    }

    public Salle getSalle() {
        return salle;
    }

    public void setSalle(Salle salle) {
        this.salle = salle;
    }
}
