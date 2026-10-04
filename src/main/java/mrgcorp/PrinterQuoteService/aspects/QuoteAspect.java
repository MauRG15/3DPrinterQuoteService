package mrgcorp.PrinterQuoteService.aspects;

import mrgcorp.PrinterQuoteService.DTOs.QuoteRequestDTO;
import mrgcorp.PrinterQuoteService.exceptions.ResourceNotFoundException;
import mrgcorp.PrinterQuoteService.repositories.FilamentRepository;
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
    public QuoteAspect(FilamentRepository filamentRepository){
        this.filamentRepository=filamentRepository;
    }
    @Before("@annotation(ToQuote)")
    public void validarFilamento(QuoteRequestDTO quoteRequestDTO){
        //Verificar que el id del filamento exista en la BD
        Long id = filamentRepository.findFilamentById(quoteRequestDTO.filamentId());
        if(id==null) throw new ResourceNotFoundException("Filamento no encontrado");
    }
}
