import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args){
        File file = new File("program.txt");
        Scanner s = null;
        String content;
        try {
            s = new Scanner(file).useDelimiter("\\Z");
            content=s.next();
        } catch (FileNotFoundException e) {
            System.out.println("file not found");
            throw new RuntimeException(e);
        }
        //System.out.println(content);
        Automate automate =new Automate(content);
        automate.Process();
    }
}
