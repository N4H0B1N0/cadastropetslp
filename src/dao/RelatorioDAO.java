package dao;

import factory.ConnectionFactory;
import modelo.CpfValidador;
import modelo.Datas;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class RelatorioDAO {

    private static final String LINHA = "------------------------------------------------------------";

    public int gerarClientesEAnimais(Path arquivo) throws SQLException, IOException {
        String sql = "SELECT c.nome AS cliente, c.cpf, a.nome AS animal, r.nome_raca, a.data_nascimento "
                + "FROM cliente c LEFT JOIN animal a ON a.id_cliente = c.id_cliente AND a.status = TRUE "
                + "LEFT JOIN raca r ON r.id_raca = a.id_raca "
                + "WHERE c.status = TRUE ORDER BY c.nome, a.nome";
        int clientes = 0;
        try (Connection con = ConnectionFactory.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery();
             BufferedWriter w = Files.newBufferedWriter(arquivo, StandardCharsets.UTF_8)) {
            w.write("RELATÓRIO - TODOS OS CLIENTES E SEUS ANIMAIS");
            w.newLine();
            w.write(LINHA);
            w.newLine();
            String atual = null;
            while (rs.next()) {
                String cpf = rs.getString("cpf");
                if (!cpf.equals(atual)) {
                    atual = cpf;
                    clientes++;
                    w.newLine();
                    w.write("Cliente: " + rs.getString("cliente") + " | CPF: " + CpfValidador.formatar(cpf));
                    w.newLine();
                }
                String animal = rs.getString("animal");
                if (animal == null) {
                    w.write("   (sem animais cadastrados)");
                } else {
                    w.write("   Animal: " + animal + " | Raça: " + rs.getString("nome_raca")
                            + " | Nascimento: " + Datas.formatar(Datas.deSql(rs.getDate("data_nascimento"))));
                }
                w.newLine();
            }
        }
        return clientes;
    }

    public int gerarAnimaisAniversariantes(int mes, Path arquivo) throws SQLException, IOException {
        String sql = "SELECT a.nome AS animal, c.nome AS cliente, c.telefone, a.data_nascimento "
                + "FROM animal a JOIN cliente c ON c.id_cliente = a.id_cliente "
                + "WHERE a.status = TRUE AND c.status = TRUE AND MONTH(a.data_nascimento) = ? "
                + "ORDER BY DAY(a.data_nascimento), a.nome";
        int total = 0;
        try (Connection con = ConnectionFactory.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             BufferedWriter w = Files.newBufferedWriter(arquivo, StandardCharsets.UTF_8)) {
            ps.setInt(1, mes);
            w.write("RELATÓRIO - ANIMAIS ANIVERSARIANTES DO MÊS " + String.format("%02d", mes));
            w.newLine();
            w.write(LINHA);
            w.newLine();
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    total++;
                    w.write("Animal: " + rs.getString("animal") + " | Cliente: " + rs.getString("cliente")
                            + " | Telefone: " + valor(rs.getString("telefone"))
                            + " | Nascimento: " + Datas.formatar(Datas.deSql(rs.getDate("data_nascimento"))));
                    w.newLine();
                }
            }
            if (total == 0) {
                w.write("Nenhum animal aniversariante neste mês.");
                w.newLine();
            }
        }
        return total;
    }

    public int gerarClientesAniversariantes(int mes, Path arquivo) throws SQLException, IOException {
        String sql = "SELECT nome, cpf, data_nascimento, telefone FROM cliente "
                + "WHERE status = TRUE AND MONTH(data_nascimento) = ? ORDER BY DAY(data_nascimento), nome";
        int total = 0;
        try (Connection con = ConnectionFactory.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             BufferedWriter w = Files.newBufferedWriter(arquivo, StandardCharsets.UTF_8)) {
            ps.setInt(1, mes);
            w.write("RELATÓRIO - CLIENTES ANIVERSARIANTES DO MÊS " + String.format("%02d", mes));
            w.newLine();
            w.write(LINHA);
            w.newLine();
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    total++;
                    w.write("Nome: " + rs.getString("nome") + " | CPF: " + CpfValidador.formatar(rs.getString("cpf"))
                            + " | Nascimento: " + Datas.formatar(Datas.deSql(rs.getDate("data_nascimento")))
                            + " | Telefone: " + valor(rs.getString("telefone")));
                    w.newLine();
                }
            }
            if (total == 0) {
                w.write("Nenhum cliente aniversariante neste mês.");
                w.newLine();
            }
        }
        return total;
    }

    private String valor(String texto) {
        return texto == null || texto.isBlank() ? "-" : texto;
    }
}
