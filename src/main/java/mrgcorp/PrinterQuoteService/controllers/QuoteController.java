package mrgcorp.PrinterQuoteService.controllers;

import mrgcorp.PrinterQuoteService.DTOs.QuoteRequestDTO;
import mrgcorp.PrinterQuoteService.DTOs.QuoteResponseDTO;
import mrgcorp.PrinterQuoteService.services.QuoteService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/3dPrinterQuoteService")
public class QuoteController {
    private final QuoteService quoteService;
    public QuoteController(QuoteService quoteService){
        this.quoteService=quoteService;
    }

    //Obtener una Quote
    @GetMapping("/quote/{id}")
    public ResponseEntity<QuoteResponseDTO> getQuote(@PathVariable Long id){
        QuoteResponseDTO quoteResponseDTO = quoteService.getQuoteById(id);
        return ResponseEntity.status(HttpStatus.OK).body(quoteResponseDTO);
    }
    //Crear una quote nueva
    //@Validated, apoya a que el tipo de dato se cumpla o arroja una runtime excepcion
    @PostMapping("/quote")
    public ResponseEntity<QuoteResponseDTO> createQuote(@Validated @ModelAttribute QuoteRequestDTO quote){
        var quoteRespuesta = quoteService.processQuote(quote);
        return ResponseEntity.status(HttpStatus.CREATED).body(quoteRespuesta);
    }
}
