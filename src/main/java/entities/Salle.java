package entities;


import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "salles")
public class Salle {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String code;

    // FetchType.EAGER : signifie que les machines sont recuperees immediatement avec la salle
    @OneToMany(mappedBy = "salle", fetch = FetchType.EAGER)
    private List<Machine> machines;

    // Constructeur par default

    public Salle() {
    }

    //Constructeur avec parametres

    public Salle(String code, List<Machine> machines) {
        this.code = code;
        this.machines = machines;
    }

    public Salle(String code) {
        this.code = code;
    }

    // Getters & Setters


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCode() {

        return code;
    }

    public void setCode(String code) {

        this.code = code;
    }

    // getter & setter for the Machine

    public List<Machine> getMachines() {
        return machines;
    }

    public void setMachines(List<Machine> machines) {

        this.machines = machines;
    }
}
