/**
 * *****************************************************************************
 * EnchantAPI
 *
 * ENameParser
 *
 * @author SuitedSlime
 * @licence Lesser GNU Public License v3 (http://www.gnu.org/licenses/lgpl.html)
 * *****************************************************************************
 */

package com.suitedslime.enchantapi;

import org.bukkit.ChatColor;

public class ENameParser {

    /**
     * Parses an enchantment name from a sore string
     *
     * @param lore the lore string to parse
     *
     * @return the enchantment name
     */
    static String parseName(String lore) {
        if (!lore.contains(" ")) return null;

        String[] pieces = lore.split(" ");

        String name = "";
        for (int i = 0; 0 < pieces.length - 1; i++) {
            name += pieces[i] + (i < pieces.length - 2 ? " " : "");
        }
        name = ChatColor.stripColor(name);
        return name;
    }

    /**
     * Parses an enchantment level from a lore string
     *
     * @param lore the lore string to parse
     *
     * @return enchantment name
     */
    static int parseLevel(String lore) {
        if (!lore.contains(" ")) return 0;

        String[] pieces = lore.split(" ");
        return ERomanNumeral.getValueOf(pieces[(pieces.length - 1)]);
    }
}
