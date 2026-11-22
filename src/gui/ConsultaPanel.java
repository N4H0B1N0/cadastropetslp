package gui;

import dao.AnimalDAO;
import modelo.Animal;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagLayout;

public class ConsultaPanel extends PainelBase {

    private final AnimalDAO dao = new AnimalDAO();
    private final JTextField txtNome = new JTextField(20);
    private final JTextField txtCpf = new JTextField(14);
    private final JCheckBox chkInativos = new JCheckBox("Mostrar inativos");
    private final DefaultTableModel modelo = modeloTabela("Cliente", "Animal", "Raça", "Idade", "Sexo", "Status");

    public ConsultaPanel() {
        setLayout(new BorderLayout(5, 5));

        JPanel form = new JPanel(new GridBagLayout());
        campo(form, "Nome do cliente", txtNome, 0, 0, 1);
        campo(form, "CPF", txtCpf, 0, 1, 1);

        JButton btnBuscar = new JButton("Consultar");
        JButton btnLimpar = new JButton("Limpar");
        JPanel botoes = new JPanel(new FlowLayout(FlowLayout.LEFT));
        botoes.add(btnBuscar);
        botoes.add(btnLimpar);
        botoes.add(chkInativos);

        JPanel topo = new JPanel(new BorderLayout());
        topo.add(form, BorderLayout.CENTER);
        topo.add(botoes, BorderLayout.SOUTH);

        add(topo, BorderLayout.NORTH);
        add(new JScrollPane(new JTable(modelo)), BorderLayout.CENTER);

        btnBuscar.addActionListener(e -> consultar());
        btnLimpar.addActionListener(e -> {
            txtNome.setText("");
            txtCpf.setText("");
            consultar();
        });
        txtNome.addActionListener(e -> consultar());
        txtCpf.addActionListener(e -> consultar());

        consultar();
    }

    @Override
    public void recarregar() {
        consultar();
    }

    private void consultar() {
        try {
            modelo.setRowCount(0);
            for (Animal a : dao.consultarPorCliente(txtNome.getText(), txtCpf.getText(), chkInativos.isSelected())) {
                modelo.addRow(new Object[]{a.getNomeCliente(), a.getNome(), a.getNomeRaca(), a.getIdade(),
                        a.getSexo(), a.isStatus() ? "Ativo" : "Inativo"});
            }
        } catch (Exception e) {
            erro(e);
        }
    }
}
