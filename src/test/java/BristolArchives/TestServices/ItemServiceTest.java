package BristolArchives.TestServices;

import BristolArchives.entities.Item;
import BristolArchives.entities.SubCollection;
import BristolArchives.services.ItemService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ItemServiceTest {

    @Autowired
    private ItemService itemService = new ItemService();

    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void getItemByName() {
//        SubCollection testSubColl = new SubCollection();
//        testSubColl.setId(1);
//        testSubColl.setName("testSubColl");
//        Item testItem = new Item("01/2019","testItem","testLocation","test Description",null,null,"06/02/2019","false","testExten","testPhysDesc","testMulIrn",testSubColl);
//        List<Item> resultItems = itemService.getItemByName(testItem.getName());
//        assertEquals(1, resultItems.size());
//        assertSame(testItem.getId(), resultItems.get(0).getId());
        List<Item> resultItems = itemService.getItemByName("Charles Trotter collection");
        assertEquals(1, resultItems.size());
        assertEquals("Charles Trotter collection", resultItems.get(0).getName());
    }
}