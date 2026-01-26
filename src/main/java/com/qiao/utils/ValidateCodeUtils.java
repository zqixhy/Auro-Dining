package com.qiao.utils;

import java.util.Random;

/**
 * Utility class for randomly generating verification codes
 */
public class ValidateCodeUtils {
    /**
     * Randomly generate verification code
     * @param length Length is 4 or 6 digits
     * @return
     */
    public static Integer generateValidateCode(int length){
        Integer code =null;
        if(length == 4){
            code = new Random().nextInt(9999);//Generate random number, maximum is 9999
            if(code < 1000){
                code = code + 1000;//Ensure random number is 4 digits
            }
        }else if(length == 6){
            code = new Random().nextInt(999999);//Generate random number, maximum is 999999
            if(code < 100000){
                code = code + 100000;//Ensure random number is 6 digits
            }
        }else{
            throw new RuntimeException("Can only generate 4 or 6 digit verification codes");
        }
        return code;
    }

    /**
     * Randomly generate verification code string of specified length
     * @param length Length
     * @return
     */
    public static String generateValidateCode4String(int length){
        Random rdm = new Random();
        String hash1 = Integer.toHexString(rdm.nextInt());
        String capstr = hash1.substring(0, length);
        return capstr;
    }
}
