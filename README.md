============================================================
 PROJETO DE POO - STRANGER THINGS EDITION
============================================================

AUTORES:
- Duarte Teixeira (130190)
- Manuel Paciencia (130156)

------------------------------------------------------------
1. MUDANÇA DE TEMA (RE-SKIN)
------------------------------------------------------------
Para este projeto, decidimos adaptar o jogo ao universo da série 
"Stranger Things". Mudámos as imagens e os nomes para corresponderem
com a história:

PERSONAGENS:
- PEIXE PEQUENO  ->  ELEVEN
- PEIXE GRANDE   ->  HOPPER

OBJETOS:
- ANCORA         ->  CARRO DESTRUIDO
- BOMBA          ->  BARRIL DO LABORATORIO
- BOIA           ->  MAX
- CARANGUEJO     ->  DEMOGORGON
- TAÇA           ->  CASSETE (KATE BUSH)
- PAREDE C/ BURACO -> PORTAL
- PEDRA          ->  RELOGIO
- ARMADILHA      ->  VINHAS SINISTRAS

Nota: O que não está nesta lista manteve o aspeto original.


------------------------------------------------------------
2. LÓGICA DO JOGO
------------------------------------------------------------

A) AS ARMADILHAS (VINHAS)
Seguindo as opções do enunciado, implementámos o seguinte comportamento:
- Eleven (Pequeno): É imune. Consegue atravessar as armadilhas e, 
  mesmo que elas caiam em cima dela, não morre (atravessam-na).
- Hopper (Grande): Não tem imunidade. Se tocar numa armadilha 
  (ou se cair uma em cima dele), é Game Over imediato.

B) MECÂNICA DE RESGATE (NUMBER 8)
Criámos esta personagem especificamente para o nosso Nível 3 (Kali/008):
- Só a Eleven consegue interagir com ela.
- Quando a Eleven toca na posição da Kali, ela é "salva". Ao ser 
  resgatada, ela desaparece e limpa todos os objetos em cima e à direita 
  para abrir caminho.

C) PONTUAÇÃO (HIGH SCORE)
Para o top de pontuações, definimos estas prioridades:
1. TEMPO: Quem acaba mais rápido fica à frente.
2. MOVIMENTOS: Serve só para desempatar. Se tiverem o mesmo tempo, 
   ganha quem fez menos movimentos.


------------------------------------------------------------
3. NOTAS SOBRE O CÓDIGO
------------------------------------------------------------

SOBRE AS INTERFACES VAZIAS (MARKER INTERFACES)

Temos várias interfaces sem métodos (como Heavy, Lightweight, Big, 
Small, etc.). Usámos o padrão "Marker Interface" porque achámos 
que era a melhor solução por três motivos:

1. Polimorfismo mais limpo:
   O GameEngine não precisa de saber se o objeto é uma Pedra ou uma 
   Âncora. Só precisa de perguntar "if (obj instanceof Heavy)". 
   Isto limpa o código de verificações desnecessárias.

2. Fácil de expandir:
   Se quisermos criar um objeto novo amanhã, basta implementar as 
   interfaces certas e o motor de jogo já sabe como lidar com ele 
   (se cai, se flutua, etc.) sem termos de mudar o código do Engine.

3. Organização:
   Em vez de encher a classe GameObject de variáveis booleanas 
   (tipo isHeavy, isSmall, etc.) que nem todos usam, preferimos 
   usar interfaces para definir os tipos de cada objeto.

============================================================
