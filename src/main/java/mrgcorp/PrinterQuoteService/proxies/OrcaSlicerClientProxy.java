package mrgcorp.PrinterQuoteService.proxies;

import mrgcorp.PrinterQuoteService.DTOs.SliceRequestDTO;
import mrgcorp.PrinterQuoteService.DTOs.SliceResultResponseDTO;
import mrgcorp.PrinterQuoteService.DTOs.StlDraftResponseDTO;
import mrgcorp.PrinterQuoteService.DTOs.ThreeMfConversionResponseDTO;
import mrgcorp.PrinterQuoteService.exceptions.ConvertStlException;
import mrgcorp.PrinterQuoteService.exceptions.ImportStlException;
import mrgcorp.PrinterQuoteService.exceptions.SlicerStlException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientException;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class OrcaSlicerClientProxy {
    private final WebClient webClient;
    @Value("${slicer.machine-id}")
    private String machineId;

    public OrcaSlicerClientProxy(WebClient webClient){
        this.webClient=webClient;
    }
    //Realizar el proceso de slicer
    public SliceResultResponseDTO processSlicer(MultipartFile stlFile, String processId, String filamentSettingsId,
                                                int pieceQuantity, boolean supportsNeeded, double infillPercentage){
        StlDraftResponseDTO stlDraftResponseDTO=importStl(processId,stlFile);
        ThreeMfConversionResponseDTO threeMfConversionResponseDTO = convertStl(stlDraftResponseDTO.draftToken());
        return sliceStl(threeMfConversionResponseDTO.inputToken(),processId,filamentSettingsId,pieceQuantity,
                supportsNeeded,infillPercentage);
    }

    //Metodos para conseguir respuesta de Endpoint
    //1. Importar el stl
    //Mono -> Promesa de un valor que aun no existe, UN SOLO VALOR Y COMO DESEREALIZARLO
    private StlDraftResponseDTO importStl(String processId, MultipartFile stlFile) {
        final Boolean center = true;
        final Boolean arrange = true;
        final Boolean autoOrient = false;

        //Convertimos los valores enviados en un Map de calve valor
        MultiValueMap<String, Object> campos = new LinkedMultiValueMap<>();
        campos.add("machine_id", machineId);
        campos.add("process_id", processId);
        campos.add("center", center);
        campos.add("arrange", arrange);
        campos.add("auto_orient", autoOrient);
        campos.add("file", stlFile.getResource());

        try {
            return webClient.post()
                    .uri("/stl/import")
                    .body(BodyInserters.fromMultipartData(campos))//Construir cuerpos de peticion en formatos que no son un objeto serializado
                    .retrieve()//Indica que requiero de la respuesta del endpoint
                    .bodyToMono(StlDraftResponseDTO.class)//Que tipo de dato vamos a mapear la respuesta
                    .block();//Didspara la peticion y obliga a esperar por el resultado y DEVUELVE EL OBJETO
        }catch(WebClientException ex){
            throw new ImportStlException("Error de importacion: "+ex.getMessage());
        }
    }
    //2. Convertir a STL
    private ThreeMfConversionResponseDTO convertStl(String draftToken){
        try{
            return webClient.post()
                    .uri("stl/"+draftToken+"/3mf")
                    .retrieve()
                    .bodyToMono(ThreeMfConversionResponseDTO.class)
                    .block();
        }catch(WebClientException ex){
            throw new ConvertStlException("Error de conversion STL: "+ex.getMessage());
        }
    }
    //3. Invocar el Slicer
    private SliceResultResponseDTO sliceStl(
            String inputToken, String processId,String filamentSettingsId,
            int pieceQuantity, boolean supportsNeeded, double infillPercentage){
        //Conversion de datos al tipo que maneja JSON y asignar los faltantes
        final boolean autoCenter = false;
        final String infillPercentageString = infillPercentage +"%";
        final String supportsNeededString = (supportsNeeded)?"1":"0";
        final List<String> filamentSettingsIds = List.of(filamentSettingsId);

        //Crear el atributo Objeto con sus valores respectivos
        Map<String, String> processOverrides = new HashMap<>();
        processOverrides.put("sparse_infill_density",infillPercentageString);
        processOverrides.put("enable_support",supportsNeededString);

        SliceRequestDTO sliceRequestDTO = new SliceRequestDTO(inputToken,machineId,processId,filamentSettingsIds,autoCenter,processOverrides,pieceQuantity);

        try {
            return webClient.post()
                    .uri("/slice/v2")
                    .body(Mono.just(sliceRequestDTO), SliceRequestDTO.class)
                    .retrieve()
                    .bodyToMono(SliceResultResponseDTO.class)
                    .block();
        }catch (WebClientException ex){
            throw new SlicerStlException("Error en el slicer: "+ex.getMessage());
        }
    }
}
