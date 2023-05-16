package at.fhv.tvv.frontendteamd.model;

import java.text.SimpleDateFormat;
import java.util.Date;

public class EventList {
    private int eventId;
    private String name;
    private String veranstaltungsserie;
    private String datum;
    private String ort;
    private int plaetzeVerfuegbar;

    public EventList(int eventId, String name, String veranstaltungsserie, int datum, String ort, int plaetzeVerfuegbar) {
        this.eventId = eventId;
        this.name = name;
        Date date = new Date(datum*1000L);
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
        this.datum = sdf.format(date);
        this.ort = ort;
        this.plaetzeVerfuegbar = plaetzeVerfuegbar;
    }

    public int getEventId() {
        return eventId;
    }

    public String getName() {
        return name;
    }

    public String getVeranstaltungsserie() {
        return veranstaltungsserie;
    }

    public String getDatum() {
        return datum;
    }

    public String getOrt() {
        return ort;
    }

    public int getPlaetzeVerfuegbar() {
        return plaetzeVerfuegbar;
    }
}
