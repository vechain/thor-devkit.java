package org.vechain.devkit;

import java.nio.ByteBuffer;
import org.vechain.devkit.cry.Utils;

public class Function {

    private com.esaulpaugh.headlong.abi.Function f;

    /**
     * Create a function instance from a JSON definition.
     * 
     * @param jsonString
     */
    public Function(String jsonString) {
        this.f = com.esaulpaugh.headlong.abi.Function.fromJson(jsonString);
    }

    public byte[] selector(){
        return this.f.selector();
    }

    private ByteBuffer encode(Object... args) {
        return this.f.encodeCallWithArgs(args);
    }

    public byte[] encodeToBytes(Object... args) {
        ByteBuffer bb = this.encode(args);
        bb.rewind();
        int total = bb.remaining();
        byte[] r = new byte[total];
        bb.get(r);
        return r;
    }

    public String encodeToHex(boolean prefix0x, Object... args) {
        byte[] b = this.encodeToBytes(args);
        String r = Utils.bytesToHex(b);
        if (prefix0x) {
            return "0x" + r;
        } else {
            return r;
        }
    }
}
