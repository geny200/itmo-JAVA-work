package info.kgeorgiy.java.advanced.implementor.full.lang;

/**
 * @author Georgiy Korneev (kgeorgiy@kgeorgiy.info)
 */

public abstract class Russian implements ПриветInterface {
    public Russian(Russian.Привет var1) throws Russian.Привет {
    }

    public abstract Russian.Привет привет(Russian.Привет var1) throws Russian.Привет;

    public static class Привет extends Exception {
        public Привет() {
        }
    }
}
