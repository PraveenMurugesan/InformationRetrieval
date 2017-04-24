package com.ir.tennis.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author OpenSource
 */
@Service
public class DocumentProcessor {
	@Autowired
	Set<String> stopWords;

	@Autowired
	Lemmatizer lemmatizer;

	public List<String> getTokens(String content) {
		List<String> tokensList = new ArrayList<>();

		/*
		 * Remove all the '. ' at the end of the line and replace it with space
		 */
		content = content.replaceAll("\\. ", " ");

		/*
		 * Split the tokens into two if there is a space or , or / or \ or -*
		 * between the two words
		 */
		String[] tokens = content.split("\\s+|\\/|\\\\|\\-|,|_");

		for (String token : tokens) {
			token = token.trim();

			/* Replace all the 's with a "" character */
			token = token.replaceAll("'s", "");

			/*
			 * Replace all the special characters except meta characters and .
			 */
			token = token.replaceAll("[^\\w.]", "");

			/*
			 * Delete the . in the token if it doesn't correspond in a valid
			 * token 2.3, j.y, C1.25 are valid tokens. . should not be removed.
			 * 1.2.... - invalid token
			 */

			/* The numbers are not considered as token. */
			if (token.matches("[\\d]+"))
				continue;
			if (token.contains(".") && !token.matches("^(\\w+)([\\.])(\\w+)+"))
				continue;
			if (token.isEmpty())
				continue;

			/* Converting all the letters in the token to lowercase */
			token = token.toLowerCase();
			token = token.trim();

			/* Exclude the stop words */
			if (stopWords.contains(token))
				continue;

			/* Lemmatize the token */
			/*			String lemma = lemmatizer.lemmatize(token).get(0);

			 Add to the vocabulary set 
			if (lemma == null || lemma.length() == 0) {
				lemma = token;
			}*/
			tokensList.add(token);
		}

		return tokensList;
	}
}
