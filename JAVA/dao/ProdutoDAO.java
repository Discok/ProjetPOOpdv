package projetopoopdv.java.dao;

import projetopoopdv.java.models.Produto;
import projetopoopdv.java.db.Conexao; 

import java.sql.*;
import java.util.*;

public class ProdutoDAO {

    public void inserir(Produto p) {
        String sql = "INSERT INTO produto (nome, codigo, preco, quantidade) VALUES (?,?,?,?)";
        try (Connection c = Conexao.obter(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, p.getNome());
            ps.setString(2, p.getCodigo());
            ps.setDouble(3, p.getPreco());
            ps.setInt   (4, p.getQuantidade());
            ps.executeUpdate();
        } catch (Exception e) { System.out.println(e.getMessage()); }
    }

    public List<Produto> listarTodos() {
        List<Produto> lista = new ArrayList<>();
        try (Connection c = Conexao.obter()
             PreparedStatement ps = c.prepareStatement("SELECT * FROM produto");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Produto p = new Produto();
                p.setId        (rs.getInt   ("id"));
                p.setNome      (rs.getString("nome"));
                p.setCodigo    (rs.getString("codigo"));
                p.setPreco     (rs.getDouble("preco"));
                p.setQuantidade(rs.getInt   ("quantidade"));
                lista.add(p);
            }
        } catch (Exception e) { System.out.println(e.getMessage()); }
        return lista;
    }

    public void atualizar(Produto p) {
        String sql = "UPDATE produto SET nome=?, codigo=?, preco=?, quantidade=? WHERE id=?";
        try (Connection c = Conexao.obter(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, p.getNome());
            ps.setString(2, p.getCodigo());
            ps.setDouble(3, p.getPreco());
            ps.setInt   (4, p.getQuantidade());
            ps.setInt   (5, p.getId());
            ps.executeUpdate();
        } catch (Exception e) { System.out.println(e.getMessage()); }
    }

    public void excluir(int id) {
        try (Connection c = Conexao.obter();
             PreparedStatement ps = c.prepareStatement("DELETE FROM produto WHERE id=?")) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (Exception e) { System.out.println(e.getMessage()); }
    }

}