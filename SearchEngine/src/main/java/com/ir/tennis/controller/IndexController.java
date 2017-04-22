package com.ir.tennis.controller;

import org.apache.log4j.Logger;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * TDOD: Remove after adding index page
 * 
 * @author giridar
 */
@RestController
public class IndexController {
	private static final Logger logger = Logger.getLogger(IndexController.class);

	@RequestMapping("/")
	public String run() {
		String msg = "Search engine for Tennis !!";
		logger.info(msg);
		return msg;
	}
}
