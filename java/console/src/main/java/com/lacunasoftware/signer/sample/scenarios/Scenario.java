package com.lacunasoftware.signer.sample.scenarios;

import java.io.IOException;
import java.net.URISyntaxException;

import com.lacunasoftware.signer.RestException;
import com.lacunasoftware.signer.SignerClient;

public abstract class Scenario {

    protected SignerClient signerClient;

    public void Init() throws URISyntaxException {
        String domain = "https://signer-lac.azurewebsites.net";
        String token = "API Sample App|43fc0da834e48b4b840fd6e8c37196cf29f919e5daedba0f1a5ec17406c13a99";
		signerClient = new SignerClient(domain, token);
    }

    public abstract void Run() throws IOException, RestException;
}