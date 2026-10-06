package mrgcorp.PrinterQuoteService.services;

import mrgcorp.PrinterQuoteService.DTOs.FilamentDTO;
import mrgcorp.PrinterQuoteService.exceptions.ResourceNotFoundException;
import mrgcorp.PrinterQuoteService.models.Filament;
import mrgcorp.PrinterQuoteService.repositories.FilamentRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FilamentService {
    private final FilamentRepository filamentRepository;

    public FilamentService(FilamentRepository filamentRepository){
        this.filamentRepository = filamentRepository;
    }
    public List<FilamentDTO> getAvailableFilaments(){
        List<Filament> list = filamentRepository.availableFilaments();
        //EN lugar de regresarle al cliente datos de la entidad que no necesita,
        //Le regresamos datos necesarios, ABSTRACTION
        return list.stream()
                .map(filament -> new FilamentDTO(
                        filament.id(),
                        filament.materialType(),
                        filament.color(),
                        filament.pricePerKg(),
                        filament.available()))
                .collect(Collectors.toUnmodifiableList());
    }

    public List<String> getColorsByMaterial(String materialType){
        String material = materialType.toUpperCase();
        return filamentRepository.colorsByMaterial(material);
    }
}
