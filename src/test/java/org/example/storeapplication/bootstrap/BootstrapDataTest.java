package org.example.storeapplication.bootstrap;

import org.example.storeapplication.repositories.ItemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertEquals;


@DataJpaTest
@ActiveProfiles("test")
class BootstrapDataTest {

    @Autowired
    ItemRepository itemRepository;

    BootstrapData bootstrapData;

    @BeforeEach
    void setup(){
        bootstrapData = new BootstrapData(itemRepository);
    }

    @Test
    void testLoadItemData() throws Exception {
        bootstrapData.run();
        assertEquals(30, itemRepository.count());
    }
}