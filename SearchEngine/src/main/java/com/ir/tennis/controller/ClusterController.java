package com.ir.tennis.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ir.tennis.model.Result;
import com.ir.tennis.service.ClusterEngine;

/**
 * @author giridar
 */
@RestController
public class ClusterController {
	@Autowired
	ClusterEngine clusterEngine;

	@RequestMapping("/cluster")
	public Result run(@RequestParam(value = "kClusterId") Integer kClusterId,
			@RequestParam(value = "aggClusterId1", required = false) Integer aggClusterId1,
			@RequestParam(value = "aggClusterId2", required = false) Integer aggClusterId2) {
		return clusterEngine.cluster(kClusterId, aggClusterId1, aggClusterId2);
	}
}
