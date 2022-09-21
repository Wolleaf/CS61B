package gitlet;

import jdk.jshell.execution.Util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.*;

import static gitlet.Utils.join;
import static gitlet.Utils.restrictedDelete;


/** Represents a gitlet repository.
 *
 *  @author Wolleaf Lee
 */
public class Repository {
    /**
     * List all instance variables of the Repository class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided two examples for you.
     */

    /** The current working directory. */
    public static final File CWD = new File(System.getProperty("user.dir"));
    /** The .gitlet directory. */
    public static final File GITLET_DIR = join(CWD, ".gitlet");

    /** The stage's directory. */
    private static final File STAGE_DIR = join(GITLET_DIR, "stage");
    /** The objects' directory. */
    private static final File OBJECT_DIR = join(GITLET_DIR, "objects");
    /** The commits' directory. */
    private static final File COMMIT_DIR = join(GITLET_DIR, "commits");
    /** The removal's directory. */
    private static final File REMOVESTAGE_DIR = join(GITLET_DIR, "removestage");
    /** The reference's directory. */
    private static final File REF_DIR = join(GITLET_DIR, "refs");
    /** The branch's directory. */
    private static final File BRANCH_DIR = join(REF_DIR, "heads");
    /** The remote's directory. */
    private static final File REMOTE_DIR = join(REF_DIR, "remotes");

    /* TODO: fill in the rest of this class. */

    /**
     * @return the gitlet repository's existence
     */
    public static boolean isGitLetDirExist() {
        return GITLET_DIR.exists();
    }

    /**
     * init gitlet directory
     */
    public static void init() {
        GITLET_DIR.mkdir();
        STAGE_DIR.mkdir();
        OBJECT_DIR.mkdir();
        COMMIT_DIR.mkdir();
        REMOVESTAGE_DIR.mkdir();
        REF_DIR.mkdir();
        BRANCH_DIR.mkdir();
        REMOTE_DIR.mkdir();
        // the firstCommit
        Commit lastCommit = new Commit();
        // store the head pointer
        String sha1Code = Utils.sha1(Utils.serialize(lastCommit));
        lastCommit.setCommitID(sha1Code);
        // create the head file
        Utils.writeContents(join(GITLET_DIR, "HEAD"), "master");
        // create the master file
        Utils.writeContents(join(BRANCH_DIR, "master"), sha1Code);
        // create the initial commit file
        File commitFile = join(COMMIT_DIR, sha1Code);
        Utils.writeObject(commitFile, lastCommit);
    }

    /**
     * add the file to the stage
     * @param fileName name of the file
     */
    public static void add(String fileName) {
        File fileInCWD = join(CWD, fileName);
        if (!fileInCWD.exists()) {
            System.out.println("File does not exist.");
            System.exit(0);
        }
        join(REMOVESTAGE_DIR, fileName).delete();
        Commit nowCommit = readCommit();
        String sha1Code = Utils.sha1(Utils.readContents(fileInCWD));
        File file = join(STAGE_DIR, fileName);
        if (sha1Code.equals(nowCommit.getBlobs().get(fileName))) {
            if (file.exists()) {
                join(file, file.list()[0]).delete();
                file.delete();
            }
            System.exit(0);
        }
        // create directory in the stage directory
        file.mkdir();
        if (file.list().length != 0) {
            join(file, file.list()[0]).delete();
        }
        File stageVersion = join(file, sha1Code);
        // create the added file
        Utils.writeContents(stageVersion, Utils.readContents(fileInCWD));
    }

    /**
     * commit the staged files
     * @param message the commit message
     */
    public static void commit(String message, String mergeCommitID) {
        // if the stage directory is empty
        if (STAGE_DIR.list().length == 0 && REMOVESTAGE_DIR.list().length == 0) {
            System.out.println("No changes added to the commit.");
            System.exit(0);
        }
        // create the new commit and read the old commit
        Commit newCommit = new Commit(message, Utils.readContentsAsString(
                join(BRANCH_DIR, Utils.readContentsAsString(join(GITLET_DIR, "HEAD")))));
        Commit lastCommit = readCommit();
        newCommit.cloneBlobs(lastCommit);
        newCommit.setMergeCommit(mergeCommitID);
        // delete the file in the removestage
        String[] filesInRemoveStage = REMOVESTAGE_DIR.list();
        for (String fileName : filesInRemoveStage) {
            newCommit.removeBlob(fileName);
            join(REMOVESTAGE_DIR, fileName).delete();
        }
        // traverse the stage directory
        File[] files = STAGE_DIR.listFiles();
        for (File file : files) {
            // add the commit blob to the commit
            newCommit.addBlob(file.getName(), file.list()[0]);
            File objectFile = join(OBJECT_DIR, file.getName());
            if (!objectFile.exists()) {
                objectFile.mkdir();
            }
            File subFile = join(file, file.list()[0]);
            Utils.writeContents(join(objectFile, file.list()[0]), Utils.readContents(subFile));
            subFile.delete();
            // delete it in the end
            file.delete();
        }
        // now the traversal is over and store the commit
        String sha1Code = Utils.sha1(Utils.serialize(newCommit));
        newCommit.setCommitID(sha1Code);
        String[] branches = BRANCH_DIR.list();
        for (String branch : branches) {
            if (branch.equals(Utils.readContentsAsString(join(GITLET_DIR, "HEAD")))) {
                Utils.writeContents(join(BRANCH_DIR, branch), sha1Code);
                break;
            }
        }
        File commitFile = join(COMMIT_DIR, sha1Code);
        Utils.writeObject(commitFile, newCommit);
    }

    public static void rm(String fileName) {
        File fileInStage = join(STAGE_DIR, fileName);
        if (fileInStage.exists()) {
            join(fileInStage, fileInStage.list()[0]).delete();
            fileInStage.delete();
            System.exit(0);
        }
        File fileInCWD = join(CWD, fileName);
        Commit nowCommit = readCommit();
        if (nowCommit.getBlobs().containsKey(fileName) && fileInCWD.exists()) {
            Utils.writeContents(join(REMOVESTAGE_DIR, fileName), fileName);
            fileInCWD.delete();
        } else if (nowCommit.getBlobs().containsKey(fileName) && !fileInCWD.exists()) {
            Utils.writeContents(join(REMOVESTAGE_DIR, fileName), fileName);
        } else {
            System.out.println("No reason to remove the file.");
        }
    }

    /**
     * the log of commit
     * TODO the merge information need to be added
     */
    public static void log() {
        Commit commit = readCommit();
        while (commit != null) {
            printCommit(commit);
            if (commit.getLastCommit() == null) {
                break;
            }
            commit = Utils.readObject(join(COMMIT_DIR, commit.getLastCommit()), Commit.class);
        }
    }

    /**
     * the information of all commits
     * TODO the merge information need to be added
     */
    public static void globalLog() {
        File[] allCommits = COMMIT_DIR.listFiles();
        for (File commitFile: allCommits) {
            Commit commit = Utils.readObject(commitFile, Commit.class);
            printCommit(commit);
        }
    }

    /**
     * find commit whose message is the same as the message
     * @param message the message of the needed commit
     */
    public static void find(String message) {
        File[] allCommits = COMMIT_DIR.listFiles();
        boolean isFind = false;
        for (File commitFile: allCommits) {
            Commit commit = Utils.readObject(commitFile, Commit.class);
            if (commit.getMessage().equals(message)) {
                System.out.println(commit.getCommitID());
                isFind = true;
            }
        }
        if (!isFind) {
            System.out.println("Found no commit with that message.");
        }
    }

    /**
     * show the worktree's status
     */
    public static void status() {
        Commit nowCommit = readCommit();
        // branch status
        System.out.println("=== Branches ===");
        String[] branches = BRANCH_DIR.list();
        Arrays.sort(branches);
        for (String branch : branches) {
            if (branch.equals(Utils.readContentsAsString(join(GITLET_DIR, "HEAD")))) {
                System.out.print("*");
            }
            System.out.println(branch);
        }
        System.out.println();
        // staged file status
        System.out.println("=== Staged Files ===");
        String[] stagedFileName = STAGE_DIR.list();
        Arrays.sort(stagedFileName);
        for (String fileName : stagedFileName) {
            System.out.println(fileName);
        }
        System.out.println();
        // removed file status
        System.out.println("=== Removed Files ===");
        String[] removedFileName = REMOVESTAGE_DIR.list();
        Arrays.sort(removedFileName);
        for (String fileName : removedFileName) {
            System.out.println(fileName);
        }
        System.out.println();
        // modified file status
        System.out.println("=== Modifications Not Staged For Commit ===");
        List<String> modifiedInformation = new ArrayList<>(); // create a list to store the information
        for (Map.Entry<String, String> committed : nowCommit.getBlobs().entrySet()) { // check the blobs of the commit
            File fileInCWD = join(CWD, committed.getKey());
            File fileInStage = join(STAGE_DIR, committed.getKey());
            File fileInRemoveStage = join(REMOVESTAGE_DIR, committed.getKey());
            File fileInObject = join(join(OBJECT_DIR, committed.getKey()), committed.getValue());
            if (fileInCWD.exists()) {
                if (!Arrays.equals(Utils.readContents(fileInCWD), Utils.readContents(fileInObject))
                        && !fileInStage.exists()) {
                    modifiedInformation.add(committed.getKey() + " (modified)");
                }
            } else {
                if (!fileInRemoveStage.exists() && !fileInStage.exists()) {
                    modifiedInformation.add(committed.getKey() + " (deleted)");
                }
            }
        }
        String[] stagedFileList = STAGE_DIR.list(); // check the stage directory
        for (String staged : stagedFileList) {
            File stagedFileDir = join(STAGE_DIR, staged);
            File stagedFile = join(stagedFileDir, stagedFileDir.list()[0]);
            File cwdFile = join(CWD, staged);
            if (!cwdFile.exists() && !join(REMOVESTAGE_DIR, staged).exists()) {
                modifiedInformation.add(staged + " (deleted)");
            } else {
                if (!Arrays.equals(Utils.readContents(stagedFile), Utils.readContents(cwdFile))) {
                    modifiedInformation.add(staged + " (modified)");
                }
            }
        }
        modifiedInformation.sort(((o1, o2) -> o1.compareTo(o2)));
        for (String modifiedString : modifiedInformation) {
            System.out.println(modifiedString);
        }
        System.out.println();
        // untracked file status
        System.out.println("=== Untracked Files ===");
        List<String> allUntrackedFileInCWD = untrackedFiles();
        allUntrackedFileInCWD .sort((o1, o2) -> o1.compareTo(o2));
        for (String cwdFile : allUntrackedFileInCWD) {
            System.out.println(cwdFile);
        }
        System.out.println();
    }

    /**
     *
     * @param args
     */
    public static void checkOut(String[] args) {
        // case 1
        if (args.length == 3 && args[1].equals("--")) {
            checkOut(args[2], null);
        } else if (args.length == 4 && args[2].equals("--")) { // case 2
            checkOut(args[3], findFullCommitID(args[1])); // get the full ID
        } else if (args.length == 2) { // case 3
            if (args[1].equals(Utils.readContentsAsString(join(GITLET_DIR, "HEAD")))) {
                System.out.println("No need to checkout the current branch.");
                System.exit(0);
            }
            File branchFile = join(BRANCH_DIR, args[1]);
            if (!branchFile.exists()) {
                System.out.println("No such branch exists.");
                System.exit(0);
            }
            checkTo(args[1]); // check out branch to that branch
        } else {
            System.out.println("Incorrect operands.");
        }
    }

    /**
     * create a new branch
     * @param branchName the name of the branch
     */
    public static void branch(String branchName) {
        if (join(BRANCH_DIR, branchName).exists()) {
            System.out.println("A branch with that name already exists.");
        } else {
            Utils.writeContents(join(BRANCH_DIR, branchName), Utils.readContentsAsString(
                    join(BRANCH_DIR, Utils.readContentsAsString(join(GITLET_DIR, "HEAD")))));
        }
    }

    /**
     * remove the branch
     * @param branchName name of the branch
     */
    public static void rmBranch(String branchName) {
        File branchFile = join(BRANCH_DIR, branchName);
        if (!branchFile.exists()) {
            System.out.println("A branch with that name does not exist.");
            System.exit(0);
        }
        if (branchName.equals(Utils.readContentsAsString(join(GITLET_DIR, "HEAD")))) {
            System.out.println("Cannot remove the current branch.");
            System.exit(0);
        }
        branchFile.delete();
    }

    /**
     * reset the head pointer and the CWD to the commitID
     * @param commitID ID of the commit
     */
    public static void reset(String commitID) {
        File commitFile = join(COMMIT_DIR, findFullCommitID(commitID));
        if (!commitFile.exists()) {
            System.out.println("No commit with that id exists.");
            System.exit(0);
        }
        checkTo(commitID); // check out the head pointer to the commitID
        for (File file : STAGE_DIR.listFiles()) {
            join(file, file.list()[0]).delete();
            file.delete();
        }
    }

    public static void merge(String branchName) {
        if (STAGE_DIR.list().length != 0 || REMOVESTAGE_DIR.list().length != 0) {
            System.out.println("You have uncommitted changes.");
            System.exit(0);
        } else if (!join(BRANCH_DIR, branchName).exists()) {
            System.out.println("A branch with that name does not exist.");
            System.exit(0);
        } else if (branchName.equals(Utils.readContentsAsString(join(GITLET_DIR, "HEAD")))) {
            System.out.println("Cannot merge a branch with itself.");
            System.exit(0);
        } else if (untrackedFiles().size() != 0) {
            System.out.println("There is an untracked file in the way; delete it, or add and commit it first.");
            System.exit(0);
        }
        Commit currentCommit = readCommit();
        Commit branchCommit = readCommit(branchName);
        String ancestorID = getCommonAncestor(currentCommit, branchCommit);
        if (ancestorID.equals(currentCommit.getCommitID())) {
            System.out.println("Current branch fast-forwarded.");
            checkTo(branchName);
            System.exit(0);
        } else if (ancestorID.equals(branchCommit.getCommitID())) {
            System.out.println("Given branch is an ancestor of the current branch.");
            System.exit(0);
        }
        Map<String, String> currentBlob = currentCommit.returnClonedBlobs();
        Map<String, String> branchBlob = branchCommit.returnClonedBlobs();
        Commit ancestorCommit = Utils.readObject(join(COMMIT_DIR, ancestorID), Commit.class);
        for (Map.Entry<String, String> blob : ancestorCommit.getBlobs().entrySet()) {
            if (currentBlob.get(blob.getKey()) == null) {
                currentBlob.remove(blob.getKey());
                branchBlob.remove(blob.getKey());
                continue;
            } else if (currentBlob.get(blob.getKey()).equals(blob.getValue())
                    && branchBlob.get(blob.getKey()) == null) { // indicate currentBlob can not be null
                rm(blob.getKey());
            } else if (currentBlob.get(blob.getKey()).equals(blob.getValue())
                    && !branchBlob.get(blob.getKey()).equals(blob.getValue())) {
                String[] command = {"checkout", branchCommit.getCommitID(), "--", blob.getKey()};
                checkOut(command);
                add(blob.getKey());
            } else if (!currentBlob.get(blob.getKey()).equals(blob.getValue())
                    && branchBlob.get(blob.getKey()) != null
                    && branchBlob.get(blob.getKey()).equals(blob.getValue())) {
                continue; // seperated from 1st to avoid NullPointerException
            } else {
                File currentFile = null;
                if (join(CWD, blob.getKey()).exists()) {
                    currentFile = join(CWD, blob.getKey());
                }
                File branchFile = null;
                if (branchBlob.get(blob.getKey()) != null) {
                    branchFile = join(OBJECT_DIR, blob.getKey(), branchBlob.get(blob.getKey()));
                }
                System.out.println("Encountered a merge conflict.");
                mergeConflict(currentFile, branchFile);
                add(blob.getKey());
            }
            currentBlob.remove(blob.getKey());
            branchBlob.remove(blob.getKey());
        }
        if (branchBlob.size() != 0) {
            for (Map.Entry<String, String> blob : branchBlob.entrySet()) {
                if (currentBlob.containsKey(blob.getKey())) {
                    File currentFile = join(CWD, blob.getKey());
                    File branchFile = join(OBJECT_DIR, blob.getKey(), blob.getValue());
                    System.out.println("Encountered a merge conflict.");
                    mergeConflict(currentFile, branchFile);
                    add(blob.getKey());
                    continue;
                }
                String[] command = {"checkout", branchCommit.getCommitID(), "--", blob.getKey()};
                checkOut(command);
                add(blob.getKey());
            }
        }
        commit(String.format("Merged %s into %s.", branchName,
                Utils.readContentsAsString(join(GITLET_DIR, "HEAD"))), branchCommit.getCommitID());
    }

    private static void mergeConflict(File currentFile, File branchFile) {
        StringBuilder newFileContent = new StringBuilder("<<<<<<< HEAD\n");
        if (currentFile == null) {
            newFileContent.append("\n=======\n");
        } else {
            newFileContent.append(Utils.readContentsAsString(currentFile) + "\n=======\n");
        }
        if (branchFile == null) {
            newFileContent.append(">>>>>>>");
        } else {
            newFileContent.append(Utils.readContentsAsString(branchFile) + "\n>>>>>>>");
        }
        Utils.writeContents(currentFile, newFileContent.toString());
    }

    /**
     * read commit from the HEAD file
     * @return the commit corresponded with the HEAD file
     */
    public static Commit readCommit() {
        Commit commit = null;
        if (join(COMMIT_DIR, Utils.readContentsAsString(join(GITLET_DIR, "HEAD"))).exists()) {
            commit = Utils.readObject(join(COMMIT_DIR, Utils.readContentsAsString(
                    join(GITLET_DIR, "HEAD"))), Commit.class);
        } else {
            commit = Utils.readObject(join(COMMIT_DIR, Utils.readContentsAsString(
                    join(BRANCH_DIR, Utils.readContentsAsString(join(GITLET_DIR, "HEAD"))))), Commit.class);
        }
        return commit;
    }

    /**
     * read commit from the branch file
     * @param branchName the branchName of the commit
     * @return commit
     */
    public static Commit readCommit(String branchName) {
        return Utils.readObject(join(COMMIT_DIR, Utils.readContentsAsString(
                join(BRANCH_DIR, branchName))), Commit.class);
    }

    /**
     * a helper method to check out that file
     * @param fileName
     * @param commitID
     */
    private static void checkOut(String fileName, String commitID) {
        Commit commit;
        if (commitID == null) {
            commit = readCommit();
        } else {
            if (!join(COMMIT_DIR, commitID).exists()) {
                System.out.println("No commit with that id exists.");
                System.exit(0);
            }
            commit = Utils.readObject(join(COMMIT_DIR, commitID), Commit.class);
        }
        if (!commit.getBlobs().containsKey(fileName)) {
            System.out.println("File does not exist in that commit.");
            System.exit(0);
        }
        File fileInObject = join(OBJECT_DIR, fileName, commit.getBlobs().get(fileName)); // the exact file
        File fileInCWD = join(CWD, fileName);
        Utils.writeContents(fileInCWD, Utils.readContents(fileInObject));
    }

    /**
     * find the common ancestor of two commits.
     * @param currentCommit
     * @param branchCommit
     * @return the commitID of the ancestor
     */
    private static String getCommonAncestor(Commit currentCommit, Commit branchCommit) {
        List<String> ancestorOfCurrent = getAncestors(currentCommit);
        List<String> ancestorOfBranch = getAncestors(branchCommit);
        int lengthI = ancestorOfCurrent.size();
        int lengthII = ancestorOfBranch.size();
        for (int i = 0; i < lengthI && i < lengthII; i++) {
            if (lengthI >= lengthII && ancestorOfCurrent.contains(ancestorOfBranch.get(i))) {
                return ancestorOfBranch.get(i);
            } else if (lengthII > lengthI && ancestorOfBranch.contains(ancestorOfCurrent.get(i))) {
                return ancestorOfCurrent.get(i);
            }
        }
        return null;
    }

    private static List<String> getAncestors(Commit commit) {
        List<String> ancestors = new ArrayList<>();
        LinkedList<Commit> sequence = new LinkedList<>();
        sequence.add(commit);
        while (!sequence.isEmpty()) {
            ancestors.add(sequence.get(0).getCommitID());
            if (sequence.get(0).getLastCommit() != null) {
                sequence.add(Utils.readObject(join(COMMIT_DIR, sequence.get(0).getLastCommit()), Commit.class));
            }
            if (sequence.get(0).getMergeCommit() != null) {
                sequence.add(Utils.readObject(join(COMMIT_DIR, sequence.get(0).getMergeCommit()), Commit.class));
            }
            sequence.remove();
        }
        return ancestors;
    }

    /**
     * a helper method to find all the untracked files
     * @return a LinkedList of untracked filenames
     */
    private static  LinkedList<String> untrackedFiles() {
        Commit nowCommit = null;
        String headContent = Utils.readContentsAsString(join(GITLET_DIR, "HEAD"));
        File headInCommitFile = join(COMMIT_DIR, headContent);
        if (headInCommitFile.exists()) {
            nowCommit = Utils.readObject(headInCommitFile, Commit.class);
        } else {
            nowCommit = Utils.readObject(join(COMMIT_DIR, Utils.readContentsAsString(join(BRANCH_DIR,
                    headContent))), Commit.class);
        }
        LinkedList<String> allFileInCWD = new LinkedList<>();
        String[] stagedFileName = STAGE_DIR.list();
        // add all files in the CWD to the list
        allFileInCWD.addAll(Arrays.asList(CWD.list()));
        allFileInCWD.remove("gitlet");
        allFileInCWD.remove(".gitlet");
        for (String stagedFile : stagedFileName) { // remove the files already in the stagedfile
            allFileInCWD.remove(stagedFile);
        }
        // remove the files already been tracked
        int length = allFileInCWD.size();
        for (int i = length - 1; i >= 0; i--) {
            if (nowCommit.getBlobs().containsKey(allFileInCWD.get(i))) {
                allFileInCWD.remove(i);
            }
        }
        return allFileInCWD;
    }

    /**
     * find the full commitID if it is an abbreviated commitID
     * @param commitID the given commitID
     * @return the full commitID
     */
    private static String findFullCommitID(String commitID) {
        String targetCommitName = commitID;
        for (String commitFileName : COMMIT_DIR.list()) {
            if (commitFileName.startsWith(commitID)) {
                targetCommitName = commitFileName;
                break;
            }
        }
        return targetCommitName;
    }

    /**
     * a helper method to change the CWD
     * @param commitIDOrName the reset commitID or the checkout branch
     */
    private static void checkTo(String commitIDOrName) {
        if (untrackedFiles().size() != 0) {
            System.out.println("There is an untracked file in the way; delete it, or add and commit it first.");
            System.exit(0);
        }
        for (File file : CWD.listFiles()) { // clear the CWD directory
            if (!file.isDirectory()) {
                file.delete();
            }
        }
        Utils.writeContents(join(GITLET_DIR, "HEAD"), commitIDOrName); // store the pointer
        Commit nowBranchCommit; // choose the commitID or branchName
        if (join(BRANCH_DIR, commitIDOrName).exists()) {
            nowBranchCommit = Utils.readObject(join(COMMIT_DIR,
                    Utils.readContentsAsString(join(BRANCH_DIR, commitIDOrName))), Commit.class);
        } else {
            nowBranchCommit = Utils.readObject(join(COMMIT_DIR, commitIDOrName), Commit.class);
        }
        for (Map.Entry<String, String> blob : nowBranchCommit.getBlobs().entrySet()) { // add the files
            File fileInObject = join(OBJECT_DIR, blob.getKey(), blob.getValue());
            Utils.writeContents(join(CWD, blob.getKey()), Utils.readContents(fileInObject));
        }
    }

    /**
     * a helper method to print the commit information
     * @param commit the commit to be printed
     */
    private static void printCommit(Commit commit) {
        System.out.println("===");
        System.out.format("commit %s\n", commit.getCommitID());
        System.out.println("Date: " + new SimpleDateFormat("EEE MMM d HH:mm:ss yyyy Z",
                new Locale("en")).format(commit.getTimestamp()));
        System.out.println(commit.getMessage());
        System.out.println();
    }
}
