package pdv.servico;

import pdv.modelo.Produto;
import pdv.contrato.IBuscaStrategy;
import java.sql.*;
import java.util.*;

public class BuscaPorNome implements IBuscaStrategy {
    private final Connection conexao;
    
    public BuscaPorNome(Connection conexao) {
        this.conexao = conexao;
    }
    
    @Override
    public List<Produto> buscar(String termo) {
        List<Produto> resultados = new ArrayList<>();
        if (termo == null || termo.trim().isEmpty()) return resultados;
        
        String sql = "SELECT * FROM produtos WHERE nome LIKE ? LIMIT 30";
        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setString(1, "%" + termo + "%");
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                resultados.add(new Produto(rs.getInt("id"), rs.getString("codigo_barras"),
                    rs.getString("nome"), rs.getDouble("preco"), rs.getInt("estoque")));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return resultados;
    }
    
    @Override
    public String getTipo() {
        return "Nome do Produto";
    }
}