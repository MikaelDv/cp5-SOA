package br.com.fiap3espv.AutoEscola3ESPV.adapter.out.client.viacep;

import br.com.fiap3espv.AutoEscola3ESPV.application.port.out.ConsultaCepPort;
import br.com.fiap3espv.AutoEscola3ESPV.exception.type.CepNotFoundException;
import br.com.fiap3espv.AutoEscola3ESPV.exception.type.ServicoExternoException;
import br.com.fiap3espv.AutoEscola3ESPV.shared.vo.endereco.Endereco;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Component
public class ViaCepClient implements ConsultaCepPort {
    private final RestClient restClient;

    public ViaCepClient(RestClient.Builder builder, @Value("${api.viacep.url}") String baseUrl) {
        this.restClient = builder.baseUrl(baseUrl).build();
    }

    @Override
    public Endereco consultar(String cep) {
        String cepNumerico = cep == null ? "" : cep.replaceAll("\\D", "");
        if (!cepNumerico.matches("\\d{8}")) {
            throw new CepNotFoundException("CEP deve conter 8 dígitos numéricos!");
        }

        DadosViaCep resposta;
        try {
            resposta = restClient
                    .get()
                    .uri("/{cep}/json/", cepNumerico)
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .body(DadosViaCep.class);
        } catch (HttpClientErrorException.BadRequest ex) {
            throw new CepNotFoundException("CEP informado é inválido!");
        } catch (RestClientException ex) {
            throw new ServicoExternoException("Falha ao consultar o serviço ViaCEP!", ex);
        }

        //ViaCEP retorna 200 com {"erro": "true"} quando o CEP não existe
        if (resposta == null || "true".equalsIgnoreCase(resposta.erro())) {
            throw new CepNotFoundException("CEP informado não foi encontrado!");
        }

        return new Endereco(
                resposta.logradouro(),
                null,
                resposta.complemento(),
                resposta.bairro(),
                resposta.localidade(),
                resposta.uf(),
                cepNumerico
        );
    }
}
