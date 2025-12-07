package pt.iscte.poo.game;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

public class ScoreGame {

    // Certifica-se que esta pasta existe na raiz do projeto
    private final String SCORE_GAME_FOLDER = "scoresGame"; 
    private final String SCORE_FILE = SCORE_GAME_FOLDER + "/topScore.txt";
    private final int MAX_PLAYERS_TOP_SCORE = 10;

    public ScoreGame() {
        // Garante que a diretoria existe
        File dir = new File(SCORE_GAME_FOLDER);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }

    // Escreve a lista completa no ficheiro
    public void writeScores(List<ScorePlayer> scores) {
        File file = new File(SCORE_FILE);
        try (PrintWriter pw = new PrintWriter(file)) {
            pw.println("Top 10 players:");
            for (ScorePlayer s : scores) {
                // Formato: Player : Nome, Time : 100, Moves : 50
                pw.println("Player : " + s.getPlayerName() + ", Time : " + s.getTime() + ", Moves : " + s.getMoves());
            }
        } catch (FileNotFoundException e) {
            System.err.println("Erro ao escrever ficheiro de scores: " + e.getMessage());
        }
    }

    // Lê o ficheiro e converte para objetos ScorePlayer
    public List<ScorePlayer> readScores() {
        List<ScorePlayer> scores = new ArrayList<>();
        File file = new File(SCORE_FILE);
        
        if (!file.exists()) return scores; // Se não existe ficheiro, devolve lista vazia

        try (Scanner sc = new Scanner(file)) {
            if (sc.hasNextLine()) sc.nextLine(); // Ignora o cabeçalho "Top 10 players:"
            
            while (sc.hasNextLine()) {
                String line = sc.nextLine();
                
                if (line.contains("Player :") && line.contains("Time :") && line.contains("Moves :")) {
                    try {
                        String[] parts = line.split(","); // Divide pelas vírgulas
                        
                        
                        String name = parts[0].substring(parts[0].indexOf(":") + 1).trim();
                        
                       
                        String timeStr = parts[1].substring(parts[1].indexOf(":") + 1).trim();
                        int time = Integer.parseInt(timeStr);
                        
                        
                        String movesStr = parts[2].substring(parts[2].indexOf(":") + 1).trim();
                        int moves = Integer.parseInt(movesStr);

                        scores.add(new ScorePlayer(name, time, moves));
                    } catch (Exception e) {
                        System.err.println("Linha mal formatada ignorada: " + line);
                    }
                }
            }
        } catch (FileNotFoundException e) {
            System.err.println("Ficheiro não encontrado.");
        }
        return scores;
    }

    // Adiciona ou atualiza um score
    public void updateTopScores(ScorePlayer newScorePlayer) {
        List<ScorePlayer> scores = readScores();
        boolean playerFound = false;

        // Tenta encontrar o jogador para atualizar se melhorou
        for (int i = 0; i < scores.size(); i++) {
            ScorePlayer current = scores.get(i);
            
            if (current.getPlayerName().equals(newScorePlayer.getPlayerName())) {
                playerFound = true;
                // Usa o compareTo para ver se o novo score é melhor (menor é melhor)
                // Se newScorePlayer < current, significa que é melhor (compareTo retorna negativo)
                if (newScorePlayer.compareTo(current) < 0) {
                    scores.set(i, newScorePlayer); // Substitui pelo novo recorde
                } else {
                    return; // O jogador já tem um score melhor registado, não fazemos nada
                }
                break;
            }
        }

        // Se não encontrou o jogador, adiciona como novo
        if (!playerFound) {
            scores.add(newScorePlayer);
        }

        // Ordena a lista (usa o compareTo do ScorePlayer: Tempo -> Movimentos)
        Collections.sort(scores);

        // Corta se tiver mais que 10
        if (scores.size() > MAX_PLAYERS_TOP_SCORE) {
            scores = scores.subList(0, MAX_PLAYERS_TOP_SCORE);
        }

        writeScores(scores);
    }

    // Devolve uma String pronta para mostrar no JOptionPane
    public String getFormattedHighScores() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== TOP 10 HIGHSCORES ===\n\n");
        List<ScorePlayer> scores = readScores();
        
        int rank = 1;
        for (ScorePlayer s : scores) {
            sb.append(rank++).append(". ")
              .append(s.getPlayerName())
              .append(" - Tempo: ").append(s.getTime()).append("s")
              .append(", Movimentos: ").append(s.getMoves())
              .append("\n");
        }
        
        if (scores.isEmpty()) {
            sb.append("Ainda não há registos.");
        }
        
        return sb.toString();
    }
}