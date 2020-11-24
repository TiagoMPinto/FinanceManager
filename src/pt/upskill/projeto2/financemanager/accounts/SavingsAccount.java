package pt.upskill.projeto2.financemanager.accounts;

import pt.upskill.projeto2.financemanager.categories.Category;
import pt.upskill.projeto2.financemanager.date.Date;

import java.util.List;

public class SavingsAccount extends Account {

    public static Category savingsCategory = new Category("SAVINGS");

    public SavingsAccount(long id, String my_savings_acc) {
        super(id, my_savings_acc, BanksConstants.savingsInterestRate());


    }
    public SavingsAccount(long id, String accountName, Date startDate, Date endDate, double balance, double interestedRate, List<StatementLine> statementLineList) {
        super(id, accountName, startDate, endDate, balance, interestedRate, statementLineList);
    }

    @Override
    public Category getAccountCategory() {
        return savingsCategory;
    }


}
