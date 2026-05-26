package pdv.servico;

import java.io.*;
import java.time.format.DateTimeFormatter;
import javax.print.*;
import pdv.contrato.INotaFiscal;
import pdv.modelo.Venda;
import pdv.modelo.VendaItem;

public class NotaFiscalTermica implements INotaFiscal {
    
    private static final String QUEBRA = "\n";
    private static final String LINHA = "--------------------------------";
    private static final String LINHA_DUPLA = "================================";
    
    @Override
    public void emitir(Venda venda) {
        try {
            // 1. Buscar impressora térmica disponível
            PrintService impressora = encontrarImpressoraTermica();
            
            if (impressora == null) {
                System.err.println("❌ Nenhuma impressora térmica encontrada!");
                // Fallback para TXT
                new NotaFiscalTXT().emitir(venda);
                return;
            }
            
            // 2. Criar o conteúdo da nota
            String conteudo = gerarConteudoNota(venda);
            
            // 3. Enviar para impressão
            enviarParaImpressora(impressora, conteudo);
            
            System.out.println("🖨️ Nota fiscal enviada para impressora térmica!");
            
        } catch (Exception e) {
            System.err.println("❌ Erro ao imprimir: " + e.getMessage());
            // Fallback: salvar como TXT
            new NotaFiscalTXT().emitir(venda);
        }
    }
    
    private PrintService encontrarImpressoraTermica() {
        PrintService[] services = PrintServiceLookup.lookupPrintServices(null, null);
        
        // Palavras-chave para identificar impressoras térmicas
        String[] palavrasChave = {"EPSON", "THERMAL", "TICKET", "POS", "BEMATECH", "ELGIN", "DARUMA"};
        
        for (PrintService service : services) {
            String nome = service.getName().toUpperCase();
            for (String chave : palavrasChave) {
                if (nome.contains(chave)) {
                    System.out.println("✅ Impressora encontrada: " + service.getName());
                    return service;
                }
            }
        }
        
        // Se não encontrar, usa a impressora padrão
        PrintService padrao = PrintServiceLookup.lookupDefaultPrintService();
        if (padrao != null) {
            System.out.println("⚠️ Usando impressora padrão: " + padrao.getName());
        }
        return padrao;
    }
    
    private String gerarConteudoNota(Venda venda) {
        StringBuilder nota = new StringBuilder();
        
        // Inicializar impressora
        nota.append(comandoInicializar());
        
        // Centralizar
        nota.append(comandoCentralizar());
        
        // Cabeçalho
        nota.append("═".repeat(32)).append(QUEBRA);
        nota.append("     PDV - NOTA FISCAL     ").append(QUEBRA);
        nota.append("═".repeat(32)).append(QUEBRA);
        nota.append(QUEBRA);
        
        // Data e hora
        nota.append(comandoEsquerda());
        nota.append("Data: ").append(venda.getDataHora().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"))).append(QUEBRA);
        nota.append(LINHA).append(QUEBRA);
        
        // Itens
        nota.append(String.format("%-3s %-20s %8s", "Qtd", "Produto", "R$")).append(QUEBRA);
        nota.append(LINHA).append(QUEBRA);
        
        for (VendaItem item : venda.getItens()) {
            String nome = item.getNomeProduto();
            if (nome.length() > 20) nome = nome.substring(0, 20);
            nota.append(String.format("%-3d %-20s %8.2f", 
                item.getQuantidade(), 
                nome, 
                item.getSubtotal())).append(QUEBRA);
        }
        
        nota.append(LINHA).append(QUEBRA);
        
        // Total
        nota.append(comandoNegrito());
        nota.append(String.format("%26s %8.2f", "TOTAL:", venda.getTotal())).append(QUEBRA);
        nota.append(comandoNegritoOff());
        
        nota.append(LINHA_DUPLA).append(QUEBRA);
        
        // Rodapé
        nota.append(comandoCentralizar());
        nota.append("Obrigado pela compra!").append(QUEBRA);
        nota.append("Volte sempre!").append(QUEBRA);
        nota.append(QUEBRA).append(QUEBRA);
        
        // Comando de corte (se disponível)
        nota.append(comandoCorte());
        
        return nota.toString();
    }
    
    private void enviarParaImpressora(PrintService impressora, String conteudo) throws IOException {
        try {
            // Método 1: Via PrintService (se tiver driver)
            DocFlavor flavor = DocFlavor.BYTE_ARRAY.AUTOSENSE;
            Doc doc = new SimpleDoc(conteudo.getBytes("UTF-8"), flavor, null);
            DocPrintJob job = impressora.createPrintJob();
            job.print(doc, null);
            
        } catch (Exception e) {
            // Método 2: Via porta serial/paralela (comandos diretos)
            System.err.println("Erro na impressão via driver, tentando porta direta...");
            
            // Tenta portas comuns (USB convertida em serial)
            String[] portas = {"COM3", "COM4", "/dev/ttyUSB0", "//./USB001"};
            for (String porta : portas) {
                try (FileOutputStream out = new FileOutputStream(porta)) {
                    out.write(conteudo.getBytes("UTF-8"));
                    out.flush();
                    System.out.println("✅ Impressão via porta: " + porta);
                    return;
                } catch (FileNotFoundException ex) {
                    // Tentar próxima porta
                }
            }
            throw new IOException("Nenhuma porta encontrada");
        }
    }
    
    // ========== COMANDOS ESC/POS ==========
    
    private String comandoInicializar() {
        return "\u001B@";  // ESC @
    }
    
    private String comandoCentralizar() {
        return "\u001BA\u0001";  // ESC A 1 (centralizar)
    }
    
    private String comandoEsquerda() {
        return "\u001BA\u0000";  // ESC A 0 (esquerda)
    }
    
    private String comandoNegrito() {
        return "\u001BE\u0001";  // ESC E 1 (negrito ligado)
    }
    
    private String comandoNegritoOff() {
        return "\u001BE\u0000";  // ESC E 0 (negrito desligado)
    }
    
    private String comandoCorte() {
        return "\u001D\u0056\u0001";  // GS V 1 (corte parcial)
    }
    
    @Override
    public String getTipo() {
        return "Impressora Térmica";
    }
}