import org.chocosolver.solver.Model;

public class exemple {
    public static void main(String[] args) throws Exception {
        parsexml parser = new parsexml("medium.xml");
        parser.parse();

        parser.generateModel();

        Model model = parser.model;
        System.out.println(model);
        
    
    }

}
