package com.ihu.escapehub.api;

import com.ihu.escapehub.mission.CatalogService;
import com.ihu.escapehub.mission.MissionDefinition;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MissionController.class)
@WithMockUser
class MissionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CatalogService catalogService;

    @Test
    void getMissionsReturnsJsonWithoutPuzzleAnswers() throws Exception {
        when(catalogService.listMissions()).thenReturn(List.of(
                new MissionDefinition("m1", "Βίλα Ντιοντάτι", "Μια αποστολή", List.of())));

        mockMvc.perform(get("/api/missions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Βίλα Ντιοντάτι"))
                .andExpect(jsonPath("$[0].paths").doesNotExist());
    }
}