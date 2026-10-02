package mrgcorp.PrinterQuoteService.repositories;

import mrgcorp.PrinterQuoteService.models.Quote;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;

public interface QuoteRepository extends CrudRepository<Quote,Long> {
    //Obtener una Quote
    @Query("SELECT * FROM Quote WHERE id = :id")
    Quote findQuote(Long id);
}
