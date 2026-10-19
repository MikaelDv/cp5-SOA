package br.com.fiap3espv.AutoEscola3ESPV.adapter.out.client.viacep;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

//Representa o JSON retornado por https://viacep.com.br/ws/{cep}/json/
@JsonIgnoreProperties(ignoreUnknown = true)
public record DadosViaCep(
        String cep,
        String logradouro,
        String complemento,
        String bairro,
        String localidade,
        String uf,
        String erro) {
}
