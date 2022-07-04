import org.chocosolver.solver.Model;
import org.chocosolver.solver.Solver;

public class quickstart {
    public static void main(String[] args) throws Exception {
        Model model=new Model();
        System.out.println(model);

        Solver solver = model.getSolver();

        System.out.print(solver);

        
    }
}
