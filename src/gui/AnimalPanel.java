package gui;

import dao.AnimalDAO;
import dao.ClienteDAO;
import dao.RacaDAO;
import modelo.Animal;
import modelo.Cliente;
import modelo.Datas;
import modelo.Raca;

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

public class AnimalPanel extends PainelBase {

    private final AnimalDAO dao = new AnimalDAO();
    private final ClienteDAO clienteDao = new ClienteDAO();
    private final RacaDAO racaDao = new RacaDAO();
    private final JTextField txtNome = new JTextField(20);
    private final JTextField txtNascimento = new JTextField(10);
    private final JComboBox<String> cmbSexo = new JComboBox<>(new String[]{"M", "F"});
    private final JTextField txtCor = new JTextField(12);
    private final JTextField txtObservacoes = new JTextField(25);
    private final JComboBox<Cliente> cmbCliente = new JComboBox<>();
    private final JComboBox<Raca> cmbRaca = new JComboBox<>();
    private final JCheckBox chkInativos = new JCheckBox("Mostrar inativos");
    private final DefaultTableModel modelo = modeloTabela("ID", "Animal", "Cliente", "Raça", "Nascimento", "Sexo", "Cor", "Status");
    private final JTable tabela = new JTable(modelo);
    private List<Animal> lista = new ArrayList<>();
    private Animal selecionado;

    public AnimalPanel() {
        setLayout(new BorderLayout(5, 5));

        JPanel form = new JPanel(new GridBagLayout());
        campo(form, "Cliente *", cmbCliente, 0, 0, 1);
        campo(form, "Raça *", cmbRaca, 0, 1, 1);
        campo(form, "Nome *", txtNome, 1, 0, 1);
        campo(form, "Nascimento", txtNascimento, 1, 1, 1);
        campo(form, "Sexo", cmbSexo, 2, 0, 1);
        campo(form, "Cor", txtCor, 2, 1, 1);
        campo(form, "Observações", txtObservacoes, 3, 0, 3);

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
            Cliente clienteAtual = (Cliente) cmbCliente.getSelectedItem();
            Raca racaAtual = (Raca) cmbRaca.getSelectedItem();
            cmbCliente.removeAllItems();
            for (Cliente c : clienteDao.listar(false)) {
                cmbCliente.addItem(c);
            }
            cmbRaca.removeAllItems();
            for (Raca r : racaDao.listar(false)) {
                cmbRaca.addItem(r);
            }
            selecionarCliente(clienteAtual == null ? 0 : clienteAtual.getId());
            selecionarRaca(racaAtual == null ? 0 : racaAtual.getId());

            lista = dao.listar(chkInativos.isSelected());
            modelo.setRowCount(0);
            for (Animal a : lista) {
                modelo.addRow(new Object[]{a.getId(), a.getNome(), a.getNomeCliente(), a.getNomeRaca(),
                        Datas.formatar(a.getDataNascimento()), a.getSexo(), texto(a.getCor()),
                        a.isStatus() ? "Ativo" : "Inativo"});
            }
        } catch (Exception e) {
            erro(e);
        }
    }

    private void selecionarCliente(int id) {
        for (int i = 0; i < cmbCliente.getItemCount(); i++) {
            if (cmbCliente.getItemAt(i).getId() == id) {
                cmbCliente.setSelectedIndex(i);
                return;
            }
        }
    }

    private void selecionarRaca(int id) {
        for (int i = 0; i < cmbRaca.getItemCount(); i++) {
            if (cmbRaca.getItemAt(i).getId() == id) {
                cmbRaca.setSelectedIndex(i);
                return;
            }
        }
    }

    private void carregar(Animal a) {
        selecionado = a;
        txtNome.setText(texto(a.getNome()));
        txtNascimento.setText(Datas.formatar(a.getDataNascimento()));
        cmbSexo.setSelectedItem(String.valueOf(a.getSexo()));
        txtCor.setText(texto(a.getCor()));
        txtObservacoes.setText(texto(a.getObservacoes()));
        selecionarCliente(a.getIdCliente());
        selecionarRaca(a.getIdRaca());
    }

    private void limpar() {
        selecionado = null;
        txtNome.setText("");
        txtNascimento.setText("");
        cmbSexo.setSelectedIndex(0);
        txtCor.setText("");
        txtObservacoes.setText("");
        tabela.clearSelection();
    }

    private void salvar() {
        try {
            Animal a = new Animal();
            boolean novo = selecionado == null;
            if (!novo) {
                a.setId(selecionado.getId());
                a.setStatus(selecionado.isStatus());
            }
            Cliente cliente = (Cliente) cmbCliente.getSelectedItem();
            Raca raca = (Raca) cmbRaca.getSelectedItem();
            a.setIdCliente(cliente == null ? 0 : cliente.getId());
            a.setIdRaca(raca == null ? 0 : raca.getId());
            a.setNome(txtNome.getText());
            a.setDataNascimento(Datas.converter(txtNascimento.getText()));
            a.setSexo(((String) cmbSexo.getSelectedItem()).charAt(0));
            a.setCor(txtCor.getText().trim());
            a.setObservacoes(txtObservacoes.getText().trim());
            dao.salvar(a);
            info(novo ? "Animal cadastrado com sucesso!" : "Animal alterado com sucesso!");
            limpar();
            recarregar();
        } catch (Exception e) {
            erro(e);
        }
    }

    private void alterarStatus(boolean ativo) {
        if (selecionado == null) {
            info("Selecione um animal na tabela.");
            return;
        }
        if (!confirmar((ativo ? "Reativar" : "Desativar") + " o animal " + selecionado.getNome() + "?")) {
            return;
        }
        try {
            dao.alterarStatus(selecionado.getId(), ativo);
            info(ativo ? "Animal reativado com sucesso!" : "Animal desativado com sucesso!");
            limpar();
            recarregar();
        } catch (Exception e) {
            erro(e);
        }
    }
}
