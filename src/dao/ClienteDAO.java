package dao;

import factory.ConnectionFactory;
import modelo.Cliente;
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

public class ClienteDAO {

    private static final String COLUNAS = "id_cliente, nome, cpf, data_nascimento, telefone, endereco, bairro, cidade, estado, cep, status";

    public void salvar(Cliente c) throws SQLException {
        validar(c);
        if (c.getId() == 0) {
            inserir(c);
        } else {
            alterar(c);
        }
    }

    private void validar(Cliente c) throws SQLException {
        if (c.getNome() == null || c.getNome().isBlank()) {
            throw new IllegalArgumentException("O nome é obrigatório.");
        }
        if (!CpfValidador.validar(c.getCpf())) {
            throw new IllegalArgumentException("CPF inválido.");
        }
        if (cpfDuplicado(c.getCpf(), c.getId())) {
            throw new IllegalArgumentException("Já existe um cliente cadastrado com este CPF.");
        }
    }

    private boolean cpfDuplicado(String cpf, int idIgnorado) throws SQLException {
        String sql = "SELECT 1 FROM cliente WHERE cpf = ? AND id_cliente <> ?";
        try (Connection con = ConnectionFactory.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, cpf);
            ps.setInt(2, idIgnorado);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    private void inserir(Cliente c) throws SQLException {
        String sql = "INSERT INTO cliente (nome, cpf, data_nascimento, telefone, endereco, bairro, cidade, estado, cep, status) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection con = ConnectionFactory.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            preencher(ps, c);
            ps.setBoolean(10, c.isStatus());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    c.setId(rs.getInt(1));
                }
            }
        }
    }

    private void alterar(Cliente c) throws SQLException {
        String sql = "UPDATE cliente SET nome = ?, cpf = ?, data_nascimento = ?, telefone = ?, endereco = ?, bairro = ?, cidade = ?, estado = ?, cep = ? WHERE id_cliente = ?";
        try (Connection con = ConnectionFactory.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            preencher(ps, c);
            ps.setInt(10, c.getId());
            ps.executeUpdate();
        }
    }

    private void preencher(PreparedStatement ps, Cliente c) throws SQLException {
        ps.setString(1, c.getNome().trim());
        ps.setString(2, c.getCpf());
        if (c.getDataNascimento() == null) {
            ps.setNull(3, Types.DATE);
        } else {
            ps.setDate(3, Datas.paraSql(c.getDataNascimento()));
        }
        ps.setString(4, c.getTelefone());
        ps.setString(5, c.getEndereco());
        ps.setString(6, c.getBairro());
        ps.setString(7, c.getCidade());
        ps.setString(8, c.getEstado());
        ps.setString(9, c.getCep());
    }

    public void alterarStatus(int id, boolean status) throws SQLException {
        String sql = "UPDATE cliente SET status = ? WHERE id_cliente = ?";
        try (Connection con = ConnectionFactory.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setBoolean(1, status);
            ps.setInt(2, id);
            ps.executeUpdate();
        }
    }

    public List<Cliente> listar(boolean incluirInativos) throws SQLException {
        String sql = "SELECT " + COLUNAS + " FROM cliente" + (incluirInativos ? "" : " WHERE status = TRUE") + " ORDER BY nome";
        List<Cliente> lista = new ArrayList<>();
        try (Connection con = ConnectionFactory.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(mapear(rs));
            }
        }
        return lista;
    }

    private Cliente mapear(ResultSet rs) throws SQLException {
        Cliente c = new Cliente();
        c.setId(rs.getInt("id_cliente"));
        c.setNome(rs.getString("nome"));
        c.setCpf(rs.getString("cpf"));
        c.setDataNascimento(Datas.deSql(rs.getDate("data_nascimento")));
        c.setTelefone(rs.getString("telefone"));
        c.setEndereco(rs.getString("endereco"));
        c.setBairro(rs.getString("bairro"));
        c.setCidade(rs.getString("cidade"));
        c.setEstado(rs.getString("estado"));
        c.setCep(rs.getString("cep"));
        c.setStatus(rs.getBoolean("status"));
        return c;
    }
}
