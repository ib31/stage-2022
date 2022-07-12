import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;

import org.chocosolver.solver.Model;
import org.chocosolver.solver.Solver;
import org.chocosolver.solver.variables.IntVar;
import org.chocosolver.util.iterators.DisposableValueIterator;

public class expe2 {
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

        parsexml parser = new parsexml("big.xml");
        parser.parse();

            parser.generateModel();

            Model model = parser.model;
            IntVar[] vars = parser.vars;
    
            Solver solver = model.getSolver();
            solver.propagate();

            //System.out.println(model);
    
            ArrayList<IntVar> varNonAssignList = new ArrayList<IntVar>(Arrays.asList(vars));
            while (!varNonAssignList.isEmpty()) {
                varNonAssignList.removeIf(var -> (var.getDomainSize() == 1));
                if (!varNonAssignList.isEmpty()) {
                    IntVar randomVar = varNonAssignList.remove(new Random().nextInt(varNonAssignList.size()));
                    int randomVal = pickRandomValueOf(randomVar);
                    model.arithm(randomVar, "=", randomVal).post();
                    solver.propagate();
                }
            }
        
    }
}
