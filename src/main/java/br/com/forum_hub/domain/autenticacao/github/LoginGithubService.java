package br.com.forum_hub.domain.autenticacao.github;

import java.util.Map;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class LoginGithubService {

	private final String clientId = "xxxxx";
	private final String clientSecret = "xxxx";
	private final String redirectUri = "http://localhost:8080/login/github/autorizado";
	private final RestClient restClient;

	public String gerarUrl() {

		return "https://github.com/login/oauth/authorize" + "?client_id=" + clientId + "&redirect_uri=" + redirectUri
				+ "&scope=read:user,user:email";
	}

	public Object obterToken(String code) {

		var resposta = restClient.post().uri("https://github.com/login/oauth/access_token")
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON).body(Map.of("code", code,
						"client_id", clientId, "client_secret", clientSecret, "redirect_uri", redirectUri))
				.retrieve()
				.body(String.class);
		return resposta;
	}

	public LoginGithubService(RestClient.Builder restClientBuilder) {
		this.restClient = restClientBuilder.build();
	}
}
