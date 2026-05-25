import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

// MODELO - Produto
class Produto {
    private String codigo;
    private String nome;
    private double preco;
    
    public Produto(String codigo, String nome, double preco) {
        this.codigo = codigo;
        this.nome = nome;
        this.preco = preco;
    }
    
    public String getCodigo() { return codigo; }
    public String getNome() { return nome; }
    public double getPreco() { return preco; }
}

// MODELO - Item da Venda
class ItemVenda {
    private Produto produto;
    private int quantidade;
    
    public ItemVenda(Produto produto, int quantidade) {
        this.produto = produto;
        this.quantidade = quantidade;
    }
    
    public Produto getProduto() { return produto; }
    public int getQuantidade() { return quantidade; }
    public double getSubtotal() { return produto.getPreco() * quantidade; }
    public void aumentarQuantidade() { quantidade++; }
}

// TELA PRINCIPAL
public class PDVTeste extends JFrame {
    private Map<String, Produto> catalogo = new HashMap<>();
    private java.util.List<ItemVenda> carrinho = new ArrayList<>();
    
    private JTextField campoCodigo;
    private JTable tabela;
    private DefaultTableModel modelo;
    private JLabel labelTotal;
    private JLabel labelStatus;
    
    public PDVTeste() {
        carregarProdutos();
        criarTela();
        configurarEventos();
    }
    
    private void carregarProdutos() {
        // Produtos fictícios (sem banco de dados)
        catalogo.put("7891234560010", new Produto("7891234560010", "Coca-Cola 2L", 8.50));
        catalogo.put("7891234560027", new Produto("7891234560027", "Arroz 5kg", 22.90));
        catalogo.put("7891234560034", new Produto("7891234560034", "Feijão 1kg", 6.75));
        catalogo.put("7891234560041", new Produto("7891234560041", "Açúcar 1kg", 4.50));
        catalogo.put("7891234560058", new Produto("7891234560058", "Café 500g", 12.90));
        catalogo.put("789100012345", new Produto("789100012345", "Pão Francês (6 un)", 5.00));
        catalogo.put("789200023456", new Produto("789200023456", "Leite Integral 1L", 4.99));
        catalogo.put("789300034567", new Produto("789300034567", "Manteiga 200g", 7.50));
        
        // Códigos de teste manuais
        catalogo.put("123", new Produto("123", "PRODUTO TESTE", 1.00));
        catalogo.put("999", new Produto("999", "ITEM PROMOCIONAL", 5.00));
    }
    
    private void criarTela() {
        setTitle("PDV Teste - Sem Banco de Dados");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(850, 600);
        setLocationRelativeTo(null);
        
        // Painel do leitor
        JPanel painelLeitor = new JPanel(new BorderLayout(10, 10));
        painelLeitor.setBorder(BorderFactory.createTitledBorder("📷 LEITOR DE CÓDIGO DE BARRAS"));
        painelLeitor.setBackground(new Color(240, 248, 255));
        
        campoCodigo = new JTextField();
        campoCodigo.setFont(new Font("Consolas", Font.PLAIN, 24));
        campoCodigo.setBackground(Color.WHITE);
        
        JLabel labelInstrucao = new JLabel("Aponte o código de barras e pressione ENTER");
        labelInstrucao.setFont(new Font("Arial", Font.ITALIC, 12));
        labelInstrucao.setForeground(Color.GRAY);
        
        painelLeitor.add(new JLabel("CÓDIGO:"), BorderLayout.WEST);
        painelLeitor.add(campoCodigo, BorderLayout.CENTER);
        painelLeitor.add(labelInstrucao, BorderLayout.SOUTH);
        
        // Tabela de itens
        String[] colunas = {"Código", "Produto", "Qtd", "Preço", "Subtotal"};
        modelo = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        tabela = new JTable(modelo);
        tabela.setFont(new Font("Arial", Font.PLAIN, 14));
        tabela.setRowHeight(28);
        tabela.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        
        JScrollPane scroll = new JScrollPane(tabela);
        scroll.setBorder(BorderFactory.createTitledBorder("🛒 ITENS DA VENDA"));
        
        // Painel de total
        JPanel painelTotal = new JPanel(new BorderLayout());
        painelTotal.setBorder(BorderFactory.createTitledBorder("💰 TOTAL"));
        
        labelTotal = new JLabel("R$ 0,00");
        labelTotal.setFont(new Font("Arial", Font.BOLD, 32));
        labelTotal.setForeground(new Color(0, 100, 0));
        labelTotal.setHorizontalAlignment(SwingConstants.RIGHT);
        
        labelStatus = new JLabel("✅ Pronto para leitura");
        labelStatus.setFont(new Font("Arial", Font.PLAIN, 12));
        
        painelTotal.add(labelTotal, BorderLayout.CENTER);
        painelTotal.add(labelStatus, BorderLayout.SOUTH);
        
        // Botões
        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        
        JButton btnRemover = criarBotao("❌ Remover Item", new Color(220, 53, 69));
        btnRemover.addActionListener(e -> removerItem());
        
        JButton btnLimpar = criarBotao("🗑️ Limpar Venda", new Color(255, 140, 0));
        btnLimpar.addActionListener(e -> limparVenda());
        
        JButton btnFinalizar = criarBotao("✅ Finalizar Venda", new Color(40, 167, 69));
        btnFinalizar.addActionListener(e -> finalizarVenda());
        
        painelBotoes.add(btnRemover);
        painelBotoes.add(btnLimpar);
        painelBotoes.add(btnFinalizar);
        
        // Layout principal
        JPanel painelInferior = new JPanel(new BorderLayout(10, 10));
        painelInferior.add(painelTotal, BorderLayout.CENTER);
        painelInferior.add(painelBotoes, BorderLayout.SOUTH);
        
        setLayout(new BorderLayout(10, 10));
        add(painelLeitor, BorderLayout.NORTH);
        add(scroll, BorderLayout.CENTER);
        add(painelInferior, BorderLayout.SOUTH);
        
        ((JComponent) getContentPane()).setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Menu com atalhos
        JMenuBar menuBar = new JMenuBar();
        JMenu menuAjuda = new JMenu("Ajuda");
        JMenuItem itemProdutos = new JMenuItem("Listar Produtos Cadastrados");
        itemProdutos.addActionListener(e -> mostrarProdutos());
        menuAjuda.add(itemProdutos);
        menuBar.add(menuAjuda);
        setJMenuBar(menuBar);
    }
    
    private JButton criarBotao(String texto, Color cor) {
        JButton botao = new JButton(texto);
        botao.setFont(new Font("Arial", Font.BOLD, 13));
        botao.setBackground(cor);
        botao.setForeground(Color.WHITE);
        botao.setFocusPainted(false);
        botao.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        botao.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return botao;
    }
    
    private void configurarEventos() {
        campoCodigo.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    adicionarProduto();
                }
            }
        });
        
        // Atalhos do teclado
        getRootPane().registerKeyboardAction(e -> limparVenda(),
            KeyStroke.getKeyStroke(KeyEvent.VK_F2, 0), JComponent.WHEN_IN_FOCUSED_WINDOW);
        
        getRootPane().registerKeyboardAction(e -> finalizarVenda(),
            KeyStroke.getKeyStroke(KeyEvent.VK_F4, 0), JComponent.WHEN_IN_FOCUSED_WINDOW);
    }
    
    private void adicionarProduto() {
        String codigo = campoCodigo.getText().trim();
        
        if (codigo.isEmpty()) {
            labelStatus.setText("⚠️ Digite ou leia um código de barras");
            return;
        }
        
        labelStatus.setText("🔍 Buscando: " + codigo);
        
        Produto produto = catalogo.get(codigo);
        
        if (produto == null) {
            JOptionPane.showMessageDialog(this,
                "❌ Produto não encontrado!\nCódigo: " + codigo + "\n\nProdutos disponíveis no menu Ajuda.",
                "Produto não localizado",
                JOptionPane.ERROR_MESSAGE);
            labelStatus.setText("❌ Produto não encontrado");
            campoCodigo.setText("");
            campoCodigo.requestFocus();
            return;
        }
        
        // Verificar se já está no carrinho
        for (ItemVenda item : carrinho) {
            if (item.getProduto().getCodigo().equals(codigo)) {
                item.aumentarQuantidade();
                atualizarTabela();
                labelStatus.setText("✅ +1 " + produto.getNome());
                campoCodigo.setText("");
                campoCodigo.requestFocus();
                Toolkit.getDefaultToolkit().beep();
                return;
            }
        }
        
        // Adicionar novo item
        carrinho.add(new ItemVenda(produto, 1));
        atualizarTabela();
        
        labelStatus.setText("✅ Adicionado: " + produto.getNome());
        campoCodigo.setText("");
        campoCodigo.requestFocus();
        
        // Feedback sonoro
        Toolkit.getDefaultToolkit().beep();
    }
    
    private void atualizarTabela() {
        modelo.setRowCount(0);
        double total = 0;
        
        for (ItemVenda item : carrinho) {
            Produto p = item.getProduto();
            double subtotal = item.getSubtotal();
            total += subtotal;
            
            modelo.addRow(new Object[]{
                p.getCodigo(),
                p.getNome(),
                item.getQuantidade(),
                String.format("R$ %.2f", p.getPreco()),
                String.format("R$ %.2f", subtotal)
            });
        }
        
        labelTotal.setText(String.format("R$ %.2f", total));
        
        // Atualizar status com quantidade
        if (carrinho.isEmpty()) {
            labelStatus.setText("🛒 Carrinho vazio. Leia um produto!");
        } else {
            labelStatus.setText(String.format("🛒 Carrinho: %d itens | Total: R$ %.2f", 
                carrinho.size(), total));
        }
    }
    
    private void removerItem() {
        int linha = tabela.getSelectedRow();
        if (linha >= 0 && linha < carrinho.size()) {
            ItemVenda item = carrinho.get(linha);
            carrinho.remove(linha);
            atualizarTabela();
            labelStatus.setText("🗑️ Removido: " + item.getProduto().getNome());
        } else {
            JOptionPane.showMessageDialog(this,
                "Selecione um item na tabela para remover",
                "Remover item",
                JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    private void limparVenda() {
        if (!carrinho.isEmpty()) {
            int confirm = JOptionPane.showConfirmDialog(this,
                "Limpar toda a venda?",
                "Confirmar",
                JOptionPane.YES_NO_OPTION);
            
            if (confirm == JOptionPane.YES_OPTION) {
                carrinho.clear();
                atualizarTabela();
                labelStatus.setText("🔄 Venda cancelada. Carrinho limpo!");
            }
        } else {
            labelStatus.setText("⚠️ Carrinho já está vazio");
        }
        campoCodigo.requestFocus();
    }
    
    private void finalizarVenda() {
        if (carrinho.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Nenhum item no carrinho!\nAdicione produtos antes de finalizar.",
                "Carrinho vazio",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        double total = 0;
        for (ItemVenda item : carrinho) {
            total += item.getSubtotal();
        }
        
        int itensTotal = 0;
        for (ItemVenda item : carrinho) {
            itensTotal += item.getQuantidade();
        }
        
        int confirm = JOptionPane.showConfirmDialog(this,
            String.format("CONFIRMAR VENDA?\n\nItens: %d\nTotal: R$ %.2f\n\nDeseja finalizar?",
                itensTotal, total),
            "Finalizar Venda",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE);
        
        if (confirm == JOptionPane.YES_OPTION) {
            // Mostrar resumo da venda
            StringBuilder resumo = new StringBuilder();
            resumo.append("=== VENDA REALIZADA ===\n\n");
            for (ItemVenda item : carrinho) {
                Produto p = item.getProduto();
                resumo.append(String.format("%s\n  %dx R$%.2f = R$%.2f\n",
                    p.getNome(),
                    item.getQuantidade(),
                    p.getPreco(),
                    item.getSubtotal()));
            }
            resumo.append(String.format("\nTOTAL: R$ %.2f\n", total));
            resumo.append("\nObrigado pela compra!");
            
            JOptionPane.showMessageDialog(this,
                resumo.toString(),
                "Venda Finalizada",
                JOptionPane.INFORMATION_MESSAGE);
            
            // Limpar carrinho
            carrinho.clear();
            atualizarTabela();
            labelStatus.setText("✅ Venda finalizada com sucesso! Próximo cliente!");
            campoCodigo.requestFocus();
        }
    }
    
    private void mostrarProdutos() {
        StringBuilder lista = new StringBuilder();
        lista.append("=== PRODUTOS CADASTRADOS ===\n\n");
        lista.append("CÓDIGO               | PRODUTO              | PREÇO\n");
        lista.append("------------------------------------------------\n");
        
        for (Produto p : catalogo.values()) {
            lista.append(String.format("%-20s | %-20s | R$ %.2f\n",
                p.getCodigo(), p.getNome(), p.getPreco()));
        }
        
        lista.append("\n------------------------------------------------");
        lista.append("\n\nPara testar o leitor, use os códigos acima.");
        lista.append("\nOU digite manualmente no campo.");
        
        JTextArea textArea = new JTextArea(lista.toString());
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        textArea.setEditable(false);
        
        JScrollPane scroll = new JScrollPane(textArea);
        scroll.setPreferredSize(new Dimension(500, 400));
        
        JOptionPane.showMessageDialog(this,
            scroll,
            "Lista de Produtos",
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    public static void main(String[] args) {
        // Estilo bonito
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        SwingUtilities.invokeLater(() -> {
            new PDVTeste().setVisible(true);
        });
    }
}