package mrgcorp.PrinterQuoteService.controllers;

import mrgcorp.PrinterQuoteService.DTOs.FilamentDTO;
import mrgcorp.PrinterQuoteService.services.FilamentService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc//prepara el MockMvc para inyectarlo
public class FilamentControllerIntegrationTest {
    @MockitoBean
    private FilamentService filamentService;
    @Autowired
    private MockMvc mockMvc;//Inyectamos el MockMvc que se encargar a de simular la peticion http
    @Test
    @DisplayName("Devuelve lista de filamentos con status Ok(200)")
    void testObtenerFilamentos() throws  Exception{
        //Arrange
        List<FilamentDTO> filamentos = List.of(
                new FilamentDTO(1L, "PLA", "Rojo", BigDecimal.valueOf(298), true),
                new FilamentDTO(2L, "PLA-CF", "Negro",BigDecimal.valueOf(298), true));
        given(filamentService.getAvailableFilaments())
                .willReturn(filamentos);
        //Act + Assert
        mockMvc.perform(get("/filament/available"))
                .andExpect(status().isOk());
    }
}
