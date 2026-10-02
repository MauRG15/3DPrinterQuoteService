package mrgcorp.PrinterQuoteService.services;

import mrgcorp.PrinterQuoteService.models.Filament;
import mrgcorp.PrinterQuoteService.repositories.FilamentRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class FilamentService {
    private final FilamentRepository filamentRepository;

    public FilamentService(FilamentRepository filamentRepository){
        this.filamentRepository = filamentRepository;
    }
    public List<Filament> getAvailableFilaments(){
        return filamentRepository.availableFilaments();
    }
    public List<String> getColorsByMaterial(String materialType){
        return filamentRepository.colorsByMaterial(materialType.toUpperCase());
    }

    //Agregar nuevo filamento
    public void addNewFilament(Filament filament){
        filamentRepository.save(filament);
    }
}
