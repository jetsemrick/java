package com.cursor.retrostore.web;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class StoreControllerWebTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void home_rendersStorefront() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Cursor Retro Parts")));
    }

    @Test
    void adminCatalog_renders() throws Exception {
        mockMvc.perform(get("/admin/catalog"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Admin: catalog")));
    }
}
