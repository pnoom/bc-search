package BristolArchives.TestServices;

import BristolArchives.entities.Item;
import BristolArchives.entities.SubCollection;
import BristolArchives.services.ItemService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

public class ItemServiceTest {

    @Autowired
    private ItemService itemService;

    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void getItemByName() {
        SubCollection testSubColl = new SubCollection();
        testSubColl.setId(1);
        testSubColl.setName("testSubColl");
        Item testItem = new Item("01/2019","testItem","testLocation","test Description","06/02/2019","false","testExten","testPhysDesc","testMulIrn",testSubColl);;
        List<Item> resultItems = itemService.getItemByName(testItem.getName());
        assertEquals(1, resultItems.size());
        assertSame(testItem.getId(), resultItems.get(0).getId());
    }
}