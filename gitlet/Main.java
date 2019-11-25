package gitlet;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileInputStream;

import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;

import java.io.Serializable;
import java.nio.file.Paths;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.List;
import java.util.Arrays;
import java.util.ArrayList;



/** Driver class for Gitlet, the tiny stupid version-control system.
 *  @author
 */
public class Main implements Serializable {
    /** Usage: java gitlet.Main ARGS, where ARGS contains
     *  <COMMAND> <OPERAND> .... */
    static CommitDataStructure currentCommitPointer;
    static String latestCommitName;
    static ArrayList<String> listOfFilesToUntract;


    public static void main(String... args) throws Exception{
        //deserialize
        derializeCommit();
        derializeListOfFilesToUntract();

        if(args.length == 0)
        {
            System.out.println("Please enter a command.");
            System.exit(0);
        }

        if (args[0].compareTo("init") == 0){
            if (currentCommitPointer != null){
                System.out.println("There's already a gitlet version control system.");
                System.exit(0);
            }
            currentCommitPointer = new CommitDataStructure();
            latestCommitName = currentCommitPointer.commitHashID();
            listOfFilesToUntract = new ArrayList<String>();
            serializeCommitPointer();
            serializeLatestCommitNamePointer();
            serializeListOfFilesToUntract();
        }
        else if (currentCommitPointer == null){
            System.out.println("There's no gitlet version control system.");
            System.exit(0);
        }
        else if (args[0].compareTo("add") == 0) {
            String fileToStagePath = Paths.get(".").toAbsolutePath().normalize().toString() + "/" + args[1];
            currentCommitPointer.addFilesToStagingArea(fileToStagePath);
        }
        else if (args[0].compareTo("commit") == 0) {
            if (args.length > 1){
                String[] newArgs = Arrays.copyOfRange(args, 1, args.length);
                currentCommitPointer.addNewCommit(newArgs, listOfFilesToUntract);
                //System.out.println(currentCommitPointer.commitHashID());
                latestCommitName = currentCommitPointer.commitHashID();
                listOfFilesToUntract.clear();
                serializeCommitPointer();
                serializeLatestCommitNamePointer();
                serializeListOfFilesToUntract();
            }
            else {
                System.out.println("You did not enter a message");
            }
        }
        else if (args[0].compareTo("rm") == 0) {
            if (args.length > 1 && args.length < 3){
                listOfFilesToUntract.add(args[1]);
                currentCommitPointer.unstageFile(args[1]);
                serializeListOfFilesToUntract();
        }
            else {
                System.out.println("You requested more than 1 file to be removed.");
            }
        }
        else if (args[0].compareTo("log") == 0) {
            currentCommitPointer.log(currentCommitPointer.currentCommitNode);
        }
        else if (args[0].compareTo("global-log") == 0) {
            currentCommitPointer.global_log(currentCommitPointer.root);
        }
        else if (args[0].compareTo("find") == 0) {
            if (args.length > 1){
                String[] newArgs = Arrays.copyOfRange(args, 1, args.length);
                currentCommitPointer.find(newArgs);
            }
            else {
                System.out.println("You did not enter a message");
            }
        }
        else if (args[0].compareTo("status") == 0) {
            currentCommitPointer.status();
        }
    }

    public static void derializeCommit() throws Exception{
        String gitletPath = Paths.get(".").toAbsolutePath().normalize().toString() + "/.gitlet";
        File dir = new File(gitletPath);
        if (dir.exists()){
            try {
                FileInputStream fileIn = new FileInputStream(".gitlet/latestCommitName");
                ObjectInputStream in = new ObjectInputStream(fileIn);

                latestCommitName = (String)in.readObject();

                in.close();
                fileIn.close();
            } catch (IOException i) {
                i.printStackTrace();
            }
        }
        if (dir.exists()){
            try {
                FileInputStream fileIn = new FileInputStream(".gitlet/"+latestCommitName);
                ObjectInputStream in = new ObjectInputStream(fileIn);

                currentCommitPointer = (CommitDataStructure)in.readObject();

                in.close();
                fileIn.close();
            } catch (IOException i) {
                i.printStackTrace();
            }
        }
    }

    public static void serializeCommitPointer()throws Exception{
        //serialize commit-----------------------
        try {
            FileOutputStream fileOut = new FileOutputStream(".gitlet/" + currentCommitPointer.commitHashID());
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(currentCommitPointer);
            out.close();
            fileOut.close();
        } catch (IOException i) {
            i.printStackTrace();
        }
    }
    public static void serializeLatestCommitNamePointer()throws Exception{
        //serialize branch pointer-----------------------
        try {
            FileOutputStream fileOut = new FileOutputStream(".gitlet/" + "latestCommitName");
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(latestCommitName);
            out.close();
            fileOut.close();
        } catch (IOException i) {
            i.printStackTrace();
        }
    }

    public static void serializeListOfFilesToUntract()throws Exception{
        //serialize branch pointer-----------------------
        try {
            FileOutputStream fileOut = new FileOutputStream(".gitlet/" + "listOfFilesToUntract");
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(listOfFilesToUntract);
            out.close();
            fileOut.close();
        } catch (IOException i) {
            i.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    public static void derializeListOfFilesToUntract() throws Exception{
        String gitletPath = Paths.get(".").toAbsolutePath().normalize().toString() + "/.gitlet/"+"listOfFilesToUntract";
        File dir = new File(gitletPath);
        if (dir.exists()){
            try {
                FileInputStream fileIn = new FileInputStream(".gitlet/"+"listOfFilesToUntract");
                ObjectInputStream in = new ObjectInputStream(fileIn);

                listOfFilesToUntract = (ArrayList<String>) in.readObject();

                in.close();
                fileIn.close();
            } catch (IOException i) {
                i.printStackTrace();
            }
        }
    }


}
