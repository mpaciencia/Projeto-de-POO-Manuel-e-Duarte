package pt.iscte.poo.game;
/**
 * Exceção personalizada para indicar que um ficheiro é inválido ou está corrompido.
 */
public class InvalidFileException extends Exception {
    public InvalidFileException(String message) {
        super(message);
    }
}
