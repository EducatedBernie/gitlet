package gitlet;

import java.io.Serializable;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * @author Bernie
 */
public class StagingArea implements Serializable {
    /**
     * Sequencing.
     */
    private TreeMap<String, String> toAdd = new TreeMap<>();
    /**
     * Sequencing.
     */
    private TreeSet<String> toRemove = new TreeSet<>();

    /**
     * Bernie.
     * @param fileName asd
     * @param sha1 asd
     */
    public void flagForAddition(String fileName, String sha1) {
        if (toRemove.contains((fileName))) {
            toRemove.remove(fileName);
        }
        toAdd.put(fileName, sha1);
    }

    /**
     * Bernie.
     * @param fileName asd.
     */
    public void unstageForAddition(String fileName) {
        if (toAdd.containsKey((fileName))) {
            toAdd.remove(fileName);
        }
    }

    /**
     * Bernie.
     * @param fileName self
     */
    public void unstageForRemoval(String fileName) {
        toRemove.remove(fileName);
    }

    /**
     * Bernie.
     * @param fileName Done
     */
    public void flagForRemoval(String fileName) {
        if (toAdd.containsKey((fileName))) {
            toAdd.remove(fileName);
        }
        toRemove.add(fileName);
    }

    /**
     * Bernie.
     */
    public void saveStage() {
        Utils.writeObject(Repository.STAGING_AREA_FILE, this);
    }

    /**
     * Bernie.
     */
    public void updateStage() {
        if (!Repository.STAGING_AREA_FILE.exists()) {
            throw new GitletException("Staging area file not initialized.");
        }
        StagingArea readFromFile = Utils.readObject(
                Repository.STAGING_AREA_FILE, StagingArea.class);
        this.toAdd = readFromFile.toAdd;
        this.toRemove = readFromFile.toRemove;
    }

    /**
     * Bernie.
     */
    public void clear() {
        toAdd.clear();
        toRemove.clear();
    }

    /**
     * Bernie.
     * @return
     */
    public TreeMap<String, String> getToAdd() {
        return toAdd;
    }

    /**
     * Bernie.
     * @return
     */
    public TreeSet<String> getToRemove() {
        return toRemove;
    }
}
