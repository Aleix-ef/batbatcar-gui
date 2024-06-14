package es.batbatcar.v2p4.utils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.HashSet;
import java.util.Set;

import org.springframework.stereotype.Component;

import es.batbatcar.v2p4.exceptions.ReservaNoValidaException;
import es.batbatcar.v2p4.exceptions.ViajeNotFoundException;
import es.batbatcar.v2p4.modelo.dto.viaje.Viaje;

@Component
public class Validator {

    public static boolean isValidDateTime(String dateTime) {
        try {
            LocalDate.parse(dateTime, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        } catch (DateTimeParseException e) {
            return false;
        }
        return true;
    }

    public static boolean isValidDate(String date) {
        try {
            LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        } catch (DateTimeParseException e) {
            return false;
        }
        return true;
    }

    public static boolean isValidTime(String time) {
        try {
            LocalDate.parse(time, DateTimeFormatter.ofPattern("HH:mm"));
        } catch (DateTimeParseException e) {
            return false;
        }
        return true;
    }

    public static boolean isValidRuta(String ruta) {
        return ruta != null && ruta.matches("^[A-Z][a-z]{2,15}-[A-Z][a-z]{2,15}$");
    }

    public static boolean isValidPlazas(int plazas) {
        return plazas >= 1 && plazas <= 6;
    }

    public static boolean isValidPropietario(String propietario) {
        return propietario != null && propietario.matches("[A-Z][a-z]\\s[A-Z][a-z]");
    }

    public static boolean isValidPrecio(double precio) {
        return precio > 0;
    }

    public static boolean isValidDuracion(long duracion) {
        return duracion > 0;
    }

    public static boolean isDateTimeProvided(String date, String time) {
        return date != null && !date.isEmpty() && time != null && !time.isEmpty();
    }

    public Set<String> isValidViaje(Viaje viaje) {
        Set<String> errores = new HashSet<>();

        if (viaje == null) {
            errores.add("El campo 'Viaje' no puede ser nulo");
            return errores;
        }
        if (!isValidPropietario(viaje.getPropietario())) {
            errores.add("El campo 'Propietario' debe tener al menos 2 palabras y empezar por mayuscula");
        }
        if (!isValidRuta(viaje.getRuta())) {
            errores.add("El campo 'ruta' debe de tener un formato similar al siguiente: Inicio-Destino");
        }
        if (!isValidDuracion(viaje.getDuracion())) {
            errores.add("El campo 'duración' debe ser mayor a 0");
        }
        if (!isValidPrecio(viaje.getPrecio())) {
            errores.add("El campo 'precio' debe ser mayor a 0");
        }
        if (!isValidPlazas(viaje.getPlazasOfertadas())) {
            errores.add("El campo 'Plazas' debe estar entre 1 y 6 ambos incluidos");
        }
        return errores;
    }

}
