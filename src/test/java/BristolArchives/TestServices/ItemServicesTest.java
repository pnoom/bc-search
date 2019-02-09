package BristolArchives.TestServices;

import BristolArchives.entities.Item;
import BristolArchives.entities.SubCollection;
import BristolArchives.services.ItemService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@RunWith(SpringRunner.class)
@DataJpaTest
public class ItemServicesTest {
    @Autowired
    ItemService itemService;

    @Before
    public void setUp() {
        Item testItem = new Item("01/2019","testItem","testLocation","test Description","06/02/2019","false","testExten","testPhysDesc","testMulIrn",testSubColl);
        Answer<Item> ans = new A;
        Mockito.when(itemService.getItemByName(testItem.getName())).thenReturn(List.asList(testItem));
    }

    @Test
    public void whenFindByName_thenReturnItem() {
        SubCollection testSubColl = new SubCollection();
        testSubColl.setId(1);
        testSubColl.setName("testSubColl");
                //assertThat(itemService.getItemByName("testItem").size().isEqualTo(1));
    }
}
