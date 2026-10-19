package br.com.fiap3espv.AutoEscola3ESPV.application.core.domain;

import br.com.fiap3espv.AutoEscola3ESPV.adapter.out.repository.entity.InstrutorEntity;
import br.com.fiap3espv.AutoEscola3ESPV.shared.vo.enumeration.MotivoCancelamento;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity(name = "Instrucao")
@Table(name = "instrucoes")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@EqualsAndHashCode(of = "id")
public class Instrucao {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_aluno")
    private Aluno aluno;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_instrutor")
    private InstrutorEntity instrutor;

    @Column(name = "data_hora")
    private LocalDateTime dataHora;

    @Enumerated(EnumType.STRING)
    @Column(name = "motivo_cancelamento")
    private MotivoCancelamento motivoCancelamento;

    public Instrucao(Long id, Aluno aluno, InstrutorEntity instrutor, LocalDateTime dataHora) {
        this(id, aluno, instrutor, dataHora, null);
    }

    public boolean isCancelada() {
        return motivoCancelamento != null;
    }

    public void cancelar(MotivoCancelamento motivo) {
        this.motivoCancelamento = motivo;
    }
}
