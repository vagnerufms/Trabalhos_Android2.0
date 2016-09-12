package br.ufms.vagner.cardapio.util;

import java.math.BigDecimal;

/**
 * Created by Vagner on 12/09/2016.
 */
public class NumberUtils {

    public static boolean isNumberBigDecimal(String str){
        try {
            new BigDecimal(str);
        }catch (Exception e){
            return false;
        }
        return true;
    }
}
