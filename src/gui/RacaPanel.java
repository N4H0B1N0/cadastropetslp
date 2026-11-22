package gui;

import dao.RacaDAO;
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

public class RacaPanel extends PainelBase {

    private final RacaDAO dao = new RacaDAO();
    private final JTextField txtNome = new JTextField(25);
    private final JComboBox<String> cmbTipo = new JComboBox<>(new String[]{Raca.CACHORRO, Raca.GATO});
    private final JCheckBox chkInativos = new JCheckBox("Mostrar inativos");
    private final DefaultTableModel modelo = modeloTabela("ID", "Raça", "Tipo", "Status");
    private final JTable tabela = new JTable(modelo);
    private List<Raca> lista = new ArrayList<>();
    private Raca selecionada;

    public RacaPanel() {
        setLayout(new BorderLayout(5, 5));

        JPanel form = new JPanel(new GridBagLayout());
        campo(form, "Nome da raça *", txtNome, 0, 0, 1);
        campo(form, "Tipo", cmbTipo, 0, 1, 1);

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
                selecionada = lista.get(tabela.getSelectedRow());
                txtNome.setText(selecionada.getNome());
                cmbTipo.setSelectedItem(selecionada.getTipoAnimal());
            }
        });

        recarregar();
    }

    @Override
    public void recarregar() {
        try {
            lista = dao.listar(chkInativos.isSelected());
            modelo.setRowCount(0);
            for (Raca r : lista) {
                modelo.addRow(new Object[]{r.getId(), r.getNome(), r.getTipoAnimal(), r.isStatus() ? "Ativo" : "Inativo"});
            }
        } catch (Exception e) {
            erro(e);
        }
    }

    private void limpar() {
        selecionada = null;
        txtNome.setText("");
        cmbTipo.setSelectedIndex(0);
        tabela.clearSelection();
    }

    private void salvar() {
        try {
            Raca r = new Raca();
            boolean nova = selecionada == null;
            if (!nova) {
                r.setId(selecionada.getId());
                r.setStatus(selecionada.isStatus());
            }
            r.setNome(txtNome.getText());
            r.setTipoAnimal((String) cmbTipo.getSelectedItem());
            dao.salvar(r);
            info(nova ? "Raça cadastrada com sucesso!" : "Raça alterada com sucesso!");
            limpar();
            recarregar();
        } catch (Exception e) {
            erro(e);
        }
    }

    private void alterarStatus(boolean ativo) {
        if (selecionada == null) {
            info("Selecione uma raça na tabela.");
            return;
        }
        if (!confirmar((ativo ? "Reativar" : "Desativar") + " a raça " + selecionada.getNome() + "?")) {
            return;
        }
        try {
            dao.alterarStatus(selecionada.getId(), ativo);
            info(ativo ? "Raça reativada com sucesso!" : "Raça desativada com sucesso!");
            limpar();
            recarregar();
        } catch (Exception e) {
            erro(e);
        }
    }
}
