package br.com.forum_hub.infra.security.totp;

import org.springframework.stereotype.Service;

import com.atlassian.onetime.core.TOTPGenerator;
import com.atlassian.onetime.model.TOTPSecret;
import com.atlassian.onetime.service.RandomSecretProvider;

import br.com.forum_hub.domain.usuario.Usuario;

@Service
public class TotpService {

	public String gerarSecret() {
		return new RandomSecretProvider().generateSecret().getBase32Encoded();
	}

	public String gerarQrCode(Usuario usuario) {
		// otpauth://totp/<Issuer>:<User>?secret=<Secret>&issuer=<Issuer>

		var issuer = "Fórum Hub";
		return String.format("otpauth://totp/%s:%s?secret=%s&issuer=%s", issuer, usuario.getNomeUsuario(),
				usuario.getSecret(), issuer);
	}

	public Boolean verificarCodigo(String code, Usuario logado) {

		var secretDecodificada = TOTPSecret.Companion.fromBase32EncodedString(logado.getSecret());
		var codigoAplicacao = new TOTPGenerator().generateCurrent(secretDecodificada).getValue();
		return codigoAplicacao.equals(code);
	}
}
