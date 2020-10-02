package com.norden.warehousemanagement;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class dateFormatChanger {
    public static String ChangeFormatDate(String _date, String FormatOrigin, String FormatFinal) throws ParseException {
        SimpleDateFormat parseador = new SimpleDateFormat(FormatOrigin);
        SimpleDateFormat formateador = new SimpleDateFormat(FormatFinal);
        Date date = parseador.parse(_date);
        return formateador.format(date);
    }
}
