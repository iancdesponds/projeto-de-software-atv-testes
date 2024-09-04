package br.insper.loja.time.service;

import br.insper.loja.time.exception.TimeNaoEncontradoException;
import br.insper.loja.time.model.Time;
import br.insper.loja.time.repository.TimeRepository;
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
public class TimeServiceTests {

    @InjectMocks
    private TimeService timeService;

    @Mock
    private TimeRepository timeRepository;

    @Test
    public void testCadastrarTimeComSucesso() {
        // Preparação
        Time time = new Time();
        time.setNome("Time A");
        time.setIdentificador("time-a");

        Mockito.when(timeRepository.save(time)).thenReturn(time);

        // Execução
        Time timeSalvo = timeService.cadastrarTime(time);

        // Verificação
        Assertions.assertNotNull(timeSalvo);
        Assertions.assertEquals("Time A", timeSalvo.getNome());
        Assertions.assertEquals("time-a", timeSalvo.getIdentificador());
    }

    @Test
    public void testCadastrarTimeComNomeVazio() {
        // Preparação
        Time time = new Time();
        time.setNome("");
        time.setIdentificador("time-a");

        // Execução e Verificação
        Assertions.assertThrows(RuntimeException.class, () -> timeService.cadastrarTime(time));
    }

    @Test
    public void testCadastrarTimeComIdentificadorVazio() {
        // Preparação
        Time time = new Time();
        time.setNome("Time A");
        time.setIdentificador("");

        // Execução e Verificação
        Assertions.assertThrows(RuntimeException.class, () -> timeService.cadastrarTime(time));
    }

    @Test
    public void testListarTimesComEstadoVazio() {
        // Preparação
        List<Time> lista = new ArrayList<>();

        Time time = new Time();
        time.setEstado("");  // Estado vazio
        time.setIdentificador("time-1");
        lista.add(time);

        Mockito.when(timeRepository.findByEstado("")).thenReturn(lista);

        // Execução
        List<Time> times = timeService.listarTimes("");

        // Verificação
        Assertions.assertFalse(times.isEmpty());  // Verifica que a lista não está vazia
        Assertions.assertEquals(1, times.size());  // Verifica que há 1 elemento na lista
        Assertions.assertEquals("", times.get(0).getEstado());  // Verifica que o estado é realmente vazio
        Assertions.assertEquals("time-1", times.get(0).getIdentificador());  // Verifica o identificador
    }

    @Test
    public void testGetTimeWhenTimeIsNotFound() {
        // Preparação
        Mockito.when(timeRepository.findById(1)).thenReturn(Optional.empty());

        // Verificação
        Assertions.assertThrows(TimeNaoEncontradoException.class, () -> timeService.getTime(1));
    }

    @Test
    public void testGetTimeWhenTimeIsPresent() {
        // Preparação
        Time time = new Time();
        time.setEstado("SP");
        time.setIdentificador("time-1");

        Mockito.when(timeRepository.findById(1)).thenReturn(Optional.of(time));

        // Execução
        Time timeRetorno = timeService.getTime(1);

        // Verificação
        Assertions.assertNotNull(timeRetorno);
        Assertions.assertEquals("SP", timeRetorno.getEstado());
        Assertions.assertEquals("time-1", timeRetorno.getIdentificador());
    }

    // Testar quando o estado não é null
    @Test
    public void testListarTimesWhenEstadoIsNotNull() {
        // Preparação
        List<Time> lista = new ArrayList<>();

        Time time = new Time();
        time.setEstado("SP");
        time.setIdentificador("time-1");
        lista.add(time);

        // Quando o estado não é nulo, deve retornar a lista filtrada pelo estado
        Mockito.when(timeRepository.findByEstado("SP")).thenReturn(lista);

        // Execução
        List<Time> times = timeService.listarTimes("SP");

        // Verificação
        Assertions.assertFalse(times.isEmpty());
        Assertions.assertEquals(1, times.size());
        Assertions.assertEquals("SP", times.get(0).getEstado());
        Assertions.assertEquals("time-1", times.get(0).getIdentificador());
    }

    // Testar quando o estado é null e deve retornar todos os times
    @Test
    public void testListarTimesWhenEstadoIsNull() {
        // Preparação
        List<Time> lista = new ArrayList<>();

        Time time1 = new Time();
        time1.setEstado("SP");
        time1.setIdentificador("time-1");

        Time time2 = new Time();
        time2.setEstado("RJ");
        time2.setIdentificador("time-2");

        lista.add(time1);
        lista.add(time2);

        // Quando o estado é null, deve retornar todos os times
        Mockito.when(timeRepository.findAll()).thenReturn(lista);

        // Execução
        List<Time> times = timeService.listarTimes(null);

        // Verificação
        Assertions.assertFalse(times.isEmpty());
        Assertions.assertEquals(2, times.size());
        Assertions.assertEquals("SP", times.get(0).getEstado());
        Assertions.assertEquals("RJ", times.get(1).getEstado());
    }

    @Test
    public void testListarTimesWhenEstadoIsNotNullButEmpty() {
        // Preparação
        List<Time> lista = new ArrayList<>();

        // Quando o estado não é nulo, mas não há times para o estado
        Mockito.when(timeRepository.findByEstado("RJ")).thenReturn(lista);

        // Execução
        List<Time> times = timeService.listarTimes("RJ");

        // Verificação
        Assertions.assertTrue(times.isEmpty());  // Espera-se que a lista seja vazia
    }
}
