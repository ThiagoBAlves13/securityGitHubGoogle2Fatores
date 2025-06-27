package br.com.forum_hub.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.com.forum_hub.domain.autenticacao.DadosA2F;
import br.com.forum_hub.domain.autenticacao.DadosLogin;
import br.com.forum_hub.domain.autenticacao.DadosRefreshToken;
import br.com.forum_hub.domain.autenticacao.DadosToken;
import br.com.forum_hub.domain.autenticacao.TokenService;
import br.com.forum_hub.domain.usuario.Usuario;
import br.com.forum_hub.domain.usuario.UsuarioService;
import br.com.forum_hub.infra.security.totp.TotpService;
import jakarta.validation.Valid;


@RestController
public class AutenticacaoController {

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    TokenService tokenService;

    @Autowired
    UsuarioService usuarioService;
    
    @Autowired
    TotpService totpService;

    @PostMapping("/login")
    public ResponseEntity<?> efetuarLogin(@RequestBody @Valid DadosLogin dados) {

        var autenticationToken = new UsernamePasswordAuthenticationToken(dados.email(), dados.senha());

        Usuario usuario = (Usuario)authenticationManager.authenticate(autenticationToken).getPrincipal();
        
        if(usuario.isA2fAtiva())
        	return ResponseEntity.ok(new DadosToken(null, null, true));

        return ResponseEntity.ok(tokenService.obterDadosToken(usuario));

    }
    
    @PostMapping("/verificar-a2f")
    public ResponseEntity<?> verificarSegundoFator(@Valid @RequestParam DadosA2F dadosA2F){
    	
    	var usuario = usuarioService.buscarUsuarioPorEmail(dadosA2F.email());
    	
    	var codigoValido = totpService.verificarCodigo(dadosA2F.codigoUsuario(), usuario);
    	
    	if(!codigoValido)
    		throw new BadCredentialsException("Código Inválido");
    	
    	return ResponseEntity.ok(tokenService.obterDadosToken(usuario));
    	
    }

    @PostMapping("/atualizar-token")
    public ResponseEntity<?> atualizarToken(@RequestBody @Valid DadosRefreshToken dados) {

        var refreshToken = dados.refreshToken();
        var usuario = usuarioService.buscarUsuarioPorId(Long.valueOf(tokenService.verificarToken(refreshToken)));
        
        return ResponseEntity.ok(tokenService.obterDadosToken(usuario));

    }

}
