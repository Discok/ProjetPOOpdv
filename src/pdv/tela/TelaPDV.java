package pdv.tela;

import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Optional;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import pdv.modelo.*;
import pdv.servico.*;

public class TelaPDV extends JFrame {
    private final ProdutoServico produtoServico;
    private final VendaServico vendaServico;
    private Venda vendaAtual;
    
    private JTextField campoCodigo;
    private JTable tabela;
    private DefaultTableModel modelo;
    private JLabel labelTotal;
    private JLabel labelStatus;
    
    public TelaPDV(ProdutoServico produtoServico, VendaServico vendaServico) {
        this.produtoServico = produtoServico;
        this.vendaServico = vendaServico;
        this.vendaAtual = new Venda();
        
        criarTela();
        configurarEventos();
    }
    
    private void criarTela() {
        setTitle("PDV Simples");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        // Campo código
        JPanel painelCodigo = new JPanel(new BorderLayout());
        painelCodigo.setBorder(BorderFactory.createTitledBorder("Leitor de Código"));
        campoCodigo = new JTextField();
        campoCodigo.setFont(new Font("Arial", Font.PLAIN, 20));
        painelCodigo.add(new JLabel("Código: "), BorderLayout.WEST);
        painelCodigo.add(campoCodigo, BorderLayout.CENTER);
        
        // Tabela
        String[] colunas = {"ID", "Produto", "Qtd", "Preço", "Subtotal"};
        modelo = new DefaultTableModel(colunas, 0);
        tabela = new JTable(modelo);
        JScrollPane scroll = new JScrollPane(tabela);
        
        // Total
        labelTotal = new JLabel("Total: R$ 0,00");
        labelTotal.setFont(new Font("Arial", Font.BOLD, 24));
        
        labelStatus = new JLabel("Pronto para leitura");
        
        // Botões
        JButton btnLimpar = new JButton("Limpar");
        btnLimpar.addActionListener(e -> limparVenda());
        
        JButton btnFinalizar = new JButton("Finalizar");
        btnFinalizar.addActionListener(e -> finalizarVenda());
        
        JPanel painelBotoes = new JPanel();
        painelBotoes.add(btnLimpar);
        painelBotoes.add(btnFinalizar);
        
        // Layout
        setLayout(new BorderLayout(10, 10));
        add(painelCodigo, BorderLayout.NORTH);
        add(scroll, BorderLayout.CENTER);
        
        JPanel painelInferior = new JPanel(new BorderLayout());
        painelInferior.add(labelTotal, BorderLayout.CENTER);
        painelInferior.add(painelBotoes, BorderLayout.EAST);
        painelInferior.add(labelStatus, BorderLayout.SOUTH);
        add(painelInferior, BorderLayout.SOUTH);
        
        ((JComponent) getContentPane()).setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
    }
    
    private void configurarEventos() {
        campoCodigo.addKeyListener(new KeyAdapter() {
            @SuppressWarnings("override")
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    adicionarProduto();
                }
            }
        });
    }
    
    private void adicionarProduto() {
        String codigo = campoCodigo.getText().trim();
        if (codigo.isEmpty()) return;
        
        Optional<Produto> produtoOpt = produtoServico.buscarPorCodigo(codigo);
        
        if (produtoOpt.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Produto não encontrado!");
            campoCodigo.setText("");
            return;
        }
        
        Produto produto = produtoOpt.get();
        
        if (!produtoServico.temEstoque(produto, 1)) {
            JOptionPane.showMessageDialog(this, "Produto sem estoque!");
            campoCodigo.setText("");
            return;
        }
        
        VendaItem item = new VendaItem(
            produto.getId(),
            produto.getNome(),
            produto.getPreco(),
            1
        );
        
        vendaAtual.adicionarItem(item);
        atualizarTela();
        
        campoCodigo.setText("");
        labelStatus.setText("Adicionado: " + produto.getNome());
    }
    
    private void atualizarTela() {
        modelo.setRowCount(0);
        
        for (VendaItem item : vendaAtual.getItens()) {
            modelo.addRow(new Object[]{
                item.getProdutoId(),
                item.getNomeProduto(),
                item.getQuantidade(),
                String.format("R$ %.2f", item.getPreco()),
                String.format("R$ %.2f", item.getSubtotal())
            });
        }
        
        labelTotal.setText(String.format("Total: R$ %.2f", vendaAtual.getTotal()));
    }
    
    private void finalizarVenda() {
        if (vendaAtual.estaVazia()) {
            JOptionPane.showMessageDialog(this, "Nenhum item na venda!");
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this,
            String.format("Finalizar venda de R$ %.2f?", vendaAtual.getTotal()),
            "Confirmar", JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            if (vendaServico.finalizar(vendaAtual)) {
                JOptionPane.showMessageDialog(this, "Venda finalizada com sucesso!");
                limparVenda();
            } else {
                JOptionPane.showMessageDialog(this, "Erro ao finalizar venda!");
            }
        }
    }
    
    private void limparVenda() {
        vendaAtual = new Venda();
        atualizarTela();
        labelStatus.setText("Venda limpa");
        campoCodigo.requestFocus();
    }
}