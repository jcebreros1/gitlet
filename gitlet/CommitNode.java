package gitlet;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;

import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.io.Serializable;
import java.security.MessageDigest;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.nio.file.Paths;
import java.io.IOException;
import java.util.Hashtable;//<K,V>;
import java.util.ArrayList;
import java.util.List;

import java.nio.file.Files;
import java.nio.file.*;


//contains the branch pointers
public class CommitNode implements Serializable  {
    public String time;
    public String log;
    public String commitHashID;
    public String parent;
    public String mergeParent;

    public CommitNode nextCommit;
    public CommitNode prevCommit;
    public CommitNode nextBranch;
	//hashtable where key is the name of the file and value is the hashID of the blod that has it
    public Hashtable<String, String> blobsInCurrentCommit;

		public CommitNode(){
            CommitNode nextPointer = null;
            CommitNode prevPointer = null;
            CommitNode nextBranch = null;
            blobsInCurrentCommit = new Hashtable<String,String>();
            time = "1970/01/01 00:00:00";
            log = "initial commit";
            String commitID = time+log+"commit";
            commitHashID = Utils.sha1(commitID);

        }
    public CommitNode(CommitNode prev, String[] logMessage){
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        time = dateFormat.format(date);
        log = String.join(" ", logMessage);
        String commitID = time+log+"commit";
        commitHashID = Utils.sha1(commitID);
        blobsInCurrentCommit = new Hashtable<String,String>();
        blobsInCurrentCommit.putAll(prev.blobsInCurrentCommit);

        prev.nextCommit = this;
        this.nextCommit = null;
        this.prevCommit = prev;
    }


	    public CommitNode getPreviousCommit(){ return prevCommit; }
	    public CommitNode getNextCommit(){
	    	return nextCommit;
	    }

	    public boolean containsIdenticalFileInCurrentCommit(String fileName, String fileHashID){
            String fileHashIDInCommit = blobsInCurrentCommit.get(fileName);
            //System.out.println(fileHashIDInCommit);
            if (fileHashIDInCommit != null && fileHashIDInCommit.equals(fileHashID)){
                return true;
            }
            else{
                return false;
            }
        }
        public boolean containsOutdatedVersionInCurrentCommit(String fileName, String fileHashID){
		    if (blobsInCurrentCommit.containsKey(fileName)){
                String fileHashIDInCommit = blobsInCurrentCommit.get(fileName);
                if (fileHashIDInCommit.equals(fileHashID)){
                    return true;
                    }
                }
		    return false;
        }

        public Blob getBlobOfFile(String path, String fileName) throws Exception{
            Blob deserializedBlob = new Blob();
            try {
                FileInputStream fileIn = new FileInputStream(path + fileName);
                ObjectInputStream in = new ObjectInputStream(fileIn);

                deserializedBlob = (Blob) in.readObject();

                in.close();
                fileIn.close();
            } catch (IOException i) {
                i.printStackTrace();
            }
            return deserializedBlob;
		}

    String time(){
        return time;
    }
    String log(){
        return log;
    }
    String commitHashID(){
        return commitHashID;
    }
}