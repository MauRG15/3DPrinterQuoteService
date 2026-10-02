package mrgcorp.PrinterQuoteService.services;

import mrgcorp.PrinterQuoteService.models.Quote;
import mrgcorp.PrinterQuoteService.repositories.FilamentRepository;
import mrgcorp.PrinterQuoteService.repositories.QuoteRepository;
import org.springframework.stereotype.Service;

@Service
public class QuoteService {
    private final FilamentRepository filamentRepository;
    private final QuoteRepository quoteRepository;

    public QuoteService(FilamentRepository filamentRepository,
                        QuoteRepository quoteRepository){
        this.filamentRepository=filamentRepository;
        this.quoteRepository=quoteRepository;
    }
    //Generar Quote
    public Quote processQuote(Quote quote){
        return quoteRepository.save(quote);
    }

    //Obtener Quote
    public Quote findQuote(Long id){
        return quoteRepository.findQuote(id);
    }
}
