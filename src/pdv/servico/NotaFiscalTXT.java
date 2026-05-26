package pdv.servico;

import java.io.*;
import java.time.format.DateTimeFormatter;
import pdv.contrato.INotaFiscal;
import pdv.modelo.Venda;
import pdv.modelo.VendaItem;

public class NotaFiscalTXT implements INotaFiscal {
    
    @Override
    @SuppressWarnings("CallToPrintStackTrace")
    public void emitir(Venda venda) {
        StringBuilder nota = new StringBuilder();
        nota.append("═".repeat(50)).append("\n");
        nota.append("              NOTA FISCAL              \n");
        nota.append("═".repeat(50)).append("\n\n");
        nota.append("Data: ").append(venda.getDataHora().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"))).append("\n");
        nota.append("─".repeat(50)).append("\n");
        nota.append(String.format("%-4s %-35s %8s\n", "Qtd", "Produto", "Subtotal"));
        nota.append("─".repeat(50)).append("\n");
        
        for (VendaItem item : venda.getItens()) {
            String nome = item.getNomeProduto().length() > 33 ? item.getNomeProduto().substring(0, 33) : item.getNomeProduto();
            nota.append(String.format("%-4d %-35s R$ %7.2f\n", item.getQuantidade(), nome, item.getSubtotal()));
        }
        
        nota.append("─".repeat(50)).append("\n");
        nota.append(String.format("%45s R$ %7.2f\n", "TOTAL:", venda.getTotal()));
        nota.append("═".repeat(50)).append("\n");
        nota.append("         Obrigado pela compra!         \n");
        nota.append("═".repeat(50)).append("\n");
        
        String nomeArquivo = "nota_" + System.currentTimeMillis() + ".txt";
        try (FileWriter writer = new FileWriter(nomeArquivo)) {
            writer.write(nota.toString());
            System.out.println("📄 Nota salva: " + nomeArquivo);
        } catch (IOException e) { 
            e.printStackTrace(); 
        }
        
        System.out.println(nota.toString());
    }
    
    @Override
    public String getTipo() {
        return "TXT";
    }
}