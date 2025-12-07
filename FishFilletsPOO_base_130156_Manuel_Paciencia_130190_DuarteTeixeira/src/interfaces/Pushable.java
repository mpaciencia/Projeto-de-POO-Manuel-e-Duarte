package interfaces;

import objects.GameObject;

public interface Pushable {
    boolean isPushableBy(GameObject gameObject);
}
