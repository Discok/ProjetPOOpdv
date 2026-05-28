package pdv.servico;

import java.util.*;
import pdv.contrato.INotaFiscal;

public class NotaFiscalBuilder {
    private static final Map<String, INotaFiscal> notas = new LinkedHashMap<>();
    
    static {
        notas.put("TXT", new NotaFiscalTXT());
        notas.put("Impressora Térmica", new NotaFiscalTermica()); 
    }
    
    public static List<String> getTipos() {
        return new ArrayList<>(notas.keySet());
    }
    
    public static INotaFiscal getNota(String tipo) {
        INotaFiscal nota = notas.get(tipo);
        return nota != null ? nota : new NotaFiscalTXT();
    }
}