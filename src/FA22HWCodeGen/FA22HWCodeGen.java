package FA22HWCodeGen;

import ADT.*;
/**
 *
 * @author abrouill SPRING 2021, Mark Fish
 */
public class FA22HWCodeGen {
    public static void main(String[] args) {
        String filePath = args[0];
        System.out.println("Parsing "+filePath);
        boolean traceon = true; //false;
        Syntactic parser = new Syntactic(filePath, traceon);
        parser.parse();
        
        System.out.println("Done.");
    }
}