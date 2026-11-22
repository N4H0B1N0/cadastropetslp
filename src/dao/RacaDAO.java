package dao;

import factory.ConnectionFactory;
import modelo.Raca;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class RacaDAO {

    public void salvar(Raca r) throws SQLException {
        validar(r);
        if (r.getId() == 0) {
            inserir(r);
        } else {
            alterar(r);
        }
    }

    private void validar(Raca r) throws SQLException {
        if (r.getNome() == null || r.getNome().isBlank()) {
            throw new IllegalArgumentException("O nome da raça é obrigatório.");
        }
        if (!Raca.CACHORRO.equals(r.getTipoAnimal()) && !Raca.GATO.equals(r.getTipoAnimal())) {
            throw new IllegalArgumentException("O tipo do animal deve ser Cachorro ou Gato.");
        }
        if (duplicada(r)) {
            throw new IllegalArgumentException("Já existe esta raça cadastrada para o tipo " + r.getTipoAnimal() + ".");
        }
    }

    private boolean duplicada(Raca r) throws SQLException {
        String sql = "SELECT 1 FROM raca WHERE nome_raca = ? AND tipo_animal = ? AND id_raca <> ?";
        try (Connection con = ConnectionFactory.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, r.getNome().trim());
            ps.setString(2, r.getTipoAnimal());
            ps.setInt(3, r.getId());
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    private void inserir(Raca r) throws SQLException {
        String sql = "INSERT INTO raca (nome_raca, tipo_animal, status) VALUES (?, ?, ?)";
        try (Connection con = ConnectionFactory.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, r.getNome().trim());
            ps.setString(2, r.getTipoAnimal());
            ps.setBoolean(3, r.isStatus());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    r.setId(rs.getInt(1));
                }
            }
        }
    }

    private void alterar(Raca r) throws SQLException {
        String sql = "UPDATE raca SET nome_raca = ?, tipo_animal = ? WHERE id_raca = ?";
        try (Connection con = ConnectionFactory.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, r.getNome().trim());
            ps.setString(2, r.getTipoAnimal());
            ps.setInt(3, r.getId());
            ps.executeUpdate();
        }
    }

    public void alterarStatus(int id, boolean status) throws SQLException {
        String sql = "UPDATE raca SET status = ? WHERE id_raca = ?";
        try (Connection con = ConnectionFactory.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setBoolean(1, status);
            ps.setInt(2, id);
            ps.executeUpdate();
        }
    }

    public List<Raca> listar(boolean incluirInativos) throws SQLException {
        String sql = "SELECT id_raca, nome_raca, tipo_animal, status FROM raca"
                + (incluirInativos ? "" : " WHERE status = TRUE") + " ORDER BY tipo_animal, nome_raca";
        List<Raca> lista = new ArrayList<>();
        try (Connection con = ConnectionFactory.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Raca r = new Raca();
                r.setId(rs.getInt("id_raca"));
                r.setNome(rs.getString("nome_raca"));
                r.setTipoAnimal(rs.getString("tipo_animal"));
                r.setStatus(rs.getBoolean("status"));
                lista.add(r);
            }
        }
        return lista;
    }
}
