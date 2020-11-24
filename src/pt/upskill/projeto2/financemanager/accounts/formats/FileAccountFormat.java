package pt.upskill.projeto2.financemanager.accounts.formats;

import pt.upskill.projeto2.financemanager.accounts.Account;
import pt.upskill.projeto2.financemanager.accounts.StatementLine;
import pt.upskill.projeto2.financemanager.date.Date;

public class FileAccountFormat implements Format<Account> {

    @Override
    public String format(Account account) {
        String nl = System.getProperty("line.separator");
        String toReturn="Account Info - " + new Date().toString() + nl
                +"Account  ;"+account.getId()+" ; EUR  ;"+account.getName()+" ;"+account.getClass().getSimpleName()+" ;" + nl
                + "Start Date ;"+account.getStartDate().toString()+ nl
                + "End Date ;"+account.getEndDate().toString()+ nl
                + "Date ;Value Date ;Description ;Draft ;Credit ;Accounting balance ;Available balance" + nl;
        for(StatementLine statementLine: account.getStatementLineList()){
            toReturn+=statementLine.toString()+nl;
        }
        return toReturn;
    }
}
