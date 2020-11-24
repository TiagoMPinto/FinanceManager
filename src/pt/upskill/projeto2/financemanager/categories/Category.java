package pt.upskill.projeto2.financemanager.categories;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author upSkill 2020
 * <p>
 * ...
 */

public class Category implements Serializable {
    private String name;
    private List<String> tags;
    private static final long serialVersionUID=-9107819223195202547L;


    public Category(String string) {
        this.name=string;
        tags = new ArrayList<String>();
    }

    /**
     * Função que lê o ficheiro categories e gera uma lista de {@link Category} (método fábrica)
     * Deve ser utilizada a desserialização de objetos para ler o ficheiro binário categories.
     *
     * @param fileName - Ficheiro onde estão apontadas as categorias possíveis iniciais, numa lista serializada (por defeito: /account_info/categories)
     * @return uma lista de categorias, geradas ao ler o ficheiro
     */
    public static List<Category> readCategories(File fileName) {
        List<Category> categories= new ArrayList<>();
        // Deserialization
        try {
            // Reading the object from a file
            FileInputStream file = new FileInputStream(fileName);
            ObjectInputStream in = new ObjectInputStream(file);

            // Method for deserialization of object

            categories= (List<Category>)in.readObject();

            in.close();
            file.close();


        } catch(IOException ex) {
            ex.printStackTrace();

        } catch(ClassNotFoundException ex) {
            System.out.println("ClassNotFoundException is caught");

        }

        return categories;
    }

    /**
     * Função que grava no ficheiro categories (por defeito: /account_info/categories) a lista de {@link Category} passada como segundo argumento
     * Deve ser utilizada a serialização dos objetos para gravar o ficheiro binário categories.
     * @param fileName
     * @param categories
     */
    public static void writeCategories(File fileName, List<Category> categories) {
        // Serialization
        try {

            // Saving of object in a file
            FileOutputStream file = new FileOutputStream
                    (fileName);
            ObjectOutputStream out = new ObjectOutputStream
                    (file);

            // Method for serialization of object
            //for(Category cat: categories){
            //    out.writeObject(cat);
            //}
            out.writeObject(categories);

            out.close();
            file.close();

        }

        catch (IOException ex) {
            System.out.println("IOException is caught");
        }

    }

    public boolean hasTag(String tag) {
        for(String t: tags){
            if(t.equals(tag)){
                return true;
            }
        }
        return false;
    }

    public void addTag(String tag) {
        tags.add(tag);

    }

    public String getName() {
        return name;
    }

    public List<String> getTags() {
        return tags;
    }
}
