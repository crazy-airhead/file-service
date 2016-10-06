package pro.redsoft;

import io.restassured.module.mockmvc.RestAssuredMockMvc;
import static io.restassured.module.mockmvc.RestAssuredMockMvc.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import pro.redsoft.controller.FileUploadController;
import pro.redsoft.storage.FileStorage;


import java.io.File;


@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
public class FileUploadControllerTest {

    @Autowired
    FileUploadController fileUploadController;

    @Autowired
    FileStorage fileStorage;

    File testFile;

    @Before
    public void setUp(){
        testFile = new File(FileUploadControllerTest.class.getResource("/file-to-test-uploading").getFile());
        RestAssuredMockMvc.standaloneSetup(fileUploadController);
    }


    @Test
    public void testController() {
        given()
                .multiPart(testFile)
                .post("/upload").then()
                .assertThat()
                .statusCode(200);

    }

    @Test
    public void testMaxCountUploads(){
        for (int i = 0; i<1000; i++){
            Thread thread = new Thread(() -> given()
                    .multiPart(testFile)
                    .post("/upload"));
            thread.start();

        }
    }

    @After
    public void tearDown(){
        fileStorage.deleteTestFiles(testFile.getName());
    }


}
