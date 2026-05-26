package pdv.tela;

import java.awt.*;
import java.awt.event.*;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import pdv.contrato.IFormaPagamento;
import pdv.contrato.INotaFiscal;
import pdv.modelo.Produto;
import pdv.modelo.Venda;
import pdv.modelo.VendaItem;
import pdv.servico.NotaFiscalBuilder;
import pdv.servico.PagamentoBuilder;
import pdv.servico.ProdutoServico;
import pdv.servico.VendaServico;

public class TelaPDV extends JFrame {
    private final ProdutoServico produtoServico;
    private final VendaServico vendaServico;
    private final Connection conexao;
    private Venda vendaAtual;
    
    private JTextField campoBusca;
    private JRadioButton radioCodigo;
    private JRadioButton radioNome;
    private JTable tabelaProdutos;
    private DefaultTableModel modeloProdutos;
    private JTable tabelaCarrinho;
    private DefaultTableModel modeloCarrinho;
    private JLabel labelTotal;
    private JLabel labelStatus;
    
    private JDialog dialogPagamento;
    private JComboBox<String> comboFormaPagamento;
    private JTextField campoValorPago;
    private JLabel labelInstrucaoPagamento;
    private JLabel labelTroco;
    private JLabel labelTotalPagamento;
    private JComboBox<String> comboTipoNota;
    
    public TelaPDV(ProdutoServico produtoServico, VendaServico vendaServico, Connection conexao) {
        this.produtoServico = produtoServico;
        this.vendaServico = vendaServico;
        this.conexao = conexao;
        this.vendaAtual = new Venda();
        
        criarTela();
        configurarEventos();
        criarDialogPagamento();
    }
    
    private void criarTela() {
        setTitle("PDView - Ponto de Venda Rapida");
        setSize(1100, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        // Painel de busca
        JPanel painelBusca = new JPanel(new BorderLayout(10, 10));
        painelBusca.setBorder(BorderFactory.createTitledBorder("🔎 Busca de Produtos"));
        
        JPanel radioPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        radioCodigo = new JRadioButton("Código de Barras", true);
        radioNome = new JRadioButton("Nome do Produto");
        ButtonGroup grupo = new ButtonGroup();
        grupo.add(radioCodigo);
        grupo.add(radioNome);
        radioPanel.add(radioCodigo);
        radioPanel.add(radioNome);
        
        campoBusca = new JTextField(30);
        campoBusca.setFont(new Font("Arial", Font.PLAIN, 14));
        
        JButton btnBuscar = new JButton("Buscar");
        btnBuscar.addActionListener(e -> realizarBusca());
        
        painelBusca.add(radioPanel, BorderLayout.NORTH);
        painelBusca.add(campoBusca, BorderLayout.CENTER);
        painelBusca.add(btnBuscar, BorderLayout.EAST);
        
        // Tabela de produtos (não editável)
        String[] colunasProd = {"ID", "Código", "Produto", "Preço", "Estoque"};
        modeloProdutos = new DefaultTableModel(colunasProd, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tabelaProdutos = new JTable(modeloProdutos);
        tabelaProdutos.setRowHeight(25);
        tabelaProdutos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabelaProdutos.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    adicionarProdutoSelecionado();
                }
            }
        });
        JScrollPane scrollProdutos = new JScrollPane(tabelaProdutos);
        scrollProdutos.setBorder(BorderFactory.createTitledBorder("📦 Produtos (duplo clique para adicionar)"));
        
        // Tabela do carrinho
        String[] colunasCarrinho = {"ID", "Produto", "Qtd", "Preço", "Subtotal"};
        modeloCarrinho = new DefaultTableModel(colunasCarrinho, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tabelaCarrinho = new JTable(modeloCarrinho);
        tabelaCarrinho.setRowHeight(25);
        JScrollPane scrollCarrinho = new JScrollPane(tabelaCarrinho);
        scrollCarrinho.setBorder(BorderFactory.createTitledBorder("🛒 Carrinho"));
        
        // Painel inferior
        JPanel painelInferior = new JPanel(new BorderLayout());
        labelTotal = new JLabel("Total: R$ 0,00");
        labelTotal.setFont(new Font("Arial", Font.BOLD, 24));
        labelStatus = new JLabel("✅ Pronto para venda");
        
        JButton btnAdicionar = new JButton("➕ Adicionar ao Carrinho");
        btnAdicionar.addActionListener(e -> adicionarProdutoSelecionado());
        
        JButton btnRemover = new JButton("❌ Remover do Carrinho");
        btnRemover.addActionListener(e -> removerDoCarrinho());
        
        JButton btnFinalizar = new JButton("✅ Finalizar Venda");
        btnFinalizar.addActionListener(e -> abrirDialogPagamento());
        
        JButton btnLimpar = new JButton("🗑️ Limpar Venda");
        btnLimpar.addActionListener(e -> limparVenda());
        
        JPanel botoes = new JPanel();
        botoes.add(btnAdicionar);
        botoes.add(btnRemover);
        botoes.add(btnLimpar);
        botoes.add(btnFinalizar);
        
        painelInferior.add(labelTotal, BorderLayout.CENTER);
        painelInferior.add(botoes, BorderLayout.EAST);
        painelInferior.add(labelStatus, BorderLayout.SOUTH);
        
        // Layout principal
        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, scrollProdutos, scrollCarrinho);
        split.setResizeWeight(0.5);
        
        setLayout(new BorderLayout(10, 10));
        add(painelBusca, BorderLayout.NORTH);
        add(split, BorderLayout.CENTER);
        add(painelInferior, BorderLayout.SOUTH);
        
        ((JComponent) getContentPane()).setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Atalhos
        getRootPane().registerKeyboardAction(e -> limparVenda(),
            KeyStroke.getKeyStroke(KeyEvent.VK_F2, 0), JComponent.WHEN_IN_FOCUSED_WINDOW);
        getRootPane().registerKeyboardAction(e -> abrirDialogPagamento(),
            KeyStroke.getKeyStroke(KeyEvent.VK_F4, 0), JComponent.WHEN_IN_FOCUSED_WINDOW);
        getRootPane().registerKeyboardAction(e -> realizarBusca(),
            KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0), JComponent.WHEN_IN_FOCUSED_WINDOW);
    }
    
    private void criarDialogPagamento() {
        dialogPagamento = new JDialog(this, "Forma de Pagamento", true);
        dialogPagamento.setSize(450, 380);
        dialogPagamento.setLocationRelativeTo(this);
        dialogPagamento.setLayout(new BorderLayout(10, 10));
        
        JPanel painel = new JPanel(new GridBagLayout());
        painel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Total
        gbc.gridx = 0; gbc.gridy = 0;
        painel.add(new JLabel("Total da Venda:"), gbc);
        gbc.gridx = 1;
        labelTotalPagamento = new JLabel("R$ 0,00");
        labelTotalPagamento.setFont(new Font("Arial", Font.BOLD, 18));
        painel.add(labelTotalPagamento, gbc);
        
        // Forma de pagamento
        gbc.gridx = 0; gbc.gridy = 1;
        painel.add(new JLabel("Forma de Pagamento:"), gbc);
        gbc.gridx = 1;
        comboFormaPagamento = new JComboBox<>(PagamentoBuilder.getFormasDisponiveis().toArray(new String[0]));
        comboFormaPagamento.addActionListener(e -> atualizarInstrucaoPagamento());
        painel.add(comboFormaPagamento, gbc);
        
        // Valor recebido
        gbc.gridx = 0; gbc.gridy = 2;
        painel.add(new JLabel("Valor Recebido:"), gbc);
        gbc.gridx = 1;
        campoValorPago = new JTextField(12);
        campoValorPago.setEnabled(false);
        painel.add(campoValorPago, gbc);
        
        // Botão calcular
        gbc.gridx = 2;
        JButton btnCalcular = new JButton("Calcular");
        btnCalcular.addActionListener(e -> calcularTroco());
        btnCalcular.setEnabled(false);
        painel.add(btnCalcular, gbc);
        
        // Instrução
        gbc.gridx = 0; gbc.gridy = 3;
        gbc.gridwidth = 3;
        labelInstrucaoPagamento = new JLabel(" ");
        labelInstrucaoPagamento.setForeground(Color.GRAY);
        painel.add(labelInstrucaoPagamento, gbc);
        
        // Troco
        gbc.gridy = 4;
        labelTroco = new JLabel(" ");
        labelTroco.setForeground(new Color(0, 100, 0));
        painel.add(labelTroco, gbc);
        
        // Nota fiscal
        gbc.gridy = 5;
        JPanel painelNota = new JPanel(new FlowLayout(FlowLayout.LEFT));
        painelNota.add(new JLabel("Nota Fiscal:"));
        comboTipoNota = new JComboBox<>();
        for (String tipo : NotaFiscalBuilder.getTipos()) {
            comboTipoNota.addItem(tipo);
        }
        painelNota.add(comboTipoNota);
        painel.add(painelNota, gbc);
        
        // Botões
        JPanel botoes = new JPanel();
        JButton btnConfirmar = new JButton("Confirmar");
        btnConfirmar.addActionListener(e -> confirmarPagamento());
        JButton btnCancelar = new JButton("Cancelar");
        btnCancelar.addActionListener(e -> dialogPagamento.dispose());
        botoes.add(btnConfirmar);
        botoes.add(btnCancelar);
        
        dialogPagamento.add(painel, BorderLayout.CENTER);
        dialogPagamento.add(botoes, BorderLayout.SOUTH);
    }
    
    private void configurarEventos() {
        campoBusca.addActionListener(e -> realizarBusca());
    }
    
    private void realizarBusca() {
        String termo = campoBusca.getText().trim();
        if (termo.isEmpty()) return;
        
        labelStatus.setText("🔍 Buscando: " + termo);
        
        List<Produto> resultados;
        if (radioCodigo.isSelected()) {
            Optional<Produto> p = produtoServico.buscarPorCodigo(termo);
            resultados = p.map(List::of).orElse(new ArrayList<>());
        } else {
            resultados = produtoServico.buscarPorNome(termo);
        }
        
        modeloProdutos.setRowCount(0);
        for (Produto p : resultados) {
            String estoqueDisplay = p.getEstoque() < 0 ? 
                "⚠️ " + p.getEstoque() : String.valueOf(p.getEstoque());
            
            modeloProdutos.addRow(new Object[]{
                p.getId(), 
                p.getCodigoBarras(), 
                p.getNome(),
                String.format("R$ %.2f", p.getPreco()), 
                estoqueDisplay
            });
        }
        labelStatus.setText("🔍 " + resultados.size() + " produto(s) encontrado(s)");
    }
    
    private void adicionarProdutoSelecionado() {
        int linha = tabelaProdutos.getSelectedRow();
        if (linha < 0) {
            JOptionPane.showMessageDialog(this, "Selecione um produto na tabela!");
            return;
        }
        
        int id = (int) modeloProdutos.getValueAt(linha, 0);
        String nome = (String) modeloProdutos.getValueAt(linha, 2);
        String precoStr = (String) modeloProdutos.getValueAt(linha, 3);
        double preco = Double.parseDouble(precoStr.replace("R$ ", "").replace(",", "."));
        
        // Procura se já existe no carrinho
        boolean encontrou = false;
        List<VendaItem> itens = vendaAtual.getItens();
        
        for (int i = 0; i < itens.size(); i++) {
            VendaItem item = itens.get(i);
            if (item.getProdutoId() == id) {
                // Aumenta a quantidade
                item.setQuantidade(item.getQuantidade() + 1);
                encontrou = true;
                labelStatus.setText("✅ +1 " + nome + " (Total: " + item.getQuantidade() + ")");
                break;
            }
        }
        
        if (!encontrou) {
            // Adiciona novo item
            VendaItem novoItem = new VendaItem(id, nome, preco, 1);
            itens.add(novoItem);
            labelStatus.setText("✅ Adicionado: " + nome);
        }
        
        // Recalcula o total
        double total = 0;
        for (VendaItem item : itens) {
            total += item.getPreco() * item.getQuantidade();
        }
        
        // Atualiza a venda
        vendaAtual = new Venda(itens, total);
        
        atualizarCarrinho();
        Toolkit.getDefaultToolkit().beep();
    }
    
    private void removerDoCarrinho() {
        int linha = tabelaCarrinho.getSelectedRow();
        if (linha < 0) {
            JOptionPane.showMessageDialog(this, "Selecione um item no carrinho!");
            return;
        }
        
        String nome = (String) modeloCarrinho.getValueAt(linha, 1);
        int confirm = JOptionPane.showConfirmDialog(this, "Remover " + nome + " do carrinho?", 
            "Confirmar", JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            List<VendaItem> itens = vendaAtual.getItens();
            itens.remove(linha);
            
            // Recalcula total
            double total = 0;
            for (VendaItem item : itens) {
                total += item.getPreco() * item.getQuantidade();
            }
            vendaAtual = new Venda(itens, total);
            
            atualizarCarrinho();
            labelStatus.setText("🗑️ Removido: " + nome);
            Toolkit.getDefaultToolkit().beep();
        }
    }
    
    private void atualizarCarrinho() {
        modeloCarrinho.setRowCount(0);
        double total = 0;
        
        for (VendaItem item : vendaAtual.getItens()) {
            double subtotal = item.getPreco() * item.getQuantidade();
            total += subtotal;
            
            modeloCarrinho.addRow(new Object[]{
                item.getProdutoId(), 
                item.getNomeProduto(), 
                item.getQuantidade(),
                String.format("R$ %.2f", item.getPreco()),
                String.format("R$ %.2f", subtotal)
            });
        }
        
        labelTotal.setText(String.format("Total: R$ %.2f", total));
        
        if (!vendaAtual.getItens().isEmpty()) {
            labelStatus.setText("🛒 Carrinho: " + vendaAtual.getItens().size() + " itens | Total: R$ " + String.format("%.2f", total));
        }
    }
    
    private void atualizarInstrucaoPagamento() {
        IFormaPagamento p = PagamentoBuilder.getForma((String) comboFormaPagamento.getSelectedItem());
        if (p != null) {
            labelInstrucaoPagamento.setText(p.getIcone() + " " + p.getInstrucoes());
            boolean permiteTroco = p.permiteTroco();
            campoValorPago.setEnabled(permiteTroco);
            
            Component[] comps = ((JPanel) dialogPagamento.getContentPane()).getComponents();
            for (Component comp : comps) {
                if (comp instanceof JPanel jPanel) {
                    for (Component c : jPanel.getComponents()) {
                        if (c instanceof JButton && ((JButton) c).getText().equals("Calcular")) {
                            c.setEnabled(permiteTroco);
                        }
                    }
                }
            }
            
            if (!permiteTroco) {
                campoValorPago.setText("");
                labelTroco.setText("");
            }
        }
    }
    
    private void calcularTroco() {
        double total = vendaAtual.getTotal();
        try {
            double pago = Double.parseDouble(campoValorPago.getText().replace(",", "."));
            if (pago < total) {
                labelTroco.setText("⚠️ Faltam R$ " + String.format("%.2f", total - pago));
                labelTroco.setForeground(Color.RED);
            } else {
                labelTroco.setText("💰 Troco: R$ " + String.format("%.2f", pago - total));
                labelTroco.setForeground(new Color(0, 100, 0));
            }
        } catch (NumberFormatException e) {
            labelTroco.setText("⚠️ Digite um valor válido");
            labelTroco.setForeground(Color.RED);
        }
    }
    
    private void abrirDialogPagamento() {
        if (vendaAtual.getItens().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Carrinho vazio! Adicione produtos primeiro.");
            return;
        }
        labelTotalPagamento.setText(String.format("R$ %.2f", vendaAtual.getTotal()));
        campoValorPago.setText("");
        labelTroco.setText("");
        atualizarInstrucaoPagamento();
        dialogPagamento.setVisible(true);
    }
    
    private void confirmarPagamento() {
        IFormaPagamento pagamento = PagamentoBuilder.getForma((String) comboFormaPagamento.getSelectedItem());
        if (pagamento == null) return;
        
        double valorPago = 0;
        
        if (pagamento.permiteTroco()) {
            try {
                valorPago = Double.parseDouble(campoValorPago.getText().replace(",", "."));
                if (valorPago < vendaAtual.getTotal()) {
                    JOptionPane.showMessageDialog(dialogPagamento, "Valor insuficiente!");
                    return;
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(dialogPagamento, "Digite o valor recebido!");
                return;
            }
        }
        
        if (pagamento.processarPagamento(vendaAtual.getTotal(), valorPago)) {
            dialogPagamento.dispose();
            
            // Emitir nota fiscal
            String tipoNota = (String) comboTipoNota.getSelectedItem();
            INotaFiscal nota = NotaFiscalBuilder.getNota(tipoNota);
            nota.emitir(vendaAtual);
            
            // Salvar no banco
            vendaServico.finalizar(vendaAtual);
            
            String msg = String.format("✅ Venda finalizada!\nTotal: R$ %.2f\nPagamento: %s", 
                vendaAtual.getTotal(), pagamento.getNome());
            JOptionPane.showMessageDialog(this, msg);
            
            limparVenda();
        } else {
            JOptionPane.showMessageDialog(dialogPagamento, "Erro no pagamento!");
        }
    }
    
    private void limparVenda() {
        vendaAtual = new Venda();
        atualizarCarrinho();
        campoBusca.setText("");
        modeloProdutos.setRowCount(0);
        labelStatus.setText("🔄 Venda limpa. Próximo cliente!");
        campoBusca.requestFocus();
    }
}