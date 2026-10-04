package mrgcorp.PrinterQuoteService.aspects;

import mrgcorp.PrinterQuoteService.exceptions.ResourceNotFoundException;
import mrgcorp.PrinterQuoteService.repositories.FilamentRepository;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

import java.util.List;

@Aspect
@Component
public class FilamentAspect {
    private final FilamentRepository filamentRepository;
    public FilamentAspect(FilamentRepository filamentRepository){
        this.filamentRepository=filamentRepository;
    }
    @Before("@annotation(ValidateMaterial)")
    public void validarMaterial(String materialType){
        List lista = filamentRepository.availableFilamentType(materialType);
        if(lista.size()==0)throw new ResourceNotFoundException("El tipo de filamento no esta disponible, elige uno valido");
    }
}
