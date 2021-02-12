import java.util.ArrayList;

public class BuildHistory {
    private ArrayList<Build> buildHistory;

    /**
     * Create build history object given arraylist<build>
     * @param buildHistory
     */
    public BuildHistory(ArrayList<Build> buildHistory) {
        this.buildHistory = buildHistory;
    }

    /**
     * Default constructor, init empty arraylist
      */
    public BuildHistory(){
        buildHistory = new ArrayList<Build>();
    }

    /**
     * Looks at the last build added and creates a new build id that is 1 larger.
     * @return The first available ID of builds.
     */
    public int getNextBuildID(){
        return buildHistory.get(buildHistory.size()-1).getBuildID()+1;
    }

    /**
     * Find a build given its build id, returns null if build is not in build history
     * @param buildID
     * @return build
     */
    public Build findBuild(int buildID){
        for (int i = 0; i < buildHistory.size(); i++) {
            if(buildHistory.get(i).getBuildID() == buildID){
                return buildHistory.get(i);
            }
        }
        return null;
    }

    /**
     * Adds a build to the build history if the build id is not already in the db.
     * @param b build to be added to db
     */
    public void addBuildToDB(Build b){
        for (int i = 0; i < buildHistory.size(); i++) {
            if(b.getBuildID() == buildHistory.get(i).getBuildID()){
                System.err.println("A build with this build id is already present in database");
                return;
            }
        }
        //build id is unique
        buildHistory.add(b);
    }


    //--------------Getters and Setters ------------------
    public ArrayList<Build> getBuildHistory() {
        return buildHistory;
    }

    public void setBuildHistory(ArrayList<Build> buildHistory) {
        this.buildHistory = buildHistory;
    }


}
