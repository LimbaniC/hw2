import static org.junit.Assert.*;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import controller.ExpenseTrackerController;
import controller.AmountFilter;
import controller.CategoryFilter;
import model.ExpenseTrackerModel;
import model.Transaction;
import view.ExpenseTrackerView;

public class AlternateTests {

    private ExpenseTrackerModel model;
    private ExpenseTrackerView view;
    private ExpenseTrackerController controller;

    @Before
    public void init() {
        model = new ExpenseTrackerModel();
        view = new ExpenseTrackerView();
        controller = new ExpenseTrackerController(model, view);
    }

    private double computeTotal() {
        return model.getTransactions()
                    .stream()
                    .mapToDouble(Transaction::getAmount)
                    .sum();
    }

    @Test
    public void shouldInsertTransactionSuccessfully() {
        assertTrue(controller.addTransaction(88.88, "entertainment"));
        assertEquals(1, model.getTransactions().size());
        assertEquals(88.88, computeTotal(), 0.01);
    }

    @Test
    public void shouldRejectInvalidEntries() {
        double originalTotal = computeTotal();
        boolean addedNegative = controller.addTransaction(-10.00, "rent");
        boolean addedInvalidCategory = controller.addTransaction(99.99, "luxury");

        assertFalse(addedNegative);
        assertFalse(addedInvalidCategory);

        assertEquals(0, model.getTransactions().size());
        assertEquals(originalTotal, computeTotal(), 0.01);
    }

    @Test
    public void canFilterTransactionsByAmountRange() {
        controller.addTransaction(25.00, "utilities");
        controller.addTransaction(40.00, "health");
        controller.addTransaction(25.00, "groceries");
        controller.addTransaction(90.00, "entertainment");

        AmountFilter filter = new AmountFilter(20.00, 30.00);
        List<Transaction> result = filter.filter(model.getTransactions());

        assertEquals(2, result.size());
        for (Transaction t : result) {
            assertTrue(t.getAmount() >= 20.00 && t.getAmount() <= 30.00);
        }
    }

    @Test
    public void canFilterTransactionsByCategoryMatch() {
        controller.addTransaction(12.00, "subscriptions");
        controller.addTransaction(44.00, "travel");
        controller.addTransaction(39.99, "subscriptions");
        controller.addTransaction(100.00, "bills");

        CategoryFilter filter = new CategoryFilter("subscriptions");
        List<Transaction> result = filter.filter(model.getTransactions());

        assertEquals(2, result.size());
        for (Transaction t : result) {
            assertEquals("subscriptions", t.getCategory());
        }
    }

    @Test
    public void removingTransactionShouldAffectTotal() {
        Transaction toRemove = new Transaction(61.50, "auto");
        model.addTransaction(toRemove);
        assertEquals(1, model.getTransactions().size());

        model.removeTransaction(toRemove);
        assertEquals(0, model.getTransactions().size());
        assertEquals(0.00, computeTotal(), 0.01);
    }
}
