package es.batbatcar.v2p4.controllers;

import es.batbatcar.v2p4.exceptions.ViajeAlreadyExistsException;
import es.batbatcar.v2p4.exceptions.ViajeNotCancelableException;
import es.batbatcar.v2p4.exceptions.ViajeNotFoundException;
import es.batbatcar.v2p4.modelo.dto.viaje.Viaje;
import es.batbatcar.v2p4.modelo.repositories.ViajesRepository;
import es.batbatcar.v2p4.utils.Validator;

import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class ViajesController {

    @Autowired
    private ViajesRepository viajesRepository;
    @Autowired
    private Validator validator;

    /**
     * Endpoint que muestra el listado de todos los viajes disponibles
     *
     */
    @GetMapping("viajes")
    public String getViajesAction(Model model) {
        model.addAttribute("viajes", viajesRepository.findAll());
        model.addAttribute("titulo", "Listado de viajes");
        return "viaje/listado";
    }

    @GetMapping("addViaje")
    public String añadirViaje(Model model) {
        return "viaje/anyadir_viaje";
    }

    @PostMapping("/detallesViaje")
    public String postDetallesViaje(@RequestParam Map<String, String> params, Map<String, Object> model, Model modelo)
            throws ViajeAlreadyExistsException, ViajeNotFoundException {
        String ruta = params.get("ruta");
        int plazas = Integer.parseInt(params.get("plazas"));
        String propietario = params.get("propietario");
        int precio = params.containsKey("precio") ? Integer.parseInt(params.get("precio")) : 0;
        int duracion = params.containsKey("duracion") ? Integer.parseInt(params.get("duracion")) : 0;
        String fechaSalida = params.get("fechaSalida");
        String horaSalida = params.get("horaSalida");
        boolean datosValidos;

        int codViaje = viajesRepository.getNextCodViaje();
        LocalDate fechaSalidaLD = LocalDate.parse(fechaSalida);
        LocalTime horaSalidaLT = LocalTime.parse(horaSalida);
        LocalDateTime dateTimeSalida = LocalDateTime.parse(fechaSalida + "T" + horaSalida);
        Viaje viaje = new Viaje(codViaje, propietario, ruta, dateTimeSalida, duracion, precio, plazas);
        Set<String> errores = validator.isValidViaje(viaje);
        if (errores.size()>0) {
            modelo.addAttribute("errores", errores);
            return "viaje/errorCrear";

        } else {
            viajesRepository.save(viaje);
            return "redirect:/viajes";
        }
    }

    @GetMapping("verDetalle")
    public String verDetallesDeUnViaje(@RequestParam("codViaje") int codViaje, Model model) throws ViajeNotFoundException {
        Viaje viaje = viajesRepository.findById(codViaje);
        model.addAttribute("viaje", viaje);
        return "viaje/detalle_viaje";
    }

        @GetMapping("cancelarViaje")
    public String getMethodName(@RequestParam ("codViaje") int codViaje, Model model) throws ViajeNotFoundException, ViajeNotCancelableException, ViajeAlreadyExistsException {
        Viaje viaje = viajesRepository.findById(codViaje);
        viaje.cancelar();
        viajesRepository.save(viaje);
        return "viaje/cancelar_viaje";
    }

}
