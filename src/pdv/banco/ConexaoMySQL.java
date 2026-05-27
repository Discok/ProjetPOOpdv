package pdv.banco;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexaoMySQL implements Conexao {
    private static final String HOST = "127.0.0.1";
    private static final String PORTA = "3306";
    private static final String BANCO = "pdviewbanco";  // ← Nome do seu banco
    private static final String USUARIO = "root";
    private static final String SENHA = "123456";  // ← Tente vazio, root, ou 123456
    
    private Connection conexao;
    
    @Override
    public Connection conectar() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("✅ Driver carregado!");
            
            String url = String.format("jdbc:mysql://%s:%s/%s?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC", 
                HOST, PORTA, BANCO);
            
            System.out.println("Conectando a: " + url);
            conexao = DriverManager.getConnection(url, USUARIO, SENHA);
            System.out.println("✅ Conectado com sucesso!");
            
        } catch (ClassNotFoundException e) {
            throw new SQLException("Driver não encontrado", e);
        } catch (SQLException e) {
            System.err.println("❌ Erro: " + e.getMessage());
            throw e;
        }
        
        return conexao;
    }
    
    @Override
    public void desconectar() {
        if (conexao != null) {
            try {
                conexao.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}