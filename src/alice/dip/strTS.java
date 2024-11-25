/*************
 * cil
 **************/

/*
 *  Structure used to keep String values that change in time (e.g. BeamMode)
 *
 */
package alice.dip;

import java.io.Serializable;

public class strTS implements Serializable {


    /**
     *
     */
    private static final long serialVersionUID = 1L;
    public long time;
    public String value;

    public strTS() {
    }

    public strTS(long time, String value) {
        this.time = time;
        this.value = value;
    }
}

