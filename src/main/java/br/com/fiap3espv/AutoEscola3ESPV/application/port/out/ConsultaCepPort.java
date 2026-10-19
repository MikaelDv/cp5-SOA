package br.com.fiap3espv.AutoEscola3ESPV.application.port.out;

import br.com.fiap3espv.AutoEscola3ESPV.shared.vo.endereco.Endereco;

public interface ConsultaCepPort {
    Endereco consultar(String cep);
}
