package org.example.storeapplication.bootstrap;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.storeapplication.entities.Item;
import org.example.storeapplication.repositories.ItemRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Component
@RequiredArgsConstructor
@Slf4j
public class BootstrapData implements CommandLineRunner {

    private final ItemRepository itemRepository;

    @Override
    public void run(String... args) throws Exception {
        loadItemData();
    }

    private void loadItemData() {

//        itemRepository.deleteAll();

        if(itemRepository.count()>3) return;

        Random rand = new Random();

        List<Item> items = new ArrayList<>();

        for (int i = 1; i <= 30; i++) {
            Item item = Item.builder()
                    .name("Test Item " + i)
                    .available(i * 3)
                    .price(new BigDecimal("9.99").add(new BigDecimal(rand.nextInt(0, i))))
                    .build();

            items.add(item);
        }
        itemRepository.saveAll(items);

        System.out.println(itemRepository.findAll());

        log.info("Load 30 generated items");
        log.info("Total items in database: {}", itemRepository.count());
    }
}
