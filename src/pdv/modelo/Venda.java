package pdv.modelo;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Venda {
    private final List<VendaItem> itens;
    private final LocalDateTime dataHora;
    private double total;
    
    public Venda() {
        this.itens = new ArrayList<>();
        this.dataHora = LocalDateTime.now();
        this.total = 0;
    }
    
    public void adicionarItem(VendaItem item) {
        for (VendaItem existente : itens) {
            if (existente.getProdutoId() == item.getProdutoId()) {
                existente.setQuantidade(existente.getQuantidade() + item.getQuantidade());
                recalcularTotal();
                return;
            }
        }
        itens.add(item);
        recalcularTotal();
    }
    
    public void removerItem(int indice) {
        if (indice >= 0 && indice < itens.size()) {
            itens.remove(indice);
            recalcularTotal();
        }
    }
    
    private void recalcularTotal() {
        total = itens.stream().mapToDouble(VendaItem::getSubtotal).sum();
    }
    
    public List<VendaItem> getItens() { return new ArrayList<>(itens); }
    public LocalDateTime getDataHora() { return dataHora; }
    public double getTotal() { return total; }
    public boolean estaVazia() { return itens.isEmpty(); }
    public int getQuantidadeItens() { return itens.size(); }
}