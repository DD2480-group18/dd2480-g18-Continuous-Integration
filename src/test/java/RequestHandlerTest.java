import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.eclipse.egit.github.core.CommitStatus;
import org.junit.jupiter.api.Test;

import java.io.FileReader;

import static org.junit.jupiter.api.Assertions.fail;

public class RequestHandlerTest {

	/**
	 * this test checks that a commit succeeds in getting updated on github
	 */
	@Test
	void testSucceed(){
		ContinuousIntegrationServer continuousIntegrationServer = new ContinuousIntegrationServer();
		JsonObject jsonObject;
		try {
			JsonParser parser = new JsonParser();
			JsonElement jsonElement = parser.parse(new FileReader("src/test/java/testdatacommit.json"));
			jsonObject = jsonElement.getAsJsonObject();

			RequestHandler.sendCommitStatus(jsonObject, CommitStatus.STATE_PENDING, "UNIT TEST: testSucceed pending");
			String status = RequestHandler.getCommitStatus(jsonObject);
			if (!status.equals(CommitStatus.STATE_PENDING)){
				System.out.println("status not updated on github, maybe bad connection");
				System.out.println();
				fail();
			}

			RequestHandler requestHandler = new RequestHandler();
			requestHandler.data = jsonObject.toString();
			requestHandler.start();
			requestHandler.join();

			status = RequestHandler.getCommitStatus(jsonObject);
			if (!status.equals(CommitStatus.STATE_SUCCESS)){
				System.out.println("Wrong status on github " + status);
				fail();
			}
		}catch (Exception e){e.printStackTrace(); fail();}
	}

	/**
	 * this test checks if a commit is properly failed
	 */
	@Test
	void testFail(){
		ContinuousIntegrationServer continuousIntegrationServer = new ContinuousIntegrationServer();
		JsonObject jsonObject;
		try {
			JsonParser parser = new JsonParser();
			JsonElement jsonElement = parser.parse(new FileReader("src/test/java/testdatacommitfail.json"));
			jsonObject = jsonElement.getAsJsonObject();

			RequestHandler.sendCommitStatus(jsonObject, CommitStatus.STATE_PENDING, "UNIT TEST: testFail pending");
			String status = RequestHandler.getCommitStatus(jsonObject);
			if (!status.equals(CommitStatus.STATE_PENDING)){
				System.out.println("status not updated on github, maybe bad connection");
				System.out.println();
				fail();
			}

			RequestHandler requestHandler = new RequestHandler();
			requestHandler.data = jsonObject.toString();
			requestHandler.start();
			requestHandler.join();

			status = RequestHandler.getCommitStatus(jsonObject);
			if (!status.equals(CommitStatus.STATE_FAILURE)){
				System.out.println("Wrong status on github " + status);
				fail();
			}
		}catch (Exception e){e.printStackTrace(); fail();}
	}
}
