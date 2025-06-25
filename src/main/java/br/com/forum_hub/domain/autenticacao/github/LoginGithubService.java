package br.com.forum_hub.domain.autenticacao.github;

import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class LoginGithubService {

	@Value("${github.oauth.client.id}")
	private String clientId;
	@Value("${github.oauth.client.secret}")
	private String clientSecret;
	private final String redirectUri = "http://localhost:8080/login/github/autorizado";
	private final RestClient restClient;

	public String gerarUrl() {

		return "https://github.com/login/oauth/authorize" + "?client_id=" + clientId + "&redirect_uri=" + redirectUri
				+ "&scope=read:user,user:email";
	}

	private String obterToken(String code) {

		var resposta = restClient.post().uri("https://github.com/login/oauth/access_token")
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON).body(Map.of("code", code,
						"client_id", clientId, "client_secret", clientSecret, "redirect_uri", redirectUri))
				.retrieve()
				.body(Map.class);
		return resposta.get("access_token").toString();
	}

	public LoginGithubService(RestClient.Builder restClientBuilder) {
		this.restClient = restClientBuilder.build();
	}
	
	public String obterEmail(String code) {
		var token = obterToken(code);
		
		var headers = new HttpHeaders();
		headers.setBearerAuth(token);
		
		var resposta = restClient.get().uri("https://api.github.com/user/emails")
				.headers(httpHeaders -> httpHeaders.addAll(headers))
				.accept(MediaType.APPLICATION_JSON)
				.retrieve()
				.body(DadosEmail[].class);
		
		for(DadosEmail d : resposta)
			if(d.primary() && d.verified())
				return d.email();
		
		return null;
	}
}
