package controller;

import view.ExpenseTrackerView;

import java.util.List;



import model.ExpenseTrackerModel;
import model.Transaction;
public class ExpenseTrackerController {
  
  private ExpenseTrackerModel model;
  private ExpenseTrackerView view;

  public ExpenseTrackerController(ExpenseTrackerModel model, ExpenseTrackerView view) {
    this.model = model;
    this.view = view;

    // Set up view event handlers
    view.getFilterButton().addActionListener(e -> applyFilter());
  }

  public void refresh() {

    // Get transactions from model
    List<Transaction> transactions = model.getTransactions();

    // Pass to view
    view.refreshTable(transactions);

  }

  public boolean addTransaction(double amount, String category) {
    if (!InputValidation.isValidAmount(amount)) {
      return false;
    }
    if (!InputValidation.isValidCategory(category)) {
      return false;
    }
    
    Transaction t = new Transaction(amount, category);
    model.addTransaction(t);
    view.getTableModel().addRow(new Object[]{t.getAmount(), t.getCategory(), t.getTimestamp()});
    refresh();
    return true;
  }
  
  // Other controller methods
  public void applyFilter() {
    List<Transaction> transactions = model.getTransactions();

    double minAmount = 0;
    double maxAmount = Double.MAX_VALUE;

    try {
        String minText = view.getMinAmountField().getText().trim();
        String maxText = view.getMaxAmountField().getText().trim();

        if (!minText.isEmpty()) {
            minAmount = Double.parseDouble(minText);
            if (!InputValidation.isValidAmount(minAmount)) {
                throw new IllegalArgumentException("Minimum amount must be between 0 and 1000.");
            }
        }

        if (!maxText.isEmpty()) {
            maxAmount = Double.parseDouble(maxText);
            if (!InputValidation.isValidAmount(maxAmount)) {
                throw new IllegalArgumentException("Maximum amount must be between 0 and 1000.");
            }
        }

        if (minAmount > maxAmount) {
            throw new IllegalArgumentException("Minimum amount cannot be greater than maximum amount.");
        }

    } catch (NumberFormatException ex) {
        javax.swing.JOptionPane.showMessageDialog(view, "Please enter valid numeric values for min/max amounts.");
        return;
    } catch (IllegalArgumentException ex) {
        javax.swing.JOptionPane.showMessageDialog(view, ex.getMessage());
        return;
    }

    // Filter by amount
    TransactionFilter amountFilter = new AmountFilter(minAmount, maxAmount);
    transactions = amountFilter.filter(transactions);

    // Validate and apply category filter
    String category = view.getFilterCategoryField().getText().trim();
    if (!category.isEmpty()) {
        if (!InputValidation.isValidCategory(category)) {
            javax.swing.JOptionPane.showMessageDialog(view, "Invalid category. Valid options: food, travel, bills, entertainment, other.");
            return;
        }

        TransactionFilter categoryFilter = new CategoryFilter(category);
        transactions = categoryFilter.filter(transactions);
    }

    // Update view with filtered list
    view.refreshTable(transactions);
}

}