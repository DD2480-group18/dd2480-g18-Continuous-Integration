import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;

import java.io.IOException;
import java.util.stream.Collectors;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;

/** 
 Skeleton of a ContinuousIntegrationServer which acts as webhook
 See the Jetty documentation for API documentation of those classes.
*/
public class ContinuousIntegrationServer extends AbstractHandler {

    public static BuildHistory db;
    public static jsonHandler json;

    /**
     * Initializes the database of builds and the jsonHandler so that
     * they are usable in the other classes and tests.
     */
    public ContinuousIntegrationServer() {
        json = new jsonHandler();
        db = json.readBuildHistory();
    }


    /**
     * The handle method is fired upon every request sent to the server. It has three endpoints:
     *      * "/": this is where the server receives the GitHub push-event and handles that
     *      * "/allBuilds": here, the build history is shown, including easy-to-read results
     *      * "/build/{buildID}": here, detailed results from the build with ID {buildID} are shown, including raw logs
     *
     * @param target: the endpoint the request is trying to reach
     * @param baseRequest: containing the HTTP request in a jetty request object
     * @param request: containing the HTTP request in a servlet request object
     * @param response: containing the HTTP response in a servlet response object
     * @throws IOException: if there is an error when reading from the request reader or writing to the response writer
     * @throws ServletException: in case there is an error initiating a request/response object
     */
    public void handle(String target,
                       Request baseRequest,
                       HttpServletRequest request,
                       HttpServletResponse response)
            throws IOException, ServletException {
        response.setContentType("text/html;charset=utf-8");
        response.setStatus(HttpServletResponse.SC_OK);
        baseRequest.setHandled(true);

        System.out.println(target);
        

        System.out.println(request.getMethod());



        // here you do all the continuous integration tasks
        // for example
        // 1st clone your repository
        // 2nd compile the code
        switch (target) {
            case "/":
                switch (request.getMethod()) {
                    case "POST":
                        build(baseRequest, request, response);
                        break;
                    default:
                        fourOFour(response);
                        break;
                }
                break;
            default:
                String[] targetParams;
                targetParams = target.split("/");
                // splitting on "/" gives an empty string as first element since first character of target is a "/"
                switch (targetParams[1]) {
                    case "build":
                        search(targetParams[2], baseRequest, request, response);
                        break;
                    case "allBuilds":
                        history(baseRequest, request, response);
                        break;
                    default:
                        fourOFour(response);
                        break;
                }
                break;
        }
    }


    /**
     * Here we handle the build (clone repo, run tests, store results etc. a.k.a bash magic)
     *
     * @param baseRequest: the base HTTP request
     * @param request: HTTP request as per Servlet's implementation
     * @param response: Where to send the result
     */
    private void build(Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.getWriter().println("POST received");
        response.setStatus(200);
        flushResponseWriter(response);
        if (!baseRequest.getHeader("X-Github-Event").equals("ping")) {
            String data = baseRequest.getReader().lines().collect(Collectors.joining());
            RequestHandler a = new RequestHandler();
            a.data = data;
            a.start();
        }
    }

    /**
     * Converts a raw log String into a String with html line breaks
     * so that they can be printed more prettily in the browser.
     * @param logs The raw log as a String, as produced by the build script.
     * @return The same log but with newline characters replaced by the html br-tag.
     */
    private String prettyPrint(String logs) {
        StringBuilder sb = new StringBuilder();
        String[] lines = logs.split("\n");
        for (String line: lines) {
            sb.append(line).append("<br>");
        }
        return sb.toString();
    }

    /**
     * Fetches information about the build with id "buildID"
     *
     * @param buildID: the id of the build to fetch
     * @param baseRequest: the base HTTP request
     * @param request: HTTP request as per Servlet's implementation
     * @param response: Where to send the result
     */
    private void search(String buildID, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException {
        /*
         * Fetch the correct build information file
         * Serve information if it exists, otherwise show some 404-page
         */
        // 404 if build doesn't exist, serve it otherwise
        Build b = db.findBuild(Integer.parseInt(buildID));
        boolean found = b != null;
        if (!found) {
            //some inline HTML as a 404-page if buildID is invalid
            fourOFour(response);
        } else {
            response.getWriter().write(
                "<html>" +
                    "<head>" +
                        "<title>Build " + b.getBuildID() + "</title>" +
                    "</head>" +
                    "<body>" +
                        "<div style=\"text-align: center; width: 100%;\">" +
                            "<h1>Commit " + b.getCommitHash() + " on branch " + b.getBranch() + " built at " + b.getBuildDate() + " with id " + b.getBuildID() + "</h1>" +
                        "</div>" +
                        "<div>" +
                            "<h2>Install results</h2>" +
                            "<div style=\"width: 100%; border: 2px solid\">" +
                                "<p>" + prettyPrint(b.getInstallResult().getInstallLogs()) + "</p>" +
                            "</div>" +
                            "<h2>Build results</h2>" +
                            "<div style=\"width: 100%; border: 2px solid\">" +
                                "<p>" + prettyPrint(b.getBuildResult().getBuildLogs()) + "</p>" +
                            "</div>" +
                            "<h2>Test results</h2>" +
                            "<div style=\"width: 100%; border: 2px solid\">" +
                                "<p>" + prettyPrint(b.getTestResult().getTestLogs()) + "</p>" +
                            "</div>" +
                        "</div>" +
                    "</body>" +
                "</html>");
        }
        flushResponseWriter(response);
    }

    /**
     * Fetches information about all builds that have been processed
     *
     * @param baseRequest: the base HTTP request
     * @param request: HTTP request as per Servlet's implementation
     * @param response: Where to send the result
     */
    private void history(Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException {
        // Fetch the full commit-history and write it to the response
        StringBuilder html = new StringBuilder(
            "<html>" +
                "<head>" +
                    "<title>Build history</title>" +
                "</head>" +
                "<body style=\"vertical-align: center\">" +
                    "<div style=\"display: inline-block; text-align: center; width: 100%;\">" +
                        "<table>" +
                            "<tr>" +
                                "<th>Build id</th>" +
                                "<th>Commit id</th>" +
                                "<th>Branch</th>" +
                                "<th>Date</th>" +
                                "<th>Install result</th>" +
                                "<th>Build result</th>" +
                                "<th>Test result</th>" +
                            "</tr>");
        for (Build b : db.getBuildHistory())
            html.append("<tr><td>").append(b.getBuildID()).append("</td>")
                    .append("<td>")
                    .append("<a href=/build/").append(b.getBuildID()).append(">").append(b.getCommitHash()).append("</a></td>")
                    .append("<td>").append(b.getBranch()).append("</td>")
                    .append("<td>").append(b.getBuildDate()).append("</td>")
                    .append("<td>").append(b.getInstallResult().isInstallSuccessfull() ? "Success" : "Failure").append("</td>")
                    .append("<td>").append(b.getBuildResult().isBuildSuccessfull() ? "Success" : "Failure").append("</td>")
                    .append("<td>").append(b.getTestResult().isTestSuccessfull() ? "Success" : "Failure").append("</td>")
                    .append("</tr>");
        html.append(
                            "</table>" +
                        "</div>" +
                    "</body>" +
                "</html>");
        response.getWriter().write(html.toString());
        flushResponseWriter(response);
    }

    /**
     * Returns an html-page that tells the user that the endpoint does not exist.
     * @param response The servlet response that should return 404 and that was created by the servlet handler.
     * @throws IOException If the response parameter does not have a valid writer.
     */
    private void fourOFour(HttpServletResponse response) throws IOException {
        response.setStatus(404);
        response.getWriter().write(
                "<html>" +
                    "<head>" +
                        "<title>404 - not found</title>" +
                    "</head>" +
                    "<body style=\"vertical-align: center\">" +
                        "<div style=\"width: 100%; display: inline-block; text-align: center;\">" +
                            "<img src=\"https://cdn.searchenginejournal.com/wp-content/uploads/2020/08/killer-404-page-coschedule-5f3d58c828b04.png\">" +
                            "The page you are trying to find does not exist" +
                        "</div>" +
                    "</body>" +
                "</html>");
        flushResponseWriter(response);
    }

    /**
     * Method for flushing the response writer
     *
     * @param response: the response object with the writer
     * @throws IOException: in case of an exception occurring in getWriter()
     */
    private void flushResponseWriter(HttpServletResponse response) throws IOException {
        response.getWriter().flush();
    }

    // used to start the CI server in command line
    public static void main(String[] args) throws Exception {
        jsonHandler jsonHandler = new jsonHandler();
        BuildHistory db = jsonHandler.readBuildHistory();

        Server server = new Server(8080);
        server.setHandler(new ContinuousIntegrationServer());
        server.start();
        server.join();
    }

}
