package mrgcorp.PrinterQuoteService.controllers;

import mrgcorp.PrinterQuoteService.DTOs.QuoteRequestDTO;
import mrgcorp.PrinterQuoteService.DTOs.QuoteResponseDTO;
import mrgcorp.PrinterQuoteService.services.QuoteService;
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
    @GetMapping("/quote")
    public QuoteResponseDTO getQuote(@RequestParam Long id){
        return quoteService.getQuoteById(id);
    }
    //Crear una quote nueva
    //@Validated, apoya a que el tipo de dato se cumpla o arroja una runtime excepcion
    @PostMapping("/quote")
    public QuoteResponseDTO createQuote(@Validated @ModelAttribute QuoteRequestDTO quote){
        return quoteService.processQuote(quote);
    }
}
