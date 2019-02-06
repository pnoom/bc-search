package BristolArchives.TestWebMVC;

import BristolArchives.controllers.ItemController;
import BristolArchives.entities.Item;
import BristolArchives.entities.SubCollection;
import BristolArchives.services.ItemService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;


import java.util.Arrays;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(ItemController.class)
public class ItemControllerTest {
    @Autowired
    private MockMvc mvc;

    @MockBean
    private ItemService itemService;

    @Test
    public void status200_whenSimpleSearchHasResult() throws Exception {

        SubCollection testSubColl = new SubCollection();
        testSubColl.setId(1);
        testSubColl.setName("testSubColl");

        Item testItem = new Item("01/2019","testItem","testLocation","test Description","06/02/2019","false","testExten","testPhysDesc","testMulIrn",testSubColl);

        List<Item> allItems = Arrays.asList(testItem);

        //given(itemService.getAllItems()).willReturn(allItems);

        mvc.perform(get("/search?q=testItem")
//                .contentType(MediaType.APPLICATION_JSON)
//                .content("[" +
//                        "{\"itemRef\": \"01/2019\", " +
//                        "\"name\": \"testItem\"," +
//                        "\"location\": \"testLocation\"," +
//                        "\"description\": \"test Description\"," +
//                        "\"dateCreated\": \"06/02/2019\"," +
//                        "\"copyrighted\": \"false\"," +
//                        "\"extent\": \"testExtent\"," +
//                        "\"physTechDesc\": \"testItem\", }" +
//                        "\"multimediaIrn\": \"testMulIrn\", }" +
//                        "\"subCollection\": \"1\", }" +
//                        " ]\n"))
                    ).andExpect(status().isOk());

    }
}
