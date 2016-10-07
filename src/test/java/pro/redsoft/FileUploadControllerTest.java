package pro.redsoft;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.io.File;

import javax.inject.Inject;

import pro.redsoft.storage.FileStorage;

import static com.jayway.restassured.module.mockmvc.RestAssuredMockMvc.given;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
@WebAppConfiguration
public class FileUploadControllerTest extends BackendBaseTest {

    @Inject
    FileStorage fileStorage;

    File testFile;

    @Test
    public void testController() {

        testFile = new File(FileUploadControllerTest.class.getResource("/file-to-test-uploading").getFile());

        given()
                .multiPart(testFile)
                .post("/upload").then()
                .assertThat()
                .statusCode(200);
    }

    @Test
    public void testMaxCountUploads() {
        for (int i = 0; i < 1000; i++) {
            Thread thread = new Thread(() -> given()
                    .multiPart(testFile)
                    .post("/upload"));
            thread.start();

        }
    }

    @Override
    public void shutdown() {
        // fileStorage.deleteTestFiles(testFile.getName());
        super.shutdown();
    }
}
