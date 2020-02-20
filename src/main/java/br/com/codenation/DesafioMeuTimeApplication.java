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
        if (buscarTime(id).isPresent()) {
            throw new IdentificadorUtilizadoException("Identificador existente!");
        } else {
            times.add(new Time(id, nome, dataCriacao, corUniformePrincipal, corUniformeSecundario));
        }
    }

    @Desafio("incluirJogador")
    public void incluirJogador(final Long id, final Long idTime, final String nome, final LocalDate dataNascimento,
                               final Integer nivelHabilidade, final BigDecimal salario) {
        if (buscarJogador(id).isPresent()) {
            throw new IdentificadorUtilizadoException("Identificador existente!");
        } else if (!buscarTime(idTime).isPresent()) {
            throw new TimeNaoEncontradoException("Time inexistente!");
        } else {
            jogadores.add(new Jogador(id, idTime, nome, dataNascimento, nivelHabilidade, salario));
        }
    }

    @Desafio("definirCapitao")
    public void definirCapitao(final Long idJogador) {
        Optional<Jogador> capitao = buscarJogador(idJogador);
        if (!capitao.isPresent()) {
            throw new JogadorNaoEncontradoException("Jogador inexistente!");
        } else {
            times.stream().filter(time -> time.getId().equals(capitao.get().getIdTime()))
                    .forEach(time -> time.setIdCapitao(capitao.get().getId()));
        }
    }

    @Desafio("buscarCapitaoDoTime")
    public Long buscarCapitaoDoTime(final Long idTime) {
        Optional<Time> time = buscarTime(idTime);
        if (!time.isPresent()) {
            throw new TimeNaoEncontradoException("Time inexistente!");
        } else if (time.get().getIdCapitao() == null) {
            throw new CapitaoNaoInformadoException("Time sem Capitao informado!");
        } else {
            return time.get().getIdCapitao();
        }
    }

    @Desafio("buscarNomeJogador")
    public String buscarNomeJogador(final Long idJogador) {
        Optional<Jogador> jogador = buscarJogador(idJogador);
        if (!jogador.isPresent()) {
            throw new JogadorNaoEncontradoException("Jogador inexistente!");
        } else {
            return jogador.get().getNome();
        }
    }

    @Desafio("buscarNomeTime")
    public String buscarNomeTime(final Long idTime) {
        Optional<Time> time = buscarTime(idTime);
        if (!time.isPresent()) {
            throw new TimeNaoEncontradoException("Time inexistente!");
        } else {
            return time.get().getNome();
        }
    }

    @Desafio("buscarJogadoresDoTime")
    public List<Long> buscarJogadoresDoTime(final Long idTime) {
        if (!buscarTime(idTime).isPresent()) {
            throw new TimeNaoEncontradoException("Time inexistente!");
        } else {
            return jogadores.stream()
                    .filter(jogador -> jogador.getIdTime().equals(idTime))
                    .sorted(Comparator.comparing(Jogador::getId))
                    .map(Jogador::getId).collect(Collectors.toList());
        }
    }

    @Desafio("buscarMelhorJogadorDoTime")
    public Long buscarMelhorJogadorDoTime(final Long idTime) {
        if (!buscarTime(idTime).isPresent()) {
            throw new TimeNaoEncontradoException("Time inexistente!");
        } else {
            return jogadores.stream().filter(jogador -> jogador.getIdTime().equals(idTime))
                    .min(Comparator.comparing(Jogador::getNivelHabilidade).reversed()
                            .thenComparing(Comparator.comparing(Jogador::getId))).get().getId();
        }
    }

    @Desafio("buscarJogadorMaisVelho")
    public Long buscarJogadorMaisVelho(final Long idTime) {
        if (!buscarTime(idTime).isPresent()) {
            throw new TimeNaoEncontradoException("Time inexistente!");
        } else {
            return jogadores.stream().filter(jogador -> jogador.getIdTime().equals(idTime))
                    .min(Comparator.comparing(Jogador::getDataNascimento)
                            .thenComparing(Comparator.comparing(Jogador::getId))).get().getId();
        }
    }

    @Desafio("buscarTimes")
    public List<Long> buscarTimes() {
        return times.stream().sorted(Comparator.comparing(Time::getId)).map(Time::getId).collect(Collectors.toList());
    }

    @Desafio("buscarJogadorMaiorSalario")
    public Long buscarJogadorMaiorSalario(final Long idTime) {
        if (!buscarTime(idTime).isPresent()) {
            throw new TimeNaoEncontradoException("Time inexistente!");
        } else {
            return jogadores.stream().filter(jogador -> jogador.getIdTime().equals(idTime))
                    .max(Comparator.comparing(Jogador::getSalario)).get().getId();
        }
    }

    @Desafio("buscarSalarioDoJogador")
    public BigDecimal buscarSalarioDoJogador(final Long idJogador) {
        Optional<Jogador> jogador = buscarJogador(idJogador);
        if (!jogador.isPresent()) {
            throw new JogadorNaoEncontradoException("Jogador inexistente!");
        } else {
            return jogador.get().getSalario();
        }
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
        if (!timeMandante.isPresent() || !timeVisitante.isPresent()) {
            throw new TimeNaoEncontradoException("Time inexistente!");
        } else if (timeMandante.get().getCorUniformePrincipal().equals(timeVisitante.get().getCorUniformePrincipal())) {
            return timeVisitante.get().getCorUniformeSecundario();
        }
        return timeVisitante.get().getCorUniformePrincipal();
    }
}
