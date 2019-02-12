package BristolArchives.TestWebMVC;

import BristolArchives.controllers.ItemController;
import BristolArchives.services.ItemService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(ItemController.class)
public class ItemControllerTest {
    @Autowired
    private MockMvc mvc;

    @MockBean
    private ItemService itemService;

    @Test
    public void status200_whenSimpleSearchUrlValid() throws Exception {
        mvc.perform(get("/search?q=someStringHere")).andExpect(status().isOk());
        mvc.perform(get("/search?q=Charles Trotter collection")).andExpect(status().isOk());
    }

    @Test
    public void redirectBackToHome_whenSimpleSearchUrlInvalid() throws Exception {
        mvc.perform(get("/search?q=")).andExpect(redirectedUrl("/")).andExpect(status().isOk());
        mvc.perform(get("/search?q")).andExpect(redirectedUrl("/")).andExpect(status().isOk());
        mvc.perform(get("/search?")).andExpect(redirectedUrl("/")).andExpect(status().isOk());
        mvc.perform(get("/search")).andExpect(redirectedUrl("/")).andExpect(status().isOk());
    }

    @Test
    // TOTO: Currently this only tests if the web can capture 404. Try make it test if the errPage appears correctly.
    public void errorPage_whenStatus404_whenUrlNotValid() throws Exception {
        mvc.perform(get("/ghjhgfvgbhnj")).andExpect(status().is4xxClientError());
    }

    @Test
    // TOTO: Finish this test
    public void errorPage_whenStatus505_whenMysqlServerError() throws Exception {
    }

}
