package pro.redsoft;

import com.jayway.restassured.module.mockmvc.response.MockMvcResponse;
import com.jayway.restassured.response.ExtractableResponse;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import static org.assertj.core.api.Assertions.*;
//import javax.inject.Inject;
import pro.redsoft.controller.FileUploadController;
import pro.redsoft.storage.FileStorage;
import pro.redsoft.storage.TempStorageImpl;

import static com.jayway.restassured.module.mockmvc.RestAssuredMockMvc.given;
import static org.springframework.test.annotation.DirtiesContext.MethodMode.AFTER_METHOD;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
@WebAppConfiguration
public class FileUploadControllerTest extends BackendBaseTest {

    @Autowired
    FileStorage fileStorage;

    @Autowired
    TempStorageImpl tempStorage;

    static File testFile;

    static byte[] multiPartFile = new byte[1024*200];
    static final String FILE_NAME = "file-to-test-uploading";
     ExecutorService  executor;
    static int numberOfThreads = 4000;

    @Override
    public void init() {
        super.init();
        executor = Executors.newFixedThreadPool(numberOfThreads);
        testFile = new File(FileUploadControllerTest.class.getResource("/file-to-test-uploading").getFile());
        multiPartFile = prepareArray(multiPartFile);
        System.out.println(multiPartFile.length);
    }

    private static byte[] prepareArray(byte[] byteArray) {

        for(int i = 0 ; i<byteArray.length ; i++){
            byteArray[i] = (byte)(Math.random()*255);
        }
        return byteArray;
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
    @DirtiesContext(methodMode = AFTER_METHOD)
    public void testMaxCountUploads() {
        setTemporaryStorage();



        List<Callable<ExtractableResponse<MockMvcResponse>>> taskList = new ArrayList<>();

        for (int i = 0; i < numberOfThreads; i++) {
            Callable<ExtractableResponse<MockMvcResponse>> task = () -> given()
                    .multiPart("file", FILE_NAME, multiPartFile)
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
            ex.printStackTrace();
        }

        //MongoThreadPool.shouldTerminate = true;

    }

    private void setTemporaryStorage() {
        context.getBean(FileUploadController.class).setFileStorage(tempStorage);
    }

    @Override
    public void shutdown() {
        //fileStorage.deleteTestFiles(testFile.getName());
        TempStorageImpl.listOfFiles.stream().forEach(File::delete);
        super.shutdown();
    }
}
