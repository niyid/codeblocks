/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.techducat.codeblocks.util;

import com.techducat.codeblocks.logic.Lexicon;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author odada
 */
public class LexiconHelper {

    private static final Logger LOGGER = Logger.getLogger(LexiconHelper.class.getName());

    public static final String lookup(String word) {
        InputStream stream = Lexicon.class.getResourceAsStream("/com/techducat/codeblocks/resources/words.txt");

        String w = null;
        BufferedReader reader;
        try {
            LOGGER.log(Level.INFO, "Lexicon => {0}", stream.available());
            reader = new BufferedReader(new InputStreamReader(stream));
            while (null != (w = reader.readLine())) {
                if (w.toLowerCase().equals(word.toLowerCase())) {
                    w = word;
                    break;
                } else {
                    w = null;
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(LexiconHelper.class.getName()).log(Level.SEVERE, null, ex);
        }
        return w;
    }
}
