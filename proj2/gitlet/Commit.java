package gitlet;


import java.io.Serializable;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

/** Represents a gitlet commit object.
 *  does at a high level.
 *
 *  @author Wolleaf Lee
 */
public class Commit implements Serializable {
    /**
     * List all instance variables of the Commit class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided one example for `message`.
     */

    /** The message of this Commit. */
    private String message;

    private String commitID;
    private String lastCommitID;
    private String mergeCommitID;
    private Date timestamp;
    private LinkedHashMap<String, String> blobs;

    public Commit() {
        this("initial commit", null);
        timestamp = new Date(0);
    }

    public Commit(String message, String lastCommitID) {
        this.message = message;
        this.lastCommitID = lastCommitID;
        commitID = null;
        mergeCommitID = null;
        timestamp = new Date();
        blobs = new LinkedHashMap<>();
    }

    public String getCommitID() {
        return commitID;
    }

    public void setCommitID(String commitID) {
        this.commitID = commitID;
    }

    public String getLastCommit() {
        return lastCommitID;
    }

    public String getMergeCommit() {
        return mergeCommitID;
    }

    public void setMergeCommitID(String mergeCommitID) {
        this.mergeCommitID = mergeCommitID;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public String getMessage() {
        return message;
    }

    public LinkedHashMap<String, String> getBlobs() {
        return blobs;
    }

    public void addBlob(String name, String sha1Code) {
        blobs.put(name, sha1Code);
    }

    public void removeBlob(String name) {
        blobs.remove(name);
    }

    @SuppressWarnings("unchecked")
    public void cloneBlobs(Commit lastCommit) {
        if (lastCommit.getBlobs() == null) {
            this.blobs = new LinkedHashMap<>();
        } else {
            this.blobs = (LinkedHashMap<String, String>) lastCommit.getBlobs().clone();
        }
    }

    @SuppressWarnings("unchecked")
    public Map<String, String> returnClonedBlobs() {
        return (Map<String, String>) blobs.clone();
    }
}
