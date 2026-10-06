package mrgcorp.PrinterQuoteService.repositories;

import mrgcorp.PrinterQuoteService.models.Filament;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface FilamentRepository extends CrudRepository<Filament,Long> {
    //Encontrar filamentos disponibles
    @Query("SELECT * FROM Filament WHERE available=1")
    List<Filament> availableFilaments();

    //Encontrar colores disponibles de un tipo de material
    @Query("SELECT DISTINCT color FROM Filament WHERE available=1 AND material_type=:type")
    List<String> colorsByMaterial(String type);

    @Query("SELECT * FROM Filament WHERE id=:id")
    Filament findFilamentById(Long id);
}
