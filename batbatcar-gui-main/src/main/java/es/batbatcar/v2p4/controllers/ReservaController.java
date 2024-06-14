package es.batbatcar.v2p4.controllers;

import es.batbatcar.v2p4.exceptions.ReservaAlreadyExistsException;
import es.batbatcar.v2p4.exceptions.ReservaNotFoundException;
import es.batbatcar.v2p4.exceptions.ViajeAlreadyExistsException;
import es.batbatcar.v2p4.exceptions.ViajeNotCancelableException;
import es.batbatcar.v2p4.exceptions.ViajeNotFoundException;
import es.batbatcar.v2p4.modelo.dto.Reserva;
import es.batbatcar.v2p4.modelo.dto.viaje.Viaje;
import es.batbatcar.v2p4.modelo.repositories.ViajesRepository;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class ReservaController {

    @Autowired
    private ViajesRepository viajesRepository;

    @GetMapping("/reservarViaje")
    public String reservarViaje(@RequestParam("codViaje") int codViaje, Model model) throws ViajeNotFoundException {
        Viaje viaje = viajesRepository.findById(codViaje);
        if (viaje != null) {
            model.addAttribute("viaje", viaje);
            String codigo = Integer.toString(codViaje);
            model.addAttribute("codViaje", codigo);
            boolean disponible = viaje.estaDisponible();
            if (disponible) {
                return "reserva/anyadir_reserva";
            } else {
                System.out.println("viaje no disponible");
                return "redirect:/viajes";
            }
        } else {
            System.out.println("Viaje null");
            return "redirect:/viajes";
        }
    }

    @PostMapping("/confirmarReserva")
    public String confirmarReserva(
            @RequestParam("nombre") String usuario,
            @RequestParam("plazas") int plazas,
            @RequestParam("codViaje") String codViaje,
            Model model,
            RedirectAttributes redirectAttributes)
            throws ReservaNotFoundException, ViajeNotFoundException, ReservaAlreadyExistsException {

        Viaje viaje = viajesRepository.findById(Integer.parseInt(codViaje));
        if (viaje == null) {
            redirectAttributes.addFlashAttribute("error", "El viaje no fue encontrado.");
            return "redirect:/viajes";
        }

        int plazasReservadas = viajesRepository.getNumPlazasReservadasEnViaje(viaje);
        int plazasDisponibles = viaje.getPlazasOfertadas() - plazasReservadas;

        if (plazas <= plazasDisponibles && !viaje.getPropietario().equals(usuario)) {
            Reserva reserva = new Reserva(String.valueOf(viajesRepository.getNextCodReserva()), usuario, plazas, LocalDateTime.now(), viaje);
            viajesRepository.save(reserva);
            model.addAttribute("viaje", viaje);
            model.addAttribute("nombre", usuario);
            model.addAttribute("plazas", plazas);
            return "reserva/confirmacion";
        } else {
            model.addAttribute("error",
                    "No hay suficientes plazas disponibles o el usuario es el propietario del viaje.");
            return "reserva/error";
        }
    }

    @GetMapping("/listarReservas")
    public String getNombre(@RequestParam ("codViaje")int codViaje, Model model) throws ViajeNotFoundException {
        Viaje viaje = viajesRepository.findById(codViaje);
        model.addAttribute("reservas", viajesRepository.findReservasByViaje(viaje));
        model.addAttribute("titulo", "Listado de reservas");
        return "reserva/listado";
    }

}