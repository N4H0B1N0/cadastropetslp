package modelo;

public class Raca {

    public static final String CACHORRO = "Cachorro";
    public static final String GATO = "Gato";

    private int id;
    private String nome;
    private String tipoAnimal;
    private boolean status = true;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getTipoAnimal() { return tipoAnimal; }
    public void setTipoAnimal(String tipoAnimal) { this.tipoAnimal = tipoAnimal; }
    public boolean isStatus() { return status; }
    public void setStatus(boolean status) { this.status = status; }

    @Override
    public String toString() {
        return nome + " (" + tipoAnimal + ")";
    }
}
