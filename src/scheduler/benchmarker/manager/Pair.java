/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package scheduler.benchmarker.manager;

/**
 *
 * @author jf
 */
public class Pair<K, V> {

    private final K elementX;
    private final V elementY;

//    public static <K, V> Pair<K, V> createPair(K element0, V element1) {
//        return new Pair<K, V>(element0, element1);
//    }

    public Pair(K elementX, V elementY) {
        this.elementX = elementX;
        this.elementY = elementY;
    }

    public K getElementX() {
        return elementX;
    }

    public V getElementY() {
        return elementY;
    }

}
