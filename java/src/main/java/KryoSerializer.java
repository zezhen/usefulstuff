import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;


public class KryoSerializer {

    private static Kryo kryo;
    
    public KryoSerializer() {
        kryo = new Kryo();
        kryo.setReferences(false);
        kryo.setRegistrationRequired(true);
    }
    
    public void register(Class...classes) {
        for(Class type : classes)
            kryo.register(type);
    }
    
    public void register(Class type, Serializer serializer) {
        kryo.register(type, serializer);
    }
    
    public byte[] toByteArray(Object obj) {
        
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        Output output = new Output(outStream, 4096);
        kryo.writeClassAndObject(output, obj);
        output.flush();
        return outStream.toByteArray();
        
    }
    
    public Object toObject(byte[] bytes) {
        
        Input input = new Input(new ByteArrayInputStream(bytes), 4096);
        Object obj = kryo.readClassAndObject(input);
        return obj;
    }
    
    
}
