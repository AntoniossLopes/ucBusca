import java.io.*;
import java.util.*;

/**
 * Class que trata de escrever  e ler os ficheiros objetos associados aos users
 *
 */

public class UserRead implements Serializable {
    List<User> users = Collections.synchronizedList(new ArrayList<>());

    public void escreveUsers(List<User> users) {
        try {
            FileOutputStream fos = new FileOutputStream("ficheiroUsers.ser");
            ObjectOutputStream oos = new ObjectOutputStream(fos);

            oos.writeObject(users);
            oos.close();


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }catch ( IOException e) {
            e.printStackTrace();
        }
    }

    public void leUsers() throws IOException, ClassNotFoundException {

        try {
            File ficheiro = new File("ficheiroUsers.ser");
            if (ficheiro.length() == 0) {
                this.users = new ArrayList<>();
            } else {

                FileInputStream fis = new FileInputStream("ficheiroUsers.ser");
                ObjectInputStream ois = new ObjectInputStream(fis);

                this.users = (List<User>) ois.readObject();

                fis.close();
                ois.close();
            }
        } catch (IOException e) {
            System.out.println("Erro no ficheiro");
        } catch (ClassNotFoundException e) {
            System.out.println("Erro no ficheiro");
        }
    }

    public List<User> getUsers() {
        return users;
    }

    public String insereUser(String nome , String password)
    {
        int id = 0 ;
        for(int i = 0 ; i < users.size();i++)
        {
            if(users.get(i).getNome().equals(nome))
            {
                return("Error");
            }
            id++;
        }
        User novo = new User(id,nome,password);
        users.add(novo);
        return("Done");
    }

    public String checkUser(String nome,String password)
    {
        for(User checking : users)
        {
            if(nome.equals(checking.getNome())){
                if(password.equals(checking.getPassword())) {
                    return "Done";
                }
                return "Error";
            }
        }
        return "Error";
    }
}
