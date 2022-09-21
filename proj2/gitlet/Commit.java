package gitlet;

// TODO: any imports you need here

import java.io.Serializable;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

/** Represents a gitlet commit object.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 *  @author Wolleaf Lee
 */
public class Commit implements Serializable {
    /**
     * TODO: add instance variables here.
     *
     * List all instance variables of the Commit class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided one example for `message`.
     */

    /** The message of this Commit. */
    private String message;

    /* TODO: fill in the rest of this class. */
    private String commitID;
    private String lastCommit;
    private String mergeCommit;
    private Date timestamp;
    private LinkedHashMap<String, String> blobs;

    public Commit() {
        this("initial commit", null);
        timestamp = new Date(0);
    }

    public Commit(String message, String lastCommit) {
        this.message = message;
        this.lastCommit = lastCommit;
        commitID = null;
        mergeCommit = null;
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
        return lastCommit;
    }

    public String getMergeCommit() {
        return mergeCommit;
    }

    public void setMergeCommit(String mergeCommit) {
        this.mergeCommit = mergeCommit;
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
