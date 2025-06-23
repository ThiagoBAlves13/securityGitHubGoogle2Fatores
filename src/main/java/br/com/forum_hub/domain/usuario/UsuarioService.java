package br.com.forum_hub.domain.usuario;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import br.com.forum_hub.domain.autenticacao.HierarquiaService;
import br.com.forum_hub.domain.perfil.DadosPerfil;
import br.com.forum_hub.domain.perfil.Perfil;
import br.com.forum_hub.domain.perfil.PerfilNome;
import br.com.forum_hub.domain.perfil.PerfilService;
import br.com.forum_hub.infra.email.EmailService;
import br.com.forum_hub.infra.exception.RegraDeNegocioException;
import jakarta.transaction.Transactional;

@Service
public class UsuarioService implements UserDetailsService {

    @Autowired
    UsuarioRepository usuarioRepository;

    @Lazy
    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    EmailService emailService;

    @Autowired
    PerfilService perfilService;

    @Autowired
    @Lazy
    HierarquiaService hierarquiaService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return usuarioRepository.findByEmailIgnoreCaseAndVerificadoTrue(username)
                .orElseThrow(() -> new UsernameNotFoundException("O usuário não foi encontrado!"));
    }

    public Usuario buscarUsuarioPorEmail(String email) {
        return usuarioRepository.findByEmailIgnoreCaseAndVerificadoTrue(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado!"));
    }

    public Usuario buscarUsuarioPorId(Long idUsuario) {
        return usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado!"));
    }

    @Transactional
    public Usuario cadastrar(DadosCadastroUsuario dados) {
        String senhaCriptografada = passwordEncoder.encode(dados.senha());

        var perfil = perfilService.buscarPerfil(PerfilNome.ESTUDANTE);
        var usuario = new Usuario(dados, senhaCriptografada, perfil);

        emailService.enviarEmailVerificacao(usuario);
        return usuarioRepository.save(usuario);
    }

    @Transactional
    public void verificarEmail(String codigo) {
        Usuario usuario = usuarioRepository.findByToken(codigo)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado!"));
        usuario.verificar();
    }

    public Usuario buscarUsuarioPorNomeUsuario(String nomeUsuario) {
        return usuarioRepository.findByNomeUsuarioIgnoreCaseAndVerificadoTrueAndAtivoTrue(nomeUsuario)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado!"));
    }

    @Transactional
    public Usuario editarPerfil(Usuario usuario, DadosEdicaoUsuario dados) {
        return usuario.alterarDados(dados);
    }

    @Transactional
    public void alterarSenha(DadosAlteracaoSenha dados, Usuario usuario) {
        if (!passwordEncoder.matches(dados.senhaAtual(), usuario.getSenha()))
            throw new RegraDeNegocioException("Senha digitada não confere com a senha atual!");
        if (!dados.novaSenha().equals(dados.novaSenhaConfirmacao()))
            throw new RegraDeNegocioException("Senha e confirmação não conferem!");

        String senhaCriptografada = passwordEncoder.encode(dados.novaSenha());
        usuario.alterarSenha(senhaCriptografada);
    }

    @Transactional
    public void desativarUsuario(Long idUsuario, Usuario logado) {
        Usuario usuario = this.buscarUsuarioPorId(idUsuario);
        if (hierarquiaService.usuarioNaoTemPermissoes(logado, usuario, "ROLE_ADMIN"))
            throw new RegraDeNegocioException(
                    "Não é possivel realizar essa operação!");
        usuario.desativar();
    }

    @Transactional
    public Usuario adicionarPerfil(Long id, DadosPerfil dados) {
        Usuario usuario = this.buscarUsuarioPorId(id);
        Perfil perfil = perfilService.buscarPerfil(dados.perfilNome());

        usuario.adicionarPerfil(perfil);
        return usuario;
    }

    public void reativarUsuario(Long id) {
        Usuario usuario = this.buscarUsuarioPorId(id);
        usuario.reativar();
    }
}
