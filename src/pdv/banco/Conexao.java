package pdv.banco;

import java.sql.Connection;
import java.sql.SQLException;

public interface Conexao {
    Connection conectar() throws SQLException;
    void desconectar();
}