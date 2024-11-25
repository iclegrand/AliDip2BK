/*************
 * cil
 **************/
/*
 *  Structure used to keep float  values that change in time (e.g. Beam Energy)
 *
 */
package alice.dip;

import java.io.Serializable;

public class floatTS implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    public long time;
    public float value;

    public floatTS() {
    }

    public floatTS(long time, float value) {
        this.time = time;
        this.value = value;
    }
}

