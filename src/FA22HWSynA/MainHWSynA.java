package FA22HWSynA;

import ADT.*;

/**
 *
 * @author abrouill FALL 2022
 */
public class MainHWSynA {

    public static void main(String[] args) {
       String filePath = args[0];
        boolean traceon = true;
        System.out.println("Mark Fish, 1682, CS4100/5100, FALL 2022");
        System.out.println("INPUT FILE TO PROCESS IS: " + filePath);
    
        Syntactic parser = new Syntactic(filePath, traceon);
        parser.parse();
        System.out.println("Done.");
    }

}
