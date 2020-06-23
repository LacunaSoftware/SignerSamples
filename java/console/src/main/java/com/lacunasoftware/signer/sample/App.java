package com.lacunasoftware.signer.sample;

import com.lacunasoftware.signer.*;
import com.lacunasoftware.signer.sample.scenarios.*;

import java.io.IOException;
import java.net.URISyntaxException;

public class App {
	public static void main(String[] args) throws URISyntaxException, RestException, IOException {
		Scenario scenario = new DocumentOrganizationScenario();
		scenario.Init();
		scenario.Run();
	}
}
