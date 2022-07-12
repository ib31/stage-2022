// source originale utilisation du protocole pour simuler une configurtion. la partie en commentaire permet de faire la resauration. 
// on rechagre le model dorigine et on reaffecte les memes affectations.

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

public class App {
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

    public static boolean isInCurrentDom(IntVar var, int val) {
        DisposableValueIterator vit = var.getValueIterator(true);
        while (vit.hasNext()) {
            if (val == vit.next()) {
                return true;
            }
        }
        return false;
    }

    public static IntVar randomVarToRestore(Map<IntVar, Integer> assingMap) {
        int randomIndexAssignationToRestore = new Random().nextInt(assingMap.size());
        int count = -1;
        IntVar varToRestore;
        Iterator<IntVar> iterator = assingMap.keySet().iterator();
        do {
            count++;
            varToRestore = iterator.next();
        } while (iterator.hasNext() && count < randomIndexAssignationToRestore);
        return varToRestore;
    }

    public static void reAssignModel(Map<IntVar, Integer> assingMap, Model model) throws Exception {
        for (IntVar var : assingMap.keySet()) {
            model.arithm(var, "=", assingMap.get(var)).post();
        }
    }

    public static void main(String[] args) throws Exception {
        ArrayList<String> choix = new ArrayList<String>();
        try {
            parsexml parser = new parsexml("toy.xml");
            parser.parse();

            parser.generateModel();

            Model model = parser.model;
            IntVar[] vars = parser.vars;

            Solver solver = model.getSolver();
            solver.propagate();

            ArrayList<IntVar> varNonAssignList = new ArrayList<IntVar>(Arrays.asList(vars));
            // Map<IntVar,Integer> assingMap=new HashMap<IntVar,Integer>();

            while (!varNonAssignList.isEmpty()) {

                IntVar randomVar = varNonAssignList.remove(new Random().nextInt(varNonAssignList.size()));
                int randomVal = pickRandomValueOf(randomVar);
                choix.add(randomVar.getName() + "," + Integer.toString(randomVal));
                /*
                 * int randomVal = parser.pickRandomValueOfOriginDomain(randomVar);
                 * 
                 * while (!isInCurrentDom(randomVar, randomVal)){
                 * IntVar varToRestore = randomVarToRestore(assingMap);
                 * assingMap.remove(varToRestore);
                 * varNonAssignList.add(varToRestore);
                 * parser.generateModel();
                 * model = parser.model;
                 * solver = model.getSolver();
                 * reAssignModel(assingMap,model);
                 * solver.propagate();
                 * }
                 */
                model.arithm(randomVar, "=", randomVal).post();
                solver.propagate();
                System.out.println(model);
                // assingMap.put(randomVar, randomVal);
                varNonAssignList.removeIf(var -> (var.getDomainSize() == 1));

            }

        } catch (Exception e) {
            // si j'ai une exception c'est que je n'ai pas réussi à propager avec choco
            System.out.println("---- echec ----");
            for (String c : choix) {
                System.out.println(c);
            }

        }

    }
}
