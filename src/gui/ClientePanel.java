package gui;

import dao.ClienteDAO;
import modelo.Cliente;
import modelo.CpfValidador;
import modelo.Datas;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagLayout;
import java.util.ArrayList;
import java.util.List;

public class ClientePanel extends PainelBase {

    private static final String[] UFS = {"", "AC", "AL", "AP", "AM", "BA", "CE", "DF", "ES", "GO", "MA", "MT", "MS", "MG", "PA",
            "PB", "PR", "PE", "PI", "RJ", "RN", "RS", "RO", "RR", "SC", "SP", "SE", "TO"};

    private final ClienteDAO dao = new ClienteDAO();
    private final JTextField txtNome = new JTextField(25);
    private final JTextField txtCpf = new JTextField(12);
    private final JTextField txtNascimento = new JTextField(10);
    private final JTextField txtTelefone = new JTextField(14);
    private final JTextField txtEndereco = new JTextField(25);
    private final JTextField txtBairro = new JTextField(15);
    private final JTextField txtCidade = new JTextField(15);
    private final JComboBox<String> cmbEstado = new JComboBox<>(UFS);
    private final JTextField txtCep = new JTextField(10);
    private final JCheckBox chkInativos = new JCheckBox("Mostrar inativos");
    private final DefaultTableModel modelo = modeloTabela("ID", "Nome", "CPF", "Nascimento", "Telefone", "Cidade", "Status");
    private final JTable tabela = new JTable(modelo);
    private List<Cliente> lista = new ArrayList<>();
    private Cliente selecionado;

    public ClientePanel() {
        setLayout(new BorderLayout(5, 5));

        JPanel form = new JPanel(new GridBagLayout());
        campo(form, "Nome *", txtNome, 0, 0, 1);
        campo(form, "CPF *", txtCpf, 0, 1, 1);
        campo(form, "Nascimento", txtNascimento, 1, 0, 1);
        campo(form, "Telefone", txtTelefone, 1, 1, 1);
        campo(form, "Endereço", txtEndereco, 2, 0, 1);
        campo(form, "Bairro", txtBairro, 2, 1, 1);
        campo(form, "Cidade", txtCidade, 3, 0, 1);
        campo(form, "UF", cmbEstado, 3, 1, 1);
        campo(form, "CEP", txtCep, 4, 0, 1);

        JButton btnSalvar = new JButton("Salvar");
        JButton btnNovo = new JButton("Novo / Limpar");
        JButton btnDesativar = new JButton("Desativar");
        JButton btnReativar = new JButton("Reativar");
        JPanel botoes = new JPanel(new FlowLayout(FlowLayout.LEFT));
        botoes.add(btnSalvar);
        botoes.add(btnNovo);
        botoes.add(btnDesativar);
        botoes.add(btnReativar);
        botoes.add(chkInativos);

        JPanel topo = new JPanel(new BorderLayout());
        topo.add(form, BorderLayout.CENTER);
        topo.add(botoes, BorderLayout.SOUTH);

        tabela.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        add(topo, BorderLayout.NORTH);
        add(new JScrollPane(tabela), BorderLayout.CENTER);

        btnSalvar.addActionListener(e -> salvar());
        btnNovo.addActionListener(e -> limpar());
        btnDesativar.addActionListener(e -> alterarStatus(false));
        btnReativar.addActionListener(e -> alterarStatus(true));
        chkInativos.addActionListener(e -> recarregar());
        tabela.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && tabela.getSelectedRow() >= 0) {
                carregar(lista.get(tabela.getSelectedRow()));
            }
        });

        recarregar();
    }

    @Override
    public void recarregar() {
        try {
            lista = dao.listar(chkInativos.isSelected());
            modelo.setRowCount(0);
            for (Cliente c : lista) {
                modelo.addRow(new Object[]{c.getId(), c.getNome(), CpfValidador.formatar(c.getCpf()),
                        Datas.formatar(c.getDataNascimento()), texto(c.getTelefone()), texto(c.getCidade()),
                        c.isStatus() ? "Ativo" : "Inativo"});
            }
        } catch (Exception e) {
            erro(e);
        }
    }

    private void carregar(Cliente c) {
        selecionado = c;
        txtNome.setText(texto(c.getNome()));
        txtCpf.setText(CpfValidador.formatar(c.getCpf()));
        txtNascimento.setText(Datas.formatar(c.getDataNascimento()));
        txtTelefone.setText(texto(c.getTelefone()));
        txtEndereco.setText(texto(c.getEndereco()));
        txtBairro.setText(texto(c.getBairro()));
        txtCidade.setText(texto(c.getCidade()));
        cmbEstado.setSelectedItem(texto(c.getEstado()));
        txtCep.setText(texto(c.getCep()));
    }

    private void limpar() {
        selecionado = null;
        txtNome.setText("");
        txtCpf.setText("");
        txtNascimento.setText("");
        txtTelefone.setText("");
        txtEndereco.setText("");
        txtBairro.setText("");
        txtCidade.setText("");
        cmbEstado.setSelectedIndex(0);
        txtCep.setText("");
        tabela.clearSelection();
    }

    private void salvar() {
        try {
            Cliente c = new Cliente();
            boolean novo = selecionado == null;
            if (!novo) {
                c.setId(selecionado.getId());
                c.setStatus(selecionado.isStatus());
            }
            c.setNome(txtNome.getText());
            c.setCpf(txtCpf.getText());
            c.setDataNascimento(Datas.converter(txtNascimento.getText()));
            c.setTelefone(txtTelefone.getText().trim());
            c.setEndereco(txtEndereco.getText().trim());
            c.setBairro(txtBairro.getText().trim());
            c.setCidade(txtCidade.getText().trim());
            c.setEstado((String) cmbEstado.getSelectedItem());
            c.setCep(txtCep.getText().trim());
            dao.salvar(c);
            info(novo ? "Cliente cadastrado com sucesso!" : "Cliente alterado com sucesso!");
            limpar();
            recarregar();
        } catch (Exception e) {
            erro(e);
        }
    }

    private void alterarStatus(boolean ativo) {
        if (selecionado == null) {
            info("Selecione um cliente na tabela.");
            return;
        }
        if (!confirmar((ativo ? "Reativar" : "Desativar") + " o cliente " + selecionado.getNome() + "?")) {
            return;
        }
        try {
            dao.alterarStatus(selecionado.getId(), ativo);
            info(ativo ? "Cliente reativado com sucesso!" : "Cliente desativado com sucesso!");
            limpar();
            recarregar();
        } catch (Exception e) {
            erro(e);
        }
    }
}
