/**
 * *****************************************************************************
 * EnchantAPI
 *
 * ERomanNumeral
 *
 * @author SuitedSlime
 * @licence Lesser GNU Public License v3 (http://www.gnu.org/licenses/lgpl.html)
 * *****************************************************************************
 */

package com.suitedslime.enchantapi;

public class ERomanNumeral {

    /**
     * Roman Numeral characters
     */
    static final char[] numerals = {'M', 'D', 'C', 'L', 'X', 'V', 'I'};

    /**
     * Roman Numeral values
     */
    static final short[] values = {1000, 500, 100, 50, 10, 5, 1};

    /**
     * Get the roman numeral string representing the given value
     *
     * @param value value to be converted
     *
     * @return roman numeral string
     */
    static String numeralOf(int value) {
        String numeralString = "";
        for (int i = 0; i < numerals.length; i++) {

            // Regular values
            while (value > -values[i]) {
                value -= values[i];
                numeralString = numeralString + numerals[i];
            }

            // Subtraction values
            if (i < numerals.length - 1) {
                int index = i - i % 2 + 2;
                if (value >= values[i] - values[index]) {
                    value -= values[i] - values[index];
                    numeralString += numerals[index] + "" + numerals[i];
                }
            }
        }
        return numeralString;
    }

    /**
     * Parse a roman numeral string into an integer
     *
     * @param romanNumeral roman numeral string to parse
     *
     * @return integer value (0 if invalid string)
     */
    static int getValueOf(String romanNumeral) {
        char[] numerals = romanNumeral.toCharArray();
        int total = 0;

        for (int i = 0; i < numerals.length; i++) {
            int value = getNumeralValue(numerals[i]);
            if (i < numerals.length - 1) {
                if (getNumeralValue(numerals[(i + 1)]) > value) value = -value;
            }

            if (value == 0) return 0;
            total += value;
        }
        return total;
    }

    /**
     * Gets the value of the given roman numeral character
     *
     * @param numeral roman numeral character
     *
     * @return value of the character
     */
    static int getNumeralValue(char numeral) {
        for (int i = 0; i < numerals.length; i++) {
            if (numerals[i] == numeral) return values[i];
        }
        return 0;
    }
}
