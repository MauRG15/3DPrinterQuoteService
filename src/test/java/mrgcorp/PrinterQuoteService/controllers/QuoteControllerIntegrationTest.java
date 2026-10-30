package mrgcorp.PrinterQuoteService.controllers;

import mrgcorp.PrinterQuoteService.exceptions.ResourceNotFoundException;
import mrgcorp.PrinterQuoteService.services.QuoteService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc//prepara el MockMvc para inyectarlo
public class QuoteControllerIntegrationTest {
    @MockitoBean
    private QuoteService quoteService;
    @Autowired
    private MockMvc mockMvc;//Inyectamos el MockMvc que se encargar a de simular la peticion http

    @Test
    @DisplayName("Test un id de Quote inexistente devuelve lo programado en el handler (404)")
    void quoteInexistente() throws  Exception{
        //Arrange
        //Buscamos una quote que no existe
        given(quoteService.getQuoteById(99L))
                .willThrow(new ResourceNotFoundException("Quote no encontrada"));
        //Act + Assert
        mockMvc.perform(get("/quote/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Devuelve 400 cuando la petición viene sin los campos requeridos")
    void quoteSinCampos() throws  Exception{
        //Act + Assert
        //Pasamos todos los parametros menos el stl
        mockMvc.perform(multipart("/quote"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Test de obtener un STL de una quote inexistente (404)")
    void stlInexistente() throws Exception {
        // 1. Configurar el mock para que lance la excepción cuando busquen un ID que no existe
        given(quoteService.getStlById(999L))
                .willThrow(new ResourceNotFoundException("Cotización no encontrada"));

        // 2. Ejecutar la petición y esperar un HTTP 404 Not Found
        mockMvc.perform(get("/quote/999/stl"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Test de obtener un STL de una quote existente")
    void stlExistente() throws Exception {
        byte[] bytesEsperados=getClass().getResourceAsStream("/benchy.stl").readAllBytes();
        given(quoteService.getStlById(1L))
                .willReturn(bytesEsperados);
        mockMvc.perform(get("/quote/1/stl"))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Disposition",
                        org.hamcrest.Matchers.containsString("attachment")))
                .andExpect(content().bytes(bytesEsperados));
    }
}
