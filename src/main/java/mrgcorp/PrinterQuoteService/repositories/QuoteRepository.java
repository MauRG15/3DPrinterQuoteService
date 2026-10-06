package mrgcorp.PrinterQuoteService.repositories;

import mrgcorp.PrinterQuoteService.models.Quote;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface QuoteRepository extends CrudRepository<Quote,Long> {
    //Obtener una Quote
    @Query("SELECT * FROM Quote WHERE id =:id")
    Optional<Quote> findQuote(Long id);

    //Guardar Query
    //Usamos el metodo heredado de CrudRepository save
}
