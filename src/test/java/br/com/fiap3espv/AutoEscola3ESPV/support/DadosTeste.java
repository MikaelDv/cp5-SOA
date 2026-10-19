package br.com.fiap3espv.AutoEscola3ESPV.support;

import br.com.fiap3espv.AutoEscola3ESPV.shared.vo.endereco.Endereco;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;

public final class DadosTeste {
    private DadosTeste() {
    }

    //Segunda-feira às 10h, pelo menos uma semana à frente (respeita antecedências de 30 min e 24 h)
    public static LocalDateTime proximaSegundaAs10() {
        return LocalDateTime
                .now()
                .plusWeeks(1)
                .with(TemporalAdjusters.next(DayOfWeek.MONDAY))
                .withHour(10)
                .withMinute(0)
                .withSecond(0)
                .withNano(0);
    }

    public static Endereco dadosEndereco() {
        return new Endereco(
                "Rua Teste",
                "896",
                "Casa dos fundos",
                "Vila Teste",
                "TestCity",
                "XT",
                "01234567"
        );
    }

    public static String enderecoJson() {
        return """
                {
                  "logradouro": "Rua Teste",
                  "numero": "896",
                  "complemento": "Casa dos fundos",
                  "bairro": "Vila Teste",
                  "cidade": "TestCity",
                  "uf": "SP",
                  "cep": "01234567"
                }""";
    }
}
