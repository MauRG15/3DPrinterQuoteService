package mrgcorp.PrinterQuoteService.controllers;

import mrgcorp.PrinterQuoteService.DTOs.QuoteRequestDTO;
import mrgcorp.PrinterQuoteService.DTOs.QuoteResponseDTO;
import mrgcorp.PrinterQuoteService.services.QuoteService;
import org.springframework.http.*;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/quote")
public class QuoteController {
    private final QuoteService quoteService;
    public QuoteController(QuoteService quoteService){
        this.quoteService=quoteService;
    }

    //Obtener una Quote
    @GetMapping("/{id}")
    public ResponseEntity<QuoteResponseDTO> getQuote(@PathVariable Long id){
        QuoteResponseDTO quoteResponseDTO = quoteService.getQuoteById(id);
        return ResponseEntity.status(HttpStatus.OK).body(quoteResponseDTO);
    }
    //Crear una quote nueva
    //@Validated, apoya a que el tipo de dato se cumpla o arroja una runtime excepcion
    @PostMapping
    public ResponseEntity<QuoteResponseDTO> createQuote(@Validated @ModelAttribute QuoteRequestDTO quote){
        var quoteRespuesta = quoteService.processQuote(quote);
        return ResponseEntity.status(HttpStatus.CREATED).body(quoteRespuesta);
    }
    //Obtener el STL de una Quote
    @GetMapping("/{id}/stl")
    public ResponseEntity<byte[]> getStl(@PathVariable Long id) {
        byte[] stl = quoteService.getStlById(id);
        //Configurar cabeceras HTTP
        HttpHeaders httpHeaders = new HttpHeaders();
        //APPLICATION_OCTET_STREAM - El navegador no intenta interpretarlos y fuerza la descarga
        httpHeaders.setContentType(MediaType.APPLICATION_OCTET_STREAM);

        //Indicar que es un archivo y no una respuesta como una pagina web o un texto
        //attachment - indica que se abra una ventana de descarga
        //filename - nombre por defecto a guardarlo
        httpHeaders.setContentDisposition(
                ContentDisposition.attachment()
                        .filename("quote_"+id+".stl")
                        .build()
        );
        return ResponseEntity.status(HttpStatus.OK).headers(httpHeaders).body(stl);
    }
}
