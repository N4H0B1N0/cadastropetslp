package modelo;

import java.time.LocalDate;
import java.time.Period;

public class Animal {

    private int id;
    private String nome;
    private LocalDate dataNascimento;
    private char sexo = 'M';
    private String cor;
    private String observacoes;
    private int idCliente;
    private int idRaca;
    private boolean status = true;
    private String nomeCliente;
    private String nomeRaca;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public LocalDate getDataNascimento() { return dataNascimento; }
    public void setDataNascimento(LocalDate dataNascimento) { this.dataNascimento = dataNascimento; }
    public char getSexo() { return sexo; }
    public void setSexo(char sexo) { this.sexo = sexo; }
    public String getCor() { return cor; }
    public void setCor(String cor) { this.cor = cor; }
    public String getObservacoes() { return observacoes; }
    public void setObservacoes(String observacoes) { this.observacoes = observacoes; }
    public int getIdCliente() { return idCliente; }
    public void setIdCliente(int idCliente) { this.idCliente = idCliente; }
    public int getIdRaca() { return idRaca; }
    public void setIdRaca(int idRaca) { this.idRaca = idRaca; }
    public boolean isStatus() { return status; }
    public void setStatus(boolean status) { this.status = status; }
    public String getNomeCliente() { return nomeCliente; }
    public void setNomeCliente(String nomeCliente) { this.nomeCliente = nomeCliente; }
    public String getNomeRaca() { return nomeRaca; }
    public void setNomeRaca(String nomeRaca) { this.nomeRaca = nomeRaca; }

    public String getIdade() {
        if (dataNascimento == null) {
            return "";
        }
        Period p = Period.between(dataNascimento, LocalDate.now());
        if (p.getYears() > 0) {
            return p.getYears() + (p.getYears() == 1 ? " ano" : " anos");
        }
        return p.getMonths() + (p.getMonths() == 1 ? " mês" : " meses");
    }
}
