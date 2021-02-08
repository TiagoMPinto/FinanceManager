package pt.upskill.projeto2.financemanager;

import pt.upskill.projeto2.financemanager.accounts.Account;
import pt.upskill.projeto2.financemanager.date.Date;
import pt.upskill.projeto2.financemanager.date.Month;
import pt.upskill.projeto2.financemanager.gui.PersonalFinanceManagerUserInterface;

import java.io.FileNotFoundException;

/**
 * @author upSkill 2020
 * <p>
 * ...
 */

public class Main {

    public static void main(String[] args) throws FileNotFoundException {

        PersonalFinanceManager personalFinanceManager = new PersonalFinanceManager();
        PersonalFinanceManagerUserInterface gui = new PersonalFinanceManagerUserInterface(
                personalFinanceManager);
        personalFinanceManager.readAccountFiles();//ler os ficheiros na pasta account_info
        personalFinanceManager.readStatementsFile();//ler statements, criar contas que nao existam e se necessario corrigir os valores dos respetivos movimentos
        gui.execute();

    }

}

