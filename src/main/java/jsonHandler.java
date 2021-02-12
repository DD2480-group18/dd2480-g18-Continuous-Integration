import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;


import javax.servlet.http.HttpServletRequest;
import java.io.*;

public class jsonHandler {
    private Gson gson;
    private String filePath;
    public static boolean local = true;

    /**
     * Default constructor with hardcoded file path to database file
     */
    public jsonHandler(){
        gson = new Gson();
        filePath = "src/main/resources/BuildHistoryDB.json";
    }

    /**
     * Consturctor with the ability to provide a new filepath for databasefile
     * @param filePath
     */
    public jsonHandler(String filePath){
        gson = new Gson();
        this.filePath = filePath;
    }

    /**
     * Reads the json database and initializes a buildHistory object containing the database
     * @return Object representation of json database
     */
    public BuildHistory readBuildHistory(){
        BuildHistory db = null;
        Gson gson = new Gson();
        try (Reader reader = new FileReader(filePath)) {

            // Convert JSON File to BuildHistory object
            db = gson.fromJson(reader, BuildHistory.class);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return db;
    }


    /**
     * Stores the object version of our database to file, src/main/resources/BuildHistoryDB.json
     * @param db, the object representation of our json database
     */
    public void saveBuildHistory(BuildHistory db){
        try (FileWriter writer = new FileWriter(filePath)) {
            gson.toJson(db, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Saves the payload from a github request, stored in localbuilds/{commitHash}/.github_req.json
     * @param rawGithubRequest payload from github reqeust
     * @param commitHash has for given commit associated with request
     */
    public void saveGithubLogs(JsonObject rawGithubRequest, String commitHash){
        String filePath = (local ? "localbuilds/" : "cloudbuilds/") + commitHash + "/.github_req.json";
        try (FileWriter writer = new FileWriter(filePath)) {
            gson.toJson(rawGithubRequest, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
