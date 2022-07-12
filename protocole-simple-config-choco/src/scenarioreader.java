import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class scenarioreader {
    
    private String nomFichier;
    public ArrayList<String[]> scenario;
    public ArrayList<ArrayList<String[]>> scenarios;
    
    public scenarioreader(String nomFichier){
        this.nomFichier=nomFichier;
    }

    public void parse() throws IOException{
        scenarios = new ArrayList<ArrayList<String[]>>();
        InputStream ips;
        InputStreamReader ipsr = null;
        BufferedReader br = null;
        String ligne;
        if (!nomFichier.contains(".csv"))                   // identifier des pbs de fichiers
            nomFichier += ".csv";

        try {
            FileReader fR = new FileReader(nomFichier);
            fR.close();
        } catch (Exception e) {
            System.out.println("err lectur fichier csv");
        }

        try {                                              // parcourir le fichier une fois: variables et noeuds
            ips = new FileInputStream(nomFichier);
            ipsr = new InputStreamReader(ips);
            br = new BufferedReader(ipsr);
        } catch (Exception e) {
            System.out.println(e.toString());
        }
        while (((ligne=br.readLine())!=null)){
            Pattern p;
            Matcher m;
            p = Pattern.compile("\\{([^,]+),(-?[0-9])\\}");
            m = p.matcher(ligne);
            ArrayList<String[]> s = new ArrayList<String[]>();
            while (m.find()){
                String[] assignation = {m.group(1),m.group(2)};
                s.add(assignation);
            }
            scenarios.add(s);          
        }
        br.close();
    }

    public void getScenario(int k) throws IOException{
        scenario = new ArrayList<String[]>();
        InputStream ips;
        InputStreamReader ipsr = null;
        BufferedReader br = null;
        String ligne;
        if (!nomFichier.contains(".csv"))                   // identifier des pbs de fichiers
            nomFichier += ".csv";

        try {
            FileReader fR = new FileReader(nomFichier);
            fR.close();
        } catch (Exception e) {
            System.out.println("err lectur fichier csv");
        }

        try {                                              // parcourir le fichier une fois: variables et noeuds
            ips = new FileInputStream(nomFichier);
            ipsr = new InputStreamReader(ips);
            br = new BufferedReader(ipsr);
        } catch (Exception e) {
            System.out.println(e.toString());
        }
        
        for (int i=1;i<k;i++){
            ligne=br.readLine();
        }
        ligne=br.readLine();
        Pattern p;
        Matcher m;
        p = Pattern.compile("\\{([^,]+),(-?[0-9])\\}");
        m = p.matcher(ligne);
        while (m.find()){
            String[] assignation = {m.group(1),m.group(2)};
            scenario.add(assignation);
        }
        br.close();
    }



}
