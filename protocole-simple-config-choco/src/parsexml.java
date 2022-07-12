
import java.util.ArrayList;
import java.util.Random;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;

import org.chocosolver.solver.Model;
import org.chocosolver.solver.constraints.extension.Tuples;
import org.chocosolver.solver.exception.ContradictionException;
import org.chocosolver.solver.variables.IntVar;
import org.chocosolver.util.iterators.DisposableValueIterator;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;

import java.io.File;

import java.util.regex.*;

public class parsexml {

   private String nomFichier;

   // domains
   private int nbDomains;
   public int[][] dom;
   private ArrayList<String> domName;

   // variables
   private int nbVariables;
   public ArrayList<String> varName;
   private ArrayList<String> varDomain;

   // relations
   private int nbRelations;
   private ArrayList<String> relName;
   private int[] relArity;
   private int[] relNbTuples;
   private int[][][] relTuples;

   // constraints
   private int nbConstraints;
   private ArrayList<String> consName;
   private String[][] consScope;
   private int[][] consVarIndex;
   private int[] consArity;
   private int[] consRelIndex;

   // choco
   public Model model;
   public IntVar[] vars;
   private Tuples[] cons;

   private int[][] origdom;

   public parsexml(String nomFichier) {
      this.nomFichier = nomFichier;
      
   }

   public void parse() {
      NodeList nList;
      try {

         File fXmlFile = new File(nomFichier);
         DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
         DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
         Document doc = dBuilder.parse(fXmlFile);
         doc.getDocumentElement().normalize();

         ////// domains///////
         nbDomains = 0;
         nList = doc.getElementsByTagName("domains");
         for (int temp = 0; temp < nList.getLength(); temp++) {
            Node nNode = nList.item(temp);
            if (nNode.getNodeType() == Node.ELEMENT_NODE) {
               Element eElement = (Element) nNode;

               nbDomains = Integer.parseInt(eElement.getAttribute("nbDomains"));

            }
         }

         ////// domain//////
         dom = new int[nbDomains][];
         domName = new ArrayList<String>(nbDomains);
         nList = doc.getElementsByTagName("domain");
         for (int temp = 0; temp < nbDomains; temp++) {
            Node nNode = nList.item(temp);
            if (nNode.getNodeType() == Node.ELEMENT_NODE) {
               Element eElement = (Element) nNode;

               // dom[temp] = new Domain();
               int nbValues = Integer.parseInt(eElement.getAttribute("nbValues"));
               dom[temp] = new int[nbValues];
               domName.add(eElement.getAttribute("name"));

               String s = nNode.getTextContent();

               Pattern p;
               Matcher m;
               p = Pattern.compile("-?[0-9]+");
               m = p.matcher(s);
               int index = 0;
               while (m.find()) {
                  dom[temp][index] = Integer.parseInt(m.group());
                  index++;
               }

            }
         }

         ////// variables///////
         nbVariables = 0;
         nList = doc.getElementsByTagName("variables");
         for (int temp = 0; temp < nList.getLength(); temp++) {
            Node nNode = nList.item(temp);
            if (nNode.getNodeType() == Node.ELEMENT_NODE) {
               Element eElement = (Element) nNode;

               nbVariables = Integer.parseInt(eElement.getAttribute("nbVariables"));

            }
         }

         ////// variable//////
         varName = new ArrayList<String>(nbVariables);
         varDomain = new ArrayList<String>(nbVariables);
         nList = doc.getElementsByTagName("variable");
         for (int temp = 0; temp < nbVariables; temp++) {
            Node nNode = nList.item(temp);
            if (nNode.getNodeType() == Node.ELEMENT_NODE) {
               Element eElement = (Element) nNode;
               varName.add(eElement.getAttribute("name"));
               varDomain.add(eElement.getAttribute("domain"));

            }
         }

         ////// Relations///////
         nbRelations = 0;
         nList = doc.getElementsByTagName("relations");
         for (int temp = 0; temp < nList.getLength(); temp++) {
            Node nNode = nList.item(temp);
            if (nNode.getNodeType() == Node.ELEMENT_NODE) {
               Element eElement = (Element) nNode;
               nbRelations = Integer.parseInt(eElement.getAttribute("nbRelations"));
            }
         }

         ////// relation//////
         relName = new ArrayList<String>(nbRelations);
         relArity = new int[nbRelations];
         relNbTuples = new int[nbRelations];
         relTuples = new int[nbRelations][][];
         nList = doc.getElementsByTagName("relation");
         for (int temp = 0; temp < nbRelations; temp++) {
            Node nNode = nList.item(temp);
            if (nNode.getNodeType() == Node.ELEMENT_NODE) {
               Element eElement = (Element) nNode;

               relName.add(eElement.getAttribute("name"));

               int arity = Integer.parseInt(eElement.getAttribute("arity"));
               int nbTuples = Integer.parseInt(eElement.getAttribute("nbTuples"));
               relArity[temp] = arity;
               relNbTuples[temp] = nbTuples;

               relTuples[temp] = new int[nbTuples][arity];

               String table = nNode.getTextContent();
               Pattern p;
               Matcher m;
               p = Pattern.compile("(-?[0-9]\s?)+");
               m = p.matcher(table);
               for (int i = 0; i < nbTuples; i++) {
                  if (m.find()) {
                     String tuple = m.group();

                     Pattern p1;
                     Matcher m1;
                     p1 = Pattern.compile("-?[0-9]+");
                     m1 = p1.matcher(tuple);
                     for (int j = 0; j < arity; j++) {
                        if (m1.find()) {
                           relTuples[temp][i][j] = Integer.parseInt(m1.group());
                        }
                     }
                  }
               }
            }
         }

         ////// Constraints///////
         nbConstraints = 0;
         nList = doc.getElementsByTagName("constraints");
         for (int temp = 0; temp < nList.getLength(); temp++) {
            Node nNode = nList.item(temp);
            if (nNode.getNodeType() == Node.ELEMENT_NODE) {
               Element eElement = (Element) nNode;
               nbConstraints = Integer.parseInt(eElement.getAttribute("nbConstraints"));
            }
         }

         ////// Constraint//////
         consName = new ArrayList<String>(nbConstraints);
         consScope = new String[nbConstraints][];
         consVarIndex = new int[nbConstraints][];
         consArity = new int[nbConstraints];
         consRelIndex = new int[nbConstraints];
         nList = doc.getElementsByTagName("constraint");
         for (int temp = 0; temp < nbConstraints; temp++) {
            Node nNode = nList.item(temp);
            if (nNode.getNodeType() == Node.ELEMENT_NODE) {
               Element eElement = (Element) nNode;

               consName.add(eElement.getAttribute("name"));
               int arity = Integer.parseInt(eElement.getAttribute("arity"));
               consArity[temp] = arity;

               // obtenir la liste des variables impliques
               consScope[temp] = new String[arity];
               consVarIndex[temp] = new int[arity];
               String scope = eElement.getAttribute("scope");
               Pattern p;
               Matcher m;
               p = Pattern.compile("[^\s]+");
               m = p.matcher(scope);
               for (int i = 0; i < arity; i++) {
                  if (m.find()) {
                     String vn = m.group();
                     consScope[temp][i] = vn;
                     consVarIndex[temp][i] = varName.indexOf(vn);
                  }

               }

               // obtenir relation
               String consRelName = eElement.getAttribute("reference");
               consRelIndex[temp] = relName.indexOf(consRelName);

            }
         }

      } catch (Exception e) {
         e.printStackTrace();
      }

   }
/*
   public int pickRandomValueOfOriginDomain(IntVar var) {

      String nameVar = var.getName();
      int indexVar = varName.indexOf(nameVar);

      int randomIndex = new Random().nextInt(origdom[indexVar].length);
      return origdom[indexVar][randomIndex];

   }

   private void generateorigindomain() {
      
      origdom = new int[nbVariables][];
      for (int k=0;k<nbVariables;k++){
         IntVar var = vars[k];
         int[] arrVal = new int[var.getDomainSize()];
         DisposableValueIterator vit = var.getValueIterator(true);
         for (int i=0;i<var.getDomainSize();i++){
            if (!vit.hasNext()){
               return;
            }
            arrVal[i] = vit.next();
         }
         vit.dispose();
         origdom[k]=arrVal;
      }
   }
*/
   public void generateModel() throws ContradictionException {
      model = new Model(nomFichier);
      vars = new IntVar[nbVariables];

      for (int i = 0; i < nbVariables; i++) {
         vars[i] = model.intVar(varName.get(i), dom[domName.indexOf(varDomain.get(i))]);
      }

      cons = new Tuples[nbConstraints];
      for (int i = 0; i < nbConstraints; i++) {
         cons[i] = new Tuples(true);
         cons[i].add(relTuples[consRelIndex[i]]);

         IntVar[] consVars = new IntVar[consArity[i]];
         for (int j = 0; j < consArity[i]; j++) {
            consVars[j] = vars[consVarIndex[i][j]];
         }
         model.table(consVars, cons[i],"CT+").post();
      }
      //model.getSolver().propagate();
      //generateorigindomain();
   }

}
