package pdv;

import javax.swing.SwingUtilities;
import pdv.banco.Conexao;
import pdv.banco.ConexaoMySQL;
import pdv.dados.ProdutoDados;
import pdv.dados.VendaDados;
import pdv.servico.EstoqueServico;
import pdv.servico.ProdutoServico;
import pdv.servico.VendaServico;
import pdv.tela.TelaPDV;

public class Main {
    public static void main(String[] args) {
        try {
            // Usando a interface Conexao
            Conexao conexao = new ConexaoMySQL();
            
            // Repositórios
            ProdutoDados produtoDados = new ProdutoDados(conexao);
            VendaDados vendaDados = new VendaDados(conexao);
            
            // Serviços
            ProdutoServico produtoServico = new ProdutoServico(produtoDados);
            EstoqueServico estoqueServico = new EstoqueServico(produtoDados);
            VendaServico vendaServico = new VendaServico(vendaDados, estoqueServico);
            
            // Abrir tela
            SwingUtilities.invokeLater(() -> {
                new TelaPDV(produtoServico, vendaServico).setVisible(true);
            });
            
            // Fechar conexão ao sair
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                conexao.desconectar();
            }));
            
        } catch (Exception e) {
            System.err.println("Erro ao iniciar: " + e.getMessage());
            e.printStackTrace();
        }
    }
}