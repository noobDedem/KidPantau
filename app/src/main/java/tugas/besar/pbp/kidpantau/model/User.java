package tugas.besar.pbp.kidpantau.model;

public class User {
    private String nama;
    private String email;
    private double lattitude;
    private double longitude;
    private String time;
    private float color;

    public User(String nama, double lattitude, double longitude, String time,  float color) {
        this.nama = nama;
        this.lattitude = lattitude;
        this.longitude = longitude;
        this.time = time;
        this.color = color;
    }

    public User(String nama, String email, double lattitude, double longitude, String time, float color) {
        this.nama = nama;
        this.email = email;
        this.lattitude = lattitude;
        this.longitude = longitude;
        this.time = time;
        this.color = color;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public float getColor() {
        return color;
    }

    public void setColor(float color) {
        this.color = color;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public double getLattitude() {
        return lattitude;
    }

    public void setLattitude(double lattitude) {
        this.lattitude = lattitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}
