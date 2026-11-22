package dao;

import factory.ConnectionFactory;
import modelo.Animal;
import modelo.CpfValidador;
import modelo.Datas;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

public class AnimalDAO {

    private static final String SELECT = "SELECT a.id_animal, a.nome, a.data_nascimento, a.sexo, a.cor, a.observacoes, "
            + "a.id_cliente, a.id_raca, a.status, c.nome AS nome_cliente, r.nome_raca "
            + "FROM animal a JOIN cliente c ON c.id_cliente = a.id_cliente JOIN raca r ON r.id_raca = a.id_raca ";

    public void salvar(Animal a) throws SQLException {
        if (a.getNome() == null || a.getNome().isBlank()) {
            throw new IllegalArgumentException("O nome do animal é obrigatório.");
        }
        if (a.getIdCliente() == 0) {
            throw new IllegalArgumentException("O animal deve estar vinculado a um cliente.");
        }
        if (a.getIdRaca() == 0) {
            throw new IllegalArgumentException("O animal deve possuir uma raça cadastrada.");
        }
        if (a.getId() == 0) {
            inserir(a);
        } else {
            alterar(a);
        }
    }

    private void inserir(Animal a) throws SQLException {
        String sql = "INSERT INTO animal (nome, data_nascimento, sexo, cor, observacoes, id_cliente, id_raca, status) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection con = ConnectionFactory.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            preencher(ps, a);
            ps.setBoolean(8, a.isStatus());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    a.setId(rs.getInt(1));
                }
            }
        }
    }

    private void alterar(Animal a) throws SQLException {
        String sql = "UPDATE animal SET nome = ?, data_nascimento = ?, sexo = ?, cor = ?, observacoes = ?, id_cliente = ?, id_raca = ? WHERE id_animal = ?";
        try (Connection con = ConnectionFactory.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            preencher(ps, a);
            ps.setInt(8, a.getId());
            ps.executeUpdate();
        }
    }

    private void preencher(PreparedStatement ps, Animal a) throws SQLException {
        ps.setString(1, a.getNome().trim());
        if (a.getDataNascimento() == null) {
            ps.setNull(2, Types.DATE);
        } else {
            ps.setDate(2, Datas.paraSql(a.getDataNascimento()));
        }
        ps.setString(3, String.valueOf(a.getSexo()));
        ps.setString(4, a.getCor());
        ps.setString(5, a.getObservacoes());
        ps.setInt(6, a.getIdCliente());
        ps.setInt(7, a.getIdRaca());
    }

    public void alterarStatus(int id, boolean status) throws SQLException {
        String sql = "UPDATE animal SET status = ? WHERE id_animal = ?";
        try (Connection con = ConnectionFactory.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setBoolean(1, status);
            ps.setInt(2, id);
            ps.executeUpdate();
        }
    }

    public List<Animal> listar(boolean incluirInativos) throws SQLException {
        String sql = SELECT + (incluirInativos ? "" : "WHERE a.status = TRUE AND c.status = TRUE ") + "ORDER BY a.nome";
        List<Animal> lista = new ArrayList<>();
        try (Connection con = ConnectionFactory.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(mapear(rs));
            }
        }
        return lista;
    }

    public List<Animal> consultarPorCliente(String nomeCliente, String cpf, boolean incluirInativos) throws SQLException {
        String sql = SELECT + "WHERE c.nome LIKE ? AND c.cpf LIKE ? "
                + (incluirInativos ? "" : "AND a.status = TRUE AND c.status = TRUE ") + "ORDER BY c.nome, a.nome";
        List<Animal> lista = new ArrayList<>();
        try (Connection con = ConnectionFactory.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, "%" + (nomeCliente == null ? "" : nomeCliente.trim()) + "%");
            ps.setString(2, "%" + CpfValidador.somenteDigitos(cpf) + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapear(rs));
                }
            }
        }
        return lista;
    }

    private Animal mapear(ResultSet rs) throws SQLException {
        Animal a = new Animal();
        a.setId(rs.getInt("id_animal"));
        a.setNome(rs.getString("nome"));
        a.setDataNascimento(Datas.deSql(rs.getDate("data_nascimento")));
        String sexo = rs.getString("sexo");
        a.setSexo(sexo == null || sexo.isEmpty() ? 'M' : sexo.charAt(0));
        a.setCor(rs.getString("cor"));
        a.setObservacoes(rs.getString("observacoes"));
        a.setIdCliente(rs.getInt("id_cliente"));
        a.setIdRaca(rs.getInt("id_raca"));
        a.setStatus(rs.getBoolean("status"));
        a.setNomeCliente(rs.getString("nome_cliente"));
        a.setNomeRaca(rs.getString("nome_raca"));
        return a;
    }
}
