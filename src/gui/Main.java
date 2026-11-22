package gui;

import javax.swing.JFrame;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class Main extends JFrame {

    public Main() {
        super("Cadastro de Clientes e Animais");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(950, 600);
        setLocationRelativeTo(null);

        JTabbedPane abas = new JTabbedPane();
        abas.addTab("Clientes", new ClientePanel());
        abas.addTab("Raças", new RacaPanel());
        abas.addTab("Animais", new AnimalPanel());
        abas.addTab("Consulta por Cliente", new ConsultaPanel());
        abas.addTab("Relatórios", new RelatorioPanel());
        abas.addChangeListener(e -> ((PainelBase) abas.getSelectedComponent()).recarregar());

        add(abas);
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignorada) {
        }
        SwingUtilities.invokeLater(() -> new Main().setVisible(true));
    }
}
