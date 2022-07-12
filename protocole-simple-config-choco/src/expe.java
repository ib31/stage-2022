import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import com.opencsv.CSVWriter;

import org.chocosolver.solver.Model;
import org.chocosolver.solver.Solver;
import org.chocosolver.solver.variables.IntVar;
import org.chocosolver.util.iterators.DisposableValueIterator;

public class expe {
    public static int pickRandomValueOf(IntVar var) {

        int rvalue = -1515151;
        int randomIndex = new Random().nextInt(var.getDomainSize());
        int count = 0;
        DisposableValueIterator vit = var.getValueIterator(true);
        while (vit.hasNext() && count <= randomIndex) {
            count += 1;
            rvalue = vit.next();
        }
        vit.dispose();
        return rvalue;
    }

    public static void main(String[] args) throws Exception {
        File file = new File("test.csv");
        // create FileWriter object with file as parameter
        FileWriter outputfile = new FileWriter(file,true);

        // create CSVWriter object filewriter object as parameter
        CSVWriter writer = new CSVWriter(outputfile);

        // adding header to csv
        //String[] header = { "temps initialisation", "temps config"};
        //writer.writeNext(header);

        ArrayList<String> timeAssignlist= new ArrayList<String>();
        // commence initialise model
        long initd = System.nanoTime();
        parsexml parser = new parsexml("small.xml");
        parser.parse();
        parser.generateModel();
        Model model = parser.model;
        IntVar[] vars = parser.vars;
        Solver solver = model.getSolver();
        solver.propagate();

        ArrayList<IntVar> varNonAssignList = new ArrayList<IntVar>(Arrays.asList(vars));

        // termine initialise model
        long initf = System.nanoTime();
        long init = initf - initd;

        // commence config
        long confd = System.nanoTime();
        while (!varNonAssignList.isEmpty()) {
            long assignd = System.nanoTime();
            varNonAssignList.removeIf(var -> (var.getDomainSize() == 1));
            if (!varNonAssignList.isEmpty()) {
                // assign et propage commence
                IntVar randomVar = varNonAssignList.remove(new Random().nextInt(varNonAssignList.size()));
                int randomVal = pickRandomValueOf(randomVar);
                model.arithm(randomVar, "=", randomVal).post();
                solver.propagate();
            }
            long assignf = System.nanoTime();
            timeAssignlist.add(Long.toString(assignf-assignd));
        }
        // termine config
        long conff = System.nanoTime();
        long conf = conff - confd;
        long moyenne=0;
        for (String st : timeAssignlist){
            moyenne +=Long.parseLong(st)/timeAssignlist.size();
        }
        String[] data = new String[3+timeAssignlist.size()]; 
        // add data to csv
        data[0]=Long.toString(init);
        data[1]=Long.toString(conf);
        data[2]=Long.toString(moyenne);
        for (int k=0;k<timeAssignlist.size();k++){
            data[k+3]=timeAssignlist.get(k);
        }
        writer.writeNext(data);

        // closing writer connection
        writer.close();

    }
}
