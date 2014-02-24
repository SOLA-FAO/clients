/**
 * ******************************************************************************************
 * Copyright (C) 2014 - Food and Agriculture Organization of the United Nations (FAO).
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 *    1. Redistributions of source code must retain the above copyright notice,this list
 *       of conditions and the following disclaimer.
 *    2. Redistributions in binary form must reproduce the above copyright notice,this list
 *       of conditions and the following disclaimer in the documentation and/or other
 *       materials provided with the distribution.
 *    3. Neither the name of FAO nor the names of its contributors may be used to endorse or
 *       promote products derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT
 * SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,PROCUREMENT
 * OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,STRICT LIABILITY,OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * *********************************************************************************************
 */
package org.sola.clients.swing.common.utils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;

public class InternalNumberComparator implements Comparator<String> {

    public static final String NUMBER_PATTERN = "(\\-?\\d+\\.\\d+)|(\\-?\\.\\d+)|(\\-?\\d+)";

    /**
     * Splits strings into parts sorting each instance of a number as a number if there is
     * a matching number in the other String.
     * 
     * For example A1B, A2B, A11B, A11B1, A11B2, A11B11 will be sorted in that order instead
     * of alphabetically which will sort A1B and A11B together.
     */
    public int compare(String str1, String str2) {
        if(str1 == null || str2 == null) {
            return 0;
        }

        List<String> split1 = split(str1);
        List<String> split2 = split(str2);
        int diff = 0;

        for(int i = 0; diff == 0 && i < split1.size() && i < split2.size(); i++) {
            String token1 = split1.get(i);
            String token2 = split2.get(i);

            if(token1.matches(NUMBER_PATTERN) && token2.matches(NUMBER_PATTERN)) {
                diff = (int) Math.signum(Double.parseDouble(token1) - Double.parseDouble(token2));
            } else {
                diff = token1.compareToIgnoreCase(token2);
            }
        }
        if(diff != 0) {
            return diff;
        } else {
            return split1.size() - split2.size();
        }
    }

    /**
     * Splits a string into strings and number tokens.
     */
    private List<String> split(String s) {
        List<String> list = new ArrayList<String>();
        Scanner scanner = new Scanner(s);
        int index = 0;
        String num = null;
        while((num = scanner.findInLine(NUMBER_PATTERN)) != null) {
            int indexOfNumber = s.indexOf(num, index);
            if(indexOfNumber > index) {
                list.add(s.substring(index, indexOfNumber));
            }
            list.add(num);
            index = indexOfNumber + num.length();
        }
        if(index < s.length()) {
            list.add(s.substring(index));
        }
        return list;
    }
}