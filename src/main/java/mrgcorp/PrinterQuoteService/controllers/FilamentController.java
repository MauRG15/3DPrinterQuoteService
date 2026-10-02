package mrgcorp.PrinterQuoteService.controllers;

import mrgcorp.PrinterQuoteService.models.Filament;
import mrgcorp.PrinterQuoteService.services.FilamentService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("3dPrinterQuoteService/filament")
public class FilamentController {
    private final FilamentService filamentService;

    public FilamentController(FilamentService filamentService){
        this.filamentService=filamentService;
    }
    @GetMapping("/available")
    public List<Filament> getAvailableFilaments(){
        return filamentService.getAvailableFilaments();
    }

    @GetMapping("/colorsByMaterial")
    public List<String> getColorsByMaterial(@RequestParam String material_type){
        return filamentService.getColorsByMaterial(material_type);
    }

    @PostMapping("/registerFilament")
    public void registerFilament(@RequestBody(required = true) Filament filament){
        filamentService.addNewFilament(filament);
    }
}
