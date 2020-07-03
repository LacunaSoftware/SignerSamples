package com.lacunasoftware.signer.sample;

import com.lacunasoftware.signer.sample.scenarios.*;

public class App {
	public static void main(String[] args) throws Exception {
		Scenario scenario = new NotifyFlowParticipantsScenario();
		scenario.Init();
		scenario.Run();
	}
}
