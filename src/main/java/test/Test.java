package test;

import entities.Machine;
import entities.Salle;
import services.MachineService;
import services.SalleService;

import java.util.Date;

public class Test {

    public static void main(String[] args) {
        SalleService salleService = new SalleService();
        MachineService machineService = new MachineService();

        // Création et insertion de salles et machines

        Salle salle1 = new Salle("S1");
        Salle salle2 = new Salle("S2");
        salleService.create(salle1);
        salleService.create(salle2);

        Machine machine1 = new Machine("M1", new Date(), salleService.findById(1));
        Machine machine2 = new Machine("M2", new Date(), salleService.findById(2));
        machineService.create(machine1);
        machineService.create(machine2);

        // Affiche les salles et leurs machines
        for(Salle salle : salleService.findAll()) {
            System.out.println("Salle: " + salle.getCode());
            for(Machine machine : salle.getMachines()) {
                System.out.println("  Machine: " + machine.getRef());
            }
        }

        //  la méthode findBetweenDate
        Date d1 = new Date(110, 0, 1);
        Date d2 = new Date();
        System.out.println("Machines achetées entre " + d1 + " et " + d2 + ":");
        for(Machine m : machineService.findBetweenDate(d1, d2)) {
            System.out.println(m.getRef() + " achetée le " + m.getDateAchat());
        }
    }
}