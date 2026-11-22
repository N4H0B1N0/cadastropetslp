package gui;

import dao.RelatorioDAO;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.io.File;
import java.nio.file.Path;
import java.time.LocalDate;

public class RelatorioPanel extends PainelBase {

    private static final String[] MESES = {"Janeiro", "Fevereiro", "Março", "Abril", "Maio", "Junho", "Julho",
            "Agosto", "Setembro", "Outubro", "Novembro", "Dezembro"};

    private final RelatorioDAO dao = new RelatorioDAO();
    private final JComboBox<String> cmbMes = new JComboBox<>(MESES);

    public RelatorioPanel() {
        setLayout(new GridLayout(0, 1, 5, 10));

        JButton btnTodos = new JButton("Todos os clientes e seus animais");
        JButton btnAnimais = new JButton("Animais aniversariantes do mês");
        JButton btnClientes = new JButton("Clientes aniversariantes do mês");

        JPanel linhaMes = new JPanel(new FlowLayout(FlowLayout.LEFT));
        linhaMes.add(new JLabel("Mês dos aniversariantes:"));
        linhaMes.add(cmbMes);
        cmbMes.setSelectedIndex(LocalDate.now().getMonthValue() - 1);

        add(btnTodos);
        add(linhaMes);
        add(btnAnimais);
        add(btnClientes);

        btnTodos.addActionListener(e -> gerar("relatorio_clientes_animais.txt", 0));
        btnAnimais.addActionListener(e -> gerar("relatorio_animais_aniversariantes.txt", 1));
        btnClientes.addActionListener(e -> gerar("relatorio_clientes_aniversariantes.txt", 2));
    }

    @Override
    public void recarregar() {
    }

    private void gerar(String nomeSugerido, int tipo) {
        JFileChooser chooser = new JFileChooser();
        chooser.setSelectedFile(new File(nomeSugerido));
        chooser.setFileFilter(new FileNameExtensionFilter("Arquivo de texto (*.txt)", "txt"));
        if (chooser.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) {
            return;
        }
        File arquivo = chooser.getSelectedFile();
        if (!arquivo.getName().toLowerCase().endsWith(".txt")) {
            arquivo = new File(arquivo.getParentFile(), arquivo.getName() + ".txt");
        }
        Path caminho = arquivo.toPath();
        int mes = cmbMes.getSelectedIndex() + 1;
        try {
            int total;
            String descricao;
            if (tipo == 0) {
                total = dao.gerarClientesEAnimais(caminho);
                descricao = "cliente(s)";
            } else if (tipo == 1) {
                total = dao.gerarAnimaisAniversariantes(mes, caminho);
                descricao = "animal(is) aniversariante(s)";
            } else {
                total = dao.gerarClientesAniversariantes(mes, caminho);
                descricao = "cliente(s) aniversariante(s)";
            }
            info("Relatório gerado com sucesso!\n" + total + " " + descricao + ".\nArquivo: " + caminho.toAbsolutePath());
        } catch (Exception e) {
            erro(e);
        }
    }
}
