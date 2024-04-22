package chatapp.messageconsumer.id.generator;

public interface IdGenerator {

    public long nextId();
    public long[] parse(long id);
}
