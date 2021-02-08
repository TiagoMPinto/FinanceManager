package pt.upskill.projeto2.financemanager;

import pt.upskill.projeto2.financemanager.accounts.*;
import pt.upskill.projeto2.financemanager.accounts.formats.FileAccountFormat;
import pt.upskill.projeto2.financemanager.accounts.formats.LongStatementFormat;
import pt.upskill.projeto2.financemanager.categories.Category;
import pt.upskill.projeto2.financemanager.date.Date;
import pt.upskill.projeto2.financemanager.date.Month;

import javax.swing.plaf.nimbus.State;
import java.io.EOFException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;

public class PersonalFinanceManager {
	private List<Account> accountsList;

	public PersonalFinanceManager(){
		accountsList=new ArrayList<Account>();
	}

	public void readAccountFiles() throws FileNotFoundException {
		File accountInfo= new File("account_info");
		for(File f: Objects.requireNonNull(accountInfo.listFiles())) {
			if(f.getName().equals("categories")){
				continue;
			}
			Account newAccount = Account.newAccount(f);
			accountsList.add(newAccount);
		}
	}

	public void readStatementsFile() throws FileNotFoundException {
		File statementsFile= new File("statements");
		Scanner scanner;
		for(File f: Objects.requireNonNull(statementsFile.listFiles())) {
			scanner=new Scanner(f);
			int i=0;
			long id = 0;
			String accountName = null;
			boolean isDraftAccount=false;
			String additionalInfo="";
			Date startDate=null;
			Date endDate=null;
			List<StatementLine> statementLineList= new ArrayList<>();
			while(scanner.hasNextLine()){
				String line= scanner.nextLine();
				String[] lineInfo=line.split(";");
				if(i==2){ //info da conta, id, nome...
					id=Long.parseLong(lineInfo[1].replaceAll("[^\\d.]", ""));
					accountName= lineInfo[3].trim();
					if(lineInfo[4].equals("DraftAccount ")){
						isDraftAccount=true;
					}
					if(lineInfo.length>5){
						additionalInfo=lineInfo[5];
					}
				}
				if(i==3){//starting date
					String[] dateInfo= lineInfo[1].split("-");
					startDate= new Date(Integer.parseInt(dateInfo[0]),Integer.parseInt(dateInfo[1]),Integer.parseInt(dateInfo[2]));
				}
				if(i==4){//end date
					String[] dateInfo= lineInfo[1].split("-");
					endDate= new Date(Integer.parseInt(dateInfo[0]),Integer.parseInt(dateInfo[1]),Integer.parseInt(dateInfo[2]));
				}

				if(i>6){
					statementLineList.add(StatementLine.newStatement(lineInfo));
				}
				i++;
			}
			Account newAcc = null;
			double availableBalance = 0;
			if(isDraftAccount){
				newAcc= new DraftAccount(id, accountName, startDate, endDate, availableBalance, BanksConstants.normalInterestRate(), statementLineList);
			}
			if(!isDraftAccount){
				newAcc= new SavingsAccount(id, accountName, startDate, endDate, availableBalance, BanksConstants.savingsInterestRate(), statementLineList);
			}
			newAcc.setAdditionalInfo(additionalInfo);

			boolean exists=false;
			for(Account account:accountsList){
				if (account.getId() == newAcc.getId()) {
					exists = true;
					boolean lineExists=false;
					for(StatementLine lineNewAcc: newAcc.getStatementLineList()){
						for(StatementLine lineAcc: account.getStatementLineList()){
							if(lineNewAcc.getDate().equals(lineAcc.getDate()) && lineNewAcc.getDescription().equals(lineAcc.getDescription()) && lineNewAcc.getAvailableBalance()==(lineAcc.getAvailableBalance())){
								lineExists=true;
								break;
							}
						}
						if(!lineExists){
							account.addStatementLine(lineNewAcc);
						}
					}
					checkStatements(account);
					break;
				}
			}
			if(!exists){
				accountsList.add(newAcc);
			}

		}

	}

	private void checkStatements(Account account) {
		int i=0;
		double previousBalance=0;
		double availableBalance=0;
		double draft=0;
		double credit=0;
		for(StatementLine statementLine: account.getStatementLineList()){
			if(i==0){
				previousBalance=statementLine.getAvailableBalance();
			}
			if(i!=0){
				availableBalance= statementLine.getAvailableBalance();
				draft=availableBalance-previousBalance- statementLine.getCredit();
				if(draft>0){
					statementLine.setCredit(statementLine.getCredit()+draft);
				}else {
					statementLine.setDraft((double) Math.round(draft * 100) / 100);//para arredondar a 2 casas decimais
				}
				previousBalance= statementLine.getAccountingBalance();
			}
			i=1;
		}
	}

	public Account searchAccount(long id){
		for(Account account:accountsList){
			if(account.getId()==id){
				return account;
			}
		}
		return null;
	}




	public void globalPosition(){
		System.out.println("Posição Global");
		System.out.println();
		System.out.println("Número da Conta\t| Saldo");
		System.out.println("------------------------");
		for(Account account:accountsList){
			System.out.println(account.getId()+"\t| "+account.currentBalance());
		}

	}





	public void accountMovements(long id){
		Account account= searchAccount(id);
		LongStatementFormat f= new LongStatementFormat();
		String date;
		String description;
		double draft;
		double credit;
		double accounting;
		double available;
		System.out.println("Movimentos da conta:");
		System.out.println("Account "+id);
		//System.out.println(f.fields());
		String[] header = {"Date", "Description", "Draft", "Credit", "Accounting balance", "Available Balance"};
		System.out.printf("%-13s %-14s %-8s %-9s %-17s%n", header[0], header[1], header[2], header[3], header[5]);
		System.out.println("  -------------------------------------------------------------------");
		if(account!=null){
			for(StatementLine line: account.getStatementLineList()){
				//System.out.println(f.format(line));
				date = line.getDate().toString();  // funciona se for Date date = "uma data"
				description = line.getDescription();
				draft = line.getDraft();
				credit = line.getCredit();
				accounting= line.getAccountingBalance();
				available = line.getAvailableBalance();
				System.out.printf("%-13s %-14s %-8.2f %-9.2f %-17.2f%n", date, description, draft, credit, available);

			}
		}
	}

	public List<Account> getAccountsList() {
		return accountsList;
	}

	public String[] getAccountNames() {
		String[] accountNames= new String[accountsList.size()];
		int i=0;
		for(Account account: accountsList){
			String accountInfo= account.getName()+" - "+account.getId();
			accountNames[i]=accountInfo;
			i++;
		}
		return accountNames;

	}

	public String[] getCategories(){
		List<Category> categoryList=Category.readCategories(new File("account_info/categories"));
		String[] categoriesNames= new String[categoryList.size()];
		int i=0;
		for(Category category:categoryList){
			categoriesNames[i]= category.getName();
			i++;
		}
		return categoriesNames;
	}

	public String[] getAccountYears(long id){
		List<Integer> anosList= new ArrayList<>();
		Account account=searchAccount(id);
		for(StatementLine line:account.getStatementLineList()){
			int year=line.getDate().getYear();
			if(!anosList.contains(year)){
				anosList.add(year);
			}

		}
		String[] anos= new String[anosList.size()];
		for(int i=0; i< anosList.size(); i++){
			anos[i]= String.valueOf(anosList.get(i));
		}
		return anos;
	}

	public void monthlySummary(long id, int year) {
		System.out.println("Evolução global por mês do ano "+year);
		System.out.println("-------------------------------------");
		Account account= searchAccount(id);
		System.out.println("Account\t\t"+account.getId()+"\t"+account.getName());
		System.out.println("-------------------------------------");
		System.out.printf("%-12s %-12s %-8.2s %-8.2s %-8.2s%n", "Month",  "Draft", "Credit", "Accounting", "Available");
		double totalDraft;
		double totalCredit;
		double finalAccounting;
		double finalAvailable;
		int day;
		StatementLine finalStatement;
		Month[] months= Month.values();
		for(Month month:months){
			totalDraft=0;
			totalCredit=0;
			finalAccounting=0;
			finalAvailable=0;
			day=0;
			finalStatement = null;
			if(month.equals(Month.NONE)){
				continue;
			}
			for(StatementLine statementLine: account.getStatementLineList()){
				if(statementLine.getValueDate().getMonth().equals(month) && statementLine.getValueDate().getYear()==year){
					totalCredit+=statementLine.getCredit();
					totalDraft+=statementLine.getDraft();
					finalAccounting=statementLine.getAccountingBalance();
					finalAvailable=statementLine.getAvailableBalance();
				}
			}
			//String frase=month.toString().toUpperCase()+";"+totalDraft+";"+totalCredit+";"+finalAccounting+";"+finalAvailable;
			System.out.printf("%-12s %-12s %-8.2f %-8.2f %-8.2f%n", month.toString().toUpperCase(), totalDraft, totalCredit, finalAccounting, finalAvailable);
		}
	}

	public void categorizeStatements(long id) {
		Account account= searchAccount(id);
		File categoriesFile= new File("account_info/categories");
		List<Category> categories= Category.readCategories(categoriesFile);
		List<StatementLine> statementLineList= account.getStatementLineList();
		Scanner keyboard= new Scanner(System.in);
		Category category1=null;
		for(StatementLine line: statementLineList){
			if(categories.size()==0){
				System.out.println("Não existe categoria para a descrição: "+line.getDescription());
				System.out.println("Escreva a categoria que pretende associar a esta compra.");
				String category= keyboard.nextLine();//buscar a categoria
				category1= new Category(category.toUpperCase());//criar a categoria
				category1.addTag(line.getDescription());//adicionar a tag
				line.setCategory(category1);//definir a categoria
				categories.add(category1);//adicionar à lista de categorias
			}else{
				for(Category category:categories){
					if(category.hasTag(line.getDescription())){
						line.setCategory(category);
					}
				}
				if(line.getCategory()==null){//quer dzr que ainda nao existe na lista
					System.out.println("Não existe categoria para a descrição: " + line.getDescription());
					System.out.println("Escreva a categoria que pretende associar a esta compra.");
					String categoryReceived = keyboard.nextLine();
					category1=new Category(categoryReceived.toUpperCase());
					category1.addTag(line.getDescription());
					line.setCategory(category1);
					categories.add(category1);
				}
			}
		}
		keyboard.close();
		Category.writeCategories(categoriesFile, categories);
	}

	public void predictionPerCatergory(long id) {
		Account account= searchAccount(id);
		Month currentMonth= new Date().getMonth();
		int daysMonth=Date.lastDayOf(currentMonth, new Date().getYear());
		int lastDay=0;
		double totalDraft=0;
		double expectedDraft=0;
		List<Category> categoryList= Category.readCategories(new File("account_info/categories"));
		System.out.println("Previsão de gastos por categoria do mês currente");
		System.out.println("Account "+id);
		for(Category  category: categoryList){
			for(StatementLine line: account.getStatementLineList()) {
				if (line.getCategory()!=null && line.getCategory().getName().equals(category.getName()) && line.getDate().getMonth().equals(currentMonth)) {
					totalDraft += line.getDraft();
					lastDay = line.getDate().getDay();
				}
			}
			expectedDraft=(totalDraft*daysMonth)/lastDay;
			System.out.println(category.getName().toUpperCase()+": "+expectedDraft);
			totalDraft=0;
		}

	}

	public void annualInterest(long id) {
		Account account= searchAccount(id);
		System.out.println("Previsão dos juros anuais");
		System.out.println("Account "+id);
		double juros=0;
		if(account instanceof SavingsAccount){
			juros=account.getInterestRate()*account.currentBalance();
		}
		else {
			juros=account.estimatedAverageBalance()* account.getInterestRate();
		}
		System.out.println("Juros :"+ juros);
	}

	public void saveInfo() throws FileNotFoundException {
		FileAccountFormat f= new FileAccountFormat();
		for(Account account:accountsList){
			File file= new File("account_info/"+account.getId()+".csv");
			PrintWriter printWriter= new PrintWriter(file);
			printWriter.print(f.format(account));
			printWriter.close();
		}
	}
}
