package gitlet;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileInputStream;

import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.io.Serializable;
import java.security.MessageDigest;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.nio.file.Paths;
import java.io.IOException;

import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;

public class Blob implements Serializable  {
    private String name;
    private String blobHashID;
    private byte[] contents;

    String name(){
        return name;
    }
    String blobHashID(){ return blobHashID; }
    byte[] contents() {return contents; }

    Blob(){
    }
    //Creates a new blob to be stored in commit
    Blob(File File){
            name = File.getName();
            contents = Utils.readContents(File);
            blobHashID = Utils.sha1(contents);
    }
    public void removeFromStagingArea(){

    }
}







