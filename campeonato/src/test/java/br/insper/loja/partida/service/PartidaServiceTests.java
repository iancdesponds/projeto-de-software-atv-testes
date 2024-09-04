package br.insper.loja.partida.service;

import br.insper.loja.partida.dto.EditarPartidaDTO;
import br.insper.loja.partida.dto.RetornarPartidaDTO;
import br.insper.loja.partida.dto.SalvarPartidaDTO;
import br.insper.loja.partida.exception.PartidaNaoEncontradaException;
import br.insper.loja.partida.model.Partida;
import br.insper.loja.partida.repository.PartidaRepository;
import br.insper.loja.time.model.Time;
import br.insper.loja.time.service.TimeService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class PartidaServiceTests {

    @InjectMocks
    private PartidaService partidaService;

    @Mock
    private PartidaRepository partidaRepository;

    @Mock
    private TimeService timeService;

    @Test
    public void testCadastrarPartida() {
        // Preparação
        SalvarPartidaDTO salvarPartidaDTO = new SalvarPartidaDTO();
        salvarPartidaDTO.setMandante(1);
        salvarPartidaDTO.setVisitante(2);

        Time mandante = new Time();
        mandante.setNome("Time 1");
        mandante.setIdentificador("time-1");

        Time visitante = new Time();
        visitante.setNome("Time 2");
        visitante.setIdentificador("time-2");

        Partida partida = new Partida();
        partida.setMandante(mandante);
        partida.setVisitante(visitante);

        Mockito.when(timeService.getTime(1)).thenReturn(mandante);
        Mockito.when(timeService.getTime(2)).thenReturn(visitante);
        Mockito.when(partidaRepository.save(Mockito.any(Partida.class))).thenReturn(partida);

        // Execução
        RetornarPartidaDTO resultado = partidaService.cadastrarPartida(salvarPartidaDTO);

        // Verificação
        Assertions.assertNotNull(resultado);
        Assertions.assertEquals("Time 1", resultado.getNomeMandante());
        Assertions.assertEquals("Time 2", resultado.getNomeVisitante());
        Mockito.verify(partidaRepository).save(Mockito.any(Partida.class));
    }

    @Test
    public void testListarPartidasQuandoMandanteNaoEhNulo() {
        // Preparação
        Time mandante = new Time();
        mandante.setNome("Time 1");
        mandante.setIdentificador("time-1");

        Time visitante = new Time();
        visitante.setNome("Time 2");

        Partida partida = new Partida();
        partida.setMandante(mandante);
        partida.setVisitante(visitante);

        List<Partida> partidas = new ArrayList<>();
        partidas.add(partida);

        Mockito.when(partidaRepository.findAll()).thenReturn(partidas);

        // Execução
        List<RetornarPartidaDTO> resultado = partidaService.listarPartidas("time-1");

        // Verificação
        Assertions.assertEquals(1, resultado.size());
        Assertions.assertEquals("Time 1", resultado.get(0).getNomeMandante());
        Assertions.assertEquals("Time 2", resultado.get(0).getNomeVisitante());
    }

    @Test
    public void testListarPartidasQuandoMandanteEhNulo() {
        // Preparação
        Time mandante = new Time();
        mandante.setNome("Time 1");

        Time visitante = new Time();
        visitante.setNome("Time 2");

        Partida partida = new Partida();
        partida.setMandante(mandante);
        partida.setVisitante(visitante);

        List<Partida> partidas = new ArrayList<>();
        partidas.add(partida);

        Mockito.when(partidaRepository.findAll()).thenReturn(partidas);

        // Execução
        List<RetornarPartidaDTO> resultado = partidaService.listarPartidas(null);

        // Verificação
        Assertions.assertEquals(1, resultado.size());
        Assertions.assertEquals("Time 1", resultado.get(0).getNomeMandante());
        Assertions.assertEquals("Time 2", resultado.get(0).getNomeVisitante());
    }

    @Test
    public void testEditarPartida() {
        // Preparação
        EditarPartidaDTO editarPartidaDTO = new EditarPartidaDTO();
        editarPartidaDTO.setPlacarMandante(2);
        editarPartidaDTO.setPlacarVisitante(3);

        Time mandante = new Time();
        mandante.setNome("Time 1");
        Time visitante = new Time();
        visitante.setNome("Time 2");

        Partida partida = new Partida();
        partida.setMandante(mandante);
        partida.setVisitante(visitante);

        Mockito.when(partidaRepository.findById(1)).thenReturn(Optional.of(partida));
        Mockito.when(partidaRepository.save(Mockito.any(Partida.class))).thenReturn(partida);

        // Execução
        RetornarPartidaDTO resultado = partidaService.editarPartida(editarPartidaDTO, 1);

        // Verificação
        Assertions.assertNotNull(resultado);
        Assertions.assertEquals(2, resultado.getPlacarMandante());
        Assertions.assertEquals(3, resultado.getPlacarVisitante());
        Assertions.assertEquals("Time 1", resultado.getNomeMandante());
        Assertions.assertEquals("Time 2", resultado.getNomeVisitante());
    }

    @Test
    public void testGetPartidaQuandoPartidaEhEncontrada() {
        // Preparação
        Time mandante = new Time();
        mandante.setNome("Time 1");

        Time visitante = new Time();
        visitante.setNome("Time 2");

        Partida partida = new Partida();
        partida.setMandante(mandante);
        partida.setVisitante(visitante);

        Mockito.when(partidaRepository.findById(1)).thenReturn(Optional.of(partida));

        // Execução
        RetornarPartidaDTO resultado = partidaService.getPartida(1);

        // Verificação
        Assertions.assertNotNull(resultado);
        Assertions.assertEquals("Time 1", resultado.getNomeMandante());
        Assertions.assertEquals("Time 2", resultado.getNomeVisitante());
    }

    @Test
    public void testGetPartidaQuandoPartidaNaoEhEncontrada() {
        // Preparação
        Mockito.when(partidaRepository.findById(1)).thenReturn(Optional.empty());

        // Verificação
        Assertions.assertThrows(PartidaNaoEncontradaException.class, () -> partidaService.getPartida(1));
    }
}
