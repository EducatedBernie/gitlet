## Gitlet Design Document
### **Name**: `Bernie Miao`

# Folders & Files
### branches file
  * Serialized `TreeMap <String, String> branches` from your repo.

### stage file
* Serialized `stage` from your repo. 

### headcommit file
* SHA1 of the most recent, serialized commit. 

### log.txt
* Global log. An accumulating String of commit toStrings. Is updated everytime a new commit is created.
### Commits folder
  * File names: SHA1 Hash of the serialized commit
  * File contents: Serialized metadata of the commit

### Blobs folder
  * File names: SHA1 Hash of the blob string content
  * File contents: Content as string, e.g helloworld





# Main.java
Before running any command, main runs `updateRepo()` first.

# Commit.java
### Instance Variables
* `String message` :  contains the message of a commit
* `String timestamp` : time at which the commit was made
* `String parent` : a string, for the file name of the parent of a commit object
* `TreeMap <String, String> blobs`: blobName -> SHA1 of blob

### Methods
* ` Commit(String message, String parent)`: Constructor, parent is null if it is initial commit, timestamp is also earliets as possible in that case.
* `tracks(String filename)`: Returns true if your commit's blobs contain this filename, false otherwise
* `containsHash(String sha1)`: Returns true if your commit blobs contains this hash, false otherwise. 
* `findCommit (String hash)`: Looks up the hash in your commit folder, returns the object if found, throws error otherwise.
* `saveCommit ()`: Serializes the commit with the hash as its name
* `toString()`: Follows the format of the log output file, builds a string from the current commit.


# StagingArea.java
Handles the add and removal, and tracking what files need to be updated per commit. Contains a TreeSet & a TreeMap. Responsible for staging for addition and removal respectively.

### Instance Variables
1. `TreeMap <String, String> toAdd`
   : text file name -> text file SHA1 hash
2. `TreeSet <String> toRemove ` : A TreeSet of File names that should be removed. 

# Repository.java

### Instance variables
* `StagingArea stage`: Every repo needs a stage, right?
* `String HeadCommitID`: Hash value of the most recent, serialized commit.
* `TreeMap<String, String> branches`: branchname -> SHA1 of branch head
### Methods
* `initCheck()`: Checks whether or not repo was initialized by checking if certain files exist and if the heacommit file is empty.
* `saveRepo()`: Serializes stage into a file, writes head commit ID into file, and serializes branches into file
* `updateRepo()`: Reads everything saveRepo() saved. 
* `init()`: Makes staging area. Makes and saves initial commit. Creates master branch. 
* `add()`: Adds file into staging area, with failure cases
* `commit()`: Clones parent commit blobs, and updates them according to the stagingArea.
* `retrieveBlobContents()`: Takes the sha1 of the blob, and return its contents as a string.
* `checkoutRecent(String filename)`: Checks out the most recent commit with a specific file in mind. Calls checkoutCommit.
* `checkoutCommit(String commitID, String filename)`: Checks out according to a specific commitID
* `checkoutBranch(String commitID)`: Checks out all your file according to a specific branch name.
* `getActiveBranch()`: Looks within your branches, if the HeadCommitID matches any of the branches's value, then return that branch name. Shuold be the active branch name.
* `()`:

* `()`:
* `()`:
* `()`:





## Algorithms

## Persistence