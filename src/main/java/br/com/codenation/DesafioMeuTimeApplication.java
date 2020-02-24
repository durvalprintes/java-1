package br.com.codenation;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import br.com.codenation.desafio.annotation.Desafio;
import br.com.codenation.desafio.app.MeuTimeInterface;
import br.com.codenation.desafio.exceptions.IdentificadorUtilizadoException;
import br.com.codenation.desafio.exceptions.TimeNaoEncontradoException;
import br.com.codenation.desafio.exceptions.JogadorNaoEncontradoException;
import br.com.codenation.desafio.exceptions.CapitaoNaoInformadoException;
import br.com.codenation.futebol.Jogador;
import br.com.codenation.futebol.Time;

public class DesafioMeuTimeApplication implements MeuTimeInterface {
    private List<Time> times = new ArrayList<>();
    private List<Jogador> jogadores = new ArrayList<>();

    private Optional <Time> buscarTime(final Long id) {
        return times.stream().filter(time -> time.getId().equals(id)).findFirst();
    }

    private Optional<Jogador> buscarJogador(final Long id) {
        return jogadores.stream().filter(jogador -> jogador.getId().equals(id)).findFirst();
    }

    @Desafio("incluirTime")
    public void incluirTime(final Long id, final String nome, final LocalDate dataCriacao,
                            final String corUniformePrincipal, final String corUniformeSecundario) {
        if (buscarTime(id).isPresent()) throw new IdentificadorUtilizadoException("Identificador existente!");
        times.add(new Time(id, nome, dataCriacao, corUniformePrincipal, corUniformeSecundario));

    }

    @Desafio("incluirJogador")
    public void incluirJogador(final Long id, final Long idTime, final String nome, final LocalDate dataNascimento,
                               final Integer nivelHabilidade, final BigDecimal salario) {
        if (buscarJogador(id).isPresent()) throw new IdentificadorUtilizadoException("Identificador existente!");
        buscarTime(idTime).orElseThrow(() -> new TimeNaoEncontradoException("Time inexistente!"));
        jogadores.add(new Jogador(id, idTime, nome, dataNascimento, nivelHabilidade, salario));
    }

    @Desafio("definirCapitao")
    public void definirCapitao(final Long idJogador) {
        Jogador capitao = buscarJogador(idJogador)
                .orElseThrow(() -> new JogadorNaoEncontradoException("Jogador inexistente!"));
        buscarTime(capitao.getIdTime()).get().setIdCapitao(idJogador);
    }

    @Desafio("buscarCapitaoDoTime")
    public Long buscarCapitaoDoTime(final Long idTime) {
        Time time = buscarTime(idTime).orElseThrow(() -> new TimeNaoEncontradoException("Time inexistente!"));
        if (time.getIdCapitao() == null) throw new CapitaoNaoInformadoException("Time sem Capitao informado!");
        return time.getIdCapitao();
    }

    @Desafio("buscarNomeJogador")
    public String buscarNomeJogador(final Long idJogador) {
        return buscarJogador(idJogador).map(Jogador::getNome)
                .orElseThrow(() -> new JogadorNaoEncontradoException("Jogador inexistente!"));
    }

    @Desafio("buscarNomeTime")
    public String buscarNomeTime(final Long idTime) {
        return buscarTime(idTime).map(Time::getNome)
                .orElseThrow(() -> new TimeNaoEncontradoException("Time inexistente!"));
    }

    @Desafio("buscarJogadoresDoTime")
    public List<Long> buscarJogadoresDoTime(final Long idTime) {
        buscarTime(idTime).orElseThrow(() -> new TimeNaoEncontradoException("Time inexistente!"));
        return jogadores.stream()
                .filter(jogador -> jogador.getIdTime().equals(idTime))
                .sorted(Comparator.comparing(Jogador::getId))
                .map(Jogador::getId).collect(Collectors.toList());
    }

    @Desafio("buscarMelhorJogadorDoTime")
    public Long buscarMelhorJogadorDoTime(final Long idTime) {
        buscarTime(idTime).orElseThrow(() -> new TimeNaoEncontradoException("Time inexistente!"));
        return jogadores.stream().filter(jogador -> jogador.getIdTime().equals(idTime))
                .min(Comparator.comparing(Jogador::getNivelHabilidade).reversed()
                        .thenComparing(Comparator.comparing(Jogador::getId))).get().getId();
    }

    @Desafio("buscarJogadorMaisVelho")
    public Long buscarJogadorMaisVelho(final Long idTime) {
        buscarTime(idTime).orElseThrow(() -> new TimeNaoEncontradoException("Time inexistente!"));
        return jogadores.stream().filter(jogador -> jogador.getIdTime().equals(idTime))
                .min(Comparator.comparing(Jogador::getDataNascimento)
                        .thenComparing(Comparator.comparing(Jogador::getId))).get().getId();
    }

    @Desafio("buscarTimes")
    public List<Long> buscarTimes() {
        return times.stream().sorted(Comparator.comparing(Time::getId)).map(Time::getId).collect(Collectors.toList());
    }

    @Desafio("buscarJogadorMaiorSalario")
    public Long buscarJogadorMaiorSalario(final Long idTime) {
        buscarTime(idTime).orElseThrow(() -> new TimeNaoEncontradoException("Time inexistente!"));
        return jogadores.stream().filter(jogador -> jogador.getIdTime().equals(idTime))
                .max(Comparator.comparing(Jogador::getSalario)).get().getId();
    }

    @Desafio("buscarSalarioDoJogador")
    public BigDecimal buscarSalarioDoJogador(final Long idJogador) {
        return buscarJogador(idJogador).map(Jogador::getSalario)
                .orElseThrow(() -> new JogadorNaoEncontradoException("Jogador inexistente!"));
    }

    @Desafio("buscarTopJogadores")
    public List<Long> buscarTopJogadores(final Integer top) {
        return jogadores.stream()
                .sorted(Comparator.comparing(Jogador::getNivelHabilidade).reversed().thenComparing(Jogador::getId))
                .map(Jogador::getId).limit(top).collect(Collectors.toList());
    }

    @Desafio("buscarCorCamisaTimeDeFora")
    public String buscarCorCamisaTimeDeFora(final Long timeDaCasa, final Long timeDeFora) {
        Optional<Time> timeMandante = buscarTime(timeDaCasa), timeVisitante = buscarTime(timeDeFora);
        if (!timeMandante.isPresent() || !timeVisitante.isPresent())
            throw new TimeNaoEncontradoException("Time inexistente!");
        if (timeMandante.get().getCorUniformePrincipal().equals(timeVisitante.get().getCorUniformePrincipal()))
            return timeVisitante.get().getCorUniformeSecundario();
        return timeVisitante.get().getCorUniformePrincipal();
    }
}
