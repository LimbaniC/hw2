package controller;

import java.util.List;
import java.util.stream.Collectors;
import model.Transaction;

public class CategoryFilter implements TransactionFilter {
    private final String categoryToMatch;

    public CategoryFilter(String categoryToMatch) {
        this.categoryToMatch = categoryToMatch;
    }

    @Override
    public List<Transaction> filter(List<Transaction> transactions) {
        return transactions.stream()
                .filter(t -> t.getCategory().equalsIgnoreCase(categoryToMatch))
                .collect(Collectors.toList());
    }
}
