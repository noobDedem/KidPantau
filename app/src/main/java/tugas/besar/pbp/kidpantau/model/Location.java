package tugas.besar.pbp.kidpantau.model;

public class Location {
    private String namaTempat;
    private String waktu;

    public Location(String namaTempat, String waktu) {
        this.namaTempat = namaTempat;
        this.waktu = waktu;
    }

    public String getNamaTempat() {
        return namaTempat;
    }

    public void setNamaTempat(String namaTempat) {
        this.namaTempat = namaTempat;
    }

    public String getWaktu() {
        return waktu;
    }

    public void setWaktu(String waktu) {
        this.waktu = waktu;
    }
}
