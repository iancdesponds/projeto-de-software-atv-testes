package br.insper.aposta.aposta;

import br.insper.aposta.partida.PartidaNaoEncontradaException;
import br.insper.aposta.partida.PartidaNaoRealizadaException;
import br.insper.aposta.partida.PartidaService;
import br.insper.aposta.partida.RetornarPartidaDTO;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Optional;
import java.util.UUID;
import java.util.List;
import java.util.ArrayList;

@ExtendWith(MockitoExtension.class)
public class ApostaServiceTests {

    @InjectMocks
    ApostaService apostaService;

    @Mock
    ApostaRepository apostaRepository;

    @Mock
    PartidaService partidaService;

    private Aposta prepararAposta(String status, String resultado, Integer idPartida) {
        Aposta aposta = new Aposta();
        aposta.setId(UUID.randomUUID().toString());
        aposta.setStatus(status);
        aposta.setResultado(resultado);
        aposta.setIdPartida(idPartida);
        return aposta;
    }

    private RetornarPartidaDTO prepararPartidaDTO(String status, int placarMandante, int placarVisitante) {
        RetornarPartidaDTO partidaDTO = new RetornarPartidaDTO();
        partidaDTO.setStatus(status);
        partidaDTO.setPlacarMandante(placarMandante);
        partidaDTO.setPlacarVisitante(placarVisitante);
        return partidaDTO;
    }

    @Test
    public void testSalvarApostaComSucesso() {
        Aposta aposta = prepararAposta(null, null, 1);

        RetornarPartidaDTO partidaDTO = prepararPartidaDTO("REALIZADA", 2, 1);

        Mockito.when(partidaService.getPartida(1))
                .thenReturn(new ResponseEntity<>(partidaDTO, HttpStatus.OK));
        Mockito.when(apostaRepository.save(Mockito.any(Aposta.class)))
                .thenReturn(aposta);

        Aposta resultado = apostaService.salvar(aposta);

        Assertions.assertNotNull(resultado);
        Assertions.assertEquals("REALIZADA", resultado.getStatus());
    }

    @Test
    public void testSalvarApostaQuandoPartidaNaoEncontrada() {
        Aposta aposta = prepararAposta(null, null, 1);

        Mockito.when(partidaService.getPartida(1))
                .thenReturn(new ResponseEntity<>(null, HttpStatus.NOT_FOUND));

        Assertions.assertThrows(PartidaNaoEncontradaException.class, () -> apostaService.salvar(aposta));
    }

    @Test
    public void testGetApostaQuandoNaoRealizada() {
        Aposta aposta = prepararAposta("PENDENTE", "VITORIA_MANDANTE", 1);

        Mockito.when(apostaRepository.findById("1"))
                .thenReturn(Optional.of(aposta));

        Aposta resultado = apostaService.getAposta("1");

        Assertions.assertEquals("PENDENTE", resultado.getStatus());
    }

    @Test
    public void testGetApostaQuandoPartidaNaoEncontrada() {
        Aposta aposta = prepararAposta("REALIZADA", "VITORIA_MANDANTE", 1);

        Mockito.when(apostaRepository.findById("1"))
                .thenReturn(Optional.of(aposta));
        Mockito.when(partidaService.getPartida(1))
                .thenReturn(new ResponseEntity<>(null, HttpStatus.NOT_FOUND));

        Assertions.assertThrows(PartidaNaoEncontradaException.class, () -> apostaService.getAposta("1"));
    }

    @Test
    public void testGetApostaQuandoPartidaNaoRealizada() {
        Aposta aposta = prepararAposta("REALIZADA", "VITORIA_MANDANTE", 1);

        RetornarPartidaDTO partidaDTO = prepararPartidaDTO("AGENDADA", 2, 1);

        Mockito.when(apostaRepository.findById("1"))
                .thenReturn(Optional.of(aposta));
        Mockito.when(partidaService.getPartida(1))
                .thenReturn(new ResponseEntity<>(partidaDTO, HttpStatus.OK));

        Assertions.assertThrows(PartidaNaoRealizadaException.class, () -> apostaService.getAposta("1"));
    }

    @Test
    public void testGetApostaQuandoGanhou() {
        Aposta aposta = prepararAposta("REALIZADA", "EMPATE", 1);

        RetornarPartidaDTO partidaDTO = prepararPartidaDTO("REALIZADA", 1, 1);

        Mockito.when(apostaRepository.findById("1"))
                .thenReturn(Optional.of(aposta));
        Mockito.when(partidaService.getPartida(1))
                .thenReturn(new ResponseEntity<>(partidaDTO, HttpStatus.OK));
        Mockito.when(apostaRepository.save(Mockito.any(Aposta.class)))
                .thenReturn(aposta);

        Aposta resultado = apostaService.getAposta("1");

        Assertions.assertEquals("GANHOU", resultado.getStatus());
    }

    @Test
    public void testGetApostaQuandoPerdeu() {
        Aposta aposta = prepararAposta("REALIZADA", "EMPATE", 1);

        RetornarPartidaDTO partidaDTO = prepararPartidaDTO("REALIZADA", 2, 1);

        Mockito.when(apostaRepository.findById("1"))
                .thenReturn(Optional.of(aposta));
        Mockito.when(partidaService.getPartida(1))
                .thenReturn(new ResponseEntity<>(partidaDTO, HttpStatus.OK));
        Mockito.when(apostaRepository.save(Mockito.any(Aposta.class)))
                .thenReturn(aposta);

        Aposta resultado = apostaService.getAposta("1");

        Assertions.assertEquals("PERDEU", resultado.getStatus());
    }

    @Test
    public void testListarApostas() {
        List<Aposta> apostas = new ArrayList<>();
        apostas.add(prepararAposta("REALIZADA", "VITORIA_MANDANTE", 1));
        apostas.add(prepararAposta("REALIZADA", "EMPATE", 2));

        Mockito.when(apostaRepository.findAll()).thenReturn(apostas);

        List<Aposta> resultado = apostaService.listar();

        Assertions.assertEquals(2, resultado.size());
    }

    @Test
    public void testGetApostaQuandoNaoEncontrada() {
        // Preparação: Simulando que a aposta não foi encontrada (Optional.empty)
        Mockito.when(apostaRepository.findById("1"))
                .thenReturn(Optional.empty());

        // Verificação: O teste espera que a exceção ApostaNaoEncontradaException seja lançada
        Assertions.assertThrows(ApostaNaoEncontradaException.class, () -> apostaService.getAposta("1"));
    }

    @Test
    public void testGetApostaQuandoEmpate() {
        // Preparação: Aposta no empate e partida terminou empatada
        Aposta aposta = prepararAposta("REALIZADA", "EMPATE", 1);

        RetornarPartidaDTO partidaDTO = prepararPartidaDTO("REALIZADA", 1, 1); // Empate

        Mockito.when(apostaRepository.findById("1"))
                .thenReturn(Optional.of(aposta));
        Mockito.when(partidaService.getPartida(1))
                .thenReturn(new ResponseEntity<>(partidaDTO, HttpStatus.OK));
        Mockito.when(apostaRepository.save(Mockito.any(Aposta.class)))
                .thenReturn(aposta);

        // Execução
        Aposta resultado = apostaService.getAposta("1");

        // Verificação: A aposta deve ter ganho
        Assertions.assertEquals("GANHOU", resultado.getStatus());
    }

    @Test
    public void testGetApostaQuandoVitoriaMandante() {
        // Preparação: Aposta na vitória do mandante e o mandante venceu
        Aposta aposta = prepararAposta("REALIZADA", "VITORIA_MANDANTE", 1);

        RetornarPartidaDTO partidaDTO = prepararPartidaDTO("REALIZADA", 2, 1); // Mandante venceu

        Mockito.when(apostaRepository.findById("1"))
                .thenReturn(Optional.of(aposta));
        Mockito.when(partidaService.getPartida(1))
                .thenReturn(new ResponseEntity<>(partidaDTO, HttpStatus.OK));
        Mockito.when(apostaRepository.save(Mockito.any(Aposta.class)))
                .thenReturn(aposta);

        // Execução
        Aposta resultado = apostaService.getAposta("1");

        // Verificação: A aposta deve ter ganho
        Assertions.assertEquals("GANHOU", resultado.getStatus());
    }

    @Test
    public void testGetApostaQuandoVitoriaVisitante() {
        // Preparação: Aposta na vitória do visitante e o visitante venceu
        Aposta aposta = prepararAposta("REALIZADA", "VITORIA_VISITANTE", 1);

        RetornarPartidaDTO partidaDTO = prepararPartidaDTO("REALIZADA", 1, 2); // Visitante venceu

        Mockito.when(apostaRepository.findById("1"))
                .thenReturn(Optional.of(aposta));
        Mockito.when(partidaService.getPartida(1))
                .thenReturn(new ResponseEntity<>(partidaDTO, HttpStatus.OK));
        Mockito.when(apostaRepository.save(Mockito.any(Aposta.class)))
                .thenReturn(aposta);

        // Execução
        Aposta resultado = apostaService.getAposta("1");

        // Verificação: A aposta deve ter ganho
        Assertions.assertEquals("GANHOU", resultado.getStatus());
    }

    @Test
    public void testGetApostaQuandoPerdeu2() {
        // Preparação: Aposta no empate, mas o mandante venceu
        Aposta aposta = prepararAposta("REALIZADA", "EMPATE", 1);

        RetornarPartidaDTO partidaDTO = prepararPartidaDTO("REALIZADA", 2, 1); // Mandante venceu

        Mockito.when(apostaRepository.findById("1"))
                .thenReturn(Optional.of(aposta));
        Mockito.when(partidaService.getPartida(1))
                .thenReturn(new ResponseEntity<>(partidaDTO, HttpStatus.OK));
        Mockito.when(apostaRepository.save(Mockito.any(Aposta.class)))
                .thenReturn(aposta);

        // Execução
        Aposta resultado = apostaService.getAposta("1");

        // Verificação: A aposta deve ter perdido
        Assertions.assertEquals("PERDEU", resultado.getStatus());
    }
}
