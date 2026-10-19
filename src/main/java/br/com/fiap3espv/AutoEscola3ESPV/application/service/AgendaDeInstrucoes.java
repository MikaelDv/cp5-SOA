package br.com.fiap3espv.AutoEscola3ESPV.application.service;

import br.com.fiap3espv.AutoEscola3ESPV.adapter.in.controller.request.instrucao.DadosAgendamento;
import br.com.fiap3espv.AutoEscola3ESPV.adapter.in.controller.request.instrucao.DadosCancelamento;
import br.com.fiap3espv.AutoEscola3ESPV.adapter.in.controller.response.instrucao.DetalhamentoAgendamento;
import br.com.fiap3espv.AutoEscola3ESPV.adapter.out.repository.mapper.InstrutorEntityMapper;
import br.com.fiap3espv.AutoEscola3ESPV.application.core.domain.Instrucao;
import br.com.fiap3espv.AutoEscola3ESPV.application.core.specification.contract.ValidadorCancelamento;
import br.com.fiap3espv.AutoEscola3ESPV.application.port.out.InstrucaoRepository;
import br.com.fiap3espv.AutoEscola3ESPV.exception.type.InstrucaoNotFoundException;
import br.com.fiap3espv.AutoEscola3ESPV.exception.type.ValidacaoException;
import br.com.fiap3espv.AutoEscola3ESPV.application.core.specification.contract.ValidadorAgendamento;
import br.com.fiap3espv.AutoEscola3ESPV.application.core.domain.Aluno;
import br.com.fiap3espv.AutoEscola3ESPV.exception.type.AlunoNotFoundException;
import br.com.fiap3espv.AutoEscola3ESPV.application.port.out.AlunoRepository;
import br.com.fiap3espv.AutoEscola3ESPV.application.core.domain.Instrutor;
import br.com.fiap3espv.AutoEscola3ESPV.exception.type.InstrutorNotFoundException;
import br.com.fiap3espv.AutoEscola3ESPV.application.port.out.InstrutorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AgendaDeInstrucoes {
    private final InstrucaoRepository repository;
    private final AlunoRepository alunoRepository;
    private final InstrutorRepository instrutorRepository;
    private final InstrutorEntityMapper instrutorEntityMapper;
    private final List<ValidadorAgendamento> validadoresAgendamento;
    private final List<ValidadorCancelamento> validadoresCancelamento;

    @Transactional
    public DetalhamentoAgendamento agendar(DadosAgendamento dados) {
        if (!alunoRepository.existsById(dados.idAluno())) {
            throw new AlunoNotFoundException("ID do aluno informado não existe!");
        }
        if (dados.idInstrutor() != null && !instrutorRepository.existsById(dados.idInstrutor())) {
            throw new InstrutorNotFoundException("ID do instrutor informado não existe!");
        }
        //Validações
        validadoresAgendamento.forEach(v -> v.validar(dados));

        Aluno aluno = alunoRepository.getReferenceById(dados.idAluno());
        Instrutor instrutor = escolherInstrutor(dados);
        if (instrutor == null) {
            throw new ValidacaoException("Nenhum instrutor disponível para a data/hora informada!");
        }

        Instrucao instrucao = new Instrucao(null, aluno, instrutorEntityMapper.toEntity(instrutor), dados.dataHora());
        Instrucao salvo = repository.save(instrucao);
        return new DetalhamentoAgendamento(salvo);
    }

    @Transactional
    public void cancelar(DadosCancelamento dados) {
        Instrucao instrucao = buscarInstrucao(dados.idInstrucao());

        //Validações
        validadoresCancelamento.forEach(v -> v.validar(instrucao));

        instrucao.cancelar(dados.motivo());
    }

    @Transactional(readOnly = true)
    public Page<DetalhamentoAgendamento> listar(Pageable pageable) {
        return repository
                .findAllByMotivoCancelamentoIsNull(pageable)
                .map(DetalhamentoAgendamento::new);
    }

    @Transactional(readOnly = true)
    public DetalhamentoAgendamento detalhar(Long id) {
        return new DetalhamentoAgendamento(buscarInstrucao(id));
    }

    private Instrucao buscarInstrucao(Long id) {
        return repository
                .findById(id)
                .orElseThrow(() ->
                        new InstrucaoNotFoundException("ID da instrução informada não existe!"));
    }

    private Instrutor escolherInstrutor(DadosAgendamento dados) {
        if (dados.idInstrutor() != null) {
            return instrutorRepository
                    .findById(dados.idInstrutor())
                    .orElseThrow(() ->
                            new InstrutorNotFoundException("ID do instrutor informado não existe!"));
        }
        if (dados.especialidade() == null) {
            throw new ValidacaoException("Especialidade é obrigatória, caso o instrutor não seja informado!");
        }
        return instrutorRepository.escolherInstrutorAleatorioDisponivel(dados.especialidade(), dados.dataHora());
    }
}
