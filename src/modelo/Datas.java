package modelo;

import java.sql.Date;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class Datas {

    public static final DateTimeFormatter FORMATO = DateTimeFormatter.ofPattern("dd/MM/uuuu");

    private Datas() {
    }

    public static LocalDate deSql(Date data) {
        return data == null ? null : data.toLocalDate();
    }

    public static Date paraSql(LocalDate data) {
        return data == null ? null : Date.valueOf(data);
    }

    public static String formatar(LocalDate data) {
        return data == null ? "" : data.format(FORMATO);
    }

    public static LocalDate converter(String texto) {
        if (texto == null || texto.isBlank()) {
            return null;
        }
        try {
            return LocalDate.parse(texto.trim(), FORMATO.withResolverStyle(java.time.format.ResolverStyle.STRICT));
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Data inválida. Use o formato dd/MM/aaaa.");
        }
    }
}
