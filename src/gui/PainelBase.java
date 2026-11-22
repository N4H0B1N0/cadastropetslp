package gui;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.table.DefaultTableModel;
import java.awt.GridBagConstraints;
import java.awt.Insets;

public abstract class PainelBase extends JPanel {

    public abstract void recarregar();

    protected void campo(JPanel painel, String rotulo, JComponent componente, int linha, int coluna, int largura) {
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(3, 5, 3, 5);
        g.anchor = GridBagConstraints.WEST;
        g.gridx = coluna * 2;
        g.gridy = linha;
        painel.add(new JLabel(rotulo), g);
        g = new GridBagConstraints();
        g.insets = new Insets(3, 5, 3, 5);
        g.fill = GridBagConstraints.HORIZONTAL;
        g.weightx = 1;
        g.gridx = coluna * 2 + 1;
        g.gridy = linha;
        g.gridwidth = largura;
        painel.add(componente, g);
    }

    protected DefaultTableModel modeloTabela(String... colunas) {
        return new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
    }

    protected void info(String mensagem) {
        JOptionPane.showMessageDialog(this, mensagem, "Informação", JOptionPane.INFORMATION_MESSAGE);
    }

    protected void erro(Exception e) {
        if (e instanceof IllegalArgumentException) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Atenção", JOptionPane.WARNING_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "Erro: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    protected boolean confirmar(String mensagem) {
        return JOptionPane.showConfirmDialog(this, mensagem, "Confirmação", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION;
    }

    protected String texto(String valor) {
        return valor == null ? "" : valor;
    }
}
