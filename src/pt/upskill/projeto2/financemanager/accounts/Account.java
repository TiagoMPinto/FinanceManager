package pt.upskill.projeto2.financemanager.accounts;

import pt.upskill.projeto2.financemanager.categories.Category;
import pt.upskill.projeto2.financemanager.date.Date;
import pt.upskill.projeto2.financemanager.filters.Filter;
import pt.upskill.projeto2.financemanager.filters.Selector;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public abstract class Account {
    private final long id;
    private String accountName;
    private Date startDate;
    private Date endDate;
    private double balance;
    private double interestedRate;
    private List<StatementLine> statementLineList;
    private String additionalInfo="";
    private String oAntonioBurro;

    public Account(long id, String accountName, Date startDate, Date endDate, double balance, double interestedRate, List<StatementLine> statementLineList) {
        this.id = id;
        this.accountName = accountName;
        this.startDate = startDate;
        this.endDate = endDate;
        this.balance = balance;
        this.interestedRate = interestedRate;
        this.statementLineList = statementLineList;
    }
    
    public Account(long id, String accountName, double interestedRate){
        this.id=id;
        this.accountName=accountName;
        startDate= null;
        endDate=null;
        balance=0.0;
        this.interestedRate= interestedRate;
        statementLineList=new ArrayList<>();
    }

    public static Account newAccount(File file) throws FileNotFoundException {
        Scanner scanner= new Scanner(file);
        int i=0;
        List<StatementLine> statementLineList= new ArrayList<>();
        long id=0;
        String accountName = null;
        Date startDate = null;
        Date endDate = null;
        Date date= null;
        String description;
        double draft=0;
        double credit=0;
        double accountingBalance=0;
        double availableBalance=0;
        boolean isDraftAccount=false;
        String additionalInfo="";
        while(scanner.hasNextLine()){
            String line= scanner.nextLine();
            String[] lineInfo= line.split(";");
            if(i==1){
                id=Long.parseLong(lineInfo[1].replaceAll("[^\\d.]", ""));
                accountName= lineInfo[3].trim();
                if(lineInfo[4].equals("DraftAccount ")){
                    isDraftAccount=true;
                }
                if(lineInfo.length>5){
                    additionalInfo=lineInfo[5];
                }
            }
            if(i==2){
                String[] dateInfo= lineInfo[1].split("-");
                startDate= new Date(Integer.parseInt(dateInfo[0]),Integer.parseInt(dateInfo[1]),Integer.parseInt(dateInfo[2]));
            }
            if(i==3){
                String[] dateInfo= lineInfo[1].split("-");
                endDate= new Date(Integer.parseInt(dateInfo[0]),Integer.parseInt(dateInfo[1]),Integer.parseInt(dateInfo[2]));
            }
            if(i>4){
                String[] dateInfo= lineInfo[0].split("-");
                System.out.println(lineInfo[0]);
                date= new Date(Integer.parseInt(dateInfo[0]),Integer.parseInt(dateInfo[1]),Integer.parseInt(dateInfo[2].trim()));//igual ao valueDate
                description= lineInfo[2].trim();
                draft=Double.parseDouble(lineInfo[3]);
                credit=Double.parseDouble(lineInfo[4]);
                accountingBalance=Double.parseDouble(lineInfo[5]);
                availableBalance=Double.parseDouble(lineInfo[6]);
                statementLineList.add(new StatementLine(date, date, description, draft, credit, accountingBalance, availableBalance, null));
            }
            i++;

        }
        Account newAcc = null;
        if(isDraftAccount){
            newAcc= new DraftAccount(id, accountName, startDate, endDate, availableBalance, BanksConstants.normalInterestRate(), statementLineList);
        }
        if(!isDraftAccount){
            newAcc= new SavingsAccount(id, accountName, startDate, endDate, availableBalance, BanksConstants.savingsInterestRate(), statementLineList);
        }
        newAcc.additionalInfo=additionalInfo;
        return newAcc;
    }

    public double getInterestRate() {
        return interestedRate;
    }

    public double currentBalance() {
        if(statementLineList.size()>0) {
            return statementLineList.get(statementLineList.size()-1).getAvailableBalance();
        }
        return 0.0;
    }

    public double estimatedAverageBalance() {
        int currentYear= new Date().getYear();
        if(statementLineList.size()==0){
            return 0;
        }
        StatementLine lastStatement=statementLineList.get(statementLineList.size()-1);
        if(lastStatement.getDate().getYear()!=currentYear){
            return lastStatement.getAvailableBalance();
        }
        int totalBalance = 0;
        int numDays = 0;
        int daysInAccount=0;
        Date date;
        Date currentDate= new Date();
        for(StatementLine line: statementLineList){
            if(line.getDate().getYear()==currentYear){
                date=line.getDate();
                if(statementLineList.size()>statementLineList.indexOf(line)+1){
                    numDays+=date.diffInDays(statementLineList.get(statementLineList.indexOf(line)+1).getDate());
                    daysInAccount=date.diffInDays(statementLineList.get(statementLineList.indexOf(line)+1).getDate());
                    totalBalance+=line.getAvailableBalance()*daysInAccount;
                }
                else{
                    numDays+=date.diffInDays(currentDate);
                    daysInAccount=date.diffInDays(currentDate);
                    totalBalance+=line.getAvailableBalance()*daysInAccount;
                    
                }
            }
        }
        return (totalBalance/numDays);
    }

    public long getId() {
        return id;
    }

    public double totalDraftsForCategorySince(Category category, Date date) {
        double totalDraft=0;
        for(StatementLine line: statementLineList){
            if(line.getCategory()==null){
                continue;
            }
            if(line.getCategory().equals(category) && (line.getDate().compareTo(date)>0 || line.getDate().compareTo(date)==0)){
                totalDraft+= line.getDraft();
            }
        }
        return totalDraft;
    }

    public String getName() {
        return accountName;
    }

    public String additionalInfo() {
        return additionalInfo;
    }

    public Object getStartDate() {
        return startDate;
    }

    public Object getEndDate() {
        return endDate;
    }

    public List<StatementLine> getStatementLineList() {
        return statementLineList;
    }

    public abstract Category getAccountCategory();

    public void addStatementLine(StatementLine statementLine) {
        if(statementLine.getCategory() == null)
            statementLine.setCategory(getAccountCategory());
        statementLineList.add(statementLine);
        this.balance=statementLine.getAvailableBalance();
        if(statementLineList.size()<2){
            this.startDate=statementLine.getDate();
            this.endDate=statementLine.getDate();
        }
        if(statementLineList.get(statementLineList.size()-1).equals(statementLine)){
            this.endDate=statementLine.getDate();
        }
        statementLineList.sort(StatementLine::compareTo);
    }

    public void removeStatementLinesBefore(Date date) {
        int index=0;
        for(StatementLine statementLine: statementLineList){
            if(statementLine.getDate().equals(date)){
                index=statementLineList.indexOf(statementLine);
                break;
            }
        }
        int currentIndex=0;
        while (currentIndex!= index){
            statementLineList.remove(currentIndex);
            currentIndex++;
        }
        statementLineList.sort(StatementLine::compareTo);
    }

    public double totalForMonth(int i, int i1) {
        double total=0;
        for(StatementLine statementLine:statementLineList){
            if(statementLine.getDate().getYear()==i1 && statementLine.getDate().getMonth().equals(Date.intToMonth(i))){
                total+=statementLine.getDraft();
            }
        }
        return total;
    }

    public void autoCategorizeStatements(List<Category> categories) {
        for(StatementLine line: statementLineList){
            for(Category category:categories){
                if(category.hasTag(line.getDescription())){
                    line.setCategory(category);
                }
            }
        }
    }

    public void setName(String other) {
        this.accountName=other;
    }
}
