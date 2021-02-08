package pt.upskill.projeto2.financemanager.accounts;

import pt.upskill.projeto2.financemanager.categories.Category;
import pt.upskill.projeto2.financemanager.date.Date;
import pt.upskill.projeto2.financemanager.filters.BeforeDateSelector;
import pt.upskill.projeto2.financemanager.filters.Filter;
import pt.upskill.projeto2.financemanager.filters.Selector;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
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
            if(line.equals("")){//caso exista uma linha vazia
                continue;
            }
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
                statementLineList.add(StatementLine.newStatement(lineInfo));
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

    public StatementLine getLastStatement(){
        int size= statementLineList.size();
        if(size==0){
            return null;
        }
        return statementLineList.get(size-1);
    }

    public double currentBalance() {
        if(getLastStatement()==null){
            return 0;
        }
        return getLastStatement().getAvailableBalance();
    }

    public double estimatedAverageBalance() {
        int currentYear= new Date().getYear();
        StatementLine lastStatement= getLastStatement();
        if(lastStatement==null|| lastStatement.getDate().getYear()!=currentYear){
            return currentBalance();
        }
        double totalBalance = 0;
        int numDays = 0;
        int daysInAccount;
        Date date;
        Date currentDate= new Date();
        for(StatementLine line: statementLineList){
            if(line.getDate().getYear()==currentYear){
                date=line.getDate();
                if(statementLineList.size()>statementLineList.indexOf(line)+1){
                    //numDays+=date.diffInDays(statementLineList.get(statementLineList.indexOf(line)+1).getDate());
                    daysInAccount=date.diffInDays(statementLineList.get(statementLineList.indexOf(line)+1).getDate());
                }
                else{
                    //numDays+=date.diffInDays(currentDate);
                    daysInAccount=date.diffInDays(currentDate);

                }
                totalBalance+=line.getAvailableBalance()*daysInAccount;
            }
        }
        Date inicioAno=new Date(1,1, currentYear);
        numDays=inicioAno.diffInDays(new Date())-1;
        //fazer alteracao ao numero de dias totais
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

    public void setAdditionalInfo(String additionalInfo) {
        this.additionalInfo = additionalInfo;
    }

    public abstract Category getAccountCategory();

    public void addStatementLine(StatementLine statementLine) {
        if(statementLine.getCategory() == null){
            statementLine.setCategory(getAccountCategory());
        }
        statementLineList.add(statementLine);

        if(statementLineList.size()<2){
            this.startDate=statementLine.getDate();
            this.endDate=statementLine.getDate();
        }
        if(getLastStatement().equals(statementLine)){
            this.endDate=statementLine.getDate();
        }
        statementLineList.sort(StatementLine::compareTo);
    }

    public void removeStatementLinesBefore(Date date) {
        BeforeDateSelector selector=new BeforeDateSelector(date);
        Filter<StatementLine, BeforeDateSelector> filter= new Filter<>(selector);
        statementLineList=(List<StatementLine>) filter.apply(statementLineList);
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
