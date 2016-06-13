package ph.edu.msuiit.circuitlens;

public interface RpCallback<T> {
    void onResult(T netlist);
    void onError(String error);
}
