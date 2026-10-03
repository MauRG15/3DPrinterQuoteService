package mrgcorp.PrinterQuoteService.repositories;

import mrgcorp.PrinterQuoteService.DTOs.QuoteResponseDTO;
import mrgcorp.PrinterQuoteService.models.Quote;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;

public interface QuoteRepository extends CrudRepository<Quote,Long> {
    //Obtener una Quote
    @Query("SELECT id,status,created_at FROM Quote WHERE id = :id")
    QuoteResponseDTO findQuote(Long id);

    //Guardar Query
    //Usamos el metodo heredado de CrudRepository save
}
