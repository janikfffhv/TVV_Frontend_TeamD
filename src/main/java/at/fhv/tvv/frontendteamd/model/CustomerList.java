package at.fhv.tvv.frontendteamd.model;

import java.util.UUID;

public class CustomerList {
    private UUID id;
    private String name;
    private String geburtsdatum;
    private String adresse;

    public CustomerList(UUID id, String name, String geburtsdatum, String adresse) {
        this.id = id;
        this.name = name;
        this.geburtsdatum = geburtsdatum;
        this.adresse = adresse;
    }
    public UUID getId() {
        return id;
    }
    public String getName() {
        return name;
    }

    public String getGeburtsdatum() {
        return geburtsdatum;
    }

    public String getAdresse() {
        return adresse;
    }
}
