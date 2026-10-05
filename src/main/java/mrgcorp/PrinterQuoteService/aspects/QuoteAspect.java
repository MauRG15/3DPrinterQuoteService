package mrgcorp.PrinterQuoteService.aspects;

import mrgcorp.PrinterQuoteService.DTOs.QuoteRequestDTO;
import mrgcorp.PrinterQuoteService.exceptions.ResourceNotFoundException;
import mrgcorp.PrinterQuoteService.repositories.FilamentRepository;
import mrgcorp.PrinterQuoteService.repositories.QuoteRepository;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

import java.util.List;
/*
    Casos de error a evaluar
    1 Recurso no encontrado (idfilamento no encontrado)
 */
@Aspect
@Component
public class QuoteAspect {
    private final FilamentRepository filamentRepository;
    private final QuoteRepository quoteRepository;
    public QuoteAspect(FilamentRepository filamentRepository, QuoteRepository quoteRepository){
        this.filamentRepository=filamentRepository;
        this.quoteRepository=quoteRepository;
    }

    @Before("@annotation(mrgcorp.PrinterQuoteService.annotations.ValidateQuote) && args(quoteRequestDTO)")
    public void validarFilamento(QuoteRequestDTO quoteRequestDTO){
        //Verificar que el id del filamento exista en la BD
        Long id = filamentRepository.findFilamentById(quoteRequestDTO.filamentId());
        if(id==null) throw new ResourceNotFoundException("Filamento no encontrado");
        //Verificar que el filamento este disponible
        if(!filamentRepository.availableFilamentId(id)) throw new ResourceNotFoundException("Filamento no disponible, elige otro");
    }

    @Before("@annotation(mrgcorp.PrinterQuoteService.annotations.ValidateIdQuote) && args(id)")
    public void validarIdQuote(Long id){
        if(quoteRepository.findQuote(id)==null) throw new ResourceNotFoundException("Quote \""+id+"\" no encontrada, verifica que el ID de la Quote sea uno valido");
    }
}
