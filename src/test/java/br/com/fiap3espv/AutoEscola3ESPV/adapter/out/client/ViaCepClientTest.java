package br.com.fiap3espv.AutoEscola3ESPV.adapter.out.client;

import br.com.fiap3espv.AutoEscola3ESPV.adapter.out.client.viacep.ViaCepClient;
import br.com.fiap3espv.AutoEscola3ESPV.exception.type.CepNotFoundException;
import br.com.fiap3espv.AutoEscola3ESPV.exception.type.ServicoExternoException;
import br.com.fiap3espv.AutoEscola3ESPV.shared.vo.endereco.Endereco;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.restclient.test.autoconfigure.RestClientTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.client.MockRestServiceServer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withServerError;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

//Simula o ViaCEP com MockRestServiceServer: o teste não depende de internet
@RestClientTest(ViaCepClient.class)
@ActiveProfiles("test")
class ViaCepClientTest {
    @Autowired
    ViaCepClient client;

    @Autowired
    MockRestServiceServer server;

    @Test
    @DisplayName("Expectativa: converter a resposta do ViaCEP em Endereco")
    void consultarCenario1() {
        server.expect(requestTo("https://viacep.com.br/ws/01001000/json/"))
                .andExpect(method(org.springframework.http.HttpMethod.GET))
                .andRespond(withSuccess("""
                        {
                          "cep": "01001-000",
                          "logradouro": "Praça da Sé",
                          "complemento": "lado ímpar",
                          "bairro": "Sé",
                          "localidade": "São Paulo",
                          "uf": "SP",
                          "ibge": "3550308"
                        }""", MediaType.APPLICATION_JSON));

        Endereco endereco = client.consultar("01001-000");

        assertThat(endereco.getLogradouro()).isEqualTo("Praça da Sé");
        assertThat(endereco.getCidade()).isEqualTo("São Paulo");
        assertThat(endereco.getUf()).isEqualTo("SP");
        assertThat(endereco.getCep()).isEqualTo("01001000");
        server.verify();
    }

    @Test
    @DisplayName("Expectativa: CEP inexistente ({\"erro\": \"true\"}) gera CepNotFoundException")
    void consultarCenario2() {
        server.expect(requestTo("https://viacep.com.br/ws/99999999/json/"))
                .andRespond(withSuccess("{\"erro\": \"true\"}", MediaType.APPLICATION_JSON));

        assertThatThrownBy(() -> client.consultar("99999999"))
                .isInstanceOf(CepNotFoundException.class);
    }

    @Test
    @DisplayName("Expectativa: CEP com formato inválido não chama o serviço externo")
    void consultarCenario3() {
        assertThatThrownBy(() -> client.consultar("123"))
                .isInstanceOf(CepNotFoundException.class);
        server.verify();
    }

    @Test
    @DisplayName("Expectativa: falha no ViaCEP gera ServicoExternoException")
    void consultarCenario4() {
        server.expect(requestTo("https://viacep.com.br/ws/01001000/json/"))
                .andRespond(withServerError());

        assertThatThrownBy(() -> client.consultar("01001000"))
                .isInstanceOf(ServicoExternoException.class);
    }
}
