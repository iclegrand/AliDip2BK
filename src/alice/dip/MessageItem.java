/*************
 * cil
 **************/


package alice.dip;

import cern.dip.DipData;

public class MessageItem {

    public String param_name;
    public String format_message;
    public DipData data;

    public MessageItem(String pm, String fm, DipData data) {
        this.data = data;
        param_name = pm;
        format_message = fm;
    }
}
