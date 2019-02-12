package BristolArchives.TestIntegration;

import BristolArchives.services.ItemService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.ArrayList;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class UserInputAndResultTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ItemService itemService;

    @Test
    public void resultCorrect_whenSimpleSearch() throws Exception {
        mvc.perform(get("/search?q=asdfghjkl"))
                .andExpect(MockMvcResultMatchers.model().attribute("itemList", new ArrayList()));
    }

    @Test
    public void resultCorrect_whenAdvanceSearch() throws Exception {
        mvc.perform(get("/advSearch?&name=Charles Trotter collection"))
                .andExpect(MockMvcResultMatchers.model().attribute("itemList", itemService.getItemByName("Charles Trotter collection")));
    }
}
