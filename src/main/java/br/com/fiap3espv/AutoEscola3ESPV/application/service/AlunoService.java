package br.com.fiap3espv.AutoEscola3ESPV.application.service;

import br.com.fiap3espv.AutoEscola3ESPV.adapter.in.controller.request.aluno.DadosAtualizacaoAluno;
import br.com.fiap3espv.AutoEscola3ESPV.adapter.in.controller.request.aluno.DadosCadastroAluno;
import br.com.fiap3espv.AutoEscola3ESPV.adapter.in.controller.response.aluno.DadosDetalhamentoAluno;
import br.com.fiap3espv.AutoEscola3ESPV.adapter.in.controller.response.aluno.DadosListagemAlunos;
import br.com.fiap3espv.AutoEscola3ESPV.application.core.domain.Aluno;
import br.com.fiap3espv.AutoEscola3ESPV.application.port.out.AlunoRepository;
import br.com.fiap3espv.AutoEscola3ESPV.exception.type.AlunoNotFoundException;
import br.com.fiap3espv.AutoEscola3ESPV.shared.vo.endereco.mapper.EnderecoMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AlunoService {
    private final AlunoRepository repository;
    private final EnderecoMapper enderecoMapper;

    @Transactional
    public DadosDetalhamentoAluno cadastrarAluno(DadosCadastroAluno dados) {
        Aluno aluno = new Aluno(
                null,
                dados.nome(),
                dados.telefone(),
                dados.email(),
                dados.cpf(),
                enderecoMapper.toVo(dados.endereco()),
                true
        );
        return new DadosDetalhamentoAluno(repository.save(aluno));
    }

    @Transactional(readOnly = true)
    public Page<DadosListagemAlunos> listarAlunos(Pageable pageable) {
        return repository
                .findAllByAtivoTrue(pageable)
                .map(DadosListagemAlunos::new);
    }

    @Transactional(readOnly = true)
    public DadosDetalhamentoAluno detalharAluno(Long id) {
        return new DadosDetalhamentoAluno(buscarAluno(id));
    }

    @Transactional
    public DadosDetalhamentoAluno atualizarAluno(DadosAtualizacaoAluno dados) {
        Aluno aluno = buscarAluno(dados.id());
        aluno.atualizarInformacoes(
                dados.nome(),
                dados.telefone(),
                dados.email(),
                dados.endereco() != null ? enderecoMapper.toVo(dados.endereco()) : null
        );
        return new DadosDetalhamentoAluno(aluno);
    }

    @Transactional
    public void excluirAluno(Long id) {
        buscarAluno(id).excluir();
    }

    private Aluno buscarAluno(Long id) {
        return repository
                .findById(id)
                .orElseThrow(() ->
                        new AlunoNotFoundException("ID do aluno informado não existe!"));
    }
}
