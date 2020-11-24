package pt.upskill.projeto2.financemanager.accounts;

import pt.upskill.projeto2.financemanager.categories.Category;
import pt.upskill.projeto2.financemanager.date.Date;


public class StatementLine implements Comparable<StatementLine> {
	private Date date;
	private Date valueDate;
	private String description;
	private double draft;
	private double credit;
	private double accountingBalance;
	private double availableBalance;
	private Category category;

	public StatementLine(Date date, Date valueDate, String description, double draft, double credit, double accountingBalance, double availableBalance, Category category) {
		if(date==null || valueDate==null){
			throw new IllegalArgumentException("Date not assigned.");
		}
		this.date = date;
		this.valueDate = valueDate;
		if(description==null || description.equals("")) {
			throw new IllegalArgumentException("Description not assigned.");
		}
		this.description = description;
		if(draft>0){
			throw new IllegalArgumentException("Draft value not accepted.");
		}
		this.draft = draft;
		if(credit<0){
			throw new IllegalArgumentException("Credit value not accepted.");
		}
		this.credit = credit;
		this.accountingBalance = accountingBalance;
		this.availableBalance = availableBalance;
		this.category = category;
	}

	public Date getDate() {
		return date;
	}

	public Date getValueDate() {
		return valueDate;
	}

	public String getDescription() {
		return description;
	}

	public double getCredit() {
		return credit;
	}

	public double getDraft() {
		return draft;
	}

	public double getAccountingBalance() {
		return accountingBalance;
	}

	public double getAvailableBalance() {
		return availableBalance;
	}

	public Category getCategory() {
		return category;
	}

	public void setCategory(Category cat) {
		this.category=cat;
		
	}

	@Override
	public String toString() {
		return date.toString()+" ;" + valueDate.toString() +
				" ;" + description +" ;"+draft+ " ;"+credit+" ;"+accountingBalance+" ;"+accountingBalance;
	}

	@Override
	public int compareTo(StatementLine o) {
		return this.getDate().compareTo(o.getDate());
	}
}
