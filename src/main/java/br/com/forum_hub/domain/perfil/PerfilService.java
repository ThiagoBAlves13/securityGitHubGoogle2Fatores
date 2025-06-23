package br.com.forum_hub.domain.perfil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PerfilService {

    @Autowired
    PerfilRepository perfilRepository;

    public Perfil buscarPerfil(PerfilNome nome){
        return perfilRepository.findByNome(nome);
    }

}
