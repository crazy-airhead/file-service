package pro.redsoft;

import com.jayway.restassured.module.mockmvc.response.MockMvcResponse;
import com.jayway.restassured.response.ExtractableResponse;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import static org.assertj.core.api.Assertions.*;
import javax.inject.Inject;
import pro.redsoft.storage.FileStorage;
import pro.redsoft.storage.TempStorageImpl;

import static com.jayway.restassured.module.mockmvc.RestAssuredMockMvc.given;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
@WebAppConfiguration
public class FileUploadControllerTest extends BackendBaseTest {

    @Inject
    FileStorage fileStorage;

    File testFile;

    @Override
    public void init() {
        super.init();
        testFile = new File(FileUploadControllerTest.class.getResource("/file-to-test-uploading").getFile());

    }

    @Test
    @Ignore
    public void testController() {

        given()
                .multiPart(testFile)
                .post("/upload").then()
                .assertThat()
                .statusCode(200);
    }

    @Test
   // @Ignore
    public void testMaxCountUploads() {
        ExecutorService executor = Executors.newCachedThreadPool();

        List<Callable<ExtractableResponse<MockMvcResponse>>> taskList = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            Callable<ExtractableResponse<MockMvcResponse>> task = () -> given()
                    .multiPart(testFile)
                    .post("/upload")
                    .then()
                    .extract();
            taskList.add(task);
        }

        try {

            executor.invokeAll(taskList).stream()
                    .forEach(responseFuture -> {
                        try {
                            assertThat(responseFuture.get().statusCode()).isEqualTo(200);
                        } catch (Exception ex) {
                            fail("Test failed");
                        }
                    });

        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }

    }

    @Override
    public void shutdown() {
        fileStorage.deleteTestFiles(testFile.getName());
        TempStorageImpl.shootDown=true;
        super.shutdown();
    }
}
