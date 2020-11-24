package pt.upskill.projeto2.financemanager.accounts;

import pt.upskill.projeto2.financemanager.categories.Category;
import pt.upskill.projeto2.financemanager.date.Date;

import java.util.ArrayList;
import java.util.List;

public class DraftAccount extends Account {

    public DraftAccount(long id, String draft_acc) {
        super(id, draft_acc, BanksConstants.normalInterestRate());

    }
    public DraftAccount(long id, String accountName, Date startDate, Date endDate, double balance, double interestedRate, List<StatementLine> statementLineList) {
       super(id, accountName, startDate, endDate, balance, interestedRate, statementLineList);
    }

    @Override
    public Category getAccountCategory() {
        return null;
    }


}
