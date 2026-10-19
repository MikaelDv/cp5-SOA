package br.com.fiap3espv.AutoEscola3ESPV.application.service;

import br.com.fiap3espv.AutoEscola3ESPV.application.port.out.ConsultaCepPort;
import br.com.fiap3espv.AutoEscola3ESPV.shared.vo.endereco.dto.DadosEndereco;
import br.com.fiap3espv.AutoEscola3ESPV.shared.vo.endereco.mapper.EnderecoMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CepService {
    private final ConsultaCepPort consultaCep;
    private final EnderecoMapper enderecoMapper;

    public DadosEndereco consultarEndereco(String cep) {
        return enderecoMapper.toDto(consultaCep.consultar(cep));
    }
}
