package lol.vifez.electron.util;

import lombok.experimental.UtilityClass;

import java.util.List;

/* 
 * Electron © Vifez Series
 * Developed by Vifez
 * Copyright (c) 2025 Vifez. All rights reserved.
*/

@UtilityClass
public class StringUtil {

    public String listToString(List<String> list) {
        StringBuilder finalString = new StringBuilder();

        int i = 0;
        for (String str : list) {
            finalString.append(str);
            i++;

            if (i < list.size()) {
                finalString.append("\n");
            }
        }

        return finalString.toString();
    }
}
