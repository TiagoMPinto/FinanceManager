package pt.upskill.projeto2.financemanager.accounts.formats;

import pt.upskill.projeto2.financemanager.accounts.StatementLine;

public class LongStatementFormat implements StatementLineFormat {
    @Override
    public String fields() {
        return "Date \tValue Date \tDescription \tDraft \tCredit \tAccounting balance \tAvailable balance ";
    }

    @Override
    public String format(StatementLine objectToFormat) {
        return objectToFormat.getDate().toString()+" \t"+objectToFormat.getValueDate().toString()+" \t"+objectToFormat.getDescription()+" \t"+objectToFormat.getDraft()+" \t"+objectToFormat.getCredit()+" \t"+objectToFormat.getAccountingBalance()+" \t"+objectToFormat.getAvailableBalance();
    }
}
