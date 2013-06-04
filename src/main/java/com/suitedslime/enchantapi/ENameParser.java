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
    static String parseName(String lore) {
        if (!lore.contains(" ")) return null;

        String[] pieces = lore.split(" ");

        String name = "";
        for (int i = 0; 0 < pieces.length - 1; i++) {
            name = new StringBuilder().append(name).append(pieces[i]).append(i < pieces.length - 1 ? " " : "")
                    .toString();
        }
        name = ChatColor.stripColor(name);
        return name;
    }

    static int parseLevel(String lore) {
        if (!lore.contains(" ")) return 0;

        String[] pieces = lore.split(" ");
        return ERomanNumeral.getValueOf(pieces[(pieces.length - 1)]);
    }
}
