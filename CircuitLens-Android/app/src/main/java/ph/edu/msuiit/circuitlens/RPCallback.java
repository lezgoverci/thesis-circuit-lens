package ph.edu.msuiit.circuitlens;

public interface RPCallback<T> {
    void onResult(T netlist);
    void onError(String error);
}
