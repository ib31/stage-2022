import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;

import com.opencsv.CSVWriter;

import org.chocosolver.solver.Model;
import org.chocosolver.solver.Solver;
import org.chocosolver.solver.variables.IntVar;

public class expescenario {

    public static void main(String[] args) throws Exception {
        File file = new File("time-default-small.csv");
        // create FileWriter object with file as parameter
        FileWriter outputfile = new FileWriter(file, true);

        // create CSVWriter object filewriter object as parameter
        CSVWriter writer = new CSVWriter(outputfile);

        // adding header to csv
        // String[] header = { "temps initialisation", "temps config"};
        // writer.writeNext(header);
        long initd = System.nanoTime();
        parsexml parser = new parsexml("small.xml");
        parser.parse();
        parser.generateModel();
        Model model = parser.model;
        IntVar[] vars = parser.vars;
        ArrayList<String> varName = parser.varName;
        Solver solver = model.getSolver();
        solver.propagate();
        // termine initialise model
        long initf = System.nanoTime();
        long init = initf - initd;

        scenarioreader scReader = new scenarioreader("scenariosmall.csv");
        scReader.getScenario(Integer.parseInt(args[0]));

        String[] timeAssignlist = new String[scReader.scenario.size()];
        // commence config
        long confd = System.nanoTime();
        for (int k=0;k<timeAssignlist.length;k++) {
            long assignd = System.nanoTime();
            String[] assignation = scReader.scenario.get(k);
            IntVar randomVar = vars[varName.indexOf(assignation[0])];
            int randomVal = Integer.parseInt(assignation[1]);
            model.arithm(randomVar, "=", randomVal).post();
            solver.propagate();
            long assignf = System.nanoTime();
            timeAssignlist[k]=Long.toString(assignf-assignd);
            
        }
        // termine config
        long conff = System.nanoTime();
        long conf = conff - confd;
        long moyenne=0;
        for (String st : timeAssignlist){
            moyenne +=Long.parseLong(st)/timeAssignlist.length;
        }
        String[] data = new String[3+timeAssignlist.length]; 
        // add data to csv
        data[0]=Long.toString(init);
        data[1]=Long.toString(conf);
        data[2]=Long.toString(moyenne);
        for (int k=0;k<timeAssignlist.length;k++){
            data[k+3]=timeAssignlist[k];
        }
        writer.writeNext(data);

        // closing writer connection
        writer.close();
    }
}
