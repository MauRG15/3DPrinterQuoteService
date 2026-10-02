package mrgcorp.PrinterQuoteService.controllers;

import mrgcorp.PrinterQuoteService.models.Quote;
import mrgcorp.PrinterQuoteService.services.QuoteService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("3dPrinterQuoteService/quote")
public class QuoteController {
    private final QuoteService quoteService;
    public QuoteController(QuoteService quoteService){
        this.quoteService=quoteService;
    }

    //Obtener una Quote
    @GetMapping("/quote")
    public Quote getQuote(@RequestParam Long id){
        return quoteService.findQuote(id);
    }
    //Crear una quote nueva
    @PostMapping("/quote")
    public void createQuote(@RequestBody Quote quote){
        quoteService.processQuote(quote);
    }
}
