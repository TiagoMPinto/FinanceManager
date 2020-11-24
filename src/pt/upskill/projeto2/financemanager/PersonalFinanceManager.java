package pt.upskill.projeto2.financemanager;

import pt.upskill.projeto2.financemanager.accounts.Account;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class PersonalFinanceManager {
	private List<Account> accountsList;

	public PersonalFinanceManager(){
		accountsList=new ArrayList<Account>();
	}

	public void readAccountFiles(File file) throws FileNotFoundException {
		File accountInfo= new File("account_info");
		for(File f: Objects.requireNonNull(accountInfo.listFiles())) {
			Account newAccount = Account.newAccount(f);
			accountsList.add(newAccount);
		}
	}

}
