package pt.upskill.projeto2.financemanager.gui;

import pt.upskill.projeto2.financemanager.PersonalFinanceManager;
import pt.upskill.projeto2.financemanager.categories.Category;
import pt.upskill.projeto2.financemanager.date.Month;
import pt.upskill.projeto2.utils.Menu;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;

/**
 * @author upSkill 2020
 * <p>
 * ...
 */

public class PersonalFinanceManagerUserInterface {

    public PersonalFinanceManagerUserInterface(
            PersonalFinanceManager personalFinanceManager) {
        this.personalFinanceManager = personalFinanceManager;
    }

    private static final String OPT_GLOBAL_POSITION = "Posição Global";
    private static final String OPT_ACCOUNT_STATEMENT = "Movimentos Conta";
    private static final String OPT_LIST_CATEGORIES = "Listar categorias";
    private static final String OPT_ANALISE = "Análise";
    private static final String OPT_SAVE= "Gravar";
    private static final String OPT_EXIT = "Sair";

    private static final String OPT_MONTHLY_SUMMARY = "Evolução global por mês";
    private static final String OPT_PREDICTION_PER_CATEGORY = "Previsão gastos totais do mês por categoria";
    private static final String OPT_ANUAL_INTEREST = "Previsão juros anuais";

    private static final String[] OPTIONS_ANALYSIS = {OPT_MONTHLY_SUMMARY, OPT_PREDICTION_PER_CATEGORY, OPT_ANUAL_INTEREST};
    private static final String[] OPTIONS = {OPT_GLOBAL_POSITION,
            OPT_ACCOUNT_STATEMENT, OPT_LIST_CATEGORIES, OPT_ANALISE, OPT_SAVE, OPT_EXIT};


    private PersonalFinanceManager personalFinanceManager;

    public void execute() throws FileNotFoundException {
        while (true){
            String  string= Menu.<String> requestSelection("Finance Manager - Options", OPTIONS);
            if(string == null || string.isEmpty()){
                break;
            }
            String[] accountNames;
            String  conta="";
            String category="";
            long id=0L;
            switch (string){
                case OPT_ANALISE:
                    String  analise= Menu.<String> requestSelection("Finance Manager - Análise", OPTIONS_ANALYSIS);
                    if(analise == null || analise.isEmpty()){
                        break;
                    }
                    switch (analise){
                        case OPT_MONTHLY_SUMMARY:
                            accountNames = personalFinanceManager.getAccountNames();
                            conta= Menu.<String> requestSelection("Escolha uma conta", accountNames);
                            if(conta== null || conta.isEmpty()){
                                break;
                            }
                            id=Long.parseLong(conta.split(" - ")[1]);
                            //escolher o ano
                            String[] anos = personalFinanceManager.getAccountYears(id);
                            String year= Menu.<String> requestSelection("Escolha o ano:", anos);
                            if(year==null || year.isEmpty()){
                                break;
                            }
                            personalFinanceManager.monthlySummary(id, Integer.parseInt(year));
                            break;
                        case OPT_PREDICTION_PER_CATEGORY:
                            accountNames = personalFinanceManager.getAccountNames();
                            conta= Menu.<String> requestSelection("Escolha uma conta", accountNames);
                            if(conta== null || conta.isEmpty()){
                                break;
                            }
                            id=Long.parseLong(conta.split(" - ")[1]);
                            /*
                            category= Menu.<String> requestSelection("Escolha uma categoria", personalFinanceManager.getCategories());
                            if(category== null || category.isEmpty()){
                                break;
                            }
                            Category categorySearching= Category.nameToCategory(category, Category.readCategories(new File("account_info/categories")));

                             */
                            personalFinanceManager.predictionPerCatergory(id);
                            break;
                        case OPT_ANUAL_INTEREST:
                            accountNames = personalFinanceManager.getAccountNames();
                            conta= Menu.<String> requestSelection("Escolha uma conta", accountNames);
                            if(conta== null || conta.isEmpty()){
                                break;
                            }
                            id=Long.parseLong(conta.split(" - ")[1]);
                            personalFinanceManager.annualInterest(id);
                            break;
                    }
                    break;
                case OPT_LIST_CATEGORIES:
                    //listar categorias
                    //da conta:
                    accountNames = personalFinanceManager.getAccountNames();
                    conta= Menu.<String> requestSelection("Escolha uma conta", accountNames);
                    if(conta== null || conta.isEmpty()){
                        break;
                    }
                    id=Long.parseLong(conta.split(" - ")[1]);
                    personalFinanceManager.categorizeStatements(id);
                    break;
                case OPT_GLOBAL_POSITION:
                    personalFinanceManager.globalPosition();
                    break;
                case OPT_ACCOUNT_STATEMENT:
                    //escolher a conta
                    accountNames = personalFinanceManager.getAccountNames();
                    conta= Menu.<String> requestSelection("Escolha uma conta", accountNames);
                    if(conta==null || conta.isEmpty()){
                        break;
                    }
                    id=Long.parseLong(conta.split(" - ")[1]);
                    personalFinanceManager.accountMovements(id);
                    break;
                case OPT_SAVE:
                    personalFinanceManager.saveInfo();
                    break;
                case OPT_EXIT:
                    System.exit(0);
                    break;


            }

        }
        System.out.println("Finance Manager was Closed!");
    }


}
