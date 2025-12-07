package pt.iscte.poo.game;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import interfaces.*;

import java.awt.Image;
import java.awt.Point;
import java.awt.event.KeyEvent;

import objects.*;
import pt.iscte.poo.gui.ImageGUI;
import pt.iscte.poo.observer.Observed;
import pt.iscte.poo.observer.Observer;
import pt.iscte.poo.utils.Direction;
import pt.iscte.poo.utils.Point2D;
import pt.iscte.poo.utils.Vector2D;


public class GameEngine implements Observer {
	
	private Map<String,Room> rooms;
	private Room currentRoom;
	private int lastTickProcessed = 0;
	private boolean smallSelected = true;
	private ScoreGame scoreGame = new ScoreGame();
	
	private String playerName;      
	private long startTime;        
	private int currentMoves = 0;   
	
	public GameEngine() {
		rooms = new HashMap<String,Room>();
		loadGame();
		if (!rooms.containsKey("room0.txt")) {
			ImageGUI.getInstance().showMessage("Erro Fatal", "Não foi possível carregar o nível inicial (room0.txt).\nVerifique a consola para detalhes.\nO jogo será encerrado.");
			System.exit(1); // Fecha a aplicação imediatamente
		}
		
		this.playerName = ImageGUI.getInstance().askUser("ENTER NAME:");
		startTime = System.currentTimeMillis();
		currentRoom = rooms.get("room0.txt");

		SmallFish.getInstance().setRoom(currentRoom);
		BigFish.getInstance().setRoom(currentRoom);

		SmallFish.getInstance().setPosition(currentRoom.getSmallFishStartingPosition());
		BigFish.getInstance().setPosition(currentRoom.getBigFishStartingPosition());
	
		updateGUI();	
	}
	
	private void updateStatusMessage() {
        // Verifica qual o peixe selecionado para mostrar na barra
        String peixeAtual = smallSelected ? "Eleven" : "Hopper";
        String nivelBonito = currentRoom.getName().replace("room", "Level ").replace(".txt", "");

     // Calcula o tempo decorrido em segundos reais desde que o jogo começou
        long tempoAtual = System.currentTimeMillis();
        long segundosDecorridos = (tempoAtual - startTime) / 1000;

        ImageGUI.getInstance().setStatusMessage(
            "PLAYER: " + playerName + 
            " | " + nivelBonito +
            " | MOVES: " + currentMoves  + 
            " | TIME: " + segundosDecorridos +
            " | SELECTED: " + peixeAtual // Mostra aqui quem controlas
        );
    }


	private void loadGame() {
        File[] files = new File("./rooms").listFiles();
        // String para acumular mensagens de erro
        StringBuilder errorLog = new StringBuilder(); 

        for(File f : files) {
            try {
                rooms.put(f.getName(), Room.readRoom(f, this));
            } catch (InvalidFileException e) {
                // Se falhar, adicionamos à lista de erros mas continuamos a tentar ler os outros
                String erro = " - " + f.getName() + ": " + e.getMessage() + "\n";
                System.err.println(erro);
                errorLog.append(erro);
            }
        }

        // Se houve erros, mostramos um popup ao utilizador
        if (errorLog.length() > 0) {
            ImageGUI.getInstance().showMessage("Aviso: Níveis Ignorados", 
                "Os seguintes ficheiros contêm erros e não foram carregados:\n" + errorLog.toString());
        }
    }

	@Override
	public void update(Observed source) {
		//sempre a ver se o jogo acabou
		if(checkWin()){
			nextLevel();
			return;
		}

		if (ImageGUI.getInstance().wasKeyPressed()) {
			int k = ImageGUI.getInstance().keyPressed();

			if (Direction.isDirection(k)) {
				//contagem de moves
				currentMoves++; 
				
				Direction dir = Direction.directionFor(k);

				if (smallSelected) {
					if (currentRoom.getObjects().contains(SmallFish.getInstance())) {
						SmallFish.getInstance().move(dir);
					}
				} else {
					if (currentRoom.getObjects().contains(BigFish.getInstance())) {
						BigFish.getInstance().move(dir);
					}
				}
				
				// Move os caranguejos
				for (GameObject obj : new ArrayList<>(currentRoom.getObjects())) {
        			if (obj instanceof Crab) {
            			((Crab) obj).moveRandom();
        			}	
    			}
			}
			else if (k == KeyEvent.VK_SPACE) {
				boolean targetSelection = !smallSelected;

				if (targetSelection) {
					// Quer mudar para o grande
					if (currentRoom.getObjects().contains(SmallFish.getInstance())) {
						smallSelected = true;
					}
				} else {
					// Quer mudar para o grande
					if (currentRoom.getObjects().contains(BigFish.getInstance())) {
						smallSelected = false;
					}
				}
			}
			else if(k == KeyEvent.VK_R){
				ImageGUI.getInstance().showMessage("Nível reiniciado", "Tecla 'r' pressionada");
				restartGame();
			}
		}

		// Processa a física do jogo (tempo/gravidade)
		int t = ImageGUI.getInstance().getTicks();
		while (lastTickProcessed < t) {
			processTick();
		}

		// atualiza a mensagem de status
		updateStatusMessage();
		
		ImageGUI.getInstance().update();
	}

	private void processTick() {
		
		//iteramos por uma copia dos objetos para evitar erros 
		for(GameObject obj : new ArrayList<>(currentRoom.getObjects())){
			if(obj instanceof GravityAffected){
				applyGravity((GravityAffected) obj);
			}
			if (obj instanceof Floatable) {
            	applyBuoyancy((Floatable) obj); 
        	}
		}
		checkSmallFishCrush();
		checkBigFishCrush();
		checkTraps();
		checkTrunkCrush();
		checkCrabCollisions();
		checkNumber8();
		lastTickProcessed++;
	}
	//metodo auxiliar para aplicar a gravidade
	public void applyGravity(GravityAffected objInterface) {
        GameObject obj = (GameObject) objInterface;
        
        // Se nao tem suporte -> cai
        if (!objInterface.isSupported()) {
            Point2D posBelow = obj.getPosition().plus(Direction.DOWN.asVector());
            obj.setPosition(posBelow);
            
            // Se for uma bomba, marcamos que está a cair
            if (obj instanceof Bomb) {
                ((Bomb) obj).setFalling(true);
            }
        } 
        // Se tem suporte (bateu ou está parada)
        else {
            if (obj instanceof Bomb) {
                Bomb b = (Bomb) obj;
                
                // Se estava a cair E agora tem suporte -> boom
                if (b.isFalling()) {
                    
                    // Ver o que está por baixo
                    Point2D posBelow = b.getPosition().plus(Direction.DOWN.asVector());
                    GameObject support = currentRoom.getObjectAt(posBelow);
                    
                    // Explode se bater num objeto (excluindo peixes)
                    // isSupported já garante que não é Water.
                    // Só precisamos garantir que não é um GameCharacter (Peixe/Caranguejo)
                    if (!(support instanceof GameCharacter)) {
                        explode(b);
                    } else {
                        // Se caiu em cima de um peixe, não explode (o peixe suporta-a sem detonar)
                        // Mas o peixe pequeno morre pelo peso (tratado no checkSmallFishCrush)
                        b.setFalling(false); 
                    }
                }
            }
        }
    }
//metodo auxiliar para a boia flutuar
	public void applyBuoyancy(Floatable objInterface) {
        GameObject obj = (GameObject) objInterface;
        Point2D currentPos = obj.getPosition();
        Point2D posAbove = currentPos.plus(Direction.UP.asVector());
        Point2D posBelow = currentPos.plus(Direction.DOWN.asVector());

        GameObject objAbove = currentRoom.getObjectAt(posAbove);

        // Define se tem carga (algo que não seja Água nem Peixe)
        boolean hasLoad = (objAbove != null && objAbove instanceof GravityAffected);

        if (hasLoad) {
            // tem carga -> tenta afundar
            GameObject objBelow = currentRoom.getObjectAt(posBelow);
            if (objBelow == null || objBelow instanceof Water) {
                obj.setPosition(posBelow);
            }
        } else {
            // nao tem carga -> tenta subir
            // Só sobe se o espaço acima for realmente água ou vazio (se for um peixe, fica quieta)
            boolean canMoveUp = (objAbove == null || objAbove instanceof Water);
            
            if (canMoveUp && posAbove.getY() >= 0) {
                obj.setPosition(posAbove);
            }
        }
    }
	// Método para tratar da explosão
    private void explode(Bomb bomb) {
        Point2D bombPos = bomb.getPosition();
        
        // Remover a própria bomba
        currentRoom.removeObject(bomb);
        
        // Verificar as 4 direções adjacentes 
        for (Direction dir : Direction.values()) {
            Point2D targetPos = bombPos.plus(dir.asVector());
            
            // Verificar se atingiu um peixe (Game Over)
            if (SmallFish.getInstance().getPosition().equals(targetPos) || 
                BigFish.getInstance().getPosition().equals(targetPos)) {
                ImageGUI.getInstance().showMessage("Game Over", "O Personagem explodiu!");
                restartGame();
                return;
            }

            // Verificar se há objetos para destruir
            GameObject targetObj = currentRoom.getObjectAt(targetPos);
            if (targetObj != null && !(targetObj instanceof Water) && !(targetObj instanceof BigFish) && !(targetObj instanceof SmallFish)) {
                // Remove paredes, pedras, etc.
                currentRoom.removeObject(targetObj);
            }
        }
    }
//metodo para ver se o tronco foi esmagado
	public void checkTrunkCrush(){
		for(GameObject obj : new ArrayList<>(currentRoom.getObjects())){
			if(obj instanceof Trunk){
				Point2D posAbove = obj.getPosition().plus(Direction.UP.asVector());

				GameObject objAbove = currentRoom.getObjectAt(posAbove);

				if(objAbove instanceof interfaces.Heavy){
					currentRoom.removeObject(obj);
				}
			}
		}
	}
//metodo para ver se o peixe pequeno foi esmagado
	public void checkSmallFishCrush(){
		Point2D currentPos = SmallFish.getInstance().getPosition();
		int stackWeight = 0;
		
		while(true){
			//vamos buscar a posição acima
			currentPos = currentPos.plus(Direction.UP.asVector());
			GameObject objAbove = currentRoom.getObjectAt(currentPos);
			if(objAbove == null || !(objAbove instanceof GravityAffected))
				break;
			stackWeight++;
			//peixe pequeno nao suporta 1 pesado
			if(objAbove instanceof Heavy && !(objAbove instanceof Trap)){
				ImageGUI.getInstance().showMessage("Nivel reiniciado", "Eleven esmagada");
				restartGame();
				return;
			}
		}//peixe pequeno 
		if(stackWeight >= 2){
			ImageGUI.getInstance().showMessage("Nivel reiniciado", "Eleven esmagada");
			restartGame();
		}
	}

	public void checkBigFishCrush() {
		Point2D currentPos = BigFish.getInstance().getPosition();
		int heavyStackWeight = 0;
		// ciclo para ver o que o peixe grande suporta
		while (true) {
			// Vamos buscar a posição acima
			currentPos = currentPos.plus(Direction.UP.asVector());
			GameObject objAbove = currentRoom.getObjectAt(currentPos);
			//se nao tiver objetos em cima siga embora daqui
			if (objAbove == null || !(objAbove instanceof GravityAffected)) {
				break;
			}
			//Se for uma Armadilha, morre logo
			if (objAbove instanceof Trap) {
				ImageGUI.getInstance().showMessage("Game Over", "O Hopper tocou na armadilha!");
				restartGame();
				return;
			}
			//incrementa o peso se tiver pesado em cima
			if (objAbove instanceof interfaces.Heavy) {
				heavyStackWeight++;
			}
		}
		if (heavyStackWeight >= 2) {
			ImageGUI.getInstance().showMessage("Nivel reiniciado", "Hopper esmagado");
			restartGame();
		}
	}
	private void checkCrabCollisions() {
		for (GameObject obj : new ArrayList<>(currentRoom.getObjects())) {
			if (obj instanceof Crab) {
				Point2D crabPos = obj.getPosition();
				 for (Direction dir : Direction.values()) {

				// Colisão com Peixe Pequeno -> Game Over
				if (SmallFish.getInstance().getPosition().equals(crabPos.plus(dir.asVector()))||SmallFish.getInstance().getPosition().equals(crabPos.plus(Direction.RIGHT.asVector())) ) {
					ImageGUI.getInstance().showMessage("Game Over", "A Eleven foi apanhada pelo demogorgon!");
					restartGame();
					return;
				}

				// Colisão com Peixe Grande -> Caranguejo morre
				if (BigFish.getInstance().getPosition().equals(crabPos.plus(dir.asVector()))) {
					currentRoom.removeObject(obj);
					continue; // Passa ao próximo objeto
				}

				// Colisão com Armadilha -> Caranguejo morre
				// procurar se há uma armadilha nesta posição
				for (GameObject t : currentRoom.getObjects()) {
					if (t instanceof Trap && t.getPosition().equals(crabPos)) {
						currentRoom.removeObject(obj);
						break;
					}
				}
			}
			}
		}
	}
	//verifica se ambos os peixes sairam da sala, chamado no processTick
	public boolean checkWin() {
		return(!(currentRoom.getObjects().contains(BigFish.getInstance())) && !(currentRoom.getObjects().contains(SmallFish.getInstance())));
	}
	//verificar as armadilhas
	private void checkTraps() {
	    Point2D bigFishPos = BigFish.getInstance().getPosition();
	    GameObject obj = currentRoom.getObjectAt(bigFishPos);
	    
	    // Se o objeto na posição do peixe for uma Armadilha
	    if (obj instanceof Trap) {
	        // Remove o BigFish imediatamente para parar colisões
	        currentRoom.removeObject(BigFish.getInstance());
	        
	        // Atualiza o ecrã para o utilizador ver que ele morreu 
	        ImageGUI.getInstance().update();

	        // Mostra a mensagem de Game Over
	        ImageGUI.getInstance().showMessage("Game Over", "O Hopper caiu na armadilha!");
	        
	        // Reinicia
	        restartGame();
	    }
	}
	//elemento adicional extra ao enunciado (ler readme)
	private void checkNumber8() {
	    Point2D smallFishPos = SmallFish.getInstance().getPosition();
	    GameObject obj = currentRoom.getObjectAt(smallFishPos);
	    
	    // Se o objeto na posição do peixe for a number8
	    if (obj instanceof Number8) {
	    	
	    	currentRoom.removeObject(obj);
	    	
	        // Remove os objetos á volta
	        currentRoom.removeObject(currentRoom.getObjectAt(smallFishPos.plus(new Vector2D(1,0))));
	        currentRoom.removeObject(currentRoom.getObjectAt(smallFishPos.plus(new Vector2D(0,-1))));
	        
	        ImageGUI.getInstance().update();

	        // Mostra a mensagem de salvamento
	        ImageGUI.getInstance().showMessage("Conseguiste!","Salvaste a Number 8, Foge!");
	        
	       
	    }
	}
	
	public void restartGame() {
	    loadGame();
	    String levelName = currentRoom.getName();
	    currentRoom = rooms.get(levelName);

	    SmallFish.getInstance().setRoom(currentRoom);
	    BigFish.getInstance().setRoom(currentRoom);
	    SmallFish.getInstance().setPosition(currentRoom.getSmallFishStartingPosition());
	    BigFish.getInstance().setPosition(currentRoom.getBigFishStartingPosition());

	    // Isto impede que o jogo tente recuperar o tempo perdido enquanto a janela estava aberta
	    lastTickProcessed = ImageGUI.getInstance().getTicks(); 

	    updateGUI();
	}
	public void nextLevel(){
		//nivel atual
		String currentName = currentRoom.getName();
		//nome começa no quarto index (roomX) e tiramos o .txt
		String numberStr = currentName.substring(4,currentName.indexOf('.'));
		int currentLevelIndex = Integer.parseInt(numberStr);
		//proximo nivel é so somar um
		int nextLevelIndex = currentLevelIndex + 1;
		String nextRoomName = "room" + nextLevelIndex + ".txt";
		if(rooms.containsKey(nextRoomName)){
			ImageGUI.getInstance().showMessage("Nível completo", "Passaste ao nível" + nextLevelIndex);
			//nova sala
			currentRoom = rooms.get(nextRoomName);
			//os peixes mudaram de sala
			SmallFish.getInstance().setRoom(currentRoom);
			BigFish.getInstance().setRoom(currentRoom);
			//mete os peixes no novo mapa
			SmallFish.getInstance().setPosition(currentRoom.getSmallFishStartingPosition());
        	BigFish.getInstance().setPosition(currentRoom.getBigFishStartingPosition());

			updateGUI();
			System.out.println("Nivel" + nextLevelIndex + "iniciado!");
		} else { 
			// não existe próximo nível, jogo terminado
			
			// Calcular tempo total em segundos
			long endTime = System.currentTimeMillis();
			int totalTime = (int)((endTime - startTime) / 1000);
			
			// Criar o objeto ScorePlayer
			ScorePlayer myScore = new ScorePlayer(playerName, totalTime, currentMoves);
			
			// Atualizar o ficheiro (lê, ordena, corta top 10, grava)
			scoreGame.updateTopScores(myScore);
			
			// Obter o texto formatado para mostrar
			String highscoreText = scoreGame.getFormattedHighScores();
			
			// Mostrar mensagem final
			ImageGUI.getInstance().showMessage("Fim de Jogo", "PARABÉNS! JOGO CONCLUÍDO!\n\n" + highscoreText);
			
			ImageGUI.getInstance().dispose();
			System.exit(0);
		}
	}

	public void updateGUI() {
		if(currentRoom!=null) {
			ImageGUI.getInstance().clearImages();
			ImageGUI.getInstance().addImages(currentRoom.getObjects());
		}
	}
}
