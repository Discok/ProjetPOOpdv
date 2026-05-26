package pdv.dados;

import java.sql.*;
import java.util.*;
import pdv.banco.Conexao;
import pdv.contrato.IProdutoDados;
import pdv.modelo.Produto;

public class ProdutoDados implements IProdutoDados {
    private final Conexao conexao;
    
    public ProdutoDados(Conexao conexao) {
        this.conexao = conexao;
        criarTabela();
    }
    
    private void criarTabela() {
        String sql = "CREATE TABLE IF NOT EXISTS produtos (" +
            "id INT PRIMARY KEY AUTO_INCREMENT," +
            "codigo_barras VARCHAR(50) UNIQUE NOT NULL," +
            "nome VARCHAR(100) NOT NULL," +
            "preco DECIMAL(10,2) NOT NULL," +
            "estoque INT DEFAULT 0)";
        try (Connection conn = conexao.conectar(); Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(sql);
        } catch (SQLException e) { e.printStackTrace(); }
    }
    
    @Override
    public Optional<Produto> buscarPorCodigo(String codigo) {
        String sql = "SELECT * FROM produtos WHERE codigo_barras = ?";
        try (Connection conn = conexao.conectar(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, codigo);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return Optional.of(new Produto(rs.getInt("id"), rs.getString("codigo_barras"),
                    rs.getString("nome"), rs.getDouble("preco"), rs.getInt("estoque")));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return Optional.empty();
    }
    
    @Override
    public Optional<Produto> buscarPorId(int id) {
        String sql = "SELECT * FROM produtos WHERE id = ?";
        try (Connection conn = conexao.conectar(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return Optional.of(new Produto(rs.getInt("id"), rs.getString("codigo_barras"),
                    rs.getString("nome"), rs.getDouble("preco"), rs.getInt("estoque")));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return Optional.empty();
    }
    
    @Override
public boolean atualizarEstoque(int produtoId, int quantidade) {
    // ❌ REMOVIDA a condição "AND estoque >= ?" para permitir negativo
    String sql = "UPDATE produtos SET estoque = estoque - ? WHERE id = ?";
    
    try (Connection conn = conexao.conectar();
         PreparedStatement stmt = conn.prepareStatement(sql)) {
        
        stmt.setInt(1, quantidade);
        stmt.setInt(2, produtoId);
        
        int rowsAffected = stmt.executeUpdate();
        
        if (rowsAffected > 0) {
            System.out.println("✅ Estoque atualizado para produto " + produtoId + " (-" + quantidade + ")");
            
            // Buscar o novo valor do estoque
            Optional<Produto> p = buscarPorId(produtoId);
            if (p.isPresent()) {
                System.out.println("📊 Novo estoque: " + p.get().getEstoque());
                if (p.get().getEstoque() < 0) {
                    System.out.println("⚠️ ESTOQUE NEGATIVO! (" + p.get().getEstoque() + ") Venda permitida.");
                }
            }
            return true;
        } else {
            System.out.println("❌ Produto " + produtoId + " não encontrado!");
            return false;
        }
        
    } catch (SQLException e) {
        System.err.println("❌ Erro ao atualizar estoque: " + e.getMessage());
        return false;
    }
}
    @Override
    public List<Produto> buscarPorNome(String nome) {
        List<Produto> resultados = new ArrayList<>();
        String sql = "SELECT * FROM produtos WHERE nome LIKE ?";
        try (Connection conn = conexao.conectar(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, "%" + nome + "%");
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                resultados.add(new Produto(rs.getInt("id"), rs.getString("codigo_barras"),
                    rs.getString("nome"), rs.getDouble("preco"), rs.getInt("estoque")));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return resultados;
    }
    
}