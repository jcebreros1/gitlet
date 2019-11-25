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
import java.util.Hashtable;
import java.util.ArrayList;
import java.util.Stack;
import java.util.List;

import java.nio.file.Files;
import java.nio.file.*;


public class CommitDataStructure implements Serializable {
	public CommitNode root;
	public CommitNode currentCommitNode;

	//creates the first empty commit
	CommitDataStructure() {
		try {
			String directoryPath = Paths.get(".").toAbsolutePath().normalize().toString() + "/.gitlet";
			File dir = new File(directoryPath);
			if (dir.exists()) {
				System.out.println("A gitlet version-control system already exists in the current directory");
				System.exit(0);
			}
			if (dir.mkdirs()) {
				//DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
				//Date date = new Date();

				//time = dateFormat.format(date);
				currentCommitNode = new CommitNode();
				root = currentCommitNode;

				File destinationFile = new File(Paths.get(".").toAbsolutePath().normalize().toString() + "/.gitlet/staging/");
				if (!destinationFile.exists()) {
					destinationFile.mkdirs();
				}
				return;
			} else {
				throw new IOException("Failed to create directory '" + dir.getAbsolutePath() + "' for an unknown reason.");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public String commitHashID() {
		return currentCommitNode.commitHashID();
	}

	public void addFilesToStagingArea(String filePath) {
		File fileToAdd = new File(filePath);
		if (!fileToAdd.exists()) {
			System.out.println("File to add does not exist");
			System.exit(0);
		}
		Blob blob = new Blob(fileToAdd);
		if (!currentCommitNode.containsIdenticalFileInCurrentCommit(blob.name(), blob.blobHashID())) {
			String stringOfFileInStaging = Paths.get(".").toAbsolutePath().normalize().toString() + "/.gitlet/staging/" + blob.blobHashID();
			//System.out.println(stringOfFileInStaging);
			File fileInStaging = new File(stringOfFileInStaging);
			if (fileInStaging.exists()) {
				//System.out.println("File to add is already in staging area");
				System.exit(0);
			} else {
				try {
					FileOutputStream blobFile = new FileOutputStream(".gitlet/staging/" + blob.blobHashID());
					ObjectOutputStream blobObject = new ObjectOutputStream(blobFile);
					blobObject.writeObject(blob);
					blobObject.close();
					blobFile.close();
				} catch (IOException i) {
					i.printStackTrace();
				}
			}
		} else if (currentCommitNode.containsIdenticalFileInCurrentCommit(blob.name(), blob.blobHashID())) {
			String stringOfFileInStaging = Paths.get(".").toAbsolutePath().normalize().toString() + "/.gitlet/staging/" + blob.blobHashID();
			File fileInStaging = new File(stringOfFileInStaging);
			if (fileInStaging.exists()) {
				//System.out.println("File to add is already in staging area");
				fileInStaging.delete();
				System.exit(0);
			}
		}
		return;
	}


	public void addNewCommit(String[] logMessage, ArrayList<String> listOfFilesToUntract) throws Exception {
		String pathOfStagingDirectory = Paths.get(".").toAbsolutePath().normalize().toString() + "/.gitlet/staging/";
		String pathOfGitletDirectory = Paths.get(".").toAbsolutePath().normalize().toString() + "/.gitlet/";
		File[] files = new File(pathOfStagingDirectory).listFiles();
		Blob deserializedBlob = new Blob();
		for (File file : files) {
			try {
				FileInputStream fileIn = new FileInputStream(pathOfStagingDirectory + file.getName());
				ObjectInputStream in = new ObjectInputStream(fileIn);

				deserializedBlob = (Blob) in.readObject();

				in.close();
				fileIn.close();
			} catch (IOException i) {
				i.printStackTrace();
			}
			if (currentCommitNode.containsIdenticalFileInCurrentCommit(deserializedBlob.name(), deserializedBlob.blobHashID())
					|| listOfFilesToUntract.contains(deserializedBlob.name())) {
				file.delete();
				System.exit(0);
			} else if (!currentCommitNode.containsIdenticalFileInCurrentCommit(deserializedBlob.name(), deserializedBlob.blobHashID())) {
				boolean containsornot = currentCommitNode.containsIdenticalFileInCurrentCommit(deserializedBlob.name(), deserializedBlob.blobHashID());
				CommitNode newCommit = new CommitNode(currentCommitNode, logMessage);
				for (String item : listOfFilesToUntract) {
					newCommit.blobsInCurrentCommit.remove(item);
				}
				newCommit.blobsInCurrentCommit.put(deserializedBlob.name(), deserializedBlob.blobHashID());
				currentCommitNode = newCommit;
				Path temp = Files.move(Paths.get(pathOfStagingDirectory + file.getName()),
						Paths.get(pathOfGitletDirectory + file.getName()));
			}
		}
	}

	public void unstageFile(String fileName) {
		String fileToStagePath = Paths.get(".").toAbsolutePath().normalize().toString() + "/" + fileName;
		File fileToRemoveFromStaging = new File(fileToStagePath);
		Blob blobToCheckIfFileStaged = new Blob(fileToRemoveFromStaging);
		String pathOfStagingDirectory = Paths.get(".").toAbsolutePath().normalize().toString() + "/.gitlet/staging/";
		File tempFile = new File(pathOfStagingDirectory + blobToCheckIfFileStaged.blobHashID());
		if (tempFile.exists()) {
			tempFile.delete();
		}
	}

	public void log(CommitNode node) {
		while (node != null) {
			System.out.println("===");
			System.out.println(node.commitHashID());
			System.out.println(node.time());
			System.out.println(node.log());
			System.out.println(" ");
			node = node.prevCommit;
			//System.out.prinitln(temp);
		}
	}

	public void global_log(CommitNode root) {

		if (root == null) {
			return;
		}
		Stack<CommitNode> stack = new Stack<>();
		stack.push(root);

		while (!stack.isEmpty()) {
			CommitNode node = stack.pop();
			System.out.println("===");
			System.out.println(node.commitHashID());
			System.out.println(node.time());
			System.out.println(node.log());
			System.out.println(" ");

			if (node.nextCommit != null) {
				stack.add(node.nextCommit);
			}
			if (node.nextBranch != null) {
				stack.add(node.nextBranch);
			}
		}

	}

	public void find(String[] logMessage) {
		String message = String.join(" ", logMessage);

		if (root == null) {
			return;
		}
		Stack<CommitNode> stack = new Stack<>();
		stack.push(root);

		while (!stack.isEmpty()) {
			CommitNode node = stack.pop();
			if (node.log().compareTo(message) == 0){
				System.out.println(node.commitHashID());
				System.out.println(" ");
			}

			if (node.nextCommit != null) {
				stack.add(node.nextCommit);
			}
			if (node.nextBranch != null) {
				stack.add(node.nextBranch);
			}
		}
	}

	public void status() {
		if (root == null) {
			return;
		}
		Stack<CommitNode> stack = new Stack<>();
		Stack<CommitNode> branches = new Stack<>();
		stack.push(root);

		while (!stack.isEmpty()) {
			CommitNode node = stack.pop();

			if (node.nextCommit != null) {
				//stack.add(node.nextCommit);
			}
			if (node.nextBranch != null) {
				branches.add(node.nextBranch);
			}
		}
		branches.add(currentCommitNode);
		while (!branches.isEmpty()) {
			CommitNode brancheNodes = branches.pop();
			System.out.println("=== Branches ===");
			System.out.println(brancheNodes.commitHashID());
			System.out.println(" ");
		}

	}

}

