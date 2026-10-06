package mrgcorp.PrinterQuoteService.controllers;

import mrgcorp.PrinterQuoteService.DTOs.FilamentDTO;
import mrgcorp.PrinterQuoteService.services.FilamentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/filament")
public class FilamentController {
    private final FilamentService filamentService;
    public FilamentController(FilamentService filamentService){
        this.filamentService=filamentService;
    }

    @GetMapping("/available")
    public ResponseEntity<List<FilamentDTO>> getAvailableFilaments(){
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(filamentService.getAvailableFilaments());
    }

    @GetMapping("/colorsByMaterial")
    public ResponseEntity<List<String>> getColorsByMaterial(@RequestParam String materialType){
        List<String> colores = filamentService.getColorsByMaterial(materialType);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(colores);
    }
}
